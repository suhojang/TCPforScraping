package project.kais.service.impl;


import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kwic.service.DaoSupport;

/**
 * @Class Name : PublicDao.java
 * @Description : PublicDao Class
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

@Repository("PublicDao")
public class PublicDao extends DaoSupport {
	//메뉴조회
	@SuppressWarnings("unchecked")
	public Map<String,Object> Public001() {
		return (Map<String,Object>)select("Public001");
	}
	//권한조회
	@SuppressWarnings("unchecked")
	public Map<String,Object> Public002() {
		return (Map<String,Object>)select("Public002");
	}
	//contents분류조회
	@SuppressWarnings("unchecked")
	public Map<String,Object> Public003() {
		return (Map<String,Object>)select("Public003");
	}

}
