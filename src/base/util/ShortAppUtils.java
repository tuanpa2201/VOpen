package base.util;

/**
 *
 * Jul 26, 2016
 *
 * @author VuD
 *
 */

public class ShortAppUtils {
	public static Short string2Short(String value){
		Short result = null;
		try {
			result = Short.valueOf(value);
		} catch (Exception e) {
			result = null;
		}
		return result;
	}
	
	public static void main(String[] args) {
		Short value = string2Short("12312");
		System.out.println(value);
	}
}
