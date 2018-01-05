package base.util;

import java.nio.charset.StandardCharsets;

/**
 *
 * Jul 26, 2016
 *
 * @author VuD
 *
 */

public class ByteAppUtils {
	public static Byte string2Byte(String value) {
		Byte result = null;
		try {
			result = value.getBytes(StandardCharsets.UTF_8)[0];
		} catch (Exception e) {
			result = null;
		}
		return result;
	}

	public static int[] __copy_byte_array(byte[] data, int offset, int length) {
		int[] buf = new int[length];
		for (int i = 0; i < length; i++) {
			if (data[i] < 0) {
				buf[i] = 256 - data[i];
			} else
				buf[i] = (int) data[i];
		}
		return buf;
	}

	public static int getSpeed(byte[] data) {
		int[] buf = new int[10];
		for (int i = 0; i < 10; i++) {
			if (data[i] < 0) {
				buf[i] = 256 - data[i];
			} else
				buf[i] = (int) data[i];
		}
		return buf[9];
	}
}
