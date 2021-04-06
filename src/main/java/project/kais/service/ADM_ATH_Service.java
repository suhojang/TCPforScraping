package project.kais.service;

import java.util.Map;

/**
 * @Class Name : ADM_ATH_Service.java
 * @Description : ADM_ATH_Service Class
 * @Modification Information
 * @
 * @  수정일      수정자              수정내용
 * @ ---------   ---------   -------------------------------
 * @ 2015.09.02           최초생성
 *
 * @author 기웅정보통신
 * @since 2015. 09.02
 * @version 1.0
 * @see
 *
 *  Copyright (C) by kwic All right reserved.
 */
public interface ADM_ATH_Service {
	
	//권한조회
	Map<String,Object> ADM_ATH_S1000A() throws Exception;

	//권한저장
	void ADM_ATH_U1020A(Map<String,String> param) throws Exception;

}
