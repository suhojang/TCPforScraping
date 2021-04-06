package com.kwic.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.kwic.util.KEnumeration;

public class KwicServletRequest implements HttpServletRequest{
	private Map<String,String[]> paramMap	= new Hashtable<String,String[]>();
	private HttpServletRequest request;
	public KwicServletRequest(HttpServletRequest request){
		this.request	= request;
	}

	@Override
	public Object getAttribute(String name) {
		return request.getAttribute(name);
	}

	@Override
	public Enumeration<?> getAttributeNames() {
		return request.getAttributeNames();
	}

	@Override
	public String getCharacterEncoding() {
		return request.getCharacterEncoding();
	}

	@Override
	public void setCharacterEncoding(String env)
			throws UnsupportedEncodingException {
		request.setCharacterEncoding(env);
	}

	@Override
	public int getContentLength() {
		return request.getContentLength();
	}

	@Override
	public String getContentType() {
		return request.getContentType();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return request.getInputStream();
	}

	@Override
	public String getParameter(String name) {
		return request.getParameter(name)==null?paramMap.get(name)[0]:request.getParameter(name);
	}
	public void setParameter(String name,String value){
		paramMap.put(name, new String[]{value});
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setParameterMap(Map map){
		Iterator<String> itr	= map.keySet().iterator();
		String key	= null;
		while(itr.hasNext()){
			key	= itr.next();
			paramMap.put(key, (map.get(key).getClass().isArray()? (String[])map.get(key):new String[]{(String)map.get(key)} ));
		}
	}
	
	@Override
	public Enumeration<?> getParameterNames() {
		KEnumeration ke	= new KEnumeration();
		Enumeration<?> em	= request.getParameterNames();
		while(em.hasMoreElements())
			ke.add(em.nextElement());
		
		Iterator<String> itr	= paramMap.keySet().iterator();
		while(itr.hasNext())
			ke.add(itr.next());
		
		return ke;
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] ovalues	= request.getParameterValues(name);
		String[] nvalues	= new String[ovalues.length+paramMap.size()];
		System.arraycopy(ovalues, 0, nvalues, 0, ovalues.length);
		
		int idx	= ovalues.length;
		Iterator<String> itr	= paramMap.keySet().iterator();
		while(itr.hasNext())
			nvalues[idx]	= paramMap.get(itr.next())[0];

		return nvalues;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map getParameterMap() {
		Map map	= request.getParameterMap();
		
		Iterator<String> itr	= paramMap.keySet().iterator();
		Object key	= null;
		while(itr.hasNext())
			key	= itr.next();
			map.put(key, paramMap.get(key));
		
		return map;
	}

	@Override
	public String getProtocol() {
		return request.getProtocol();
	}

	@Override
	public String getScheme() {
		return request.getScheme();
	}

	@Override
	public String getServerName() {
		return request.getServerName();
	}

	@Override
	public int getServerPort() {
		return request.getServerPort();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return request.getReader();
	}

	@Override
	public String getRemoteAddr() {
		return request.getRemoteAddr();
	}

	@Override
	public String getRemoteHost() {
		return request.getRemoteHost();
	}

	@Override
	public void setAttribute(String name, Object o) {
		request.setAttribute(name, o);
	}

	@Override
	public void removeAttribute(String name) {
		request.removeAttribute(name);
	}

	@Override
	public Locale getLocale() {
		return request.getLocale();
	}

	@Override
	public Enumeration<?> getLocales() {
		return request.getLocales();
	}

	@Override
	public boolean isSecure() {
		return request.isSecure();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		return request.getRequestDispatcher(path);
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getRealPath(String path) {
		return request.getRealPath(path);
	}

	@Override
	public int getRemotePort() {
		return request.getRemotePort();
	}

	@Override
	public String getLocalName() {
		return request.getLocalName();
	}

	@Override
	public String getLocalAddr() {
		return request.getLocalAddr();
	}

	@Override
	public int getLocalPort() {
		return request.getLocalPort();
	}

	@Override
	public String getAuthType() {
		return request.getAuthType();
	}

	@Override
	public Cookie[] getCookies() {
		return request.getCookies();
	}

	@Override
	public long getDateHeader(String name) {
		return request.getDateHeader(name);
	}

	@Override
	public String getHeader(String name) {
		return request.getHeader(name);
	}

	@Override
	public Enumeration<?> getHeaders(String name) {
		return request.getHeaders(name);
	}

	@Override
	public Enumeration<?> getHeaderNames() {
		return request.getHeaderNames();
	}

	@Override
	public int getIntHeader(String name) {
		return request.getIntHeader(name);
	}

	@Override
	public String getMethod() {
		return request.getMethod();
	}

	@Override
	public String getPathInfo() {
		return request.getPathInfo();
	}

	@Override
	public String getPathTranslated() {
		return request.getPathTranslated();
	}

	@Override
	public String getContextPath() {
		return request.getContextPath();
	}

	@Override
	public String getQueryString() {
		return request.getQueryString();
	}

	@Override
	public String getRemoteUser() {
		return request.getRemoteUser();
	}

	@Override
	public boolean isUserInRole(String role) {
		return request.isUserInRole(role);
	}

	@Override
	public Principal getUserPrincipal() {
		return request.getUserPrincipal();
	}

	@Override
	public String getRequestedSessionId() {
		return request.getRequestedSessionId();
	}

	@Override
	public String getRequestURI() {
		return request.getRequestURI();
	}

	@Override
	public StringBuffer getRequestURL() {
		return request.getRequestURL();
	}

	@Override
	public String getServletPath() {
		return request.getServletPath();
	}

	@Override
	public HttpSession getSession(boolean create) {
		return request.getSession(create);
	}

	@Override
	public HttpSession getSession() {
		return request.getSession();
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return request.isRequestedSessionIdValid();
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return request.isRequestedSessionIdFromCookie();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isRequestedSessionIdFromURL() {
		return request.isRequestedSessionIdFromUrl();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isRequestedSessionIdFromUrl() {
		return request.isRequestedSessionIdFromUrl();
	}
}
