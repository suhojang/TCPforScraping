package com.kwic.reference;

public class FileDownVO {
	private String name	= "";	//파일명
	private String realFilePath	= "";	//저장full경로
	private byte[] bytes;
	
	public void setName(String name){
		this.name	= name;
	}
	public void setRealFilePath(String realFilePath){
		this.realFilePath	= realFilePath;
	}
	public void setBytes(byte[] bytes){
		this.bytes	= bytes;
	}
	
	public String getName(){
		return name;
	}
	public String getRealFilePath(){
		return realFilePath;
	}
	public byte[] getBytes(){
		return bytes;
	}
}
