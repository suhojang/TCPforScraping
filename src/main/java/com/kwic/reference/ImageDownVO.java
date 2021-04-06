package com.kwic.reference;

public class ImageDownVO {
	private String name;	//이미지명
	private String realFilePath;	//저장full경로
	private String contentType;	//contentType
	private byte[] bytes;
	
	public void setName(String name){
		this.name	= name;
	}
	public void setRealFilePath(String realFilePath){
		this.realFilePath	= realFilePath;
	}
	public void setContentType(String contentType){
		this.contentType	= contentType;
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
	public String getContentType(){
		return contentType;
	}
	public byte[] getBytes(){
		return bytes;
	}
}
