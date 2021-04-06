package com.kwic.security.rsa;

import java.nio.ByteBuffer;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

public class RSA {
	public static final int KEY_SIZE_1024		= 1024;
	public static final int KEY_SIZE_2048		= 2048;
	
	public static final int PLAIN_BLOCK_SIZE_1024		= 117;
	public static final int PLAIN_BLOCK_SIZE_2048		= 245;
	
	public static RSA instance;
	
	private RSA(){
		if(Security.getProvider("BC")==null)
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());		
	}
	
	public static RSA getInstance(){
		synchronized(RSA.class){
			if(instance==null){
				instance	= new RSA();
			}
			return instance;
		}
	}
	
	private int getEncryptBlockSize(int keySize){
		return keySize/8;
	}
	
	private int getPlainBlockSize(int keySize){
		if(keySize==KEY_SIZE_1024)
			return PLAIN_BLOCK_SIZE_1024;
		else if(keySize==KEY_SIZE_2048)
			return PLAIN_BLOCK_SIZE_2048;
		else
			return -1;
	}
	
	/**
	 * generate RSA key pair
	 * */
	public RSAKeyPair generateKey(int keySize) throws Exception{
		if(keySize!=KEY_SIZE_1024 && keySize!=KEY_SIZE_2048)
			throw new Exception("Invalid Key Size ["+keySize+"]. Please refer to the following. -> RSA.KEY_SIZE_1024, RSA.KEY_SIZE_2048");
		
		SecureRandom random	= new SecureRandom();
		KeyPairGenerator generator	= KeyPairGenerator.getInstance("RSA");
		
		generator.initialize(keySize, random);
		KeyPair pair	= generator.generateKeyPair();
		
		Key publicKey	= pair.getPublic();
		Key privateKey	= pair.getPrivate();
		
		return new RSAKeyPair(publicKey,privateKey,keySize);
	}
	
	public byte[] encrypt(byte[] plain,Key publicKey) throws Exception{
		Cipher cipher	= Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE,publicKey);
		byte[] enc	= cipher.doFinal(plain);
		return enc;
	}
	
	public byte[] encryptBlock(byte[] plain,Key publicKey, int keyLength) throws Exception{
		int encBlockSize	= getEncryptBlockSize(keyLength);
		int plainBolckSize	= getPlainBlockSize(keyLength);
				
		int idx	= 0;
		byte[] bytes	= null;
		
		ByteBuffer bb	= ByteBuffer.allocate(encBlockSize*(plain.length/plainBolckSize+(plain.length%plainBolckSize==0?0:1)));
		while(idx<plain.length){
			bytes	= new byte[(plain.length-idx-plainBolckSize)<0?plain.length-idx:plainBolckSize];
			System.arraycopy(plain, idx, bytes, 0, bytes.length);
			idx	+= bytes.length;
			bb.put(encrypt(bytes,publicKey));
		}
		
		byte[] enc	= new byte[bb.position()];
		bb.rewind();
		bb.get(enc);
		
		return enc;
	}
	
	public byte[] decrypt(byte[] encrypt,Key privateKey) throws Exception{
		Cipher cipher	= Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE,privateKey);
		byte[] dec	= cipher.doFinal(encrypt);
		return dec;
	}
	
	public byte[] decryptBlock(byte[] encrypt,Key privateKey, int keyLength) throws Exception{
		int encBlockSize	= getEncryptBlockSize(keyLength);
		int plainBolckSize	= getPlainBlockSize(keyLength);

		int idx	= 0;
		byte[] bytes	= null;
		
		ByteBuffer bb	= ByteBuffer.allocate(plainBolckSize*(encrypt.length/encBlockSize+(encrypt.length%encBlockSize==0?0:1)));
		while(idx<encrypt.length){
			bytes	= new byte[encrypt.length-idx-encBlockSize<0?encrypt.length-idx:encBlockSize];
			System.arraycopy(encrypt, idx, bytes, 0, bytes.length);
			bb.put(decrypt(bytes,privateKey));
			idx	+= bytes.length;
		}
		byte[] dec	= new byte[bb.position()];
		bb.rewind();
		bb.get(dec);
		return dec;
	}
	
	public void test(RSAKeyPair keyPair,byte[] plain) throws Exception{
		byte[] enc	= RSA.getInstance().encryptBlock(plain,keyPair.getPublicKey(),keyPair.getKeySize());
		System.out.println("encoded : "+Base64.encodeBase64String(enc));
		byte[] dec	= RSA.getInstance().decryptBlock(enc,keyPair.getPrivateKey(),keyPair.getKeySize());
		System.out.println("decoded : "+new String(dec));
	}
	public void commonTest(RSAKeyPair keyPair,byte[] plain) throws Exception{
		System.out.println("=================== commonTest ======================");
		test(keyPair,plain);
	}
	public void pemTest(RSAKeyPair keyPair,byte[] plain) throws Exception{
		System.out.println("====================== pemTest ======================");
		//make pem from publicKey
		String publicKeyPem	= keyPair.publicKeyToPem();
		System.out.println(publicKeyPem);
		//recover publicKey from pem
		Key publicKey	= keyPair.publicKeyFromPem(keyPair.publicKeyToPem());
		
		//make pem from privateKey
		String privateKeyPem	= keyPair.privateKeyToPem();
		System.out.println(privateKeyPem);
		//recover privateKey from pem
		Key privateKey	= keyPair.privateKeyFromPem(keyPair.privateKeyToPem());
		
		test(new RSAKeyPair(publicKey,privateKey,keyPair.getKeySize()),plain);
	}
	public void encryptPrivateKeyPemTest(RSAKeyPair keyPair,byte[] plain) throws Exception{
		System.out.println("============= encryptPrivateKeyPemTest ==============");
		String password	= "password12!@";
		
		//make pem from publicKey
		String publicKeyPem	= keyPair.publicKeyToPem();
		System.out.println(publicKeyPem);
		//recover publicKey from pem
		Key publicKey	= keyPair.publicKeyFromPem(keyPair.publicKeyToPem());
		
		//make pem from privateKey with password
		String privateKeyPem	= keyPair.privateKeyToPem(password);
		System.out.println(privateKeyPem);
		//recover privateKey from pem with password
		Key privateKey	= keyPair.privateKeyFromPem(keyPair.privateKeyToPem(password),password);
		
		test(new RSAKeyPair(publicKey,privateKey,keyPair.getKeySize()),plain);
	}
	
	public static void main(String[] args) throws Exception{
		//키 생성
		RSAKeyPair keyPair	= RSA.getInstance().generateKey(RSA.KEY_SIZE_2048);
		//공개키
		String publicKey	= keyPair.publicKeyToBase64();
		System.out.println("base64 public key : "+publicKey);
		//개인키
		String privateKey	= keyPair.privateKeyToBase64();
		System.out.println("base64 private key : "+privateKey);
		
		byte[] plain	= "아름다운 우리나라 내가태어나 살고 있는 곳아름다운 우리나라 내가태어나 살고 있는 곳아름다운 우리나라 내가태어나 살고 있는 곳아름다운 우리나라 내가태어나 살고 있는 곳아름다운 우리나라 내가태어나 살고 있는 곳아름다운 우리나라 내가태어나 살고 있는 곳아름다운 우리나라 내가태어나 살고 있는 곳아름다운 우리나라 내가태어나 살고 있는 곳아름다운 우리나라 내가태어나 살고 있는 곳아름다운 우리나라 내가태어나 살고 있는 곳아름다운 우리나라 내가태어나 살고 있는 곳아름다운 우리나라 내가태어나 살고 있는 곳".getBytes();
		
		System.out.println("plain : "+new String(plain));
		RSA.getInstance().commonTest(keyPair,plain);
		RSA.getInstance().pemTest(keyPair,plain);
		RSA.getInstance().encryptPrivateKeyPemTest(keyPair,plain);
		
	}
	
}
