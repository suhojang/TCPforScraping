package project.kais.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kwic.service.DaoSupport;

/**
 * @Class Name : ScrapingDao.java
 * @Description : ScrapingDao Class
 * @Modification Information
 * @ @ 수정일 수정자 수정내용 @ --------- --------- ------------------------------- @
 *   2015.09.02 최초생성
 *
 * @author 기웅정보통신
 * @since 2015. 09.08
 * @version 1.0
 * @see
 *
 * 		Copyright (C) by kwic All right reserved.
 */

@Repository("ScrapingDao")
public class ScrapingDao extends DaoSupport {
	
	/**
	 * 해당월의 NICE 스크래핑 요청 테이블 생성여부 확인
	 */
	@SuppressWarnings("unchecked")
	public boolean CHECK_YYMM_NCRQ(Map<String, String> param) throws Exception {
		Map<String, Object> result = (Map<String, Object>) select("CHECK_YYMM_NCRQ", param);
		boolean rst = (result == null || result.get("TABLENAME") == null) ? false : true;
		return rst;
	}

	/**
	 * 해당월의 스크래핑 처리 상세 테이블 생성여부 확인
	 **/
	@SuppressWarnings("unchecked")
	public boolean CHECK_YYMM_SCDL(Map<String, String> param) throws Exception {
		Map<String, Object> result = (Map<String, Object>) select("CHECK_YYMM_SCDL", param);
		boolean rst = (result == null || result.get("TABLENAME") == null) ? false : true;
		return rst;
	}

	/**
	 * 해당월의 스크래핑 테이블 생성
	 * 
	 * @param param
	 */
	public void CREATE_YYMM_NCRQ_TBL(Map<String, String> param) {
		update("CREATE_YYMM_NCRQ_TBL", param);
		update("CREATE_YYMM_NCRQ_IDX1", param);
		update("CREATE_YYMM_NCRQ_IDX2", param);
		update("CREATE_YYMM_NCRQ_IDX3", param);
	}

	public void CREATE_YYMM_SCDL_TBL(Map<String, String> param) {
		update("CREATE_YYMM_SCDL_TBL", param);
		update("CREATE_YYMM_SCDL_IDX1", param);
		update("CREATE_YYMM_SCDL_IDX2", param);
		update("CREATE_YYMM_SCDL_IDX3", param);
		update("CREATE_YYMM_SCDL_IDX4", param);
	}

	/**
	 * AIB 스크래핑 요청 대상 목록 조회(현재월+이전월 동시처리)
	 * 
	 * @param param
	 *            스크래핑 요청 처리상태=A0 (AIB요청 대상)
	 * @return
	 */
	public List<Map<String, Object>> SELECT_REQUEST_AIB_LIST(Map<String, String> param) {
		return list("SELECT_REQUEST_AIB_LIST", param);
	}

	/**
	 * AIB 스크래핑 요청 대상 데이터 조회(현재월+이전월 2회 호출)
	 * 
	 * @param param
	 *            스크래핑 요청 처리상태=AA (AIB요청 대상 지정)
	 */
	public void UPDATE_REQUEST_AIB(Map<String, String> param) {
		update("UPDATE_REQUEST_AIB", param);
	}
	
	/**
	 * AIB 스크래핑 요청 대상 목록 조회(현재월+이전월 동시처리)
	 * 
	 * @param param
	 *            스크래핑 요청 처리상태=A0 (AIB요청 대상)
	 * @return
	 */
	public List<Map<String, Object>> SELECT_REQUEST_AIB_FTP_LIST(Map<String, String> param) {
		return list("SELECT_REQUEST_AIB_FTP_LIST", param);
	}

	/**
	 * AIB 스크래핑 요청 대상 데이터 조회(현재월+이전월 2회 호출)
	 * 
	 * @param param
	 *            스크래핑 요청 처리상태=AA (AIB요청 대상 지정)
	 */
	public void UPDATE_REQUEST_AIB_FTP(Map<String, String> param) {
		update("UPDATE_REQUEST_AIB_FTP", param);
	}

	/**
	 * NICE 정보등록 대상 조회
	 * 
	 * @param param
	 *            스크래핑 요청 처리상태=R0 (NICE정보등록 대상)
	 * @return
	 */
	public List<Map<String, Object>> SELECT_REQUEST_NICE_LIST(Map<String, String> param) {
		return list("SELECT_REQUEST_NICE_LIST", param);
	}

	/**
	 * NICE 정보등록 요청 대상 지정(현재월+이전월 2회 호출)
	 * 
	 * @param param
	 *            스크래핑 요청 처리상태=RR (NICE정보등록 대상 지정)
	 */
	public void UPDATE_REQUEST_NICE(Map<String, String> param) {
		update("UPDATE_REQUEST_NICE", param);
	}

	/**
	 * NICE 스크래핑 요청 key 생성
	 * 
	 * @param SEQUENCE
	 *            시퀀스 ID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> NEXTVAL(String SEQUENCE) {
		return (Map<String, Object>) select("NEXTVAL", SEQUENCE);
	}

	/**
	 * NICE요청 중 응답불가 오류건 저장
	 * 
	 * @param param
	 */
	public void INSERT_REQUEST_NCRQER(Map<String, String> param) {
		insert("INSERT_REQUEST_NCRQER", param);
	}

	/**
	 * NICE 스크래핑 요청 저장
	 * 
	 * @param param
	 */
	public void INSERT_REQUEST_NCRQ(Map<String, String> param) {
		insert("INSERT_REQUEST_NCRQ", param);
	}

	/**
	 * NICE 스크래핑 요청에 대한 결과 저장
	 * 
	 * @param param
	 */
	public void UPDATE_REQUEST_NCRQ(Map<String, String> param) {
		update("UPDATE_REQUEST_NCRQ", param);
	}

	/**
	 * NICE 스크래핑 처리상세 저장
	 * 
	 * @param param
	 */
	public void INSERT_REQUEST_SCDL(Map<String, String> param) {
		insert("INSERT_REQUEST_SCDL", param);
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
	public void SAVE_RESPONSE_AIB(Map<String, String> param) {
		update("SAVE_RESPONSE_AIB", param);
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
	public void SAVE_RESPONSE_NICE(Map<String, String> param) {
		update("SAVE_RESPONSE_NICE", param);
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
	public void SAVE_RESPONSE_AIB_FTP(Map<String, String> param) {
		update("SAVE_RESPONSE_AIB_FTP", param);
	}

	/**
	 * 처리시간 초과건 불능 처리 
	 * @param param
	 *             SCDL_STS	      처리상태=E0 (처리불가)
	 *             SCDL_RTCD   스크래핑 응답코드=E888
	 *             SCDL_RTMSG  스크래핑 처리메시지
	 */
	public void REMOVE_OLD_REQUEST(Map<String, String> param) {
		update("REMOVE_OLD_REQUEST", param);
	}

	/**
	 * 서비스 시작 전 스크래핑요청 처리상태 초기화  (AIB요청 대상(AA) to AIB요청 대상 지정(A0)
	 * @param param
	 */
	public void INIT_STATUS(Map<String, String> param) {
		update("INIT_STATUS", param);
	}

	/**
	 * NICE 로그등록 대상 조회 (스크래핑 요청 처리상태=L0)
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> SELECT_REQUEST_NICE_LOGGER_LIST(Map<String, String> param) {
		return list("SELECT_REQUEST_NICE_LOGGER_LIST", param);
	}

	/**
	 * NICE 로그등록 대상 지정 (스크래핑 요청 처리상태=LL)
	 * @param param
	 */
	public void UPDATE_REQUEST_NICE_LOGGER(Map<String, String> param) {
		update("UPDATE_REQUEST_NICE_LOGGER", param);
	}

	/**
	 * 로그 응답 저장
	 * @param param
	 */
	public void SAVE_RESPONSE_NICE_LOGGER(Map<String, String> param) {
		update("SAVE_RESPONSE_NICE_LOGGER", param);
	}
	
	/**
	 * FTP 업로드 결과 저장
	 * @param param
	 */
	public void SAVE_FTP_RESULT(Map<String, String> param){
		update("SAVE_FTP_RESULT", param);
		
	}
}
