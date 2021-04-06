<%@page contentType="text/html;charset=utf-8"%><%@
page import="java.net.URLEncoder"%><%@
page import="java.net.URLDecoder"%><%@
page import="com.kwic.util.pdf.Html2Pdf"%><%
out.clearBuffer();

String projectPath	= "C:/Program Files/Apache Software Foundation/Tomcat 7.0/webapps/ROOT/";
//String projectPath	= "E:/eGovFrame/eGovFrameDev-3.5.0-64bit/workspace/PrintViewer/WebContent/"

request.setCharacterEncoding("UTF-8");

response.setHeader("Content-Disposition","attachment;filename="+URLEncoder.encode("샘플PDF.pdf","UTF-8")+";");
response.setHeader("Pragma","dummy=bogus");
response.setHeader("Cache-Control","private");
//response.setContentType("application/octet-stream;charset=UTF-8");
response.setContentType("application/pdf;charset=UTF-8");

//response.setHeader("Content-Transper-Encoding", "binary");
//response.setHeader("Content-Disposition", "inline; filename=" + fname + ".pdf");

String htmlString	= URLDecoder.decode(request.getParameter("PDF_HTML"));
String baseURL		= URLDecoder.decode(request.getParameter("BASE_URL"));
String cssPath		= projectPath+"print/css/JPrintViewer3.0.css";
String fontPath		= projectPath+"print/font/MALGUN.TTF";
String fontAlias	= "MalgunGothic";


System.out.println(htmlString);
//Html2Pdf.parse(response.getOutputStream(), htmlString, new String[]{cssPath}, fontPath, fontAlias);

Html2Pdf.parse(response.getOutputStream(), htmlString,fontPath,baseURL);



%>