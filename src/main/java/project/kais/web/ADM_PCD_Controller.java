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

import com.kwic.exception.DefinedException;
import com.kwic.web.controller.Controllers;

import egovframework.rte.fdl.property.EgovPropertyService;

@Controller
public class ADM_PCD_Controller extends Controllers{

	@Resource(name="propertiesService")
	private EgovPropertyService properties;
	
	/** service */
	@Resource(name = "ADM_PCD_Service")
	private ADM_PCD_Service service;

	
	/**
	 * 공통코드 관리 화면
	 * 
	 * */
	@RequestMapping(value="/ADM_PCD_010000/")
	public String ADM_PCD_010000(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		return "/ADM/PCD/ADM_PCD_010000";
	}

	/**
	 * 분류코드 목록조회
	 * 
	 * */
	@RequestMapping(value="/ADM_PCD_S1100A/")
	public void ADM_PCD_S1100A(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		try{
			List<Map<String,Object>> list	= service.ADM_PCD_S1100A();
			
			Map<String,Object> ajax	= new HashMap<String,Object>();
			ajax.put("LIST", list);
			
			ajaxResponse(request,response,ajax);//정상
		
		}catch(Exception e){
			ajaxResponse(request,response,e);//오류
			logger.error(e.getMessage(), e);
		}
	}
	/**
	 * 분류코드 추가
	 * */
	@RequestMapping(value="/ADM_PCD_I1110A/")
	public void ADM_PCD_I1110A(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {
		try{
			String CDCLS_CLSCD	= getParam(request,"CDCLS_CLSCD","분류코드가",true,4);
			String CDCLS_NM	= getParam(request,"CDCLS_NM","분류명이",true,50);
			String CDCLS_LEN	= getParam(request,"CDCLS_LEN","상세코드 크기가",true,3);
			
			Map<String,String> param	= new HashMap<String,String>();
			param.put("CDCLS_CLSCD", CDCLS_CLSCD);
			param.put("CDCLS_NM", CDCLS_NM);
			param.put("CDCLS_LEN", CDCLS_LEN);
			param.put("CDCLS_RUSR", ((MgrInfoRec)session.getAttribute("MgrInfoRec")).getMgrinf_id());
			
			//분류코드 존재여부 확인
			Map<String,Object> info	= service.ADM_PCD_S1100A_1(CDCLS_CLSCD);
			if(info!=null)
				throw new DefinedException("동일한 분류코드가 이미 존재합니다.");
			service.ADM_PCD_I1110A(param);
			
			ajaxResponse(request,response);//정상
		
		}catch(Exception e){
			ajaxResponse(request,response,e);//오류
			logger.error(e.getMessage(), e);
		}
	}
	/**
	 * 분류코드 수정
	 * */
	@RequestMapping(value="/ADM_PCD_U1120A/")
	public void ADM_PCD_U1120A(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {
		try{
			String CDCLS_CLSCD	= getParam(request,"CDCLS_CLSCD","분류코드가",true,4);
			String CDCLS_NM	= getParam(request,"CDCLS_NM","분류명이",true,50);
			String CDCLS_LEN	= getParam(request,"CDCLS_LEN","상세코드 크기가",true,3);
			
			Map<String,String> param	= new HashMap<String,String>();
			param.put("CDCLS_CLSCD", CDCLS_CLSCD);
			param.put("CDCLS_NM", CDCLS_NM);
			param.put("CDCLS_LEN", CDCLS_LEN);
			param.put("CDCLS_RUSR", ((MgrInfoRec)session.getAttribute("MgrInfoRec")).getMgrinf_id());
			
			//분류코드 존재여부 확인
			Map<String,Object> info	= service.ADM_PCD_S1100A_1(CDCLS_CLSCD);
			if(info==null)
				throw new DefinedException("분류코드가 존재하지 않습니다.");
			
			service.ADM_PCD_U1120A(param);
			
			ajaxResponse(request,response);//정상
		
		}catch(Exception e){
			ajaxResponse(request,response,e);//오류
			logger.error(e.getMessage(), e);
		}
	}
	/**
	 * 분류코드 삭제
	 * */
	@RequestMapping(value="/ADM_PCD_D1130A/")
	public void ADM_PCD_D1130A(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {
		try{
			String CDCLS_CLSCD	= getParam(request,"CDCLS_CLSCD","분류코드가",true,4);
			
			//분류코드 존재여부 확인
			Map<String,Object> info	= service.ADM_PCD_S1100A_1(CDCLS_CLSCD);
			if(info==null)
				throw new DefinedException("분류코드가 존재하지 않습니다.");
			
			service.ADM_PCD_D1130A(CDCLS_CLSCD);
			
			ajaxResponse(request,response);//정상
		
		}catch(Exception e){
			ajaxResponse(request,response,e);//오류
			logger.error(e.getMessage(), e);
		}
	}
	/**
	 * 상세코드 목록조회
	 * 
	 * */
	@RequestMapping(value="/ADM_PCD_S1200A/")
	public void ADM_PCD_S1200A(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		try{
			String CDCLS_CLSCD	= getParam(request,"CDCLS_CLSCD","분류코드가",true,4);
			List<Map<String,Object>> list	= service.ADM_PCD_S1200A(CDCLS_CLSCD);
			
			Map<String,Object> ajax	= new HashMap<String,Object>();
			ajax.put("LIST", list);
			
			ajaxResponse(request,response,ajax);//정상
		
		}catch(Exception e){
			ajaxResponse(request,response,e);//오류
			logger.error(e.getMessage(), e);
		}
	}
	/**
	 * 상세코드 추가
	 * */
	@RequestMapping(value="/ADM_PCD_I1210A/")
	public void ADM_PCD_I1210A(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {
		try{
			String CDCLS_CLSCD	= getParam(request,"CDCLS_CLSCD","분류코드가",true,4);
			String CDDTL_DTLCD	= getParam(request,"CDDTL_DTLCD","상세코드가",true,50);
			String CDDTL_NM		= getParam(request,"CDDTL_NM","코드명이",true,100);
			String CDDTL_ORD	= getParam(request,"CDDTL_ORD","정렬순서가",true,4);
			String CDDTL_USEYN	= getParam(request,"CDDTL_USEYN","사용여부가",true,1);
			
			Map<String,String> param	= new HashMap<String,String>();
			param.put("CDCLS_CLSCD", CDCLS_CLSCD);
			param.put("CDDTL_DTLCD", CDDTL_DTLCD);
			param.put("CDDTL_NM", CDDTL_NM);
			param.put("CDDTL_ORD", CDDTL_ORD);
			param.put("CDDTL_USEYN", CDDTL_USEYN);
			param.put("CDDTL_RUSR", ((MgrInfoRec)session.getAttribute("MgrInfoRec")).getMgrinf_id());
			
			//분류코드 존재여부 확인
			Map<String,Object> info	= service.ADM_PCD_S1200A_1(param);
			if(info!=null)
				throw new DefinedException("동일한 상세코드가 이미 존재합니다.");
			service.ADM_PCD_I1210A(param);
			
			ajaxResponse(request,response);//정상
		
		}catch(Exception e){
			ajaxResponse(request,response,e);//오류
			logger.error(e.getMessage(), e);
		}
	}
	/**
	 * 상세코드 수정
	 * */
	@RequestMapping(value="/ADM_PCD_U1220A/")
	public void ADM_PCD_U1220A(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {
		try{
			String CDCLS_CLSCD	= getParam(request,"CDCLS_CLSCD","분류코드가",true,4);
			String CDDTL_DTLCD	= getParam(request,"CDDTL_DTLCD","상세코드가",true,50);
			String CDDTL_NM		= getParam(request,"CDDTL_NM","코드명이",true,100);
			String CDDTL_ORD	= getParam(request,"CDDTL_ORD","정렬순서가",true,4);
			String CDDTL_USEYN	= getParam(request,"CDDTL_USEYN","사용여부가",true,1);
			
			Map<String,String> param	= new HashMap<String,String>();
			param.put("CDCLS_CLSCD", CDCLS_CLSCD);
			param.put("CDDTL_DTLCD", CDDTL_DTLCD);
			param.put("CDDTL_NM", CDDTL_NM);
			param.put("CDDTL_ORD", CDDTL_ORD);
			param.put("CDDTL_USEYN", CDDTL_USEYN);
			param.put("CDDTL_RUSR", ((MgrInfoRec)session.getAttribute("MgrInfoRec")).getMgrinf_id());
			
			//분류코드 존재여부 확인
			Map<String,Object> info	= service.ADM_PCD_S1200A_1(param);
			if(info==null)
				throw new DefinedException("상세코드가 존재하지 않습니다.");
			
			service.ADM_PCD_U1220A(param);
			
			ajaxResponse(request,response);//정상
		
		}catch(Exception e){
			ajaxResponse(request,response,e);//오류
			logger.error(e.getMessage(), e);
		}
	}
	/**
	 * 상세코드 삭제
	 * */
	@RequestMapping(value="/ADM_PCD_D1230A/")
	public void ADM_PCD_D1230A(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {
		try{
			String CDCLS_CLSCD	= getParam(request,"CDCLS_CLSCD","분류코드가",true,4);
			String CDDTL_DTLCD	= getParam(request,"CDDTL_DTLCD","상세코드가",true,50);
			
			Map<String,String> param	= new HashMap<String,String>();
			param.put("CDCLS_CLSCD", CDCLS_CLSCD);
			param.put("CDDTL_DTLCD", CDDTL_DTLCD);
			
			//분류코드 존재여부 확인
			Map<String,Object> info	= service.ADM_PCD_S1200A_1(param);
			if(info==null)
				throw new DefinedException("상세코드가 존재하지 않습니다.");
			
			service.ADM_PCD_D1230A(param);
			
			ajaxResponse(request,response);//정상
		
		}catch(Exception e){
			ajaxResponse(request,response,e);//오류
			logger.error(e.getMessage(), e);
		}
	}

}
