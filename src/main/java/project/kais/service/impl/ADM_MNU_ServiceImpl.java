package project.kais.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import project.kais.service.ADM_MNU_Service;
import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;

/**
 * @Class Name : ADM_MNU_ServiceImpl.java
 * @Description : 스케쥴 로그
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

@Service("ADM_MNU_Service")
public class ADM_MNU_ServiceImpl extends EgovAbstractServiceImpl implements ADM_MNU_Service {

	@Resource(name = "ADM_MNU_Dao")
	private ADM_MNU_Dao dao;

	//메뉴조회
	public Map<String,Object> ADM_MNU_S1000A() throws Exception{
		return dao.ADM_MNU_S1000A();
	}

	//메뉴저장
	public void ADM_MNU_U1020A (Map<String,String> param) throws Exception{
		dao.ADM_MNU_U1020A(param);
	}
}
