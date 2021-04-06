package project.kais.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import project.kais.service.ADM_LGN_Service;
import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;

/**
 * @Class Name : ADM_LGN_ServiceImpl.java
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

@Service("ADM_LGN_Service")
public class ADM_LGN_ServiceImpl extends EgovAbstractServiceImpl implements ADM_LGN_Service {

	/** MobileDAO */
	@Resource(name = "ADM_LGN_Dao")
	private ADM_LGN_Dao dao;

	//사용자 정보 조회
	public Map<String,Object> ADM_LGN_01000A(Map<String,String> param) throws Exception{
		return dao.ADM_LGN_01000A(param);
	}

	//로그인 이력 저장
	public Object ADM_LGN_01000A_I1(Map<String,String> param) throws Exception{
		return dao.ADM_LGN_01000A_I1(param);
	}
	//모듈목록 조회
	public List<Map<String,Object>> ADM_LGN_02010A() throws Exception{
		return dao.ADM_LGN_02010A();
	}
	
}
