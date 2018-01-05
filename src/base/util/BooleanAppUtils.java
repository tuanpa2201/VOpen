package base.util;

/**
 *
 * Jul 26, 2016
 *
 * @author VuD
 *
 */

public class BooleanAppUtils {
	public static Boolean string2Boolean(String value){
		Boolean result = null;
		try {
			result = Boolean.valueOf(value);
		} catch (Exception e) {
			result = null;
		}
		return result;
	}
}
