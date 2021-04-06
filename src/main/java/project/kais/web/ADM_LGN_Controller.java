package project.kais.web;


import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Element;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import project.kais.record.MgrInfoRec;
import project.kais.schedule.task.Task_manageips;
import project.kais.service.ADM_LGN_Service;
import project.kais.service.PublicService;

import com.kwic.exception.DefinedException;
import com.kwic.security.aes.AESCipher;
import com.kwic.web.controller.Controllers;
import com.kwic.xml.parser.JXParser;

import egovframework.rte.fdl.property.EgovPropertyService;

@Controller
public class ADM_LGN_Controller extends Controllers{
	
	@Resource(name="propertiesService")
	private EgovPropertyService properties;
	
	/** service */
	@Resource(name = "ADM_LGN_Service")
	private ADM_LGN_Service service;
	
	/** service */
	@Resource(name = "PublicService")
	private PublicService servicePub;
	
	/**
	 * 로그인 화면
	 * 
	 * */
	@RequestMapping(value={"/ADM_LGN_010000/","/kwic/"})
	public String ADM_LGN_010000(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		return "/ADM/LGN/ADM_LGN_010000";
	}

	/**
	 * 로그인
	 * 
	 * */
	@RequestMapping(value="/ADM_LGN_01000A/")
	public void ADM_LGN_01000A(HttpServletRequest request,HttpServletResponse response,Model model) throws Exception {
		
		MgrInfoRec rec	= null;
		Map<String,Object> ajaxObject	= new HashMap<String,Object>();
		try{
			//관리자 로그인은 특정 IP(본사 내부망)로만 접근가능하도록
			if(!Task_manageips.isAllowedIp(getRemoteIP(request)))
				throw new DefinedException("허가되지 않은 접근입니다. ["+getRemoteIP(request)+"]");
			
			String MGRINF_ID	= getParam(request,"MGRINF_ID","아이디가",true,20);
			String MGRINF_PWD	= getParam(request,"MGRINF_PWD","비밀번호가",true,30);
			
			Map<String,String> param	= new HashMap<String,String>();
			param.put("MGRINF_ID", MGRINF_ID);
			param.put("MGRINF_PWD", AESCipher.encode(MGRINF_PWD, AESCipher.DEFAULT_KEY,AESCipher.TYPE_256,"UTF-8",AESCipher.MODE_ECB_NOPADDING));
			
			//관리자 정보 조회
			Map<String,Object> info	= service.ADM_LGN_01000A(param);
			if(info==null || info.get("MGRINF_GRD")==null)
				throw new DefinedException("로그인 정보가 올바르지 않습니다.");
			
			rec	= new MgrInfoRec();
			rec.setMgrinf_id((String)info.get("MGRINF_ID"));
			rec.setMgrinf_grd((String)info.get("MGRINF_GRD"));
			rec.setMgrinf_grd_nm((String)info.get("MGRINF_GRD_NM"));
			rec.setMgrinf_nm((String)info.get("MGRINF_NM"));
			rec.setMgrinf_tel((String)info.get("MGRINF_TEL"));
			
			//메뉴 조회
			String menuXml	= servicePub.getMENU_XML();
			rec.setMenufullxml(menuXml);
			
			JXParser jxp	= new JXParser(menuXml);
			Element[] noAuthMenus	= jxp.getElements("//*[@AUTH"+rec.getMgrinf_grd()+"!='Y' or @USYN!='Y']");
			for(int i=0;i<noAuthMenus.length;i++)
				jxp.removeElement(noAuthMenus[i]);
			
			rec.setMenuxml(jxp.toString(null));
			
			//변경된 권한목록을 세션에 재저장
			jxp	= new JXParser(servicePub.getURI_XML());
			Element[] noAuthUris	= jxp.getElements("//*[@AUTH"+rec.getMgrinf_grd()+"!='Y']");
			for(int i=0;i<noAuthUris.length;i++)
				jxp.removeElement(noAuthUris[i]);
			rec.setUrixml(jxp);
			
			//세션 저장
			request.getSession().invalidate();
			request.getSession().setAttribute("MgrInfoRec", rec);
			
			//로그인 이력 저장
			param.put("LGNHST_RMTIP", getRemoteIP(request));
			service.ADM_LGN_01000A_I1(param);

			//Cookie 아이디 저장
//			Cookie cookie	= new Cookie("MGRINF_ID",rec.getMgrinf_id());
//			cookie.setMaxAge(60*60*24*7);
//			response.addCookie(cookie);
			ajaxObject.put("MGRINF_ID", rec.getMgrinf_id());
			
			ajaxResponse(request,response,ajaxObject);//정상로그인
			
		}catch(Exception e){
			ajaxResponse(request,response,e);//오류
		}
	}
	
	/**
	 * 로그아웃
	 * 
	 * */
	@RequestMapping(value="/ADM_LGN_01010A/")
	public String ADM_LGN_01010A(HttpServletRequest request,HttpServletResponse response,Model model) throws Exception {
		request.getSession().invalidate();
		return "redirect:/ADM_LGN_010000/";
	}
	
	/**
	 * 메인화면으로 이동
	 * */
	@RequestMapping(value="/ADM_LGN_020000/")
	public String ADM_LGN_020000(HttpServletRequest request,HttpServletResponse response,Model model) throws Exception {
		String REDIRECT_URI	= getParam(request,"REDIRECT_URI",50);
		if(REDIRECT_URI!=null && !"".equals(REDIRECT_URI)){
			response.sendRedirect(REDIRECT_URI);
			return null;
		}
		return "/ADM/main";
	}

}
