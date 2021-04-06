package project.kais.service.impl;


import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kwic.service.DaoSupport;

/**
 * @Class Name : ADM_LGN_Dao.java
 * @Description : ADM_LGN_Dao Class
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

@Repository("ADM_LGN_Dao")
public class ADM_LGN_Dao extends DaoSupport {
	
	//사용자 정보 조회
	@SuppressWarnings("unchecked")
	public Map<String,Object> ADM_LGN_01000A(Map<String,String> param) {
		return (Map<String,Object>)select("ADM_LGN_01000A",param);
	}

	//로그인 이력 저장
	public Object ADM_LGN_01000A_I1(Map<String,String> param) {
		return insert("ADM_LGN_01000A_I1",param);
	}
	//모듈목록 조회
	public List<Map<String,Object>> ADM_LGN_02010A() {
		return list("ADM_LGN_02010A");
	}
}
