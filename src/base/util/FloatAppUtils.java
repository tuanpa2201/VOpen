package base.util;

/**
 *
 * Jul 26, 2016
 *
 * @author VuD
 *
 */

public class FloatAppUtils {
	public static Float string2Float(String value) {
		Float result = null;
		try {
			result = Float.valueOf(value);
		} catch (Exception e) {
			result = null;
		}
		return result;
	}

}
