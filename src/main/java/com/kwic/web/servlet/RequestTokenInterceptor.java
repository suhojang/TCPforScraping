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

import com.kwic.support.CryptoKeyGenerator;
import com.kwic.util.StringUtil;
import com.kwic.web.controller.Controllers;

/**
 * 중복실행 방지 감시자
 * @see dispatcher-servlet.xml
 * @author ykkim
 */
public class RequestTokenInterceptor extends HandlerInterceptorAdapter {
	
	public static final String ATTRIBUTE_NAME	= "KWIC_REQUEST_TOKEN_ATTRIBUTE";
	
	public static final int FILTER_MAIN_INCLUDE	= 1;
	public static final int FILTER_MAIN_EXCLUDE	= 2;
	
	public static final String AJAX_PATTERN	   = "/**/*A";
	public static final String DOWNLOAD_PATTERN	= "/**/*D";
	public static final String EXCEL_PATTERN	= "/**/*E";
	private PathMatcher pathMatcher = new AntPathMatcher();

	/**
	 * 중복실행 여부 검사 대상 URL 목록
	 * dispatcher-servlet.xml의 requestTokenInterceptor.checkUrlPattern 
	 */
	private ArrayList<String> checkUrlPattern;
	
	/**
	 * 토큰 생성 URL 목록
	 * dispatcher-servlet.xml의 requestTokenInterceptor.makeTokenUrlPattern 
	 */
	private ArrayList<String> makeTokenUrlPattern;
	
    /**
     * default constructor
     * **/
    public RequestTokenInterceptor(){
    }
    
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)throws ServletException{
    	String uri	= request.getRequestURI().toString();
    	if(uri.indexOf("?") >= 0){
    		uri	= uri.substring(0, uri.indexOf("?"));
    	}
    	uri	= StringUtil.replace(uri,".do", "");

    	boolean match = true;
		if(isCheckUrl(uri)){
			String ssToken	= (String) request.getSession().getAttribute(ATTRIBUTE_NAME);
			String rqToken	= (String) request.getParameter(ATTRIBUTE_NAME);
			if(ssToken == null || !ssToken.equals(rqToken)) {
				match	= false;
			}
			request.getSession().setAttribute(ATTRIBUTE_NAME, CryptoKeyGenerator.getRandomKey(CryptoKeyGenerator.ALGORITHM_AES256, new int[]{CryptoKeyGenerator.KEY_TYPE_NUM, CryptoKeyGenerator.KEY_TYPE_ENG_CAPITAL, CryptoKeyGenerator.KEY_TYPE_ENG_SMALL}));
		}
		
		if(isMakeTokenUrlPattern(uri)) {
			request.getSession().setAttribute(ATTRIBUTE_NAME, CryptoKeyGenerator.getRandomKey(CryptoKeyGenerator.ALGORITHM_AES256, new int[]{CryptoKeyGenerator.KEY_TYPE_NUM, CryptoKeyGenerator.KEY_TYPE_ENG_CAPITAL, CryptoKeyGenerator.KEY_TYPE_ENG_SMALL}));
		}
		
		if(match){
			return true;
		}

    	//ajax response
    	if(pathMatcher.match(AJAX_PATTERN, uri)){
    		Map<String,Object> obj	= new HashMap<String,Object>();
			obj.put(Controllers.RESULT_CD,   "N");
			obj.put(Controllers.RESULT_ERCD, Controllers.RESULT_ERCD_NOSESSION);
			obj.put(Controllers.RESULT_MSG,  "사용이 만료된 요청입니다.");
    			
	    	String jsonString = null;
	    	try{
	    		jsonString	= new ObjectMapper().writeValueAsString(obj);
		    	String callback	= request.getParameter("callback");
		    	response.setStatus(HttpServletResponse.SC_OK);
		    	response.setHeader("Content-Type", "application/json; charset=UTF-8");
				response.getWriter().append(callback == null || "".equals(callback) ? "" : callback).append("(").append(jsonString).append(")");
				response.getWriter().close();
	    	}catch(Exception e){
	    	}
	    	return false;
	    	
    	}else if(pathMatcher.match(DOWNLOAD_PATTERN, uri) || pathMatcher.match(EXCEL_PATTERN, uri)){
    		ModelAndView modelAndView	= new ModelAndView();
    		modelAndView.addObject(Controllers.EXCEPTION, new Exception("사용이 만료된 요청입니다."));
    		throw new ModelAndViewDefiningException(modelAndView);
    	}else{
    		ModelAndView modelAndView	= new ModelAndView();
    		modelAndView.addObject(Controllers.EXCEPTION, new Exception("사용이 만료된 요청입니다."));
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
     * checkUrlPattern 분석
     * */
    public boolean isCheckUrl(String url){
    	if(url == null){
    		return false;
    	}
    	
    	String checkUrl	= "";
    	if(checkUrlPattern == null || checkUrlPattern.size() == 0){
    		return false;
    	}
    	
        for(int i=0; i<checkUrlPattern.size(); i++){
    		if(checkUrlPattern.get(i) == null){
    			return false;
    		}
    		checkUrl = (String) checkUrlPattern.get(i);
        	if(pathMatcher.match(checkUrl, url)){
        		return true;
        	}
    	}
        return false;
    } 
    
    /**
     * makeTokenUrlPattern 분석
     * */
    public boolean isMakeTokenUrlPattern(String url){
    	if(url == null){
    		return false;
    	}
    	
    	String checkUrl	= "";
    	if(makeTokenUrlPattern == null || makeTokenUrlPattern.size() == 0){
    		return false;
    	}
    	
        for(int i=0; i<makeTokenUrlPattern.size(); i++){
    		if(makeTokenUrlPattern.get(i) == null){
    			return false;
    		}
    		checkUrl = (String) makeTokenUrlPattern.get(i);
        	if(pathMatcher.match(checkUrl, url)){
        		return true;
        	}
    	}
        return false;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public Map cloneParameters(HttpServletRequest request){
    	Map map	= request.getParameterMap();
    	
    	Map savedParametersMap = new HashMap();
    	
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
    
    public void setCheckUrlPattern(ArrayList<String> checkUrlPattern){
        this.checkUrlPattern = checkUrlPattern;
    }
    
    public void setMakeTokenUrlPattern(ArrayList<String> makeTokenUrlPattern){
        this.makeTokenUrlPattern = makeTokenUrlPattern;
    }
}
