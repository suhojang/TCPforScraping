<%@page contentType="text/html;charset=utf-8"%><%@
page import="java.net.URLEncoder"%><%@
page import="java.net.URLEncoder"%><%@
page import="java.io.File"%><%@
page import="java.io.FileInputStream"%><%@
page import="com.kwic.io.JOutputStream"%><%
out.clearBuffer();

String projectPath	= "C:/Program Files/Apache Software Foundation/Tomcat 7.0/webapps/ROOT/";
//String projectPath	= "E:/eGovFrame/eGovFrameDev-3.5.0-64bit/workspace/PrintViewer/WebContent/"

request.setCharacterEncoding("UTF-8");

String filename	= request.getParameter("filename");

response.setHeader("Content-Disposition","attachment;filename="+URLEncoder.encode(filename+".pdf","UTF-8")+";");
response.setHeader("Pragma","dummy=bogus");
response.setHeader("Cache-Control","private");
//response.setContentType("application/octet-stream;charset=UTF-8");
response.setContentType("application/pdf;charset=UTF-8");

FileInputStream fis	= null;
JOutputStream jos	= null;
try{
	fis	= new FileInputStream(new File(projectPath+"WEB-INF/html2pdf/"+filename+".pdf"));

	jos	= new JOutputStream();
	jos.write(fis);
	
}catch(Exception e){
	e.printStackTrace();
}finally{
	try{if(fis!=null)fis.close();}catch(Exception e){}
	try{if(jos!=null)jos.close();}catch(Exception e){}
}

response.getOutputStream().write(jos.getBytes()==null?new byte[0]:jos.getBytes());
response.getOutputStream().flush();
response.getOutputStream().close();

%>