package com.kwic.web.servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 메뉴 접근 감시자
 * @see dispatcher-servlet.xml
 * @author ykkim
 *
 */
public class MenuInterceptor extends HandlerInterceptorAdapter {
	
	public static final int FILTER_MAIN_INCLUDE	= 1;
	public static final int FILTER_MAIN_EXCLUDE	= 2;
	
	public static final String AJAX_PATTERN	    = "/**/*A";
	public static final String DOWNLOAD_PATTERN	= "/**/*E";
	public static final String EXCEL_PATTERN	= "/**/*E";
	private PathMatcher pathMatcher = new AntPathMatcher();

	/**
	 * 접근허용 URL 목록
	 * dispatcher-servlet.xml의 mgrMenuInterceptor.includeUrlPattern 참고
	 */
	private ArrayList<String> includeUrlPattern;
    
	/**
	 * 접근제한 URL 목록
	 * dispatcher-servlet.xml의 mgrMenuInterceptor.excludeUrlPattern 참고 
	 */
	private ArrayList<String> excludeUrlPattern;
	
	/**
	 * 접근제한 정책 유형
	 * dispatcher-servlet.xml의 mgrMenuInterceptor.filterType 참고
	 * IF filterType=1 THEN includePattern에 해당하고,  excludeUrlPattern에 해당하지 않는 URL 처리
	 * IF filterType=2 THEN excludeUrlPattern에 해당하지 않거나, includePattern에 해당하는 URL 처리
	 */
    private int filterType;	
	
    /**
     * default constructor
     * **/
    public MenuInterceptor(){
    }
    
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException{
    	String url	= request.getRequestURI().toString();

		if(filterType == FILTER_MAIN_EXCLUDE){
			if(isExcludeUrl(url) && !isIncludeUrl(url)) {
				return true;
			}
		}else{
			if(isExcludeUrl(url) || !isIncludeUrl(url)){
				return true;
			}
		}
		
    	String uri = request.getRequestURI();
    	String menuId	= "";
    	if(uri == null || "".equals(uri) || "/".equals(uri)){
    		menuId	= "";
    	} else {
    		menuId = uri;
    		if(uri.endsWith("/")){
    			menuId	= menuId.substring(0, menuId.length() - 1);
    		}
    		menuId	= menuId.substring(menuId.lastIndexOf("/") + 1);
    	}
    	request.setAttribute("URI_ID", menuId);
    	return true;
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
        	includeUrl = (String) includeUrlPattern.get(i);
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
        for(int i=0;i <excludeUrlPattern.size(); i++){
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
}
