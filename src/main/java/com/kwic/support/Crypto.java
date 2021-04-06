package com.kwic.support;

import com.kwic.security.base64.SBase64;
import com.kwic.security.seed.SeedUtil;

public class Crypto {
    private static String paddingKey(String key){
        byte bytes[] = key.getBytes();
        int len = bytes.length / 16;
        if(bytes.length % 16 != 0)
            len++;
        byte returnBytes[] = new byte[16 * len];
        System.arraycopy(bytes, 0, returnBytes, 0, bytes.length);
        for(int i = bytes.length; i < returnBytes.length; i++)
            returnBytes[i] = 49;

        return new String(returnBytes);
    }

    private static byte[] trim(byte bytes[]){
        int idx = bytes.length;
        for(int i = 0; i < bytes.length; i++)
        {
            if(bytes[i] != 0)
                continue;
            idx = i;
            break;
        }

        byte returnBytes[] = new byte[idx];
        System.arraycopy(bytes, 0, returnBytes, 0, idx);
        return returnBytes;
    }

    public static byte[] encryptBytes(String plain, String key, String encoding) throws Exception{
        if(plain == null || plain.getBytes().length == 0)
        {
            return plain.getBytes();
        } else
        {
            key = paddingKey(key);
            SeedUtil util = new SeedUtil();
            byte encrypt[] = util.encrypt(plain, key, encoding, true);
            return encrypt;
        }
    }

    public static String encrypt(String plain, String key, String encoding) throws Exception {
        if(plain == null || plain.getBytes().length == 0)
        {
            return plain;
        } else
        {
            key = paddingKey(key);
            SeedUtil util = new SeedUtil();
            byte encrypt[] = util.encrypt(plain, key, encoding, true);
            return new String(encrypt);
        }
    }

    public static byte[] decryptBytes(String encrypt, String key, String encoding) throws Exception {
        if(encrypt == null || encrypt.getBytes().length == 0)
        {
            return encrypt.getBytes();
        } else
        {
            key = paddingKey(key);
            SeedUtil util = new SeedUtil();
            byte decrypt[] = util.decrypt(encrypt, key, encoding, true);
            return trim(decrypt);
        }
    }

    public static String decrypt(String encrypt, String key, String encoding) throws Exception {
        if(encrypt == null || encrypt.getBytes().length == 0)
        {
            return encrypt;
        } else
        {
            key = paddingKey(key);
            SeedUtil util = new SeedUtil();
            byte decrypt[] = util.decrypt(encrypt, key, encoding, true);
            return new String(trim(decrypt));
        }
    }
	
	public static void main(String[] args) throws Exception{
	/*	//------------- 암호화 샘플 --------------
//		String plain	= "sgkim@kwic.co.kr";
//		String key		= "기웅정보";
//		key	= "$KWIC_#FC_MOBILE";
//		//암호화
//		String encrypt	= Crypto.encrypt(plain, key, "UTF-8");
////		encrypt	= "lxzW8BC1xIq6cUw+OCOEmw==";
////		encrypt	= "NfH44dpwjwMFRnt97A4zZg==";
//		System.out.println(encrypt);
//		//복호화
//		String decrypt	= Crypto.decrypt(encrypt, key, "UTF-8");
//		
//		System.out.println(decrypt);
		
		
		
		String base64	= "VERDWHZWSHFCM2I4L3J0azRXZ1kvdz09";
		String dec	= new String(SBase64.decodeBase64(base64.getBytes()));
		System.out.println(dec);
		String decrypt	= Crypto.decrypt(dec, "5F0D902300^@/GdxWZWTR/jff6j", "UTF-8");
		System.out.println(decrypt);*/
		
		
		String pwd	= "1234";
		String enc	= Crypto.encrypt(pwd, "$KWIC_#FC_MOBILE", "UTF-8");
		System.out.println("enc :: "+enc);
		
		String dec	= new String(Crypto.decryptBytes(enc, "$KWIC_#FC_MOBILE", "UTF-8"));
		System.out.println("dec :: "+ dec);
		
	}
	
	
	
	
}
