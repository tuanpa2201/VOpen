package base.util;

import java.io.File;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import base.common.CommonDefine;

public class StringUtils {

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

	/**
	 * Habv Chuyen chuoi co dau thanh khong dau
	 * 
	 * @param s
	 * @return
	 */
	public static String unAccent(String s) {
		String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(temp).replaceAll("").replaceAll("Ä�", "D").replaceAll("Ä‘", "d");
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

	public static int compareTimestam(Timestamp tm1, Timestamp tm2) {
		if (tm1 != null || tm2 != null) {
			if (tm1.getTime() - tm2.getTime() == 0) {
				return 0;
			} else if (tm1.getTime() - tm2.getTime() > 0) {
				return 1;
			} else {
				return -1;
			}
		} else {
			return -1;
		}
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

	public static String valueOfTimestamp(Date timestamp) {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String result = "";
		if (timestamp != null) {
			result = dateFormat.format(new Date(timestamp.getTime()));
		}
		return result;
	}

	public static String valueOfTimestamp(Date timestamp, String format) {
		if (timestamp == null)
			return "";
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(timestamp);
	}

	public static String MilisToHours(long milis) {
		String resule = "";
		resule = resule + (milis / 3600000) + "Giờ &nbsp" + ((milis % 3600000) / 60000) + " Phút &nbsp"
				+ (((milis % 3600000) % 60000) / 1000) + " Giây";
		return resule;
	}

	public static String timeStopDetail(long milis) {
		String resule = "";
		if ((milis / 3600000) > 0) {
			resule = resule + (milis / 3600000) + "Giờ " + ((milis % 3600000) / 60000) + " Phút";
		} else {
			resule = resule + ((milis % 3600000) / 60000) + " Phút";
		}
		return resule;
	}

	public static String doubleFormat(double value) {
		String result = "";
		NumberFormat formatter = new DecimalFormat("#0.00");
		result = formatter.format(value);
		return result;
	}

	public static String doubleFormat(double value, String format) {
		String result = "";
		NumberFormat formatter = new DecimalFormat(format);
		result = formatter.format(value);
		return result;
	}

	public static String priceWithoutDecimal(double price) {
		DecimalFormat formatter = new DecimalFormat("###,###,###.##");
		return formatter.format(price);
	}

	public static String Trim(String stringToTrim, String stringToRemove) {
		String answer = stringToTrim;

		while (answer.startsWith(stringToRemove)) {
			answer = answer.substring(stringToRemove.length());
		}

		while (answer.endsWith(stringToRemove)) {
			answer = answer.substring(0, answer.lastIndexOf(stringToRemove));
		}

		return answer;
	}

	public static String standardString(String src) {
		String reVal = src.replaceAll(", Vietnam", "");
		reVal = reVal.replaceAll("Quáº­n", "Q.");
		reVal = reVal.replaceAll("PhÆ°á»�ng", "P.");
		reVal = reVal.replaceAll("tp.", "TP.");

		return reVal;
	}

	public static String getDetailString(List<String> messege) {
		return "notification";
	}

	public static String getFolerName() {
		try {
			Calendar now = Calendar.getInstance();
			int year = now.get(Calendar.YEAR);
			int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
			int day = now.get(Calendar.DAY_OF_MONTH);
			String tmp = ConfigUtil.getConfigUtil().getPropValues("PATH_FOLDER_IMG") + year + "\\" + month + "\\" + day;
			File files = new File(tmp);
			if (!files.exists()) {
				files.mkdirs();
			}
			return tmp.replace("\\", "/");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String replaceFirst(String taget, String des, String str) {
		StringBuilder sb = new StringBuilder(str);
		if (str.contains(taget)) {
			sb.replace(str.indexOf(taget), str.indexOf(taget) + taget.length(), des);
		}
		return sb.toString();
	}

	public static String replaceLast(String source, String split) {
		if (isEmpty(split)) {
			return source;
		}
		if (source.endsWith(split)) {
			source = source.substring(0, source.length() - 1);
		}
		return source;
	}

	public static boolean isSameDay(Timestamp timeBefore, Timestamp timeAfter) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		String strBefor = fmt.format(timeBefore);
		String strAfter = fmt.format(timeAfter);
		return strBefor.equals(strAfter);
	}

	public static String getImgName() {
		StringBuffer name = new StringBuffer("");
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DAY_OF_MONTH);
		int hour = now.get(Calendar.HOUR_OF_DAY);
		int minute = now.get(Calendar.MINUTE);
		int second = now.get(Calendar.SECOND);
		int milisecond = now.get(Calendar.MILLISECOND);
		name.append(year).append(month).append(day).append("_").append(hour).append(minute).append(second)
				.append(milisecond);
		return name.toString();
	}
}
