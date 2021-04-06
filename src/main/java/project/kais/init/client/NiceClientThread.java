package project.kais.init.client;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
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
import com.kwic.math.Calculator;
import com.kwic.security.aes.AESCipher;
import com.kwic.telegram.tcp.JTcpManager;
import com.kwic.util.ByteUtil;
import com.kwic.util.StringUtil;
import com.kwic.xml.parser.JXParser;

import project.kais.init.KaisCode;
import project.kais.init.KaisState;
import project.kais.init.NiceProcessStatus;
import project.kais.init.client.except.CommonExcept;
import project.kais.init.client.except.Except;
import project.kais.init.exception.PropertyNotFoundException;
import project.kais.init.server.parser.CommonParser;
import project.kais.init.server.parser.Parser;
import project.kais.service.ScrapingService;

/**
 * 1. RequestAib.requestList에서 대상건 추출 
 * 2. 대상건으로 AIB 요청 xml생성 
 * 3. aib요청
 * 4. aib응답내용을  db에 반영
 */
public class NiceClientThread extends Thread {

	private static Logger log = LoggerFactory.getLogger(NiceClientThread.class);

	private boolean stop;

	/** service */
	private ScrapingService scrapingService;

	Map<String, Object> threadParams;

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
	
	/**
	 * 정보등록 실행
	 */
	@Override
	public void run() {
		scrapingService = (ScrapingService) getBean("ScrapingService");
		
		/* FTP 업로드 필요여부 */
		boolean ftpNeed = false;
		boolean isSuccess = false;

		/* NICE로 송신할 요청정보 */	Map<String, Object> requestMap = null;
		byte[] requestBytes = null;
		Map<String, Object> mappingInfoMap = null;
		
		Map<String, Object> responseMap = null;
		byte[] responseBytes = null;
		
		String abrq = null;///
		String abrs = null;
		String ostr = null;
		
		/* AIB 거래키  */	  String SCDL_SEQ = null;
		/* 거래구분코드 */    String NCRQ_BZCD = null;
		/* NICE 상품코드 */  String SCDL_G_BZCD = null;
		/* 정보등록 처리횟수 */ String SCDL_CNT2 = null;
		
		String YYMM = null;
		Except except = null;
		String rtcd = null;
		String bzcd = null;
		
		byte[] lengthBytes = null;
		
		Map<String, String> logingReqParam = null;
		Map<String, String> logingResParam = null;

		try {
			String encoding      = getThreadParamsString("peer-encoding");
			int lengthStartIndex = getThreadParamsInt("length-start-index");
			int lengthSize       = getThreadParamsInt("length-size");
			
			while (!stop) {
				try {
					requestMap 		= RequestNice.getRequest();
					
					mappingInfoMap	= new HashMap<String, Object>();
					responseMap		= new HashMap<String, Object>();
					
					if (requestMap == null) {
						Thread.sleep(1);
						continue;
					}
					
					abrq = AESCipher.decode(String.valueOf(requestMap.get("SCDL_ABRQ")), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING);
					abrs = AESCipher.decode(String.valueOf(requestMap.get("SCDL_ABRS")), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING);
					requestMap.put("SCDL_ABRS", abrs); //AIB 응답 XML

					ostr = AESCipher.decode(String.valueOf(requestMap.get("SCDL_OSTR")), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING);
					requestMap.put("SCDL_OSTR", ostr); //NICE요청 문자열 원문

					YYMM        = String.valueOf(requestMap.get("YYMM"));
					SCDL_SEQ    = String.valueOf(requestMap.get("SCDL_SEQ"));    //AIB 거래키
					NCRQ_BZCD   = String.valueOf(requestMap.get("NCRQ_BZCD"));   //거래구분코드
					SCDL_G_BZCD = String.valueOf(requestMap.get("SCDL_G_BZCD")); //NICE 상품코드
					SCDL_CNT2   = String.valueOf(requestMap.get("SCDL_CNT2"));   //정보등록 처리횟수
					
					//requestMap
					//0078-71, 0016-9
					//홈택스, 국민연금은 정보등록요청 전문을 전송하지 않으므로, 초기화 필요. isSuccess는 무조건 true로
					if(("0078".equals(requestMap.get("SCDL_BZCD")) && "71".equals(requestMap.get("SCDL_JBCD")))
							|| ("0016".equals(requestMap.get("SCDL_BZCD")) && "9".equals(requestMap.get("SCDL_JBCD")))
							 || ("0018".equals(requestMap.get("SCDL_BZCD")) && "13".equals(requestMap.get("SCDL_JBCD")))
							){
						//정보등록요청 전문을 보내지 않기 위한 설정
						mappingInfoMap.put("SCDL_SEQ", SCDL_SEQ);	//요청
						responseMap.put("H_RTCD", KaisCode.P000);	//응답
						responseMap.put("INFO_PASS", true);
						
						mappingInfoMap.put("NCRQ_BZCD", NCRQ_BZCD);
						
						isSuccess		= true;
						
					}else{
						//bzcd-mapper.xml에서 3A시리즈를 찾아준다.
						bzcd = new CommonExcept().getResponseBzCd(NCRQ_BZCD + "-" + SCDL_G_BZCD);
						
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
							rtcd = KaisCode.E860; //NICE 응답 거래구분코드 확인 불가
							throw new DefinedException("Undefined business code [" + NCRQ_BZCD + "_" + SCDL_G_BZCD + "]." + e.getMessage());
						}
						
						try {
							log.debug(String.format("[전문호출] 정보등록 Except.mapNiceRequest %s", except.getClass().getSimpleName()));						
							mappingInfoMap = except.mapNiceRequest(bzcd, requestMap);
							log.debug(String.format("정보등록 요청 매핑 결과  %s", mappingInfoMap));
						} catch (Exception e) {
							log.error(String.format("NICE exception handling is failed. %s %s", bzcd, requestMap), e);
							rtcd = KaisCode.E861; //NICE 요청 데이터 매핑 오류
							throw new DefinedException("Request mapping error [" + SCDL_SEQ + "]." + e.getMessage());
						}
						
						// 기본설정 정보
						mappingInfoMap.put("NCRQ_BZCD", 	NCRQ_BZCD);
						mappingInfoMap.put("H_ORG_ID",    requestMap.get("NCRQ_ORG_ID")); //참가기관ID
						mappingInfoMap.put("H_ORG_TL_NO", String.valueOf(requestMap.get("SCDL_SEQ")).substring(10)); //AIB 거래키
						mappingInfoMap.put("H_ORG_RQTM",  new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime())); //기관요청시각
						mappingInfoMap.put("G_BZCD",      requestMap.get("SCDL_G_BZCD")); //NICE 상품코드
						mappingInfoMap.put("CST_MBR_NO",  requestMap.get("SCDL_MBRNO")); //회원사 고객번호
						mappingInfoMap.put("USR_ID",      requestMap.get("NCRQ_ORG_ID")); //참가기관ID
						mappingInfoMap.put("RTCD",        requestMap.get("SCDL_RTCD"));	//스크래핑 응답코드
						mappingInfoMap.put("SCAF_ID",     requestMap.get("SCDL_SCAF_ID"));
						
						try {
							log.debug(String.format("[전문호출] 정보등록 Except.exceptResponse %s", except.getClass().getSimpleName()));						
							mappingInfoMap = except.exceptResponse(mappingInfoMap, requestMap, threadParams);
							log.debug(String.format("정보등록 요청 매핑 예외처리 결과  %s", mappingInfoMap));
						} catch (Exception e) {
							log.error(String.format("Exception handling about NICE response  is failed. %s %s", mappingInfoMap, requestMap), e);
							rtcd = KaisCode.E862; //NICE 요청 데이터 예외 사항 처리 오류
							throw new DefinedException("Request exceptable process error [" + SCDL_SEQ + "]." + e.getMessage());
						}
						mappingInfoMap.put("SCDL_SEQ",  SCDL_SEQ);
						mappingInfoMap.put("SCDL_CNT2", SCDL_CNT2);
						
						//자동차등등록원부에서 주민등록번호가 없으면 '#            '로 대체한다.
						/***********************************************************************************************************************************/
						String xmlNm = "req-" + bzcd;
						
						String structPath = StringUtil.replace(ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/WEB-INF/struct"), "\\", "/") + File.separator + xmlNm + ".xml";
						try {
							//자동차등록원부(갑,을,소유자검증) 발급, 건설기계원부 발급
							if("req-3A039".equals(xmlNm) || "req-3A040".equals(xmlNm)) {
								JXParser jxpReq 	= new JXParser(abrq.substring(10));
								if ("req-3A039".equals(xmlNm)) {
									//자동차 등록원부 일 경우 USR_KN값 할당 
									//mappingInfoMap.put("USR_KN", "");
									if(jxpReq != null && jxpReq.getElement("//INPUT/REGISELECT") != null) {
										String REGISELECT	= jxpReq.getAttribute(jxpReq.getElement("//INPUT/REGISELECT"), "VALUE");

										String USR_KN	= "1";
										if ("2".equals(REGISELECT)) {
											USR_KN	= "3";
										} else if("3".equals(REGISELECT)) {
											USR_KN	= "2";
										}
										mappingInfoMap.put("USR_KN", USR_KN);
									}
								}
								
								String ssnNm = "DC1_OWNO";
								String SSN = mappingInfoMap.get(ssnNm) == null ? null : (String) mappingInfoMap.get(ssnNm);
								
								if(SSN == null || SSN.trim().length() != 13){
									mappingInfoMap.put(ssnNm, String.format("%-13s", "#"));
									log.debug(String.format(">>> 자동차등록원부(갑,을,소유자검증)/건설기계원부 발급 주민등록번호 대체 실행. bzCd=%s, ssn=%s, structPath=%s ", bzcd, mappingInfoMap.get(ssnNm), structPath));
								}
							} else if ("req-3A056".equals(xmlNm)) {
								String gbNm 		= "BZ38_14_11";
								String BZ38_14_11 	= mappingInfoMap.get(gbNm) == null ? null : (String) mappingInfoMap.get(gbNm);
								
								log.debug("req-3A056 mappingInfoMap >>>> "+mappingInfoMap);
								log.debug("BZ38_14_11 >>>> "+mappingInfoMap.get(gbNm));
								
								if(BZ38_14_11.trim().length() == 13){
									//개인
									mappingInfoMap.put("USR_GB",    "1");
								} else if(BZ38_14_11.trim().length() == 10) {
									//사업자
									mappingInfoMap.put("USR_GB",    "2");
								}
							}
						} catch (Exception e) {
							log.error(String.format("주민등록번호 대체 실패. bzCd=%s, structPath=%s ", bzcd, xmlNm + ".xml"), e);
						}				
						/***********************************************************************************************************************************/
						
						// status 02
						mappingInfoMap.put("PRC_STS", NiceProcessStatus.PROCESS_02);
						mappingInfoMap.put("USYN",    "N"); //사용구분은 진행상태 01, 02 단계에서는  N
						try {
							requestBytes = except.makeNiceRequestBytes(bzcd, mappingInfoMap, encoding);
							lengthBytes = ByteUtil.addByte(String.valueOf(requestBytes.length - ("0".equals(getThreadParamsString("length-contain-self")) ? lengthSize : 0)).getBytes(),	ByteUtil.APPEND_CHARACTER_ZERO, lengthSize, true);
							System.arraycopy(lengthBytes, 0, requestBytes, lengthStartIndex, lengthSize);
						} catch (Exception e) {
							log.error(String.format("Creating NICE data is failed. %s %s", mappingInfoMap, requestMap), e);
							rtcd = KaisCode.E863; //NICE 요청 데이터 생성 오류
							throw new DefinedException("Fail to send request bytes [" + SCDL_SEQ + "]." + e.getMessage());
						}
						
						mappingInfoMap.put("SCDL_RTRQ", new String(requestBytes, encoding));
						
						try {						
							log.debug(String.format("[정보등록 요청] 진행상태 PRC_STS=02으로 정보등록요청에 대한 요청.bzcd=%s\n요청전문=%s", bzcd, new String(requestBytes, encoding)));
							responseBytes = request(requestBytes);
							log.debug(String.format("[정보등록 응답] 진행상태 PRC_STS=02으로 정보등록요청에 대한 응답.bzcd=%s\n응답전문=%s", bzcd, new String(responseBytes, encoding)));
						} catch (Exception e) {
							log.error(String.format("Receiving NICE response is failed. request size=%d, request=%s, request size=%d, request=%s", 
									(requestBytes  == null ? 0 : requestBytes.length),  new String(requestBytes,  encoding), 
									(responseBytes == null ? 0 : responseBytes.length), new String(responseBytes, encoding)), e);
							rtcd = KaisCode.E864; //NICE 요청 데이터 송신 오류
							throw new DefinedException(" [" + SCDL_SEQ + "]." + e.getMessage());
						}
						
						isSuccess = isProcessSuccess(bzcd, responseBytes, encoding);
						
						log.debug(String.format("isSuccess=%s", isSuccess));
						
						if(isSuccess){
							// status 03
							mappingInfoMap.put("PRC_STS", NiceProcessStatus.PROCESS_03);
							mappingInfoMap.put("USYN",    "Y"); //사용구분은 진행상태 03 단계에서는 Y
							try {
								log.debug(String.format("[전문호출] Except.makeNiceRequestBytes %s.", except.getClass().getSimpleName()));
								
								requestBytes = except.makeNiceRequestBytes(bzcd, mappingInfoMap, encoding);
								lengthBytes = ByteUtil.addByte(String.valueOf(requestBytes.length - ("0".equals(getThreadParamsString("length-contain-self")) ? lengthSize : 0)).getBytes(),
										ByteUtil.APPEND_CHARACTER_ZERO, 
										lengthSize, 
										true);
								System.arraycopy(lengthBytes, 0, requestBytes, lengthStartIndex, lengthSize);
							} catch (Exception e) {
								log.error(String.format("Creating NICE request data is failed."), e);
								rtcd = KaisCode.E863; //NICE 요청 데이터 생성 오류
								throw new DefinedException("Fail to send request bytes [" + SCDL_SEQ + "]." + e.getMessage());
							}
							
							mappingInfoMap.put("SCDL_RTRQ", new String(requestBytes, encoding));
							try {
								log.debug(String.format("[정보등록 요청] 진행상태 PRC_STS=03으로 정보등록요청에 대한 요청.bzcd=%s\n###%s###", bzcd, new String(requestBytes, encoding)));
								responseBytes = request(requestBytes);
								log.debug(String.format("[정보등록 응답] 진행상태 PRC_STS=03으로 정보등록요청에 대한 응답.\n###%s###", new String(responseBytes, encoding)));
							} catch (Exception e) {
								log.error(String.format("Sending NICE request data is failed."), e);
								rtcd = KaisCode.E864; //NICE 요청 데이터 송신 오류
								throw new DefinedException(" [" + SCDL_SEQ + "]." + e.getMessage());
							}
						}//if - 응답코드 확인
						
						// byte to map
						responseMap = except.mapNiceResponse(bzcd, responseBytes, encoding, Parser.RESPONSE);
						responseMap.put("SCDL_RTRS", new String(responseBytes, encoding));
						
					}
					
					/* ****** 로깅전문 전송 여부를 결정할 파라미터 설정 시작 **********************************************************************/
					logingReqParam = new HashMap<String, String>();
					logingResParam = new HashMap<String, String>();
					logingReqParam.put("SPECIALCODE",    String.valueOf(requestMap.get("SCDL_BZCD")));
					logingReqParam.put("MODULE",         String.valueOf(requestMap.get("SCDL_JBCD")));
					logingReqParam.put("CERTKEY",        getThreadParamsString("CERTKEY"));
					logingReqParam.put("DEPARTMENTCODE", getThreadParamsString("DEPARTMENTCODE"));
					logingReqParam.put("SCDL_SEQ",       SCDL_SEQ);
					logingReqParam.put("SCDL_CNT",       String.valueOf(requestMap.get("SCDL_CNT")));
					
					Map<String, Object> parsingReqInfoMap = new CommonParser().parse(NCRQ_BZCD + "-" + SCDL_G_BZCD
							                                                       , String.valueOf(requestMap.get("SCDL_OSTR")).getBytes(encoding)
							                                                       , encoding
							                                                       , Parser.REQUEST);
					logingReqParam.put("CST_PARAM", String.valueOf(parsingReqInfoMap.get("SCAF_ID")));
					Class<?> exceptClass2 = null;
					try {
						exceptClass2 = Class.forName("project.kais.init.client.except.Except_" + NCRQ_BZCD + "_" + SCDL_G_BZCD);
					} catch (ClassNotFoundException ce) {
						exceptClass2 = Class.forName("project.kais.init.client.except.CommonExcept");
					}
					except = (Except) exceptClass2.newInstance();
					Map<String, String> tmpMap = except.mapAibRequest( String.valueOf(requestMap.get("SCDL_BZCD")),  String.valueOf(requestMap.get("SCDL_JBCD")), parsingReqInfoMap);
					logingReqParam.putAll(tmpMap);

					logingResParam.put("SPECIALCODE", String.valueOf(requestMap.get("SCDL_BZCD")));
					logingResParam.put("MODULE",      String.valueOf(requestMap.get("SCDL_JBCD")));
					logingResParam.put("SCDL_SEQ",    SCDL_SEQ);
					logingResParam.put("SCDL_CNT",    String.valueOf(requestMap.get("SCDL_CNT")));
					logingResParam.put("NCRQ_BZCD",   String.valueOf(requestMap.get("NCRQ_BZCD")));					
					logingResParam.put("SCDL_ABRQ", abrq); ///AIB 요청 XML
					logingResParam.put("SCDL_ABRS", abrs); //AIB 응답 XML
					/* ****** 로깅전문 전송 여부를 결정할 파라미터 설정 끝 **********************************************************************/					
					
					try {
						/**
						 * NICE 정보등록 요청 및 결과 DB 저장
						 */
						ftpNeed = saveResponse(YYMM, mappingInfoMap, responseMap, logingReqParam, logingResParam);
					} catch (Exception e) {
						log.error(String.format("Sending NICE response data is failed."), e);
						rtcd = KaisCode.E865; //NICE 응답 데이터 저장 오류
						throw new DefinedException("Fail to save " + bzcd + " response [" + SCDL_SEQ + "]." + e.getMessage());
					}
					
					try {						
						//FTP 전송
						if(isSuccess && ftpNeed){
							String ftpEncoding = "UTF-8";
							uploadFTP(except, abrs, ftpEncoding, YYMM, SCDL_SEQ);
						}
					} catch (Exception e) {
						log.error(String.format("FTP UPLOAD FAILED."), e);
					}
					
				} catch (Exception e) {
					
					// aib 응답 실패 처리
					try {
						ftpNeed = saveResponse(rtcd, YYMM, mappingInfoMap, responseMap, logingReqParam, logingResParam, e);
					} catch (Exception ex) {
						log.error(String.format("Sending NICE response data and error is failed."), ex);
					}
					
					try {						
						//FTP 전송
						if(isSuccess && ftpNeed){
							String ftpEncoding = "UTF-8";
							uploadFTP(except, abrs, ftpEncoding, YYMM, SCDL_SEQ);
						}
					} catch (Exception e1) {
						log.error(String.format("FTP UPLOAD FAILED."), e1);
					}
					
					log.error(String.format("Processing NICE response data and error is failed. rtcd=%s, YYMM=%s, mappingInfoMap=%s, responseMap=%s", rtcd, YYMM, mappingInfoMap, responseMap), e);
				} finally {
					try {
						Thread.sleep(1);
					} catch (Exception e) {
					}
				}
			}
		} catch (Exception e) {
			log.error(String.format("AibClient is failed."), e);
		} finally {
			log.debug("AibClient terminated");
		}
	}

	/**
	 * 응답코드에 따른 진행상태=03으로 요청전문 전송 여부 결정
	 * @param bzcd
	 * @param responseBytes 진행상태=02로 요청전문을 보낸 결과로 받은 응답전문
	 * @param encoding 응답전문을 문자열로 변환하기 위한 인코딩.
	 * @return true : 진행상태=03으로 요청, false=요청안함.
	 */
	private boolean isProcessSuccess(String bzcd, byte[] responseBytes, String encoding) {
		
		String RTCD = null;
		String H_RTCD = null;
		String ER_RTCD = null;
		
		try {
			log.debug(String.format("rtcd-check-list=%s", getThreadParamsString("rtcd-check-list")));
			String[] checkList  = getThreadParamsString("rtcd-check-list").split(",");
			if (checkList.length < 1) {
				checkList	= getThreadParamsString("rtcd-check-list").split("\\,");
			}
			log.debug(String.format("checkList.length=%s", checkList.length));
			
			boolean ok = false;
			for(String check : checkList){
				log.debug(String.format("check=%s", check));
				log.debug(String.format("bzcd=%s", bzcd));
				if(bzcd.equals(check)){
					ok = true;
					break;
				}
			}
			
			if(!ok){
				return false;
			}
			Map<String, Object> resInfo = new CommonParser().parse(bzcd, responseBytes, encoding, Parser.RESPONSE);	
			
			RTCD 		= resInfo.get("RTCD") != null ? (String) resInfo.get("RTCD") : "";
			H_RTCD 		= resInfo.get("H_RTCD") != null ? (String) resInfo.get("H_RTCD") : "";
			ER_RTCD	 	= resInfo.get("ER_RTCD") != null ? (String) resInfo.get("ER_RTCD") : "";
			
			/*
			 * 3A039 응답전문에서 공통부의 응답코드와 등록전문부의 에러응답코드가 모두 정상인 경우에만 진행상태=03을 전송한다.
			 * 공통부-응답코드, 스크래핑 응답코드, 등록전문부-응답코드가 모두 'P000'일 때 03을 전송한다.
			 */
			boolean result = KaisCode.P000.equals(RTCD) && KaisCode.P000.equals(H_RTCD) && KaisCode.P000.equals(ER_RTCD);

			log.debug(String.format("진행상태=02 전송 후 결과 확인. bzcd=%s, 공통부 응답코드 H_RTCD =%s, 스크래핑 응답코드=%s, 등록전문부 응답코드 ER_RTCD=%s, result=%s", bzcd, H_RTCD, RTCD, ER_RTCD, result));
			log.debug(String.format("진행상태=02 전송 후 결과 확인. encoding=%s, 응답전문=%s", encoding, new String(responseBytes, encoding)));
			log.debug(String.format("진행상태=02 전송 후 결과 확인. 응답매핑=%s", resInfo));
			return result;

		} catch (UnsupportedEncodingException e) {
			log.error(String.format("Cannot find %s encoding. so result is false.", encoding));
			return false;
		} catch(Exception e){
			log.error(String.format("Response parsing error. bzcd=%s, %s", bzcd, e.getMessage()));
			return false;
		}
	}

	public static Object getBean(String name) {
		return ContextLoader.getCurrentWebApplicationContext().getBean(name);
	}

	/**
	 * 스레드 종료
	 */
	public void terminate() {
		stop = true;
	}

	/**
	 * NICE 정보등록 요청을 전송 및 수신한다.
	 * @param bytes 정보등록 요청
	 * @return 정보등록 요청 결과
	 * @throws Exception
	 */
	public byte[] request(byte[] bytes) throws Exception {
		int timeout  = (int) Calculator.calculate(getThreadParamsString("nice-timeout"));
		String ip    = getThreadParamsString("ip");
		int port     = getThreadParamsInt("port");
		int lenStIdx = getThreadParamsInt("length-start-index");
		int lenSz    = getThreadParamsInt("length-size");
		boolean isContainLength = "0".equals(getThreadParamsString("length-contain-self"));
		byte[] temp = String.valueOf(bytes.length - (isContainLength ? lenSz : 0)).getBytes();
		byte[] lengthBytes = ByteUtil.addByte(temp, ByteUtil.APPEND_CHARACTER_ZERO, lenSz, true);
		int lengthType = isContainLength ? JTcpManager._LENGTH_TYPE_BODYONLY : JTcpManager._LENGTH_TYPE_FULLSIZE;
		
		// set total length
		System.arraycopy(lengthBytes, 0, bytes, lenStIdx, lenSz);

		byte[] response = null; //JTcpManager.getInstance().sendMessageWithoutEOF(ip, port, timeout, null, bytes, lenSz,  lenStIdx, lenSz, lengthType, true);
		
		try {
			 response = JTcpManager.getInstance().sendMessageWithoutEOF(ip, port, timeout, null, bytes, lenSz,  lenStIdx, lenSz, lengthType, true);
		} catch (EOFException e) {
			log.error(String.format("정보등록 전송에러  EOFException"), e);
			return null;
		} catch(Exception e){
			log.error(String.format("정보등록 전송에러 Exception"), e);
			throw e;
		}
		return response;
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
	 * NICE 정보등록 수신 저장(에러 메시지 포함)
	 * @param rtcd
	 * @param YYMM
	 * @param reqMap
	 * @param resMap
	 * @param aibReqParam
	 * @param aibResParam
	 * @param e
	 * @throws Exception
	 */
	public boolean saveResponse(String rtcd, String YYMM, Map<String, Object> reqMap, Map<String, Object> resMap, Map<String, String> aibReqParam, Map<String, String> aibResParam, Exception e) throws Exception {
		if (KaisCode.P000.equals(rtcd)){
			rtcd = KaisCode.E859; //AIB 요청 중 정의되지 않은 오류
		}
		if (resMap == null){
			resMap = new HashMap<String, Object>();
		}
		resMap.put("SCDL_RTCD",  rtcd);
		resMap.put("SCDL_RTMSG", e.getMessage());
		return saveResponse(YYMM, reqMap, resMap, aibReqParam, aibResParam);
	}

	/**
	 * 정보등록 요청 결과 저장
	 * @param YYMM
	 * @param reqMap
	 * @param resMap
	 * @param logingReqParam
	 * @param logingResParam
	 * @throws Exception
	 */
	public boolean saveResponse(String YYMM, Map<String, Object> reqMap, Map<String, Object> resMap, Map<String, String> logingReqParam, Map<String, String> logingResParam) throws Exception {
		Map<String, String> param = new HashMap<String, String>();
		
		boolean ftpNeed = false;
	
		/*
		 * 전송 중 실패인 경우에만 재전송 3회를 시도하도록 수정함.
		 */
		String rtcd = resMap.get("H_RTCD") == null ? null : (String) resMap.get("H_RTCD");
		String bzcd	= (String) (resMap.get("G_BZCD") == null ? null : resMap.get("G_BZCD"));
		
		if(rtcd == null  && Integer.parseInt(String.valueOf(reqMap.get("SCDL_CNT2")==null?"0":String.valueOf(reqMap.get("SCDL_CNT2")))) < 2) {
			param.put("SCDL_STS", KaisState.INFO_TGT_R0);//NICE정보등록 대상			
		} else if(!"1S121".equals(reqMap.get("NCRQ_BZCD")) && (bzcd == null || "".equals(bzcd))) {	//공통부만 전송됐을 경우 처리
			resMap.put("H_RTCD", 		KaisCode.S506);
			param.put("SCDL_STS",		"1S111".equals(reqMap.get("NCRQ_BZCD")) ? KaisState.LOG_TGT_L0 : KaisState.INFO_RCV_R2);
			param.put("SCDL_FTPCD", 	KaisCode.S018);
			param.put("SCDL_FTPMSG",	"원본저장 스크래핑 실패");
		} else {
			logingResParam.put("PRC_STS", String.valueOf(reqMap.get("PRC_STS")));
			
			switch(checkNextProcess(logingResParam)){
			case END: 
				param.put("SCDL_STS", KaisState.INFO_RCV_R2);//NICE정보등록 요청 수신응답
				break;
				
			case ONLY_LOG:
				param.put("SCDL_STS", 		KaisState.LOG_TGT_L0);// do LOGGING
				param.put("SCDL_FTPCD", 	logingResParam.get("SCDL_FTPCD")==null?"":String.valueOf(logingResParam.get("SCDL_FTPCD")));	//오류코드 0018
				param.put("SCDL_FTPMSG", 	logingResParam.get("SCDL_FTPMSG")==null?"":String.valueOf(logingResParam.get("SCDL_FTPMSG")));
				break;
				
			case AF_LOG:
				param.put("SCDL_STS", KaisState.AIB_TGT_AF);
				break;	
				
			case FTP_LOG:
				param.put("SCDL_STS", KaisState.FTP_F0);
				ftpNeed = true;
				break;
			}
		}
		
		param.put("YYMM", YYMM);
		
		if (resMap.get("INFO_PASS")==null) {
			param.put("SCDL_NRTCD", resMap.get("H_RTCD") == null ? null :  String.valueOf(resMap.get("H_RTCD")));
		} else {
			param.put("SCDL_NRTCD", "");
		}
		
		String rtrq = "";
		String rtrs = "";
		if (reqMap.get("SCDL_RTRQ")!=null) {
			rtrq = AESCipher.encode(String.valueOf(reqMap.get("SCDL_RTRQ")), 
					AESCipher.DEFAULT_KEY,
					AESCipher.TYPE_256, 
					"UTF-8", 
					AESCipher.MODE_ECB_NOPADDING);
		}
		
		if (resMap.get("SCDL_RTRS")!=null) {
			rtrs = AESCipher.encode(String.valueOf(resMap.get("SCDL_RTRS")), 
	                AESCipher.DEFAULT_KEY,
	                AESCipher.TYPE_256, 
	                "UTF-8", 
	                AESCipher.MODE_ECB_NOPADDING);
		}
		param.put("SCDL_RTRQ", rtrq);
		param.put("SCDL_RTRS", rtrs);
		param.put("SCDL_NQTM", reqMap.get("H_ORG_RQTM") == null ? "" : String.valueOf(resMap.get("H_ORG_RQTM")));
		param.put("SCDL_NSTM", resMap.get("H_NCE_RQTM") == null ? "" : String.valueOf(resMap.get("H_NCE_RQTM")));
		param.put("SCDL_ER_CK_BT", resMap.get("ER_CK_BT") == null ? "" : String.valueOf(resMap.get("ER_CK_BT")));
		param.put("SCDL_ER_RTCD",  resMap.get("ER_RTCD")  == null ? "" : String.valueOf(resMap.get("ER_RTCD")));
		param.put("SCDL_SEQ", String.valueOf(reqMap.get("SCDL_SEQ")));
		param.put("SCDL_FTPCD", param.get("SCDL_FTPCD")==null?"":String.valueOf(param.get("SCDL_FTPCD")));
		param.put("SCDL_FTPMSG", param.get("SCDL_FTPMSG")==null?"":String.valueOf(param.get("SCDL_FTPMSG")));
		log.debug(String.format("[DB-SAVE_RESPONSE_NICE] 정보등록 결과 저장. SCDL_SEQ=%s, SCDL_STS=%s, SCDL_NRTCD=%s, SCDL_ER_CK_BT=%s, SCDL_ER_RTCD=%s", param.get("SCDL_SEQ"), param.get("SCDL_STS"), param.get("SCDL_NRTCD"), param.get("SCDL_ER_CK_BT"), param.get("SCDL_ER_RTCD")  ));
		scrapingService.SAVE_RESPONSE_NICE(param);
		
		return ftpNeed;
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
	

	private static final String FTP_SUCCESS = "S";
	private static final String FTP_FAILED = "F";
	
	/**
	 * FTP 파일 업로드 요청
	 * @param except
	 * @param aibResXml
	 * @param encoding
	 * @throws Exception
	 */
	private void uploadFTP(Except except, String aibResXml, String encoding, String YYMM, String SCDL_SEQ) throws Exception {
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
	 * 로깅 저장여부 판단
	 * @param aibReqParam AIB 요청 정보
	 * @param aibResParam AIB 응답 정보
	 * @return
	 */
	/* **********************************************************************************************************	
	public static boolean isLoggingTaskNotUse(Map<String, String> aibReqParam, Map<String, String> aibResParam) {
		
	
		if ((aibResParam.get("SCDL_ABRQ") == null || aibResParam.get("SCDL_ABRS") == null) && aibReqParam == null) {
			return false;
		}

		String bzcd = aibResParam.get("NCRQ_BZCD");
		String SPECIALCODE = aibResParam.get("SPECIALCODE");
		String MODULE = aibResParam.get("MODULE");
		String abrs = aibResParam.get("SCDL_ABRS"); //AIB 응답XML

		if (abrs == null || "".equals(abrs)) {
			abrs = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root/>";
		}
		log.debug(String.format("[로깅 저정여부 판단] bzcd=%s, SPECIALCODE=%s, MODULE=%s, abrs=%s", bzcd, SPECIALCODE, MODULE, abrs));
		
		String structPath = StringUtil.replace(ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/WEB-INF/struct"), "\\", "/") + File.separator + "logging-condition.xml";
		try {
			JXParser parser = new JXParser(new File(structPath));
			JXParser jxpRes = new JXParser(abrs);
			return false;
			//return RequestNiceLogger.isLoggingTaskCore(parser, bzcd, SPECIALCODE, MODULE, jxpRes, aibReqParam);
		} catch (Exception e) {
			log.error(String.format("Determining logging condition is failed. response=%s", abrs), e);
		}

		return false;
	}
	*********************************************************************************************************/
	
	private static final int FTP_LOG = 1;
	private static final int ONLY_LOG = 2;
	private static final int AF_LOG = 3;
	private static final int END = 0;
	
	/**
	 * AIB 응답전문에서 INQNORMALPE, , 를 
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
   	   
	   	try {
			 nodelist = document.getElementsByTagName("PRINTYN");
			 element = (Element) nodelist.item(0);
		   	 map.put(element.getNodeName() , element.getAttribute("VALUE"));
		} catch (Exception e) {
			map.put("PRINTYN", "");
		}
   	 	
	 	log.debug(String.format("[정보등록 후 작업 결정] map=%s , AIB 응답  xml=\n%s",map,  xml));
	 	return map;
	}
	
	/**
	 * 정보등록 후 작업 결정
	 * @param aibResParam
	 * @return
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public int checkNextProcess(Map<String, String> aibResParam) throws ParserConfigurationException, SAXException, IOException {
		String BZCD        = aibResParam.get("NCRQ_BZCD");
		String SPECIALCODE = aibResParam.get("SPECIALCODE");
		String MODULE      = aibResParam.get("MODULE");
		
		Map<String,String> parsingMap = null;
		String INQNORMALPE	= "";
		String PRINT		= "";
		String PRINTYN		= "";
		String TRANSSVRYN	= "";
		
		String xml 			= aibResParam.get("SCDL_ABRS"); //AIB 응답XML
		if (xml!=null && !"".equals(xml)) {
			parsingMap 	= parsingXml(xml);			
			INQNORMALPE	= (String) parsingMap.get("INQNORMALPE");
			PRINT		= (String) parsingMap.get("PRINT");
			PRINTYN		= (String) parsingMap.get("PRINTYN");
			TRANSSVRYN	= (String) parsingMap.get("TRANSLSAYN");
		}
		
		if("1S111".equals(BZCD) && "0009".equals(SPECIALCODE) && "50".equals(MODULE)){
			//자동차등록원부
			/*if("P".equals(INQNORMALPE) && "Y".equals(PRINT) && "Y".equals(TRANSSVRYN)){
				if (NiceProcessStatus.PROCESS_02.equals(aibResParam.get("PRC_STS"))) {
					//로깅전문 태울수 있도록 변경
					aibResParam.put("SCDL_FTPCD", 	KaisCode.S018);	//FTP를 송신하지 않음.
					aibResParam.put("SCDL_FTPMSG", 	"원본저장 스크래핑 실패");
					
					log.debug(String.format("[정보등록 후 작업 결정] 로깅등록만 요청. 파라미터 BZCD=%s, SPECIALCODE=%s, MODULE=%s, INQNORMALPE=%s, PRINT=%s, TRANSSVRYN=%s, PRC_STS=%s", BZCD, SPECIALCODE, MODULE, INQNORMALPE, PRINT, TRANSSVRYN, aibResParam.get("PRC_STS")));
					return ONLY_LOG;
				}
				log.debug(String.format("[정보등록 후 작업 결정] FTP 업로드 후 로깅등록 요청. 파라미터 BZCD=%s, SPECIALCODE=%s, MODULE=%s, INQNORMALPE=%s, PRINT=%s, TRANSSVRYN=%s", BZCD, SPECIALCODE, MODULE, INQNORMALPE, PRINT, TRANSSVRYN));
				return FTP_LOG;
			}*/
			if("P".equals(INQNORMALPE)){
				if (NiceProcessStatus.PROCESS_02.equals(aibResParam.get("PRC_STS"))) {
					//로깅전문 태울수 있도록 변경
					aibResParam.put("SCDL_FTPCD", 	KaisCode.S018);	//FTP를 송신하지 않음.
					aibResParam.put("SCDL_FTPMSG", 	"원본저장 스크래핑 실패");
					
					log.debug(String.format("[정보등록 후 작업 결정] 로깅등록만 요청. 파라미터 BZCD=%s, SPECIALCODE=%s, MODULE=%s, INQNORMALPE=%s, PRINT=%s, TRANSSVRYN=%s, PRC_STS=%s", BZCD, SPECIALCODE, MODULE, INQNORMALPE, PRINT, TRANSSVRYN, aibResParam.get("PRC_STS")));
					return ONLY_LOG;
				}
				return AF_LOG;
			}
			//로깅전문 태울수 있도록 변경
			aibResParam.put("SCDL_FTPCD", 	KaisCode.S018);	//FTP를 송신하지 않음.
			aibResParam.put("SCDL_FTPMSG", 	"원본저장 스크래핑 실패");
			
			log.debug(String.format("[정보등록 후 작업 결정] 로깅등록만 요청. 파라미터 BZCD=%s, SPECIALCODE=%s, MODULE=%s, INQNORMALPE=%s, PRINT=%s, TRANSSVRYN=%s", BZCD, SPECIALCODE, MODULE, INQNORMALPE, PRINT, TRANSSVRYN));
			return ONLY_LOG;
		}else if("1S111".equals(BZCD) && "0040".equals(SPECIALCODE) && "51".equals(MODULE)){
			//건설기계
			/*if("P".equals(INQNORMALPE) && "Y".equals(PRINT) && "Y".equals(TRANSSVRYN)){
				if (NiceProcessStatus.PROCESS_02.equals(aibResParam.get("PRC_STS"))) {
					//로깅전문 태울수 있도록 변경
					aibResParam.put("SCDL_FTPCD", 	KaisCode.S018);	//FTP를 송신하지 않음.
					aibResParam.put("SCDL_FTPMSG", 	"원본저장 스크래핑 실패");
					
					log.debug(String.format("[정보등록 후 작업 결정] 로깅등록만 요청. 파라미터 BZCD=%s, SPECIALCODE=%s, MODULE=%s, INQNORMALPE=%s, PRINT=%s, TRANSSVRYN=%s, PRC_STS=%s", BZCD, SPECIALCODE, MODULE, INQNORMALPE, PRINT, TRANSSVRYN, aibResParam.get("PRC_STS")));
					return ONLY_LOG;
				}
				log.debug(String.format("[정보등록 후 작업 결정] FTP 업로드 후 로깅등록 요청. 파라미터 BZCD=%s, SPECIALCODE=%s, MODULE=%s, INQNORMALPE=%s, PRINT=%s, TRANSSVRYN=%s", BZCD, SPECIALCODE, MODULE, INQNORMALPE, PRINT, TRANSSVRYN));
				//return FTP_LOG;
				return AF_LOG;
			}*/
			if("P".equals(INQNORMALPE)){
				if (NiceProcessStatus.PROCESS_02.equals(aibResParam.get("PRC_STS"))) {
					//로깅전문 태울수 있도록 변경
					aibResParam.put("SCDL_FTPCD", 	KaisCode.S018);	//FTP를 송신하지 않음.
					aibResParam.put("SCDL_FTPMSG", 	"원본저장 스크래핑 실패");
					
					log.debug(String.format("[정보등록 후 작업 결정] 로깅등록만 요청. 파라미터 BZCD=%s, SPECIALCODE=%s, MODULE=%s, INQNORMALPE=%s, PRINT=%s, TRANSSVRYN=%s, PRC_STS=%s", BZCD, SPECIALCODE, MODULE, INQNORMALPE, PRINT, TRANSSVRYN, aibResParam.get("PRC_STS")));
					return ONLY_LOG;
				}
				return AF_LOG;
			}
			//로깅전문 태울수 있도록 변경
			aibResParam.put("SCDL_FTPCD", 	KaisCode.S018);	//FTP를 송신하지 않음.
			aibResParam.put("SCDL_FTPMSG", 	"원본저장 스크래핑 실패");
			
			log.debug(String.format("[정보등록 후 작업 결정] 로깅등록만 요청. 파라미터 BZCD=%s, SPECIALCODE=%s, MODULE=%s, INQNORMALPE=%s, PRINT=%s, TRANSSVRYN=%s", BZCD, SPECIALCODE, MODULE, INQNORMALPE, PRINT, TRANSSVRYN));
			return ONLY_LOG;
		}else if("1S111".equals(BZCD) && "0078".equals(SPECIALCODE) && "71".equals(MODULE)){
			//홈택스
			if("P".equals(INQNORMALPE) && "3".equals(PRINTYN) && "Y".equals(TRANSSVRYN)){
				log.debug(String.format("[정보등록 후 작업 결정] FTP 업로드 후 로깅등록 요청. 파라미터 BZCD=%s, SPECIALCODE=%s, MODULE=%s, INQNORMALPE=%s, PRINT=%s, TRANSSVRYN=%s", BZCD, SPECIALCODE, MODULE, INQNORMALPE, PRINT, TRANSSVRYN));
				return FTP_LOG;
			}
			aibResParam.put("SCDL_FTPCD", 	KaisCode.S018);	//FTP를 송신하지 않음.
			aibResParam.put("SCDL_FTPMSG", 	"원본저장 스크래핑 실패");
			return ONLY_LOG;
		}else if("1S111".equals(BZCD) && "0016".equals(SPECIALCODE) && "9".equals(MODULE)){
			//국민연금
			if("P".equals(INQNORMALPE)  && "Y".equals(TRANSSVRYN)){
				log.debug(String.format("[정보등록 후 작업 결정] FTP 업로드 후 로깅등록 요청. 파라미터 BZCD=%s, SPECIALCODE=%s, MODULE=%s, INQNORMALPE=%s, PRINT=%s, TRANSSVRYN=%s", BZCD, SPECIALCODE, MODULE, INQNORMALPE, PRINT, TRANSSVRYN));
				return FTP_LOG;
			}
			aibResParam.put("SCDL_FTPCD", KaisCode.S018);	//FTP를 송신하지 않음.
			aibResParam.put("SCDL_FTPMSG", "원본저장 스크래핑 실패");
			return ONLY_LOG;
		}else if("1S111".equals(BZCD) && "0018".equals(SPECIALCODE) && "13".equals(MODULE)){
			//정부24
			if("P".equals(INQNORMALPE)  && "Y".equals(TRANSSVRYN)){
				log.debug(String.format("[정보등록 후 작업 결정] FTP 업로드 후 로깅등록 요청. 파라미터 BZCD=%s, SPECIALCODE=%s, MODULE=%s, INQNORMALPE=%s, PRINT=%s, TRANSSVRYN=%s", BZCD, SPECIALCODE, MODULE, INQNORMALPE, PRINT, TRANSSVRYN));
				return FTP_LOG;
			}
			aibResParam.put("SCDL_FTPCD", KaisCode.S018);	//FTP를 송신하지 않음.
			aibResParam.put("SCDL_FTPMSG", "원본저장 스크래핑 실패");
			return ONLY_LOG;
		}else if("1S121".equals(BZCD) && "0009".equals(SPECIALCODE) && "50".equals(MODULE)){
			//자동차등록원부
			if("P".equals(INQNORMALPE) && "Y".equals(PRINT) && "Y".equals(TRANSSVRYN)){
				log.debug(String.format("[정보등록 후 작업 결정] FTP 업로드 후 로깅등록 요청. 파라미터 BZCD=%s, SPECIALCODE=%s, MODULE=%s, INQNORMALPE=%s, PRINT=%s, TRANSSVRYN=%s", BZCD, SPECIALCODE, MODULE, INQNORMALPE, PRINT, TRANSSVRYN));
				return FTP_LOG;
			}
			//로깅전문 태울수 있도록 변경
			aibResParam.put("SCDL_FTPCD", 	KaisCode.S018);	//FTP를 송신하지 않음.
			aibResParam.put("SCDL_FTPMSG", 	"원본저장 스크래핑 실패");
			
			log.debug(String.format("[정보등록 후 작업 결정] 결정할 수 없음. 파라미터 BZCD=%s, SPECIALCODE=%s, MODULE=%s, INQNORMALPE=%s, PRINT=%s, TRANSSVRYN=%s, xml=\n%s", BZCD, SPECIALCODE, MODULE, INQNORMALPE, PRINT, TRANSSVRYN, xml));
			return ONLY_LOG;
		}else if("1S121".equals(BZCD) && "0040".equals(SPECIALCODE) && "51".equals(MODULE)){
			//건설기계
			if("P".equals(INQNORMALPE) && "Y".equals(PRINT) && "Y".equals(TRANSSVRYN)){
				if (NiceProcessStatus.PROCESS_02.equals(aibResParam.get("PRC_STS"))) {
					//로깅전문 태울수 있도록 변경
					aibResParam.put("SCDL_FTPCD", 	KaisCode.S018);	//FTP를 송신하지 않음.
					aibResParam.put("SCDL_FTPMSG", 	"원본저장 스크래핑 실패");
					
					log.debug(String.format("[정보등록 후 작업 결정] 로깅등록만 요청. 파라미터 BZCD=%s, SPECIALCODE=%s, MODULE=%s, INQNORMALPE=%s, PRINT=%s, TRANSSVRYN=%s, PRC_STS=%s", BZCD, SPECIALCODE, MODULE, INQNORMALPE, PRINT, TRANSSVRYN, aibResParam.get("PRC_STS")));
					return ONLY_LOG;
				}
				log.debug(String.format("[정보등록 후 작업 결정] FTP 업로드 후 로깅등록 요청. 파라미터 BZCD=%s, SPECIALCODE=%s, MODULE=%s, INQNORMALPE=%s, PRINT=%s, TRANSSVRYN=%s", BZCD, SPECIALCODE, MODULE, INQNORMALPE, PRINT, TRANSSVRYN));
				return FTP_LOG;
			}
			//로깅전문 태울수 있도록 변경
			aibResParam.put("SCDL_FTPCD", 	KaisCode.S018);	//FTP를 송신하지 않음.
			aibResParam.put("SCDL_FTPMSG", 	"원본저장 스크래핑 실패");
			
			log.debug(String.format("[정보등록 후 작업 결정] 로깅등록만 요청. 파라미터 BZCD=%s, SPECIALCODE=%s, MODULE=%s, INQNORMALPE=%s, PRINT=%s, TRANSSVRYN=%s", BZCD, SPECIALCODE, MODULE, INQNORMALPE, PRINT, TRANSSVRYN));
			return ONLY_LOG;
		}else if("1S121".equals(BZCD) && "0078".equals(SPECIALCODE) && "71".equals(MODULE)){
			//홈택스
			if("P".equals(INQNORMALPE) && "3".equals(PRINTYN) && "Y".equals(TRANSSVRYN)){
				log.debug(String.format("[정보등록 후 작업 결정] FTP 업로드 후 로깅등록 요청. 파라미터 BZCD=%s, SPECIALCODE=%s, MODULE=%s, INQNORMALPE=%s, PRINT=%s, TRANSSVRYN=%s", BZCD, SPECIALCODE, MODULE, INQNORMALPE, PRINT, TRANSSVRYN));
				return FTP_LOG;
			}
			aibResParam.put("SCDL_FTPCD", 	KaisCode.S018);	//FTP를 송신하지 않음.
			aibResParam.put("SCDL_FTPMSG", 	"원본저장 스크래핑 실패");
			return ONLY_LOG;
		}else if("1S121".equals(BZCD) && "0016".equals(SPECIALCODE) && "9".equals(MODULE)){
			//국민연금
			if("P".equals(INQNORMALPE)  && "Y".equals(TRANSSVRYN)){
				log.debug(String.format("[정보등록 후 작업 결정] FTP 업로드 후 로깅등록 요청. 파라미터 BZCD=%s, SPECIALCODE=%s, MODULE=%s, INQNORMALPE=%s, PRINT=%s, TRANSSVRYN=%s", BZCD, SPECIALCODE, MODULE, INQNORMALPE, PRINT, TRANSSVRYN));
				return FTP_LOG;
			}
			aibResParam.put("SCDL_FTPCD", KaisCode.S018);	//FTP를 송신하지 않음.
			aibResParam.put("SCDL_FTPMSG", "원본저장 스크래핑 실패");
			return ONLY_LOG;
		}else if("1S121".equals(BZCD) && "0018".equals(SPECIALCODE) && "13".equals(MODULE)){
			//정부24
			if("P".equals(INQNORMALPE)  && "Y".equals(TRANSSVRYN)){
				log.debug(String.format("[정보등록 후 작업 결정] FTP 업로드 후 로깅등록 요청. 파라미터 BZCD=%s, SPECIALCODE=%s, MODULE=%s, INQNORMALPE=%s, PRINT=%s, TRANSSVRYN=%s", BZCD, SPECIALCODE, MODULE, INQNORMALPE, PRINT, TRANSSVRYN));
				return FTP_LOG;
			}
			aibResParam.put("SCDL_FTPCD", KaisCode.S018);	//FTP를 송신하지 않음.
			aibResParam.put("SCDL_FTPMSG", "원본저장 스크래핑 실패");
			return ONLY_LOG;
		}else{
			log.debug(String.format("[정보등록 후 작업 결정] 정보등록으로 끝. 파라미터 BZCD=%s, SPECIALCODE=%s, MODULE=%s, INQNORMALPE=%s, PRINT=%s, TRANSSVRYN=%s", BZCD, SPECIALCODE, MODULE, INQNORMALPE, PRINT, TRANSSVRYN));
			return END;
		}
	}
}
