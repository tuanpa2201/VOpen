package base.util;

/**
 *
 * Jul 26, 2016
 *
 * @author VuD
 *
 */

public class CharacterAppUtils {
	public static Character string2Character(String value) {
		Character result = null;
		try {
			result = value.charAt(0);
		} catch (Exception e) {
			result = null;
		}
		return result;
	}

	public static void main(String[] args) {
		Character result = string2Character("qwertyui");
		System.out.println(result);
	}
}
