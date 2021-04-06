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
import project.kais.service.ADM_MNU_Service;
import project.kais.service.ADM_PCD_Service;
import project.kais.service.PublicService;

import com.kwic.web.controller.Controllers;
import com.kwic.xml.parser.JXParser;

import egovframework.rte.fdl.property.EgovPropertyService;

@Controller
public class ADM_MNU_Controller extends Controllers{

	@Resource(name="propertiesService")
	private EgovPropertyService properties;
	
	/** service */
	@Resource(name = "ADM_MNU_Service")
	private ADM_MNU_Service service;
	/** service */
	@Resource(name = "ADM_PCD_Service")
	private ADM_PCD_Service pcdService;
	/** service */
	@Resource(name = "PublicService")
	private PublicService servicePub;

	/**
	 * 메뉴관리 화면
	 * 
	 * */
	@RequestMapping(value="/ADM_MNU_010000/")
	public String ADM_MNU_010000(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		model.addAttribute("CD0001"		, pcdService.ADM_PCD_S1200A("0001"));//공통코드 관리자등급 목록
		return "/ADM/MNU/ADM_MNU_010000";
	}
	
	/**
	 * 메뉴조회
	 * 
	 * */
	@RequestMapping(value="/ADM_MNU_S1100A/")
	public void ADM_MNU_S1100A(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		try{
			Map<String,Object> list	= service.ADM_MNU_S1000A();
			
			Map<String,Object> ajax	= new HashMap<String,Object>();
			ajax.put("LIST", list);
			
			ajaxResponse(request,response,ajax);//정상
		
		}catch(Exception e){
			ajaxResponse(request,response,e);//오류
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 메뉴저장
	 * 
	 * */
	@RequestMapping(value="/ADM_MNU_U1020A/")
	public void ADM_MNU_U1020A(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {
		try{
			String MNLST_XML	= getParam(request,"MNLST_XML","MNLST_XML이",true,10000);
			Map<String,String> param	= new HashMap<String,String>();
			param.put("MNLST_XML", MNLST_XML);
			
			//변경된 메뉴xml저장
			service.ADM_MNU_U1020A(param);
			
			//변경된 메뉴목록을 세션에 재저장
			MgrInfoRec rec	= (MgrInfoRec)session.getAttribute("MgrInfoRec");
			String MENU_XML	= servicePub.getMENU_XML();
			rec.setMenufullxml(MENU_XML);
			
			JXParser jxp	= new JXParser(MENU_XML);
			Element[] noAuthMenus	= jxp.getElements("//*[@AUTH"+rec.getMgrinf_grd()+"!='Y' or @USYN!='Y']");
			for(int i=0;i<noAuthMenus.length;i++)
				jxp.removeElement(noAuthMenus[i]);
			rec.setMenuxml(jxp.toString(null));
			request.getSession().setAttribute("MgrInfoRec", rec);
			
			ajaxResponse(request,response);//정상
		
		}catch(Exception e){
			ajaxResponse(request,response,e);//오류
			logger.error(e.getMessage(), e);
		}
	}

}
