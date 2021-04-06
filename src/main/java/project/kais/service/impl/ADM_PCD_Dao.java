package project.kais.service.impl;


import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kwic.service.DaoSupport;

/**
 * @Class Name : ADM_LOG_Dao.java
 * @Description : ADM_LOG_Dao Class
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

@Repository("ADM_PCD_Dao")
public class ADM_PCD_Dao extends DaoSupport {
	//분류코드 목록조회
	public List<Map<String,Object>> ADM_PCD_S1100A() {
		return list("ADM_PCD_S1100A");
	}
	//분류코드 조회
	@SuppressWarnings("unchecked")
	public Map<String,Object> ADM_PCD_S1100A_1(String CDCLS_CLSCD) {
		return (Map<String,Object>)select("ADM_PCD_S1100A_1",CDCLS_CLSCD);
	}
	//분류코드 추가
	public void ADM_PCD_I1110A(Map<String,String> param) {
		insert("ADM_PCD_I1110A",param);
	}
	//분류코드 수정
	public void ADM_PCD_U1120A(Map<String,String> param) {
		update("ADM_PCD_U1120A",param);
	}
	//분류코드 삭제
	public void ADM_PCD_D1130A(String CDCLS_CLSCD) {
		update("ADM_PCD_D1130A",CDCLS_CLSCD);
		update("ADM_PCD_D1130A_1",CDCLS_CLSCD);
	}

	//상세코드 목록조회
	public List<Map<String,Object>> ADM_PCD_S1200A(String CDCLS_CLSCD) {
		return list("ADM_PCD_S1200A",CDCLS_CLSCD);
	}
	//상세코드 목록조회(사용건만)
	public List<Map<String,Object>> ADM_PCD_S1200A_USE(String CDCLS_CLSCD) {
		return list("ADM_PCD_S1200A_USE",CDCLS_CLSCD);
	}
	//상세코드 조회
	@SuppressWarnings("unchecked")
	public Map<String,Object> ADM_PCD_S1200A_1(Map<String,String> param) {
		return (Map<String,Object>)select("ADM_PCD_S1200A_1",param);
	}
	//상세코드 명으로 조회
	@SuppressWarnings("unchecked")
	public Map<String,Object> ADM_PCD_S1200A_2(Map<String,String> param) {
		return (Map<String,Object>)select("ADM_PCD_S1200A_2",param);
	}
	
	//상세코드 추가
	public void ADM_PCD_I1210A(Map<String,String> param) {
		insert("ADM_PCD_I1210A",param);
	}
	//상세코드 수정
	public void ADM_PCD_U1220A(Map<String,String> param) {
		update("ADM_PCD_U1220A",param);
	}
	//상세코드 삭제
	public void ADM_PCD_D1230A(Map<String,String> param) {
		update("ADM_PCD_D1230A",param);
	}

}
