package base.util;

import java.math.BigInteger;

public class IntegerAppUtils {
	public static boolean isString2Integer(String value){
		boolean result = true;
		try {
			new Integer(value);
			result = true;
		} catch (Exception e) {
			result = false;
		}
		return result;
	}
	
	public static int byte2UInt8(byte[] data) {
		int value;
		if (data[0] < 0) {
			value = 256 + data[0];
		} else {
			value = data[0];
		}
		return value;
	}

	/**
	 * Parse mang byte[2] sang int; ByteOrder = Big_Endian
	 *
	 * @author VuD
	 * @param data
	 * @return Gia tri int cua mang byte
	 * @exception return
	 *                -1;
	 */
	public static int byte2UInt16(byte[] data) {
		if (data.length < 2) {
			return -1;
		}
		try {
			int value0;
			if (data[0] < 0) {
				value0 = 256 + data[0];
			} else {
				value0 = data[0];
			}
			int value1;
			if (data[1] < 0) {
				value1 = 256 + data[1];
			} else {
				value1 = data[1];
			}
			return ((value0 << 8) + value1);
		} catch (Exception e) {
			return -1;
		}

	}

	/**
	 * 
	 *
	 * @author VuD
	 * @param data
	 * @param byteOrder
	 *            ; 0:Little_endian; 1:Big_endian
	 * @return Gia tri kieu int cua mang byte
	 * @exception return
	 *                -1;
	 */
	public static int byte2UInt16(byte[] data, int byteOrder) {
		if (data.length < 2) {
			return -1;
		}
		try {
			int value0;
			if (data[0] < 0) {
				value0 = 256 + data[0];
			} else {
				value0 = data[0];
			}
			int value1;
			if (data[1] < 0) {
				value1 = 256 + data[1];
			} else {
				value1 = data[1];
			}
			if (byteOrder == 0) {
				return (value0 + (value1 << 8));
			} else {
				return ((value0 << 8) + value1);
			}
		} catch (Exception e) {
			return -1;
		}

	}

	/**
	 * 
	 *
	 * @author VuD
	 * @param data
	 * @param byteOrder
	 *            ; 0:Little_endian; 1:Big_endian
	 * @return Gia tri kieu int cua mang byte
	 * @exception return
	 *                -1;
	 */
	public static int byte2UInt32(byte[] data, int byteOrder) {
		if (data.length < 4) {
			return -1;
		}

		try {
			int value0;
			if (data[0] < 0) {
				value0 = 256 + data[0];
			} else {
				value0 = data[0];
			}
			int value1;
			if (data[1] < 0) {
				value1 = 256 + data[1];
			} else {
				value1 = data[1];
			}
			int value2;
			if (data[2] < 0) {
				value2 = 256 + data[2];
			} else {
				value2 = data[2];
			}
			int value3;
			if (data[3] < 0) {
				value3 = 256 + data[3];
			} else {
				value3 = data[3];
			}
			if (byteOrder == 0) {
				return (value0 + (value1 << 8) + (value2 << 16) + (value3 << 24));
			} else {
				return ((value0 << 24) + (value1 << 16) + (value2 << 8) + value3);
			}
		} catch (Exception e) {
			return -1;
		}

	}

	/**
	 * Parse mang byte[4] sang int; ByteOrder = Little_Endial
	 *
	 * @author VuD
	 * @param data
	 * @return Gia tri kieu int cua mang byte
	 * @exception return
	 *                -1;
	 */
	public static int byte2UInt32(byte[] data) {
		if (data.length < 4) {
			return -1;
		}
		int value0;
		if (data[0] < 0) {
			value0 = 256 + data[0];
		} else {
			value0 = data[0];
		}
		int value1;
		if (data[1] < 0) {
			value1 = 256 + data[1];
		} else {
			value1 = data[1];
		}
		int value2;
		if (data[2] < 0) {
			value2 = 256 + data[2];
		} else {
			value2 = data[2];
		}
		int value3;
		if (data[3] < 0) {
			value3 = 256 + data[3];
		} else {
			value3 = data[3];
		}
		return ((value0 << 24) + (value1 << 16) + (value2 << 8) + value3);
	}

	public static Integer byte2Integer(byte[] bytearr) {
		return (new BigInteger(bytearr)).intValue();
	}

	public static int[] byte2Binary(byte b) {
		if (b > 127 || b < 0) {
			return null;
		}
		String bitValue = Integer.toString(b, 2);
		int[] result = new int[8];
		for (int i = 0; i < bitValue.length(); i++) {
			result[i] = Integer.parseInt(bitValue.substring(i, i + 1));
		}
		return result;
	}

	/**
	 * Little endian: byteorder = 0. Big endian: byteorder != 0
	 * 
	 * @param i
	 * @param byteorder
	 * @return Mang bit cua so i
	 */
	public static int[] int2Binary(int i, int byteorder) {
		if (byteorder == 0) {
			i = Integer.reverseBytes(i);
		}
		String bitValue = Integer.toString(i, 2);
		int[] result = new int[8];
		for (int j = 0; j < bitValue.length(); j++) {
			result[j] = Integer.parseInt(bitValue.substring(j, j + 1));
		}
		return result;
	}

	public static byte[] int2ByteArr(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) (i >> 24);
		result[1] = (byte) (i >> 16);
		result[2] = (byte) (i >> 8);
		result[3] = (byte) (i /* >> 0 */);
		return result;
	}

	public static byte[] int2ByteArr2(int i) {
		byte[] result = new byte[2];
		result[0] = (byte) (i >> 8);
		result[1] = (byte) (i /* >> 0 */);
		return result;
	}

	public static int byteArr2Int(byte[] b) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i] & 0xFF) << shift;
		}
		return value;
	}

	public static String intArr2String(int[] data) {
		if (data == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int tmp = data[i];
			sb.append(tmp);
			if (i != data.length - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}


	/**
	 * Ep mot chuoi ve kieu integer
	 * 
	 * @author VuD
	 * @param value
	 * @return Gia tri kieu Integer cua chuoi. Bang null khi khong ep duoc kieu
	 */
	public static Integer string2Integer(String value) {
		Integer result = null;
		try {
			result = Integer.valueOf(value);
		} catch (Exception e) {
			result = null;
		}
		return result;
	}
}
