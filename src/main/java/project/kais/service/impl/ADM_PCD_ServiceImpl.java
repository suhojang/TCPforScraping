package project.kais.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import project.kais.service.ADM_PCD_Service;
import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;

/**
 * @Class Name : ADM_PCD_ServiceImpl.java
 * @Description : 스케쥴 로그
 * @Modification Information
 * @
 * @  수정일      수정자              수정내용
 * @ ---------   ---------   -------------------------------
 * @ 2016.11.07           최초생성
 *
 * @author 기웅정보통신
 * @since 2016. 11.07
 * @version 1.0
 * @see
 *
 *  Copyright (C) by MOPAS All right reserved.
 */

@Service("ADM_PCD_Service")
public class ADM_PCD_ServiceImpl extends EgovAbstractServiceImpl implements ADM_PCD_Service {

	/** MobileDAO */
	@Resource(name = "ADM_PCD_Dao")
	private ADM_PCD_Dao dao;

	//분류코드목록 조회
	public List<Map<String,Object>> ADM_PCD_S1100A() throws Exception{
		return dao.ADM_PCD_S1100A();
	}
	//분류코드 조회
	public Map<String,Object> ADM_PCD_S1100A_1(String CDCLS_CLSCD) throws Exception{
		return dao.ADM_PCD_S1100A_1(CDCLS_CLSCD);
	}
	//분류코드 추가
	public void ADM_PCD_I1110A(Map<String,String> param) throws Exception{
		dao.ADM_PCD_I1110A(param);
	}
	//분류코드 수정
	public void ADM_PCD_U1120A(Map<String,String> param) throws Exception{
		dao.ADM_PCD_U1120A(param);
	}
	//분류코드 삭제
	public void ADM_PCD_D1130A(String CDCLS_CLSCD) throws Exception{
		dao.ADM_PCD_D1130A(CDCLS_CLSCD);
	}

	//상세코드 목록
	public List<Map<String,Object>> ADM_PCD_S1200A(String CDCLS_CLSCD) throws Exception{
		return dao.ADM_PCD_S1200A(CDCLS_CLSCD);
	}
	//상세코드 목록(사용건만)
	public List<Map<String,Object>> ADM_PCD_S1200A_USE(String CDCLS_CLSCD) throws Exception{
		return dao.ADM_PCD_S1200A_USE(CDCLS_CLSCD);
	}
	//상세코드 조회
	public Map<String,Object> ADM_PCD_S1200A_1(Map<String,String> param) throws Exception{
		return dao.ADM_PCD_S1200A_1(param);
	}
	//상세코드명으로 조회
	public Map<String,Object> ADM_PCD_S1200A_2(Map<String,String> param) throws Exception{
		return dao.ADM_PCD_S1200A_2(param);
	}
	//상세코드 추가
	public void ADM_PCD_I1210A(Map<String,String> param) throws Exception{
		dao.ADM_PCD_I1210A(param);
	}
	//상세코드 수정
	public void ADM_PCD_U1220A(Map<String,String> param) throws Exception{
		dao.ADM_PCD_U1220A(param);
	}
	//상세코드 삭제
	public void ADM_PCD_D1230A(Map<String,String> param) throws Exception{
		dao.ADM_PCD_D1230A(param);
	}
	
}
