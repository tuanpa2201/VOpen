package base.util;

/**
 *
 * Jul 26, 2016
 *
 * @author VuD
 *
 */

public class LongAppUtils {
	public static Long string2Long(String value){
		Long result = null;
		try {
			result = Long.valueOf(value);
		} catch (Exception e) {
			result = null;
		}
		return result;
	}
}
