package project.kais.service;

import java.util.Map;

/**
 * @Class Name : ADM_MNU_Service.java
 * @Description : ADM_MNU_Service Class
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
 *  Copyright (C) by MOPAS All right reserved.
 */
public interface ADM_MNU_Service {
	
	//메뉴조회
	Map<String,Object> ADM_MNU_S1000A() throws Exception;

	//메뉴저장
	void ADM_MNU_U1020A(Map<String,String> param) throws Exception;

}
