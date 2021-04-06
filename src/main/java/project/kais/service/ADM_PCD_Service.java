package project.kais.service;

import java.util.List;
import java.util.Map;

/**
 * @Class Name : ADM_LOG_Service.java
 * @Description : ADM_LOG_Service Class
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
public interface ADM_PCD_Service {
	
	//분류코드 목록조회
	List<Map<String,Object>> ADM_PCD_S1100A() throws Exception;
	//분류코드 조회
	Map<String,Object> ADM_PCD_S1100A_1(String CDCLS_CLSCD) throws Exception;
	//분류코드 추가
	void ADM_PCD_I1110A(Map<String,String> param) throws Exception;
	//분류코드 수정
	void ADM_PCD_U1120A(Map<String,String> param) throws Exception;
	//분류코드 삭제
	void ADM_PCD_D1130A(String CDCLS_CLSCD) throws Exception;

	//상세코드 목록조회
	List<Map<String,Object>> ADM_PCD_S1200A(String CDCLS_CLSCD) throws Exception;
	//상세코드 목록조회(사용건만)
	List<Map<String,Object>> ADM_PCD_S1200A_USE(String CDCLS_CLSCD) throws Exception;
	//상세코드 조회
	Map<String,Object> ADM_PCD_S1200A_1(Map<String,String> param) throws Exception;
	//상세코드명으로 조회
	Map<String,Object> ADM_PCD_S1200A_2(Map<String,String> param) throws Exception;
	//상세코드 추가
	void ADM_PCD_I1210A(Map<String,String> param) throws Exception;
	//상세코드 수정
	void ADM_PCD_U1220A(Map<String,String> param) throws Exception;
	//상세코드 삭제
	void ADM_PCD_D1230A(Map<String,String> param) throws Exception;
}
