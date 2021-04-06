package com.kwic.web.servlet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
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

import com.kwic.log.Tracker;
import com.kwic.security.aes.AESCipher;
import com.kwic.util.StringUtil;
import com.kwic.web.controller.Controllers;

/**
 * 관리자 이력 감시자
 * @see dispatcher-servlet.xml
 * @author ykkim
 *
 */
public class ActionTracker extends HandlerInterceptorAdapter{
	
	public static final int FILTER_MAIN_INCLUDE	= 1;
	public static final int FILTER_MAIN_EXCLUDE	= 2;
	
	public static final String AJAX_PATTERN	    = "/**/*A";
	public static final String DOWNLOAD_PATTERN	= "/**/*D";
	public static final String EXCEL_PATTERN	= "/**/*E";
	private PathMatcher pathMatcher = new AntPathMatcher();

	/**
	 * 접근허용 URL 목록
	 * dispatcher-servlet.xml의 mgrActionTracker.includeUrlPattern 참고
	 */
	private ArrayList<String> includeUrlPattern;
	
	/**
	 * 접근제한 URL 목록
	 * dispatcher-servlet.xml의 mgrActionTracker.excludeUrlPattern 참고
	 */
    private ArrayList<String> excludeUrlPattern;
    
    /**
     * 접근제한 정책 유형
	 * dispatcher-servlet.xml의 mgrActionTracker.filterType 참고
	 * IF filterType=1 THEN includePattern에 해당하고,  excludeUrlPattern에 해당하지 않는 URL 처리
	 * IF filterType=2 THEN excludeUrlPattern에 해당하지 않거나, includePattern에 해당하는 URL 처리
     */
    private int filterType;
	
	
    /** 
     * 관리자 이력 추정 정보 클래스의 이름
     * session attribute name
	 * dispatcher-servlet.xml의 mgrActionTracker.atrtributeName 참고
     * project.kais.record.MgrInfoRec 참고
     */
    private String atrtributeName;
    
    /** 권한 등급을 획득할 수 있는 메소드명   
	 * dispatcher-servlet.xml의 mgrActionTracker.methodName 참고
     * project.kais.record.MgrInfoRec.getMgrinf_id() 참고
     */
    private String methodName;
    
    /** 
     * tracker id  
	 * dispatcher-servlet.xml의 mgrActionTracker.id 참고
     */
    private String id;
    
    /** 
     * 암호화 파라미터 목록
	 * dispatcher-servlet.xml의 mgrActionTracker.encryptParams 참고 (,로 구분한 문자열)
     */
    private String encryptParams;
    
    /**
     * default constructor
     * **/
    public ActionTracker(){
    }
       
    /**
     * 
     */
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException{
    	String uri	= request.getRequestURI().toString();
    	if(uri.indexOf("?") >= 0){
    		uri	= uri.substring(0, uri.indexOf("?"));
    	}
    	uri	= StringUtil.replace(uri, ".do", "");

		if(filterType == FILTER_MAIN_EXCLUDE){
			if(isExcludeUrl(uri) && !isIncludeUrl(uri)){
				return true;
			}
		}else{
			if(isExcludeUrl(uri) || !isIncludeUrl(uri)){
				return true;
			}
		}
    	StringBuffer sb	= new StringBuffer();
    	sb.append(" ").append(Controllers.getRemoteIP(request)).append(" ").append(uri).append(" - ");
		
		if(atrtributeName != null && request.getSession().getAttribute(atrtributeName) != null){
	    	Object sessionAttribute	= request.getSession().getAttribute(atrtributeName);
	    	String id = null;
	    	if(sessionAttribute != null){
	    		try{
	        		Method method = sessionAttribute.getClass().getMethod(methodName);
	        		id	= (String) method.invoke(sessionAttribute, new Object[]{});
				} catch (Exception e) {
					//권한등급을 확인할 수 있는 메서드명을 확인할 수 없습니다.
				}
	    	}
	    	sb.append("[").append(id).append("] ");
		}
		
		Enumeration<?> enumeration	= request.getParameterNames();
		String key = null;
		String value = null;
		sb.append("{");
		while(enumeration.hasMoreElements()){
			if(key != null){
				sb.append(",");
			}
			key	= String.valueOf(enumeration.nextElement());
			
			try{
				if(encryptParams.indexOf(","+key+",")>=0) {
					value = AESCipher.encode(request.getParameter(key), AESCipher.DEFAULT_KEY, AESCipher.TYPE_256);
					sb.append("\"").append(key).append("\"").append(":").append("\"").append(value).append("\"");
				} else {
					value = request.getParameter(key);
					sb.append("\"").append(key).append("\"").append(":").append("\"").append(value).append("\"");
				}
			}catch(Exception ex){
				//key에 해당하는 값을 찾을 수 없습니다.
			}
		}
		sb.append("}");
		
		Tracker.getLogger(id).track(sb.toString());
		
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
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) throws Exception{
    }
    
    /**
     * includeUrlPattern 분석
     * */
    public boolean isIncludeUrl(String url){
    	if(url == null){
    		return false;
    	}
    	
    	String includeUrl	= "";
    	if(includeUrlPattern == null || includeUrlPattern.size() == 0) {
    		return false;
    	}
    	for(int i=0; i<includeUrlPattern.size(); i++){
    		if(includeUrlPattern.get(i) == null){
    			return false;
    		}
        	includeUrl = (String) includeUrlPattern.get(i);
        	if(pathMatcher.match(includeUrl, url)) {
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
    	
    	String excludeUrl = "";
    	if(excludeUrlPattern == null || excludeUrlPattern.size() == 0){
    		return false;
    	}
        for(int i=0; i<excludeUrlPattern.size(); i++){
    		if(excludeUrlPattern.get(i) == null) {
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
    
    public void setId(String id){
        this.id	= id;
    }
    
    public void setPath(String path){
        Tracker.getLogger(id).setPath(path);
    }
    
    public void setMethodName(String methodName){
        this.methodName	= methodName;
    }
    
    public void setEncryptParams(String encryptParams){
        this.encryptParams	= "," + encryptParams + ",";
    }
}
