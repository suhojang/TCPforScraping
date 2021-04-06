package project.kais.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import project.kais.service.ADM_USR_Service;
import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;

/**
 * @Class Name : ADM_USR_ServiceImpl.java
 * @Description : 사용자 관리
 * @Modification Information
 * @
 * @  수정일      수정자              수정내용
 * @ ---------   ---------   -------------------------------
 * @ 2016.11.07           최초생성
 *
 * @author 기웅정보통신
 * @since 2016. 11.07
 * @version 1.0
 * @see
 *
 *  Copyright (C) by MOPAS All right reserved.
 */

@Service("ADM_USR_Service")
public class ADM_USR_ServiceImpl extends EgovAbstractServiceImpl implements ADM_USR_Service {

	/** MobileDAO */
	@Resource(name = "ADM_USR_Dao")
	private ADM_USR_Dao dao;

	//사용자목록 조회
	public List<Map<String,Object>> ADM_USR_S1000A() throws Exception{
		return dao.ADM_USR_S1000A();
	}
	//사용자 조회
	public Map<String,Object> ADM_USR_V1040A(String MGRINF_SEQ) throws Exception{
		return dao.ADM_USR_V1040A(MGRINF_SEQ);
	}
	//사용자 id 중복확인
	public Map<String,Object> ADM_USR_V1040A_1(String MGRINF_ID) throws Exception{
		return dao.ADM_USR_V1040A_1(MGRINF_ID);
	}
	//사용자 id 중복확인
	public Map<String,Object> ADM_USR_V1040A_2(Map<String,String> param) throws Exception{
		return dao.ADM_USR_V1040A_2(param);
	}
	//사용자 추가
	public void ADM_USR_I1010A(Map<String,String> param) throws Exception{
		dao.ADM_USR_I1010A(param);
	}
	//사용자 수정
	public void ADM_USR_U1020A(Map<String,String> param) throws Exception{
		dao.ADM_USR_U1020A(param);
	}
	//사용자 삭제
	public void ADM_USR_D1030A(String MGRINF_SEQ) throws Exception{
		dao.ADM_USR_D1030A(MGRINF_SEQ);
	}
	
}
