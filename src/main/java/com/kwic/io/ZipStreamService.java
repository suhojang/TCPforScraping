package com.kwic.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import net.sf.jazzlib.ZipEntry;
import net.sf.jazzlib.ZipInputStream;
import net.sf.jazzlib.ZipOutputStream;

/**
 * <pre>
 * Title		: ZipStreamService
 * Description	: 
 * Date			: 2011.12.09
 * Copyright	: Copyright	(c)	2012
 * Company		: KWIC
 * 
 * -------------------------------------------
 * 
 * </pre>
 *
 * @author 장정훈
 * @version	1.0
 * @since 1.4
 */
public class ZipStreamService{
	/**
	 * 압축HIGH - 10
	 * */
	public static final int _COMPRESS_LEVEL_HIGH	= 10;
	/**
	 * 압축COMMON - 5
	 * */
	public static final int _COMPRESS_LEVEL_COMMON	= 5;
	/**
	 * 압축LOW - 3
	 * */
	public static final int _COMPRESS_LEVEL_LOW		= 3;
	/**
	 * 압축DEFAULT - 8
	 * */
	public static final int _COMPRESS_LEVEL_DEFAULT	= 8;
	/**
	 * 압축파일 확장자
	 * */
	public static final String _EXTENSION			= ".zip";
	
	/**<pre>
	 * inputstream 을 entryName으로 OutputStream으로 내보낸다.
	 * </pre>
	 * @param is InputStream
	 * @param entryName String
	 * @param os OutputStream
	 * @param compressLevel int
	 * @throws Exception
	 * */
	public static final void write(File file,OutputStream os, int compressLevel) throws Exception{
		write(file,file.getName(),System.currentTimeMillis(),os, compressLevel);
	}
	public static final void write(File file,String entryName,OutputStream os, int compressLevel) throws Exception{
		write(file,entryName,System.currentTimeMillis(),os, compressLevel);
	}
	public static final void write(File file,String entryName,long lastModified,OutputStream os, int compressLevel) throws Exception{
		write(FileIO.getBytes(file),entryName,file.lastModified(),os, compressLevel);
	}
	public static final void write(File[] files,String[] entryName,OutputStream os, int compressLevel) throws Exception{
		byte[][] bytes	= new byte[files.length][];
		long[] lastModifiedArr	= new long[files.length];
		
		for(int i=0;i<bytes.length;i++){
			bytes[i]	= FileIO.getBytes(files[i]);
			lastModifiedArr[i]	= files[i].lastModified();
		}
		write(bytes,entryName,lastModifiedArr,os, compressLevel);
	}
	
	/**<pre>
	 * inputstream 을 entryName으로 OutputStream으로 내보낸다.
	 * </pre>
	 * @param is InputStream
	 * @param entryName String
	 * @param os OutputStream
	 * @param compressLevel int
	 * @throws Exception
	 * */
	public static final void write(InputStream is,String entryName,OutputStream os, int compressLevel) throws Exception{
		write(is,entryName,System.currentTimeMillis(),os, compressLevel);
	}
	public static final void write(InputStream is,String entryName,long lastModified,OutputStream os, int compressLevel) throws Exception{
		write(new InputStream[]{is},new String[]{entryName},new long[]{lastModified},os,compressLevel);
	}
	/**<pre>
	 * 복수의 inputstream 을 각각의 entryName으로 OutputStream으로 내보낸다.
	 * </pre>
	 * @param isArr InputStream[]
	 * @param entryNameArr String[]
	 * @param os OutputStream
	 * @param compressLevel int
	 * @throws Exception
	 * */
	public static final void write(InputStream[] isArr,String[] entryNameArr,OutputStream os, int compressLevel) throws Exception{
		long[] lastModifiedArr	= new long[entryNameArr.length];
		for(int i=0;i<lastModifiedArr.length;i++)
			lastModifiedArr[i]	= System.currentTimeMillis();
		write(isArr,entryNameArr,lastModifiedArr,os, compressLevel);
	}
	public static final void write(InputStream[] isArr,String[] entryNameArr,long[] lastModified,OutputStream os, int compressLevel) throws Exception{
		ZipOutputStream	zos			= null;
		try{
			zos			= new ZipOutputStream(os);
			zos.setLevel(compressLevel);
			
			for(int i=0;i<entryNameArr.length;i++){
				write(isArr[i],entryNameArr[i],zos);
			}
			zos.flush();
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			for(int i=0;i<isArr.length;i++){try{if(isArr[i]!=null)isArr[i].close();}catch(Exception e){}}
			try{if(zos!=null)zos.close();}catch(Exception e){}
			try{if(os!=null)os.close();}catch(Exception e){}
		}
	}
	/**<pre>
	 * bytes 을 entryName으로 OutputStream으로 내보낸다.
	 * </pre>
	 * @param bytes byte[]
	 * @param entryName String
	 * @param os OutputStream
	 * @param compressLevel int
	 * @throws Exception
	 * */
	public static final void write(byte[] bytes,String entryName,OutputStream os, int compressLevel) throws Exception{
		write(bytes,entryName,System.currentTimeMillis(),os, compressLevel);
	}
	public static final void write(byte[] bytes,String entryName,long lastModified,OutputStream os, int compressLevel) throws Exception{
		write(new byte[][]{bytes},new String[]{entryName},new long[]{lastModified},os,compressLevel);
	}
	/**<pre>
	 * 복수의 bytes 을 각각의 entryName으로 OutputStream으로 내보낸다.
	 * </pre>
	 * @param bytesArr byte[][]
	 * @param entryNameArr String[]
	 * @param os OutputStream
	 * @param compressLevel int
	 * @throws Exception
	 * */
	public static final void write(byte[][] bytesArr,String[] entryNameArr,OutputStream os, int compressLevel) throws Exception{
		long[] lastModifiedArr	= new long[entryNameArr.length];
		for(int i=0;i<lastModifiedArr.length;i++)
			lastModifiedArr[i]	= System.currentTimeMillis();
		write(bytesArr,entryNameArr,lastModifiedArr,os, compressLevel);
	}
	public static final void write(byte[][] bytesArr,String[] entryNameArr,long[] lastModified,OutputStream os, int compressLevel) throws Exception{
		ZipOutputStream	zos			= null;
		try{
			zos			= new ZipOutputStream(os);
			zos.setLevel(compressLevel);
			
			for(int i=0;i<entryNameArr.length;i++){
				write(bytesArr[i],entryNameArr[i],lastModified[i],zos);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			try{if(zos!=null)zos.close();}catch(Exception e){}
			try{if(os!=null)os.close();}catch(Exception e){}
		}
	}
	/**<pre>
	 * bytes 을 entryName으로 ZipOutputStream으로 내보낸다.
	 * </pre>
	 * @param bytes byte[]
	 * @param entryName String
	 * @param zos ZipOutputStream
	 * @throws Exception
	 * */
	/*private static final void write(byte[] bytes,String entryName,ZipOutputStream zos) throws Exception{
		ZipEntry		zentry		= null;
		
		if(bytes==null)
			throw new Exception("Contents is null.");
		if(entryName==null)
			throw new Exception("Entry name is null.");
		if(zos==null)
			throw new Exception("OutputStream is null.");

		try{
			zentry		= new ZipEntry(entryName);
			zentry.setTime(System.currentTimeMillis());
			zentry.setSize(bytes.length);
			zos.putNextEntry(zentry);
			zos.write(bytes, 0, bytes.length);
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			
		}
	}*/
	/**<pre>
	 * bytes 을 entryName으로 ZipOutputStream으로 내보낸다.
	 * </pre>
	 * @param bytes byte[]
	 * @param entryName String
	 * @param zos ZipOutputStream
	 * @throws Exception
	 * */
	private static final void write(byte[] bytes,String entryName,long lastModified,ZipOutputStream zos) throws Exception{
		ZipEntry		zentry		= null;
		
		if(bytes==null)
			throw new Exception("Contents is null.");
		if(entryName==null)
			throw new Exception("Entry name is null.");
		if(zos==null)
			throw new Exception("OutputStream is null.");

		try{
			zentry		= new ZipEntry(entryName);
			zentry.setTime(lastModified);
			zentry.setSize(bytes.length);
			zos.putNextEntry(zentry);
			zos.write(bytes, 0, bytes.length);
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			
		}
	}
	/**<pre>
	 * ?�나???�상?�인 byte array dta�??�나???�축 stream?�로 �?��?�여 ?�송?�다.
	 * </pre>
	 * @param is InputStream
	 * @param entryName String
	 * @param zos ZipOutputStream
	 * @throws Exception
	 * */
	private static final void write(InputStream is,String entryName,ZipOutputStream zos) throws Exception{
		ZipEntry		zentry		= null;
		
		if(is==null)
			throw new Exception("InputStream is null.");
		if(entryName==null)
			throw new Exception("Entry name is null.");
		if(zos==null)
			throw new Exception("OutputStream is null.");

		try{
			int size		= -1;
			byte[] buffer	= new byte[1024];
			zentry		= new ZipEntry(entryName);
			zos.putNextEntry(zentry);
			while ((size = is.read(buffer)) != -1) {
				zos.write(buffer, 0, size);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			
		}
	}
	public static final HashMap<String,byte[]> read(byte[] inBytes) throws Exception{
		ByteArrayInputStream bis	= null;
		
		HashMap<String,byte[]>	entryMap	= null;
		try{
			bis	= new ByteArrayInputStream(inBytes);
			
			entryMap	= read(bis);
		}catch(Exception e){
			throw e;
		}finally{
			try{if(bis!=null)bis.close();}catch(Exception e){}
		}
		return entryMap;
	}
	
	public static final HashMap<String,byte[]> read(InputStream is) throws Exception{
		
		ZipInputStream	zis		= null;
		byte[]			bytes	= new byte[1024];
		int				size	= -1;
		ZipEntry		ze		= null;
		HashMap<String,byte[]>	entryMap	= new HashMap<String,byte[]>();
		JOutputStream	jos		= null;
		
		try{
			zis	= new ZipInputStream(is);
			
			while((ze=zis.getNextEntry()) != null){
				try{if(jos!=null){jos.close();}}catch(Exception ex){}
				
				jos	= new JOutputStream();
				while( (size=zis.read(bytes))>0){
					jos.write(bytes,0,size);
				}
				entryMap.put(ze.getName(),jos.getBytes());
			}
		}catch(Exception e){
			throw e;
		}finally{
			try{if(zis!=null)zis.close();}catch(Exception e){}
			try{if(jos!=null)jos.close();}catch(Exception e){}
		}
		return entryMap;
	}
	
//	public static void main(String[] args) throws Exception{
//		FileInputStream[] isArr	= new FileInputStream[3];
//		isArr[0]	= new FileInputStream("C:\\build.xml");
//		isArr[1]	= new FileInputStream("C:\\DARInstaller.log");
//		isArr[2]	= new FileInputStream("C:\\hostname.txt");
//		
//		FileOutputStream os	= new FileOutputStream("C:\\test.zip");
//		
//		ZipStreamService.write(isArr, new String[]{"a.xml","DARInstaller.log","hostname.txt"}, os, ZipStreamService._COMPRESS_LEVEL_DEFAULT);
//	}
	public static void main(String[] args) throws FileNotFoundException, Exception{
		ZipStreamService.write(FileIO.getBytes(new File("E:\\spark_2_6_3.exe")), "spark_2_6_3.exe", new FileOutputStream(new File("E:\\spark_2_6_3.zip")), ZipStreamService._COMPRESS_LEVEL_DEFAULT);
	}
}
