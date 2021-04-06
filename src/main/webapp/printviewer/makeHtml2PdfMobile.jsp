<%@page contentType="text/html;charset=utf-8"%><%@
page import="java.net.URLEncoder"%><%@
page import="java.net.URLDecoder"%><%@
page import="com.kwic.util.pdf.Html2Pdf"%><%@
page import="java.util.Map"%><%@
page import="java.util.HashMap"%><%@
page import="java.util.Calendar"%><%@
page import="java.io.File"%><%@
page import="java.io.FileOutputStream"%><%@
page import="org.codehaus.jackson.map.ObjectMapper"
%><%
out.clearBuffer();

String projectPath	= "C:/Program Files/Apache Software Foundation/Tomcat 7.0/webapps/ROOT/";
//String projectPath	= "E:/eGovFrame/eGovFrameDev-3.5.0-64bit/workspace/PrintViewer/WebContent/"

Map<String,String> param	= new HashMap<String,String>();
FileOutputStream fos	= null;
try{
	request.setCharacterEncoding("UTF-8");
	
	String htmlString	= URLDecoder.decode(request.getParameter("PDF_HTML"));
	String baseURL		= URLDecoder.decode(request.getParameter("BASE_URL"));
	String cssPath		= projectPath+"print/css/JPrintViewer3.0.css";
	String fontPath		= projectPath+"print/font/MALGUN.TTF";
	String fontAlias	= "MalgunGothic";
	
	String filename	= String.valueOf(Calendar.getInstance().getTimeInMillis());
	String path		= projectPath+"WEB-INF/html2pdf/";
	
	if(!new File(path).exists()){
		new File(path).mkdirs();
	}
	fos	= new FileOutputStream(new File(path+"/"+filename+".pdf"));
	
	Html2Pdf.parse(fos, htmlString,fontPath,baseURL);
	
	param.put("RESULT", "Y");
	param.put("filename", filename);
	
}catch(Exception e){
	param.put("RESULT", "N");
	param.put("MESSAGE", e.getMessage());
	e.printStackTrace();
}finally{
	try{if(fos!=null)fos.close();}catch(Exception e){}
}

String jsonString = new ObjectMapper().writeValueAsString(param);
String callback	= request.getParameter("callback");
response.setStatus(HttpServletResponse.SC_OK);
response.setHeader("Content-Type", "application/json; charset=UTF-8");

response.getWriter().append(callback==null||"".equals(callback)?"":callback).append("(").append(jsonString).append(")");
response.getWriter().close();

%>