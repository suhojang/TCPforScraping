package com.kwic.support;

import com.kwic.telegram.tcp.JTcpManager;

public class TcpClient {
	public static final String DEFAULT_ENCODING	= "UTF-8";
	
	public static String connect(String ip,int port,String message) throws Exception{
		return new String(JTcpManager.getInstance().sendMessage(ip, port, message.getBytes(),true),DEFAULT_ENCODING); 
	}
	
	public static String connect(String ip,int port,String message,String encoding) throws Exception{
		return new String(JTcpManager.getInstance().sendMessage(ip, port, message.getBytes(),true),encoding); 
	}
	public static String connect(String ip,int port,String message,String outEncoding,String inEncoding) throws Exception{
		return new String(JTcpManager.getInstance().sendMessage(ip, port, message.getBytes(outEncoding),true),inEncoding); 
	}
	
	
	public static void main(String[] args) throws Exception{
		System.out.println(TcpClient.connect("192.168.24.160",11100,"0004^0000^20150414000238^000.000.000.000^XX:XX:XX:XX^8101061397318^김유진^충북^03^611420^30^7329QP"));
	}
}
