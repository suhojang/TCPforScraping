package com.kwic.security.certificate;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.util.io.pem.PemReader;

import com.kwic.io.JOutputStream;
import com.kwic.security.base64.Base64Util;


public class CertSign {
	 
	public static String toPem(Object cert) throws IOException{
		 StringWriter sw	= new StringWriter();
		 JcaPEMWriter pw		= new JcaPEMWriter(sw);
		 pw.writeObject(cert);
		 pw.flush();
		 pw.close();
		 return sw.toString();
	 }
	
	 public static Object fromPem(String str) throws IOException{
		 StringReader sr	= new StringReader(str);
		 PemReader pr		= null;
		 Object obj	= null;
		 try{
			 pr		= new PemReader(sr);
			 obj	= pr.readPemObject();
		 }catch(IOException e){
			 throw e;
		 }finally{
			 try{if(pr!=null)pr.close();}catch(Exception ex){}
		 }
		 
		 return obj;
	 }
	
	public static PublicKey getPublicKey(String derFilePath) throws Exception{
		FileInputStream in = new FileInputStream(derFilePath);  
		CertificateFactory cf = CertificateFactory.getInstance("x.509");   
		X509Certificate cert = (X509Certificate)cf.generateCertificate(in);  
		in.close();
		
		return cert.getPublicKey();
	}
	
	public static Certificate getCertificate(String derFilePath) throws Exception{
		FileInputStream in = new FileInputStream(derFilePath);  
		CertificateFactory cf = CertificateFactory.getInstance("x.509");   
		X509Certificate cert = (X509Certificate)cf.generateCertificate(in);  
		in.close();
		
		return cert;
	}
	
	public static PrivateKey getPrivateKey(String keyFilePath,String password) throws Exception{
		byte[] encKey			= null; 
		JOutputStream jos		= null;
		FileInputStream fis		= null;
		try{
			jos	= new JOutputStream();
			fis	= new FileInputStream(new File(keyFilePath));
			jos.write(new FileInputStream(new File(keyFilePath)));
			
			encKey	= jos.getBytes();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{if(jos!=null)jos.close();}catch(Exception e){}
			try{if(fis!=null)fis.close();}catch(Exception e){}
		}
		
		EncryptedPrivateKeyInfo epki	= new EncryptedPrivateKeyInfo(encKey);
		String OID	= epki.getAlgName();

		
		byte[] salt	= new byte[8];
		System.arraycopy(encKey, 20, salt, 0, 8);
		
		byte[] cBytes	= new byte[4];
		System.arraycopy(encKey, 30, cBytes, 2, 2);
		
		ByteBuffer buffer	= ByteBuffer.wrap(cBytes);
		buffer.order(ByteOrder.BIG_ENDIAN);
		int icnt	= buffer.getInt();
//		int icnt	= toInt(cBytes);
		
		byte[] dKey	= new byte[20];
		MessageDigest md	= MessageDigest.getInstance("SHA1");
		md.update(password.getBytes());
		md.update(salt);
		
		dKey	= md.digest();
		for(int i=1;i<icnt;i++){
			dKey	= md.digest(dKey);
		}
		
		byte[] key			= null;
		byte[] ivt			= null;
		byte[] div			= null;
		byte[] iv			= null;
		Provider provider	= new BouncyCastleProvider();
		
		if("1.2.410.200004.1.15".equals(OID)){//SHA1 & SEED CBC Mode
			key	= new byte[16];
			System.arraycopy(dKey, 0, key, 0, 16);
			
			ivt	= new byte[4];
			System.arraycopy(dKey, 16, ivt, 0, 4);

			div	= new byte[20];
			MessageDigest sha1	= MessageDigest.getInstance("SHA1");
			div	= sha1.digest(ivt);
			
			iv	= new byte[16];
			System.arraycopy(div, 0, iv, 0, 16);
			
		}else if("1.2.410.200004.1.4".equals(OID)){ //SEED CBC Mode
			key	= new byte[16];
			iv	= new byte[]{48,49,50,51,52,53,54,55,56,57,48,49,50,51,52,53};
		}else{
			throw new Exception("Not Supported OID ["+OID+"]");
		}
		
		Cipher cipher		= Cipher.getInstance("SEED/CBC/PKCS5Padding",provider);
		SecretKeySpec sKey	= new SecretKeySpec(key,"SEED");
		cipher.init(Cipher.DECRYPT_MODE, sKey,new IvParameterSpec(iv));
		
		byte[] decryptKey		= cipher.doFinal(epki.getEncryptedData());
		PKCS8EncodedKeySpec ks	= new PKCS8EncodedKeySpec(decryptKey);
		KeyFactory kf			= KeyFactory.getInstance("RSA");
		
		return (RSAPrivateCrtKey)kf.generatePrivate(ks);
	}
	
	public static final int toInt(byte[] src, int srcPos) {
		int dword = 0;
		for (int i = 0; i < 4; i++) {
			dword = (dword << 8) + (src[i + srcPos] & 0xFF);
		}
		return dword;
	}
	public static final int toInt(byte[] src) {
		return toInt(src, 0);
	}
	
	
	public static void main(String args[]) throws Exception{
//		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		String msg		= "아름다운우리나라";
		String password	= "j2g8j0s8!@";//인증서 암호
		String derPath	= "C:/Users/jjh/AppData/LocalLow/NPKI/yessign/USER/cn=장정훈()0020041200806232804005,ou=WOORI,ou=personal4IB,o=yessign,c=kr/signCert.der";
		String keyPath	= "C:/Users/jjh/AppData/LocalLow/NPKI/yessign/USER/cn=장정훈()0020041200806232804005,ou=WOORI,ou=personal4IB,o=yessign,c=kr/signPri.key";

		Signature sn	= Signature.getInstance("NONEwithRSA");//SHA1withRSA
		sn.initSign(getPrivateKey(keyPath,password));
		
		sn.update(msg.getBytes());
		byte[] sign	= sn.sign();
		String encodedSign	= Base64Util.encode(sign);
		System.out.println(encodedSign);
		
		// 전사서명 검증하기
		String msgB = msg;
		
		Signature sn2 = Signature.getInstance("NONEwithRSA");
		sn2.initVerify(getPublicKey(derPath));
		sn2.update(msgB.getBytes());
		
		byte[] decodedSign	= Base64Util.decode(encodedSign);
		boolean verifty = sn2.verify(decodedSign);
		System.out.println("검증 결과 : " + verifty);

//		String signature		= Base64Util.encode(sign);
//		X509Certificate cert	= (X509Certificate) getCertificate(derPath);
//		PublicKey pubkey		= cert.getPublicKey();
//		Cipher cipher			= Cipher.getInstance("SEED/CBC/PKCS5Padding","SunJCE");
//		cipher.init(Cipher.DECRYPT_MODE, pubkey);
//		
//		byte[] decodedSignature = Base64Util.decode(signature);
//		cipher.update(decodedSignature);
//		byte[] sha1 = cipher.doFinal();
//	  
//		System.out.println(new String(sha1));		
//		System.out.println(Base64Util.encode(sha1));		
		
		
	}
}


