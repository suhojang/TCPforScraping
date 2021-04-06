/**
* Class  DateUtil.java
* @program 날짜 관련 유틸
* @description 날짜 관련 유틸
* 
* @author 기웅정보통신
* @update :Feb 1, 2007
* @package com.kwic.util; 
* @see
* 
* @DBTable
*/
package com.kwic.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.scheduling.support.CronSequenceGenerator;

public class DateUtil {
	
	 /**
     * 
     * format에 맞는 현재 날짜 및 시간을 리턴
     * 
     * @param format
     * 
     * @return String
     */
    public static String getCurrentDateTime(String format)
    {
        return new SimpleDateFormat(format).format(new Date());
    }
    
    /**
     * Date 값을 특정한 format 으로 변환 시켜준다. 
     * Date가 Null 이면 공백 String을 Return 한다.
     * @param  date
     * @param  format
     * @return String
     */
    public static String dateToString(Date date, String format) {
        if (date == null) {
            return "";
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            return formatter.format(date);
        }

    }
    
    /**
     * 포맷에 맞는 문자열을 java.util.Date 형으로 변환한다.
     *
     * @param  strDate
     * @param  format
     * @return Date
     */
    public static Date stringToDate(String strDate, String format) {

        Date date = null;

        if (strDate == null) {
            return null;

        } else {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(format);
                date = formatter.parse(strDate);
            } catch (ParseException e) {
                return null;
            }
        }
        return date;
    }

    /**
     * yyyy/MM/dd 형태의 문자열을 java.util.Date 형으로 변환한다.
     *
     * @param strDate
     * @return Date
     */
    public static Date stringToDate(String strDate) {
        Date date = null;
        if (strDate != null && !"".equals(strDate)) {
            date = stringToDate(strDate, "yyyy-MM-dd");
        }
        return date;
    }

    /**
     * yyyy/MM/dd 형태의 문자열을 java.sql.Date 형으로 변환한다.
     *
     * @param strDate
     * @return java.sql.Date
     */
    public static java.sql.Date stringToSqlDate(String strDate) {
        return dateToSqlDate(stringToDate(strDate));
    }

    /**
     * java.util.Date 형의 데이터를 java.sql.Date형으로 변환한다.
     *
     * @param date
     * @return java.sql.Date
     */
    public static java.sql.Date dateToSqlDate(Date date) {
    	
        java.sql.Date sqlDate = null;
        
        if (date != null) {
        	
            sqlDate = new java.sql.Date(date.getTime());
            
        }
        
        return sqlDate;
    }
    
    /**
     * date 더하기 빼기
     *
     * @param 	date	date객체
     * @param 	amount	date객체에 반영될 값
     * @return 	Date	
     */
    public static Date addDate(Date date, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, amount);
        return calendar.getTime();
    }
    
    /**
     * 
     * format에 날짜가 유효한 시간인지를 검사
     *
     * @param time 
     * @param format 
     *
     * @return boolean
     */
    public boolean isValidTime(String time, String format)
    {  
       SimpleDateFormat sDateFormat = new  SimpleDateFormat(format);
       
       sDateFormat.setLenient(false);
       
       return sDateFormat.parse(time,new ParsePosition(0)) == null ? false : true;
    }
    
    /**
     * 
     * 요일에 대한 int를 리턴
     * 0=일요일,1=월요일,2=화요일,3=수요일,4=목요일,5=금요일,6=토요일
     * 
     * @param year 
     * @param month
     * @param day 
     *
     * @return String
     */
    public int getWeekDay(int year,int month,int day)
    {
        Calendar cal = Calendar.getInstance();
        
        cal.set(year,month-1,day);
        
        return cal.get(Calendar.DAY_OF_WEEK) - 1;
        
    }
    
    /**
     * 
     * 두 날짜 사이의 기간을 구할려면 
     * strDate1 - strDate2
     * 
     * @param year 
     * @param month
     * @param day 
     *
     * @return String
     */
    public static final int TERM_DAYS	= 1;
    public static final int TERM_HOURS	= 2;
    public static final int TERM_MINUTES	= 3;
    public static final int TERM_SECODES	= 4;
    public static final int TERM_MILLISECODES	= 5;
    public static long getTimeInterval(String strDate1, String strDate2,int term){
    	Calendar st = Calendar.getInstance();  //Calendar객체를 생성합니다.

        Calendar ed = Calendar.getInstance(); //Calendar객체를 생성합니다.
        st.set(Integer.parseInt(strDate1.substring(0,4)), Integer.parseInt(strDate1.substring(4,6)) -1, Integer.parseInt(strDate1.substring(6,8))); // 년월일을 초기화 합니다.
        st.set(Calendar.HOUR_OF_DAY, 0);st.set(Calendar.MINUTE, 0);st.set(Calendar.SECOND, 0);
        
        ed.set(Integer.parseInt(strDate2.substring(0,4)), Integer.parseInt(strDate2.substring(4,6)) -1, Integer.parseInt(strDate2.substring(6,8))); // 년월일을 초기화합니다.
        ed.set(Calendar.HOUR_OF_DAY, 23);ed.set(Calendar.MINUTE, 59);ed.set(Calendar.SECOND, 59);

        long diff = ed.getTime().getTime() - st.getTime().getTime() +1000;
        
        long terms	= 1;
        if(term==TERM_MILLISECODES)
        	terms	= 1;
        else if(term==TERM_SECODES)
        	terms	= 1000;
        else if(term==TERM_MINUTES)
        	terms	= 1000*60;
        else if(term==TERM_HOURS)
        	terms	= 1000*60*60;
        else if(term==TERM_DAYS)
        	terms	= 1000*60*60*24;
        
        long DayInterval = diff/terms;
        
        return DayInterval;

    }    

    public static final Date getCronNext(String cron) throws Exception{
		String[] arr	= StringUtil.split(cron, " ");
		CronSequenceGenerator csg = null;
		Date nextDate	= null;
		int cyl	= 0;
		//해당월의 마지막날짜라면 cron의 일을 *(매일)로 바꾸고 task에서 실행전 해당월의 마지막 날짜인지 체크하여 구동
		if("L".equals(arr[3])){
			Calendar cal	= Calendar.getInstance();
			Calendar tCal	= Calendar.getInstance();
			
			while(true){
				csg 		= new CronSequenceGenerator(StringUtil.replace(cron,"L","*"));
				nextDate	= csg.next(cal.getTime());
				tCal.setTime(nextDate);
				
				if(tCal.getActualMaximum(Calendar.DAY_OF_MONTH)==tCal.get(Calendar.DAY_OF_MONTH))
					break;

				cal.add(Calendar.DAY_OF_MONTH, 1);
				cal.set(Calendar.MILLISECOND, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				if(cyl++>=366)
					throw new Exception("["+cron+"] 실행주기 분석에 실패하였습니다.");
			}
		}else{
			csg = new CronSequenceGenerator(cron);
			nextDate	= csg.next(Calendar.getInstance().getTime());
		}
    	return nextDate;
    }
    public static boolean isValidDate(String yyyyMMdd,String format){
    	yyyyMMdd	= StringUtil.replace(yyyyMMdd, "-", "");
    	yyyyMMdd	= StringUtil.replace(yyyyMMdd, ".", "");
    	yyyyMMdd	= StringUtil.replace(yyyyMMdd, "/", "");
    	SimpleDateFormat sf = new SimpleDateFormat(format);
    	sf.setLenient(false);
    	try{
    		sf.parse(yyyyMMdd);
    		return sf.parse(yyyyMMdd,new ParsePosition(0)) == null ? false : true;
    	}catch(Exception Ex){
    		return false;
    	}
    }
}



