package com.kwic.web.servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kwic.util.StringUtil;
import com.kwic.web.KwicServletRequest;
import com.kwic.web.controller.Controllers;

public class SessionInterceptor extends HandlerInterceptorAdapter {
	
	public static final int FILTER_MAIN_INCLUDE	= 1;
	public static final int FILTER_MAIN_EXCLUDE	= 2;
	
	public static final String AJAX_PATTERN	= "/**/*A";
	public static final String DOWNLOAD_PATTERN	= "/**/*D";
	public static final String EXCEL_PATTERN	= "/**/*E";

	private ArrayList<String> includeUrlPattern;
    private ArrayList<String> excludeUrlPattern;
    private int filterType;
    private boolean saveParam;
	
	private PathMatcher pathMatcher = new AntPathMatcher();
	
    /** session attribute name **/
    private String atrtributeName;
    /** redirect url **/
    private String redirectUrl;

    /**
     * default constructor
     * **/
    public SessionInterceptor(){
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
        this.atrtributeName				= atrtributeName;
    }
    public void setRedirectUrl(String redirectUrl){
        this.redirectUrl				= redirectUrl;
    }
    public void setSaveParam(boolean saveParam){
        this.saveParam	= saveParam;
    }
    
    @SuppressWarnings("rawtypes")
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)throws ServletException{

        //check session --------------->
    	if(atrtributeName==null || redirectUrl==null)
    		return true;
    	
    	String url	= request.getRequestURI().toString();
    	if(url.indexOf("?")>=0)
    		url	= url.substring(0, url.indexOf("?"));
    	url	= StringUtil.replace(url,".do","");

		if(filterType==FILTER_MAIN_EXCLUDE){
			if( isExcludeUrl(url) && !isIncludeUrl(url) )
        		return true;
		}else{
			if(isExcludeUrl(url) || !isIncludeUrl(url))
        		return true;
		}
    	Object sessionAttribute	= request.getSession().getAttribute(atrtributeName);
    	
    	if(sessionAttribute!=null){
    		if(saveParam && request.getSession().getAttribute(url)!=null){
    			KwicServletRequest krequest	= new KwicServletRequest(request);
    			
    			Map map	= (Map)request.getSession().getAttribute(url);
    			request.getSession().removeAttribute(url);
    			
    			krequest.setParameterMap(map);
    			
    			request.setAttribute("SAVED_REQUEST", krequest);
    		}
    		return true;
    	}
    	
    	//ajax response
    	if(pathMatcher.match(AJAX_PATTERN, url )){
    		Map<String,Object> obj	= new HashMap<String,Object>();
			obj.put(Controllers.RESULT_CD, "N");
			obj.put(Controllers.RESULT_ERCD, Controllers.RESULT_ERCD_NOSESSION);
			obj.put(Controllers.RESULT_MSG, "작업시간 초과로 로그아웃 되었습니다.\n\n다시 로그인하여 주십시오.");
    			
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
       		//throw new ModelAndViewDefiningException(null);
    	}else if(pathMatcher.match(DOWNLOAD_PATTERN, url ) || pathMatcher.match(EXCEL_PATTERN, url )){
    		ModelAndView modelAndView	= new ModelAndView("redirect:"+redirectUrl);
    		modelAndView.addObject(Controllers.EXCEPTION, new Exception("작업시간 초과로 로그아웃 되었습니다.\n\n다시 로그인하여 주십시오."));
//    		modelAndView.addObject(Controllers.REDIRECT_URI, request.getRequestURI().toString());
    		modelAndView.addObject(Controllers.REDIRECT_TYPE, "DOWNLOAD" );
    		
    		throw new ModelAndViewDefiningException(modelAndView);
    	}else{
    		if(saveParam)
    			request.getSession().setAttribute(url, cloneParameters(request));
        	
    		ModelAndView modelAndView	= new ModelAndView("redirect:"+redirectUrl);
    		modelAndView.addObject(Controllers.EXCEPTION, new Exception("작업시간 초과로 로그아웃 되었습니다.\n\n다시 로그인하여 주십시오."));
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
    	if(url==null)
    		return false;
    	String includeUrl	= "";
    	if(includeUrlPattern==null || includeUrlPattern.size()==0)
    		return false;
    	for(int i=0;i<includeUrlPattern.size();i++){
    		if(includeUrlPattern.get(i)==null)
    			return false;
        	includeUrl	= (String)includeUrlPattern.get(i);
        	if( pathMatcher.match(includeUrl, url ) )
        		return true;
    	}
        return false;
    }
    /**
     * excludeUrlPattern 분석
     * */
    public boolean isExcludeUrl(String url){
    	if(url==null)
    		return false;
    	String excludeUrl	= "";
    	if(excludeUrlPattern==null || excludeUrlPattern.size()==0)
    		return false;
        for(int i=0;i<excludeUrlPattern.size();i++){
    		if(excludeUrlPattern.get(i)==null)
    			return false;
        	excludeUrl	= (String)excludeUrlPattern.get(i);
        	if( pathMatcher.match(excludeUrl, url ) )
        		return true;
    	}
        return false;
    } 

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public Map cloneParameters(HttpServletRequest request){
    	Map map	= request.getParameterMap();
    	
    	Map savedParametersMap	= new HashMap();
    	
		Iterator<String> itr	= map.keySet().iterator();
		String key	= null;
		String[] value = null;
		
		while(itr.hasNext()){
			key	= itr.next();
			value = map.get(key).getClass().isArray() ? (String[]) map.get(key) : new String[]{(String) map.get(key)} ;
			savedParametersMap.put(key, value);
		}
    	return savedParametersMap;
    }
}
