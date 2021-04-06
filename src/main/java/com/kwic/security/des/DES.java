package com.kwic.security.des;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DES {
	public static final int MODE_ECB	= 1;
	public static final int MODE_CBC	= 2;
	
	public static byte[] encrypt(byte[] keyBytes,byte[] plainTextBytes,int mode) throws Exception{
		SecretKey key = new SecretKeySpec(keyBytes, "DESede");
        IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        Cipher cipher = Cipher.getInstance("DESede/"+(mode==MODE_ECB?"ECB":"CBC")+"/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        byte[] cipherText = cipher.doFinal(plainTextBytes);
        
        return cipherText;
	}
	public static byte[] decrypt(byte[] keyBytes,byte[] cipherTextBytes,int mode) throws Exception{
        SecretKey key = new SecretKeySpec(keyBytes, "DESede");
        IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        Cipher decipher = Cipher.getInstance("DESede/"+(mode==MODE_ECB?"ECB":"CBC")+"/PKCS5Padding");
        if(mode==MODE_ECB)
            decipher.init(Cipher.DECRYPT_MODE, key);
        else
        	decipher.init(Cipher.DECRYPT_MODE, key, iv);

        byte[] plainText = decipher.doFinal(cipherTextBytes);

        return plainText;
	}	
}
