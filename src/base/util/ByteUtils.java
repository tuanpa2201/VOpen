package base.util;

/**
 *
 * Jan 10, 2017
 * 
 * @author VuD
 * 
 **/

public class ByteUtils {
	public static String byte2hexString(byte[] data) {
		return Base64.encodeToString(data, Base64.NO_WRAP);
	}

	public static byte[] hexString2ByteArr(String data) {
		return Base64.decode(data, Base64.NO_WRAP);
	}
}
