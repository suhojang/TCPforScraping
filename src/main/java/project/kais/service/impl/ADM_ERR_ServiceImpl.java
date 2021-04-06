package project.kais.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import project.kais.service.ADM_ERR_Service;
import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;

/**
 * @Class Name : ADM_ERR_ServiceImpl.java
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

@Service("ADM_ERR_Service")
public class ADM_ERR_ServiceImpl extends EgovAbstractServiceImpl implements ADM_ERR_Service {

	@Resource(name = "ADM_ERR_Dao")
	private ADM_ERR_Dao dao;

	//처리불가 목록 조회
	public List<Map<String,Object>> ADM_ERR_S1000A(Map<String,String> param) throws Exception{
		return dao.ADM_ERR_S1000A(param);
	}
	public Map<String,Object> ADM_ERR_S1000A_1(Map<String,String> param) throws Exception{
		return dao.ADM_ERR_S1000A_1(param);
	}
	public Map<String,Object> ADM_ERR_V1000A(Map<String,String> param) throws Exception{
		return dao.ADM_ERR_V1000A(param);
	}
	
}
