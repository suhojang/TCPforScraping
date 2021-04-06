package project.kais.web;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dom4j.Element;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import project.kais.record.MgrInfoRec;
import project.kais.service.ADM_ATH_Service;
import project.kais.service.ADM_PCD_Service;
import project.kais.service.PublicService;

import com.kwic.web.controller.Controllers;
import com.kwic.xml.parser.JXParser;

import egovframework.rte.fdl.property.EgovPropertyService;

@Controller
public class ADM_ATH_Controller extends Controllers{

	@Resource(name="propertiesService")
	private EgovPropertyService properties;
	
	/** service */
	@Resource(name = "ADM_ATH_Service")
	private ADM_ATH_Service service;
	/** service */
	@Resource(name = "ADM_PCD_Service")
	private ADM_PCD_Service pcdService;
	/** service */
	@Resource(name = "PublicService")
	private PublicService servicePub;

	/**
	 * 권한관리 화면
	 * 
	 * */
	@RequestMapping(value="/ADM_ATH_010000/")
	public String ADM_ATH_010000(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		model.addAttribute("CD0001"		, pcdService.ADM_PCD_S1200A("0001"));//공통코드 관리자등급 목록
		model.addAttribute("URI_XML"	, servicePub.getURI_XML());
		return "/ADM/ATH/ADM_ATH_010000";
	}
	
	/**
	 * 권한조회
	 * 
	 * */
	@RequestMapping(value="/ADM_ATH_S1100A/")
	public void ADM_ATH_S1100A(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		try{
			Map<String,Object> list	= service.ADM_ATH_S1000A();
			
			Map<String,Object> ajax	= new HashMap<String,Object>();
			ajax.put("LIST", list);
			
			ajaxResponse(request,response,ajax);//정상
		
		}catch(Exception e){
			ajaxResponse(request,response,e);//오류
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 권한저장
	 * 
	 * */
	@RequestMapping(value="/ADM_ATH_U1020A/")
	public void ADM_ATH_U1020A(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {
		try{
			String URLST_XML	= getParam(request,"URLST_XML","URLST_XML이",true,20000);
			Map<String,String> param	= new HashMap<String,String>();
			param.put("URLST_XML", URLST_XML);
			
			//변경된 권한xml저장
			service.ADM_ATH_U1020A(param);
			
			//변경된 권한목록을 세션에 재저장
			MgrInfoRec rec	= (MgrInfoRec)session.getAttribute("MgrInfoRec");
			JXParser jxp	= new JXParser(servicePub.getURI_XML());
			Element[] noAuthMenus	= jxp.getElements("//*[@AUTH"+rec.getMgrinf_grd()+"!='Y']");
			for(int i=0;i<noAuthMenus.length;i++)
				jxp.removeElement(noAuthMenus[i]);
			rec.setUrixml(jxp);
			request.getSession().setAttribute("MgrInfoRec", rec);
			
			ajaxResponse(request,response);//정상
		
		}catch(Exception e){
			ajaxResponse(request,response,e);//오류
			logger.error(e.getMessage(), e);
		}
	}

}
