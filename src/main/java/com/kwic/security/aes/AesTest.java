package com.kwic.security.aes;

public class AesTest {
	//VB Aes256 Key	= ABCDEFGHIJKLMNOPQRSTUVWXYZ123456
	public static void main(String[] args) throws Exception{

//		String key = "abcdefghijklmnopqrstuvwxyz123456";
		String key = "A!B1C@D2E#F3GHIJKLMNOPQRSTUVWXYZ";
//		key	= AESCipher.DEFAULT_KEY;
		
		String encodeText	= null;
		String decodeText	= null;
		// Encrypt
//		String plainText  = "abcdEFG한글임123당";
		String plainText  = "kwic5539!@";
		
		//aes 256 UTF-8,CBC
		encodeText = AESCipher.encode(plainText, key,AESCipher.TYPE_256);
		System.out.println("aes 256 UTF-8 CBC PKCS5Padding : "+encodeText);
		decodeText = AESCipher.decode(encodeText, key,AESCipher.TYPE_256);
		System.out.println("aes 256 UTF-8 CBC PKCS5Padding : "+decodeText);
		
		//aes 256 EUC-KR,CBC
		encodeText = AESCipher.encode(plainText, key,AESCipher.TYPE_256,"EUC-KR");
		System.out.println("aes 256 EUC-KR CBC PKCS5Padding : "+encodeText);
		
		//aes 256 UTF-8,ECB
		encodeText = AESCipher.encode(plainText, key,AESCipher.TYPE_256,AESCipher.MODE_ECB);
		System.out.println("aes 256 UTF-8 ECB PKCS5Padding : "+encodeText);
		
		//aes 256 UTF-8,ECB
		encodeText = AESCipher.encode(plainText, key,AESCipher.TYPE_256,AESCipher.MODE_ECB);
		System.out.println("aes 256 UTF-8 ECB PKCS5Padding : "+encodeText);
		
		
		//aes 256 UTF-8,ECB NoPadding
		encodeText = AESCipher.encode(plainText, key,AESCipher.TYPE_256,"UTF-8",AESCipher.MODE_ECB_NOPADDING);
		System.out.println("aes 256 UTF-8 ECB NoPadding : "+encodeText);
		
		
		//aes 256 EUC-KR,ECB
		encodeText = AESCipher.encode(plainText, key,AESCipher.TYPE_256,"EUC-KR",AESCipher.MODE_ECB);
		System.out.println("aes 256 EUC-KR ECB PKCS5Padding : "+encodeText);
		decodeText = AESCipher.decode(encodeText, key,AESCipher.TYPE_256,"EUC-KR",AESCipher.MODE_ECB_NOPADDING);
		System.out.println("aes 256 EUC-KR ECB PKCS5Padding decode : "+decodeText);
		
		//aes 256 EUC-KR,ECB NoPadding (couple with VB AES256)
		encodeText = AESCipher.encode(plainText, key,AESCipher.TYPE_256,"EUC-KR",AESCipher.MODE_ECB_NOPADDING);
		System.out.println("aes 256 EUC-KR ECB NoPadding : "+encodeText);
		
		decodeText = AESCipher.decode("bPaQaRkUtg830gI1jreXRA==", key,AESCipher.TYPE_256,"EUC-KR",AESCipher.MODE_ECB_NOPADDING);
		System.out.println("aes 256 EUC-KR ECB NoPadding decode : "+decodeText);
		

		
		System.out.println("\n\n");
		
		//aes 128 UTF-8,CBC
		encodeText = AESCipher.encode(plainText, key,AESCipher.TYPE_128);
		System.out.println("aes 128 UTF-8 CBC PKCS5Padding : "+encodeText);
		
		//aes 128 EUC-KR,CBC
		encodeText = AESCipher.encode(plainText, key,AESCipher.TYPE_128,"EUC-KR");
		System.out.println("aes 128 EUC-KR CBC PKCS5Padding : "+encodeText);
		
		//aes 128 UTF-8,ECB
		encodeText = AESCipher.encode(plainText, key,AESCipher.TYPE_128,AESCipher.MODE_ECB);
		System.out.println("aes 128 UTF-8 ECB PKCS5Padding : "+encodeText);
		
		//aes 128 EUC-KR,ECB
		encodeText = AESCipher.encode(plainText, key,AESCipher.TYPE_128,"EUC-KR",AESCipher.MODE_ECB);
		System.out.println("aes 128 EUC-KR ECB PKCS5Padding : "+encodeText);
		
		
		decodeText = AESCipher.decode(encodeText, key,AESCipher.TYPE_128,"EUC-KR",AESCipher.MODE_ECB);
		System.out.println("aes 128 EUC-KR ECB decode : "+decodeText);
		
		/*
		String key			= AESCipher.DEFAULT_KEY;
		String plainText	= "0234635975";
		
		String encodeText = AESCipher.encode(plainText, key,AESCipher.TYPE_256);
		System.out.println("aes 256 UTF-8 CBC PKCS5Padding : "+encodeText);
		String decodeText = AESCipher.decode(encodeText, key,AESCipher.TYPE_256);
		System.out.println("aes 256 UTF-8 CBC PKCS5Padding : "+decodeText);
		
		System.out.println(AESCipher.decode("nW+AFuNvBuLoopxmGasJ8w==", key,AESCipher.TYPE_256));
		
		
		
		System.out.println(AESCipher.encode("Kwic5539!!", AESCipher.DEFAULT_KEY,AESCipher.TYPE_256));
		*/
	}

}
