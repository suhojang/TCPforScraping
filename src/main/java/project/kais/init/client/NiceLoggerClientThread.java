package project.kais.init.client;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;

import project.kais.init.KaisCode;
import project.kais.init.KaisState;
import project.kais.init.client.except.Except;
import project.kais.init.exception.PropertyNotFoundException;
import project.kais.init.server.parser.CommonParser;
import project.kais.init.server.parser.Parser;
import project.kais.service.ScrapingService;

import com.kwic.exception.DefinedException;
import com.kwic.math.Calculator;
import com.kwic.security.aes.AESCipher;
import com.kwic.telegram.tcp.JTcpManager;
import com.kwic.util.ByteUtil;
import com.kwic.util.StringUtil;
import com.kwic.xml.parser.JXParser;

public class NiceLoggerClientThread extends Thread {

	private static Logger log = LoggerFactory.getLogger(NiceLoggerClientThread.class);

	private boolean stop;

	/** service */
	private ScrapingService scrapingService;

	Map<String, Object> threadParams;	

	@Override
	public String toString() {
		return "NiceLoggerClientThread [stop=" + stop + ", service=" + scrapingService + ", initServiceParams=" + threadParams + "]";
	}

	public void setThreadParams(Map<String, Object> threadParams) {
		this.threadParams = threadParams;
	}
	
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
	

	@Override
	public void run() {
		scrapingService = (ScrapingService) getBean("ScrapingService");

		Map<String, Object> requestMap = null;
		byte[] requestBytes = null;
		
		Map<String, Object> mappingInfoMap = null;
		
		Map<String, Object> responseMap = null;
		byte[] responseBytes = null;
		
		String aibReqXml = null;
		String aibResXml = null;
		String ostr = null;
		String SCDL_SEQ = null;
		String NCRQ_BZCD = null;
		String SCDL_G_BZCD = null;
		String SCDL_CNT3 = null;
		String SCDL_OSTR = null;
		String SCDL_NRTCD	= null;
		String YYMM = null;
		Except except = null;
		String rtcd = null;
		String bzcd = null;
		byte[] lengthBytes = null;

		try {
			String encoding       = getThreadParamsString("peer-encoding");
			int lengthStartIndex  = getThreadParamsInt("length-start-index");
			int lengthSize        = getThreadParamsInt("length-size");
			
			while (!stop) {
				try {
					requestMap = RequestNiceLogger.getRequest();
					if (requestMap == null) {
						Thread.sleep(1);
						continue;
					}
					log.debug(String.format("[로깅등록] 등록대상 정보 requestMap=%s", requestMap));

					aibReqXml = AESCipher.decode(String.valueOf(requestMap.get("SCDL_ABRQ")), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING);
					requestMap.put("SCDL_ABRQ", aibReqXml);//AIB 요청 XML

					aibResXml = AESCipher.decode(String.valueOf(requestMap.get("SCDL_ABRS")), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING);
					requestMap.put("SCDL_ABRS", aibResXml);//AIB 응답 XML

					ostr = AESCipher.decode(String.valueOf(requestMap.get("SCDL_OSTR")), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING);
					requestMap.put("SCDL_OSTR", ostr);//NICE요청 문자열 원문

					YYMM        = String.valueOf(requestMap.get("YYMM"));
					NCRQ_BZCD   = String.valueOf(requestMap.get("NCRQ_BZCD"));   //기웅 전문코드
					SCDL_G_BZCD = String.valueOf(requestMap.get("SCDL_G_BZCD")); //NICE 상품코드
					SCDL_CNT3   = String.valueOf(requestMap.get("SCDL_CNT3"));   //로그등록 처리횟수
					SCDL_SEQ    = String.valueOf(requestMap.get("SCDL_SEQ"));    //AIB 거래키
					SCDL_OSTR   = String.valueOf(requestMap.get("SCDL_OSTR"));   //NICE요청 문자열 원문
					SCDL_NRTCD	= String.valueOf(requestMap.get("SCDL_NRTCD"));  //NICE정보등록 응답코드

					Class<?> cls = null;
					try {
						cls = Class.forName("project.kais.init.client.except.Except_" + NCRQ_BZCD + "_" + SCDL_G_BZCD);
					} catch (ClassNotFoundException ce) {
						cls = Class.forName("project.kais.init.client.except.CommonExcept");
					}
					try {
						except = (Except) cls.newInstance();
					} catch (Exception e) {
						log.error(String.format("Cannot found Except Class. Because of undefined business code [%s_%s].", NCRQ_BZCD, SCDL_G_BZCD), e);
						rtcd = KaisCode.E860; //NICE 응답 거래구분코드 확인 불가
						throw new DefinedException("Undefined business code [" + NCRQ_BZCD + "_" + SCDL_G_BZCD + "]." + e.getMessage());
					}
					
					// LOGGING is Fixed
					Map<String, Object> niceInfo;
					try {
						log.debug(String.format("[전문호출] 로깅등록 Parser.parse"));	
						
						/*
						 * /WEB-INF/struct/ 밑에 있는 XML 파일들의 명명규칙상 언더바(_)가 아닌 대시(-)를 써야 한다.
						 */
						niceInfo = new CommonParser().parse(NCRQ_BZCD + "-" + SCDL_G_BZCD, SCDL_OSTR.getBytes(encoding), encoding, Parser.REQUEST);
					} catch (Exception e) {
						log.error(String.format("Conversion of request sepcial statement is failed. bzCd=%s, origin=%s, encoding=%s", NCRQ_BZCD + "-" + SCDL_G_BZCD, SCDL_OSTR, encoding), e);
						rtcd = KaisCode.S503;
						throw new DefinedException("Request parsing error [" + SCDL_SEQ + "]." + e.getMessage());
					}

					for (Entry<String, Object> entry : niceInfo.entrySet()) {
						log.debug("NICEINFO - " + entry.getKey() + " = " + entry.getValue());
					}

					bzcd = "1S103";
					try {
						log.debug(String.format("[전문호출] 로깅등록 Excep.mapNiceRequest %s ,requestMap=%s", except.getClass().getSimpleName(), requestMap));							
						mappingInfoMap = except.mapNiceRequest(bzcd, requestMap);
					} catch (Exception e) {
						log.error(String.format("NICE request mapping is failed. bzCd=%s, request=%s", bzcd, requestMap), e);
						rtcd = KaisCode.E861; //NICE 요청 데이터 매핑 오류
						throw new DefinedException("Request mapping error [" + SCDL_SEQ + "]." + e.getMessage());
					}
					// 기본설정 정보
					//mappingInfoMap.put("H_ORG_ID",    requestMap.get("NCRQ_ORG_ID"));	//참가기관ID
					
					//참가기관ID 변경 - 2018.11.20 나이스신용평가 요청으로 인해 기존에 들어오던 참가기관 ID를 회원사ID로 치환하고, 새로운 참가기관ID를 송신한다.
					mappingInfoMap.put("H_ORG_TL_NO", String.valueOf(requestMap.get("SCDL_SEQ")).substring(10));	//기관전문 관리번호
					mappingInfoMap.put("H_ORG_RQTM",  new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime()));	//기관전문 전송시간
					mappingInfoMap.put("G_BZCD",      requestMap.get("SCDL_G_BZCD"));	//NICE 상품코드
					mappingInfoMap.put("CST_MBR_NO",  requestMap.get("SCDL_MBRNO"));	//회원사고객번호
					mappingInfoMap.put("RTCD",        requestMap.get("SCDL_RTCD"));		//스크래핑 응답코드
					mappingInfoMap.put("SCAF_ID",     requestMap.get("SCDL_SCAF_ID"));	//NICE SCAF_ID
					mappingInfoMap.put("PRODCD",      requestMap.get("SCDL_G_BZCD"));	//NICE 상품코드
					mappingInfoMap.put("ORG_ID",      requestMap.get("NCRQ_ORG_ID"));	//(참가기관ID)로 받은 값을 (회원사ID)로 송신  - 40자리
					
					mappingInfoMap.put("H_RTCD",      (SCDL_NRTCD != null && !KaisCode.P000.equals(SCDL_NRTCD)) ? SCDL_NRTCD : "");	//응답코드 추가

					try {
						log.debug(String.format("[전문호출] 로깅등록 Excep.exceptResponse %s", except.getClass().getSimpleName()));							
						mappingInfoMap = except.exceptResponse(mappingInfoMap, requestMap, threadParams);
					} catch (Exception e) {
						log.error(String.format("Request exceptable process error. %s", mappingInfoMap), e);
						rtcd = KaisCode.E862; //NICE 요청 데이터 예외 사항 처리 오류
						throw new DefinedException("Request exceptable process error [" + SCDL_SEQ + "]." + e.getMessage());
					}
					mappingInfoMap.put("SCDL_SEQ",  SCDL_SEQ);  //AIB 거래키
					mappingInfoMap.put("SCDL_CNT3", SCDL_CNT3); //로그등록 처리횟수

					String specialCode = String.valueOf(requestMap.get("SCDL_BZCD")); //기웅 전문코드
					String module      = String.valueOf(requestMap.get("SCDL_JBCD"));  //기웅 업무코드

					log.debug(String.format("로깅등록을 위한 aib 요청전문xml  aibReqXml=%s", aibReqXml));	
					log.debug(String.format("로깅등록을 위한 aib 응답전문xml  aibResXml=%s", aibResXml));	

					//logging-condition.xml에 정의된 값을 읽어오긴 위한 처리
					Map<String, String> tmpMap = getParamFromAib(specialCode, module, aibReqXml, aibResXml);
					log.debug(String.format("로깅등록을 위한 aib 전문해석결과  tmpMap=%s", tmpMap));	
					
					
					for (Entry<String, String> entry : tmpMap.entrySet()) {
						mappingInfoMap.put(entry.getKey(), entry.getValue());
					}
					
					mappingInfoMap.put("FILESTEP",    requestMap.get("SCDL_FTPCD")); //NICE FTP로 전송한 결과를 처리결과코드로 보낸다.
					/*
					 * 요청 정보 생성
					 */
					try {
						log.debug(String.format("[전문호출] 로깅등록 Excep.makeNiceRequestBytes %s, mappingInfoMap=%s", except.getClass().getSimpleName(), mappingInfoMap));							
						requestBytes = except.makeNiceRequestBytes(bzcd, mappingInfoMap, encoding);
						lengthBytes = ByteUtil.addByte(String.valueOf(requestBytes.length - ("0".equals(getThreadParamsString("length-contain-self")) ? lengthSize : 0)).getBytes(), ByteUtil.APPEND_CHARACTER_ZERO, lengthSize, true);
						System.arraycopy(lengthBytes, 0, requestBytes, lengthStartIndex, lengthSize);
					} catch (Exception e) {
						log.error(String.format("Making request byte array is failed. bzCd=%s, parameters=%s, encoding=%s", bzcd, mappingInfoMap, encoding), e);
						rtcd = KaisCode.E863; //NICE 요청 데이터 생성 오류
						throw new DefinedException("Fail to send request bytes [" + SCDL_SEQ + "]." + e.getMessage());
					}
					mappingInfoMap.put("SCDL_LTRQ", new String(requestBytes, encoding));
					
					/*
					 * NICE 요청 송신
					 */
					try {
						log.debug(String.format("[로깅등록 요청] bzcd=%s\n###%s###", bzcd, new String(requestBytes, encoding)));
						responseBytes = request(requestBytes);
						log.debug(String.format("[로깅등록 응답] bzcd=%s\n###%s###", bzcd, new String(responseBytes, encoding)));
					} catch (Exception e) {
						log.error("Request sending is failed", e);
						rtcd = KaisCode.E864; //NICE 요청 데이터 송신 오류
						throw new DefinedException(" [" + SCDL_SEQ + "]." + e.getMessage());
					}
					
					log.debug(String.format("[전문호출] 로깅등록 Excep.mapNiceResponse %s", except.getClass().getSimpleName()));							
					// byte to map
					responseMap = except.mapNiceResponse(bzcd, responseBytes, encoding, Parser.RESPONSE);
					responseMap.put("SCDL_LTRS", new String(responseBytes, encoding));
					try {
						// save nice response
						saveResponse(YYMM, mappingInfoMap, responseMap);
					} catch (Exception e) {
						log.error("Saving response is failed", e);
						rtcd = KaisCode.E865; //NICE 응답 데이터 저장 오류
						throw new DefinedException("Fail to save " + bzcd + " response [" + SCDL_SEQ + "]." + e.getMessage());
					}
					
				} catch (Exception e) {
					// aib 응답 실패 처리
					try {
						saveResponse(rtcd, YYMM, mappingInfoMap, responseMap, e);
					} catch (Exception ex) {
						log.error(String.format("Saving response is failed. return code=%s, parameters=%s, response=%s", rtcd, mappingInfoMap, responseMap), ex);
					}
					log.error("Processing response is failed", e);
				} finally {
					try {
						Thread.sleep(1);
					} catch (Exception e) {
						log.debug("AibClient is awaken. " + e);
					}
				}
			}//while
			
		} catch (Exception e) {
			log.error("Logging response is failed", e);
		} finally {
			log.debug("AibClient terminated");
		}
	}	

	/**
	 * 로깅 조건을 xml 파일에서 map으로 가져옴.
	 * @param specialCode 기웅 전문코드
	 * @param module 기웅 업무코드
	 * @param abrq AIB 요청 XML
	 * @param abrs AIB 응답 XML
	 * @return
	 */
	private Map<String, String> getParamFromAib(String specialCode, String module, String abrq, String abrs) {		
		String structPath = StringUtil.replace(ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/WEB-INF/struct"), "\\", "/") + File.separator + "logging-condition.xml";
		try {
			JXParser jxp = new JXParser(new File(structPath));
			if (abrq.length() > 10 && abrq.indexOf("<?xml") > 0) {
				abrq = abrq.substring(10);
			}
			if (abrq == null || "".equals(abrq)) {
				abrq = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root/>";
			}
			if (abrs == null || "".equals(abrs)) {
				abrs = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root/>";
			}
			JXParser jxpReq = new JXParser(abrq);
			JXParser jxpRes = new JXParser(abrs);
			return RequestNiceLogger.getParams(jxp, specialCode, module, jxpReq, jxpRes);
		} catch (Exception e) {
			log.error(String.format("Parsing of loggin condition is failed. special code=%s, business code=%s xml file path=%s", specialCode, module, structPath), e);
		}

		return new HashMap<String, String>();
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	private static Object getBean(String name) {
		return ContextLoader.getCurrentWebApplicationContext().getBean(name);
	}

	/**
	 * 
	 */
	public void terminate() {
		stop = true;
	}
	
	/**
	 * 요청 송신 및 응답 수신
	 * @param bytes 응답
	 * @return
	 * @throws Exception
	 */
	private byte[] request(byte[] bytes) throws Exception {
		int timeout = (int) Calculator.calculate(getThreadParamsString("nice-timeout"));
		String ip = getThreadParamsString("ip");
		int port = getThreadParamsInt("port");

		int lenStIdx = getThreadParamsInt("length-start-index");
		int lenSz = getThreadParamsInt("length-size");
		int lengthContainSelf = "0".equals(getThreadParamsString("length-contain-self")) ? lenSz : 0;

		byte[] lengthBytes = ByteUtil.addByte(String.valueOf(bytes.length - lengthContainSelf).getBytes(),
				                              ByteUtil.APPEND_CHARACTER_ZERO, 
				                              lenSz, 
				                              true);
		// set total length
		System.arraycopy(lengthBytes, 0, bytes, lenStIdx, lenSz);

		byte[] response = JTcpManager.getInstance().sendMessageWithoutEOF(ip, port, timeout, 
				                                                          null,  /*header*/
				                                                          bytes, /*body*/
				                                                          lenSz,	//10
				                                                          lenStIdx,	//0 
				                                                          lenSz,	//10 
				                                                          "0".equals(getThreadParamsString("length-contain-self")) ? JTcpManager._LENGTH_TYPE_BODYONLY : JTcpManager._LENGTH_TYPE_FULLSIZE, 
				                                                          true);
		return response;
	}

	/**
	 * 응답결과코드, 응답메시지 등 응답 로그 DB 저장
	 * @param rtcd
	 * @param YYMM
	 * @param reqMap
	 * @param resMap
	 * @param error
	 * @throws Exception
	 */
	private void saveResponse(String rtcd, String YYMM, Map<String, Object> reqMap, Map<String, Object> resMap, Exception error) throws Exception {
		if (KaisCode.P000.equals(rtcd)){
			rtcd = KaisCode.E859;//AIB 요청 중 정의되지 않은 오류
		}
		if (resMap == null){
			resMap = new HashMap<String, Object>();
		}
		resMap.put("SCDL_RTCD",  rtcd);
		resMap.put("SCDL_RTMSG", error.getMessage());
		saveResponse(YYMM, reqMap, resMap);
	}

	/**
	 * 응답에 대한 로그 DB 저장
	 * @param YYMM
	 * @param reqMap 요청정보 (로그등록 처리횟수, 로그등록 요청정보, 로그등록 요청시각, AIB 거래키)
	 * @param resMap 응답정보 (로그등록 응답코드, 로그등록 요청 수신응답 시각,에러체크비트, 오류응답코드)
	 * @throws Exception
	 */
	private void saveResponse(String YYMM, Map<String, Object> reqMap, Map<String, Object> resMap) throws Exception {
		Map<String, String> param = new HashMap<String, String>();
		
		if(resMap.get("H_RTCD") == null && Integer.parseInt(String.valueOf(reqMap.get("SCDL_CNT3"))) < 2){
			param.put("SCDL_STS", KaisState.LOG_TGT_L0); //NICE로그등록 대상			
		}else{
			param.put("SCDL_STS", KaisState.LOG_RCV_L2);//NICE로그등록 요청 수신응답
		}

		String rtrq = AESCipher.encode(String.valueOf(reqMap.get("SCDL_LTRQ")), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING);
		String rtrs = AESCipher.encode(String.valueOf(resMap.get("SCDL_LTRS")), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING);
		
		param.put("YYMM", YYMM);	
		param.put("SCDL_NRLCD",    String.valueOf(resMap.get("H_RTCD"))); //로그등록 응답코드
		param.put("SCDL_LTRQ",     rtrq); //로그등록 요청
		param.put("SCDL_LTRS",     rtrs); //로그등록 수신응답
		param.put("SCDL_LQTM",     String.valueOf(reqMap.get("H_ORG_RQTM"))); //로그등록 요청 시각
		param.put("SCDL_LSTM",     String.valueOf(resMap.get("H_NCE_RQTM"))); //로그등록 요청 수신응답 시각
		param.put("SCDL_ER_CK_BT", resMap.get("ER_CK_BT") == null ? "" : String.valueOf(resMap.get("ER_CK_BT"))); //에러체크비트
		param.put("SCDL_ER_RTCD",  resMap.get("ER_RTCD")  == null ? "" : String.valueOf(resMap.get("ER_RTCD"))); //오류응답코드
		param.put("SCDL_SEQ",      String.valueOf(reqMap.get("SCDL_SEQ"))); //AIB 거래키
		
		log.debug(String.format("[DB-SAVE_RESPONSE_NICE_LOGGER] 로깅등록 결과 저장. SCDL_SEQ=%s, SCDL_STS=%s, SCDL_NRLCD=%s, SCDL_ER_CK_BT=%s, SCDL_ER_RTCD=%s", param.get("SCDL_SEQ"), param.get("SCDL_STS"), param.get("SCDL_NRLCD"), param.get("SCDL_ER_CK_BT"), param.get("SCDL_ER_RTCD")  ));
		scrapingService.SAVE_RESPONSE_NICE_LOGGER(param);
	}
}
