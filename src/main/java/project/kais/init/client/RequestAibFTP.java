package project.kais.init.client;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import project.kais.init.KaisState;
import project.kais.init.SynchedTokens;
import project.kais.logger.Logger;
import project.kais.logger.LoggerFactory;
import project.kais.service.ScrapingService;

import com.kwic.web.init.InitServiceImpl;

/**
 * 1. POOL 개수 만큼의 THREAD 생성 
 * 2. DB에서 대상건 지정
 * 3. DB에서 대상건 조회
 */
public class RequestAibFTP extends InitServiceImpl {	

	private static Logger log = LoggerFactory.getLogger(RequestAibFTP.class);

	/** service */
	private ScrapingService service;

	private boolean stop;

	/**
	 * AIB 요청 대상 목록
	 */
	private static List<Map<String, Object>> requestList = new Vector<Map<String, Object>>();

	/**
	 * 요청처리를 위한 스레도풀이며 'pool-size' 속성값으로 그 크기가 정해진다.
	 * 참고 : init-service.xml
	 */
	private static List<AibFTPClientThread> threadtList = new Vector<AibFTPClientThread>();

	/**
	 * 서비스 실행
	 */
	@Override
	public void execute() throws Exception {
		service = (ScrapingService) getBean("ScrapingService");
		
		/*
		 * 1. 처리 전 상태 초기화
		 */
		Map<String, String> initParam = new HashMap<String, String>();
		initParam.put("SCDL_STS",    KaisState.AIB_TGT_AF); //처리상태 - AIB요청 대상
		initParam.put("BF_SCDL_STS", KaisState.AIB_ASN_AE); //처리상태 - AIB요청 대상 지정
		// 서비스 시작 시 FF건들을 F0로 변경
		service.INIT_STATUS(initParam);
		
		/*
		 * 2. 스레드풀 생성
		 * pool size 만큼 client thread 생성
		 */
		AibFTPClientThread thread = null;
		for (int i = 0; i < getServiceParamInt("pool-size"); i++) {
			thread = new AibFTPClientThread();
			thread.setThreadParams(super.getServiceParams());
			threadtList.add(thread);
			thread.start();
		}

		SimpleDateFormat sf = new SimpleDateFormat("yyMM");
		Map<String, String> param = new HashMap<String, String>();
		Calendar cal = null;

		param.put("SCDL_SVMK", getServiceParamString("process-mark")); //처리 서버 mark
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
				service.CREATE_YYMM_TBL(param);
				
				/*
				 * 5. 대상건 조회 후 표시
				 *    처리상태가 [AIB요청 대상]이고  스크래핑 요청횟수 3회 미만
				 */
				try {
					param.put("SCDL_STS", KaisState.AIB_TGT_AF);
					param.put("RETRY_CNT", getServiceParamString("RETRY_CNT"));
					targetList = service.SELECT_REQUEST_AIB_FTP_LIST(param);
					//log.debug(String.format("[DB-SELECT_REQUEST_AIB_FTP_LIST] AIB 목록 조회. param=%s, count=%d", param, targetList.size()));
				} catch (Exception e) {
					log.error(String.format("Selecting AIB scraping request list is failed. %s", param), e);
				}
				
				/*
				 * 6. 대상 목록의 처리상태를 [AIB요청 대상 지정]으로 변경하고 요청대상목록 requestList에 담는다.
				 */
				for (int i = 0; targetList != null && i < targetList.size(); i++) {
					param.put("SCDL_SEQ", String.valueOf(targetList.get(i).get("SCDL_SEQ"))); //AIB 거래키
					param.put("YYMM",     String.valueOf(targetList.get(i).get("YYMM")));
					// 대상건 상태를 진행중(AE)으로
					param.put("SCDL_STS", KaisState.AIB_ASN_AE);
					
					service.UPDATE_REQUEST_AIB_FTP(param);
					//log.debug(String.format("[DB-UPDATE_REQUEST_AIB_FTP] AIB FTP 전송전 상태 변경. SCDL_SEQ=%s, SCDL_STS=%s, SCDL_CNT=%s", param.get("SCDL_SEQ"), param.get("SCDL_STS"), param.get("SCDL_CNT") ));
					synchronized (SynchedTokens.TOKEN_REQUEST_AIB_POOL) {
						// 스크래핑 요청 대기열에 추가
						requestList.add(targetList.get(i));
					}
				}
			} catch (Exception e) {
				log.error("Processing AIB request is failed.", e);
			}

			// 지정 초만큼 휴식
			Thread.sleep(getServiceParamInt("sleep-time") * 1000);
		} //while
	}

	/**
	 * AIB 요청 스레드에게 요청목록정보를 제공한다.
	 * 잔존하는 첫번째 요청건 반환(반환건은 pool에서 제거됨)
	 * @return
	 */
	public static Map<String, Object> getRequest() {
		synchronized (SynchedTokens.TOKEN_REQUEST_AIB_POOL) {
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

}
