<%@ page contentType="text/html;charset=UTF-8" errorPage = "/WEB-INF/jsp/common/error.jsp"%><%@
 include file="/WEB-INF/jsp/project/kais/ADM/include/declare.jspf" %><%@
 include file="/WEB-INF/jsp/project/kais/ADM/include/session.jspf" %><%@
 include file="/WEB-INF/jsp/project/kais/ADM/include/header.jspf" %><%@
 include file="/WEB-INF/jsp/project/kais/ADM/include/error.jspf" %><%
try{
	//모니터링 화면으로 이동
	response.sendRedirect("/ADM_PRS_010000/");
}catch(Exception e){
	e.printStackTrace();
}
%>