package project.kais.service.impl;


import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kwic.service.DaoSupport;

/**
 * @Class Name : ADM_MNU_Dao.java
 * @Description : ADM_MNU_Dao Class
 * @Modification Information
 * @
 * @  수정일      수정자              수정내용
 * @ ---------   ---------   -------------------------------
 * @ 2015.09.02           최초생성
 *
 * @author 기웅정보통신
 * @since 2015. 09.08
 * @version 1.0
 * @see
 *
 *  Copyright (C) by MOPAS All right reserved.
 */

@Repository("ADM_MNU_Dao")
public class ADM_MNU_Dao extends DaoSupport {
	//메뉴조회
	@SuppressWarnings("unchecked")
	public Map<String,Object> ADM_MNU_S1000A() {
		return (Map<String,Object>)select("ADM_MNU_S1000A");
	}
	//메뉴저장
	public int ADM_MNU_U1020A(Map<String,String> param) {
		return update("ADM_MNU_U1020A",param);
	}

}
