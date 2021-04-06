package com.kwic.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFWriter {
	private String TYPE_TEXT	= "1";
	private String TYPE_IMAGE	= "2";
	
	private List<Map<String,Object>> pointList	= null;
	private String pdfPath;
	private String fontPath;
	private int totPageCount;
	
	public PDFWriter(String pdfPath,String fontPath){
		pointList			= new ArrayList<Map<String,Object>>();
		this.pdfPath		= pdfPath;
		this.fontPath		= fontPath;
		this.totPageCount	= 1;
	}
	
	public PDFWriter(String pdfPath,String fontPath,int totPageCount){
		pointList			= new ArrayList<Map<String,Object>>();
		this.pdfPath		= pdfPath;
		this.fontPath		= fontPath;
		this.totPageCount	= totPageCount;
	}
	
	public void addText(String text, float x, float y){
		addText(1,text, x, y,10);
	}
	public void addText(String text, float x, float y,int size){
		addText(1,text, x, y,size);
	}
	public void addText(int pageNo,String text, float x, float y){
		addText(pageNo,text, x, y,13);
	}
	public void addText(int pageNo,String text, float x, float y,int size){
		Map<String,Object> map	= new HashMap<String,Object>();
		map.put("TYPE", TYPE_TEXT);
		map.put("PAGE", String.valueOf(pageNo));
		map.put("DATA", text);
		map.put("X", String.valueOf(x));
		map.put("Y", String.valueOf(y));
		map.put("SIZE", String.valueOf(size));
		pointList.add(map);
	}
	public void addImage(byte[] bytes,float pX,float pY,int sW,int sH){
		addImage(1,bytes,pX,pY,sW,sH);
	}
	public void addImage(int pageNo,byte[] bytes,float pX,float pY,int sW,int sH){
		Map<String,Object> map	= new HashMap<String,Object>();
		map.put("TYPE", TYPE_IMAGE);
		map.put("PAGE", String.valueOf(pageNo));
		map.put("DATA", bytes);
		map.put("X", String.valueOf(pX));
		map.put("Y", String.valueOf(pY));
		map.put("W", String.valueOf(sW));
		map.put("H", String.valueOf(sH));
		pointList.add(map);
	}
	
	public void write(String writePath) throws Exception{
	    Document document	= null;
	    PdfWriter writer 	= null;
	    PdfReader reader	= null;
	    PdfContentByte cb	= null;
	    PdfImportedPage pPage = null;
	    FileInputStream is	= null;
	    
	    try{
	    	if(!new File(writePath).getParentFile().exists())
	    		new File(writePath).getParentFile().mkdirs();
	    	
	    	document	= new Document(PageSize.A4);
			is		= new FileInputStream(new File(pdfPath));
	        writer	= PdfWriter.getInstance(document, new FileOutputStream(writePath));
		    document.open();
		    cb		= writer.getDirectContent();
		    reader	= new PdfReader(is);
	    	
	    	for(int i=1;i<=totPageCount;i++){
				pPage	= writer.getImportedPage(reader, i);			
			    document.newPage();
			    cb.addTemplate(pPage, 0, 0);
	    		
	    		for(int j=0;j<pointList.size();j++){
	    			if(!pointList.get(j).get("PAGE").equals(String.valueOf(i)))
	    				continue;
	    			
	    			if(pointList.get(j).get("TYPE").equals(TYPE_TEXT))
	    				absText(cb,pointList.get(j));
	    			else if(pointList.get(j).get("TYPE").equals(TYPE_IMAGE))
	    				setImage(cb,pointList.get(j));
	    		}
	    	}
	    	
	    }catch(Exception e){
	    	throw e;
	    }finally{
			try{if(document!=null)document.close();}catch(Exception e){}
			try{if(reader!=null)reader.close();}catch(Exception e){}
			try{if(writer!=null)writer.close();}catch(Exception e){}
			try{if(is!=null)is.close();}catch(Exception e){}
	    }
	}
	
	
    public void absText(PdfContentByte cb, Map<String,Object> inf) throws Exception{
    	absText(cb, (String)inf.get("DATA"), Float.parseFloat((String) inf.get("X")), Float.parseFloat((String) inf.get("Y")),Integer.parseInt((String) inf.get("SIZE")));
    }
    public void setImage(PdfContentByte cb, Map<String,Object> inf) throws Exception{
    	setImage(cb, (byte[])inf.get("DATA"), Float.parseFloat((String) inf.get("X")), Float.parseFloat((String) inf.get("Y")),Integer.parseInt((String) inf.get("W")),Integer.parseInt((String) inf.get("H")));
    }
	
	//PDF 특정위치에 작성 
    public void absText(PdfContentByte cb, String text, float x, float y) throws Exception{
        BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        cb.saveState();
        cb.beginText();
        cb.moveText(x, y);
        cb.setFontAndSize(bf, 13);
        cb.showText(text);
        cb.endText();
        cb.restoreState();
    }
	//PDF 특정위치에 특정 크기로 작성 
    public void absText(PdfContentByte cb, String text, float x, float y,int size) throws Exception {
        BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        cb.saveState();
        cb.beginText();
        cb.moveText(x, y);
        cb.setFontAndSize(bf, size);
        cb.showText(text);
        cb.endText();
        cb.restoreState();
    }
	//PDF 특정위치에 특정 크기로 이미지 적용 
    public void setImage(PdfContentByte cb,byte[] bytes,float pX,float pY,int sW,int sH) throws Exception{
		ByteArrayOutputStream stream	= null;
		Image image	= null;
		try{
	        image			= Image.getInstance(bytes);
	        image.setAbsolutePosition(pX,pY);
	        if(sW!=0 && sH!=0)
	        	image.scaleToFit(sW, sH);
	        else
    			image.scaleToFit(100, 50);
	        cb.addImage(image);
		}catch(Exception e){
			throw e;
		}finally{
			try{if(stream!=null)stream.close();}catch(Exception ex){}
		}
	}

    
    
    public static void main(String[] args) throws Exception{
    	PDFWriter pdfWriter	= new PDFWriter("C:/agrevd/files/pdf/CMSSign_agreement.pdf","C:/agrevd/files/fonts/NanumGothic-Bold.ttf");
    	
    	
    	pdfWriter.addText("예금주",410,160);
    	pdfWriter.addText("예서명",495,160);
    	pdfWriter.addText("신청인",410,180);
    	pdfWriter.addText("신서명",495,180);
    	
    	pdfWriter.addText("2015",425,200);
    	pdfWriter.addText("10",474,200);
    	pdfWriter.addText("13",505,200);
    	
    	pdfWriter.addText("V",452,258);
    	pdfWriter.addText("V",452,352);
    	
    	//예금주전번
    	pdfWriter.addText("0",145,434,20);
    	pdfWriter.addText("1",171,434,20);
    	pdfWriter.addText("0",197,434,20);
    	//x간격=일반=26,-=43
    	pdfWriter.addText("5",240,434,20);
    	pdfWriter.addText("6",266,434,20);
    	pdfWriter.addText("7",292,434,20);
    	pdfWriter.addText("8",318,434,20);
    	pdfWriter.addText("3",359,434,20);
    	pdfWriter.addText("1",385,434,20);
    	pdfWriter.addText("6",411,434,20);
    	pdfWriter.addText("8",437,434,20);
    	
    	//예금주생년월일
    	pdfWriter.addText("0",145,471,20);
    	pdfWriter.addText("0",171,471,20);
    	pdfWriter.addText("0",197,471,20);
    	pdfWriter.addText("0",223,471,20);
    	pdfWriter.addText("0",249,471,20);
    	pdfWriter.addText("0",274,471,20);
    	pdfWriter.addText("0",300,471,20);
    	pdfWriter.addText("0",326,471,20);
    	pdfWriter.addText("0",352,471,20);
    	pdfWriter.addText("0",378,471,20);
    	
    	//계좌번호
    	pdfWriter.addText("0",145,508,20);
    	pdfWriter.addText("0",171,508,20);
    	pdfWriter.addText("0",197,508,20);
    	pdfWriter.addText("0",223,508,20);
    	pdfWriter.addText("0",249,508,20);
    	pdfWriter.addText("0",274,508,20);
    	pdfWriter.addText("0",300,508,20);
    	pdfWriter.addText("0",326,508,20);
    	pdfWriter.addText("0",352,508,20);
    	pdfWriter.addText("0",378,508,20);
    	pdfWriter.addText("0",403,508,20);
    	pdfWriter.addText("0",429,508,20);
    	pdfWriter.addText("0",455,508,20);
    	pdfWriter.addText("0",481,508,20);
    	pdfWriter.addText("0",507,508,20);
    	pdfWriter.addText("0",533,508,20);
    	
    	pdfWriter.addText("한국스탠차타드은행",145,546);
    	pdfWriter.addText("예금주명",400,546);

    	
    	pdfWriter.addText("V",142,578);//변동금액
    	pdfWriter.addText("V",142,590);//고정금액
    	pdfWriter.addText("800,000",202,590);//고정금액
    	
    	pdfWriter.addText("29",475,584);//출금일
    	
    	pdfWriter.addText("신청인",145,620);
    	pdfWriter.addText("본인",259,620,8);
    	
    	pdfWriter.addText("010-5678-3168",400,620);

    	pdfWriter.addText("서울시 금천구 가산동 550-1 롯데IT캐슬2동 613호",145,687);
    	
    	pdfWriter.addText("대표자명",145,713);
    	pdfWriter.addText("000-00-00000",400,713);
    	
    	pdfWriter.addText("수납업체명",145,738);
    	pdfWriter.addText("수납목적",400,738);
    	
    	pdfWriter.write("C:/agrevd/files/pdf/CMSSign_agreement_26.pdf");
    	/*
		PDFImage pdf	= new PDFImage(new File("C:/agrevd/files/pdf/CMSSign_agreement_26.pdf"));
		int cnt	= pdf.getPageCount();
		File file	= pdf.writeImage(new File("C:/agrevd/files/pdf/CMSSign_agreement_26_tmp.jpeg"), cnt, cnt, "1234");

		ImageConverter.convert(file,new File("C:/agrevd/files/pdf/CMSSign_agreement_26.jpeg"),300*1024);
    	*/
    }
}
