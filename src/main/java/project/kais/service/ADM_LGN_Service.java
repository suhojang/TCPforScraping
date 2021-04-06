package project.kais.service;

import java.util.List;
import java.util.Map;

/**
 * @Class Name : ADM_LGN_Service.java
 * @Description : ADM_LGN_Service Class
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
public interface ADM_LGN_Service {
	
	//사용자정보 조회
	Map<String,Object> ADM_LGN_01000A(Map<String,String> param) throws Exception;

	Object ADM_LGN_01000A_I1(Map<String,String> param) throws Exception;

	//모듈목록 조회
	List<Map<String,Object>> ADM_LGN_02010A() throws Exception;
}
