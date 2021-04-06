package project.kais.init.client;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;

import com.kwic.exception.DefinedException;
import com.kwic.io.FileIO;
import com.kwic.math.Calculator;
import com.kwic.security.aes.AESCipher;
import com.kwic.support.client.KwScrapClient;
import com.kwic.support.client.KwScrapResult;
import com.kwic.util.StringUtil;
import com.kwic.xml.parser.JXParser;

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
public class AibClientThread extends Thread {

	protected static Logger log = LoggerFactory.getLogger(AibClientThread.class);

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
	 * 서비스 에러 메시지
	 */
	private String[] data_error_messages;
	
	/**
	 * 1S121 서비스 재시도 에러 메시지
	 */
	private String[] data_error_1s121_messages;
	
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
		/* NICE 스크래핑 요청 KEY */ String NCRQ_SEQ = null;
		/* NICE 참가기관ID */ String NCRQ_ORG_ID = null;
		/* NICE 거래구분코드 */	 String NCRQ_BZCD = null;
		/* NICE 상품코드 */        String SCDL_G_BZCD = null;
		/* 기웅 전문코드 */          String SCDL_BZCD = null;
		/* 기웅 업무코드 */          String SCDL_JBCD = null;
		/* 스크래핑 처리횟수 */       String SCDL_CNT = null;
		/* 요청연월 */             String YYMM = null;
		/* 예외처리 클래스  */ 		Except except = null;
		/* 등록 일시 */			String SCDL_RDT	= null;
		
		String encoding = null;
		/* 결과코드 */    String rtcd = KaisCode.P000;
		/* 스크래핑 결과 */ KwScrapResult scrapResult = null;
		/* 요청원문 */    String ostr = null;
		
		try {
			data_error_messages 		= getThreadParamsString("data-error-messages").split("//");
			data_error_1s121_messages 	= getThreadParamsString("data-error-1s121-messages").split("//");
			
			encoding    = getThreadParamsString("data-encoding");			
			aibIp       = getThreadParamsString("ip");
			aibPort     = getThreadParamsInt("port");
			aibCryptKey = getThreadParamsString("crypt-key");
			aibTimeout  = (int) Calculator.calculate(getThreadParamsString("aib-timeout"));

			while (!stop) {
				try {
					rtcd = KaisCode.P000;
					requestMap = RequestAib.getRequest();
					
					/*
					 * 요청에 대한 응답이 없다면 1분간 대기한다.
					 */
					if (requestMap == null) {
						Thread.sleep(1);
						continue;
					}
					log.debug(String.format("First request %s", requestMap));
					
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
					
					//자동차
					if ("1S111".equals(requestMap.get("NCRQ_BZCD")) && "0009".equals(requestMap.get("SCDL_BZCD")) && "50".equals(requestMap.get("SCDL_JBCD"))) {
						requestMap.put("SCDL_JBCD", "61");
					}
					//건설기계
					else if ("1S111".equals(requestMap.get("NCRQ_BZCD")) && "0040".equals(requestMap.get("SCDL_BZCD")) && "51".equals(requestMap.get("SCDL_JBCD"))) {
						requestMap.put("SCDL_JBCD", "71");
					}
					

					YYMM        = String.valueOf(requestMap.get("YYMM"));
					SCDL_SEQ    = String.valueOf(requestMap.get("SCDL_SEQ"));    //AIB 거래키
					NCRQ_SEQ  	= String.valueOf(requestMap.get("NCRQ_SEQ"));    //NICE 스크래핑 요청  key
					NCRQ_ORG_ID = String.valueOf(requestMap.get("NCRQ_ORG_ID")); //NICE 참가기관ID
					NCRQ_BZCD   = String.valueOf(requestMap.get("NCRQ_BZCD"));   //거래구분코드
					SCDL_G_BZCD = String.valueOf(requestMap.get("SCDL_G_BZCD")); //NICE 상품코드
					SCDL_BZCD   = String.valueOf(requestMap.get("SCDL_BZCD"));   //기웅 전문코드
					SCDL_JBCD   = String.valueOf(requestMap.get("SCDL_JBCD"));   //기웅 업무코드
					SCDL_CNT    = String.valueOf(requestMap.get("SCDL_CNT"));    //스크래핑 요청횟수
					SCDL_RDT    = String.valueOf(requestMap.get("SCDL_RDT"));    //등록일시

					/*
					 * AIB 요청 파라미터
					 */
					aibReqParam.put("SPECIALCODE",    SCDL_BZCD);
					aibReqParam.put("MODULE",         SCDL_JBCD);
					aibReqParam.put("CERTKEY",        getThreadParamsString("CERTKEY"));
					aibReqParam.put("DEPARTMENTCODE", getThreadParamsString("DEPARTMENTCODE"));
					//aibReqParam.put("TRANSKEY",       SCDL_SEQ);
					aibReqParam.put("TRANSKEY", 	  "");
					aibReqParam.put("SCDL_SEQ",       SCDL_SEQ);
					aibReqParam.put("CST_PARAM2",     NCRQ_SEQ);
					
					aibReqParam.put("NICEORGID",      NCRQ_ORG_ID);	//NICE 참가기관ID
					aibReqParam.put("SCDL_CNT",       SCDL_CNT);

					/*
					 * AIB 응답 파라미터
					 */
					aibResParam.put("SPECIALCODE", SCDL_BZCD);
					aibResParam.put("MODULE",      SCDL_JBCD);
					aibResParam.put("SCDL_SEQ",    SCDL_SEQ);
					aibResParam.put("SCDL_CNT",    SCDL_CNT);
					aibResParam.put("SCDL_RDT",    SCDL_RDT);

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
						
						//자동차, 건설기계인 경우 PRINT값을 D로 설정, 그 외 다른업무들 PRINT 필드를 사용하지 않는다.
						if ("1S121".equals(requestMap.get("NCRQ_BZCD")) && "0018".equals(requestMap.get("SCDL_BZCD")) && "13".equals(requestMap.get("SCDL_JBCD"))) {
							tmpMap.put("PRINT", "Y");
						} else if ("1S111".equals(requestMap.get("NCRQ_BZCD")) && "0018".equals(requestMap.get("SCDL_BZCD")) && "13".equals(requestMap.get("SCDL_JBCD"))) {
							tmpMap.put("PRINT", "Y");
						} else {
							tmpMap.put("PRINT", "D");
						}
						
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
					
					try {
						log.debug(String.format("[전문호출] AIB Except.exceptRequest  %s", except.getClass().getSimpleName()));						
						// aib요청 data 생성
						aibReqParam = except.exceptRequest(aibReqParam, parsingReqInfoMap, threadParams);
						
					} catch (Exception e) {
						log.error(String.format("Creating AIB data is failed. parameters=%s request=%s", aibReqParam, parsingReqInfoMap), e);
						rtcd = KaisCode.S502;
						throw new DefinedException("Request exceptable process error [" + SCDL_SEQ + "]." + e.getMessage());
					}
					aibResParam.put("SCDL_AQTM", new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime())); //AIB 요청 시각
					
					log.debug(String.format("[처리시간측정] AIB request start. SCAF_ID=%s, %s", parsingReqInfoMap.get("SCAF_ID"), aibReqParam));////////////////////
					
					/*
					 * AIB 요청 전송 및 응답결과 수신
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

					aibResParam.put("SCDL_ASTM", new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime())); //AIB 응답 시각
					responseMap = scrapResult.getResponseMap();
					aibResParam.put("SCDL_ABRQ", scrapResult.getRequestXml()); //AIB 요청 XML		
					
					aibResParam.put("SCDL_ABRS", scrapResult.getDecryptResponseXml()); //AIB 응답 XML
					if("1S111".equals(requestMap.get("NCRQ_BZCD")) && (("0009".equals(requestMap.get("SCDL_BZCD")) && "61".equals(requestMap.get("SCDL_JBCD"))) || ("0040".equals(requestMap.get("SCDL_BZCD")) && "71".equals(requestMap.get("SCDL_JBCD"))))){
						JXParser resAib	= new JXParser(scrapResult.getDecryptResponseXml());
						if (!"E".equals(responseMap.get("INQNORMALPE"))) {
							resAib.removeElement("XMLFILEDATA2");
							
							//xml데이터를 파일로 저장하고 파일명은 SCAF_ID로 한다.
							String xmldataPath = StringUtil.replace(ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/xmldata"), "\\", "/") + File.separator + String.valueOf(parsingReqInfoMap.get("SCAF_ID")) + ".txt";
							if(!new File(xmldataPath).getParentFile().exists())
								new File(xmldataPath).getParentFile().mkdirs();
							
							FileIO.decodeBase64ToFile(FileIO.encodeBase64(String.valueOf(responseMap.get("XMLFILEDATA2")).getBytes()), new File(xmldataPath));
							
							aibResParam.put("SCDL_XMLDATA", String.valueOf(parsingReqInfoMap.get("SCAF_ID")));
						}
						aibResParam.put("SCDL_ABRS", resAib.toString(null)); //AIB 응답 XML
					}
					
					//홈택스 민원증명 발급번호 진위 조회 - 주민/사업자번호 처리
					if("1S101".equals(requestMap.get("NCRQ_BZCD")) && "0078".equals(requestMap.get("SCDL_BZCD")) && "10".equals(requestMap.get("SCDL_JBCD"))){
						JXParser resAib	= new JXParser(scrapResult.getDecryptResponseXml());
						resAib.addElement(resAib.getElement("//OUTPUT"), "REGNUMBER");
						resAib.setAttribute(resAib.getElement("//OUTPUT/REGNUMBER"), "VALUE", String.valueOf(parsingReqInfoMap.get("SSN")));
						aibResParam.put("SCDL_ABRS", resAib.toString(null)); //AIB 응답 XML
					}
					
					aibResParam.put("SCDL_ABIP", String.valueOf(responseMap.get("SCRAPIP")));
					
					//xml데이터를 파일로 저장하고 파일명은 SCAF_ID로 한다.
					/*String xmldataPath = StringUtil.replace(ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/xmldata"), "\\", "/") + File.separator + String.valueOf(parsingReqInfoMap.get("SCAF_ID")) + ".txt";
					if(!new File(xmldataPath).getParentFile().exists())
						new File(xmldataPath).getParentFile().mkdirs();
					
					FileIO.decodeBase64ToFile(FileIO.encodeBase64(String.valueOf(responseMap.get("XMLFILEDATA2")).getBytes()), new File(xmldataPath));
					
					aibResParam.put("SCDL_XMLDATA", String.valueOf(parsingReqInfoMap.get("SCAF_ID")));*/
//					aibResParam.put("SCDL_XMLDATA", String.valueOf(responseMap.get("XMLFILEDATA2")));
					
					log.debug(String.format("[AIB 응답1] SCAF_ID=%s", parsingReqInfoMap.get("SCAF_ID")));
					log.debug(String.format("[AIB 응답2] SCDL_ABRQ=%s", aibResParam.get("SCDL_ABRQ")));
					log.debug(String.format("[AIB 응답3] SCDL_ABRS=%s", aibResParam.get("SCDL_ABRS")));
					log.debug(String.format("[AIB 응답4] SCDL_XMLDATA=%s", aibResParam.get("SCDL_XMLDATA")));
					log.debug(String.format("[AIB 응답5] responseMap=%s", responseMap));
					
					// aib 스크래핑 시스템 오류
					if (responseMap == null || responseMap.get("INQNORMALPE") == null || !"P".equals(responseMap.get("INQNORMALPE"))) {
						rtcd = "E" + scrapResult.getErrorCode();
						log.error(String.format("[스크래핑 오류] Processing AIB Error>>>. INQNORMALPE=%s, error code=%s, error=%s", responseMap.get("INQNORMALPE"), scrapResult.getErrorCode(), scrapResult.getErrorMessage()));
						throw new DefinedException(scrapResult.getErrorMessage());
					}
					
					aibResParam.put("SCDL_RTCD", rtcd); //스크래핑 응답코드
					
					log.debug("scrapResult.getResult() :: "+scrapResult.getResult());
					log.debug("scrapResult.getErrorCode() :: "+scrapResult.getErrorCode());
					log.debug("scrapResult.getErrorMessage() :: "+scrapResult.getErrorMessage());
					
					if (!scrapResult.getResult() || !"000".equals(scrapResult.getErrorCode())){
						aibResParam.put("SCDL_RTMSG", scrapResult.getErrorMessage()); //스캐리핑 처리 메시지
					}
					
					try {
						/**
						 * AIB 응답저장
						 */
						saveResponse(aibReqParam, aibResParam, YYMM);
					} catch (Exception e) {
						log.error(String.format("Saving AIB response is failed. request parameters=%s, response parameters=%s", aibReqParam, aibResParam), e);
						rtcd = KaisCode.S503;
						throw new DefinedException("Fail to save " + SCDL_BZCD + "-" + SCDL_JBCD + " response [" + SCDL_SEQ + "]." + e.getMessage());
					}
				} catch (Exception e) {
					log.error(String.format("Processing AIB data is failed. request parameters=%s, response parameters=%s", aibReqParam, aibResParam), e);
					try {
						// aib 응답 실패 처리
						saveResponse(aibReqParam, aibResParam, rtcd, YYMM, e);
					} catch (Exception ex) {
						log.error(String.format("After error, saving AIB failure is failed. return code=%s, request parameters=%s, response parameters=%s", rtcd, aibReqParam, aibResParam), e);
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
	public void saveResponse(Map<String, String> aibReqParam, Map<String, String> aibResParam, String rtcd, String YYMM, Exception error) throws Exception {
		if (KaisCode.P000.equals(rtcd)){
			rtcd = KaisCode.E859; //AIB 요청 중 정의되지 않은 오류
		}
		aibResParam.put("SCDL_RTCD",  rtcd);
		aibResParam.put("SCDL_RTMSG", error.getMessage());
		saveResponse(aibReqParam, aibResParam, YYMM);
	}

	/**
	 * 스크래핑 요청 전문 DB 저장
	 * @param aibReqParam
	 * @param aibResParam
	 * @param YYMM
	 * @throws Exception
	 */
	public void saveResponse(Map<String, String> aibReqParam, Map<String, String> aibResParam, String YYMM)	throws Exception {
		
		/*
		 * 1) 스크래핑 응답코드로 [스크래핑 처리상태]가 결정된다.
		 * 스크래핑 응답코드가 성공이 아니고 스크래핑 요청횟수가 2회 미만이면 처리상태는 A0 스크래핑 대상이 된다.
		 */
		if (!KaisCode.P000.equals(aibResParam.get("SCDL_RTCD")) && Integer.parseInt(String.valueOf(aibResParam.get("SCDL_CNT"))) < 2) {
			aibResParam.put("SCDL_STS", KaisState.AIB_TGT_A0);// 스크래핑 대상
		} else {
			aibResParam.put("SCDL_STS", KaisState.INFO_TGT_R0);// 정보등록 대상
		}

		for (Entry<String, String> entry : aibResParam.entrySet()) {
			log.debug("AIBRES-PARAM[" + entry.getKey() + "] = " + entry.getValue());
		}

		/*
		 * 2) 스크래핑 처리 메시지로 [스크래핑 처리상태]가 결정된다. 
		 */
		String msg = aibResParam.get("SCDL_RTMSG") == null ? "" : String.valueOf(aibResParam.get("SCDL_RTMSG"));
		
		if (msg.indexOf(":") >= 0) {
			msg = msg.substring(msg.indexOf(":") + 1);
		}
		
		//예외처리관련 오류 메시지 발생 시 모든 전문은 정보등록 대상으로 상태값이 변경된다.
		//1S111 원본처리경우 데이터부/원본저장부 2개의 통신으로 이루어 지지만, 예외처리관련 오류 메시지 발생 시 원본저장부를 거치지 않고 정보등록 상태로 변경되고, 로깅등록 시 FTP 코드는 18로 전송된다.
		if (!KaisCode.P000.equals(aibResParam.get("SCDL_RTCD")) && !"".equals(msg)) {
			// 등록된 데이터 오류 메시지일 경우 재처리하지 않는다.
			for (int i = 0; i < data_error_messages.length; i++) {
				String errMsg	= data_error_messages[i].trim().substring(4);
				if (msg.trim().indexOf(errMsg) >= 0) {
					aibResParam.put("SCDL_STS", KaisState.INFO_TGT_R0);// 정보등록 대상
					break;
				}
			}
			
			//1S121 홈택스/국민연금 재시도가 필요한 메시지에 대한 처리
			if ("1S121".equals(aibResParam.get("NCRQ_BZCD")) && data_error_1s121_messages != null) {
				//최초 데이터가 등록 된 시간에 설정되어 있는 시간이 지날때 까지 A0 상태로 변경
				long RETRY_TIME_1S121	= getThreadParamsInt("RETRY_TIME_1S121");
				
				try {
					java.text.SimpleDateFormat sdf	= new java.text.SimpleDateFormat("yyyyMMddkkmmss", Locale.KOREA);
					
					java.util.Date SCDL_RDT		= sdf.parse(aibResParam.get("SCDL_RDT"));
					java.util.Date currentTime	= sdf.parse(sdf.format(new java.util.Date()));
					
					long diff		= currentTime.getTime() - SCDL_RDT.getTime();
					long minutes	= diff / 1000 / 60;
					
					if (RETRY_TIME_1S121 >= minutes) {
						for (int i = 0; i < data_error_1s121_messages.length; i++) {
							String errMsg	= data_error_1s121_messages[i].trim();
							if (msg.trim().indexOf(errMsg) >= 0) {
								aibResParam.put("SCDL_STS", KaisState.AIB_TGT_A0);	//AIB요청 대상
								aibResParam.put("TRY_CNT", "0");	//재시도 횟수 초기화
								break;
							}
						}
					}
					
				} catch (Exception e) {
					log.error("Unknown AIB client error.", e);
				}
			}
		}
		
		// 메시지에 따른 오류코드 분기
		// 스크래핑 결과값이 E 로 떨어진 경우 S502(스크래핑 모듈 수행 중 조회정상여부 값이 E일 때)
		if (aibResParam.get("SCDL_RTCD").indexOf("E") > -1) {
			String erCd = KaisCode.S502;
			if (!"".equals(msg)) {
				for (int i = 0; i < data_error_messages.length; i++) {
					String errMsg	= data_error_messages[i].trim().substring(4);
					if (msg.trim().indexOf(errMsg) >= 0) {
						erCd	=  data_error_messages[i].trim().substring(0,4);
						break;
					}
				}
			}
			
			if (erCd != null){
				aibResParam.put("SCDL_RTCD", erCd);
			}
		}
		
		//AIB 요청 XML 암호화
		if (aibResParam.get("SCDL_ABRQ") == null){
			aibResParam.put("SCDL_ABRQ", "");
		}else {
			String abrq = AESCipher.encode(aibResParam.get("SCDL_ABRQ"), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256,	"UTF-8", AESCipher.MODE_ECB_NOPADDING);
			aibResParam.put("SCDL_ABRQ", abrq);
		}
		
		//AIB 응답 XML 암호화
		if (aibResParam.get("SCDL_ABRS") == null){
			aibResParam.put("SCDL_ABRS", "");
		}else {
			String abrs = AESCipher.encode(aibResParam.get("SCDL_ABRS"), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256,	"UTF-8", AESCipher.MODE_ECB_NOPADDING);
			aibResParam.put("SCDL_ABRS", abrs);
		}
		
		//AIB 요청 시각
		if (aibResParam.get("SCDL_AQTM") == null){
			aibResParam.put("SCDL_AQTM", "");
		}
		
		//AIB 응답 시각
		if (aibResParam.get("SCDL_ASTM") == null){
			aibResParam.put("SCDL_ASTM", "");
		}

		try {
			aibResParam.put("YYMM", YYMM);
			
			log.debug(String.format("[DB-SAVE_RESPONSE_AIB] AIB 요청 및 응답 저장. SCDL_SEQ=%s, SCDL_STS=%s, SCDL_RTCD=%s, SCDL_RTMSG=%s", aibResParam.get("SCDL_SEQ"), aibResParam.get("SCDL_STS"), aibResParam.get("SCDL_RTCD"), aibResParam.get("SCDL_RTMSG")   ));
			/**
			 * AIB 요청 및 응답 저장
			 */
			scrapingService.SAVE_RESPONSE_AIB(aibResParam);
		} catch (Exception e) {
			log.error(String.format("Saving AIB result is failed. %s", aibResParam), e);
		}
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
