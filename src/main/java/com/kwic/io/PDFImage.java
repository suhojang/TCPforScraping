package com.kwic.io;

import java.awt.image.BufferedImage;
import java.io.File;

import org.apache.pdfbox.pdmodel.PDDocument;

public class PDFImage {
	public static final String EXT_JPEG	= "jpeg";
	public static final String EXT_GIF	= "gif";
	public static final String EXT_BMP	= "bmp";
	public static final String EXT_PNG	= "png";
	
	private PDDocument pDoc		= null;
	private int totPageCount	= 0;
	
	public PDFImage(File source) throws Exception{
		try{
			pDoc			= PDDocument.load(source);
			totPageCount	= pDoc.getNumberOfPages();
		}catch(Exception e){
			throw e;
		}
	}
	
	public int getPageCount(){
		return totPageCount;
	}
	
	public File writeImage(File image,int sPageNo,int ePageNo,String pwd) throws Exception{
		PDFImageIO	writer	= null; 
		try{
			String ext	= image.getName();
			ext			= ext.substring(ext.lastIndexOf(".")+1);
			String path	= image.getAbsolutePath();
			path		= path.substring(0,path.lastIndexOf("."));
			
			writer		= new PDFImageIO();
			boolean result	= false;
			if(sPageNo==ePageNo)
				result	= writer.writeImage(pDoc, ext, pwd, sPageNo, path, BufferedImage.TYPE_INT_RGB, 300);
			else
			result	= writer.writeImage(pDoc, ext, pwd, sPageNo, ePageNo, path, BufferedImage.TYPE_INT_RGB, 300);
			
			if(!result)
				throw new Exception("Could not convert image.");
		}catch(Exception e){
			throw e;
		}
		return image;
	}
	
	
	public static void main(String[] args) throws Exception{
		PDFImage pdf	= new PDFImage(new File("C:/agrevd/files/pdf/CMSSign_agreement.pdf"));
		int cnt	= pdf.getPageCount();
		File file	= pdf.writeImage(new File("C:/agrevd/files/pdf/CMSSign_agreement.jpeg"), cnt, cnt, "1234");

		ImageConverter.convert(file,new File("C:/agrevd/files/pdf/CMSSign_agreement_26.jpeg"),300*1024);
	}
}
