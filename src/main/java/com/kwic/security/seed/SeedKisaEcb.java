package com.kwic.security.seed;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class SeedKisaEcb {
	
	public static byte[] hexToByteArray(String hex) {
		return new java.math.BigInteger(hex, 16).toByteArray();
	}
	
	private static byte[] initKey(byte[] oriKeyBytes,String enc) throws UnsupportedEncodingException{
		byte[] keyBytes	= new byte[16];
		System.arraycopy(oriKeyBytes, 0, keyBytes, 0, oriKeyBytes.length>keyBytes.length?keyBytes.length:oriKeyBytes.length);
		
		return keyBytes;
	} 
	private static byte[] initKey(String key,String enc) throws UnsupportedEncodingException{
		byte[] keyBytes	= new byte[16];
		byte[] oriKeyBytes	= key.getBytes(enc);
		System.arraycopy(oriKeyBytes, 0, keyBytes, 0, oriKeyBytes.length>keyBytes.length?keyBytes.length:oriKeyBytes.length);
		return keyBytes;
	} 
	
	private static byte[] initData(byte[] oriDataBytes){
		int blockSize	= (oriDataBytes.length/16)+(oriDataBytes.length%16==0?0:1);
		byte[] dataBytes	= new byte[blockSize*16];
		System.arraycopy(oriDataBytes, 0, dataBytes, 0, oriDataBytes.length);
		return dataBytes;
	}
	
	public static byte[] encrypt(byte[] key,byte[] oriDataBytes,String enc) throws UnsupportedEncodingException{
		byte[] keyBytes		= initKey(key,enc);
		byte[] dataBytes	= initData(oriDataBytes);
		
		byte[] pbCipher	= KISA_SEED_ECB.SEED_ECB_Encrypt(keyBytes, dataBytes, 0, dataBytes.length);
		return pbCipher;
	}
	
	public static String toHexString(byte[] bytes){
		/*
		StringBuffer sb	= new StringBuffer();
		String c	= null;
	    for (int i=0; i<bytes.length; i++){
	    	c	= Integer.toHexString(0xff&bytes[i]);
	    	if(c.length()==1)
	    		c	= "0"+c;
	    	sb.append(c);
	    }
	    return sb.toString().toUpperCase(Locale.KOREA);
	    */
		return new java.math.BigInteger(bytes).toString(16).toUpperCase(Locale.KOREA);
	}
	public static byte[] encrypt(String key,String data,String enc) throws UnsupportedEncodingException{
		byte[] keyBytes		= initKey(key,enc);
		byte[] dataBytes	= initData(data.getBytes(enc));
		byte[] pbCipher	= KISA_SEED_ECB.SEED_ECB_Encrypt(keyBytes, dataBytes, 0, dataBytes.length);
		
		return pbCipher;
	}
	
	public static byte[] decrypt(byte[] key,byte[] encodedBytes,String enc) throws UnsupportedEncodingException{
		byte[] keyBytes		= initKey(key,enc);
		
		byte[] pbPlain	= KISA_SEED_ECB.SEED_ECB_Decrypt(keyBytes, encodedBytes, 0, encodedBytes.length);
		return pbPlain;
	}
	
	public static String decrypt(String key,String encodedData,String enc) throws UnsupportedEncodingException{
		byte[] keyBytes		= initKey(key,enc);
		byte[] dataBytes	= hexToByteArray(encodedData);
		
		byte[] pbPlain		= KISA_SEED_ECB.SEED_ECB_Decrypt(keyBytes, dataBytes, 0, dataBytes.length);
		
		return new String(pbPlain,enc);
	}
	
	public static String decryptVB(String key,String encodedData,String enc) throws UnsupportedEncodingException{
		byte[] keyBytes		= initKey(key,enc);
		byte[] dataBytes	= hexToByteArray(encodedData);
		
		byte[] pbPlain		= KISA_SEED_ECB.SEED_ECB_DecryptVB(keyBytes, dataBytes, 0, dataBytes.length);
		int idx	= 0;
		for(int i=0;i<pbPlain.length;i++){
			if(pbPlain[i]==0x00){
				idx	= i;
				break;
			}
		}
		byte[] newBytes	= new byte[idx];
		System.arraycopy(pbPlain, 0, newBytes, 0, idx);
		
		return new String(newBytes,enc);
	}
	
	/**
	 * VB 16바이트 암호문 연동용
	 * @throws UnsupportedEncodingException 
	 * 
	 * */
	public static String encode(String key,String data) throws UnsupportedEncodingException{
		return encode(key,data,"EUC-KR");
	}
	public static String encode(String key,String data,String enc) throws UnsupportedEncodingException{
		byte[] encBytes	= encrypt(key, data, enc);
//		byte[] vbbytes	= new byte[16];
//		System.arraycopy(encBytes, 0, vbbytes, 0, vbbytes.length);
//		return toHexString(vbbytes);
		return toHexString(encBytes);
	}
	/**
	 * VB 16바이트 암호문 연동용
	 * @throws UnsupportedEncodingException 
	 * 
	 * */
	public static String decode(String key,String encStr) throws UnsupportedEncodingException{
		return decode(key,encStr,"EUC-KR");
	}
	public static String decode(String key,String encStr,String enc) throws UnsupportedEncodingException{
		return decryptVB(key, encStr, enc);
	}

	public static void main(String[] args) throws Exception{
		String key	= "kwic5539";
		String data	= "1#@#@ㅌ치ㅏㅟ나ㅜㅇ니1212121adsdjsnslkdnl가나다라마바사";
//		String enc	= "EUC-KR";
		
		String encStr	= encode(key,data,"UTF-8");
		System.out.println(encStr);
		String decStr	= decode(key,encStr,"UTF-8");
		System.out.println(decStr);
		
		/*byte[] encBytes	= SeedKisaEcb.encode(key, data, enc);
		byte[] vbbytes	= new byte[16];
		System.arraycopy(encBytes, 0, vbbytes, 0, vbbytes.length);
		
		String encStr	= SeedKisaEcb.toHexString(encBytes);
		System.out.println("원본 암호문 : "+encStr);
		String decStr	= SeedKisaEcb.decode(key, encStr, enc);
		System.out.println("원본 복호문 : "+decStr);
		
		encStr	= SeedKisaEcb.toHexString(vbbytes);
		System.out.println("16Bytes CUT 암호문 : "+encStr);
		decStr	= SeedKisaEcb.decryptVB(key, encStr, enc);
		System.out.println("16Bytes CUT 복호문 : "+decStr);*/
		
	}	

}
