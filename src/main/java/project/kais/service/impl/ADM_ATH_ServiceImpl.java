package project.kais.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import project.kais.service.ADM_ATH_Service;
import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;

/**
 * @Class Name : ADM_ATH_ServiceImpl.java
 * @Description : 권한관리
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
 *  Copyright (C) by kwic All right reserved.
 */

@Service("ADM_ATH_Service")
public class ADM_ATH_ServiceImpl extends EgovAbstractServiceImpl implements ADM_ATH_Service {

	@Resource(name = "ADM_ATH_Dao")
	private ADM_ATH_Dao dao;

	//권한조회
	public Map<String,Object> ADM_ATH_S1000A() throws Exception{
		return dao.ADM_ATH_S1000A();
	}

	//권한저장
	public void ADM_ATH_U1020A (Map<String,String> param) throws Exception{
		dao.ADM_ATH_U1020A(param);
	}
}
