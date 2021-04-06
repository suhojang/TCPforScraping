package com.kwic.security.aria;

import java.nio.ByteBuffer;

public class ARIA {
	private static final int ROUND_KEY_SIZE_128	= 128;
	private static final int ROUND_KEY_SIZE_192	= 192;
	private static final int ROUND_KEY_SIZE_256	= 256;
	
	private static final int KEY_SIZE_16	= 16;
	private static final int KEY_SIZE_24	= 24;
	private static final int KEY_SIZE_32	= 32;
	
	private static final int ENCRYPT_BLOCK_SIZE	= 16;
	private static final int PLAIN_BLOCK_SIZE	= 16;
	
	private static int getRoundKeySize(int masterKeySize){
		if(masterKeySize==KEY_SIZE_16)
			return ROUND_KEY_SIZE_128;
		else if(masterKeySize==KEY_SIZE_24)
			return ROUND_KEY_SIZE_192;
		if(masterKeySize==KEY_SIZE_32)
			return ROUND_KEY_SIZE_256;
		return -1;
	}
	
	public static byte[] encrypt(byte[] plain,byte[] masterKey) throws Exception{
		int roundKeySize	= getRoundKeySize(masterKey.length);
		
	    ARIAEngine instance = new ARIAEngine(roundKeySize);
		instance.reset();
		instance.setKeySize(roundKeySize);
	    instance.setKey(masterKey);
	    instance.setupRoundKeys();
		
	    byte[] p	= padding(plain,ENCRYPT_BLOCK_SIZE);
	    	
	    byte[] enc	= new byte[ENCRYPT_BLOCK_SIZE];
	    instance.encrypt(p, 0, enc, 0);
	    
	    return enc;
	}
	
	public static byte[] encryptBlock(byte[] plain,byte[] masterKey) throws Exception{
		int idx	= 0;
		byte[] bytes	= null;
		
		ByteBuffer bb	= ByteBuffer.allocate(ENCRYPT_BLOCK_SIZE*(plain.length/PLAIN_BLOCK_SIZE+(plain.length%PLAIN_BLOCK_SIZE==0?0:1)));
		while(idx<plain.length){
			bytes	= new byte[(plain.length-idx-PLAIN_BLOCK_SIZE)<0?plain.length-idx:PLAIN_BLOCK_SIZE];
			System.arraycopy(plain, idx, bytes, 0, bytes.length);
			idx	+= bytes.length;
			bb.put(encrypt(bytes,masterKey));
		}
		
		byte[] enc	= new byte[bb.position()];
		bb.rewind();
		bb.get(enc);
		
		return enc;
	}
	
	public static byte[] decrypt(byte[] enc,byte[] masterKey) throws Exception{
		int roundKeySize	= getRoundKeySize(masterKey.length);
		
	    ARIAEngine instance = new ARIAEngine(roundKeySize);
		instance.reset();
		instance.setKeySize(roundKeySize);
	    instance.setKey(masterKey);
	    instance.setupRoundKeys();
		
		byte[] dec		= new byte[enc.length];
	    instance.decrypt(enc, 0, dec, 0);
		
	    byte[] d	= trim(dec);
	    return d;
	}
	
	public static byte[] decryptBlock(byte[] enc,byte[] masterKey) throws Exception{
		int idx	= 0;
		byte[] bytes	= null;
		
		ByteBuffer bb	= ByteBuffer.allocate(PLAIN_BLOCK_SIZE*(enc.length/ENCRYPT_BLOCK_SIZE+(enc.length%ENCRYPT_BLOCK_SIZE==0?0:1)));
		while(idx<enc.length){
			bytes	= new byte[enc.length-idx-ENCRYPT_BLOCK_SIZE<0?enc.length-idx:ENCRYPT_BLOCK_SIZE];
			System.arraycopy(enc, idx, bytes, 0, bytes.length);
			bb.put(decrypt(bytes,masterKey));
			idx	+= bytes.length;
		}
		byte[] dec	= new byte[bb.position()];
		bb.rewind();
		bb.get(dec);
		return dec;
	}
	public static byte[] padding(byte[] bytes,int size){
		byte[] p	= bytes;
	    if(bytes.length<size){
		    p	= new byte[size];
		    System.arraycopy(bytes, 0, p, 0, bytes.length);
	    }
		return p;
	}
	public static byte[] trim(byte[] bytes){
		int idx	= -1;
		for(int i=bytes.length-1;i>=0;i--){
			if(bytes[i]==0x00)
				idx	= i;
			else
				break;
		}
		byte[] b	= bytes;
		if(idx!=-1){
			b	= new byte[idx];
			System.arraycopy(bytes, 0, b, 0, idx);
		}
		return b;
	}
	
	
	public static void main(String[] args) throws Exception{
		byte[] plain	= "http://www.minwon.go.kr/fdnbdfnbdfnbdgfndfndfndndbdndfndnvsdsdvfsvdsv".getBytes();
//		byte[] plain	= "http://".getBytes();
		System.out.println(new String(plain));
		byte[] key	= "12345678901234561234567890123456".getBytes();
		byte[] enc	= encryptBlock(plain,key);
		System.out.println(org.apache.commons.codec.binary.Base64.encodeBase64String(enc));
		byte[] dec	= decryptBlock(enc,key);
		System.out.println(new String(dec));
	}
}
