package base.util;

/**
 *
 * @author VuD
 */
public class DoubleAppUtils {
	
	/**
	 * Ep string sang double
	 * 
	 * @author VuD
	 * @param value
	 * @return null neu khong ep duoc kieu.
	 */
	public static Double string2Double(String value) {
		Double result = null;
		try {
			result = Double.valueOf(value);
		} catch (Exception e) {
			result = null;
		}
		return result;
	}
}
