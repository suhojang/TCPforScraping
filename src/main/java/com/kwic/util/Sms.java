package com.kwic.util;

/*
============================================================================================
== ���ϸ� : sms.java
============================================================================================
* ��  �� : ���� �Լ�
* �ۼ��� : ����������(��) ������
* �ۼ��� : 2003-10-10 18:53:08
*/


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Sms {
	
	public synchronized static String sendSms(Map<String, String> map,String msg) throws Exception{
		if(msg.getBytes("euc-kr").length<=80)
			return sendSms(map);
		int idx1	= 0;
		List<String> msgList	= new ArrayList<String>();
		for(int i=0;i<msg.length();i++){
			if(msg.substring(idx1,i).getBytes("euc-kr").length>=80){
				msgList.add(msg.substring(idx1,i));
				idx1	= i;
			}
		}
		if(idx1<msg.length())
			msgList.add(msg.substring(idx1));
		
		String rst	= null;
		for(int i=0;i<msgList.size();i++){
			map.put("MSG_TXT", msgList.get(i));
			rst	= sendSms(map);
			if(!"SMOK".equals(rst)){
				return rst;
			}
		}
		return rst;
	}
//	public static void main(String[] args) throws Exception{
//		String msg	= "123456789012345678901234567890123456789012345678901234567890123456789012345678901";
//		sendSms(new HashMap<String, String>(),msg);
//	}
	
	
	
	public synchronized static String sendSms(Map<String, String> map) throws Exception{
		
		String msg = "";
		
		//----------------------------------------------------------------------
		// Url Connection Post���
		//----------------------------------------------------------------------
        StringBuffer sb = new StringBuffer();
		StringBuffer sbQry = new StringBuffer();
	    sbQry.append("ComID="	 + URLEncoder.encode("kwic","euc-kr"));
		sbQry.append("&ComPass=" + URLEncoder.encode("kwic5539","euc-kr"));
		//2015.10.14 SMS ���� ��ȣ ��å �������� ���Ͽ� ������ ��� ��ȭ��ȣ�� 02-1588-5976 �����ͷ� ����. (������ SMS ��ȭ��ȣ�� �̸� ��� �Ǿ� �־�� �Ѵ�.)
		String sphone2 = "1588";
		String sphone3 = "5976";
		sbQry.append("&sphone1=" + URLEncoder.encode("","euc-kr"));
		sbQry.append("&sphone2=" + URLEncoder.encode(sphone2,"euc-kr"));
		sbQry.append("&sphone3=" + URLEncoder.encode(sphone3,"euc-kr"));
		
		sbQry.append("&rphone1=" + URLEncoder.encode((String)map.get("rphone1"),"euc-kr"));
		sbQry.append("&rphone2=" + URLEncoder.encode((String)map.get("rphone2"),"euc-kr"));
		sbQry.append("&rphone3=" + URLEncoder.encode((String)map.get("rphone3"),"euc-kr"));
		sbQry.append("&UserID="  + URLEncoder.encode("fctools","euc-kr"));
		sbQry.append("&SDate="   + URLEncoder.encode("00000000","euc-kr"));
		sbQry.append("&STime="   + URLEncoder.encode("000000","euc-kr"));
		sbQry.append("&Msg="	 + URLEncoder.encode((String)map.get("MSG_TXT"),"euc-kr"));

	    String qry = sbQry.toString();
	    
	    DataOutputStream dos = null;
        HttpURLConnection connect = null;
		try {
			connect = (HttpURLConnection)(new URL("http://sms.kwic.co.kr/iHeart/SmsSend.asp")).openConnection();
			connect.setRequestMethod("POST");
			connect.setDoInput(true);
			connect.setDoOutput(true);
			connect.setUseCaches(false);

			dos = new DataOutputStream(connect.getOutputStream());
			dos.writeBytes(qry);
			dos.flush();
			dos.close();

			BufferedReader in = new BufferedReader( new InputStreamReader(connect.getInputStream(),"euc-kr"));
			String s ="";
			while( (s = in.readLine()) != null ){
			   sb.append(s);
			}
			in.close();
			msg = sb.toString();
		}catch (Exception e) {
			throw e;
		}finally{
			try{if(connect!=null)connect.disconnect();}catch(Exception ex){}
			try{if(dos!=null)dos.close();}catch(Exception ex){}
		}
		return msg;
	}
}
