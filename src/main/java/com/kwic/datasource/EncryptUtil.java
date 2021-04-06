package com.kwic.datasource;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class EncryptUtil {
	
	public void encrypt(){
		BufferedReader br		= null;
		FileWriter fw			= null;
		String driverClassName	= null;
		String url				= null;
		String username			= null;
		String password			= null;
		String line	= System.getProperty("line.separator");
		try{
			br	= new BufferedReader(new InputStreamReader(System.in));
			System.out.println(line+"=============================== PLAIN TEXT ================================="+line);
			System.out.println("Input the database information, follow the instructions below."+line);
			System.out.println(line+"[driverClassName]");
			System.out.println("   ex) ");
			System.out.println("       MSSQL\t: com.microsoft.sqlserver.jdbc.SQLServerDriver");
			System.out.println("       ORACLE\t: oracle.jdbc.driver.OracleDriver");
			System.out.print("   >> ");
			driverClassName	= br.readLine().trim();
			
			System.out.println(line+"[url]");
			System.out.println("   ex) ");
			System.out.println("       MSSQL\t: jdbc:sqlserver://127.0.0.1:1433;DatabaseName=scapmntr;SelectMethod=Cursor");
			System.out.println("       ORACLE\t: jdbc:oracle:thin:@127.0.0.1:1521:scapmntr");
			System.out.print("   >> ");
			url				= br.readLine().trim();
			
			System.out.println(line+"[username]");
			System.out.println("   ex) ");
			System.out.println("       MSSQL\t: sa");
			System.out.println("       ORACLE\t: scott");
			System.out.print("   >> ");
			username		= br.readLine().trim();
			
			System.out.println(line+"[password]");
			System.out.println("   ex) ");
			System.out.println("       MSSQL\t: password123");
			System.out.println("       ORACLE\t: tiger");
			System.out.print("   >> ");
			password		= br.readLine().trim();
			
			StringBuffer sb	= new StringBuffer();
			
			sb.append(line+line+"============================= ENCRYPT TEXT ================================="+line).append(line);
			sb.append("[driverClassName]").append(line);
			sb.append("\t"+EncryptDatasource.encrypt(driverClassName)).append(line);
			sb.append(line+"[url]").append(line);
			sb.append("\t"+EncryptDatasource.encrypt(url)).append(line);
			sb.append(line+"[username]").append(line);
			sb.append("\t"+EncryptDatasource.encrypt(username)).append(line);
			sb.append(line+"[password]").append(line);
			sb.append("\t"+EncryptDatasource.encrypt(password)).append(line);
			sb.append(line+"============================================================================"+line+line).append(line);
			
			fw	= new FileWriter("encrypt.txt");
			fw.write(sb.toString());
			fw.flush();fw.close();
			
			System.out.println(sb.toString());
			System.out.println("Saved successfully. Check the file [encrypt.txt]."+line+line);
			
			System.out.println("To exit the program, input the enter key");
			br.readLine();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{if(br!=null)br.close();}catch(Exception e){}
			try{if(fw!=null)fw.close();}catch(Exception e){}
		}
	}
	
	public static void main(String[] args){
		new EncryptUtil().encrypt();
	}
}
