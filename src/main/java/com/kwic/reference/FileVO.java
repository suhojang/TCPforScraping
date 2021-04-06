package com.kwic.reference;

import java.io.File;
import java.util.Locale;

public class FileVO {
	private File file;	//저장파일
	private String originName	= "";	//원본파일명
	private String newName		= "";	//변경파일명
	private String realFilePath	= "";	//저장full경로
	private String ext			= "";	//확장자
	private long size			= 0;	//파일사이즈
	private byte[] bytes;
	
	public void setFile(File file){
		this.file	= file;
	}
	public void setOriginName(String originName){
		this.originName	= originName;
	}
	public void setNewName(String newName){
		this.newName	= newName;
	}
	public void setRealFilePath(String realFilePath){
		this.realFilePath	= realFilePath;
	}
	public void setExt(String ext){
		this.ext	= ext==null?"":ext.toLowerCase(Locale.KOREA);
	}
	public void setSize(long size){
		this.size	= size;
	}
	public void setBytes(byte[] bytes){
		this.bytes	= bytes;
	}
	
	public File getFile(){
		return file;
	}
	public String getOriginName(){
		return originName;
	}
	public String getNewName(){
		return newName;
	}
	public String getRealFilePath(){
		return realFilePath;
	}
	public String getExt(){
		return ext;
	}
	public long getSize(){
		return size;
	}
	public byte[] getBytes(){
		return bytes;
	}
}
