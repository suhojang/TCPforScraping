package project.kais.service.impl;


import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kwic.service.DaoSupport;

/**
 * @Class Name : ADM_ERR_Dao.java
 * @Description : ADM_ERR_Dao Class
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

@Repository("ADM_ERR_Dao")
public class ADM_ERR_Dao extends DaoSupport {
	//처리불가 목록조회
	public List<Map<String,Object>> ADM_ERR_S1000A(Map<String,String> param) {
		return list("ADM_ERR_S1000A",param);
	}
	@SuppressWarnings("unchecked")
	public Map<String,Object> ADM_ERR_S1000A_1(Map<String,String> param) {
		return (Map<String, Object>) select("ADM_ERR_S1000A_1",param);
	}
	@SuppressWarnings("unchecked")
	public Map<String,Object> ADM_ERR_V1000A(Map<String,String> param) {
		return (Map<String, Object>) select("ADM_ERR_V1000A",param);
	}
	
}
