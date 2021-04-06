package com.kwic.support;

import com.kwic.security.aes.AESCipher;

public class CryptoKeyGenerator {
	
	public static final int KEY_TYPE_NUM			= 1;
	public static final int KEY_TYPE_ENG_CAPITAL	= 2;
	public static final int KEY_TYPE_ENG_SMALL		= 3;
	public static final int KEY_TYPE_SIGN			= 4;
	public static final int KEY_TYPE_USER_PASSWORD	= 5;
	
	public static final int ALGORITHM_SMSNUM	= 5;
	public static final int ALGORITHM_SEED128	= 16;
	public static final int ALGORITHM_AES256	= 32;
	
	private static final char[] keyNum	= new char[]{
		'0','1','2','3','4','5','6','7','8','9'
	};
	private static final char[] keySign	= new char[]{
		'#','$','%',':','&','*','+','-','_','/','@','^','=',';','(',')'
	};
	private static final char[] keyEngCapital	= new char[]{
		'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
	};
	private static final char[] keyEngSmall	= new char[]{
		'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'
	};
	private static final char[] keyUserPassword	= new char[]{
		'2','3','4','5','6','7','8','9','a','b','c','d','e','f','g','h','i','j','k','m','n','p','q','r','s','t','u','v','w','x','y','z'
	};
	
	public static final String getSmsAuthKey(){
		return getRandomKey(ALGORITHM_SMSNUM,new int[]{KEY_TYPE_NUM});
	}
	public static final String getRandomKey(int algorithm){
		return getRandomKey(algorithm,new int[]{KEY_TYPE_NUM,KEY_TYPE_ENG_CAPITAL,KEY_TYPE_ENG_SMALL,KEY_TYPE_SIGN});
	}
	public static final String getRandomKey(int algorithm,int[] keyType){
		StringBuffer sb	= new StringBuffer();
		for(int i=0;i<keyType.length;i++){
			if(keyType[i]==KEY_TYPE_NUM)
				for(int j=0;j<keyNum.length;j++)
					sb.append(keyNum[j]);
			else if(keyType[i]==KEY_TYPE_SIGN)
				for(int j=0;j<keySign.length;j++)
					sb.append(keySign[j]);
			else if(keyType[i]==KEY_TYPE_ENG_CAPITAL)
				for(int j=0;j<keyEngCapital.length;j++)
					sb.append(keyEngCapital[j]);
			else if(keyType[i]==KEY_TYPE_ENG_SMALL)
				for(int j=0;j<keyEngSmall.length;j++)
					sb.append(keyEngSmall[j]);
			else if(keyType[i]==KEY_TYPE_USER_PASSWORD)
				for(int j=0;j<keyUserPassword.length;j++)
					sb.append(keyUserPassword[j]);
		}
		StringBuffer rst	= new StringBuffer();
		for(int i=0;i<algorithm;i++){
			rst.append(sb.charAt((int)(Math.random()*1000*sb.length())/1000));
		}
		return rst.toString();
	}
	
	public static void main(String[] args) throws Exception{
		String key	= CryptoKeyGenerator.getRandomKey(32,new int[]{CryptoKeyGenerator.KEY_TYPE_NUM,CryptoKeyGenerator.KEY_TYPE_ENG_CAPITAL,CryptoKeyGenerator.KEY_TYPE_SIGN});
		System.out.println(key);
		
//		String key = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456";
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
		
	}
}
