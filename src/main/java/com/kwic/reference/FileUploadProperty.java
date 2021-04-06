package com.kwic.reference;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 	<pre>파일업로드에 필요한 속성정보를 저장하는 property객체
 *   [context-common. 설정정보]
 *	&lt;!-- File Upload Property --&gt;
 *	&lt;bean id="fileUploadProperty" class="gaia.com.property.FileUploadProperty" init-method="getInstance"&gt;
 *		&lt;property name="folder" value="C:/GTMS/workspace/gtms/pilot/files"/&gt;
 *		&lt;property name="maxsize" value="10"/&gt;
 *		&lt;property name="sizeunit" value="M"/&gt;
 *		&lt;property name="sub"&gt;
 *			&lt;map&gt;
 *				&lt;entry key="common" value="common"/&gt;
 *				&lt;entry key="userMgr" value="user/mgr"/&gt;
 *				&lt;entry key="temp" value="temp"/&gt;
 *				&lt;entry key="pdf_temp" value="pdf_temp"/&gt;
 *			&lt;/map&gt;
 *		&lt;/property&gt;
 *	&lt;/bean&gt;
 *
 *   수정일         수정자                   수정내용
 *  -------        --------    ---------------------------
 *  2015. 04. 30   장정훈        최초작성
 * </pre>
 * @author 장정훈
 * @version 1.0
 * @see com.kwic.reference.FileUploadService
 * @since jdk 1.6
 */

public final class FileUploadProperty {
	/** singleton object **/
	private static FileUploadProperty instance;
	
	/**파일 저장 폴더 지정방식 a:절대경로,r:상대경로*/
	private String position;
    /** 파일 저장 폴더 **/
    private String folder;
    /** 파일 허용 최대 크기 **/
	private String maxsize;
    /** 파일 허용 최대 크기 단위(M=Mega Bytes/K=Killo Bytes/B=Bytes) **/
	private String sizeunit;
    /** 파일의 저장경로와 Alias **/
	private Map<String,String> sub;
	
    /**<pre>
     * FileUploadProperty 객체 획득 메소드 
     * ex) FileUploadProperty property	= FileUploadProperty.getInstance();
     * </pre>
    * @return FileUploadProperty singleton Object
    */
	public static FileUploadProperty getInstance( ){
		synchronized (FileUploadProperty.class){
			if(instance == null){
				instance = new FileUploadProperty( );
			}
		}
		return instance;
	}

    /**<pre>
     * private constructor
     * 객체 생성방법 => FileUploadProperty property	= FileUploadProperty.getInstance();
     * </pre>
    */
	private FileUploadProperty(){
		
	}
	
    /**
     * 파일저장 폴더 지정방식을 보관한다.
    * @param position 파일저장 폴더 지정방식
    */
	public void setPosition(String position){
		this.position	= position;
	}
    /**
     * 파일 저장 폴더 정보를 보관한다.
    * @param folder 파일저장폴더의 alias
    */
	public void setFolder(String folder){
		this.folder	= folder;
	}
    /**
     * 파일 최대 허용 크기를 설정한다.
    * @param maxsize 허용가능한 최대크기
    */
	public void setMaxsize(String maxsize){
		this.maxsize	= maxsize;
	}
    /**
     * 파일 최대 허용 크기의 단위를 설정한다.
    * @param sizeunit 허용가능한 최대크기단위(M/K/B)
    */
	public void setSizeunit(String sizeunit){
		this.sizeunit	= sizeunit;
	}
    /**
     * 파일저장 폴더의 경로와 alias의 mapping 정보를 설정한다. 
    * @param sub 허용가능한 최대크기단위(M/K/B)
    */
	public void setSub(Map<String,String> sub){
		this.sub	= sub;
	}
	
    /**
     * 파일저장 폴더 지정방식을 반환
    * @return String 파일저장 경로 방식
    */
	public String getPosition(){
		return position;
	}
    /**
     * 파일저장 폴더 alias를 반환한다.
    * @return String 파일저장 경로 alias
    */
	public String getFolder(){
		return folder;
	}
    /**
     * 허용 최대 크기를 반환한다.
    * @return String 허용최대크기
    */
	public String getMaxsize(){
		return maxsize;
	}
    /**
     * 파일 허용 최대크기의 단위를 반환한다.(M/K/B)
    * @return String 허용최대크기단위(M/K/B)
    */
	public String getSizeunit(){
		return sizeunit;
	}
    /**
     * 파일저장 폴더의 경로를 반환한다.
    * @param key 파일폴더경로 alias
    * @return String 파일 저장 폴더 경로
    */
	public String getSub(String key){
		if(key==null)
			return "";
		return sub.get(key);
	}
    /**
     * 파일 허용 최대크기를 byte로 환산한다.
    * @param size 파일 허용 사이즈
    * @param unit 파일 허용 사이즈 단위
    * @throws JspException,NumberFormatException
    * @return String byte로 환산한 파일 허용 최대 크기
    */
    public static String getSize(String size, String unit) throws Exception{
    	String sSize	= "";
    	if("M".equals(unit.trim().toUpperCase())){
    		sSize		= String.valueOf( Long.parseLong(size)*1024*1024 );
    	}else if("K".equals(unit.trim().toUpperCase())){
    		sSize		= String.valueOf( Long.parseLong(size)*1024 );
    	}else if("B".equals(unit.trim().toUpperCase())){
    		//do nothing
    	}else{
    		throw new Exception("Unknow file size unit["+unit+"]..");
    	}
    	return sSize;
    }
    /**
     * 파일저장 절대경로 반환
     * */
    public String getFolderPath(HttpServletRequest request){
    	if("a".equals(position))
    		return getFolder();
    	return request.getSession().getServletContext().getRealPath(getFolder());
    }
}
