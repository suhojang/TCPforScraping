package project.kais.init.client;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import project.kais.init.KaisState;
import project.kais.service.ScrapingService;

import com.kwic.math.Calculator;
import com.kwic.web.init.InitServiceImpl;

public class RemoveOldRequest extends InitServiceImpl {
	

	private static Logger log = LoggerFactory.getLogger(RemoveOldRequest.class);

	/** service */
	private ScrapingService service;
	private boolean stop;

	@Override
	public void execute() throws Exception {
		service = (ScrapingService) getBean("ScrapingService");

		String ST_TIME = null;
		Calendar cal = null;
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
		Map<String, String> param = new HashMap<String, String>();
		param.put("SCDL_RTMSG", "처리시간 초과(미처리 " + getServiceParamString("collapse-time") + "분 경과)"); //처리 메시지
		
		while (!stop) {
			try {
				cal = Calendar.getInstance();
				param.put("YYMM", sf.format(cal.getTime()).substring(2, 6));
				cal.add(Calendar.MONTH, -1);
				param.put("BF_YYMM", sf.format(cal.getTime()).substring(2, 6));

				cal = Calendar.getInstance();
				cal.add(Calendar.MINUTE, -1 * (int) Calculator.calculate(getServiceParamString("collapse-time")));
				ST_TIME = sf.format(cal.getTime());

				param.put("ST_TIME", ST_TIME);
				// 1.현재월 테이블 확인 및 생성
				service.CREATE_YYMM_TBL(param);

				param.put("SCDL_STS_E0", KaisState.CANNOT_PROC_E0);
				param.put("SCDL_STS_A0", KaisState.AIB_TGT_A0);
				param.put("SCDL_STS_AA", KaisState.AIB_ASN_AA);
				
				/*
				 * 2. 현재월의 미처리건은  처리결과 는 '처리시간 초과(미처리 6시간 경과)', 처리상태는  '처리불가'로 변경
				 */				
				service.REMOVE_OLD_REQUEST(param);
				log.debug(String.format("[DB-REMOVE_OLD_REQUEST] 처리불가 저장 param=%s", param));
				/*
				 * 3. 이전월의 미처리건은  처리결과 는 '처리시간 초과(미처리 6시간 경과)', 처리상태는  '처리불가'로 변경
				 */
				param.put("YYMM", param.get("BF_YYMM"));
				service.REMOVE_OLD_REQUEST(param);
				log.debug(String.format("[DB-REMOVE_OLD_REQUEST] 처리불가 저장 param=%s", param));
			} catch (Exception e) {
				log.error("Removing old data is failed.", e);
			}
			// 지정 초만큼 휴식
			Thread.sleep(getServiceParamInt("sleep-time") * 1000);
		}
	}

	/**
	 * 종료
	 */
	@Override
	public void terminate() throws Exception {
		stop = true;
	}
}
