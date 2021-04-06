package project.kais.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import project.kais.record.MgrInfoRec;
import project.kais.service.ADM_PCD_Service;
import project.kais.service.ADM_USR_Service;

import com.kwic.exception.DefinedException;
import com.kwic.security.aes.AESCipher;
import com.kwic.util.StringUtil;
import com.kwic.web.controller.Controllers;

import egovframework.rte.fdl.property.EgovPropertyService;

@Controller
public class ADM_USR_Controller extends Controllers{

	@Resource(name="propertiesService")
	private EgovPropertyService properties;
	
	/** service */
	@Resource(name = "ADM_USR_Service")
	private ADM_USR_Service service;

	/** service */
	@Resource(name = "ADM_PCD_Service")
	private ADM_PCD_Service pcdService;
	
	/**
	 * 사용자 관리 화면
	 * 
	 * */
	@RequestMapping(value="/ADM_USR_010000/")
	public String ADM_USR_010000(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		List<Map<String,Object>> list	= pcdService.ADM_PCD_S1200A("0001");
		for(int i=list.size()-1;i>=0;i--){
			//고객사담당자용 등급만 추출
			if(list.get(i).get("CDDTL_DTLCD").toString().startsWith("1"))
				list.remove(i);
		}
		model.addAttribute("CD0001"		, list);//공통코드 관리자등급 목록
		return "/ADM/USR/ADM_USR_010000";
	}

	/**
	 * 사용자 목록조회
	 * 
	 * */
	@RequestMapping(value="/ADM_USR_S1000A/")
	public void ADM_PCD_S1100A(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		try{
			List<Map<String,Object>> list	= service.ADM_USR_S1000A();
			
			Map<String,Object> ajax	= new HashMap<String,Object>();
			ajax.put("LIST", list);
			
			ajaxResponse(request,response,ajax);//정상
		
		}catch(Exception e){
			ajaxResponse(request,response,e);//오류
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 사용자 추가
	 * */
	@RequestMapping(value="/ADM_USR_I1010A/")
	public void ADM_USR_I1010A(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {
		try{
			String MGRINF_ID	= getParam(request,"MGRINF_ID","ID가",true,20);
			String MGRINF_PWD	= getParam(request,"MGRINF_PWD","비밀번호가",true,30);
			String MGRINF_GRD	= getParam(request,"MGRINF_GRD","권한등급이",true,2);
			String MGRINF_NM	= getParam(request,"MGRINF_NM","사용자명이",true,50);
			String MGRINF_TEL	= getParam(request,"MGRINF_TEL",50);
			String MGRINF_USYN	= getParam(request,"MGRINF_USYN","사용여부가",true,1);
			if(MGRINF_ID.length()<4)
				throw new DefinedException("ID를 4자리 이상으로 입력하여 주십시오.");
			if("00000000".equals(MGRINF_PWD))
				throw new DefinedException("패스워드에 00000000를 사용할 수 없습니다.");
			else if(MGRINF_PWD.length()<8)
				throw new DefinedException("패스워드를 8자리 이상으로 입력하여 주십시오.");
			
			if(!StringUtil.isContain(MGRINF_PWD,StringUtil.CONTAIN_TYPE_NUMBER))
				throw new DefinedException("패스워드는 영문/숫자/특수문자가 모두 포함되어야합니다.");
			if(!StringUtil.isContain(MGRINF_PWD,StringUtil.CONTAIN_TYPE_ENGLISH))
				throw new DefinedException("패스워드는 영문/숫자/특수문자가 모두 포함되어야합니다.");
			if(!StringUtil.isContain(MGRINF_PWD,StringUtil.CONTAIN_TYPE_SIGNAL))
				throw new DefinedException("패스워드는 영문/숫자/특수문자가 모두 포함되어야합니다.");
			
			Map<String,String> param	= new HashMap<String,String>();
			param.put("MGRINF_ID", MGRINF_ID);
			param.put("MGRINF_PWD", AESCipher.encode(MGRINF_PWD, AESCipher.DEFAULT_KEY,AESCipher.TYPE_256,"UTF-8",AESCipher.MODE_ECB_NOPADDING));
			param.put("MGRINF_GRD", MGRINF_GRD);
			param.put("MGRINF_NM", MGRINF_NM);
			param.put("MGRINF_TEL", MGRINF_TEL);
			param.put("MGRINF_USYN", MGRINF_USYN);
			param.put("MGRINF_RUSR", ((MgrInfoRec)session.getAttribute("MgrInfoRec")).getMgrinf_id());
			
			//ID 중복확인
			Map<String,Object> info	= service.ADM_USR_V1040A_1(MGRINF_ID);
			if(info!=null)
				throw new DefinedException("동일한 ID가 이미 존재합니다.");
			
			service.ADM_USR_I1010A(param);
			
			ajaxResponse(request,response);//정상
		
		}catch(Exception e){
			ajaxResponse(request,response,e);//오류
			logger.error(e.getMessage(), e);
		}
	}
	/**
	 * 사용자 수정
	 * */
	@RequestMapping(value="/ADM_USR_U1020A/")
	public void ADM_PCD_U1120A(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {
		try{
			String MGRINF_ID	= getParam(request,"MGRINF_ID","ID가",true,20);
			String MGRINF_PWD	= getParam(request,"MGRINF_PWD","비밀번호가",true,30);
			String MGRINF_GRD	= getParam(request,"MGRINF_GRD","권한등급이",true,2);
			String MGRINF_NM	= getParam(request,"MGRINF_NM","사용자명이",true,50);
			String MGRINF_TEL	= getParam(request,"MGRINF_TEL",50);
			String MGRINF_USYN	= getParam(request,"MGRINF_USYN","사용여부가",true,1);
			String MGRINF_SEQ	= getParam(request,"MGRINF_SEQ","사용자번호가",true,12);
			if(MGRINF_ID.length()<4)
				throw new DefinedException("ID를 4자리 이상으로 입력하여 주십시오.");
			if(MGRINF_PWD.length()<8)
				throw new DefinedException("패스워드를 8자리 이상으로 입력하여 주십시오.");
			if(!MGRINF_PWD.equals("00000000")){
				if(!StringUtil.isContain(MGRINF_PWD,StringUtil.CONTAIN_TYPE_NUMBER))
					throw new DefinedException("패스워드는 영문/숫자/특수문자가 모두 포함되어야합니다.");
				if(!StringUtil.isContain(MGRINF_PWD,StringUtil.CONTAIN_TYPE_ENGLISH))
					throw new DefinedException("패스워드는 영문/숫자/특수문자가 모두 포함되어야합니다.");
				if(!StringUtil.isContain(MGRINF_PWD,StringUtil.CONTAIN_TYPE_SIGNAL))
					throw new DefinedException("패스워드는 영문/숫자/특수문자가 모두 포함되어야합니다.");
			}
			
			Map<String,String> param	= new HashMap<String,String>();
			param.put("MGRINF_ID", MGRINF_ID);
			param.put("MGRINF_PWD", MGRINF_PWD.equals("00000000")?"-":AESCipher.encode(MGRINF_PWD, AESCipher.DEFAULT_KEY,AESCipher.TYPE_256,"UTF-8",AESCipher.MODE_ECB_NOPADDING));
			param.put("MGRINF_GRD", MGRINF_GRD);
			param.put("MGRINF_NM", MGRINF_NM);
			param.put("MGRINF_TEL", MGRINF_TEL);
			param.put("MGRINF_USYN", MGRINF_USYN);
			param.put("MGRINF_RUSR", ((MgrInfoRec)session.getAttribute("MgrInfoRec")).getMgrinf_id());
			param.put("MGRINF_SEQ", MGRINF_SEQ);
			
			//사용자 존재여부 확인
			Map<String,Object> info	= service.ADM_USR_V1040A(MGRINF_SEQ);
			if(info==null)
				throw new DefinedException("사용자가 존재하지 않습니다.");
			
			//ID 중복확인2
			info	= service.ADM_USR_V1040A_2(param);
			if(info!=null)
				throw new DefinedException("동일한 ID가 이미 존재합니다.");
			
			service.ADM_USR_U1020A(param);
			
			ajaxResponse(request,response);//정상
		
		}catch(Exception e){
			ajaxResponse(request,response,e);//오류
			logger.error(e.getMessage(), e);
		}
	}
	/**
	 * 사용자 삭제
	 * */
	@RequestMapping(value="/ADM_USR_D1030A/")
	public void ADM_USR_D1030A(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {
		try{
			String MGRINF_SEQ	= getParam(request,"MGRINF_SEQ","사용자번호가",true,12);
			
			//사용자 존재여부 확인
			Map<String,Object> info	= service.ADM_USR_V1040A(MGRINF_SEQ);
			if(info==null)
				throw new DefinedException("사용자가 존재하지 않습니다.");
			
			service.ADM_USR_D1030A(MGRINF_SEQ);
			ajaxResponse(request,response);//정상
		
		}catch(Exception e){
			ajaxResponse(request,response,e);//오류
			logger.error(e.getMessage(), e);
		}
	}

}
