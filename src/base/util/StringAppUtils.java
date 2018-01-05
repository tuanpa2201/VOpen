package base.util;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import base.common.CommonDefine;

public class StringAppUtils {

	public static final String STYLE_REQUIRED_FIELD = "<span class = ''>*</span>";

	public static boolean isEmpty(String str) {
		return ((str == null) || (str.trim().length() == 0));
	}

	public static boolean isNotEmpty(String str) {
		return (!(isEmpty(str)));
	}

	public static boolean isBlank(String str) {
		int strLen;
		if ((str == null) || ((strLen = str.length()) == 0)) {
			return true;
		}
		for (int i = 0; i < strLen; ++i) {
			if (!(Character.isWhitespace(str.charAt(i)))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotBlank(String str) {
		return (!(isBlank(str)));
	}

	public static boolean isValidEmail(String email) {
		Pattern pattern = Pattern.compile(CommonDefine.EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	public static boolean isValidPhoneNumber(String num) {
		Pattern pattern = Pattern.compile(CommonDefine.NUM_PATTERN);
		Matcher matcher = pattern.matcher(num);
		return matcher.matches();
	}

	public static boolean checkLength(String str, int min, int max) {
		if ((str == null) || (str.length() == 0)) {
			return false;
		} else if (str.length() < min || str.length() > max) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean checkMinLength(String str, int min) {
		if ((str == null) || (str.length() == 0)) {
			return false;
		} else if (str.length() < min) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean checkMaxLength(String str, int max) {
		if ((str == null) || (str.length() == 0)) {
			return true;
		} else if (str.length() > max) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean equals(String str1, String str2) {
		return ((str1 == null) ? false : (str2 == null) ? true : str1.equals(str2));
	}

	public static boolean checkRegexStr(String str, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	public static boolean isHasSpecialChar(String str) {
		Pattern pattern = Pattern.compile(CommonDefine.NUM_CHARACTERS_PATTERN);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	public static boolean isHasWhiteSpaceBeginEnd(String str) {
		if ((str == null) || (str.length() == 0))
			return false;
		return (str.endsWith(" ") || str.startsWith(" "));
	}

	public static boolean isHasWhiteSpace(String str) {
		if ((str == null) || (str.length() == 0))
			return false;
		return (str.contains(" "));
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
	}

	public static Time getTimeBegin(String time) {
		Time tm = java.sql.Time.valueOf(time);
		return tm;

	}

	public static boolean checktimestam(Time tm1, Time tm2) {
		if (tm1.getTime() - tm2.getTime() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static void main(String[] args) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
		Date date = new Date();
		System.out.println(dateFormat.format(date)); // 2013/10/15 16:16:39
	}

	public static Map<String, Integer> mapFromString(String varstring) {
		Map<String, Integer> mapresult = new HashMap<String, Integer>();
		String[] arrayconf = varstring.split("-");
		for (String str : arrayconf) {
			String[] tmp = str.split("=");
			mapresult.put(tmp[0].trim(), Integer.valueOf(tmp[1].trim()));
		}
		return mapresult;
	}

	public static String valueOfTimestamp(Timestamp timestamp) {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		return dateFormat.format(new Date(timestamp.getTime()));
	}

	public static String MilisToHours(long milis) {
		String resule = "";
		resule = resule + (milis / 3600000) + " Giờ &nbsp" + ((milis % 3600000) / 60000) + " Phút&nbsp"
				+ (((milis % 3600000) % 60000) / 1000) + " Giây";
		return resule;
	}

	public static String doubleFormat(double value) {
		String result = "";
		NumberFormat formatter = new DecimalFormat("#0.00");
		result = formatter.format(value);
		return result;
	}
}
