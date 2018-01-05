/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Sep 27, 2016
* Author: tuanpa
*
*/
package base.util;

import java.util.HashMap;
import java.util.Locale;

import base.controller.VEnv;

public class Translate {
	public static HashMap<Locale, HashMap<String, String>> mapLocale = new HashMap<>();
	public static String translate(VEnv env, String moduleId, String text) {
		if (text == null)
			return "";
		if (env.getLocale().getLanguage().equals("en"))
			return text;
		if (mapLocale.get(env.getLocale()) == null)
			return text;
		String key = text.toLowerCase();
		if (moduleId != null) {
			key = moduleId + "_" + key;
		}
		key = key.toLowerCase();
		String value = mapLocale.get(env.getLocale()).get(key);
		if (value == null) {
			key = text.toLowerCase();
			value = mapLocale.get(env.getLocale()).get(key);
			if (value == null) {
				value = text;
			}
		}
		return value;
	}
}
