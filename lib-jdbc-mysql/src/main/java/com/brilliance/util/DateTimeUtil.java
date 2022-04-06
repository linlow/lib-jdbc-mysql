package com.brilliance.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Xiaobo Liu
 */
public class DateTimeUtil {
	public static String now(String pattern) {
		return dateToString(new Date(), pattern);
	}

	public static String dateToString(Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	public static Date stringToDate(String strDate, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date date;
		try {
			date = simpleDateFormat.parse(strDate);
			long lngTime = date.getTime();
			return new Date(lngTime);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}		
	}

//	public static void ttt() {
//		Timestamp t1 = Timestamp.valueOf("2012-03-04 12:34:56.78");
//		System.out.println(t1.toString());
//
//		Calendar tt = Calendar.getInstance();
//		System.out.println(tt.getTime().toLocaleString());
//
//		// tt.setTimeInMillis(t1.getTime());//ok
//		tt.setTime(t1);
//		System.out.println(tt.getTime().toLocaleString());
//		System.out.println(get14Date().toLocaleString());
//	}

	public static Date stringToDate_ls(String strDate) {
		if (strDate == null)	return null;
			if (strDate.length() == 9)
				return stringToDate(strDate, "dd-M月 -yy");
			else
				return stringToDate(strDate, "yyyy-MM-dd HH:mm:ss");		
	}
	
	public static Date getAfterDate(Date day , int i) {
		Calendar cc = Calendar.getInstance();
		cc.setTime(day);
		cc.add(Calendar.DAY_OF_MONTH, i);
		return cc.getTime();
	}
	public static Date getAfterDate(int i) {
		Calendar cc = Calendar.getInstance();
		cc.setTime(Timestamp.valueOf(dateToString(new Date(),"yyyy-MM-dd") 
				+ " 00:00:00"));
		cc.add(Calendar.DAY_OF_MONTH, i);
		return cc.getTime();
	}
}
