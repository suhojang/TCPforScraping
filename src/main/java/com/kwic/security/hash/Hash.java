package com.kwic.security.hash;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.kwic.io.JOutputStream;

public class Hash {
	public static final String HASH_TYPE_MD5	= "MD5";
	public static final String HASH_TYPE_SHA256	= "SHA-256";
	
	public static final String getHash(byte[] bytes, String type) throws NoSuchAlgorithmException{
		MessageDigest md	= MessageDigest.getInstance(type); 
		md.update(bytes);
		byte[] digest	= md.digest();
		
		StringBuffer sb	= new StringBuffer();
		
		for(int i=0;i<digest.length;i++){
			sb.append(Integer.toString((digest[i]&0xff)+0x100,16).substring(1));
		}
		return sb.toString();
	}

	public static final String getHash(File file, String type)  throws NoSuchAlgorithmException{
		JOutputStream jos	= null;
		InputStream is	= null;
		try{
			jos	= new JOutputStream();
			is	= new FileInputStream(file);
			jos.write(is);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{if(jos!=null)jos.close();}catch(Exception e){}
			try{if(is!=null)is.close();}catch(Exception e){}
		}
		String hash	= Hash.getHash(jos.getBytes(), Hash.HASH_TYPE_SHA256);
		return hash;
	}
	
	public static void main(String[] args) throws Exception{
		String a	= Hash.getHash("89HU2F8V7ULNV52V1496034000".getBytes("UTF-8"), Hash.HASH_TYPE_SHA256);
		System.out.println(a);
	}

}
