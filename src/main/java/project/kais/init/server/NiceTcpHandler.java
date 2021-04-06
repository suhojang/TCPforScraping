package project.kais.init.server;

import java.io.InputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.kwic.support.client.KwScrapClient;
import com.kwic.support.client.KwScrapResult;
import com.kwic.util.StringUtil;
import com.kwic.web.init.Handler;

public class NiceTcpHandler extends Thread implements Handler {

	private static Logger log = LoggerFactory.getLogger(NiceTcpHandler.class);

	/***
	 * 스크래핑 서비스
	 */
	private ScrapingService scrapingService;
	
	/**
	 * 결과코드
	 */
	private String resultCode = KaisCode.P000;// normal
	
	/**
	 * 발생연월
	 */
	private String YYMM;

	/**
	 * 설정정보
	 * NiceTcpListener에서 핸들러 클래스 생성할 때 service-params 라는 이름으로 값을 지정한다.
	 */
	private Map<String, Object> param = new HashMap<String, Object>();

	/**
	 * project.kais.init.server.NiceTcpListener.execute()에서 정의한 client-socket
	 */
	private Socket socket = null;
	
	/**
	 * 핸들러 식별자
	 * project.kais.init.server.NiceTcpListener.execute() 에서 정의한 client-id
	 */
	private String id = null;
	
	private int bufSize = 0;
	
	/**
	 * 요청 및 응답 해석 담당 클래스
	 */
	private Parser parser;
	
	/**
	 * 수신 정보의 바이트 배열
	 */
	private byte[] requestBytes;
	
	/**
	 * 서비스 설정값
	 * init-service.xml에서 가져온 설정값으로 
	 * NiceTcpListener 에서 service-params 라는 이름으로 가져온다.
	 */
	private Map<String, Object> initServiceParams;
	
	private KwScrapResult scrapResult = null;

	/**
	 * 설정값 지정
	 */
	@Override
	public void put(String name, Object obj) throws Exception {
		param.put(name, obj);
	}

	/**
	 * 핸들러 식별자와 연결 소켓을 할당하고 핸들러를 작동시킨다.
	 */
	@Override
	public void handle() throws Exception {
		/*
		 * project.kais.init.server.NiceTcpListener.execute() 에서 정의한 소켓 정보
		 */
		socket = (Socket) param.get("client-socket");
		id     = (String) param.get("client-id");
		this.start();
	}

	/**
	 * project.kais.init.server.NiceTcpListener.execute() 에서 정의한 서비스 설정정보 
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getServiceParam() {
		return (Map<String, Object>) param.get("service-params");
	}

	/**
	 * 스프링 컨텍스트의 저장소로부터  ScrapingService의 bean을 가져온다.
	 * @param name
	 * @return
	 */
	public static Object getBean(String name) {
		return ContextLoader.getCurrentWebApplicationContext().getBean(name);
	}
	
	/**
	 * 정수형 시스템 설정값 반환
	 * @param propertyName
	 * @return
	 * @throws PropertyNotFoundException
	 */
	private int getInitServiceParamsInt(String propertyName) throws PropertyNotFoundException{
		if(initServiceParams.get(propertyName) == null || "".equals(initServiceParams.get(propertyName))){
			throw new PropertyNotFoundException(propertyName);
		}
		return Integer.parseInt(String.valueOf(initServiceParams.get(propertyName)));
	}
	
	/**
	 * 문자형 시스템 설정값 반환
	 * @param propertyName
	 * @return
	 * @throws PropertyNotFoundException
	 */
	private String getInitServiceParamsString(String propertyName) throws PropertyNotFoundException{
		if(initServiceParams.get(propertyName) == null || "".equals(initServiceParams.get(propertyName))){
			throw new PropertyNotFoundException(propertyName);
		}
		return String.valueOf(initServiceParams.get(propertyName));
	}

	/**
	 * 1. 리스너의 연결객체풀에 등록한다. <br/>
	 * 2. 스크래핑 서비스 빈을 획득한다. <br/>
	 * 3. 클라이언트의 요청사항을 읽는다. <br/>
	 */
	@SuppressWarnings("unchecked")
	public void run() {
		
		/*
		 * 핸들러가 작동을 시작하면 리스너의 커넥션풀에 등록한다.
		 */
		NiceTcpListener.addClientSocket(id, socket);
		scrapingService = (ScrapingService) getBean("ScrapingService");

		InputStream inputStream = null;		
		
		/*
		 * parser로부터 해석된 정보
		 */
		Map<String, Object> scrapInfoMap = null;
		scrapResult	= null;
		
		try {
			/*
			 * 서비스의 설정값을 가져온다.
			 */
			initServiceParams = (Map<String, Object>) param.get("service-params");
			
			int lengthStartIndex  = getInitServiceParamsInt("length-start-index");
			int lengthSize        = getInitServiceParamsInt("length-size");
			int bufSize           = getInitServiceParamsInt("bufSize");
			int bzcdStartIndex    = getInitServiceParamsInt("bzcd-start-index");
			int bzcdSize          = getInitServiceParamsInt("bzcd-size"); //업무구분코드의 크기 - 파서 클래스를 찾기 위해 필요하다.
			boolean lengthContailSelf = "0".equals(getInitServiceParamsString("length-contain-self"));
			String parserPrefix       = getInitServiceParamsString("parser-prefix");
			String peerEncoding = getInitServiceParamsString("peer-encoding");
			
			int idx = 0;
			int realRequestSize = 0;
			byte[] lengthBytes = new byte[lengthStartIndex + lengthSize];
			byte[] bufBytes = new byte[bufSize];
			byte[] wholeBytes = null;
			int wholeSize = 0;
			byte[] bzcdBytes = new byte[bzcdSize];
			
			/*
			 * 요청을 읽어들여 버퍼에 저장한다.
			 * 전체 수신 정보는 lengthBytes에 저장되어 있다.
			 */
			inputStream = socket.getInputStream();
			while (idx < lengthBytes.length && !socket.isClosed()) {
				realRequestSize = inputStream.read(bufBytes, 0, lengthBytes.length - idx);
				if (realRequestSize < 0) {
					setResultCode(KaisCode.E801);//커넥션 종료
					throw new DefinedException("Connection closed by peer [" + realRequestSize + "]");
				}
				System.arraycopy(bufBytes, 0, lengthBytes, idx, realRequestSize);
				idx += realRequestSize;
			}
			
			/*
			 * 전문길이만큼 lengthBytes에 추가한다.
			 */
			try {
				wholeSize = Integer.parseInt(new String(lengthBytes));
				if (lengthContailSelf) {
					wholeSize += lengthSize;
				}
			} catch (Exception e) {
				log.error(String.format("Invalid length of special statement. whole size=%d, real size=%d", wholeSize, lengthBytes.length), e);
				setResultCode(KaisCode.E802);//전문길이 오류
				throw new DefinedException("Invalid length error. length-value = [" + new String(lengthBytes) + "]" + e.getMessage());
			}

			wholeBytes = new byte[wholeSize];
			// make whole bytes
			System.arraycopy(lengthBytes, 0, wholeBytes, 0, lengthBytes.length);
			while (idx < wholeSize && !socket.isClosed()) {
				realRequestSize = inputStream.read(bufBytes, 0, wholeSize - idx > 2048 ? 2048 : wholeSize - idx);
				if (realRequestSize < 0) {
					setResultCode(KaisCode.E801);//커넥션 종료
					throw new DefinedException("Connection closed by peer [" + realRequestSize + "]");
				}
				
				try {
					System.arraycopy(bufBytes, 0, wholeBytes, idx, realRequestSize);
				} catch (Exception e) {
					log.error("Invalid length of spaceil code,", e);
					requestBytes = wholeBytes;
					setResultCode(KaisCode.E802);//전문길이 오류
					throw new DefinedException("Invalid length error. length-value = [" + new String(lengthBytes) + "]" + e.getMessage());
				}
				idx += realRequestSize;
			}

			/*
			 * 업무구분코드 bzcd를 알아낸다.
			 */
			System.arraycopy(wholeBytes, bzcdStartIndex, bzcdBytes, 0, bzcdBytes.length);
			requestBytes = wholeBytes;
			log.debug(String.format("[KW] 최초 수신전문 request[encoding=%s] : %s<<<<<<<", getInitServiceParamsString("peer-encoding"), new String(wholeBytes, getInitServiceParamsString("peer-encoding"))));
			
			param.put("NCRQ_SEQ", getSerial(new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime()))); //NICE 스크래핑 요청 key

			/*
			 * 파서를 생성한다.
			 */
			parser = createParser(parserPrefix, bzcdBytes);	
			parser.setSysParam(initServiceParams);
			parser.setHandler(this);
			parser.setSerial(String.valueOf(param.get("NCRQ_SEQ")));
			parser.setBzcd(new String(bzcdBytes));
			parser.setEncoding(peerEncoding);
			parser.setRequest(wholeBytes);

			/*
			 * 요청 해석
			 */
			try {
				scrapInfoMap = parser.parseRequest();
			} catch (DefinedException de) {
				log.error("Request parsing error." , de);
				throw de;
			} catch (Exception e) {
				log.error("Request parsing error." , e);
				setResultCode(KaisCode.E804);//요청데이터 전문 해석 오류
				throw new DefinedException("Request parsing error." + e.getMessage());
			}

			/*
			 * 스크래핑 요청 DB 저장
			 */
			scrapInfoMap.put("H_ORG_TL_NO", String.valueOf(param.get("NCRQ_SEQ")).substring(7)); //NICE 스크래핑 요청 key

			try {
				saveRequest(scrapInfoMap);
			} catch (DefinedException de) {
				throw de;
			} catch (Exception e) {
				log.error(String.format("Parsing request is failed. parameters=%s", scrapInfoMap), e);
				setResultCode(KaisCode.E805);//요청데이터 저장 오류
				throw new DefinedException("Fail to save request data." + e.getMessage());
			}

			try {
				parser.execute();
			} catch (DefinedException de) {
				throw de;
			} catch (Exception e) {
				log.error(String.format("Parsing request and DB saving are failed. parameters=%s", scrapInfoMap), e);
				setResultCode(KaisCode.E806);//요청데이터 본문 해석 오류
				throw new DefinedException("Fail to save request data." + e.getMessage());
			}
			/*
			 * 스크래핑 요청 응답 저장
			 */
			response(scrapInfoMap);

		} catch (DefinedException de) {
			
			/*
			 * 운영 L4 스위치에서 헬스체크 신호를 보내서 E801를 로그를 불필요하게 남겨서 TBKW_NCRQER 테이블의 용량이 너무 크게 증가하여 
			 * E801에 대해서는 DB 저장은 안하고 디버깅 로그만 남기도록수정함.
			 */
			if(!KaisCode.E801.equals(getResultCode())){
				log.debug(String.format("Hander exception. so impossible mission is saved. result code=%s", getResultCode()), de);
				response(scrapInfoMap, de);
			}
			
		} catch (Exception e) {
			log.error("Undefined and unkown error.", e);
			setResultCode(KaisCode.E899);//요청전문 해석 중 정의되지 않은 오류
			response(scrapInfoMap, e);
		} finally {
			/*
			 * 연결 해제
			 */
			NiceTcpListener.removeClientSocket(id);
		}
	}

	/**
	 * parser 생성
	 * @param parserPrefix
	 * @param bzcdBytes
	 * @return
	 * @throws Exception
	 */
	private Parser createParser(String parserPrefix, byte[] bzcdBytes) throws Exception {
		String parserName = null;
		String bzcd = null;
		try {
			bzcd = new String(bzcdBytes);
			parserName = parserPrefix + bzcd;
			Class<?> parserClass = Class.forName(parserName);
			log.debug(String.format("Parser class is %s.", parserName));
			return (Parser) parserClass.newInstance();
		} catch (Exception e) {
			log.error(String.format("Parser class %s cannot found, because of invalid business code.(bzcd=%s)", parserName, bzcd), e);
			setResultCode(KaisCode.E803);//입력된 거래구분코드가 유효하지 않음
			throw new DefinedException("Undefined parser class[" + parserName + "]." + e.getMessage());
		}
	}

	/**
	 * 스크래핑 요청 전문 DB 저장
	 * @param requestMap
	 * @throws Exception
	 */
	public void saveRequest(Map<String, Object> requestMap) throws Exception {
		SimpleDateFormat sf = new SimpleDateFormat("yyMM");
		// 스크래핑 요청정보 DB저장
		Calendar cal = Calendar.getInstance();
		String H_NCE_RQTM = String.valueOf(requestMap.get("H_NCE_RQTM"));// NICE 전문  전송시각  yyyyMMddHHmmss
		Map<String, String> dbParam = new HashMap<String, String>();
		
		try {
			// NICE 전문 전송시각을 기준으로 테이블을 생성한다.
			cal.set(Calendar.YEAR,         Integer.parseInt(H_NCE_RQTM.substring(0, 4)));
			cal.set(Calendar.MONTH,        Integer.parseInt(H_NCE_RQTM.substring(4, 6)) - 1);
			cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(H_NCE_RQTM.substring(6, 8)));

			dbParam.put("YYMM", sf.format(cal.getTime()));
			YYMM = dbParam.get("YYMM");

			cal.add(Calendar.MONTH, -1);
			dbParam.put("BF_YYMM", sf.format(cal.getTime()));
		} catch (Exception e) {
			log.error("Calculating time information is failed.", e);
			setResultCode(KaisCode.E807);//스크래핑 요청전문 전송시간 오류
			throw new DefinedException("Invalid request time[" + H_NCE_RQTM + "]." + e.getMessage());
		}
		
		/*checkAIBConnection(requestMap);
		System.out.println("scrapResult >>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + scrapResult);
		if (scrapResult != null) {
			parser.getReqMap().put("H_RTCD", getResultCode());
			
			if(parser.getReqMap().get("INF_LST") != null) {
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> list	= (List<Map<String, Object>>) parser.getReqMap().get("INF_LST");
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).get("RTCD")==null || "".equals(String.valueOf(list.get(i).get("RTCD")))) {
						list.get(i).put("RTCD", getResultCode());
					}
				}
				parser.getReqMap().put("INF_LST", list);
			}
			
			try {
				// 1. 현재월 테이블 확인 및 생성
				scrapingService.CREATE_YYMM_TBL(dbParam);
			} catch (Exception e) {
				log.error(String.format("Creating toble of reuest duration is failed. %s", dbParam), e);
				setResultCode(KaisCode.E808);//스크래핑 로그 테이블(매월생성) 생성 오류
				throw new DefinedException("Fail to create scraping tables." + e.getMessage());
			}
			
			// 2. 요청내역 저장
			String scrq = AESCipher.encode(new String(requestBytes, getInitServiceParamsString("peer-encoding")),
					AESCipher.DEFAULT_KEY, 
					AESCipher.TYPE_256, 
					"UTF-8", 
					AESCipher.MODE_ECB_NOPADDING);
			
			dbParam.put("NCRQ_SEQ",     String.valueOf(param.get("NCRQ_SEQ")));         //NICE 스크래핑 요청 key
			dbParam.put("NCRQ_ORG_ID",  String.valueOf(requestMap.get("H_ORG_ID")));    //참가기관ID
			dbParam.put("NCRQ_BZCD",    String.valueOf(requestMap.get("H_BZ_CD")));     //거래구분코드
			dbParam.put("NCRQ_TL_NO",   String.valueOf(requestMap.get("H_NCE_TL_NO"))); //NICE 전문 관리번호
			dbParam.put("NCRQ_INF_CNT", String.valueOf(requestMap.get("RQ_CNT")));      //정보건수
			dbParam.put("NCRQ_STS",     KaisState.SCR_REQ_S1);                          // 처리상태 - NICE 스크래핑 요청
			dbParam.put("NCRQ_SCRQ",    scrq);                                          //스크래핑 요청
			dbParam.put("NCRQ_RQTM",    String.valueOf(requestMap.get("H_NCE_RQTM")));  //NICE 전문  전송시각
			
			try {
				log.debug(String.format("[DB-INSERT_REQUEST_NCRQ] 스크래핑  요청전문 저장. parameters=%s", dbParam));
				
				// TBKW_NCRQ_$YYMM$저장
				scrapingService.INSERT_REQUEST_NCRQ(dbParam);
			} catch (Exception e) {
				log.error(String.format("Saving scraping request is failed. %s", dbParam), e);
				setResultCode(KaisCode.E809);//스크래핑 요청정보 저장 오류
				throw new DefinedException("Fail to save scraping data." + e.getMessage());
			}
		}*/
		
		try {
			// 1. 현재월 테이블 확인 및 생성
			scrapingService.CREATE_YYMM_TBL(dbParam);
		} catch (Exception e) {
			log.error(String.format("Creating toble of reuest duration is failed. %s", dbParam), e);
			setResultCode(KaisCode.E808);//스크래핑 로그 테이블(매월생성) 생성 오류
			throw new DefinedException("Fail to create scraping tables." + e.getMessage());
		}
		
		// 2. 요청내역 저장
		String scrq = AESCipher.encode(new String(requestBytes, getInitServiceParamsString("peer-encoding")),
				AESCipher.DEFAULT_KEY, 
				AESCipher.TYPE_256, 
				"UTF-8", 
				AESCipher.MODE_ECB_NOPADDING);
		
		dbParam.put("NCRQ_SEQ",     String.valueOf(param.get("NCRQ_SEQ")));         //NICE 스크래핑 요청 key
		dbParam.put("NCRQ_ORG_ID",  String.valueOf(requestMap.get("H_ORG_ID")));    //참가기관ID
		dbParam.put("NCRQ_BZCD",    String.valueOf(requestMap.get("H_BZ_CD")));     //거래구분코드
		dbParam.put("NCRQ_TL_NO",   String.valueOf(requestMap.get("H_NCE_TL_NO"))); //NICE 전문 관리번호
		dbParam.put("NCRQ_INF_CNT", String.valueOf(requestMap.get("RQ_CNT")));      //정보건수
		dbParam.put("NCRQ_STS",     KaisState.SCR_REQ_S1);                          // 처리상태 - NICE 스크래핑 요청
		dbParam.put("NCRQ_SCRQ",    scrq);                                          //스크래핑 요청
		dbParam.put("NCRQ_RQTM",    String.valueOf(requestMap.get("H_NCE_RQTM")));  //NICE 전문  전송시각
		
		try {
			log.debug(String.format("[DB-INSERT_REQUEST_NCRQ] 스크래핑  요청전문 저장. parameters=%s", dbParam));
			
			// TBKW_NCRQ_$YYMM$저장
			scrapingService.INSERT_REQUEST_NCRQ(dbParam);
		} catch (Exception e) {
			log.error(String.format("Saving scraping request is failed. %s", dbParam), e);
			setResultCode(KaisCode.E809);//스크래핑 요청정보 저장 오류
			throw new DefinedException("Fail to save scraping data." + e.getMessage());
		}

	}

	/**
	 * 전문번호 채번
	 * @param yyMMdd
	 * @return
	 * @throws Exception
	 */
	public String getSerial(String yyMMdd) throws Exception {
		try {
			long seq = scrapingService.NEXTVAL("KW_NCRQ_SEQ");
			String NCRQ_SEQ = getInitServiceParamsString("instance-code") + yyMMdd + StringUtil.addChar(seq, 7);
			return NCRQ_SEQ;
		} catch (Exception e) {
			log.error(String.format("Creating sequence is failed. yyMMdd=%s", yyMMdd), e);
			setResultCode(KaisCode.E810);//스크래핑 전문코드 채번 오류
			throw new DefinedException("Fail to query sequence [KW_NCRQ_SEQ]." + e.getMessage());
		}
	}

	/**
	 * 응답불가 건 DB 저장
	 * @param info
	 * @param e
	 */
	public void response(Map<String, Object> info, Exception e) {
		try {
			Map<String, String> dbParam = new HashMap<String, String>();
			dbParam.put("NCRQER_RTCD", getResultCode());
			dbParam.put("NCRQER_RTMSG", e.getMessage());
			String scrq = AESCipher.encode(requestBytes == null ? "" : new String(requestBytes, getInitServiceParamsString("peer-encoding")),
					                       AESCipher.DEFAULT_KEY, 
					                       AESCipher.TYPE_256, 
					                       "UTF-8", 
					                       AESCipher.MODE_ECB_NOPADDING);
			dbParam.put("NCRQER_SCRQ", scrq);

			// 오류건 Db저장
			// TBKW_NCRQ_$YYMM$저장
			scrapingService.INSERT_REQUEST_NCRQER(dbParam);
		} catch (Exception ex) {
			log.error(String.format("Saving impossible response is failed. parameters=%s", info), e);
		}
		
		try {
			if (info == null){
				info = new HashMap<String, Object>();
			}
			info.put("NCRQ_RTMSG", e.getMessage());
			byte[] responseBytes = parser.makeResponse(e);
			
			info.put("NCRQ_SCRS", responseBytes);
			socket.getOutputStream().write(responseBytes);
			socket.getOutputStream().flush();
			saveResponse(info);
		} catch (Exception ex) {
			log.error(String.format("Saving result is failed. parameters=%s", info), ex);
		}
	}

	/**
	 * NICE 스크래핑 요청 수신응답 저장(update)
	 * @param scrapInfomap
	 */
	public void response(Map<String, Object> scrapInfomap) {
		try {
			byte[] responseBytes = parser.makeResponse();
			scrapInfomap.put("NCRQ_SCRS", responseBytes); //스크래핑 수신응답
			socket.getOutputStream().write(responseBytes);
			socket.getOutputStream().flush();
			saveResponse(scrapInfomap);
		
		} catch (Exception e) {
			log.error(String.format("Saving response is failed. parameters=%s", scrapInfomap), e);
		}
	}
	
	public void checkAIBConnection(Map<String, Object> scrapInfomap){
		String aibIp = null;
		int aibPort = 0;
		String aibCryptKey = null;
		int aibTimeout = 0;
		String encoding = null;
		
		Map<String, String> aibReqParam = null;
		Map<String, String> tmpMap = null;
		
		String bzCd 		= null;// 상품코드
		byte[] gArea 		= null;// 요청내역
		String[] kwBzCd		= null;
		
		String SCDL_SEQ		= null;
		String rtcd			= "";
		
		Except except = null;
		
		try {
			aibIp      	= getInitServiceParamsString("aib-ip");
			aibPort     = getInitServiceParamsInt("aib-port");
			aibCryptKey = getInitServiceParamsString("crypt-key");
			aibTimeout  = (int) Calculator.calculate(getInitServiceParamsString("aib-timeout"));
			encoding    = getInitServiceParamsString("data-encoding");	
			
			aibReqParam = new HashMap<String, String>();
			
			Map<String, Object> parsingInfoMap = null;
			Map<String, Object> parsingReqInfoMap = null;
			
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = (List<Map<String, Object>>) parser.getReqMap().get("INF_LST");
			
			bzCd			= (String) list.get(0).get("G_BZCD");
			gArea 			= (byte[]) list.get(0).get("G_AREA");
			
			SCDL_SEQ		= parser.getSerial() + StringUtil.addChar(1, 3);
			
			parsingInfoMap 	= parser.parse(parser.getBzcd() + "-" + bzCd, gArea, parser.getEncoding(), Parser.REQUEST);
			
			//원본 저장 처리
			/*if ("1S111".equals(parser.getBzcd())) {
				parsingInfoMap.put("ISSUE_TYPE", "1");
			}*/
			
			kwBzCd 			= parser.getKwBzCd(parser.getBzcd() + "-" + bzCd, parsingInfoMap);
			
			System.out.println("parsingInfoMap : "+parsingInfoMap);
			System.out.println("kwBzCd[0] : "+kwBzCd[0]);
			System.out.println("kwBzCd[1] : "+kwBzCd[1]);
			
			System.out.println("scrapInfomap : "+scrapInfomap);
			/*
			 * AIB 요청 파라미터
			 */
			aibReqParam.put("SPECIALCODE",    kwBzCd[0]);
			aibReqParam.put("MODULE",         kwBzCd[1]);
			aibReqParam.put("CERTKEY",        getInitServiceParamsString("CERTKEY"));
			aibReqParam.put("DEPARTMENTCODE", getInitServiceParamsString("DEPARTMENTCODE"));
			aibReqParam.put("TRANSKEY",       SCDL_SEQ);
			aibReqParam.put("SCDL_SEQ",       SCDL_SEQ);
			aibReqParam.put("SCDL_CNT",       "0");
			
			parsingReqInfoMap = new CommonParser().parse(scrapInfomap.get("H_BZ_CD") + "-" + bzCd, 
					gArea, 
                    encoding, 
                    Parser.REQUEST);
			
			aibReqParam.put("CST_PARAM", String.valueOf(parsingReqInfoMap.get("SCAF_ID")));
			
			String DC_FL_NM	= String.valueOf(parsingReqInfoMap.get("DC_FL_NM"));
			if (DC_FL_NM!=null && !"".equals(DC_FL_NM)) {
				DC_FL_NM	= StringUtil.replace(DC_FL_NM, "\\", "/");
				DC_FL_NM	= DC_FL_NM.substring(DC_FL_NM.lastIndexOf("/") + 1, DC_FL_NM.length());
				parsingReqInfoMap.put("DC_FL_NM", DC_FL_NM);
			}
			
			System.out.println("parsingReqInfoMap : "+parsingReqInfoMap);
			
			
			
			Class<?> exceptClass = null;
			try {
				exceptClass = Class.forName("project.kais.init.client.except.Except_" + scrapInfomap.get("H_BZ_CD") + "_" + bzCd);
			} catch (ClassNotFoundException ce) {
				exceptClass = Class.forName("project.kais.init.client.except.CommonExcept");
			}
			
			except = (Except) exceptClass.newInstance();
			
			try {
				except = (Except) exceptClass.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				rtcd = KaisCode.S502;
			}
			
			System.out.println("exceptClass : project.kais.init.client.except.Except_" + scrapInfomap.get("H_BZ_CD") + "_" + bzCd);
			
			tmpMap = except.mapAibRequest(kwBzCd[0], kwBzCd[1], parsingInfoMap);
			
			aibReqParam.putAll(tmpMap);
			aibReqParam.put("CAPTCHAOPTION", getInitServiceParamsString("CAPTCHAOPTION"));
			
			System.out.println("aibReqParam : "+aibReqParam);
			
			try {
				scrapResult = KwScrapClient.request(aibIp, 
						                           aibPort, 
						                           aibTimeout,
						                           KwScrapClient.CRYPT_TYPE_KWICAES256, 
						                           aibCryptKey, 
						                           aibReqParam,
						                           KwScrapClient.REQUEST_DATA_TYPE_HEADLENGTH_XML, 
						                           KwScrapClient.DEFAULT_CAPTCHA_TIMEOUT);
				
				log.debug(String.format("scrapResult=%s", scrapResult.getResponseMap()));
				
				String INQNORMALPE	= (String) scrapResult.getResponseMap().get("INQNORMALPE");
				String ERRMSG		= (String) scrapResult.getResponseMap().get("ERRMSG");
				//LSA서버연결 실패, SSA서버 비활성화
				if (("E".equals(INQNORMALPE) && ERRMSG.indexOf("서버에 연결할 수 없습니다.")>=0) || ("E".equals(INQNORMALPE) && ERRMSG.indexOf("스크랩핑 SSA 서버")>=0 && ERRMSG.indexOf("실행되어 있지 않습니다.")>=0)) {
					rtcd	= KaisCode.S502;
				} else {
					rtcd	= KaisCode.P000;
				}
				
				//rtcd	= KaisCode.P000;
			} catch (Exception e) {
				e.printStackTrace();
				log.error(String.format("Processing AIB data is failed. ip=%s, port=%d, timeout=%d, request=%s result=%s", aibIp, aibPort, aibTimeout, aibReqParam, scrapResult), e);
				rtcd = KaisCode.S502;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			rtcd	= KaisCode.S502;
		}
		
		setResultCode(rtcd);
	}
	
	/**
	 * NICE 스크래핑 요청 수신응답 저장(update)
	 * @param scrapInfomap
	 */
	public void saveResponse(Map<String, Object> scrapInfomap) {
		if (YYMM == null || scrapInfomap.get("NCRQ_SCRS") == null) {
			return;
		}
		Map<String, String> dbParam = new HashMap<String, String>();
		dbParam.put("NCRQ_SEQ",  String.valueOf(param.get("NCRQ_SEQ"))); //NICE 스크래핑 요청 key
		dbParam.put("YYMM",      YYMM);
		dbParam.put("NCRQ_STS",  KaisState.SCR_RES_S2);                           //처리상태 - NICE스크래핑요청 수신응답
		dbParam.put("NCRQ_RTCD", String.valueOf(scrapInfomap.get("H_RTCD")));     //스크래핑 응답코드
		dbParam.put("NCRQ_RSTM", String.valueOf(scrapInfomap.get("H_ORG_RQTM"))); //수신 응답 시각
		try {
			String scrs = AESCipher.encode(new String((byte[]) scrapInfomap.get("NCRQ_SCRS")), 
					                       AESCipher.DEFAULT_KEY,
					                       AESCipher.TYPE_256, 
					                       "UTF-8", 
					                       AESCipher.MODE_ECB_NOPADDING);
			dbParam.put("NCRQ_SCRS", scrs); //스크래핑 수신응답
			scrapingService.UPDATE_REQUEST_NCRQ(dbParam);
			log.debug(String.format("[DB-UPDATE_REQUEST_NCRQ] 스크래핑 결과 저장. NCRQ_SEQ=%s, NCRQ_STS=%s, NCRQ_RTCD=%s, NCRQ_RTMSG=%s", dbParam.get("NCRQ_SEQ"), dbParam.get("NCRQ_STS"), dbParam.get("NCRQ_RTCD"), dbParam.get("NCRQ_RTMSG")  ));
		} catch (Exception ex) {
			log.error(String.format("Saving result of response is failed. parameters=%s", scrapInfomap), ex);
		}
	}


	@Override
	public String toString() {
		return "NiceTcpHandler [service=" + scrapingService + ", resultCode=" + resultCode + ", YYMM=" + YYMM + ", param="
				+ param + ", socket=" + socket + ", id=" + id + ", bufSize=" + bufSize + ", parser=" + parser
				+ ", requestBytes=" + Arrays.toString(requestBytes) + ", p=" + initServiceParams + "]";
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultCode() {
		return resultCode;
	}
}
