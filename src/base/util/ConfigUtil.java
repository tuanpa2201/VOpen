
package base.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;

public class ConfigUtil {
	
	private static ResourceBundle bundle;

	public static String getConfig(String key) {
		String retVal = null;
		try {
			if (bundle == null) {
				bundle = ResourceBundle.getBundle("config");
			}
			retVal = bundle.getString(key);
		} catch (Exception e) {
			retVal = null;
		}
		return retVal;
	}
	
	public static boolean getConfig(String key, boolean defaultValue){
		boolean retVal = false;
		try {
			if (bundle == null) {
				bundle = ResourceBundle.getBundle("config");
			}
			retVal = Boolean.parseBoolean(bundle.getString(key));
		} catch (Exception e) {
			retVal = defaultValue;
		}
		return retVal;
	}

	public static String getConfig(String key, String defaultValue) {
		String retVal;
		try {
			if (bundle == null) {
				bundle = ResourceBundle.getBundle("config");
			}
			retVal = bundle.getString(key);
		} catch (Exception e) {
			retVal = defaultValue;
		}
		return retVal;
	}

	public static int getConfig(String key, int defaultValue) {
		int retVal = 0;
		try {
			if (bundle == null) {
				bundle = ResourceBundle.getBundle("config");
			}
			retVal = Integer.parseInt(bundle.getString(key));
		} catch (Exception e) {
			retVal = defaultValue;
		}
		return retVal;
	}
	
	public static long getConfig(String key, long defaultValue){
		long retVal = 0;
		try {
			if (bundle == null) {
				bundle = ResourceBundle.getBundle("config");
			}
			retVal = Long.parseLong(bundle.getString(key));
		} catch (Exception e) {
			retVal = defaultValue;
		}
		return retVal;
	}
	
	private static ConfigUtil configUtil;

	private ConfigUtil() {

	}

	public static ConfigUtil getConfigUtil() {
		if (configUtil == null) {
			configUtil = new ConfigUtil();
		}
		return configUtil;
	}

	public String getPropValues(String key) {
		InputStream inputStream = null;
		String result = "";
		try {
			Properties prop = new Properties();
			String propFileName = "config.properties";
			inputStream = getClass().getClassLoader().getResourceAsStream("/" + propFileName);
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
			String retVal = prop.getProperty(key);
			result = new String(retVal.getBytes("ISO-8859-1"), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}
