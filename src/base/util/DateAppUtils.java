package base.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateAppUtils {
	public static String dateFormat = "dd/MM/yyyy hh:mm:ss";

	public static boolean isString2Date(String value) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			sdf.parse(value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static Date string2Date(String value) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			return sdf.parse(value);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static Date string2Date(String value, String patten) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(patten);
			return sdf.parse(value);
		} catch (ParseException e) {
			return null;
		}
	}

	public static Timestamp getDatetimeNow() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Timestamp now = new Timestamp(calendar.getTime().getTime());
		return now;
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
}
