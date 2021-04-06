package project.kais.service.impl;


import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kwic.service.DaoSupport;

/**
 * @Class Name : ADM_ATH_Dao.java
 * @Description : ADM_ATH_Dao Class
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
 *  Copyright (C) by kwic All right reserved.
 */

@Repository("ADM_ATH_Dao")
public class ADM_ATH_Dao extends DaoSupport {
	//권한조회
	@SuppressWarnings("unchecked")
	public Map<String,Object> ADM_ATH_S1000A() {
		return (Map<String,Object>)select("ADM_ATH_S1000A");
	}
	//권한저장
	public int ADM_ATH_U1020A(Map<String,String> param) {
		return update("ADM_ATH_U1020A",param);
	}

}
