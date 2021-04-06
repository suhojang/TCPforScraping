package com.kwic.datasource;

import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import org.apache.commons.dbcp.BasicDataSource;

import com.kwic.security.aes.AESCipher;
/**
 * context-datasource.xml 에 정의된 암호화된 정보로 dataSource 생성
 * context-datasource.xml을 작성할 암호화 문자열 생성방법은 main 메소드 참조
 * 
 *   &lt;bean id="dataSource" class="com.kwic.datasource.EncryptDatasource" destroy-method="close"&gt;
 *       &lt;property name="driverClassName" value="com.microsoft.sqlserver.jdbc.SQLServerDriver"/&gt;
 *       &lt;property name="url" value="jYfPTVHEoF3cQrJpBdD6WZhPinYkF/7WwtvWmFAGnrrBCo1/1Ale3nrEiu/olJiVZpb5ngpB4j4zPTz3giuVia3OEb1Ft3LuwOcc24lDH7w=" /&gt;
 *       &lt;property name="username" value="K7XSlnC6YwymHviyoTVaHg=="/&gt;
 *       &lt;property name="password" value="Ep08QaBd/fNBJ0KTGC8U4g=="/&gt;
 *   &lt;/bean&gt;
 * 
 * */
public class EncryptDatasource extends BasicDataSource {
	
	/**
	 * 암복호화를 위한 비밀키
	 */
	private static final String	ENCRYPT_KEY	= "ABCDEfghijk12345zxcvECXStyui0987";
	
	/**
	 * driverClassName 복호화
	 * */
	@Override
	public synchronized void setDriverClassName(String driverClassName){
		super.setDriverClassName(decrypt(driverClassName));
	}
	
	/**
	 * url 복호화
	 * */
	@Override
    public synchronized void setUrl(String url) {
		super.setUrl(decrypt(url));
    }

	/**
	 * username 복호화
	 * */
	@Override
    public void setUsername(String username) {
		super.setUsername(decrypt(username));
    }
	
	/**
	 * password 복호화
	 * */
	@Override
    public void setPassword(String password) {
		super.setPassword(decrypt(password));
    }
	
	/**
	 * autocommit
	 * */
	@Override
    public void setDefaultAutoCommit(boolean autoCommit) {
		super.setDefaultAutoCommit(autoCommit);
    }
	
	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 암호화
	 * */
	public static final String encrypt(String plain){
		String encrypt	= plain;
		try{
			encrypt	= AESCipher.encode(plain, EncryptDatasource.ENCRYPT_KEY, AESCipher.TYPE_256);
		}catch(Exception e){
			//암호화를 실패하였습니다.
		}
		return encrypt;
	}
	/**
	 * 복호화
	 * */
	public static final String decrypt(String encrypt){
		String plain = encrypt;
		try{
			plain = AESCipher.decode(encrypt, EncryptDatasource.ENCRYPT_KEY, AESCipher.TYPE_256);
		}catch(Exception e){
			//복호화를 실패하였습니다.
		}
		return plain;
	}
	
	public static void main(String[] args) throws Exception{
		String url	= "jdbc:postgresql://127.0.0.1:5432/kais";
		String username	= "kaisadmin";
		String password	= "kwic$5539!@";
		String driverClassName	= "org.postgresql.Driver";
		
		String encodeText = EncryptDatasource.encrypt(url);
		System.out.println("url encrypt : "+encodeText);
		String decodeText = EncryptDatasource.decrypt(encodeText);
		System.out.println("url decrypt : "+decodeText);
		
		encodeText = EncryptDatasource.encrypt(username);
		System.out.println("username encrypt : "+encodeText);
		decodeText = EncryptDatasource.decrypt(encodeText);
		System.out.println("username decrypt : "+decodeText);
		
		encodeText = EncryptDatasource.encrypt(password);
		System.out.println("password encrypt : "+encodeText);
		decodeText = EncryptDatasource.decrypt(encodeText);
		System.out.println("password decrypt : "+decodeText);
		
		encodeText = EncryptDatasource.encrypt(driverClassName);
		System.out.println("driverClassName encrypt : "+encodeText);
		decodeText = EncryptDatasource.decrypt(encodeText);
		System.out.println("driverClassName decrypt : "+decodeText);
		
		System.out.println(EncryptDatasource.decrypt("Nhcwiyhzb0pNQcZUHPFnAnnDt/qJ2lm4LJpJ3meGGfE="));
		String encd = AESCipher.encode("kwic5539!@", AESCipher.DEFAULT_KEY,AESCipher.TYPE_256,"UTF-8",AESCipher.MODE_ECB_NOPADDING);
		System.out.println(encd);
		
		System.out.println(EncryptDatasource.decrypt("nF1U5x2H5B/tQB8SvqV+CMqjM0G0PrS//vNpg1Lz0vdvyHlU/PCsM3s/mII0ezO2"));
		System.out.println(AESCipher.decode("ti3Q3WjEftgluYCWBEpkrI3fg0kvuZRlGzzmJg0EmBMtj7/fM1Jf9m9RX0R5hZGKtmva48so3UB8m0UjqH3P1FeEf7t9HU5CWYtp34qsCYf06agERPAUcpmK1sRIf/rM9loUtCgeeplRMOonaekRfbZr2uPLKN1AfJtFI6h9z9S2a9rjyyjdQHybRSOofc/Utmva48so3UB8m0UjqH3P1LZr2uPLKN1AfJtFI6h9z9S2a9rjyyjdQHybRSOofc/Uu/VPUPin3dJe/cHdq/NeTw==", AESCipher.DEFAULT_KEY,AESCipher.TYPE_256,"UTF-8",AESCipher.MODE_ECB_NOPADDING));
		System.out.println(AESCipher.decode("hf4qjy2YeZymgwMlrzzUhOnUGucySdbHv1FCBf/koe3KEZtz18t/Z4jiBzX3X7ED80klHK6y/fWX7nEz3WFS8KKHTPJWYEI/nSEFr/nMzBImktGx6goBoJ1uzR8YwLnIbwGx1ixVCiLCP+32pXYAV918JD6CXpFjbX692E48d9xIModOyPq45viT60gGhEZMOCUl7zF0wxNjx/N0zewgSsoa2vVX4mhtlnd7UXjAUjKuEdgYbUo1I3kgZWJrMJzO74RN4MoOS1NR9uKTqmp4txqtsaoEv2zeEZ3IgAamSNQDwOZ14VxsEZPacl5Dh0zMfmqXuu6ICqJLpYaskoqGjMG8stH8LHzaVgeji8bht5k4v8truPQKRFrO4bQzRdluwHsJpH30bAcmlOl2QiXWQHSYt4I95L3PcR2ZkC1I/llWJ/AD9upfE/YcjlD85Gy1e+Z55GSV3Zzp6h3fa+5LnA/6/UAFeCzgg4huXy7SKpp1cTWOvgd6wBK9FszJmKXq0+2OYHd0dvt16JyOVdViI38KOyNkMo6+/TOzYfgUbbq3NjIh8B9VLR5RccmdoHruIMExfn9lEMiymgte5GAznMHnx4NWaDfiQdvRAhgMK1a/CNeJsVuq/4Gjt9qV9QN1", AESCipher.DEFAULT_KEY,AESCipher.TYPE_256,"UTF-8",AESCipher.MODE_ECB_NOPADDING));
		System.out.println(AESCipher.decode("bymVlV5OYbNgTB8w35g9ySrlzrfeYFI6RgycHkI7OJEMbkDFKLh9pMtHXH949erhlpveEToFbNiyMmP6j7YYD07LLhZwLuq8BsnEjJHakWcGvlmuXhJIFbkLvof0xUVTxCHqidaWy+bF5/wQ67bg2v0yPs8ccQ54b+1nTmnAN63Dy/Lo7hZBrjnouLJ8vfqrtmva48so3UB8m0UjqH3P1LZr2uPLKN1AfJtFI6h9z9RqdvErVeHOn6qFdGq3owvTc9gchb6Of9HkYK+YtFFVILZr2uPLKN1AfJtFI6h9z9R2GK1xECdKyx/eDN2BU6u/3J7gaP6HdNAX3Fg4nQpcQChCo5BZVecEap08iaHQ6buHXPQVMLm8tHnoxSMBD/V0tmva48so3UB8m0UjqH3P1LZr2uPLKN1AfJtFI6h9z9S2a9rjyyjdQHybRSOofc/Utmva48so3UB8m0UjqH3P1LZr2uPLKN1AfJtFI6h9z9SbptNA0SDQn5Gf/DVNdemPv98+BpBDxB05t/1tp659vyENlbswWmMD2dX4btj4YAw=", AESCipher.DEFAULT_KEY,AESCipher.TYPE_256,"UTF-8",AESCipher.MODE_ECB_NOPADDING));
		
		System.out.println(EncryptDatasource.decrypt("+jkRUcoJqpwoszSrFMMdJQ=="));
	}
}
