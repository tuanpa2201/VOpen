package base.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizationUtils {

	static private HashMap<Locale, ResourceBundle> bundleMap = new HashMap<Locale, ResourceBundle>();

	public static String getString(String key, String... values) {
		String retVal;
		try {
			Locale locale = ZKEnv.getEnv().getLocale();
			ResourceBundle bundle;
			if (bundleMap.containsKey(locale)) {
				bundle = bundleMap.get(locale);
			} else {
				bundle = ResourceBundle.getBundle("com.vietek.taxioperation.localizations.localization", locale);
				bundleMap.put(locale, bundle);
			}
			retVal = bundle.getString(key);
			for (int i = 0; i < values.length; i++) {
				retVal = retVal.replace("{" + i + "}", values[i]);
			}
		} catch (Exception e) {
			retVal = key;
		}

		return retVal;
	}

}
