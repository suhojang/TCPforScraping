package com.kwic.web.servlet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.jaxen.JaxenException;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kwic.util.StringUtil;
import com.kwic.web.controller.Controllers;
import com.kwic.xml.parser.JXParser;

/**
 * 권한 감시자
 * @see dispatcher-servlet.xml
 * @author ykkim
 *
 */
public class AuthInterceptor extends HandlerInterceptorAdapter {
	
	public static final int FILTER_MAIN_INCLUDE	= 1;
	public static final int FILTER_MAIN_EXCLUDE	= 2;
	
	public static final String AJAX_PATTERN	    = "/**/*A";
	public static final String DOWNLOAD_PATTERN	= "/**/*D";
	public static final String EXCEL_PATTERN	= "/**/*E";
	private PathMatcher pathMatcher = new AntPathMatcher();
	
	/**
	 * dispatcher-servlet.xml에 저장한 mgrAuthInterceptor.includeUrlPattern 목록 참고
	 */
	private ArrayList<String> includeUrlPattern;
    
	/**
	 * dispatcher-servlet.xml에 저장한 mgrAuthInterceptor.excludeUrlPattern 목록 참고
	 */
	private ArrayList<String> excludeUrlPattern;
    
	/**
	 * dispatcher-servlet.xml에 저장한 mgrAuthInterceptor.filterType 속성값 참고
	 */
	private int filterType;
	
	/** 
	 * dispatcher-servlet.xml에 저장한 mgrAuthInterceptor.atrtributeName 참고
	 * session attribute name
	 * 관리자 추정정보 클래스의 이름을 지정한다.
	 * project.kais.record.MgrInfoRec 참고
	 */
    private String atrtributeName;
    
    /** 
     * 접근불응일 경우 돌아갈 redirect URL
     * dispatcher-servlet.xml의  mgrAuthInterceptor.redirectUrl 참고
     */
    private String redirectUrl;
    
    /** 
     * dispatcher-servlet.xml에 저장한 mgrAuthInterceptor.xmlMethodName 참고
     * 권한 xml을 획득할 수 있는 메소드명
     * project.kais.record.MgrInfoRec.getUrixml() 호출
     */
    private String xmlMethodName;
    
    /** 
     * dispatcher-servlet.xml에 저장한 mgrAuthInterceptor.grdMethodName 참고
     *  권한 등급을 획득할 수 있는 메소드명
     *  project.kais.record.MgrInfoRec.getMgrinf_grd()
     */
    private String grdMethodName;
    
    /**
     * default constructor
     * **/
    public AuthInterceptor(){
    }
    
    /**
     * 
     */
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {

    	if(atrtributeName == null || redirectUrl == null || request.getSession().getAttribute(atrtributeName) == null){
    		return true;
    	}
    	
    	String uri	= request.getRequestURI().toString();
    	if(uri.indexOf("?") >= 0){
    		uri	= uri.substring(0, uri.indexOf("?"));
    	}
    	uri	= StringUtil.replace(uri, ".do", "");
    	//권한설정 관련 url replace
    	//uri = uri.replace("'", "").replace("\"\"", "").replace("@", "").replace("[", "").replace("]", "").replace("<", "").replace(">", "");

		if(filterType == FILTER_MAIN_EXCLUDE){
			if(isExcludeUrl(uri) && !isIncludeUrl(uri)){
				return true;
			}
		}else{
			if(isExcludeUrl(uri) || !isIncludeUrl(uri)){
				return true;
			}
		}
		
    	Object sessionAttribute	= request.getSession().getAttribute(atrtributeName);
    	JXParser xml = null;
    	String grd	= null;
		String errMsg	= null;
    	if(sessionAttribute != null){
    		try{
        		Method method = sessionAttribute.getClass().getMethod(xmlMethodName);
        		xml	= (JXParser) method.invoke(sessionAttribute, new Object[]{});
        		method = sessionAttribute.getClass().getMethod(grdMethodName);
        		grd	= (String) method.invoke(sessionAttribute, new Object[]{});
				if(xml.getElement("//URI[@URI='" + uri + "' and @AUTH" + grd + "='Y']") == null){
					errMsg	= "해당 경로에 대한 접근 권한이 없습니다.\n\n다시 로그인하여 주십시오.";
				}
			} catch (IllegalAccessException e) {
				errMsg	= e.getClass().getName() + " : " + e.getMessage();
			} catch (NoSuchMethodException e) {
				errMsg	= e.getClass().getName() + " : " + e.getMessage();
			} catch (SecurityException e) {
				errMsg	= e.getClass().getName() + " : " + e.getMessage();
			} catch (IllegalArgumentException e) {
				errMsg	= e.getClass().getName() + " : " + e.getMessage();
			} catch (InvocationTargetException e) {
				errMsg	= e.getClass().getName() + " : " + e.getMessage();
			} catch (JaxenException e) {
				errMsg	= e.getClass().getName() + " : " + e.getMessage();
			}
    	}
    	if(errMsg == null){
    		return true;
    	}
    	
    	//ajax response
    	if(pathMatcher.match(AJAX_PATTERN, uri)){
    		Map<String,Object> obj = new HashMap<String,Object>();
			obj.put(Controllers.RESULT_CD,   "N");
			obj.put(Controllers.RESULT_ERCD, Controllers.RESULT_ERCD_NOAUTH);
			obj.put(Controllers.RESULT_MSG,  errMsg);
    			
	    	String jsonString = null;
	    	try{
	    		jsonString	= new ObjectMapper().writeValueAsString(obj);
		    	String callback	= request.getParameter("callback");
		    	response.setStatus(HttpServletResponse.SC_OK);
		    	response.setHeader("Content-Type", "application/json; charset=UTF-8");
				response.getWriter().append(callback==null||"".equals(callback)?"":callback).append("(").append(jsonString).append(")");
				response.getWriter().close();
	    	}catch(Exception e){
	    	}
	    	return false;

    	}else if(pathMatcher.match(DOWNLOAD_PATTERN, uri) || pathMatcher.match(EXCEL_PATTERN, uri)){
    		ModelAndView modelAndView = new ModelAndView("redirect:" + redirectUrl);
    		modelAndView.addObject(Controllers.EXCEPTION,     new Exception(errMsg));
    		modelAndView.addObject(Controllers.REDIRECT_TYPE, "DOWNLOAD" );
    		
    		throw new ModelAndViewDefiningException(modelAndView);
    	}else{
    		ModelAndView modelAndView	= new ModelAndView("redirect:"+redirectUrl);
    		modelAndView.addObject(Controllers.EXCEPTION, new Exception(errMsg));
//    		modelAndView.addObject(Controllers.REDIRECT_URI, request.getRequestURI().toString());
    		
    		throw new ModelAndViewDefiningException(modelAndView);
    	}
    }

    /**<pre>
     * Controller의 처리 직후 시행된다.
     * Controller내에서의 오류 발생 시 실행되지 않음에 유의한다.
     * </pre>
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param handler controller
     * @param modelandview ModelAndView
     * **/
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelandview)throws Exception{
    }
    
    
    /**<pre>
     * Request에 대한 모든 처리가 완료된 후 실행된다.
     * 1. session에 현재 request URI를 저장한다.
     * 2. session에 현재 servletPath(jsp 경로)를 저장한다.
     * 3. RequestHolder에 저장한 정보를 제거한다.
     * </pre>
     * @param httpservletrequest HttpServletRequest
     * @param httpservletresponse HttpServletResponse
     * @param handler controller
     * @param exception Exception
     * **/
    public void afterCompletion(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse, Object handler, Exception exception) throws Exception{
    }
    
    /**
     * includeUrlPattern 분석
     * */
    public boolean isIncludeUrl(String url){
    	if(url == null){
    		return false;
    	}
    	
    	String includeUrl	= "";
    	if(includeUrlPattern == null || includeUrlPattern.size() == 0){
    		return false;
    	}
    	for(int i=0; i<includeUrlPattern.size(); i++){
    		if(includeUrlPattern.get(i) == null){
    			return false;
    		}
        	includeUrl	= (String) includeUrlPattern.get(i);
        	if(pathMatcher.match(includeUrl, url)){
        		return true;
        	}
    	}
        return false;
    }
    
    /**
     * excludeUrlPattern 분석
     * */
    public boolean isExcludeUrl(String url){
    	if(url == null){
    		return false;
    	}
    	String excludeUrl	= "";
    	if(excludeUrlPattern == null || excludeUrlPattern.size() == 0){
    		return false;
    	}
        for(int i=0; i<excludeUrlPattern.size(); i++){
    		if(excludeUrlPattern.get(i) == null){
    			return false;
    		}
        	excludeUrl = (String) excludeUrlPattern.get(i);
        	if(pathMatcher.match(excludeUrl, url)){
        		return true;
        	}
    	}
        return false;
    } 

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public Map cloneParameters(HttpServletRequest request){
    	Map map	= request.getParameterMap();
    	Map savedParametersMap	= new HashMap();
    	
		Iterator<String> itr = map.keySet().iterator();
		String key	= null;
		String[] value = null;
		while(itr.hasNext()){
			key	= itr.next();
			value = map.get(key).getClass().isArray() ? (String[]) map.get(key) : new String[]{(String) map.get(key)} ;
			savedParametersMap.put(key, value);
		}
    	return savedParametersMap;
    }  
    
    public void setIncludeUrlPattern(ArrayList<String> includeUrlPattern){
        this.includeUrlPattern = includeUrlPattern;
    }
    
    public void setExcludeUrlPattern(ArrayList<String> excludeUrlPattern){
        this.excludeUrlPattern = excludeUrlPattern;
    }
    
    public void setFilterType(int filterType){
        this.filterType = filterType;
    }
    
    public void setAtrtributeName(String atrtributeName){
        this.atrtributeName	= atrtributeName;
    }
    
    public void setRedirectUrl(String redirectUrl){
        this.redirectUrl = redirectUrl;
    }
    
    public void setXmlMethodName(String xmlMethodName){
        this.xmlMethodName = xmlMethodName;
    }
    
    public void setGrdMethodName(String grdMethodName){
        this.grdMethodName = grdMethodName;
    }
}
