package project.kais.service.impl;


import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kwic.service.DaoSupport;

/**
 * @Class Name : ADM_USR_Dao.java
 * @Description : ADM_USR_Dao Class
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

@Repository("ADM_USR_Dao")
public class ADM_USR_Dao extends DaoSupport {
	//사용자 목록조회
	public List<Map<String,Object>> ADM_USR_S1000A() {
		return list("ADM_USR_S1000A");
	}
	//사용자 조회
	@SuppressWarnings("unchecked")
	public Map<String,Object> ADM_USR_V1040A(String MGRINF_SEQ) {
		return (Map<String,Object>)select("ADM_USR_V1040A",MGRINF_SEQ);
	}
	//사용자 id 중복확인
	@SuppressWarnings("unchecked")
	public Map<String,Object> ADM_USR_V1040A_1(String MGRINF_ID) {
		return (Map<String,Object>)select("ADM_USR_V1040A_1",MGRINF_ID);
	}
	//사용자 id 중복확인
	@SuppressWarnings("unchecked")
	public Map<String,Object> ADM_USR_V1040A_2(Map<String,String> param) {
		return (Map<String,Object>)select("ADM_USR_V1040A_2",param);
	}
	//사용자 추가
	public void ADM_USR_I1010A(Map<String,String> param) {
		insert("ADM_USR_I1010A",param);
	}
	//사용자 수정
	public void ADM_USR_U1020A(Map<String,String> param) {
		update("ADM_USR_U1020A",param);
	}
	//사용자 삭제
	public void ADM_USR_D1030A(String MGRINF_SEQ) {
		update("ADM_USR_D1030A",MGRINF_SEQ);
	}

}
