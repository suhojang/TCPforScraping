package project.kais.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import project.kais.service.PublicService;

import com.kwic.exception.DefinedException;
import com.kwic.web.controller.Controllers;

import egovframework.rte.fdl.property.EgovPropertyService;

@Controller
public class ADM_CFG_Controller extends Controllers{

	@Resource(name="propertiesService")
	private EgovPropertyService properties;
	
	/** service */
	@Resource(name = "PublicService")
	private PublicService servicePub;

	/**
	 * 설정변경 화면
	 * 
	 * */
	@RequestMapping(value="/ADM_CFG_010000/")
	public String ADM_CFG_010000(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		return "/ADM/CFG/ADM_CFG_010000";
	}
	
	/**
	 * 설정 조회
	 * 
	 * */
	@RequestMapping(value="/ADM_CFG_S1000A/")
	public void ADM_CFG_S1100A(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		try{
			String CFG_PATH	= getParam(request,"CFG_PATH","설정파일 경로가",true,200);
			if(!new File(CFG_PATH).exists())
				throw new DefinedException("Could not find file ["+CFG_PATH+"].");
				
			BufferedReader br	= null;
			String line	= null;
			StringBuffer sb	= new StringBuffer();
			try{
				br	= new BufferedReader(new InputStreamReader(new FileInputStream(new File(CFG_PATH))));
				
				while((line=br.readLine())!=null){
					sb.append(line).append(System.getProperty("line.separator"));
				}
			}catch(Exception e){
				throw e;
			}finally{
				try{if(br!=null)br.close();}catch(Exception e){}
			}
			
			Map<String,Object> ajax	= new HashMap<String,Object>();
			ajax.put("CFG_TEXT", sb.toString());
			
			ajaxResponse(request,response,ajax);//정상
		
		}catch(Exception e){
			ajaxResponse(request,response,e);//오류
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 설정 저장
	 * 
	 * */
	@RequestMapping(value="/ADM_CFG_U1000A/")
	public void ADM_CFG_U1020A(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {
		try{
			String CFG_PATH	= getParam(request,"CFG_PATH","설정파일 경로가",true,200);
			String CFG_TEXT	= request.getParameter("CFG_TEXT");
			if(CFG_TEXT==null || "".equals(CFG_TEXT))
				throw new Exception("설정파일 내용이 입력되지 않았습니다.");
			
			FileWriter fw	= null;
			try{
				fw	= new FileWriter(new File(CFG_PATH));
				fw.write(CFG_TEXT);
			}catch(Exception e){
				throw e;
			}finally{
				try{if(fw!=null)fw.close();}catch(Exception e){}
			}
			
			ajaxResponse(request,response);//정상
		
		}catch(Exception e){
			ajaxResponse(request,response,e);//오류
			logger.error(e.getMessage(), e);
		}
	}

}
