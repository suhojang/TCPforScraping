package project.kais.init.client;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.dom4j.Element;
import org.jaxen.JaxenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kwic.web.init.InitServiceImpl;
import com.kwic.xml.parser.JXParser;

import project.kais.init.KaisState;
import project.kais.init.SynchedTokens;
import project.kais.service.ScrapingService;

public class RequestNiceLogger extends InitServiceImpl {

	private static Logger log = LoggerFactory.getLogger(RequestNiceLogger.class);
	
	/** service */
	private ScrapingService scrapingService;

	private boolean stop;

	// nice response list
	private static List<Map<String, Object>> requestList = new Vector<Map<String, Object>>();

	private static List<NiceLoggerClientThread> threadtList = new Vector<NiceLoggerClientThread>();

	@Override
	public void execute() throws Exception {
		scrapingService = (ScrapingService) getBean("ScrapingService");
		
		/*
		 * 1. 처리 전 상태 초기화
		 */
		Map<String, String> initParam = new HashMap<String, String>();
		initParam.put("SCDL_STS",    KaisState.LOG_TGT_L0); //NICE로그등록 대상
		initParam.put("BF_SCDL_STS", KaisState.LOG_ASN_LL); //NICE로그등록 대상 지정
		// 서비스 시작 시 L0 -> LL
		scrapingService.INIT_STATUS(initParam);

		/*
		 * 2. 스레드풀 생성
		 * pool size 만큼 client thread 생성
		 */
		NiceLoggerClientThread thread = null;
		for (int i = 0; i < getServiceParamInt("pool-size"); i++) {
			thread = new NiceLoggerClientThread();
			thread.setThreadParams(super.getServiceParams()); //서비스의 설정정보를 스레드에게 부여한다.
			threadtList.add(thread); //스레드풀에 등록한다.
			thread.start();
		}

		SimpleDateFormat sf = new SimpleDateFormat("yyMM");
		Map<String, String> param = new HashMap<String, String>();
		Calendar cal = null;

		param.put("SCDL_SVMK", getServiceParamString("process-mark")); //처리서버 mark
		param.put("POOL_SIZE", getServiceParamString("pool-size")); 

		List<Map<String, Object>> targetList = null;
		/*
		 * 3. sleep-time으로 설정한 시간만큼 (10분) 스레드를 대기시켰다가 작업을 시작한다.
		 */
		while (!stop) {
			if (requestList.size() >= getServiceParamInt("pool-size")) {
				Thread.sleep(getServiceParamInt("sleep-time") * 1000);
				continue;
			}
			cal = Calendar.getInstance();
			param.put("YYMM", sf.format(cal.getTime()));
			cal.add(Calendar.MONTH, -1);
			param.put("BF_YYMM", sf.format(cal.getTime()));
			try {
				/*
				 * 4. 현재월 테이블 확인 및 생성(NICE 첫요청이 들어오기 전에 실행될 수 있으므로 체크해야함)
				 */
				scrapingService.CREATE_YYMM_TBL(param);
				
				/*
				 * 5. 대상목록 조회
				 */
				targetList = null;
				try {
					param.put("SCDL_STS",  KaisState.LOG_TGT_L0);
					param.put("RETRY_CNT", getServiceParamString("RETRY_CNT"));
					targetList = scrapingService.SELECT_REQUEST_NICE_LOGGER_LIST(param);
					//log.debug(String.format("[DB-SELECT_REQUEST_NICE_LOGGER_LIST] AIB 목록 조회. param=%s, count=%d", param, targetList.size()));
				} catch (Exception e) {
					log.error(String.format("Selecting logging list is failed. %s", param), e);
				}
				
				/*
				 * 6. 대상목록들의 처리상태를 LL(NICE로그등록 대상 지정)로 변경하고 요청 목록 requestList에 담는다.
				 */
				for (int i = 0; targetList != null && i < targetList.size(); i++) {
					param.put("SCDL_SEQ", String.valueOf(targetList.get(i).get("SCDL_SEQ")));
					param.put("YYMM",     String.valueOf(targetList.get(i).get("YYMM")));
					param.put("SCDL_STS",  KaisState.LOG_ASN_LL);
					scrapingService.UPDATE_REQUEST_NICE_LOGGER(param);
					log.debug(String.format("[DB-UPDATE_REQUEST_NICE_LOGGER] 로그등록 전 상태 변경. param=%s", param));
					synchronized (SynchedTokens.TOKEN_REQUEST_AIB_POOL) {
						// 스크래핑 요청 대기열에 추가
						requestList.add(targetList.get(i));
					}
				}
			} catch (Exception e) {
				log.error("Logging is failed.", e);
			}
			// 지정 초만큼 휴식
			Thread.sleep(getServiceParamInt("sleep-time") * 1000);
		}
	}

	/**
	 * 잔존하는 첫번째 요청건 반환(반환건은 pool에서 제거됨)
	 */
	public static Map<String, Object> getRequest() {
		synchronized (SynchedTokens.TOKEN_REQUEST_NICE_POOL) {
			if (requestList.size() == 0){
				return null;
			}
			Map<String, Object> request = requestList.get(0);
			requestList.remove(0);
			return request;
		}
	}

	/**
	 * 종료
	 */
	@Override
	public void terminate() throws Exception {
		for (int i = 0; i < threadtList.size(); i++) {
			threadtList.get(i).terminate();
		}
		stop = true;
	}

	/**
	 * 로깅대상여부 판단
	 * @param parser
	 * @param bzcd 거래구분코드
	 * @param specialCode 전문코드
	 * @param module
	 * @param responseParser
	 * @param aibReqParam
	 * @return
	 * @throws JaxenException
	 */
	/* **********************************************************************************************************
	public static boolean isLoggingTaskCore(JXParser parser, String bzcd, String specialCode, String module, JXParser responseParser, Map<String, String> aibReqParam) throws JaxenException {
		Element condition = parser.getElement(parser.getRootElement(), "//condition[@BZCD='" + bzcd + "' and @SPECIALCODE='" + specialCode + "' and @MODULE='" + module + "']");
		if (condition == null) {
			log.debug("//condition[@BZCD='" + bzcd + "' and @SPECIALCODE='" + specialCode + "' and @MODULE='" + module + "'] is None.");
			return false;
		}
		Element[] mapList      = parser.getElements(condition, "send-condition/map/element");
		log.debug(String.format("[로깅대상여부 판단] mapList.length=%d", mapList.length));
		log.debug(String.format("[로깅대상여부 판단] aibReqParam=%s", aibReqParam));

		for (Element element : mapList) {
			String name      = parser.getAttribute(element, "name");
			String valueType = parser.getAttribute(element, "valueType");
			String value     = parser.getAttribute(element, "value");
			log.debug(String.format("[로깅대상여부 판단] name=%s, valueType=%s, value=%s, aib=%s", name, valueType, value, aibReqParam.get(name)));
			if (!isMatchCondition(aibReqParam.get(name), valueType, value)) {
				log.debug(String.format("[로깅대상여부 판단] name=%s, valueType=%s, value=%s, aib=%s 불일치", name, valueType, value, aibReqParam.get(name)));
				return false;
			}
		}

		return true;
	}
	*********************************************************************************************************/
	
	/**
	 * 조건일치 여부 확인
	 * @param value
	 * @param valueType
	 * @param conditionValue
	 * @return
	 */
	public static boolean isMatchCondition(String value, String valueType, String conditionValue) {
		log.debug("isMatchCondition  " + value + " : " + valueType + ": " + conditionValue);
		if ("EXISTS".equals(valueType)) {
			if (!"".equals(value)) {
				return true;
			}
		}
		if ("EXACT".equals(valueType)) {
			if (conditionValue.equals(value)) {
				return true;
			}
		}
		if ("NOTEXACT".equals(valueType)) {
			if (!conditionValue.equals(value)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 전문분석 후 결과 반환
	 * @param parser
	 * @param specialCode
	 * @param module
	 * @param requestParser
	 * @param responseParser
	 * @return
	 * @throws JaxenException
	 */
	public static Map<String, String> getParams(JXParser parser, String specialCode, String module, JXParser requestParser, JXParser responseParser) throws JaxenException {
		Map<String, String> result = new HashMap<String, String>();

		Element condition   = parser.getElement(parser.getRootElement(), "//condition[@SPECIALCODE='" + specialCode + "' and @MODULE='" + module + "']");
		Element[] paramList = parser.getElements(condition, "params/param");

		for (Element element : paramList) {
			String name         = parser.getAttribute(element, "name");
			String type         = parser.getAttribute(element, "type");
			String defaultValue = parser.getAttribute(element, "default");
			if ("code".equals(type)) {
				String value = getExtractByCode(parser, requestParser, responseParser, element);
				if (value == null || "".equals(value)) {
					value = defaultValue;
				}
				log.debug("getParams byCode [" + name + "] = " + value);
				result.put(name, value);
			} else if ("extract".equals(type)) {
				String value = getExtractValue(parser, requestParser, responseParser, element);
				if (value == null) {
					value = defaultValue;
				}
				log.debug("getParams Value [" + name + "] = " + value);
				result.put(name, value);
			}

		}
		return result;
	}

	/**
	 * 값 추출
	 * @param parser
	 * @param requestParser
	 * @param responseParser
	 * @param element
	 * @return
	 * @throws JaxenException
	 */
	private static String getExtractValue(JXParser parser, JXParser requestParser, JXParser responseParser, Element element) throws JaxenException {
		Element[] requestList  = parser.getElements(element, "request/element");
		Element[] responseList = parser.getElements(element, "response/element");
		for (Element item : requestList) {
			String xpath = parser.getAttribute(item, "xpath1");
			return parser.getAttribute(requestParser.getElement(xpath), "VALUE");
		}
		for (Element item : responseList) {
			String xpath = parser.getAttribute(item, "xpath1");
			return parser.getAttribute(responseParser.getElement(xpath), "VALUE");
		}
		return "";
	}

	/**
	 * 코드 추출
	 * @param parser
	 * @param requestParser
	 * @param responseParser
	 * @param element
	 * @return
	 * @throws JaxenException
	 */
	private static String getExtractByCode(JXParser parser, JXParser requestParser, JXParser responseParser, Element element)	throws JaxenException {
		Element[] valueConditionList = parser.getElements(element, "code");

		for (Element valueCondition : valueConditionList) {
			String value = parser.getAttribute(valueCondition, "value");
			boolean isMatch = true;
			Element[] requestList  = parser.getElements(valueCondition, "request/element");
			Element[] responseList = parser.getElements(valueCondition, "response/element");
			for (Element item : requestList) {
				String xpath          = parser.getAttribute(item, "xpath1");
				String valueType      = parser.getAttribute(item, "valueType");
				String conditionValue = parser.getAttribute(item, "value");
				if (!isMatchCondition(parser.getAttribute(requestParser.getElement(xpath), "VALUE"), valueType, conditionValue)) {
					isMatch = false;
					break;
				}
			}
			for (Element item : responseList) {
				String xpath          = parser.getAttribute(item, "xpath1");
				String valueType      = parser.getAttribute(item, "valueType");
				String conditionValue = parser.getAttribute(item, "value");
				if (!isMatchCondition(parser.getAttribute(responseParser.getElement(xpath), "VALUE"), valueType, conditionValue)) {
					isMatch = false;
					break;
				}
			}
			if (isMatch) {
				return value;
			}
		}
		return "";
	}

}
