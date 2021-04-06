package project.kais.service;

import java.util.List;
import java.util.Map;

/**
 * @Class Name : ADM_USR_Service.java
 * @Description : ADM_USR_Service Class
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
public interface ADM_USR_Service {
	
	//사용자 목록조회
	List<Map<String,Object>> ADM_USR_S1000A() throws Exception;
	//사용자 조회
	Map<String,Object> ADM_USR_V1040A(String MGRINF_SEQ) throws Exception;
	//사용자 id 중복확인
	Map<String,Object> ADM_USR_V1040A_1(String MGRINF_ID) throws Exception;
	//사용자 id 중복확인
	Map<String,Object> ADM_USR_V1040A_2(Map<String,String> param) throws Exception;
	//사용자 추가
	void ADM_USR_I1010A(Map<String,String> param) throws Exception;
	//사용자 수정
	void ADM_USR_U1020A(Map<String,String> param) throws Exception;
	//사용자 삭제
	void ADM_USR_D1030A(String MGRINF_SEQ) throws Exception;

}
