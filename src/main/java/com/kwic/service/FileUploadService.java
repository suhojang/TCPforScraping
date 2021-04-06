package com.kwic.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.kwic.exception.DefinedException;
import com.kwic.io.JOutputStream;
import com.kwic.reference.FileUploadProperty;
import com.kwic.reference.FileVO;

/**
 *<pre>
 *업로드된 파일의 저장을 수행하고 저장정보를 xml로 반환한다.
 *  (사용예)
 *    	//file parameter
 *    	JXParser result	= fileUploadService.fileUpload(request);
 *
 *    	for(int i=0;i&lt;result.getLoopCount("",FileUploadProperty.FILE_TAG);i++)
 *    	{
 *    		fileName	= result.getValue( FileUploadProperty.FILE_TAG
 *    						+"["+i+"]/"
 *    						+FileUploadProperty.ORIGINAL_FILE_NAME_TAG)
 *    						);
 *    
 *    		filePath 	= result.getValue(FileUploadProperty.FILE_TAG
 *		    				+"["+i+"]/"
 *		    				+FileUploadProperty.FOLDER_PATH_TAG)
 *		    				+ File.separator
 *		    				+ result.getValue(FileUploadProperty.FILE_TAG
 *							+"["+i+"]/"
 *							+FileUploadProperty.REAL_FILE_NAME_TAG)
 *    			);
 *    	}
 *  	fileName	= result.getValue( FileUploadProperty.FILE_TAG
 *    						+"["+FileUploadProperty.PARAM_NAME_TAG+"='photoPathFile']/"
 *    						+FileUploadProperty.ORIGINAL_FILE_NAME_TAG);
 *    	filePath 	= result.getValue(FileUploadProperty.FILE_TAG
 *		    				+"["+FileUploadProperty.PARAM_NAME_TAG+"='photoPathFile']/"
 *		    				+FileUploadProperty.FOLDER_PATH_TAG)
 *		    				+ File.separator
 *		    				+ result.getValue(FileUploadProperty.FILE_TAG
 *							+"["+FileUploadProperty.PARAM_NAME_TAG+"='photoPathFile']/"
 *							+FileUploadProperty.REAL_FILE_NAME_TAG);
 *  (반환 xml sample)
 *    &lt;?xml version="1.0" encoding="utf-8" ?&gt;
 *    &lt;fileList&gt;
 *    	&lt;file&gt;
 *    		&lt;param_name&gt;uploadFile&lt;/param_name&gt;
 *    		&lt;original_file_name&gt;userList.xls&lt;/original_file_name&gt;
 *    		&lt;real_file_name&gt;userList_11002018367.xls&lt;/real_file_name&gt;
 *    		&lt;folder_path&gt;D:/files/user/mgr/&lt;/folder_path&gt;
 *    		&lt;file_size&gt;2003240&lt;/file_size&gt;
 *    	&lt;/file&gt;
 *    &lt;/fileList&gt;
 *    
 *    배열성 파일 파라미터 처리 : 파일 element 명을 FILE[0], FILE[1]과 같이 배열의 방번호가 포함되게 작성한다.
 * 
 *   수정일         수정자                   수정내용
 *  -------        --------    ---------------------------
 *  2015. 04. 30    장정훈        최초작성
 *  
 * </pre>
 * @author 장정훈
 * @version 1.0
 * @see FileUploadProperty
 * @see MultipartHttpServletRequest
 * @see MultipartFile
 * @since jdk 1.6
 */
public class FileUploadService{
	/** logger **/
	private Log log			= LogFactory.getLog(getClass());
	/** stream buffer size **/
	private final int BUFF_SIZE	= 2048;
	
	/** file upload property **/
	@Resource(name="fileUploadProperty")
	private FileUploadProperty fileUploadProperty;
	
	private int fileSequence	= 1;
	
	public synchronized int getNext(){
		fileSequence++;
		if(fileSequence>99999999)
			fileSequence	= 1;
		return 		fileSequence++;
	}
	
    /**
     * request로부터 파일객체를 추출하여 byte[] 저장하고 저장정보를 xml 객체로 반환한다.
     * 
    * @param request HttpServletRequest
    * @throws Exception
    * @return Map<String,List<FileVO>> 파일저장정보 객체
    */
    public Map<String,List<FileVO>> getFileBytes( HttpServletRequest request,String maxSize) throws Exception {
    	Map<String,List<FileVO>>		fileMap		= new HashMap<String,List<FileVO>>();
    	List<FileVO>					voList		= null;
    	FileVO							vo			= null;
    	try{
	    	MultipartHttpServletRequest	mptRequest	= (MultipartHttpServletRequest)request;
	    	Iterator<?>					fileIter	= mptRequest.getFileNames();
	    	String						paramName	= null;
	    	String						entityName	= null;
	    	MultipartFile				mFile		= null;
	    	int							ord			= 0;
	    	while (fileIter.hasNext()) {
	    		ord			= 0;
	    	    mFile		= mptRequest.getFile(entityName=(String)fileIter.next());
	    	    paramName	= entityName;
	    	    if(paramName.indexOf("[")>=0 && paramName.indexOf("]")>=0){
	    	    	try{
	    	    		ord			= Integer.parseInt(paramName.substring(paramName.lastIndexOf("[")+1,paramName.lastIndexOf("]")));
    	    			paramName	= paramName.substring(0,paramName.lastIndexOf("["));
	    	    	}catch(Exception e){
	    	    		e.printStackTrace();
	    	    	}
	    	    }
	    	    if(mFile.getSize() > 0) {
	    	    	if(fileMap.get(paramName)==null){
	    	    		voList	= new ArrayList<FileVO>();
	    	    		fileMap.put(paramName, voList);
	    	    	}
	    	    	vo	= getBytes(mFile,maxSize);
    	    		fileMap.get(paramName).add(fileMap.get(paramName).size()>ord?ord:fileMap.get(paramName).size(),vo);
	    	    }
	    	}
    	}catch(Exception e){
    		log.error(e);
    		throw e;
    	}	
    	return fileMap;
    }
    public FileVO getBytes(MultipartFile file,String mSize) throws Exception{
    	String maxsize			= mSize;
    	String orginFileName	= file.getOriginalFilename();
    	String fileExt			= (orginFileName.lastIndexOf(".")>=0 && orginFileName.length()>orginFileName.lastIndexOf(".")+1)?orginFileName.substring(orginFileName.lastIndexOf(".") + 1):"";
    	
    	if(maxsize==null || "".equals(maxsize.trim()) || "0".equals(maxsize.trim())){
    		maxsize		= FileUploadProperty.getSize(
    						fileUploadProperty.getMaxsize()
		    				, fileUploadProperty.getSizeunit()
    					);	
    	}
    	
    	if(file.getSize()>Long.parseLong(maxsize))
			throw new DefinedException("The file size too large. [max-size="+maxsize.trim()+" bytes , file-size="+String.valueOf(file.getSize())+" bytes]");

    	FileVO	vo	= new FileVO();
    	vo.setOriginName(orginFileName);
    	vo.setSize(file.getSize());
    	vo.setExt(fileExt);
    	vo.setBytes(getBytes(file));

    	return vo;
    }
    protected byte[] getBytes(MultipartFile file) throws Exception {
    	InputStream is	= null;
    	JOutputStream os	= null;
    	
    	try {
    	    is			= file.getInputStream();
    	    os = new JOutputStream();
    	    os.write(is);
    	} catch (IOException ioe) {
    		log.error(ioe);
    		throw ioe;
    	} catch (Exception e) {
    		log.error(e);
    		throw e;
    	} finally {
    	    if (os != null) {try {os.close();} catch (Exception ignore) {}}
    	    if (is != null) {try {is.close();} catch (Exception ignore) {}}
    	}
    	return os.getBytes();
	}
    
	
    /**
     * request로부터 파일객체를 추출하여 저장하고 저장정보를 xml 객체로 반환한다.
     * 
    * @param request HttpServletRequest
    * @throws Exception
    * @return Map<String,List<FileVO>> 파일저장정보 객체
    */
	public Map<String,List<FileVO>> fileUpload(HttpServletRequest request,String sub )throws Exception{
		return fileUpload(request, fileUploadProperty.getFolderPath(request),fileUploadProperty.getSub(sub),null,false);
	}
	public Map<String,List<FileVO>> fileUpload(HttpServletRequest request,String sub,String rename,boolean isRenamed )throws Exception{
		return fileUpload(request, fileUploadProperty.getFolderPath(request),fileUploadProperty.getSub(sub),rename,isRenamed);
	}
    /**<pre>
     * request로부터 파일객체를 추출하여 저장하고 저장정보를 xml 객체로 반환한다.
     * sub 폴더를 메소드 파라미터로 명시할 경우와 request 파라미터로 지정할 경우에 대한 분기를 수행한다
     * 
     *  - 파일 허용크기 	= 파일파라미터명+FileUploadProperty.NAMEPREFIX+"maxsize"+FileUploadProperty.NAMESUFFIX
     *  - fixed 파일명 	= 파일파라미터명+FileUploadProperty.NAMEPREFIX+"filename"+FileUploadProperty.NAMESUFFIX
     *  - sub폴더 alias	= 파일파라미터명+FileUploadProperty.NAMEPREFIX+"sub"+FileUploadProperty.NAMESUFFIX
     *  fileName[n]과 같이 배열 형식으로 업로드되는 파일은 모두 fileName으로 저장된다.
     * </pre>
    * @param request HttpServletRequest
    * @param folderPath 저장할 base folder
    * @param sub 저장할 sub folder
    * @throws Exception
    * @return Map<String,List<FileVO>> 파일저장정보 객체
    */
    public Map<String,List<FileVO>> fileUpload( HttpServletRequest request, String folderPath, String sub,String rename,boolean isRenamed ) throws Exception {
    	Map<String,List<FileVO>>		fileMap		= new HashMap<String,List<FileVO>>();
    	List<FileVO>					voList		= null;
    	FileVO							vo			= null;
    	try{
	    	MultipartHttpServletRequest	mptRequest	= (MultipartHttpServletRequest)request;
	    	Iterator<?>					fileIter	= mptRequest.getFileNames();
	    	String						paramName	= null;
	    	String						entityName	= null;
	    	MultipartFile				mFile		= null;
	    	int							ord			= 0;
	    	while (fileIter.hasNext()) {
	    		ord			= 0;
	    	    mFile		= mptRequest.getFile(entityName=(String)fileIter.next());
	    	    paramName	= entityName;
	    	    if(paramName.indexOf("[")>=0 && paramName.indexOf("]")>=0){
	    	    	try{
	    	    		ord			= Integer.parseInt(paramName.substring(paramName.lastIndexOf("[")+1,paramName.lastIndexOf("]")));
    	    			paramName	= paramName.substring(0,paramName.lastIndexOf("["));
	    	    	}catch(Exception e){
	    	    		e.printStackTrace();
	    	    	}
	    	    }
	    	    if(mFile.getSize() > 0) {
	    	    	if(fileMap.get(paramName)==null){
	    	    		voList	= new ArrayList<FileVO>();
	    	    		fileMap.put(paramName, voList);
	    	    	}
	    	    	vo	= uploadFile(folderPath , mFile,null, isRenamed?rename:null, sub);
    	    		fileMap.get(paramName).add(fileMap.get(paramName).size()>ord?ord:fileMap.get(paramName).size(),vo);
	    	    }
	    	}
    	}catch(Exception e){
    		log.error(e);
    		throw e;
    	}	
    	return fileMap;
    }
    /**<pre>
     * 업로드된 파일 stream을 파일객체로 저장한다.
     * sub 폴더를 메소드 파라미터로 명시할 경우와 request 파라미터로 지정할 경우에 대한 분기를 수행한다
     * 
     *  - 파일 허용크기 	= 파일파라미터명+FileUploadProperty.NAMEPREFIX+"maxsize"+FileUploadProperty.NAMESUFFIX
     *  - fixed 파일명 	= 파일파라미터명+FileUploadProperty.NAMEPREFIX+"filename"+FileUploadProperty.NAMESUFFIX
     *  - sub폴더 alias	= 파일파라미터명+FileUploadProperty.NAMEPREFIX+"sub"+FileUploadProperty.NAMESUFFIX
     *  	 
     * </pre>
    * @param stordFilePath 저장할 base 폴더 경로
    * @param file 업로드할 multipart 파일 객체
    * @param mSize 허용 최대크기
    * @param filename 확장자를뺀 파일명
    * @param sub sub 폴더경로
    * @param paramName 파일파라미터 명
    * @throws Exception
    * @return String 파일저장정보 xml 문자열
    */
    public FileVO uploadFile(String stordFilePath , MultipartFile file,String mSize, String filename, String sub) throws Exception{
    	String stordFolderPath	= "";
    	String maxsize			= mSize;
    	String newName		= "";
    	String orginFileName	= file.getOriginalFilename();
    	String fileExt				= (orginFileName.lastIndexOf(".")>=0 && orginFileName.length()>orginFileName.lastIndexOf(".")+1)?orginFileName.substring(orginFileName.lastIndexOf(".") + 1):"";
    	
    	if(filename!=null && !"".equals(filename.trim()))
    		newName				= filename+"_"+String.valueOf(Calendar.getInstance().getTime().getTime())+"_"+getNext()+ "." + fileExt;
    	else
    		newName				= String.valueOf(Calendar.getInstance().getTime().getTime())+"_"+getNext()+"_"+orginFileName;
    	
    	if(sub!=null && !"".equals(sub.trim()))
    		stordFolderPath	= stordFilePath+File.separator+sub;

    	stordFolderPath	= StringUtils.replace(stordFolderPath, "\\", File.separator);
    	stordFolderPath	= StringUtils.replace(stordFolderPath, "/", File.separator);

    	if(maxsize==null || "".equals(maxsize.trim()) || "0".equals(maxsize.trim())){
    		maxsize		= FileUploadProperty.getSize(
    						fileUploadProperty.getMaxsize()
		    				, fileUploadProperty.getSizeunit()
    					);	
    	}
    	
    	if(file.getSize()>Long.parseLong(maxsize))
			throw new DefinedException("The file size too large. [max-size="+maxsize.trim()+" bytes , file-size="+String.valueOf(file.getSize())+" bytes]");

		writeFile(file, newName, stordFolderPath);
		
    	FileVO	vo	= new FileVO();
    	vo.setFile(new File(stordFolderPath+(stordFolderPath.endsWith(File.separator)?"":File.separator)+newName));
    	vo.setOriginName(orginFileName);
    	vo.setNewName(newName);
    	vo.setRealFilePath(stordFolderPath+(stordFolderPath.endsWith(File.separator)?"":File.separator)+newName);
    	vo.setSize(file.getSize());
    	vo.setExt(fileExt);

    	log.debug("["+stordFolderPath+File.separator+newName+"] saved.");
    	
    	return vo;
    }
    
    /**<pre>
     * 파일을 생성한다.
     * </pre>
    * @param stordFilePath 저장할 폴더 전체경로
    * @param file 업로드할 multipart 파일 객체
    * @param newName 저장할 파일명
    * @throws Exception
    */
    protected void writeFile(MultipartFile file, String newName, String stordFilePath) throws Exception {
    	InputStream is	= null;
    	OutputStream os	= null;
    	
    	try {
    	    is			= file.getInputStream();
    	    File folder	= new File(stordFilePath);

    	    if(!folder.exists()){
    	    	folder.mkdirs();
    	    }else if (!folder.isDirectory()){
    	    	folder.mkdir();
    	    }

    	    os = new FileOutputStream(stordFilePath + File.separator + newName);

    	    int bytesRead	= 0;
    	    byte[] buffer	= new byte[BUFF_SIZE];

    	    while ((bytesRead = is.read(buffer, 0, BUFF_SIZE)) != -1) {
    	    	os.write(buffer, 0, bytesRead);
    	    }
    	} catch (FileNotFoundException fnfe) {
    		log.error(fnfe);
    		throw fnfe;
    	} catch (IOException ioe) {
    		log.error(ioe);
    		throw ioe;
    	} catch (Exception e) {
    		log.error(e);
    		throw e;
    	} finally {
    	    if (os != null) {try {os.close();} catch (Exception ignore) {}}
    	    if (is != null) {try {is.close();} catch (Exception ignore) {}}
    	}
	}
}
