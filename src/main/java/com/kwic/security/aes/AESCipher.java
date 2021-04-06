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
public class AESCipher {
	public static final String DEFAULT_KEY = "0^2/2a4T5!H@1#9%GDGsjbjip!@$752$";

	public static final int TYPE_128 = 16;
	public static final int TYPE_192 = 24;
	public static final int TYPE_256 = 32;

	public static final int MODE_ECB = 1;
	public static final int MODE_CBC = 2;
	public static final int MODE_ECB_NOPADDING = 3;

	private static final char[] keypadding = { 'a', '1', 'b', 'C', 'k', '!', 'e', '*', 'f', 'K', 'D', '8', 's', '4', 'W', 'p', 'G', 'a', 'd', '#', 'G', '7', '&', 'E', 'U', 'l', 'J', 'j', 'i', 'W', '2', 'Q' };

	private static byte[] ivBytes = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	/**
	 * 
	 * @param key
	 * @param blockSize
	 * @param enc
	 * @return
	 * @throws Exception
	 */
	private static String initKey(String key, int blockSize, String enc) throws Exception {
		byte[] keys = key.getBytes(enc);
		String newKey = null;
		if (keys.length > blockSize) {
			byte[] k = new byte[blockSize];
			System.arraycopy(keys, 0, k, 0, blockSize);
			newKey = new String(k);

			if (newKey.getBytes().length != blockSize) {
				newKey = initKey(newKey.substring(0, newKey.length() - 2), blockSize, enc);
			}
			return newKey;
		}

		int keyBlock = keys.length % blockSize == 0 ? keys.length / blockSize : keys.length / blockSize + 1;

		StringBuffer sb = new StringBuffer();
		sb.append(key);

		for (int i = keys.length; i < keyBlock * blockSize; i++) {
			sb.append(keypadding[i % blockSize]);
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param bytes
	 * @return
	 * @throws Exception
	 */
	private static byte[] padECBNoPaddingText(byte[] bytes) throws Exception {
		int size = bytes.length;
		int block = 1;
		block = size / 16;
		if (size % 16 > 0){
			block++;
		}

		byte[] newBytes = new byte[block * 16];
		System.arraycopy(bytes, 0, newBytes, 0, size);

		for (int i = size; i < block * 16; i++){
			newBytes[i] = 0x00;
		}
		return newBytes;
	}

	/**
	 * 
	 * @param bytes
	 * @return
	 * @throws Exception
	 */
	private static byte[] removeECBNoPaddingText(byte[] bytes) throws Exception {
		int idx = bytes.length;
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] == 0x00) {
				idx = i;
				break;
			}
		}
		byte[] newBytes = new byte[idx];
		System.arraycopy(bytes, 0, newBytes, 0, idx);
		return newBytes;
	}

	/**
	 * 
	 * @param str
	 * @param okey
	 * @param blockSize
	 * @return
	 * @throws Exception
	 */
	public static String encode(String str, String okey, int blockSize) throws Exception {
		return encode(str, okey, blockSize, "UTF-8", MODE_CBC);
	}

	/**
	 * 
	 * @param str
	 * @param okey
	 * @param blockSize
	 * @param enc
	 * @return
	 * @throws Exception
	 */
	public static String encode(String str, String okey, int blockSize, String enc) throws Exception {
		return encode(str, okey, blockSize, enc, MODE_CBC);
	}

	/**
	 * 
	 * @param str
	 * @param okey
	 * @param blockSize
	 * @param mode
	 * @return
	 * @throws Exception
	 */
	public static String encode(String str, String okey, int blockSize, int mode) throws Exception {
		return encode(str, okey, blockSize, "UTF-8", mode);
	}

	/**
	 * 블록 암호화
	 * @param str 암호화 대상 원문
	 * @param okey 암호화 키
	 * @param blockSize 블럭 크기
	 * @param enc 인코딩
	 * @param mode 암호화 모드
	 * @return
	 * @throws Exception
	 */
	public static String encode(String str, String okey, int blockSize, String enc, int mode) throws Exception {
		if (str == null || "".equals(str)) {
			return str;
		}
		String key = initKey(okey, blockSize, enc);
		byte[] textBytes = str.getBytes(enc);
		SecretKeySpec newKey = new SecretKeySpec(key.getBytes(enc), "AES");
		Cipher cipher = null;
		if (mode == MODE_ECB) {
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, newKey);
		} else if (mode == MODE_ECB_NOPADDING) {
			cipher = Cipher.getInstance("AES/ECB/NoPadding");
			textBytes = padECBNoPaddingText(textBytes);
			cipher.init(Cipher.ENCRYPT_MODE, newKey);
		} else if (mode == MODE_CBC) {
			AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
		} else {
			throw new NoSuchAlgorithmException("Unknown encryption mode [" + mode + "].");
		}
		return Base64.encodeBase64String(cipher.doFinal(textBytes));
	}

	/**
	 * 
	 * @param str
	 * @param okey
	 * @param blockSize
	 * @return
	 * @throws Exception
	 */
	public static String decode(String str, String okey, int blockSize) throws Exception {
		return decode(str, okey, blockSize, "UTF-8", MODE_CBC);
	}

	/**
	 * 
	 * @param str
	 * @param okey
	 * @param blockSize
	 * @param enc
	 * @return
	 * @throws Exception
	 */
	public static String decode(String str, String okey, int blockSize, String enc) throws Exception {
		return decode(str, okey, blockSize, enc, MODE_CBC);
	}

	/**
	 * 
	 * @param str
	 * @param okey
	 * @param blockSize
	 * @param mode
	 * @return
	 * @throws Exception
	 */
	public static String decode(String str, String okey, int blockSize, int mode) throws Exception {
		return decode(str, okey, blockSize, "UTF-8", mode);
	}

	/**
	 * 
	 * @param str
	 * @param okey
	 * @param blockSize
	 * @param enc
	 * @param mode
	 * @return
	 * @throws Exception
	 */
	public static String decode(String str, String okey, int blockSize, String enc, int mode) throws Exception {
		if (str == null || "".equals(str)) {
			return str;
		}
		String key = initKey(okey, blockSize, enc);

		byte[] textBytes = Base64.decodeBase64(str);
		SecretKeySpec newKey = new SecretKeySpec(key.getBytes(enc), "AES");
		Cipher cipher = null;
		byte[] dBytes = null;
		if (mode == MODE_ECB) {
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, newKey);
			dBytes = cipher.doFinal(textBytes);
		} else if (mode == MODE_ECB_NOPADDING) {
			cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, newKey);
			dBytes = cipher.doFinal(textBytes);
			dBytes = removeECBNoPaddingText(dBytes);
		} else if (mode == MODE_CBC) {
			AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
			dBytes = cipher.doFinal(textBytes);
		} else {
			throw new NoSuchAlgorithmException("Unknown encryption mode [" + mode + "].");
		}

		return new String(dBytes, enc);
	}

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		String plain =  AESCipher.decode("MGL3U1CencbZsWnBjCwvTA==", AESCipher.DEFAULT_KEY, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING);
		System.out.println(plain);

		/*
		// String plain = "jang2808";
		String encKey = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456";

		// String enc = AESCipher.encode(plain,
		// encKey,AESCipher.TYPE_256,"UTF-8",AESCipher.MODE_ECB_NOPADDING);
		// System.out.println(enc);

		String enc = "aCO1F+I6QIAwpRChOr1Tzg==";
		String dec = AESCipher.decode(enc, encKey, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING);
		System.out.println(dec);
		enc = "HTA4FbafrYY8KWFWnTnrDw==";
		dec = AESCipher.decode(enc, encKey, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING);
		System.out.println(dec);
		enc = "VFyBq+pCQEmqxa11IuALPA==";
		dec = AESCipher.decode(enc, encKey, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING);
		System.out.println(dec);
		enc = "ITnfsO+CAcs0HqtIue9+eg==";
		dec = AESCipher.decode(enc, encKey, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING);
		System.out.println(dec);
		enc = "V9QWooioeQFK8hEUuGiQjA==";
		dec = AESCipher.decode(enc, encKey, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING);
		System.out.println(dec);
		enc = "d5zXvd9s3cilAaTcnC8ejQ==";
		dec = AESCipher.decode(enc, encKey, AESCipher.TYPE_256, "UTF-8", AESCipher.MODE_ECB_NOPADDING);
		System.out.println(dec);
*/
		//
		// enc = "VERDWHZWSHFCM2I4L3J0azRXZ1kvdz09";
		// enc = new String(Base64.decodeBase64(enc));
		// dec = AESCipher.decode(enc,
		// encKey,AESCipher.TYPE_256,"EUC-KR",AESCipher.MODE_ECB_NOPADDING);
		// System.out.println(dec);

	}

}