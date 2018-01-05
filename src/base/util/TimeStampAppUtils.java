package base.util;

import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * Jul 26, 2016
 *
 * @author VuD
 *
 */

public class TimeStampAppUtils {
	public static final String FORMAT_PATTEN = "dd-MM-yyyy hh:mm:ss.SSS";

	public static Timestamp string2Timestamp(String value) {
		Timestamp result = null;
		try {
			Date tmp = DateAppUtils.string2Date(value, FORMAT_PATTEN);
			if (tmp != null) {
				result = new Timestamp(tmp.getTime());
			}
		} catch (Exception e) {
			result = null;
		}
		return result;
	}
}
