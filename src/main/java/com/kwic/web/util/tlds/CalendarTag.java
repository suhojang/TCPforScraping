package com.kwic.web.util.tlds;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

/**
 *<pre>
 *jsp 에서 달력 생성
 *  [사용 예]
 *      <kwic:select id="RCVDT_FR" value="20150721" add="class='form' style='width:200px;'"/>
 * 
 *   수정일         수정자                   수정내용
 *  -------        --------    ---------------------------
 *  2015. 09. 18    장정훈        최초작성
 * </pre>
 * @author 기웅정보통신
 * @since 2015. 09. 18
 * @version 1.0
 * @see TagSupport
 * @since 1.6
 */
public class CalendarTag extends TagSupport {
	/** serial version UID **/
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String value;
    private String add;
    
    public void setId(String id){
    	this.id	= id;
    }
    public void setValue(String value){
    	this.value	= value;
    }
    public void setAdd(String add){
    	this.add	= "".equals(add)?null:add;
    }
    
    public String getId(){
    	return id;
    }
    public String getValue(){
    	return value;
    }
    public String getAdd(){
    	return add;
    }
    
    public int doEndTag() throws JspException{
    	StringBuffer sb	= new StringBuffer();
    	sb.append("")
    	.append("<input type=\"text\" class=\"form\" size=\"10\" id=\"").append(id).append("\"")
    	.append(" onfocus=\"javascript:fn_date_format(this,false);this.select();\"")
    	.append(" onblur=\"javascript:fn_date_format(this,true);\"")
    	.append(" value=\"").append(value).append("\"")
    	.append(" onfocus=\"this.select();\" onkeyup=\"javascript:fn_check_date_length(this);\" onclick=\"fn_open_diary(this,'yyyy.mm.dd')\" maxlength=\"8\" ")
    	.append(add)
    	.append("/>")
    	;
    	
	    JspWriter out = pageContext.getOut();
	    try{
	    	out.println(sb.toString());
	    }catch(IOException e){
	    	throw new JspException(e);
	    }
	    return Tag.EVAL_PAGE;
    }
}
