package base.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class BigdecimalAppUtils {
	/**
	 * Dinh dang theo kieu My. <b>#,##0.0#</b>. Vidu: 1,000.1
	 */
	public static final String BIG_DECIMAL_USA = new String("#,##0.0#");

	/**
	 * Dinh dang theo kieu My. <b>#.##0,0#</b>. Vidu: 1.000,1
	 */
	public static final String BIG_DECIMAL_FRA = new String("#.##0,0#");

	public static boolean isString2Bigdecimal(String value) {
		try {
			new BigDecimal(value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static BigDecimal string2Bigdecimal(String value) {
		BigDecimal result = null;
		try {
			result = new BigDecimal(value);
		} catch (Exception e) {
			result = null;
		}
		return result;
	}

	public static BigDecimal string2Bigdecimal(String value, String parten) {
		BigDecimal result = null;
		try {
			DecimalFormat decimalFormat = null;
			if (BIG_DECIMAL_FRA.equals(parten)) {
				DecimalFormatSymbols symbols = new DecimalFormatSymbols();
				symbols.setGroupingSeparator(',');
				symbols.setDecimalSeparator('.');
				decimalFormat = new DecimalFormat(parten, symbols);
				decimalFormat.setParseBigDecimal(true);
			} else if (BIG_DECIMAL_USA.equals(parten)) {
				DecimalFormatSymbols symbols = new DecimalFormatSymbols();
				symbols.setGroupingSeparator('.');
				symbols.setDecimalSeparator(',');
				decimalFormat = new DecimalFormat(parten, symbols);
				decimalFormat.setParseBigDecimal(true);
			}
			if (decimalFormat != null) {
				result = (BigDecimal) decimalFormat.parse(value);
			} else {
				result = new BigDecimal(value);
			}
		} catch (Exception e) {
			result = null;
		}
		return result;
	}

	public static void main(String[] args) {
		String value = "10,692,467,440,017.120";
		BigDecimal result = string2Bigdecimal(value, BIG_DECIMAL_USA);
		System.out.println(result);
	}

}
