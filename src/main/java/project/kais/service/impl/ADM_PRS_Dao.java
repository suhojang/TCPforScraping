package project.kais.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kwic.service.DaoSupport;

/**
 * @Class Name : ADM_PRS_Dao.java
 * @Description : ADM_PRS_Dao Class
 * @Modification Information
 * @ @ 수정일 수정자 수정내용  
 *   2015.09.02 최초생성
 *
 * @author 기웅정보통신
 * @since 2015. 09.08
 * @version 1.0
 * @see
 *
 * 		Copyright (C) by MOPAS All right reserved.
 */
@Repository("ADM_PRS_Dao")
public class ADM_PRS_Dao extends DaoSupport {
	
	/**
	 * 해당월의 NICE 스크래핑 요청 테이블 생성여부 확인
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> CHECK_YYMM_NCRQ(Map<String, String> param) {
		return (Map<String, Object>) select("CHECK_YYMM_NCRQ", param);
	}

	/**
	 * 처리현황 목록 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> ADM_PRS_S1000A(Map<String, String> param) {
		return list("ADM_PRS_S1000A", param);
	}

	/**
	 * 처리현황 목록 건수 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> ADM_PRS_S1000A_1(Map<String, String> param) {
		return (Map<String, Object>) select("ADM_PRS_S1000A_1", param);
	}
	
	/**
	 * 처리현황 목록 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> ADM_PRS_S1001A(Map<String, String> param) {
		return list("ADM_PRS_S1001A", param);
	}
	
	/**
	 * 처리현황 목록 건수 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> ADM_PRS_S1001A_1(Map<String, String> param) {
		return (Map<String, Object>) select("ADM_PRS_S1001A_1", param);
	}

	/**
	 * 처리현황 상세 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> ADM_PRS_V1000A(Map<String, String> param) {
		return (Map<String, Object>) select("ADM_PRS_V1000A", param);
	}

}
