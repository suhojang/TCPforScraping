package com.kwic.security.aes;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import org.apache.commons.codec.binary.Base64;

/*
 * need JCE library
 * must override local_policy.jar, US_export_policy.jar in \jre\lib\security\
 * 
 * */
public class AESCipher2 {
	public static final String DEFAULT_KEY	= "0^2/2a4T5!H@1#9%GDGsjbjip!@$752$";
	
	public static final int TYPE_128	= 16;
	public static final int TYPE_192	= 24;
	public static final int TYPE_256	= 32;
	
	public static final int MODE_ECB	= 1;
	public static final int MODE_CBC	= 2;
	public static final int MODE_ECB_NOPADDING	= 3;
	public static final int MODE_CBC_NOPADDING	= 4;
	
	private static final char[] keypadding	= {
		'a','1','b','C','k','!','e','*'
		,'f','K','D','8','s','4','W','p'
		,'G','a','d','#','G','7','&','E'
		,'U','l','J','j','i','W','2','Q'
	};
	
	private static byte[] ivBytes = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	private static byte[] initKey(byte[] keys,int blockSize) throws Exception{
		byte[] k	= new byte[blockSize];
		if(keys.length>blockSize)
			System.arraycopy(keys, 0, k, 0, blockSize);
		else{
			System.arraycopy(keys, 0, k, 0, keys.length);
			for(int i=keys.length;i<blockSize;i++)
				k[i]	= (byte)keypadding[i%blockSize];
		}
		return k;
	}
	
	private static byte[] padECBNoPaddingText(byte[] bytes) throws Exception{
		int size	= bytes.length;
		int block	= 1;
		block		= size/16;
		if(size%16>0)
			block++;
		
		byte[] newBytes	= new byte[block*16];
		System.arraycopy(bytes, 0, newBytes, 0, size);
		
		for(int i=size;i<block*16;i++)
			newBytes[i]	= 0x00;
		return newBytes;
	}
	private static byte[] removeECBNoPaddingText(byte[] bytes) throws Exception{
		int idx	= bytes.length;
		for(int i=0;i<bytes.length;i++){
			if(bytes[i]==0x00){
				idx	= i;
				break;
			}
		}
		byte[] newBytes	= new byte[idx];
		System.arraycopy(bytes, 0, newBytes, 0, idx);
		return newBytes;
	}
	
	
	public static String encode(String str, String okey,int blockSize) throws Exception {
		return encode(str, okey,blockSize,"UTF-8",MODE_CBC);
	}
	public static String encode(String str, String okey,int blockSize,String enc) throws Exception {
		return encode(str, okey,blockSize,enc,MODE_CBC);
	}
	public static String encode(String str, String okey,int blockSize,int mode) throws Exception {
		return encode(str, okey,blockSize,"UTF-8",mode);
	}
	public static String encode(String str, String okey,int blockSize,String enc,int mode) throws Exception {
		return encode(str.getBytes(enc), okey.getBytes(enc),blockSize,mode);
	}
	public static String encode(byte[] str, byte[] okey,int blockSize,int mode) throws Exception {
		if(str==null || str.length==0)
			return new String(str);
		byte[] key	= initKey(okey,blockSize);
		byte[] textBytes = str;
		SecretKeySpec newKey = new SecretKeySpec(key, "AES");
		Cipher cipher = null;
		if(mode==MODE_ECB){
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, newKey);
		}else if(mode==MODE_ECB_NOPADDING){
			cipher = Cipher.getInstance("AES/ECB/NoPadding");
			textBytes	= padECBNoPaddingText(textBytes);
			cipher.init(Cipher.ENCRYPT_MODE, newKey);
		}else if(mode==MODE_CBC){
			AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
		}else if(mode==MODE_CBC_NOPADDING){
			AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
			cipher = Cipher.getInstance("AES/CBC/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
		}else{
			throw new NoSuchAlgorithmException("Unknown encryption mode ["+mode+"].");
		}
		return Base64.encodeBase64String(cipher.doFinal(textBytes));
	}

	public static byte[] decode(String str, String okey,int blockSize) throws Exception {
		return decode(str, okey,blockSize,"UTF-8",MODE_CBC);
	}
	public static byte[] decode(String str, String okey,int blockSize,String enc) throws Exception {
		return decode(str, okey,blockSize,enc,MODE_CBC);
	}
	public static byte[] decode(String str, String okey,int blockSize,int mode) throws Exception {
		return decode(str, okey,blockSize,"UTF-8",mode);
	}
	public static byte[] decode(String str, String okey,int blockSize,String enc,int mode) throws Exception {
		return decode(str.getBytes(enc), okey.getBytes(enc),blockSize,mode);
	}
	public static byte[] decode(byte[] str, byte[] okey,int blockSize,int mode) throws Exception {
		if(str==null || str.length==0)
			return str;
		byte[] key	= initKey(okey,blockSize);
		
		byte[] textBytes = Base64.decodeBase64(str);
		SecretKeySpec newKey = new SecretKeySpec(key, "AES");
		Cipher cipher 	= null;
		byte[] dBytes	= null;
		if(mode==MODE_ECB){
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, newKey);
			dBytes	= cipher.doFinal(textBytes);
		}else if(mode==MODE_ECB_NOPADDING){
			cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, newKey);
			dBytes	= cipher.doFinal(textBytes);
			dBytes	= removeECBNoPaddingText(dBytes);
		}else if(mode==MODE_CBC){
			AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
			dBytes	= cipher.doFinal(textBytes);
		}else if(mode==MODE_CBC_NOPADDING){
			AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
			cipher = Cipher.getInstance("AES/CBC/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
			dBytes	= cipher.doFinal(textBytes);
		}else{
			throw new NoSuchAlgorithmException("Unknown encryption mode ["+mode+"].");
		}
		
		return dBytes;
	}
	
	public static void main(String[] args) throws Exception{
		String key	= "123456789012345678901234";
		String plain	= "아름다운 우리나라";
		String enc	= AESCipher2.encode(plain.getBytes(), key.getBytes(), TYPE_192,MODE_ECB);
		String dec	= new String(AESCipher2.decode(enc.getBytes(), key.getBytes(), TYPE_192,MODE_ECB));
		
		System.out.println(enc);
		System.out.println(dec);
	}
}