package project.kais.init.client;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import project.kais.init.KaisState;
import project.kais.init.SynchedTokens;
import project.kais.service.ScrapingService;

import com.kwic.web.init.InitServiceImpl;

/**
 * NICE에 정보등록 요청 발신자
 * @author ykkim
 *
 */
public class RequestNice extends InitServiceImpl {	

	private static Logger log = LoggerFactory.getLogger(RequestNice.class);

	/** service */
	private ScrapingService scrapingService;

	/**
	 * 서비스 중단여부
	 */
	private boolean stop;

	/**
	 * NICE 정보등록 요청 목록
	 */
	private static List<Map<String, Object>> requestList = new Vector<Map<String, Object>>();

	/**
	 * 스레드풀
	 */
	private static List<NiceClientThread> threadtList = new Vector<NiceClientThread>();

	/**
	 * 서비스 실행
	 */
	@Override
	public void execute() throws Exception {
		scrapingService = (ScrapingService) getBean("ScrapingService");

		/*
		 * 1. 처리전 상태 초기화
		 */
		Map<String, String> initParam = new HashMap<String, String>();
		initParam.put("SCDL_STS",    KaisState.INFO_TGT_R0); //NICE정보등록 대상		
		initParam.put("BF_SCDL_STS", KaisState.INFO_ASN_RR); //NICE정보등록 대상 지정
		// 서비스 시작 시 AA건들을 A0로 변경
		scrapingService.INIT_STATUS(initParam);

		/*
		 * 2. 스레드풀 생성
		 * pool size 만큼 client thread 생성
		 */
		NiceClientThread thread = null;
		for (int i = 0; i < getServiceParamInt("pool-size"); i++) {
			thread = new NiceClientThread();
			thread.setThreadParams(super.getServiceParams());
			threadtList.add(thread);
			thread.start();
		}

		SimpleDateFormat sf = new SimpleDateFormat("yyMM");
		Map<String, String> param = new HashMap<String, String>();
		Calendar cal = null;

		param.put("SCDL_SVMK", getServiceParamString("process-mark")); //처리서비 마크
		param.put("POOL_SIZE", getServiceParamString("pool-size"));   //

		List<Map<String, Object>> tgtList = null;
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
				 * 5. 대상건 조회 후 표시
				 */
				try {
					param.put("SCDL_STS",  KaisState.INFO_TGT_R0);
					param.put("RETRY_CNT", getServiceParamString("RETRY_CNT"));
					tgtList = scrapingService.SELECT_REQUEST_NICE_LIST(param);
					//log.debug(String.format("[DB-SELECT_REQUEST_NICE_LIST] NICE 정보등록 목록 조회. param=%s, count=%d", param, tgtList.size()));
				} catch (Exception e) {
					log.error(String.format("Selecting NICE request list is failed. %s", param), e);
				}
				
				/*
				 * 6. 대상 목록의 처리상태를 [NICE 정보등록 대상 지정]으로 변경하고 요청대상목록 requestList에 담는다.
				 */
				for (int i = 0; tgtList != null && i < tgtList.size(); i++) {
					param.put("SCDL_SEQ", String.valueOf(tgtList.get(i).get("SCDL_SEQ")));
					param.put("YYMM",     String.valueOf(tgtList.get(i).get("YYMM")));
					// 대상건 상태를 진행중(RR)으로
					param.put("SCDL_STS",  KaisState.INFO_ASN_RR);
					scrapingService.UPDATE_REQUEST_NICE(param);
					log.debug(String.format("[DB-UPDATE_REQUEST_NICE] 정보등록 전 상태변경. param=%s", param));
					synchronized (SynchedTokens.TOKEN_REQUEST_AIB_POOL) {
						// 스크래핑 요청 대기열에 추가
						requestList.add(tgtList.get(i));
					}
				}
			} catch (Exception e) {
				log.error("Processing NICE request is failed.", e);
			}
			// 지정 초만큼 휴식
			Thread.sleep(getServiceParamInt("sleep-time") * 1000);
		}
	}

	/**
	 * NICE 정보등록 요청 스레드에게 요청목록을 전달한다.
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

}
