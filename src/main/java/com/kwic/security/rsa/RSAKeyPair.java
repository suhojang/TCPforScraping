package com.kwic.security.rsa;

import java.io.StringReader;
import java.io.StringWriter;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8EncryptorBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.util.io.pem.PemObject;

public class RSAKeyPair {
	private Key publicKey;
	private Key privateKey;
	private int keySize;
	
	public RSAKeyPair(){
		if(Security.getProvider("BC")==null)
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());		
	}
	
	public RSAKeyPair setPublicKey(Key publicKey){
		this.publicKey	= publicKey;
		return this;
	}
	public RSAKeyPair setPrivateKey(Key privateKey){
		this.privateKey	= privateKey;
		return this;
	}
	public RSAKeyPair setKeySize(int keySize){
		this.keySize	= keySize;
		return this;
	}
	
	public RSAKeyPair(Key publicKey,Key privateKey,int keySize){
		if(Security.getProvider("BC")==null)
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());		
		
		this.publicKey	= publicKey;
		this.privateKey	= privateKey;
		this.keySize	= keySize;
	}
	
	public static Key base64ToPublicKey(String base64KeyString) throws Exception{
		byte[] keyBytes	= Base64.decodeBase64(base64KeyString);
		X509EncodedKeySpec keySpec	= new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory	= KeyFactory.getInstance("RSA");
		Key key		= keyFactory.generatePublic(keySpec);
		return key;
	}
	public static Key base64ToPrivateKey(String base64KeyString) throws Exception{
		byte[] keyBytes	= Base64.decodeBase64(base64KeyString);
		PKCS8EncodedKeySpec keySpec	= new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory	= KeyFactory.getInstance("RSA");
		Key key		= keyFactory.generatePrivate(keySpec);
		return key;
	}
	
	public String publicKeyToPem() throws Exception{
		StringWriter sw	= null;
		JcaPEMWriter pw	= null;
		try{
			sw	= new StringWriter();
			pw	= new JcaPEMWriter(sw);
			pw.writeObject(publicKey);
		}catch(Exception e){
			throw e;
		}finally{
			try{if(pw!=null)pw.close();}catch(Exception e){}
		}
		return sw.toString();
	}
	
	public Key publicKeyFromPem(String pem) throws Exception{
		StringReader sr	= null;
		PEMParser pr	= null;
		Key key	= null;
		try{
			sr	= new StringReader(pem);
			pr	= new PEMParser(sr);
			Object obj	= pr.readObject();
			
			JcaPEMKeyConverter converter	= new JcaPEMKeyConverter().setProvider("BC");
			key	= converter.getPublicKey((SubjectPublicKeyInfo)obj);
		}catch(Exception e){
			throw e;
		}finally{
			try{if(pr!=null)pr.close();}catch(Exception e){}
		}
		return key;
	}
	
	public String privateKeyToPem() throws Exception{
		StringWriter sw	= null;
		JcaPEMWriter pw	= null;
		try{
			sw	= new StringWriter();
			pw	= new JcaPEMWriter(sw);
			pw.writeObject(privateKey);
		}catch(Exception e){
			throw e;
		}finally{
			try{if(pw!=null)pw.close();}catch(Exception e){}
		}
		return sw.toString();
	}
	public String privateKeyToPem(String pwd) throws Exception{
		StringWriter sw	= null;
		JcaPEMWriter pw	= null;
		try{
			sw	= new StringWriter();
			
			JceOpenSSLPKCS8EncryptorBuilder encryptorBuilder	= new JceOpenSSLPKCS8EncryptorBuilder(PKCS8Generator.PBE_SHA1_3DES);
		    encryptorBuilder.setRandom(new SecureRandom());
		    encryptorBuilder.setPasssword(pwd.toCharArray());
		    OutputEncryptor oe		= encryptorBuilder.build();
		    JcaPKCS8Generator gen	= new JcaPKCS8Generator((PrivateKey)privateKey,oe);
		    PemObject obj			= gen.generate();
			
			pw	= new JcaPEMWriter(sw);
			pw.writeObject(obj);
		}catch(Exception e){
			throw e;
		}finally{
			try{if(pw!=null)pw.close();}catch(Exception e){}
		}
		return sw.toString();
	}
	
	public Key privateKeyFromPem(String pem) throws Exception{
		StringReader sr	= null;
		PEMParser pr	= null;
		Key key	= null;
		try{
			sr	= new StringReader(pem);
			pr	= new PEMParser(sr);
			Object obj	= pr.readObject();
			
			JcaPEMKeyConverter converter	= new JcaPEMKeyConverter().setProvider("BC");
			PEMKeyPair keyPair	= (PEMKeyPair)obj;
			key	= converter.getPrivateKey(keyPair.getPrivateKeyInfo());
		}catch(Exception e){
			throw e;
		}finally{
			try{if(pr!=null)pr.close();}catch(Exception e){}
		}
		return key;
	}
	
	public Key privateKeyFromPem(String pem,String pwd) throws Exception{
		StringReader sr	= null;
		PEMParser pr	= null;
		Key key	= null;
		try{
			sr	= new StringReader(pem);
			pr	= new PEMParser(sr);
			
			PKCS8EncryptedPrivateKeyInfo privateKeyInfo	= (PKCS8EncryptedPrivateKeyInfo)pr.readObject();
			JceOpenSSLPKCS8DecryptorProviderBuilder jce	= new JceOpenSSLPKCS8DecryptorProviderBuilder().setProvider("BC");
			InputDecryptorProvider decProv	= jce.build(pwd.toCharArray());
			PrivateKeyInfo info				= privateKeyInfo.decryptPrivateKeyInfo(decProv);
			
			JcaPEMKeyConverter converter	= new JcaPEMKeyConverter().setProvider("BC");
			key	= converter.getPrivateKey(info);

		}catch(Exception e){
			throw e;
		}finally{
			try{if(pr!=null)pr.close();}catch(Exception e){}
		}
		return key;
	}
	
	public Key getPublicKey(){
		return publicKey;
	}
	
	public Key getPrivateKey(){
		return privateKey;
	}
	
	public String publicKeyToBase64(){
		return Base64.encodeBase64String(publicKey.getEncoded());
	}
	
	public String privateKeyToBase64(){
		return Base64.encodeBase64String(privateKey.getEncoded());
	}
	
	public int getKeySize(){
		return keySize;
	}
}
