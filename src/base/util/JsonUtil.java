package base.util;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import base.common.AppLogger;

public class JsonUtil {

	public static JsonObject parseFromString(String jsonString) {
		JsonObject retJson = null;
		try {
			JsonReader reader = Json.createReader(new StringReader(jsonString));
			retJson = reader.readObject();
			reader.close();
		} catch (Exception e) {
			AppLogger.logTracking.error("ParseData|Content:" + jsonString, e);
		}
		return retJson;
	}

}
