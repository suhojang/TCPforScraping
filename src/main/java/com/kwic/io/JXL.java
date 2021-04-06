package com.kwic.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.BooleanCell;
import jxl.BooleanFormulaCell;
import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.DateFormulaCell;
import jxl.ErrorFormulaCell;
import jxl.LabelCell;
import jxl.NumberCell;
import jxl.NumberFormulaCell;
import jxl.StringFormulaCell;
import jxl.Workbook;
import jxl.biff.FontRecord;
import jxl.biff.formula.FormulaException;
import jxl.format.Alignment;
import jxl.format.BoldStyle;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.read.biff.BiffException;
import jxl.write.DateFormat;
import jxl.write.NumberFormat;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import jxl.CellView;

import com.kwic.io.JOutputStream;

/**
 * <pre>
 * Title		: JXL
 * Description	: jxl library를 이용한 excel 작성
 * Date			: 2012.9.01
 * Copyright	: Copyright	(c)	2012
 * Company		: KWIC
 * 
 *    수정일                  수정자                      수정내용
 * -------------------------------------------
 * 
 * </pre>
 *
 * @author 장정훈 
 * @version	1.0
 * @since 1.6
 */
public class JXL implements Serializable{

	private static final long	serialVersionUID = 1L;

	public static int COLUMN_TYPE_STRING	= 1;
	public static int COLUMN_TYPE_INTEGER	= 2;
	public static int COLUMN_TYPE_DOUBLE	= 5;
	public static int COLUMN_TYPE_DATE		= 3;
	public static int COLUMN_TYPE_BINARY	= 4;
	
	public static final	String[] apb	= new String[]{
		"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"
		,"AA","AB","AC","AD","AE","AF","AG","AH","AI","AJ","AK","AL","AM","AN","AO","AP","AQ","AR","AS","AT","AU","AV","AW","AX","AY","AZ"
		,"BA","BB","BC","BD","BE","BF","BG","BH","BI","BJ","BK","BL","BM","BN","BO","BP","BQ","BR","BS","BT","BU","BV","BW","BX","BY","BZ"
		,"CA","CB","CC","CD","CE","CF","CG","CH","CI","CJ","CK","CL","CM","CN","CO","CP","CQ","CR","CS","CT","CU","CV","CW","CX","CY","CZ"
		,"DA","DB","DC","DD","DE","DF","DG","DH","DI","DJ","DK","DL","DM","DN","DO","DP","DQ","DR","DS","DT","DU","DV","DW","DX","DY","DZ"
		,"EA","EB","EC","ED","EE","EF","EG","EH","EI","EJ","EK","EL","EM","EN","EO","EP","EQ","ER","ES","ET","EU","EV","EW","EX","EY","EZ"
	};
	
	public static final int ALIGN_LEFT		= 1;
	public static final int ALIGN_CENTER	= 2;
	public static final int ALIGN_RIGHT		= 3;
	
	public static final int ALIGN_LEFT_IMPACT		= 11;
	public static final int ALIGN_CENTER_IMPACT		= 12;
	public static final int ALIGN_RIGHT_IMPACT		= 13;

	private String[] extendedApb;
	
	private WritableWorkbook 	jWBook;
	private InputStream 		is;
	private OutputStream 		os;

	public final WritableCellFormat CELL_THOUSANDS_INTEGER	= new WritableCellFormat(NumberFormats.THOUSANDS_INTEGER);
	{
		try {
			CELL_THOUSANDS_INTEGER.setBorder(Border.ALL , BorderLineStyle.THIN);
			CELL_THOUSANDS_INTEGER.setAlignment(Alignment.RIGHT);
			CELL_THOUSANDS_INTEGER.setVerticalAlignment(VerticalAlignment.CENTRE);
			CELL_THOUSANDS_INTEGER.setFont(new FontRecord("Arial",10,BoldStyle.NORMAL.getValue(),false,0,Colour.BLACK.getValue(),0){});
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
	public final WritableCellFormat CELL_THOUSANDS_FLOAT		= new WritableCellFormat(NumberFormats.THOUSANDS_FLOAT);
	{
		try {
			CELL_THOUSANDS_FLOAT.setBorder(Border.ALL , BorderLineStyle.THIN);
			CELL_THOUSANDS_FLOAT.setAlignment(Alignment.RIGHT);
			CELL_THOUSANDS_FLOAT.setVerticalAlignment(VerticalAlignment.CENTRE);
			CELL_THOUSANDS_FLOAT.setFont(new FontRecord("Arial",10,BoldStyle.NORMAL.getValue(),false,0,Colour.BLACK.getValue(),0){});
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
	public final WritableCellFormat CELL_INTEGER				= new WritableCellFormat(NumberFormats.INTEGER);
	{
		try {
			CELL_INTEGER.setBorder(Border.ALL , BorderLineStyle.THIN);
			CELL_INTEGER.setAlignment(Alignment.RIGHT);
			CELL_INTEGER.setVerticalAlignment(VerticalAlignment.CENTRE);
			CELL_INTEGER.setFont(new FontRecord("Arial",10,BoldStyle.NORMAL.getValue(),false,0,Colour.BLACK.getValue(),0){});
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
	public final WritableCellFormat CELL_FLOAT				= new WritableCellFormat(NumberFormats.FLOAT);
	{
		try {
			CELL_FLOAT.setBorder(Border.ALL , BorderLineStyle.THIN);
			CELL_FLOAT.setAlignment(Alignment.RIGHT);
			CELL_FLOAT.setVerticalAlignment(VerticalAlignment.CENTRE);
			CELL_FLOAT.setFont(new FontRecord("Arial",10,BoldStyle.NORMAL.getValue(),false,0,Colour.BLACK.getValue(),0){});
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
	public final WritableCellFormat CELL_THOUSANDS_INTEGER_IMPACT	= new WritableCellFormat(NumberFormats.THOUSANDS_INTEGER);
	{
		try {
			CELL_THOUSANDS_INTEGER_IMPACT.setBorder(Border.ALL , BorderLineStyle.THIN);
			CELL_THOUSANDS_INTEGER_IMPACT.setAlignment(Alignment.RIGHT);
			CELL_THOUSANDS_INTEGER_IMPACT.setVerticalAlignment(VerticalAlignment.CENTRE);
			CELL_THOUSANDS_INTEGER_IMPACT.setFont(new FontRecord("Arial",10,BoldStyle.BOLD.getValue(),false,0,Colour.BLACK.getValue(),0){});
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
	public final WritableCellFormat CELL_THOUSANDS_FLOAT_IMPACT	= new WritableCellFormat(NumberFormats.THOUSANDS_FLOAT);
	{
		try {
			CELL_THOUSANDS_FLOAT_IMPACT.setBorder(Border.ALL , BorderLineStyle.THIN);
			CELL_THOUSANDS_FLOAT_IMPACT.setAlignment(Alignment.RIGHT);
			CELL_THOUSANDS_FLOAT_IMPACT.setVerticalAlignment(VerticalAlignment.CENTRE);
			CELL_THOUSANDS_FLOAT_IMPACT.setFont(new FontRecord("Arial",10,BoldStyle.BOLD.getValue(),false,0,Colour.BLACK.getValue(),0){});
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
	public final WritableCellFormat CELL_INTEGER_IMPACT	= new WritableCellFormat(NumberFormats.INTEGER);
	{
		try {
			CELL_INTEGER_IMPACT.setBorder(Border.ALL , BorderLineStyle.THIN);
			CELL_INTEGER_IMPACT.setAlignment(Alignment.RIGHT);
			CELL_INTEGER_IMPACT.setVerticalAlignment(VerticalAlignment.CENTRE);
			CELL_INTEGER_IMPACT.setFont(new FontRecord("Arial",10,BoldStyle.BOLD.getValue(),false,0,Colour.BLACK.getValue(),0){});
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
	public final WritableCellFormat CELL_FLOAT_IMPACT	= new WritableCellFormat(NumberFormats.FLOAT);
	{
		try {
			CELL_FLOAT_IMPACT.setBorder(Border.ALL , BorderLineStyle.THIN);
			CELL_FLOAT_IMPACT.setAlignment(Alignment.RIGHT);
			CELL_FLOAT_IMPACT.setVerticalAlignment(VerticalAlignment.CENTRE);
			CELL_FLOAT_IMPACT.setFont(new FontRecord("Arial",10,BoldStyle.BOLD.getValue(),false,0,Colour.BLACK.getValue(),0){});
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	
	private WritableCellFormat headFormat;
	private WritableCellFormat cellFormat;
	private WritableCellFormat cellFormatL;
	private WritableCellFormat cellFormatC;
	private WritableCellFormat cellFormatR;
	private WritableCellFormat impactFormatL;
	private WritableCellFormat impactFormatC;
	private WritableCellFormat impactFormatR;
	
	private Map<String,WritableCellFormat>	numberFormats;
	private Map<String,WritableCellFormat>	dateFormats;
	
	public JXL() throws IOException, BiffException, WriteException{
		this(new JOutputStream());
	}
	public JXL(File file) throws IOException, BiffException, WriteException{
		this(file,new JOutputStream());
	}
	public JXL(File src,File tgt) throws IOException, BiffException, WriteException{
		this(new FileInputStream(src),new FileOutputStream(tgt));
	}
	public JXL(File src,OutputStream os) throws BiffException, IOException, WriteException{
		this(new FileInputStream(src),os);
	}
	public JXL(OutputStream os) throws IOException, WriteException{
		this.os	= os;
		jWBook	= Workbook.createWorkbook(os);
		initCellFormat();
	}
	public JXL(InputStream is) throws BiffException, IOException, WriteException{
		this.is	= is;
		os		= new JOutputStream();
		jWBook	= Workbook.createWorkbook(os, Workbook.getWorkbook(is));
		initCellFormat();
	}
	public JXL(InputStream is,OutputStream os) throws BiffException, IOException, WriteException{
		if(is!=null && os!=null){
			jWBook	= Workbook.createWorkbook(os, Workbook.getWorkbook(is));
		}else if(is!=null && os==null){
			os		= new JOutputStream();
			jWBook	= Workbook.createWorkbook(os, Workbook.getWorkbook(is));
		}else if(is==null && os!=null){
			jWBook	= Workbook.createWorkbook(os);
		}else{
			os	= new JOutputStream();
			jWBook	= Workbook.createWorkbook(os);
		}

		this.is	= is;
		this.os	= os;
		initCellFormat();
	}
	
	//get bytes array
	public byte[] getBytes(){
		if(os.getClass()==JOutputStream.class){
			return ((JOutputStream)os).getBytes();
		}
		return null;
	}

	//write excel file
	public void write() throws IOException{
		jWBook.write();
	}
	//close readable & writable stream
	public void close(){
		try{if(is!=null)is.close();}catch(Exception e){}
		try{if(os!=null)os.close();}catch(Exception e){}
		try{if(jWBook!=null)jWBook.close();}catch(Exception e){}
	}
	//set cell align
	public void setCellAlign(int sIdx,int rIdx,int cIdx,int align){
		WritableCellFormat frmt	= null;
		
		if(align==ALIGN_LEFT)
			frmt	= cellFormatL;
		else if(align==ALIGN_CENTER)
			frmt	= cellFormatC;
		else if(align==ALIGN_RIGHT)
			frmt	= cellFormatR;
		else if(align==ALIGN_LEFT_IMPACT)
			frmt	= impactFormatL;
		else if(align==ALIGN_CENTER_IMPACT)
			frmt	= impactFormatC;
		else if(align==ALIGN_RIGHT_IMPACT)
			frmt	= impactFormatR;
		
		getSheet(sIdx).getWritableCell(cIdx, rIdx).setCellFormat(frmt);
	}

	//initialize cell format
	public void initCellFormat() throws WriteException{
		cellFormat = new WritableCellFormat();
		cellFormat.setBorder(Border.ALL , BorderLineStyle.THIN);

		cellFormatL = new WritableCellFormat();
		cellFormatL.setBorder(Border.ALL , BorderLineStyle.THIN);
		cellFormatL.setAlignment(Alignment.LEFT);
		cellFormatL.setVerticalAlignment(VerticalAlignment.CENTRE);
		cellFormatC = new WritableCellFormat();
		cellFormatC.setBorder(Border.ALL , BorderLineStyle.THIN);
		cellFormatC.setAlignment(Alignment.CENTRE);
		cellFormatC.setVerticalAlignment(VerticalAlignment.CENTRE);
		cellFormatR = new WritableCellFormat();
		cellFormatR.setBorder(Border.ALL , BorderLineStyle.THIN);
		cellFormatR.setAlignment(Alignment.RIGHT);
		cellFormatR.setVerticalAlignment(VerticalAlignment.CENTRE);
		
		headFormat	= new WritableCellFormat();
		headFormat.setBorder(Border.ALL , BorderLineStyle.THIN);
		headFormat.setBackground(jxl.format.Colour.YELLOW);
		FontRecord font	= new FontRecord("Arial",10,BoldStyle.BOLD.getValue(),false,0,Colour.BLACK.getValue(),0){};
		headFormat.setAlignment(Alignment.CENTRE);
		headFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
		headFormat.setFont(font);
		

		impactFormatL	= new WritableCellFormat();
		impactFormatL.setBorder(Border.ALL , BorderLineStyle.THIN);
		//impactFormatL.setBackground(jxl.format.Colour.ORANGE);
		font	= new FontRecord("Arial",10,BoldStyle.BOLD.getValue(),false,0,Colour.BLACK.getValue(),0){};
		impactFormatL.setAlignment(Alignment.LEFT);
		impactFormatL.setVerticalAlignment(VerticalAlignment.CENTRE);
		impactFormatL.setFont(font);
		
		impactFormatC	= new WritableCellFormat();
		impactFormatC.setBorder(Border.ALL , BorderLineStyle.THIN);
		//impactFormatC.setBackground(jxl.format.Colour.ORANGE);
		font	= new FontRecord("Arial",10,BoldStyle.BOLD.getValue(),false,0,Colour.BLACK.getValue(),0){};
		impactFormatC.setAlignment(Alignment.CENTRE);
		impactFormatC.setVerticalAlignment(VerticalAlignment.CENTRE);
		impactFormatC.setFont(font);
		
		impactFormatR	= new WritableCellFormat();
		impactFormatR.setBorder(Border.ALL , BorderLineStyle.THIN);
		//impactFormatR.setBackground(jxl.format.Colour.ORANGE);
		font	= new FontRecord("Arial",10,BoldStyle.BOLD.getValue(),false,0,Colour.BLACK.getValue(),0){};
		impactFormatR.setAlignment(Alignment.RIGHT);
		impactFormatR.setVerticalAlignment(VerticalAlignment.CENTRE);
		impactFormatR.setFont(font);

		
		numberFormats	= new HashMap<String,WritableCellFormat>();
		dateFormats		= new HashMap<String,WritableCellFormat>();
	}
	
	public String toString(){
		StringBuffer sb	= new StringBuffer();
		try{
			WritableSheet[] sheetArr	= jWBook.getSheets();
			Cell[] cellArr	= null;
			for(int i=0;i<sheetArr.length;i++){
				sb.append("---- "+sheetArr[i].getName()+" ----\n");
				for(int j=0;j<sheetArr[i].getRows();j++){
					cellArr	= sheetArr[i].getRow(j);
					for(int k=0;k<cellArr.length;k++){
						sb.append(k==0?"":",").append(getValue(cellArr[k]));
					}
					sb.append("\n");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return sb.toString();
	}
	//get excel column index (AA~ZZ)
	public String getXLColumnName(int cIdx){
		if(cIdx<apb.length)
			return apb[cIdx];

		if(extendedApb!=null)
			return extendedApb[cIdx-apb.length];
		
		extendedApb	= new String[apb.length*apb.length];
		
		for(int i=0;i<apb.length;i++){
			for(int j=0;j<apb.length;j++){
				extendedApb[i*apb.length+j]	= apb[i]+apb[j];
			}
		}
		return extendedApb[cIdx-apb.length];
	}
	public WritableSheet getSheet(String sheetName){
		return jWBook.getSheet(sheetName);
	}
	public WritableSheet getSheet(int sIdx){
		return jWBook.getSheet(sIdx);
	}
	public WritableSheet addSheet(){
		return jWBook.createSheet("sheet_"+jWBook.getNumberOfSheets(), jWBook.getNumberOfSheets());
	}	
	public WritableSheet addSheet(String sheetName){
		return jWBook.createSheet(sheetName, jWBook.getNumberOfSheets());
	}	
	public WritableSheet insertSheet(int sIdx){
		return jWBook.createSheet("sheet_"+sIdx, sIdx);
	}
	public WritableSheet insertSheet(String sheetName,int sIdx){
		return jWBook.createSheet(sheetName, sIdx);
	}
	public String getSheetName(int sIdx){
		return jWBook.getSheetNames()[sIdx];
	}
	public int getSheetIndex(String sheetName){
		String[] arr	= jWBook.getSheetNames();
		for(int i=0;i<arr.length;i++){
			if(arr[i].equals(sheetName))
				return i;
		}
		return -1;
	}
	
	
	
	public void addTitleRow(int sIdx,Object[] arr) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addRow(sIdx,arr,true);
	}
	public void addRow(int sIdx,Object[] arr) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addRow(sIdx,arr,false);
	}
	public void addRow(int sIdx,Object[] arr,boolean isHead) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		WritableSheet sheet	= getSheet(sIdx);
		int rowIdx	= sheet.getRows()==0?0:sheet.getRows();
		
		for(int i=0;i<arr.length;i++){
			addCell(sIdx,rowIdx,i,arr[i],isHead);
		}
	}
	public void insertRow(int sIdx,int rIdx,Object[] arr,boolean isHead) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		WritableSheet sheet	= getSheet(sIdx);
		if(rIdx<0)
			rIdx	= 0;
		if(rIdx>=sheet.getRows()){
			rIdx	= sheet.getRows();
			addRow(sIdx,arr,isHead);
			return;
		}
		sheet.insertRow(rIdx);
	}

	
	private void addCell(int sIdx,int rIdx,int cIdx,Object val,boolean isHead) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,val,isHead?headFormat:cellFormat);
	}
	public void addCell(int sIdx,int rIdx,int cIdx,Object val,WritableCellFormat cf) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		int	size = 2048;

		WritableSheet sheet	= getSheet(sIdx);
		if(val==null){
			sheet.addCell(new jxl.write.Label(cIdx,rIdx,"",cf));
		}else if(val.getClass()==Boolean.class || val.getClass()==boolean.class){
			sheet.addCell(new jxl.write.Boolean(cIdx,rIdx,(Boolean)val,cf));
		}else if(val.getClass()==String.class){
			if(val.toString().trim().startsWith("="))
				sheet.addCell(new jxl.write.Formula(cIdx,rIdx,String.valueOf(val).trim().substring(1),cf));
			else
				sheet.addCell(new jxl.write.Label(cIdx,rIdx,String.valueOf(val),cf));

			size	= String.valueOf(val).getBytes("EUC-KR").length*350;		
		}else if(val.getClass()==Integer.class|| val.getClass()==int.class){
			sheet.addCell(new jxl.write.Number(cIdx,rIdx,(Integer)val,cf));
			size	= new java.math.BigDecimal((Integer)val).toString().length()*250;		
		}else if(val.getClass()==Long.class|| val.getClass()==long.class){
			sheet.addCell(new jxl.write.Number(cIdx,rIdx,(Long)val,cf));
			size	= new java.math.BigDecimal((Long)val).toString().length()*250;	
		}else if(val.getClass()==Short.class|| val.getClass()==short.class){
			sheet.addCell(new jxl.write.Number(cIdx,rIdx,(Short)val,cf));
			size	= String.valueOf(val).length()*250;		
		}else if(val.getClass()==Double.class|| val.getClass()==double.class){
			sheet.addCell(new jxl.write.Number(cIdx,rIdx,(Double)val,cf));
			size	= new java.math.BigDecimal((Double)val).toString().length()*250;		
		}else if(val.getClass()==Float.class|| val.getClass()==float.class){
			sheet.addCell(new jxl.write.Number(cIdx,rIdx,(Float)val,cf));
			size	= new java.math.BigDecimal((Float)val).toString().length()*250;		
		}else if(val.getClass()==BigInteger.class){
			sheet.addCell(new jxl.write.Number(cIdx,rIdx,((BigInteger)val).intValue(),cf));
			size	= String.valueOf(val).length()*250;		
		}else if(val.getClass()==BigDecimal.class){
			sheet.addCell(new jxl.write.Number(cIdx,rIdx,((BigDecimal)val).doubleValue(),cf));
			size	= String.valueOf(val).length()*250;		
		}else if(val.getClass()==java.util.Date.class){
			sheet.addCell(new jxl.write.DateTime(cIdx,rIdx,(java.util.Date)val,cf));
			size	= 250*12;
		}else{
			sheet.addCell(new jxl.write.Label(cIdx,rIdx,String.valueOf(val),cf));
			size	= String.valueOf(val).getBytes("EUC-KR").length*250;		
		}
		CellView cv	= sheet.getColumnView(cIdx);
		if(rIdx!=0 && cv.getSize()<size){
			cv.setSize((int)size);
		}
		sheet.setColumnView(cIdx, cv);
	}
	
	public void setCellSize(int sIdx,int cIdx,int size){
		jxl.CellView cv	= null;
		cv	= getSheet(sIdx).getColumnView(cIdx);
		cv.setSize(size);
		getSheet(sIdx).setColumnView(cIdx, cv);
	}

	public void setCellSize(int sIdx,int[] sizeArr){
		jxl.CellView cv	= null;
		for(int i=0;i<sizeArr.length;i++){
			if(sizeArr[i]<0)
				continue;
			cv	= getSheet(sIdx).getColumnView(i);
			cv.setSize(sizeArr[i]);
			getSheet(sIdx).setColumnView(i, cv);
		}
	}
	
	public void setValue(int sIdx,int rIdx,int cIdx,String formula,String format) throws RowsExceededException, WriteException{
		if(!formula.toString().trim().startsWith("="))
			return;
		if(format.indexOf("y")>=0 || format.indexOf("M")>=0 || format.indexOf("d")>=0 || format.indexOf("H")>=0 || format.indexOf("m")>=0 || format.indexOf("s")>=0 || format.indexOf("S")>=0 || format.indexOf("W")>=0){
			getSheet(sIdx).addCell(new jxl.write.Formula(cIdx,rIdx,String.valueOf(formula).trim().substring(1),makeDateFromat(format)));
		}else{
			getSheet(sIdx).addCell(new jxl.write.Formula(cIdx,rIdx,String.valueOf(formula).trim().substring(1),makeNumberFromat(format)));
		}
	}
	public void setValue(int sIdx,int rIdx,int cIdx,short val,String format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,new Short(val),makeNumberFromat(format));
	}
	public void setValue(int sIdx,int rIdx,int cIdx,short val,WritableCellFormat format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,new Short(val),format);
	}
	public void setValue(int sIdx,int rIdx,int cIdx,int val,String format) throws WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,new Integer(val),makeNumberFromat(format));
	}
	public void setValue(int sIdx,int rIdx,int cIdx,int val,WritableCellFormat format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,new Integer(val),format);
	}
	public void setValue(int sIdx,int rIdx,int cIdx,long val,String format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,new Long(val),makeNumberFromat(format));
	}
	public void setValue(int sIdx,int rIdx,int cIdx,long val,WritableCellFormat format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,new Long(val),format);
	}
	public void setValue(int sIdx,int rIdx,int cIdx,float val,String format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,new Float(val),makeNumberFromat(format));
	}
	public void setValue(int sIdx,int rIdx,int cIdx,float val,WritableCellFormat format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,new Float(val),format);
	}
	public void setValue(int sIdx,int rIdx,int cIdx,double val,String format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,new Double(val),makeNumberFromat(format));
	}
	public void setValue(int sIdx,int rIdx,int cIdx,double val,WritableCellFormat format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,new Double(val),format);
	}
	public void setValue(int sIdx,int rIdx,int cIdx,java.util.Date val,String format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,val,makeNumberFromat(format));
	}
	public void setValue(int sIdx,int rIdx,int cIdx,java.util.Date val,WritableCellFormat format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,val,format);
	}
	public void setValue(int sIdx,int rIdx,int cIdx,Object val) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		setValue(sIdx,rIdx,cIdx,val,false);
	}
	public void setValue(int sIdx,int rIdx,int cIdx,Object val,boolean isHead) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,val,isHead);
	}
	
	//format : SimpleDateFormat
	public void setDateFormat(int sIdx,int rIdx,int cIdx,String format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,getValue(sIdx,rIdx,cIdx),makeDateFromat(format));
	}
	//format : NumberFormat
	public void setNumberFormat(int sIdx,int rIdx,int cIdx,String format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		addCell(sIdx,rIdx,cIdx,getValue(sIdx,rIdx,cIdx),makeNumberFromat(format));
	}
	
	private WritableCellFormat makeDateFromat(String format) throws WriteException{
		WritableCellFormat cf	= null;
		if(dateFormats.get(format)==null){
			cf = new WritableCellFormat(new DateFormat(format));
			cf.setBorder(Border.ALL , BorderLineStyle.THIN);
			cf.setAlignment(Alignment.CENTRE);
			dateFormats.put(format,cf);
		}
		cf	= dateFormats.get(format);
		return cf;
	}
	
	private WritableCellFormat makeNumberFromat(String format) throws WriteException{
		
		WritableCellFormat cf	= null;
		if(numberFormats.get(format)==null){
			cf = new WritableCellFormat(new NumberFormat(format));
			cf.setBorder(Border.ALL , BorderLineStyle.THIN);
			cf.setAlignment(Alignment.RIGHT);
			numberFormats.put(format,cf);
		}
		cf	= numberFormats.get(format);
		
		return cf;
	}
	
	//열 전체 날짜 형식 지정
	public void setDateFormat(int sIdx,int cIdx,String format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		int rows	= getSheet(sIdx).getRows();
		for(int i=0;i<rows;i++){
			setDateFormat(sIdx,i,cIdx,format);
		}
	}
	//열전체 숫자형식 지정
	public void setNumberFormat(int sIdx,int cIdx,String format) throws RowsExceededException, WriteException, UnsupportedEncodingException{
		int rows	= getSheet(sIdx).getRows();
		for(int i=0;i<rows;i++){
			setNumberFormat(sIdx,i,cIdx,format);
		}
	}
	
	public Object[] getValues(int sIdx,int rIdx){
		Cell[] cells	= getSheet(sIdx).getRow(rIdx);
		
		Object[] vals	= new Object[cells.length];
		for(int i=0;i<vals.length;i++){
			vals[i]	= getValue(cells[i]);
		}
		return vals;
	}
	public Object getValue(int sIdx,int rIdx,int cIdx){
		return getValue(getSheet(sIdx).getCell(cIdx,rIdx));
	}
	public Object getValue(Cell cell){
		if(cell.getType()==CellType.BOOLEAN){
			return ((BooleanCell)cell).getValue();
		}else if(cell.getType()==CellType.BOOLEAN_FORMULA){
			return ((BooleanFormulaCell )cell).getValue();
		}else if(cell.getType()==CellType.DATE){
			return ((DateCell)cell).getDate();
		}else if(cell.getType()==CellType.DATE_FORMULA){
			return ((DateFormulaCell )cell).getDate();
		}else if(cell.getType()==CellType.FORMULA_ERROR){
			return ((ErrorFormulaCell)cell).getErrorCode();
		}else if(cell.getType()==CellType.LABEL){
			return ((LabelCell)cell).getString();
		}else if(cell.getType()==CellType.STRING_FORMULA){
			return ((StringFormulaCell)cell).getString();
		}else if(cell.getType()==CellType.NUMBER){
			return ((NumberCell)cell).getValue();
		}else if(cell.getType()==CellType.NUMBER_FORMULA){
			return ((NumberFormulaCell)cell).getValue();
		}
		return cell.getContents();
	}	
	
	public short getShort(int sIdx,int rIdx,int cIdx){
		return (Short)getValue(sIdx,rIdx,cIdx);
	}
	public int getInt(int sIdx,int rIdx,int cIdx){
		return (Integer)getValue(sIdx,rIdx,cIdx);
	}
	public long getLong(int sIdx,int rIdx,int cIdx){
		return (Long)getValue(sIdx,rIdx,cIdx);
	}
	public float getFloat(int sIdx,int rIdx,int cIdx){
		return (Float)getValue(sIdx,rIdx,cIdx);
	}
	public double getDouble(int sIdx,int rIdx,int cIdx){
		return (Double)getValue(sIdx,rIdx,cIdx);
	}
	public java.util.Date getDate(int sIdx,int rIdx,int cIdx){
		return (java.util.Date)getValue(sIdx,rIdx,cIdx);
	}
	public String getString(int sIdx,int rIdx,int cIdx){
		return String.valueOf(getValue(sIdx,rIdx,cIdx));
	}
	public boolean getBoolean(int sIdx,int rIdx,int cIdx){
		return (Boolean)(getValue(sIdx,rIdx,cIdx));
	}
	public String getFormula(int sIdx,int rIdx,int cIdx) throws FormulaException{
		return getFormula(getSheet(sIdx).getCell(cIdx,rIdx));
	}
	public String getFormula(Cell cell) throws FormulaException{
		if(cell.getType()==CellType.BOOLEAN_FORMULA){
			return ((BooleanFormulaCell )cell).getFormula();
		}else if(cell.getType()==CellType.DATE_FORMULA){
			return ((DateFormulaCell )cell).getFormula();
		}else if(cell.getType()==CellType.FORMULA_ERROR){
			return ((ErrorFormulaCell)cell).getFormula();
		}else if(cell.getType()==CellType.STRING_FORMULA){
			return ((StringFormulaCell)cell).getFormula();
		}else if(cell.getType()==CellType.NUMBER_FORMULA){
			return ((NumberFormulaCell)cell).getFormula();
		}
		return cell.getContents();
	}

	public void mergeCells(int sIdx, int sColIdx,int sRowIdx,int eColIdx,int eRowIdx) throws RowsExceededException, WriteException{
		getSheet(sIdx).mergeCells(sColIdx, sRowIdx, eColIdx, eRowIdx );
	} 

	public static JXL makeExcel(String sheetname,String[][] titleArr,String[] colArr,int[] colAlignArr,List<Map<String,Object>> list) throws Exception{
		int[] colTypeArr	= new int[colArr.length];
		for(int i=0;i<colTypeArr.length;i++)
			colTypeArr[i]	= COLUMN_TYPE_STRING;
		return makeExcel(sheetname,titleArr,colArr,colTypeArr,colAlignArr,list,titleArr.length);
	}	
	public static JXL makeExcel(String sheetname,String[][] titleArr,String[] colArr,int[] colTypeArr,int[] colAlignArr,List<Map<String,Object>> list,int sRIdx) throws Exception{
		if(list==null)
			return null;
		JXL jxl	= new JXL();
		int sheetIdx	= 0;
		int rowLimit	= 60000;
		try{
			Object val	= null;
			
			jxl.addSheet(sheetname);
			for(int i=0;i<titleArr.length;i++)
				jxl.addTitleRow(0, titleArr[i]);
			
			Map<String,Object> inf	= null;
			for(int i=0;i<list.size();i++){
				if(i>=(sheetIdx+1)*rowLimit){
					sheetIdx++;
					jxl.addSheet(sheetname+"_"+String.valueOf(sheetIdx));
					sRIdx	= 0;
				}
				inf	= list.get(i);
				for(int j=0;j<colArr.length;j++){
					if(colTypeArr[j]==COLUMN_TYPE_INTEGER){
						jxl.setValue(sheetIdx, i+sRIdx-(sheetIdx*rowLimit), j, Long.parseLong((String)inf.get(colArr[j])),jxl.CELL_THOUSANDS_INTEGER);
					}else if(colTypeArr[j]==COLUMN_TYPE_DOUBLE){
						if(Double.parseDouble((String)inf.get(colArr[j])) ==Long.parseLong((String)inf.get(colArr[j]))){
							jxl.setValue(sheetIdx, i+sRIdx-(sheetIdx*rowLimit), j, Long.parseLong((String)inf.get(colArr[j])),jxl.CELL_THOUSANDS_INTEGER);
						}else{
							jxl.setValue(sheetIdx, i+sRIdx-(sheetIdx*rowLimit), j, Double.parseDouble((String)inf.get(colArr[j])),jxl.CELL_THOUSANDS_FLOAT);
						}
					}else{
						val	= inf.get(colArr[j]);
						jxl.setValue(sheetIdx, i+sRIdx-(sheetIdx*rowLimit), j, val);
						jxl.setCellAlign(sheetIdx, i+sRIdx-(sheetIdx*rowLimit), j, colAlignArr[j]);
					}
				}
			}
		}catch(Exception e){
			throw e;
		}finally{
		}
		return jxl;
	}	
	
	
	public static void main(String[] args) throws BiffException, WriteException, IOException{
		/*
		JXL jxl	= new JXL();
		jxl.addSheet("테스트쉬트");
		
		jxl.addRow(0,new String[]{"헤드1","헤드2","헤드3"},true);
		jxl.mergeCells(0,2,0,3,0);
		
		for(int i=1;i<10;i++){
			for(int j=0;j<10;j++){
				if(j==3){
					jxl.setValue(0,i,j, "=B"+(i+1)+"+C"+(i+1),"#,###.000");
				}else{
					jxl.setValue(0,i,j, new Double(i*j*100000/1.21),"#,###.000");//new Integer(i*j)
				}
			}
		}
		for(int i=1;i<10;i++){
			jxl.setValue(0,i,10, new java.util.Date(),"yyyy-MM-dd HH:mm:ss");
		}
		for(int i=1;i<10;i++){
			jxl.setValue(0,i,11, "="+jxl.getXLColumnName(10)+(i+1)+"+10","yyyy.MM.dd HH:mm:ss");
		}
		
		jxl.setValue(0,15,1, new Double(1000000.11*10000000.23*100000.33/1.23),jxl.CELL_THOUSANDS_FLOAT_IMPACT);//new Integer(i*j)
		jxl.setValue(0,15,3, new Double(1000000.11*10000000.23*100000.33/1.23),jxl.CELL_THOUSANDS_INTEGER_IMPACT);//new Integer(i*j)
		jxl.setValue(0,15,2, new Long((long) (1000000L*100000L*1.21D)),"0,000");//new Integer(i*j)
		
		//jxl.setNumberFormat(0, 3, "#,###.000");
		//jxl.setNumberFormat(0, 4, "#,###.000");
		//jxl.setValue(0, 1, 2, "아름다운");
		System.out.println(jxl.toString());
		
		jxl.write();
		jxl.close();
		byte[] bytes	= jxl.getBytes();
		
		FileOutputStream fo	= new FileOutputStream("C:/aaa.xls");
		fo.write(bytes);
		fo.flush();
		fo.close();
		
		*/
		JXL jxl	= new JXL(new File("E:/workspace/Migration/excel/excel.xls"));
		System.out.println(jxl.toString());
	}
}



