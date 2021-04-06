package com.kwic.log;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.kwic.util.StringUtil;

public class Tracker {
	public static Map<String,Tracker> map	= new Hashtable<String,Tracker>();
	
	public static final String LINE		= System.getProperty("line.separator");
	private FileWriter writer;
	private String path;
	private String cPath;
	
	private SimpleDateFormat sf	= new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
	
	private Tracker(){
		
	}
	private Tracker(String path){
		this.path	= path;
		sf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
	}
	
	public static Tracker getLogger(String id,String path){
		synchronized(Tracker.class){
			if(map.get(id)==null)
				map.put(id, new Tracker(path));
			return map.get(id);
		}
	}
	public static Tracker getLogger(String id){
		synchronized(Tracker.class){
			if(map.get(id)==null)
				map.put(id, new Tracker());
			return map.get(id);
		}
	}
	public void setPath(String path){
		this.path	= path;
	}
	public void track(Object msg){
		write(msg);
	}
	private synchronized void write(Object msg){
		getWriter();
		StringBuffer sb	= new StringBuffer();
		try{
			if(msg instanceof Throwable){
				sb.append(((Throwable)msg).toString()+": "+((Throwable)msg).getMessage()+LINE);
				StackTraceElement[] arr	= ((Throwable)msg).getStackTrace();
				for(int i=0;i<arr.length;i++)
					sb.append(arr[i].toString()+LINE);
			}else{
				sb.append(String.valueOf(msg)+LINE);
			}
			writer.write("["+sf.format(Calendar.getInstance(Locale.KOREA).getTime())+"] "+sb.toString());
			writer.flush();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private synchronized void getWriter(){
		String tPath		= getPath();
		
		if(!tPath.equals(cPath)){
			cPath	= tPath;
			if(writer!=null)
				try{writer.close();}catch(Exception e){}
			
			if(!new File(cPath).getParentFile().exists())
				new File(cPath).getParentFile().mkdirs();
			
			try{writer	= new FileWriter(cPath,true);}catch(Exception e){e.printStackTrace();}
		}
	}
	public String getPath(){
		SimpleDateFormat sf	= new SimpleDateFormat("yyyyMMdd");
		sf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
		String yyyyMMdd	= sf.format(Calendar.getInstance(Locale.KOREA).getTime());
		String yy	= yyyyMMdd.substring(2,4);
		String yyyy	= yyyyMMdd.substring(0,4);
		String MM	= yyyyMMdd.substring(4,6);
		String dd	= yyyyMMdd.substring(6,8);
		
		String tPath	= "";
		tPath	= StringUtil.replace(path, "%yyyy%", yyyy);
		tPath	= StringUtil.replace(tPath, "%yy%", yy);
		tPath	= StringUtil.replace(tPath, "%MM%", MM);
		tPath	= StringUtil.replace(tPath, "%dd%", dd);
		return tPath;
	}

	public static void main(String[] args){
		System.out.println(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS").format(Calendar.getInstance(Locale.KOREA).getTime()));
		System.out.println(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS").format(new java.util.Date()));
//		Logger.getLogger().debug("아름다운 우리나라");
//		Logger.getLogger().debug("아름다운 우리나라1");
//		Logger.getLogger().debug("아름다운 우리나라2");
//		Logger.getLogger().debug("아름다운 우리나라3");
//		
//		Logger.getLogger().error("아름다운 우리나라3");
	}
}
