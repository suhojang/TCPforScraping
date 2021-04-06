package com.kwic.support;

import com.kwic.security.aes.AESCipher;

public class Aes256Crypto extends CryptoKeyGenerator{
	
	public static final String encode(String plainText,String key) throws Exception{
		return encode(plainText,key,"UTF-8");
	}
	public static final String encode(String plainText,String key,String enc) throws Exception{
		return AESCipher.encode(plainText, key,AESCipher.TYPE_256,enc,AESCipher.MODE_ECB_NOPADDING);
	}
	public static final String decode(String encodedText,String key) throws Exception{
		return decode(encodedText,key,"UTF-8");
	}
	public static final String decode(String encodedText,String key,String enc) throws Exception{
		return AESCipher.decode(encodedText, key,AESCipher.TYPE_256,enc,AESCipher.MODE_ECB_NOPADDING);
	}
	
	
	
	public static void main2(String[] args) throws Exception{
//		String key	= "QNKyGCYDWE2b4kkT";
//		String enc	= "JjKdZi/eIp15+cWiA5jg6PESCUZLyj7MjbVb7hvJUFqwwifJVAdMdf1wcGtGTLiG0iDFnaOwoU5brdGR+M3H5aBJESiouVh1WcO6qTPbnmMnv0GxFsqAPd4it7qWbRHq";
		//enc	= new String(Base64.decodeBase64(enc.getBytes("UTF-8")),"UTF-8");
//System.out.println(enc);
//		String dec	= Aes256Crypto.decode(enc, key,"ms949");
		//SmpLZFppL2VJcDE1K2NXaUE1amc2UEVTQ1VaTHlqN01qYlZiN2h2SlVGcXd3aWZKVkFkTWRmMXdjR3RHVExpRzBpREZuYU93b1U1YnJkR1IrTTNINWFCSkVTaW91VmgxV2NPNnFUUGJubU1udjBHeEZzcUFQZDRpdDdxV2JSSHE=
//		System.out.println(dec);
		

		String key = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456";
		String plainText  = "abcd!@34";
		
		//aes 256 EUC-KR,ECB NoPadding (couple with VB AES256)
		String encodeText = Aes256Crypto.encode(plainText, key,"EUC-KR");
		System.out.println("aes 256 EUC-KR ECB NoPadding : "+encodeText);
		
		String decodeText = Aes256Crypto.decode(encodeText, key,"EUC-KR");
		System.out.println("aes 256 EUC-KR ECB NoPadding decode : "+decodeText);
		
		System.out.println("---------------------------------\n\n");
		//aes 256 EUC-KR,CBC Padding
		encodeText	= AESCipher.encode(plainText, key,AESCipher.TYPE_256,"UTF-8",AESCipher.MODE_CBC);
		System.out.println("aes 256 UTF-8 CBC Padding : "+encodeText);
		
		decodeText	= AESCipher.decode(encodeText, key,AESCipher.TYPE_256,"UTF-8",AESCipher.MODE_CBC);
		System.out.println("aes 256 UTF-8 CBC Padding decode : "+decodeText);

//		for(int i=21;i<25;i++){
//			System.out.println((i+1)+" : "+getRandomKey(ALGORITHM_AES256,new int[]{KEY_TYPE_NUM,KEY_TYPE_ENG_CAPITAL}));
//		}
		
		for(int i=0;i<2;i++){
			System.out.println((i+1)+" : "+getRandomKey(ALGORITHM_AES256));
		}
	}
}
