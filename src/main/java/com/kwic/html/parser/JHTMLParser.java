package com.kwic.html.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.FormControl;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

/**
 * <pre>
 * Title		: JHTMLParser
 * Description	: HTML Parser
 * Date			: 
 * Copyright	: Copyright	(c)	2012
 * Company		: KWIC.
 * 
 *    수정일                  수정자                      수정내용
 * -------------------------------------------
 * 
 * </pre>
 *
 * @author 장정훈
 * @version	
 * @since 
 */
public class JHTMLParser {
	private Source src;
	
	public JHTMLParser(String htmlString){
		src	= new Source(htmlString);
	}
	public JHTMLParser(String filePath,boolean isURL) throws FileNotFoundException, IOException{
		src	= new Source(new FileInputStream(new File(filePath)));
	}
	public List<Element> getElementsByTagName(String tagName){
		return src.getAllElements(tagName);
	}
	public Element getElementById(String id){
		return src.getElementById(id);
	}
	
	public List<FormControl> getFormControl(String name) throws Exception{
		List<FormControl> rsts	= new ArrayList<FormControl>();
		
		List<FormControl> forms	= src.getFormControls();
		for(int i=0;i<forms.size();i++){
			if(forms.get(i)==null || forms.get(i).getAttributesMap()==null || forms.get(i).getAttributesMap().get("name")==null)
				continue;
			if(forms.get(i).getAttributesMap().get("name").equals(name)){
				rsts.add(forms.get(i));
			}
		}
		if(rsts.size()<1)
			throw new Exception("[name='"+name+"']에 해당하는 ELEMENT가 없습니다.");

		return rsts;
	}
	
	public List<FormControl> getFormControlStartsWith(String name) throws Exception{
		List<FormControl> rsts	= new ArrayList<FormControl>();
		
		List<FormControl> forms	= src.getFormControls();
		for(int i=0;i<forms.size();i++){
			if(forms.get(i)==null || forms.get(i).getAttributesMap()==null || forms.get(i).getAttributesMap().get("name")==null)
				continue;
			if(forms.get(i).getAttributesMap().get("name").startsWith(name)){
				rsts.add(forms.get(i));
			}
		}
		if(rsts.size()<1)
			throw new Exception("[name='"+name+"...']에 해당하는 ELEMENT가 없습니다.");

		return rsts;
	}
	public List<FormControl> getFormControlEndsWith(String name) throws Exception{
		List<FormControl> rsts	= new ArrayList<FormControl>();
		
		List<FormControl> forms	= src.getFormControls();
		for(int i=0;i<forms.size();i++){
			if(forms.get(i)==null || forms.get(i).getAttributesMap()==null || forms.get(i).getAttributesMap().get("name")==null)
				continue;
			if(forms.get(i).getAttributesMap().get("name").endsWith(name)){
				rsts.add(forms.get(i));
			}
		}
		if(rsts.size()<1)
			throw new Exception("[name='..."+name+"']에 해당하는 ELEMENT가 없습니다.");

		return rsts;
	}

	public String getFormValue(String elementName) throws Exception{
		return getFormValue(elementName,0);
	}
	
	public String getFormValue(String elementName,int idx) throws Exception{
		List<FormControl> list	= getFormControl(elementName);
		if(list.size()<=idx)
			throw new Exception("[name='"+elementName+"']의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		
		FormControl control	= list.get(idx);
		return control.getAttributesMap().get("value");
	}
	
	public String getFormValue(FormControl control) throws Exception{
		return control.getAttributesMap().get("value");
	}
	
	public String getFormType(String elementName) throws Exception{
		return getFormType(elementName,0);
	}
	public String getFormType(String elementName,int idx) throws Exception{
		List<FormControl> list	= getFormControl(elementName);
		if(list.size()<=idx)
			throw new Exception("[name='"+elementName+"']의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");

		FormControl control	= list.get(idx);
		return control.getAttributesMap().get("type");
	}
	public String getFormAttribute(String elementName,String attrName) throws Exception{
		return getFormAttribute(elementName,0,attrName);
	}
	public String getFormAttribute(String elementName,int idx,String attrName) throws Exception{
		List<FormControl> list	= getFormControl(elementName);
		if(list.size()<=idx)
			throw new Exception("[name='"+elementName+"']의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");

		FormControl control	= list.get(idx);
		return control.getAttributesMap().get(attrName);
	}
	public String getTdValue(int tbIdx,int trIdx,int tdIdx) throws Exception{
		List<Element> list	= src.getAllElements(HTMLElementName.TABLE);
		if(list.size()<=tbIdx)
			throw new Exception("TABLE의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		Element table	= list.get(tbIdx);
		
		list	= table.getAllElements(HTMLElementName.TR);
		if(list.size()<=trIdx)
			throw new Exception("TABLE["+tbIdx+"]-TR의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		Element tr	= list.get(trIdx);

		list	= tr.getAllElements(HTMLElementName.TD);
		if(list.size()<=tdIdx)
			throw new Exception("TABLE["+tbIdx+"]-TR["+trIdx+"]-TD의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		Element td	= list.get(tdIdx);
		
		return td.getContent().toString();
	}
	
	public String getTdValue(Element tr,int tdIdx) throws Exception{
		List<Element> list	= tr.getAllElements(HTMLElementName.TD);
		if(list.size()<=tdIdx)
			throw new Exception("TD의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		Element td	= list.get(tdIdx);
		
		return td.getContent().toString();
	}

	public List<Element> getTRList(int tbIdx) throws Exception{
		List<Element> list	= src.getAllElements(HTMLElementName.TABLE);
		if(list.size()<=tbIdx)
			throw new Exception("TABLE의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		Element table	= list.get(tbIdx);
		
		list	= table.getAllElements(HTMLElementName.TR);
		
		return list;
	}
	
	public String getTdValue(String tbId,int trIdx,int tdIdx) throws Exception{
		Element table	= getElementById(tbId);

		List<Element> list	= table.getAllElements(HTMLElementName.TR);
		if(list.size()<=trIdx)
			throw new Exception("TABLE[id='"+tbId+"']-TR의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		Element tr	= list.get(trIdx);
		
		list	= tr.getAllElements(HTMLElementName.TD);
		if(list.size()<=tdIdx)
			throw new Exception("TABLE[id='"+tbId+"']-TR["+trIdx+"]-TD의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		Element td	= list.get(tdIdx);
		
		return td.getContent().toString();
	}
	public String getTdValue(String trId,int tdIdx) throws Exception{
		Element tr	= getElementById(trId);

		List<Element> list	= tr.getAllElements(HTMLElementName.TD);
		if(list.size()<=tdIdx)
			throw new Exception("TR[id='"+trId+"']-TD의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		
		Element td	= tr.getAllElements(HTMLElementName.TD).get(tdIdx);
		return td.getContent().toString();
	}
	public String getTdValue(String tdId){
		Element td	= getElementById(tdId);
		return td.getContent().toString();
	}
	
	public static void main(String[] args) throws Exception{
		StringBuffer sb	= new StringBuffer();
		sb.append("")
		.append("")
		.append("<html>")
		.append("<head>")
		.append("<title>기웅정보통신 로그인</title>")
		.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\">")
		.append("<link rel=\"stylesheet\" href=\"../common/css/css_page.css\" type=\"text/css\">")
		.append("<SCRIPT src=\"../common/js/common.js\"></SCRIPT>")
		.append("</head>")
		.append("<SCRIPT>")
		.append("<!--")
		.append("	function CheckEnter(){")
		.append("        if(event.keyCode==13){CheckFrom(document.form);}")
		.append("	}")
		.append("")
		.append("	function CheckFrom(theForm){")
		.append("		if(theForm.userid.value==''){")
		.append("		    alert('아이디를 입력해주십시요!');")
		.append("		    theForm.userid.focus();")
		.append("		    return;")
		.append("		}else if(theForm.password34.value==''){")
		.append("		    alert('비밀번호를 입력해주십시요!');")
		.append("		    theForm.password34.focus();")
		.append("		    return;")
		.append("		}")
		.append("		form.submit();")
		.append("	}")
		.append("")
		.append("    function Form_setting(){")
		.append("")
		.append("    }")
		.append("-->")
		.append("</SCRIPT>")
		.append("<body onload=\"javascript:Form_setting();\" leftmargin=\"0\" topmargin=\"0\" rightmargin=\"0\" bottommargin=\"0\">")
		.append("<FORM name=form action=\"./loginBiz.asp\" method=post>")
		.append("<!-- -->")
		.append("")
		.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" height=\"100%\" width=\"100%\">")
		.append("<tr>")
		.append("	<td valign=\"top\" align=\"left\" background=\"./img/bg01.gif\">")
		.append("")
		.append("	<!-- -->")
		.append("	<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" height=\"100%\" width=\"100%\">")
		.append("	<tr>")
		.append("		<td height=\"100%\" valign=\"top\" align=\"left\" background=\"./img/bg01.gif\">")
		.append("")
		.append("		<!-- -->")
		.append("		<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" height=\"570\" width=\"1024\">")
		.append("		<tr>")
		.append("			<td height=\"235\" valign=\"top\"><img src=\"./img/img01.gif\" width=\"1024\" height=\"235\" border=\"0\"></td>")
		.append("		</tr>")
		.append("		<tr>")
		.append("			<td height=\"282\" valign=\"top\">")
		.append("")
		.append("			<!-- -->")
		.append("			<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">")
		.append("			<tr>")
		.append("				<td width=\"277\" rowspan=\"3\"><img src=\"./img/img02.gif\" width=\"277\" height=\"282\" border=\"0\"></td>")
		.append("				<td width=\"533\" valign=\"top\"><img src=\"./img/img05.gif\" width=\"533\" height=\"172\" border=\"0\"></td>")
		.append("				<td width=\"214\" rowspan=\"3\"><img src=\"./img/img03.gif\" width=\"214\" height=\"282\" border=\"0\"></td>")
		.append("			</tr>")
		.append("			<tr>")
		.append("				<td height=\"63\">")
		.append("		<!-- 로그인 박스 시작 -->")
		.append("			<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">")
		.append("				<tr>")
		.append("					<td width=\"211\"><img src=\"./img/img07.gif\" width=\"211\" height=\"63\" border=\"0\"></td>")
		.append("					<td width=\"161\" background=\"./img/img09.gif\">")
		.append("					<input type=\"text\" name=\"userid\" class=\"input\" style=\"font-family:Dotum;font-size:12px;color:#666666;background-color:#FFFFFF;border-width:1px;border-color:#b6b6b7;border-style:solid;height:24px;width:150px;\" onMouseOver=\"this.style.backgroundColor='#EAECED'\"  onMouseOut=\"this.style.backgroundColor='#ffffff'\" onKeyUp=\"sendFocus()\">")
		.append("					<br>")
		.append("					<input type=\"password\" name=\"password34\" class=\"input\" style=\"font-family:Dotum;font-size:12px;color:#666666;background-color:#FFFFFF;border-width:1px;border-color:#b6b6b7;border-style:solid;height:24px;width:150px;\"  onMouseOver=\"this.style.backgroundColor='#EAECED'\"  onMouseOut=\"this.style.backgroundColor='#ffffff'\" onKeyUp=\"CheckEnter()\">")
		.append("					</td>")
		.append("					<td width=\"66\"><a href=\"javascript:CheckFrom(form)\"><img src=\"./img/btn99.gif\" width=\"66\" height=\"63\" border=\"0\"></a></td>")
		.append("					<td width=\"95\"><img src=\"./img/img08.gif\" width=\"95\" height=\"63\" border=\"0\"></td>")
		.append("				</tr>")
		.append("			</table>")
		.append("			<SCRIPT>")
		.append("				form.userid.focus()")
		.append("			</SCRIPT>")
		.append("			<!-- -->")
		.append("")
		.append("			</td>")
		.append("			</tr>")
		.append("			<tr>")
		.append("				<td><img src=\"./img/img06.gif\" width=\"533\" height=\"47\" border=\"0\"></td>")
		.append("			</tr>")
		.append("			</table>")
		.append("			<!-- -->")
		.append("")
		.append("			</td>")
		.append("		</tr>")
		.append("		<tr>")
		.append("			<td height=\"100\" valign=\"top\"><img src=\"./img/img04.gif\" width=\"1024\" height=\"100\" border=\"0\"></td>")
		.append("		</tr>")
		.append("		</table>")
		.append("		<!-- -->")
		.append("")
		.append("		</td>")
		.append("	</tr>")
		.append("	<tr>")
		.append("		<td height=\"100%\"><img src=\"./img/img10.gif\" width=\"1024\" height=\"68\" border=\"0\"></td>")
		.append("	</tr>")
		.append("	</table>")
		.append("	<!-- -->")
		.append("")
		.append("	</td>")
		.append("</tr>")
		.append("</table>")
		.append("<!-- -->")
		.append("")
		.append("</body>")
		.append("</html>")
		.append("")
		.append("")
		;
		
		//JHTMLParser parser	= new JHTMLParser("http://groupware.kwic.co.kr/login/login.asp",true);
		JHTMLParser parser	= new JHTMLParser(sb.toString());
		System.out.println(parser.getFormAttribute("password34","type"));
		System.out.println(parser.getElementsByTagName("td").size());
	}
	
}
