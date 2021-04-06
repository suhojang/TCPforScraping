package project.kais.service;

import java.util.List;
import java.util.Map;

/**
 * @Class Name : ADM_PRS_Service.java
 * @Description : ADM_PRS_Service Class
 * @Modification Information
 * @ @ 수정일 수정자 수정내용 @ --------- --------- ------------------------------- @
 *   2015.09.02 최초생성
 *
 * @author 기웅정보통신
 * @since 2015. 09.02
 * @version 1.0
 * @see
 *
 * 		Copyright (C) by MOPAS All right reserved.
 */
public interface ADM_PRS_Service {

	/**
	 * 해당월의 NICE 스크래핑 요청 테이블 생성여부 확인
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public boolean CHECK_YYMM_NCRQ(Map<String, String> param) throws Exception;

	/**
	 * 처리현황 목록 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> ADM_PRS_S1000A(Map<String, String> param) throws Exception;

	/**
	 * 처리현황 목록 건수 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> ADM_PRS_S1000A_1(Map<String, String> param) throws Exception;
	
	/**
	 * 처리현황 목록 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> ADM_PRS_S1001A(Map<String, String> param) throws Exception;

	/**
	 * 처리현황 목록 건수 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> ADM_PRS_S1001A_1(Map<String, String> param) throws Exception;

	/**
	 * 처리현황 상세 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> ADM_PRS_V1000A(Map<String, String> param) throws Exception;

}
