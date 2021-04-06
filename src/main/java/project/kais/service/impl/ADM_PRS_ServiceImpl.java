package project.kais.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import project.kais.service.ADM_PRS_Service;
import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;

/**
 * @Class Name : ADM_PRS_ServiceImpl.java
 * @Description : 사용자 관리
 * @Modification Information
 * @ @ 수정일 수정자 수정내용 @ --------- --------- ------------------------------- @
 *   2016.11.07 최초생성
 *
 * @author 기웅정보통신
 * @since 2016. 11.07
 * @version 1.0
 * @see
 *
 * 		Copyright (C) by MOPAS All right reserved.
 */

@Service("ADM_PRS_Service")
public class ADM_PRS_ServiceImpl extends EgovAbstractServiceImpl implements ADM_PRS_Service {

	@Resource(name = "ADM_PRS_Dao")
	private ADM_PRS_Dao dao;

	/**
	 * 해당월의 NICE 스크래핑 요청 테이블 생성여부 확인
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean CHECK_YYMM_NCRQ(Map<String, String> param) throws Exception {
		Map<String, Object> info = (Map<String, Object>) dao.CHECK_YYMM_NCRQ(param);

		if (info == null || info.get("TABLENAME") == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 처리현황 목록 조회
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<Map<String, Object>> ADM_PRS_S1000A(Map<String, String> param) throws Exception {
		return dao.ADM_PRS_S1000A(param);
	}

	/**
	 * 처리현황 목록 건수 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map<String, Object> ADM_PRS_S1000A_1(Map<String, String> param) throws Exception {
		return dao.ADM_PRS_S1000A_1(param);
	}
	
	/**
	 * 처리현황 목록 조회
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<Map<String, Object>> ADM_PRS_S1001A(Map<String, String> param) throws Exception {
		return dao.ADM_PRS_S1001A(param);
	}
	
	/**
	 * 처리현황 목록 건수 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map<String, Object> ADM_PRS_S1001A_1(Map<String, String> param) throws Exception {
		return dao.ADM_PRS_S1001A_1(param);
	}

	/**
	 * 처리현황 상세 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map<String, Object> ADM_PRS_V1000A(Map<String, String> param) throws Exception {
		return dao.ADM_PRS_V1000A(param);
	}

}
