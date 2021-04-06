package project.kais.web;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import project.kais.service.ADM_MNR_Service;
import project.kais.service.ADM_PCD_Service;
import project.kais.service.PublicService;

import com.kwic.web.controller.Controllers;

import egovframework.rte.fdl.property.EgovPropertyService;

@Controller
public class ADM_MNR_Controller extends Controllers{

	@Resource(name="propertiesService")
	private EgovPropertyService properties;
	
	/** service */
	@Resource(name = "ADM_MNR_Service")
	private ADM_MNR_Service service;
	/** service */
	@Resource(name = "ADM_PCD_Service")
	private ADM_PCD_Service pcdService;
	/** service */
	@Resource(name = "PublicService")
	private PublicService servicePub;

	/**
	 * 모니터링 화면
	 * 
	 * */
	@RequestMapping(value="/ADM_MNR_010000/")
	public String ADM_MNR_010000(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		return "/ADM/MNR/ADM_MNR_010000";
	}
	

}
