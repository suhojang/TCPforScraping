package project.kais.init.client;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.kwic.exception.DefinedException;
import com.kwic.io.FileIO;
import com.kwic.math.Calculator;
import com.kwic.security.aes.AESCipher;
import com.kwic.support.client.KwScrapClient;
import com.kwic.support.client.KwScrapResult;
import com.kwic.telegram.tcp.JTcpManager;
import com.kwic.util.ByteUtil;
import com.kwic.util.StringUtil;

import project.kais.init.KaisState;
import project.kais.init.KaisCode;
import project.kais.init.client.except.Except;
import project.kais.init.exception.PropertyNotFoundException;
import project.kais.init.server.parser.CommonParser;
import project.kais.init.server.parser.Parser;
import project.kais.service.ScrapingService;

/**
 * 1. RequestAib.requestList에서 대상건 추출 
 * 2. 대상건으로 AIB 요청 xml생성 
 * 3. aib요청 4. aib응답내용을
 * db에 반영
 */
public class AibFTPClientThread extends Thread {

	protected static Logger log = LoggerFactory.getLogger(AibFTPClientThread.class);

	/** service */
	private ScrapingService scrapingService;

	/**
	 * 서비스 중단여부
	 */
	private boolean stop;

	/**
	 * 서비스 설정정보
	 * init-service.xml에 정의된 서비스 설정정보
	 */
	Map<String, Object> threadParams;
	
	/**
	 * 정수형 시스템 설정값 반환
	 * @param propertyName
	 * @return
	 * @throws PropertyNotFoundException
	 */
	private int getThreadParamsInt(String propertyName) throws PropertyNotFoundException{
		if(threadParams.get(propertyName) == null || "".equals(threadParams.get(propertyName))){
			throw new PropertyNotFoundException(propertyName);
		}
		return Integer.parseInt(String.valueOf(threadParams.get(propertyName)));
	}
	
	/**
	 * 문자형 시스템 설정값 반환
	 * @param propertyName
	 * @return
	 * @throws PropertyNotFoundException
	 */
	private String getThreadParamsString(String propertyName) throws PropertyNotFoundException{
		if(threadParams.get(propertyName) == null || "".equals(threadParams.get(propertyName))){
			throw new PropertyNotFoundException(propertyName);
		}
		return String.valueOf(threadParams.get(propertyName));
	}
	

	/**
	 * AIB 서비스 실행
	 */
	@Override
	public void run() {
		scrapingService = (ScrapingService) getBean("ScrapingService");

		boolean ftpNeed = false;
		
		String aibIp = null;
		int aibPort = 0;
		String aibCryptKey = null;
		int aibTimeout = 3 * 60 * 1000; //3분

		Map<String, Object> requestMap = null;
		Map<String, Object> parsingReqInfoMap = null;
		Map<String, Object> responseMap = null;
		
		/*
		 * AIB로 최종적으로 보내는 요청맵
		 */
		Map<String, String> aibReqParam = null;
		Map<String, String> aibResParam = null;
		Map<String, String> tmpMap = null;

		/* AIB 거래키  */		    String SCDL_SEQ = null;
		/* NICE 스크래핑 요청 KEY */ String NCRQ_BZCD = null;
		/* NICE 상품코드 */        String SCDL_G_BZCD = null;
		/* 기웅 전문코드 */          String SCDL_BZCD = null;
		/* 기웅 업무코드 */          String SCDL_JBCD = null;
		/* 스크래핑 원본 처리횟수 */     String SCDL_CNT4 = null;
		/* 요청연월 */             String YYMM = null;
		/* 예외처리 클래스  */ 		Except except = null;
		
		String encoding = null;
		/* 결과코드 */    String rtcd = KaisCode.P000;
		/* 스크래핑 결과 */ KwScrapResult scrapResult = null;
		//String abrq = null;
		String abrs = null;
		/* 요청원문 */    String ostr = null;
		String afrs	= null;
		String xmlData	= null;
		
		try {
			encoding    = getThreadParamsString("data-encoding");			
			aibIp       = getThreadParamsString("ip");
			aibPort     = getThreadParamsInt("port");
			aibCryptKey = getThreadParamsString("crypt-key");
			aibTimeout  = (int) Calculator.calculate(getThreadParamsString("aib-timeout"));

			while (!stop) {
				try {
					rtcd = KaisCode.P000;
					requestMap = RequestAibFTP.getRequest();
					
					/*
					 * 요청에 대한 응답이 없다면 1분간 대기한다.
					 */
					if (requestMap == null) {
						Thread.sleep(1);
						continue;
					}
					log.debug(String.format("First request %s", requestMap));
					
					//abrq = AESCipher.decode(String.valueOf(requestMap.get("SCDL_ABRQ")), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING);
					abrs 	= AESCipher.decode(String.valueOf(requestMap.get("SCDL_ABRS")), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING);
					xmlData	= String.valueOf(requestMap.get("SCDL_XMLDATA")); 
					
					String xmldataPath 	= StringUtil.replace(ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/xmldata"), "\\", "/") + File.separator + xmlData + ".txt";
					
					System.out.println("xmldataPath :: "+xmldataPath);
					System.out.println("재시도 횟수 :: "+String.valueOf(requestMap.get("SCDL_CNT4")));
					
					/*
					 * NICE요청 문자열 원문을 복호화 한다.
					 */
					ostr = AESCipher.decode(String.valueOf(requestMap.get("SCDL_OSTR")), 
							                AESCipher.DEFAULT_KEY,
							                AESCipher.TYPE_256, 
							                "UTF-8", 
							                AESCipher.MODE_ECB_NOPADDING);
					
					requestMap.put("SCDL_OSTR", ostr); //NICE요청 문자열 원문

					aibReqParam = new HashMap<String, String>();
					aibResParam = new HashMap<String, String>();
					
					log.debug("requestMap.get('NCRQ_BZCD') >>>>>>>>>>>>>>>>> " + requestMap.get("NCRQ_BZCD"));
					log.debug("requestMap.get('SCDL_BZCD') >>>>>>>>>>>>>>>>> " + requestMap.get("SCDL_BZCD"));
					log.debug("requestMap.get('SCDL_JBCD') >>>>>>>>>>>>>>>>> " + requestMap.get("SCDL_JBCD"));
					log.debug("xmlData >>>>>>>>>>>>>>>>> " + xmlData);

					//requestMap.put("SCDL_JBCD", "0009".equals(requestMap.get("SCDL_BZCD"))?"62":"0040".equals(requestMap.get("SCDL_BZCD"))?"72":requestMap.get("SCDL_JBCD"));
					//자동차 모듈번호 : 52, 건설기계 모듈번호 : 53
					requestMap.put("SCDL_JBCD", "0009".equals(requestMap.get("SCDL_BZCD"))?"52":"0040".equals(requestMap.get("SCDL_BZCD"))?"53":requestMap.get("SCDL_JBCD"));

					YYMM        = String.valueOf(requestMap.get("YYMM"));
					SCDL_SEQ    = String.valueOf(requestMap.get("SCDL_SEQ"));    //AIB 거래키
					NCRQ_BZCD   = String.valueOf(requestMap.get("NCRQ_BZCD"));   //거래구분코드
					SCDL_G_BZCD = String.valueOf(requestMap.get("SCDL_G_BZCD")); //NICE 상품코드
					SCDL_BZCD   = String.valueOf(requestMap.get("SCDL_BZCD"));   //기웅 전문코드
					SCDL_JBCD   = String.valueOf(requestMap.get("SCDL_JBCD"));   //기웅 업무코드
					SCDL_CNT4    = String.valueOf(requestMap.get("SCDL_CNT4"));   //스크래핑 요청횟수

					/*
					 * AIB 요청 파라미터
					 */
					aibReqParam.put("SPECIALCODE",    SCDL_BZCD);
					aibReqParam.put("MODULE",         SCDL_JBCD);
					aibReqParam.put("CERTKEY",        getThreadParamsString("CERTKEY"));
					aibReqParam.put("DEPARTMENTCODE", getThreadParamsString("DEPARTMENTCODE"));
					//aibReqParam.put("TRANSKEY",       getFieldValue(abrs, "TRANSKEY", "VALUE"));
					aibReqParam.put("TRANSKEY",       "");
					aibReqParam.put("SCDL_SEQ",       SCDL_SEQ);
					aibReqParam.put("SCDL_CNT4",      SCDL_CNT4);

					/*
					 * AIB 응답 파라미터
					 */
					aibResParam.put("SPECIALCODE", SCDL_BZCD);
					aibResParam.put("MODULE",      SCDL_JBCD);
					aibResParam.put("SCDL_SEQ",    SCDL_SEQ);
					aibResParam.put("SCDL_CNT4",   SCDL_CNT4);

					// for logging
					aibResParam.put("NCRQ_BZCD", NCRQ_BZCD); //거래구분코드 - 1S101, H_BZ_CD 항목값

					// parsing
					try {
						log.debug(String.format("[전문호출] AIB Parser.parse"));	
						parsingReqInfoMap = new CommonParser().parse(NCRQ_BZCD + "-" + SCDL_G_BZCD, 
								                                     String.valueOf(requestMap.get("SCDL_OSTR")).getBytes(encoding), 
								                                     encoding, 
								                                     Parser.REQUEST);
					} catch (Exception e) {
						log.error(String.format("Parsing error. bzCd=%s, origin=%s, encoding=%s", NCRQ_BZCD + "-" + SCDL_G_BZCD, String.valueOf(requestMap.get("SCDL_OSTR")).getBytes(encoding), encoding), e);
						rtcd = KaisCode.S503;
						throw new DefinedException("Request parsing error [" + SCDL_SEQ + "]." + e.getMessage());
					}
					aibReqParam.put("CST_PARAM", String.valueOf(parsingReqInfoMap.get("SCAF_ID")));
					
					
					log.debug("requestMap.get('SCDL_CNT4') >>>>>>>>>>>>>>>>> " + requestMap.get("SCDL_CNT4"));
					
					//3회째 시도 시 LASTJOBYN 필드 추가
					if (Integer.parseInt(aibReqParam.get("SCDL_CNT4")) == 2)
						aibReqParam.put("LASTJOBYN", "Y");

					//////////////////////////////파일명 처리 - 2018.11.13(문서파일명 : '/' 제거 후 파일명만 기재)///////////////////////////////////////
					String DC_FL_NM	= String.valueOf(parsingReqInfoMap.get("DC_FL_NM"));
					if (DC_FL_NM!=null && !"".equals(DC_FL_NM)) {
						DC_FL_NM	= StringUtil.replace(DC_FL_NM, "\\", "/");
						DC_FL_NM	= DC_FL_NM.substring(DC_FL_NM.lastIndexOf("/") + 1, DC_FL_NM.length());
						parsingReqInfoMap.put("DC_FL_NM", DC_FL_NM);
					}
					//////////////////////////////////////////////////////////////////////////////////////////////////////

					/*
					 * 에외처리 클래스 생성
					 */
					Class<?> exceptClass = null;
					try {
						exceptClass = Class.forName("project.kais.init.client.except.Except_" + NCRQ_BZCD + "_" + SCDL_G_BZCD);
					} catch (ClassNotFoundException ce) {
						exceptClass = Class.forName("project.kais.init.client.except.CommonExcept");
					}
					
					try {
						except = (Except) exceptClass.newInstance();
					} catch (Exception e) {
						log.error(String.format("Cannot instatiate Except Class. project.kais.init.client.except.Except_%s_%s", NCRQ_BZCD, SCDL_G_BZCD), e);
						rtcd = KaisCode.S503;
						throw new DefinedException("Undefined business code [" + NCRQ_BZCD + "-" + SCDL_G_BZCD + "]." + e.getMessage());
					}
					log.debug(String.format("Except Class is %s.", exceptClass.getSimpleName()));

					try {
						log.debug(String.format("[전문호출] AIB Except.mapAibRequest"));						
						tmpMap = except.mapAibRequest(SCDL_BZCD, SCDL_JBCD, parsingReqInfoMap);
						
						//요청 전문의 해당 필드 가져오기
						String[] addField	= new String[]{
								"REQSTNO",
								"REQSTDT",
								"CTCQCODE",
								"CVPLNO",
								"NCTRID",
								"RSCODE",
								"REQSTSECODE",
								"ISSUNBFPS",
								"XMLFILENM1",
								"XMLFILENM2",
								"XMLFILEDATA1"
						};
						
						for (int i = 0; i < addField.length; i++) {
							tmpMap.put(addField[i], getFieldValue(abrs, addField[i], "VALUE"));
						}
						
						byte[] fileByte		= FileIO.getBytes(new File(xmldataPath));
						
						tmpMap.put("XMLFILEDATA2", new String(fileByte));
						
						//3회 시도시 삭제
						if(Integer.parseInt(SCDL_CNT4) >= 2){
							if (new File(xmldataPath).exists()) 
								new File(xmldataPath).delete();
						}
						/*if (new File(xmldataPath).exists()) 
							new File(xmldataPath).delete(); */
						
						//tmpMap.put("XMLFILEDATA2", xmlData);
						tmpMap.put("SCDL_STS", "AF");
						
						log.debug("tmpMap >>>>>>>>>>>>>>>>>>> " + tmpMap);
						
					} catch (Exception e) {
						log.error(String.format("AIB exception handling is failed. project.kais.init.client.except.Except_%s_%s, %s %s %s", NCRQ_BZCD, SCDL_G_BZCD, SCDL_BZCD, SCDL_JBCD, parsingReqInfoMap), e);
						rtcd = KaisCode.S503;
						throw new DefinedException("Request mapping error [" + SCDL_SEQ + "]." + e.getMessage());
					}
					aibReqParam.putAll(tmpMap);
					
					/*
					 * CAPTCHAOPTION 설정
					 */
					aibReqParam.put("CAPTCHAOPTION", getThreadParamsString("CAPTCHAOPTION"));
					aibReqParam.put("PRINT", "Y");	//원본 관련 설정
					
					try {
						log.debug(String.format("[전문호출] AIB Except.exceptRequest  %s", except.getClass().getSimpleName()));						
						// aib 원본저장 요청 data 생성
						aibReqParam = except.exceptRequest(aibReqParam, parsingReqInfoMap, threadParams);
					} catch (Exception e) {
						log.error(String.format("Creating AIB data is failed. parameters=%s request=%s", aibReqParam, parsingReqInfoMap), e);
						rtcd = KaisCode.S502;
						throw new DefinedException("Request exceptable process error [" + SCDL_SEQ + "]." + e.getMessage());
					}
					aibResParam.put("SCDL_AFQTM", new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime())); //AIB 요청 시각
					
					log.debug(String.format("[처리시간측정] AIB request start. SCAF_ID=%s, %s", parsingReqInfoMap.get("SCAF_ID"), aibReqParam));////////////////////
					
					/*
					 * AIB 원본저장 요청 전송 및 응답결과 수신
					 */
					try {
						scrapResult = KwScrapClient.request(aibIp, 
								                           aibPort, 
								                           aibTimeout,
								                           KwScrapClient.CRYPT_TYPE_KWICAES256, 
								                           aibCryptKey, 
								                           aibReqParam,
								                           KwScrapClient.REQUEST_DATA_TYPE_HEADLENGTH_XML, 
								                           KwScrapClient.DEFAULT_CAPTCHA_TIMEOUT);
					} catch (Exception e) {
						log.error(String.format("Processing AIB data is failed. ip=%s, port=%d, timeout=%d, request=%s result=%s", aibIp, aibPort, aibTimeout, aibReqParam, scrapResult), e);
						rtcd = KaisCode.S503;
						throw new DefinedException("Could not call KwScrapClient.request [" + SCDL_SEQ + "]." + e.getMessage());
					}
					
					log.debug(String.format("[처리시간측정] AIB request end. SCAF_ID=%s, %s", parsingReqInfoMap.get("SCAF_ID"), scrapResult));////////////////////

					aibResParam.put("SCDL_AFSTM", new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime())); //AIB 응답 시각
					responseMap = scrapResult.getResponseMap();
					aibResParam.put("SCDL_AFRQ", scrapResult.getRequestXml()); //AIB 원본저장 요청 XML		
					aibResParam.put("SCDL_AFRS", scrapResult.getDecryptResponseXml()); //AIB 원본저장 응답 XML
					aibResParam.put("SCDL_AFIP", String.valueOf(responseMap.get("SCRAPIP")));
					
					log.debug(String.format("[AIB 원본저장 응답1] SCAF_ID=%s", parsingReqInfoMap.get("SCAF_ID")));
					log.debug(String.format("[AIB 원본저장 응답2] SCDL_AFRQ=%s", aibResParam.get("SCDL_AFRQ")));
					log.debug(String.format("[AIB 원본저장 응답3] SCDL_AFRS=%s", aibResParam.get("SCDL_AFRS")));
					log.debug(String.format("[AIB 원본저장 응답4] responseMap=%s", responseMap));
					
					afrs	= aibResParam.get("SCDL_AFRS");
					
					// aib 스크래핑 시스템 오류
					if (responseMap == null || responseMap.get("INQNORMALPE") == null || !"P".equals(responseMap.get("INQNORMALPE"))) {
						rtcd = "E" + scrapResult.getErrorCode();
						log.error(String.format("[스크래핑 오류] Processing AIB Error>>>. INQNORMALPE=%s, error code=%s, error=%s", responseMap.get("INQNORMALPE"), scrapResult.getErrorCode(), scrapResult.getErrorMessage()));
						throw new DefinedException(scrapResult.getErrorMessage());
					}
					
					aibResParam.put("SCDL_RTCD2", rtcd); //스크래핑 응답코드
					aibResParam.put("SCDL_RTMSG2", ""); //스크래핑 응답 메시지
					if (!scrapResult.getResult() || !"000".equals(scrapResult.getErrorCode())){
						aibResParam.put("SCDL_RTMSG2", scrapResult.getErrorMessage()); //스캐리핑 처리 메시지
					}
					
					try {
						/**
						 * AIB 원본 응답 저장 처리 후 FTP 송신
						 */
						ftpNeed	= saveResponse(aibReqParam, aibResParam, YYMM);
					} catch (Exception e) {
						log.error(String.format("Saving AIB response is failed. request parameters=%s, response parameters=%s", aibReqParam, aibResParam), e);
						rtcd = KaisCode.S503;
						throw new DefinedException("Fail to save " + SCDL_BZCD + "-" + SCDL_JBCD + " response [" + SCDL_SEQ + "]." + e.getMessage());
					}
					
					try {						
						//FTP 전송
						if(ftpNeed){
							if (new File(xmldataPath).exists()) 
								new File(xmldataPath).delete();
							
							String ftpEncoding = "UTF-8";
							uploadFTP(afrs, ftpEncoding, YYMM, SCDL_SEQ);
						}
					} catch (Exception e) {
						log.error(String.format("FTP UPLOAD FAILED."), e);
					}
					
				} catch (Exception e) {
					log.error(String.format("Processing AIB data is failed. request parameters=%s, response parameters=%s", aibReqParam, aibResParam), e);
					try {
						// aib 원본저장 응답 실패 처리
						ftpNeed	= saveResponse(aibReqParam, aibResParam, rtcd, YYMM, e);
					} catch (Exception ex) {
						log.error(String.format("After error, saving AIB failure is failed. return code=%s, request parameters=%s, response parameters=%s", rtcd, aibReqParam, aibResParam), e);
					}
					
					try {
						//FTP 전송
						if(ftpNeed){
							String ftpEncoding = "UTF-8";
							uploadFTP(afrs, ftpEncoding, YYMM, SCDL_SEQ);
						}
					} catch (Exception e1) {
						log.error(String.format("FTP UPLOAD FAILED."), e1);
					}
				} finally {
					try {
						Thread.sleep(1);
					} catch (Exception e) {
					}
				}
			}
		} catch (Exception e) {
			log.error("Unknown AIB client error.", e);
		} finally {
			log.debug("AibClient terminated");
		}
	}	
	
	/**
	 * 에러 메시지를 포함한 스크래핑 요청 전문 DB 저장
	 * @param aibReqParam 요청 정보
	 * @param aibResParam 응답정보
	 * @param rtcd 스크래핑 응답코드
	 * @param YYMM 요청연월
	 * @param error
	 * @throws Exception
	 */
	public boolean saveResponse(Map<String, String> aibReqParam, Map<String, String> aibResParam, String rtcd, String YYMM, Exception error) throws Exception {
		if (KaisCode.P000.equals(rtcd)){
			rtcd = KaisCode.E859; //AIB 요청 중 정의되지 않은 오류
		}
		aibResParam.put("SCDL_RTCD",  rtcd);
		aibResParam.put("SCDL_RTMSG", error.getMessage());
		
		return saveResponse(aibReqParam, aibResParam, YYMM);
	}
	
	public boolean saveResponse(Map<String, String> aibReqParam, Map<String, String> aibResParam, String YYMM) throws Exception {
		boolean ftpNeed = false;
		
		//String rtcd = aibResParam.get("H_RTCD") == null ? null : (String) aibResParam.get("H_RTCD");
		
		log.debug("SCDL_RTCD2 >>>>>>>>>>>>>>> " + aibResParam.get("SCDL_RTCD2"));
		log.debug("요청 횟수 >>>>>>>>>>>>>>>>>> "+Integer.parseInt(aibReqParam.get("SCDL_CNT4")==null?"0":aibReqParam.get("SCDL_CNT4")));
		
		//!KaisCode.P000.equals(aibResParam.get("SCDL_RTCD")) && Integer.parseInt(String.valueOf(aibResParam.get("SCDL_CNT"))) < 2
		
		if(!KaisCode.P000.equals(aibResParam.get("SCDL_RTCD2")) && Integer.parseInt(aibReqParam.get("SCDL_CNT4")==null?"0":aibReqParam.get("SCDL_CNT4")) < 2) {
			aibResParam.put("SCDL_STS", KaisState.AIB_TGT_AF);//원본저장 대상
		} else {
			switch(checkNextProcess(aibResParam)){
			case ONLY_LOG:
				aibResParam.put("SCDL_STS", 	KaisState.LOG_TGT_L0);// do LOGGING
				aibResParam.put("SCDL_FTPCD", 	aibResParam.get("SCDL_FTPCD")==null?"":String.valueOf(aibResParam.get("SCDL_FTPCD")));	//오류코드 0018
				aibResParam.put("SCDL_FTPMSG", 	aibResParam.get("SCDL_FTPMSG")==null?"":String.valueOf(aibResParam.get("SCDL_FTPMSG")));
				break;
				
			case FTP_LOG:
				aibResParam.put("SCDL_STS", KaisState.FTP_F0);
				ftpNeed = true;
				break;
			}
		}
		
		log.debug("SCDL_STS >>>>>>>>>>>>>>> " + aibResParam.get("SCDL_STS"));
		
		aibResParam.put("YYMM", YYMM);
		
		if (String.valueOf(aibResParam.get("SCDL_RTCD2")).indexOf("E") > -1) {
			String erCd = KaisCode.S502;
			if (erCd != null){
				aibResParam.put("SCDL_RTCD2", erCd);
			}
		}
		
		//AIB 원본저장 요청 XML 암호화
		if (aibResParam.get("SCDL_AFRQ") == null){
			aibResParam.put("SCDL_AFRQ", "");
		}else {
			String afrq = AESCipher.encode(String.valueOf(aibResParam.get("SCDL_AFRQ")), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256,	"UTF-8", AESCipher.MODE_ECB_NOPADDING);
			aibResParam.put("SCDL_AFRQ", afrq);
		}
		
		//AIB 원본저장 응답 XML 암호화
		if (aibResParam.get("SCDL_AFRS") == null){
			aibResParam.put("SCDL_AFRS", "");
		}else {
			String afrs = AESCipher.encode(String.valueOf(aibResParam.get("SCDL_AFRS")), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256,	"UTF-8", AESCipher.MODE_ECB_NOPADDING);
			aibResParam.put("SCDL_AFRS", afrs);
		}
		
		//AIB 원본저장 요청 시각
		if (aibResParam.get("SCDL_AFQTM") == null){
			aibResParam.put("SCDL_AFQTM", "");
		}
		
		//AIB 원본저장 응답 시각
		if (aibResParam.get("SCDL_AFSTM") == null){
			aibResParam.put("SCDL_AFSTM", "");
		}
		log.debug(String.format("[DB-SAVE_RESPONSE_AIB_FTP] FTP 결과저장. aibResParam=%s", aibResParam));	
		
		scrapingService.SAVE_RESPONSE_AIB_FTP(aibResParam);
		
		return ftpNeed;
	}
	
	private static final int FTP_LOG 	= 1;
	private static final int ONLY_LOG 	= 2;
	
	public int checkNextProcess(Map<String, String> aibResParam) throws ParserConfigurationException, SAXException, IOException {
		Map<String,String> parsingMap = null;
		String INQNORMALPE	= "";
		String PRINT		= "";
		String TRANSSVRYN	= "";
		
		String xml 			= aibResParam.get("SCDL_AFRS"); //AIB 원본저장 응답XML
		if (xml!=null && !"".equals(xml)) {
			parsingMap 	= parsingXml(xml);			
			INQNORMALPE	= (String) parsingMap.get("INQNORMALPE");
			PRINT		= (String) parsingMap.get("PRINT");
			TRANSSVRYN	= (String) parsingMap.get("TRANSLSAYN");
		}
		
		log.debug("INQNORMALPE >>>>>>>>>>>>>>>> " + INQNORMALPE);
		log.debug("PRINT >>>>>>>>>>>>>>>> " + PRINT);
		log.debug("TRANSSVRYN >>>>>>>>>>>>>>>> " + TRANSSVRYN);
		
		if("P".equals(INQNORMALPE) && "Y".equals(PRINT) && "Y".equals(TRANSSVRYN)) {
			return FTP_LOG;
		}
		
		aibResParam.put("SCDL_FTPCD", 	KaisCode.S018);	//FTP를 송신하지 않음.
		aibResParam.put("SCDL_FTPMSG", 	"원본저장 스크래핑 실패");
		
		return ONLY_LOG;
	}
	
	/**
	 * AIB 원본저장 응답전문에서 INQNORMALPE, , 를 
	 * @param xml
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private Map<String,String> parsingXml(String xml) throws ParserConfigurationException, SAXException, IOException{	
		
		Map<String,String> map = new HashMap<String,String>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xml));
		Document document = db.parse(is);
		NodeList nodelist = null;
		Element element = null;
		
		try {
			 nodelist = document.getElementsByTagName("INQNORMALPE");
	   	 	 element = (Element) nodelist.item(0);
	   	 	 map.put(element.getNodeName() , element.getAttribute("VALUE"));
		} catch (Exception e) {
			map.put("INQNORMALPE", "");
		}
		
		try {
			 nodelist = document.getElementsByTagName("PRINT");
			 	element = (Element) nodelist.item(0);
		   	 	map.put(element.getNodeName() , element.getAttribute("VALUE"));
		} catch (Exception e) {
			map.put("PRINT", "");
		}
   	 	
   	   try {
	   		nodelist = document.getElementsByTagName("TRANSLSAYN");
		 	element = (Element) nodelist.item(0);
		 	map.put(element.getNodeName() , element.getAttribute("VALUE"));
		} catch (Exception e) {
			map.put("TRANSLSAYN", "");
		}
   	 	
	 	log.debug(String.format("map=%s , AIB 원본저장 응답  xml=\n%s",map,  xml));
	 	
	 	return map;
	}
	
	private static final String FTP_SUCCESS = "S";
	private static final String FTP_FAILED = "F";
	
	/**
	 * FTP 파일 업로드 요청
	 * @param except
	 * @param aibResXml
	 * @param encoding
	 * @throws Exception
	 */
	private void uploadFTP(String aibResXml, String encoding, String YYMM, String SCDL_SEQ) throws Exception {
		log.debug("aibResXml >>>>>>>>>>>>>>> " + aibResXml);
		
		Map<String,Object> ftpRequestMap = makeFtpRequestMap(aibResXml);
		log.debug(String.format("[FTP] 전송정보 ftpMap.INTPUTXML=%s", ftpRequestMap.get("INTPUTXML")));

		byte[] ftpRequestBytes = (String.valueOf(ftpRequestMap.get("INTPUTLENGTH")) + String.valueOf(ftpRequestMap.get("INTPUTXML"))).getBytes(encoding);
		log.debug(String.format("[FTP] encoding=%s, 전송정보 시작\n%s\n", encoding, new String(ftpRequestBytes, encoding)));	
		
		byte[] ftpResponseBytes = null;
		Map<String, String> dbParam = new HashMap<String, String>();
		
		boolean connectionSuccess = false;
		try {
			ftpResponseBytes = requestFtp(ftpRequestBytes);
			connectionSuccess = true;			
			log.debug(String.format("[FTP] 수신완료. 수신정보=%s", new String(ftpResponseBytes, encoding)));
		} catch (Exception e) {
			log.error(String.format("[FTP] NICE FTP 연결 실패."), e);
			connectionSuccess = false;
		}
		
		if(connectionSuccess){
			String ftpResponseXml = new String(ftpResponseBytes, encoding);			
			Map<String,Object> ftpResponseMap = makeFtpResponseMap(ftpResponseXml);

			log.debug(String.format("[FTP] 수신정보 시작%s종료", ftpResponseXml));
			dbParam.put("SCDL_SEQ",   	SCDL_SEQ);
			dbParam.put("SCDL_FTPCD", 	String.valueOf(ftpResponseMap.get("FTP_FILE_SEND_RESULTCD"))); 	//FTP 파일 전송 결과코드
			dbParam.put("SCDL_STS",   	KaisState.LOG_TGT_L0); 
			dbParam.put("YYMM",       	YYMM);

			switch(String.valueOf(ftpResponseMap.get("FTP_FILE_SEND_SF"))){
			case FTP_SUCCESS:
				break;
			case FTP_FAILED:
				dbParam.put("SCDL_FTPMSG", String.valueOf(ftpResponseMap.get("FTP_FILE_SEND_ERRMSG"))); //FTP 파일 전송 에러메시지
				break;
			}
			
		}else{			
			dbParam.put("SCDL_SEQ",    SCDL_SEQ);
			dbParam.put("SCDL_FTPCD",  KaisCode.S019); //FTP 파일 전송 결과코드 : 2018.11.13 FTP전송결과 코드 변경됨.
			dbParam.put("SCDL_STS",    KaisState.LOG_TGT_L0); 
			dbParam.put("SCDL_FTPMSG", "FTP서버 원본저장 실패"); //FTP 파일 전송 에러메시지			
			dbParam.put("YYMM",        YYMM); 
			log.debug(String.format("[FTP] NICE FTP와 연결 실패. 연결정보=%s:%d, dbParam=%s", getThreadParamsString("NICE_FTP_IP"), getThreadParamsInt("NICE_FTP_PORT"), dbParam));	
		}

		log.debug(String.format("[DB-SAVE_FTP_RESULT] FTP 결과저장. dbParam=%s", dbParam));	
		
		scrapingService.SAVE_FTP_RESULT(dbParam);	
	}
	
	/**
	 * FTP 업로드를 위한 정보 생성
	 * @param xml AIB 응답전문 XML
	 * @return
	 * @throws Exception
	 */
	private  Map<String,Object> makeFtpRequestMap(String xml) {
		DocumentBuilderFactory dbf 	= null;
		DocumentBuilder db			= null;
		InputSource is 				= null;
		Document document			= null;
		
		Map<String,Object> inputMap = new HashMap<String,Object>();	
		StringBuilder builder 		= new StringBuilder();

		NodeList nodelist 	= null;
		Element element 	= null;
		
		NodeList subNodes 	= null;
		NodeList childNodes	= null;
		try {
			dbf		= DocumentBuilderFactory.newInstance();
			db 		= dbf.newDocumentBuilder();
			is 		= new InputSource();
			
			is.setCharacterStream(new StringReader(xml));
			document 	= db.parse(is);
			
			nodelist 	= document.getElementsByTagName("TRANSKEY");
			element 	= (Element) nodelist.item(0);
			
			builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><INPUT>");
			builder.append(String.format("<%s VALUE=\"%s\"/>", "TRANSKEY", element.getAttribute("VALUE")));
			builder.append("<SENDFTPINFO>");
			
			subNodes 	= document.getElementsByTagName("SENDFTPINFO");
			childNodes	=  subNodes.item(0).getChildNodes();
			
			if (childNodes != null && childNodes.getLength() > 0) {
				for (int i = 0; i < childNodes.getLength(); i++) {
					Element subelement	= (Element) childNodes.item(i);
					builder.append(String.format("<%s VALUE=\"%s\"/>", subelement.getNodeName(), subelement.getAttribute("VALUE")));
				}
			}
			builder.append("</SENDFTPINFO>");
			builder.append("</INPUT>");
			
			int size = builder.toString().getBytes().length;
			
			inputMap.put("INTPUTLENGTH", String.format("%010d", size + 10));
			inputMap.put("INTPUTXML", builder.toString());
			
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		return inputMap;
	}
	
	/**
	 * NICE FTP로 파일 업로드 및 결과 회신
	 * @param bytes
	 * @return
	 * @throws Exception
	 */
	public byte[] requestFtp(byte[] bytes) throws Exception {
		int timeout  = (int) Calculator.calculate(getThreadParamsString("nice-timeout"));
		int lenStIdx = getThreadParamsInt("length-start-index");
		int lenSz    = getThreadParamsInt("length-size");
		String ip = getThreadParamsString("NICE_FTP_IP");
		int port = getThreadParamsInt("NICE_FTP_PORT");
		boolean isContainLength = "0".equals(getThreadParamsString("NICE_FTP_length-contain-self"));
		byte[] temp = String.valueOf(bytes.length - (isContainLength ? lenSz : 0)).getBytes();
		byte[] lengthBytes = ByteUtil.addByte(temp, ByteUtil.APPEND_CHARACTER_ZERO, lenSz, true);
		int lengthType = isContainLength ? JTcpManager._LENGTH_TYPE_BODYONLY : JTcpManager._LENGTH_TYPE_FULLSIZE;
		
		// set total length
		System.arraycopy(lengthBytes, 0, bytes, lenStIdx, lenSz);

		byte[] response = null; 
		try {
			 response = JTcpManager.getInstance().sendMessageWithoutEOF(ip, port, timeout, null, bytes, lenSz,  lenStIdx, lenSz, lengthType, true);
		} catch (EOFException e) {
			log.error(String.format("[FTP 전송에러] EOFException"), e);
			return null;
		} catch(Exception e){
			log.error(String.format("[FTP 전송에러] Exception"), e);
			throw e;
		}
		return response;
	}
	
	/**
	 * FTP 업로드 결과 정보 생성
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	private  Map<String,Object> makeFtpResponseMap(String xml) throws Exception {
		log.debug(String.format("[FTP] 업로드 후 응답 XML=%s<<<<<", xml));
		xml = xml.substring(10, xml.length());
		log.debug(String.format("[FTP] 업로드 후 응답 XML=%s<<<<<", xml));
		Map<String,Object> ftpMap = new HashMap<String,Object>();	
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xml));
		
		String[] names = {"FTP_FILE_SEND_SF", "FTP_FILE_SEND_ERRMSG", "FTP_FILE_SEND_RESULTCD", "TRANSKEY"};
		
		Document document = db.parse(is);
		for(String name : names){
			NodeList subnodelist = document.getElementsByTagName(name);
	    	Element subelement = (Element) subnodelist.item(0);
	    	ftpMap.put(subelement.getNodeName(), subelement.getAttribute("VALUE"));	 		
		}
		
		return ftpMap;
	}

	public String getFieldValue(String abrs, String field, String attr) {
		String transKey	= "";
		
		DocumentBuilderFactory dbf 	= null;
		DocumentBuilder db			= null;
		InputSource is 				= null;
		Document document			= null;
		

		NodeList nodelist 	= null;
		Element element 	= null;
		try {
			dbf		= DocumentBuilderFactory.newInstance();
			db 		= dbf.newDocumentBuilder();
			is 		= new InputSource();
			
			is.setCharacterStream(new StringReader(abrs));
			document 	= db.parse(is);
			
			nodelist 	= document.getElementsByTagName(field);
			element 	= (Element) nodelist.item(0);
			
			if (element == null)
				return "";
			
			transKey	= element.getAttribute(attr);
			
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		return transKey;
	}

	public static Object getBean(String name) {
		return ContextLoader.getCurrentWebApplicationContext().getBean(name);
	}

	public void terminate() {
		stop = true;
	}

	

	public void setThreadParams(Map<String, Object> threadParams) {
		this.threadParams = threadParams;
	}

}
