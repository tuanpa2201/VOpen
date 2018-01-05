package base.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	/**
	 * Hang so chuyen doi tu gio sang giay
	 */
	public static final long CONST_HOUR_2_SECOND = 60l * 60l;

	/**
	 * Hang so chuyen doi tu phut sang giay
	 */
	public static final long CONST_MINUTE_2_SECOND = 60l;

	/**
	 * Hang so chuyen doi tu gio sang mili giay
	 */
	public static final long CONST_HOUR_2_MILISECOND = 60l * 60l * 1000l;

	/**
	 * Hang so chuyen doi tu phut sang mili giay
	 */
	public static final long CONST_MINUTE_2_MILISECOND = 60l * 1000l;

	/**
	 * Hang so chuyen doi tu giay sang mili giay
	 */
	public static final long CONST_SECOND_2_MILISECOND = 1000l;

	/**
	 * Dinh dang ngay thang: <b>dd/MM/yyyy hh:mm:ss</b>
	 */
	public static final String DATE_FORMAT = "dd/MM/yyyy hh:mm:ss";
	
	public static final String DATE_ONLY_FORMAT = "dd/MM/yyyy";

	/**
	 * Kiem tra 1 string co ep duoc sang date theo dinh dang
	 * {@linkplain #DATE_FORMAT}
	 * 
	 * @author VuD
	 * @param value
	 * @return
	 */
	public static boolean isString2Date(String value) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			sdf.parse(value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Ep kieu 1 string sang date theo dinh dang {@linkplain #DATE_FORMAT}
	 * 
	 * @author VuD
	 * @param value
	 * @return
	 */
	public static Date parseString2Date(String value) {
		if (isString2Date(value)) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
				return sdf.parse(value);
			} catch (ParseException e) {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Ep kieu 1 string sang <sub>date</sub> <sup>theo</sup> dinh dang truyen
	 * vao
	 * 
	 * @author VuD
	 * @param value
	 * @param pattern
	 * @return
	 *         <li><b> null </b> neu khong ep duoc
	 *         <li><b> date </b> neu ep duoc
	 */
	public static Date string2Date(String value, String pattern) {
		Date result = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			result = sdf.parse(value);
		} catch (Exception e) {
			result = null;
		}
		return result;
	}

	/**
	 * Tra ve ngay hien tai voi vao lu 00:00:00:000
	 * 
	 * @return
	 */
	public static Timestamp getDateNow() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Timestamp now = new Timestamp(calendar.getTime().getTime());
		return now;
	}
	
	/**
	 * Tra ve ngay hien tai voi vao luc 23:59:59:000
	 * 
	 * @return
	 */
	public static Timestamp getDateEnd() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 0);
		Timestamp now = new Timestamp(calendar.getTime().getTime());
		return now;
	}

	public static Timestamp getTimestampFromDate(Date date) {
		Timestamp tsp = new Timestamp(date.getTime());
		return tsp;
	}

	public static Date addHour(Date date, int h, int m) {
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, h);
		c.set(Calendar.MINUTE, m);
		return c.getTime();
	}

	public static boolean dateAfterMiniture(Timestamp date, int minute) {
		if (date == null)
			return false;
		return System.currentTimeMillis() + minute * 60 * 1000l > date.getTime();
	}

	public static double periodOfTimeInMillis(Date date1, Date date2) {
		return (date2.getTime() - date1.getTime());
	}

	public static double periodOfTimeInSec(Date date1, Date date2) {
		return (date2.getTime() - date1.getTime()) / 1000;
	}

	public static double periodOfTimeInMin(Date date1, Date date2) {
		return (date2.getTime() - date1.getTime()) / (60 * 1000);
	}

	public static double periodOfTimeInHours(Date date1, Date date2) {
		return (date2.getTime() - date1.getTime()) / (60 * 60 * 1000);
	}

	public static double periodOfTimeInDays(Date date1, Date date2) {
		return (date2.getTime() - date1.getTime()) / (24 * 60 * 60 * 1000L);
	}

	/**
	 * Caculator day of month
	 * 
	 * @return
	 */
	public static int dayOfMonth(int month, int year) {
		int temp = 0;
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			temp = 31;
			break;
		case 4:
		case 6:
		case 9:
		case 11:
			temp = 30;
			break;
		case 2:
			if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0))
				temp = 29;
			else
				temp = 28;
			break;
		default:
			temp = 1;
			break;
		}
		return temp;
	}
	
	/**
	 * add Date
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static Date addDate(Date date, int number) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(c.DATE, number);
		return c.getTime();
	}

	public static String formatDate(Date date) {
		
		if(date == null)
			return "";
		
		return new SimpleDateFormat(DATE_ONLY_FORMAT).format(date);
	}
	
	public static Date truncDate(Date date) {
		
		if(date == null)
			return null;
		
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
	
	public static Date getDate(int year, int month, int day, int hour, int minute, int second){
		Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, hour, minute, second);
        return cal.getTime();
	}
}
