package com.kwic.web.util.tlds;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

/**
 *<pre>
 *jsp 에서 Select box 생성
 *  [사용 예]
 *      <kwic:select id="TAGNC_DFRMT" items="${CD0010}" value="CDDTL_DTLCD" text="CDDTL_NM" noselect="false" noselectvalue="" noselecttext=" - 전체 - " add="class='select' style='width:200px;' onchange='javascript:'" initValue='01'/>
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
public class SelectTag extends TagSupport {
	/** serial version UID **/
    private static final long serialVersionUID = 1L;
    
    private String id;
    private List<Map<String,Object>> items;
    private String value;
    private String text;
    private boolean noselect;
    private String noselectvalue;
    private String noselecttext;
    private String add;
    private boolean mobile;
    private String className;
    private String onChange;
    private int height;
    private String initValue;
    
    public void setId(String id){
    	this.id	= id;
    }
    public void setValue(String value){
    	this.value	= value;
    }
    public void setText(String text){
    	this.text	= text;
    }
    public void setNoselect(boolean noselect){
    	this.noselect	= noselect;
    }
    public void setNoselectvalue(String noselectvalue){
    	this.noselectvalue	= "".equals(noselectvalue)?null:noselectvalue;
    }
    public void setNoselecttext(String noselecttext){
    	this.noselecttext	= "".equals(noselecttext)?null:noselecttext;
    }
    public void setItems(List<Map<String,Object>> items){
    	this.items	= items;
    }
    public void setAdd(String add){
    	this.add	= "".equals(add)?null:add;
    }
    public void setMobile(boolean mobile){
    	this.mobile	= mobile;
    }
    public void setClassName(String className){
    	this.className	= "".equals(className)?null:className;
    }
    public void setOnChange(String onChange){
    	this.onChange	= "".equals(onChange)?null:onChange;
    }
    public void setHeight(int height){
    	this.height	= height;
    }
    public void setInitValue(String initValue){
    	this.initValue	= "".equals(initValue)?null:initValue;
    }
    
    public String getId(){
    	return id;
    }
    public String getValue(){
    	return value;
    }
    public String getText(){
    	return text;
    }
    public boolean getNoselect(){
    	return noselect;
    }
    public String getNoselectvalue(){
    	return noselectvalue;
    }
    public String getNoselecttext(){
    	return noselecttext;
    }
    public List<Map<String,Object>> setItems(){
    	return items;
    }
    public String getAdd(){
    	return add;
    }
    public boolean getMobile(){
    	return mobile;
    }
    public String getClassName(){
    	return className;
    }
    public String getOnChange(){
    	return onChange;
    }
    public int getHeight(){
    	return height;
    }
    public String getInitValue(){
    	return initValue;
    }
    
    public int doEndTag() throws JspException{
    	StringBuffer sb	= new StringBuffer();
    	
    	if(mobile){
    		String iText	= "";
    		String iValue	= "";
    		String initText	= "";
        	if(noselect){
        		iText	= noselecttext==null?" - 전체 - ":noselecttext;
        		iValue	= noselectvalue==null?"":noselectvalue;
        	}else{
        		if(items.size()>=1){
        			iText	= (String)items.get(0).get(text);
            		iValue	= (String)items.get(0).get(value);
        		}else{
        			iText	= "&nbsp;";
            		iValue	= "";
        		}
        	}
        	if(initValue!=null){
	        	for(Map<String,Object> map:items){
	        		if(initValue.equals(map.get(value))){
	        			initText	= (String) map.get(text);
	        		}
	        	}
    		}
    		sb.append("<span class=\"select"+(className==null?"":" "+className+"")+"\" id='"+id+"'"+(add!=null?(" "+add):"")+">");
    		sb.append("<a href=\"#\">"+(initValue!=null?initText:iText)+"</a>");
    		sb.append("<input type=\"hidden\" value=\""+(initValue!=null?initValue:iValue)+"\"/>");
    		
    		sb.append("<ul"+(height==0?"":" style='height:"+height+"px;'")+">");
        	if(noselect)
        		sb.append("<li val='"+iValue+"'>"+iText+"</li>");
        	for(Map<String,Object> map:items)
        		sb.append("<li val='"+map.get(value)+"'>"+map.get(text)+"</li>");
    		sb.append("</ul>");
    		sb.append("</span>");
    		
    	}else{
        	sb.append("<select name='"+id+"' id='"+id+"'"+(add!=null?(" "+add):"")+""+(className==null?"":" class='"+className+"'")+""+(onChange==null?"":" onchange=\"javascript:"+onChange+"\"")+">").append("\n");
        	if(noselect){
        		sb.append("<option value='").append(noselectvalue==null?"":noselectvalue).append("'>").append(noselecttext==null?" - 전체 - ":noselecttext).append("</option>").append("\n");
        	}
        	for(Map<String,Object> map:items){
        		sb.append("<option value='").append(map.get(value)).append("'"+(initValue!=null&&initValue.equals(map.get(value))?" selected":"")+">").append(map.get(text)).append("</option>").append("\n");
        	}
        	sb.append("</select>").append("\n");
    	}
    	
	    JspWriter out = pageContext.getOut();
	    try{
	    	out.println(sb.toString());
	    }catch(IOException e){
	    	throw new JspException(e);
	    }
	    return Tag.EVAL_PAGE;
    }
}
