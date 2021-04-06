package project.kais.service;

import java.util.List;
import java.util.Map;

/**
 * 스크래핑 요청 처리에 대한 DB의 CRUD 인터페이스
 * @author ykkim
 *
 */
public interface ScrapingService {

	/**
	 * 해당 연월별 스크래핑 요청 및 처리상세 테이블 생성
	 * 
	 * @param param
	 * @throws Exception
	 */
	public void CREATE_YYMM_TBL(Map<String, String> param) throws Exception;

	/**
	 * AIB 스크래핑 요청 대상 목록 조회(현재월+이전월 동시처리)
	 * 
	 * @param param
	 *            스크래핑 요청 처리상태=A0 (AIB요청 대상)
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> SELECT_REQUEST_AIB_LIST(Map<String, String> param) throws Exception;

	/**
	 * AIB 스크래핑 요청 대상 데이터 조회(현재월+이전월 2회 호출)
	 * 
	 * @param param
	 *            스크래핑 요청 처리상태=AA (AIB요청 대상 지정)
	 */
	public void UPDATE_REQUEST_AIB(Map<String, String> param) throws Exception;
	
	/**
	 * AIB 스크래핑 요청 대상 목록 조회(현재월+이전월 동시처리)
	 * 
	 * @param param
	 *            스크래핑 요청 처리상태=A0 (AIB요청 대상)
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> SELECT_REQUEST_AIB_FTP_LIST(Map<String, String> param) throws Exception;

	/**
	 * AIB 스크래핑 요청 대상 데이터 조회(현재월+이전월 2회 호출)
	 * 
	 * @param param
	 *            스크래핑 요청 처리상태=AA (AIB요청 대상 지정)
	 */
	public void UPDATE_REQUEST_AIB_FTP(Map<String, String> param) throws Exception;

	/**
	 * NICE 정보등록 대상 조회
	 * 
	 * @param param
	 *            스크래핑 요청 처리상태=R0 (NICE정보등록 대상)
	 * @return
	 */
	public List<Map<String, Object>> SELECT_REQUEST_NICE_LIST(Map<String, String> param) throws Exception;

	/**
	 * NICE 정보등록 요청 대상 지정(현재월+이전월 2회 호출)
	 * @param param
	 * 		스크래핑 요청 처리상태=RR (NICE정보등록 대상 지정)
	 */
	public void UPDATE_REQUEST_NICE(Map<String, String> param) throws Exception;

	/**
	 * NICE 스크래핑 요청 key 생성
	 * @param param 시퀀스 ID
	 * @return
	 * @throws Exception
	 */
	public long NEXTVAL(Map<String, String> param) throws Exception;

	/**
	 * NICE 스크래핑 요청 key 생성
	 * @param SEQUENCE 시퀀스 ID
	 * @return
	 * @throws Exception
	 */
	public long NEXTVAL(String SEQUENCE) throws Exception;

	/**
	 * NICE요청 중 응답불가 오류건 저장
	 * @param param
	 */
	public void INSERT_REQUEST_NCRQER(Map<String, String> param) throws Exception;

	/**
	 * NICE 스크래핑 요청 저장
	 * @param param
	 */
	public void INSERT_REQUEST_NCRQ(Map<String, String> param) throws Exception;

	/**
	 * NICE 스크래핑 요청에 대한 결과 저장
	 * @param param
	 */
	public void UPDATE_REQUEST_NCRQ(Map<String, String> param) throws Exception;

	/**
	 * NICE 스크래핑 처리상세 저장
	 * @param param
	 */
	public void INSERT_REQUEST_SCDL(Map<String, String> param) throws Exception;

	/**
	 * AIB 스크래핑 응답 결과 저장
	 * 
	 * @param param
	 * <br/>          SCDL_STS 처리상태 
	 * <br/>          SCDL_RTCD 스크래핑 응답코드 
	 * <br/>          SCDL_RTMSG 스크래핑 처리메시지
	 * <br/>          SCDL_NRTCD 정보등록 응답코드 
	 * <br/>          SCDL_ABRQ AIB 요청 XML 
	 * <br/>          SCDL_ABRS AIB 응답 XML
	 * <br/>          SCDL_AQTM AIB 요청 시각 
	 * <br/>          SCDL_ASTM AIB 응답 시각
	 */
	public void SAVE_RESPONSE_AIB(Map<String, String> param) throws Exception;

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
	public void SAVE_RESPONSE_NICE(Map<String, String> param) throws Exception;
	
	/**
	 * AIB 스크래핑 응답 결과 저장
	 * 
	 * @param param
	 * <br/>          SCDL_STS 처리상태 
	 * <br/>          SCDL_RTCD 스크래핑 응답코드 
	 * <br/>          SCDL_RTMSG 스크래핑 처리메시지
	 * <br/>          SCDL_NRTCD 정보등록 응답코드 
	 * <br/>          SCDL_ABRQ AIB 요청 XML 
	 * <br/>          SCDL_ABRS AIB 응답 XML
	 * <br/>          SCDL_AQTM AIB 요청 시각 
	 * <br/>          SCDL_ASTM AIB 응답 시각
	 */
	public void SAVE_RESPONSE_AIB_FTP(Map<String, String> param) throws Exception;

	/**
	 * 처리시간 초과건 불능 처리 
	 * @param param
	 *             SCDL_STS	      처리상태=E0 (처리불가)
	 *             SCDL_RTCD   스크래핑 응답코드=E888
	 *             SCDL_RTMSG  스크래핑 처리메시지
	 */
	public void REMOVE_OLD_REQUEST(Map<String, String> param) throws Exception;

	/**
	 * 서비스 시작 전 스크래핑요청 처리상태 초기화  (AIB요청 대상(AA) to AIB요청 대상 지정(A0)
	 * @param param
	 */
	public void INIT_STATUS(Map<String, String> param) throws Exception;

	/**
	 * NICE 로그등록 대상 조회 (스크래핑 요청 처리상태=L0)
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> SELECT_REQUEST_NICE_LOGGER_LIST(Map<String, String> param);

	/**
	 * NICE 로그등록 대상 지정 (스크래핑 요청 처리상태=LL)
	 * @param param
	 */
	public void UPDATE_REQUEST_NICE_LOGGER(Map<String, String> param);

	/**
	 * 로그 응답 저장
	 * @param param
	 */
	public void SAVE_RESPONSE_NICE_LOGGER(Map<String, String> param);
	
	/**
	 * FTP 업로드 결과 저장
	 * @param param
	 */
	public void SAVE_FTP_RESULT(Map<String, String> param);
	
}
