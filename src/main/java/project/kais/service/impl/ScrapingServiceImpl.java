package project.kais.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import project.kais.init.SynchedTokens;
import project.kais.service.ScrapingService;

@Service("ScrapingService")
public class ScrapingServiceImpl implements ScrapingService {
	
	@Resource(name = "ScrapingDao")
	private ScrapingDao dao;

	/**
	 * 해당월의 스크래핑 테이블 생성
	 * @param param
	 */
	@Override
	public void CREATE_YYMM_TBL(Map<String, String> param) throws Exception {
		synchronized (SynchedTokens.TOKEN_CREATE_MONTHLY_TABLE) {
			String YYMM = param.get("YYMM");
			param.put("YYMM", param.get("BF_YYMM"));

			// 전월 스크래핑 요청 테이블 존재 확인 및 생성
			boolean rst = dao.CHECK_YYMM_NCRQ(param);
			if (!rst) {
				dao.CREATE_YYMM_NCRQ_TBL(param);
			}

			// 전월 스크래핑 요청 테이블 존재 확인 및 생성
			rst = dao.CHECK_YYMM_SCDL(param);
			if (!rst) {
				dao.CREATE_YYMM_SCDL_TBL(param);
			}

			param.put("YYMM", YYMM);
			// 현월 스크래핑 요청 테이블 존재 확인 및 생성
			rst = dao.CHECK_YYMM_NCRQ(param);
			if (!rst) {
				dao.CREATE_YYMM_NCRQ_TBL(param);
			}

			// 현월 스크래핑 요청 테이블 존재 확인 및 생성
			rst = dao.CHECK_YYMM_SCDL(param);
			if (!rst) {
				dao.CREATE_YYMM_SCDL_TBL(param);
			}
		}
	}

	/**
	 * AIB 스크래핑 요청 대상 목록 조회(현재월+이전월 동시처리)
	 * @param param
	 *            스크래핑 요청 처리상태=A0 (AIB요청 대상)
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<Map<String, Object>> SELECT_REQUEST_AIB_LIST(Map<String, String> param) throws Exception {
		return dao.SELECT_REQUEST_AIB_LIST(param);
	}

	/**
	 * AIB 스크래핑 요청 대상 데이터 조회(현재월+이전월 2회 호출)
	 * @param param
	 *            스크래핑 요청 처리상태=AA (AIB요청 대상 지정)
	 */
	@Override
	public void UPDATE_REQUEST_AIB(Map<String, String> param) throws Exception {
		dao.UPDATE_REQUEST_AIB(param);
	}
	
	/**
	 * AIB 스크래핑 요청 대상 목록 조회(현재월+이전월 동시처리)
	 * @param param
	 *            스크래핑 요청 처리상태=A0 (AIB요청 대상)
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<Map<String, Object>> SELECT_REQUEST_AIB_FTP_LIST(Map<String, String> param) throws Exception {
		return dao.SELECT_REQUEST_AIB_FTP_LIST(param);
	}

	/**
	 * AIB 스크래핑 요청 대상 데이터 조회(현재월+이전월 2회 호출)
	 * @param param
	 *            스크래핑 요청 처리상태=AA (AIB요청 대상 지정)
	 */
	@Override
	public void UPDATE_REQUEST_AIB_FTP(Map<String, String> param) throws Exception {
		dao.UPDATE_REQUEST_AIB_FTP(param);
	}

	/**
	 * NICE 정보등록 대상 조회
	 * @param param
	 *            스크래핑 요청 처리상태=R0 (NICE정보등록 대상)
	 * @return
	 */
	@Override
	public List<Map<String, Object>> SELECT_REQUEST_NICE_LIST(Map<String, String> param) throws Exception {
		return dao.SELECT_REQUEST_NICE_LIST(param);
	}

	/**
	 * NICE 정보등록 요청 대상 지정(현재월+이전월 2회 호출)
	 * @param param
	 * 		스크래핑 요청 처리상태=RR (NICE정보등록 대상 지정)
	 */
	@Override
	public void UPDATE_REQUEST_NICE(Map<String, String> param) throws Exception {
		dao.UPDATE_REQUEST_NICE(param);
	}

	/**
	 * NICE 스크래핑 요청 key 생성
	 * @param param 시퀀스 ID
	 * @return
	 * @throws Exception
	 */
	@Override
	public long NEXTVAL(Map<String, String> param) throws Exception {
		return NEXTVAL(param.get("SEQUENCE"));
	}

	/**
	 * NICE 스크래핑 요청 key 생성
	 * @param SEQUENCE 시퀀스 ID
	 * @return
	 * @throws Exception
	 */
	@Override
	public long NEXTVAL(String SEQUENCE) throws Exception {
		Map<String, Object> info = dao.NEXTVAL(SEQUENCE);
		long seq = Long.parseLong(String.valueOf(info.get("SEQ")));
		return seq;
	}

	/**
	 * NICE요청 중 응답불가 오류건 저장
	 * @param param
	 */
	@Override
	public void INSERT_REQUEST_NCRQER(Map<String, String> param) throws Exception {
		dao.INSERT_REQUEST_NCRQER(param);
	}

	/**
	 * NICE 스크래핑 요청 저장
	 * @param param
	 */
	@Override
	public void INSERT_REQUEST_NCRQ(Map<String, String> param) throws Exception {
		dao.INSERT_REQUEST_NCRQ(param);
	}

	/**
	 * NICE 스크래핑 요청에 대한 결과 저장
	 * @param param
	 */
	@Override
	public void UPDATE_REQUEST_NCRQ(Map<String, String> param) throws Exception {
		dao.UPDATE_REQUEST_NCRQ(param);
	}

	/**
	 * NICE 스크래핑 처리상세 저장
	 * @param param
	 */
	@Override
	public void INSERT_REQUEST_SCDL(Map<String, String> param) throws Exception {
		dao.INSERT_REQUEST_SCDL(param);
	}

	/**
	 * NICE 정보등록 응답 결과 저장
	 * @param param
	 *             SCDL_STS	          처리상태 <br/>
	 *             SCDL_RTCD	  스크래핑 응답코드 <br/>
	 *             SCDL_RTRQ	  정보등록 요청 <br/>
	 *             SCDL_RTRS	  정보등록 수신응답 <br/>
	 *             SCDL_NQTM 	  정보등록 요청 시각 <br/>
	 *             SCDL_NSTM	  정보등록 요청 수신응답 시각 <br/>
	 *             SCDL_ER_CK_BT 에러체크비트   <br/>
	 *             SCDL_ER_RTCD	  오류응답코드 <br/>
	 */
	@Override
	public void SAVE_RESPONSE_AIB(Map<String, String> param) throws Exception {
		dao.SAVE_RESPONSE_AIB(param);
	}

	/**
	 * NICE 정보등록 응답 결과 저장
	 * @param param
	 *             SCDL_STS	          처리상태
	 *             SCDL_RTCD	  스크래핑 응답코드
	 *             SCDL_RTRQ	  정보등록 요청
	 *             SCDL_RTRS	  정보등록 수신응답
	 *             SCDL_NQTM 	  정보등록 요청 시각
	 *             SCDL_NSTM	  정보등록 요청 수신응답 시각
	 *             SCDL_ER_CK_BT 에러체크비트  
	 *             SCDL_ER_RTCD	  오류응답코드
	 */
	@Override
	public void SAVE_RESPONSE_NICE(Map<String, String> param) throws Exception {
		dao.SAVE_RESPONSE_NICE(param);
	}
	
	/**
	 * NICE 정보등록 응답 결과 저장
	 * @param param
	 *             SCDL_STS	          처리상태 <br/>
	 *             SCDL_RTCD	  스크래핑 응답코드 <br/>
	 *             SCDL_RTRQ	  정보등록 요청 <br/>
	 *             SCDL_RTRS	  정보등록 수신응답 <br/>
	 *             SCDL_NQTM 	  정보등록 요청 시각 <br/>
	 *             SCDL_NSTM	  정보등록 요청 수신응답 시각 <br/>
	 *             SCDL_ER_CK_BT 에러체크비트   <br/>
	 *             SCDL_ER_RTCD	  오류응답코드 <br/>
	 */
	@Override
	public void SAVE_RESPONSE_AIB_FTP(Map<String, String> param) throws Exception {
		dao.SAVE_RESPONSE_AIB_FTP(param);
	}

	/**
	 * 처리시간 초과건 불능 처리 
	 * @param param
	 *             SCDL_STS	      처리상태=E0 (처리불가)
	 *             SCDL_RTCD   스크래핑 응답코드=E888
	 *             SCDL_RTMSG  스크래핑 처리메시지
	 */
	@Override
	public void REMOVE_OLD_REQUEST(Map<String, String> param) throws Exception {
		dao.REMOVE_OLD_REQUEST(param);
	}

	/**
	 * 서비스 시작 전 스크래핑요청 처리상태 초기화  (AIB요청 대상(AA) to AIB요청 대상 지정(A0)
	 * @param param
	 */
	@Override
	public void INIT_STATUS(Map<String, String> param) throws Exception {
		SimpleDateFormat sf = new SimpleDateFormat("yyMM");
		Calendar cal = Calendar.getInstance();
		param.put("YYMM", sf.format(cal.getTime()));
		try {
			dao.INIT_STATUS(param);
		} catch (Exception e) {
		}
		cal.add(Calendar.MONTH, -1);
		param.put("YYMM", sf.format(cal.getTime()));
		try {
			dao.INIT_STATUS(param);
		} catch (Exception e) {
		}
	}

	/**
	 * NICE 로그등록 대상 조회 (스크래핑 요청 처리상태=L0)
	 * @param param
	 * @return
	 */
	@Override
	public List<Map<String, Object>> SELECT_REQUEST_NICE_LOGGER_LIST(Map<String, String> param) {
		return dao.SELECT_REQUEST_NICE_LOGGER_LIST(param);
	}

	/**
	 * NICE 로그등록 대상 지정 (스크래핑 요청 처리상태=LL)
	 * @param param
	 */
	@Override
	public void UPDATE_REQUEST_NICE_LOGGER(Map<String, String> param) {
		dao.UPDATE_REQUEST_NICE_LOGGER(param);
	}

	/**
	 * 로그 응답 저장
	 * @param param
	 */
	@Override
	public void SAVE_RESPONSE_NICE_LOGGER(Map<String, String> param) {
		dao.SAVE_RESPONSE_NICE_LOGGER(param);
	}
	/**
	 * FTP 업로드 결과 저장
	 * @param param
	 */
	@Override
	public void SAVE_FTP_RESULT(Map<String, String> param){
		dao.SAVE_FTP_RESULT(param);
		
	}
}
