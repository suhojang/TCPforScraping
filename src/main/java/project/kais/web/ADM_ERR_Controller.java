package project.kais.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import project.kais.service.ADM_PCD_Service;
import project.kais.service.ADM_ERR_Service;

import com.kwic.security.aes.AESCipher;
import com.kwic.util.StringUtil;
import com.kwic.web.controller.Controllers;

import egovframework.rte.fdl.property.EgovPropertyService;

@Controller
public class ADM_ERR_Controller extends Controllers{

	@Resource(name="propertiesService")
	private EgovPropertyService properties;
	
	/** service */
	@Resource(name = "ADM_ERR_Service")
	private ADM_ERR_Service service;

	/** service */
	@Resource(name = "ADM_PCD_Service")
	private ADM_PCD_Service pcdService;
	
	/**
	 * 처리불가 화면
	 * 
	 * */
	@RequestMapping(value="/ADM_ERR_010000/")
	public String ADM_ERR_010000(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		List<Map<String,Object>> list	= pcdService.ADM_PCD_S1200A("0003");
		model.addAttribute("CD0003"		, list);//오류코드 종류
		return "/ADM/ERR/ADM_ERR_010000";
	}

	/**
	 * 처리불가 목록 조회
	 * 
	 * */
	@RequestMapping(value="/ADM_ERR_S1000A/")
	public void ADM_ERR_S1000A(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		try{
			String NCRQER_RTCD	= getParam(request,"NCRQER_RTCD",4);
			String NCRQER_RDT_FR	= getParam(request,"NCRQER_RDT_FR",10);
			String NCRQER_RDT_TO	= getParam(request,"NCRQER_RDT_TO",10);
			String PAGE_NO			= getParam(request,"PAGE_NO",5);
			String ROWPERPAGE		= getParam(request,"ROWPERPAGE",3);

			NCRQER_RDT_FR	= StringUtil.replace(StringUtil.replace(NCRQER_RDT_FR, "-", ""),".","");
			NCRQER_RDT_TO	= StringUtil.replace(StringUtil.replace(NCRQER_RDT_TO, "-", ""),".","");

			if(PAGE_NO==null || "".equals(PAGE_NO))
				PAGE_NO	= "1";
			if(ROWPERPAGE==null || "".equals(ROWPERPAGE))
				ROWPERPAGE	= properties.getString("pageSize");
			
			String STNO	= String.valueOf((Integer.parseInt(PAGE_NO)-1)*Integer.parseInt(ROWPERPAGE)+1);
			String EDNO	= String.valueOf(Integer.parseInt(PAGE_NO)*Integer.parseInt(ROWPERPAGE));

			Map<String,String> param	= new HashMap<String,String>();
			param.put("NCRQER_RTCD", NCRQER_RTCD);
			param.put("NCRQER_RDT_FR", NCRQER_RDT_FR);
			param.put("NCRQER_RDT_TO", NCRQER_RDT_TO);
			param.put("STNO",STNO);
			param.put("EDNO",EDNO);
			
			List<Map<String,Object>> list	= service.ADM_ERR_S1000A(param);
			Map<String,Object> info	= service.ADM_ERR_S1000A_1(param);
			
			Map<String,Object> ajax	= new HashMap<String,Object>();
			ajax.put("LIST", list);
			ajax.put("TCNT", info.get("TCNT"));
			
			ajaxResponse(request,response,ajax);//정상
		
		}catch(Exception e){
			ajaxResponse(request,response,e);//오류
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 처리불가 조회
	 * 
	 * */
	@RequestMapping(value="/ADM_ERR_V1000A/")
	public void ADM_ERR_V1000A(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		try{
			String NCRQER_SEQ	= getParam(request,"NCRQER_SEQ",20);
			
			Map<String,String> param	= new HashMap<String,String>();
			param.put("NCRQER_SEQ", NCRQER_SEQ);

			
			Map<String,Object> info	= service.ADM_ERR_V1000A(param);
			String NCRQER_SCRQ	= AESCipher.decode(String.valueOf(info.get("NCRQER_SCRQ")), AESCipher.DEFAULT_KEY,AESCipher.TYPE_256,"UTF-8",AESCipher.MODE_ECB_NOPADDING);
			info.put("NCRQER_SCRQ", NCRQER_SCRQ);
			
			Map<String,Object> ajax	= new HashMap<String,Object>();
			ajax.put("INFO", info);
			
			ajaxResponse(request,response,ajax);//정상
		
		}catch(Exception e){
			ajaxResponse(request,response,e);//오류
			logger.error(e.getMessage(), e);
		}
	}
}
