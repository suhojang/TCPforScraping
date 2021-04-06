package project.kais.service;

import java.util.List;
import java.util.Map;

/**
 * @Class Name : ADM_ERR_Service.java
 * @Description : ADM_ERR_Service Class
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
public interface ADM_ERR_Service {
	
	//처리불가 목록조회
	List<Map<String,Object>> ADM_ERR_S1000A(Map<String,String> param) throws Exception;
	Map<String,Object> ADM_ERR_S1000A_1(Map<String,String> param) throws Exception;
	Map<String,Object> ADM_ERR_V1000A(Map<String,String> param) throws Exception;

}
