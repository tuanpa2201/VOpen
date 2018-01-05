package base.webservice;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import org.zkforge.json.simple.JSONObject;

import base.common.AppLogger;
import base.util.ConfigUtil;

public class WebServicesUtil {

	public static String doGet(String strUrl, Map<String, Object> params) {
		String result = "";
		InputStream inputStream = null;
		HttpURLConnection conn = null;

		try {
			URL url;
			if (params == null)
				url = new URL(strUrl);
			else
				url = new URL(strUrl + "?" + getQuery(params));
			AppLogger.logTracking.info("WEBSERVICES:-" + url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(5000);
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			conn.getResponseCode();
			inputStream = conn.getInputStream();
			result = readInputStream(inputStream);
			return result;
		} catch (Exception ex) {
			result = ex.getMessage();
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
				if (conn != null)
					conn.disconnect();
			} catch (Exception ex) {
			}
		}

		return result;
	}

	private static String getQuery(Map<String, Object> mapParams) throws UnsupportedEncodingException {
		if (mapParams == null)
			return "";
		StringBuilder result = new StringBuilder();
		boolean first = true;
		Set<Map.Entry<String, Object>> paramSet = mapParams.entrySet();
		for (Map.Entry<String, Object> param : paramSet) {
			if (first)
				first = false;
			else
				result.append("&");
			result.append(URLEncoder.encode(param.getKey(), "UTF-8"));
			result.append('=');
			result.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
		}

		return result.toString();
	}

	private static String doPost(String strUrl) {
		String result = "";
		InputStream inputStream = null;
		HttpURLConnection conn = null;

		try {
			URL url;
			url = new URL(strUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(5000);
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.connect();
			inputStream = conn.getInputStream();
			result = readInputStream(inputStream);
			return result;
		} catch (Exception ex) {
			result = ex.getMessage();
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
				if (conn != null)
					conn.disconnect();
			} catch (Exception ex) {
			}
		}

		return result;
	}

	private static String doHttpRequest(String strUrl, String method) {
		String result = "";
		InputStream inputStream = null;
		HttpURLConnection conn = null;

		try {
			URL url;
			url = new URL(strUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(5000);
			conn.setConnectTimeout(5000);
			conn.setRequestMethod(method);
			conn.setDoInput(true);
			conn.connect();
			inputStream = conn.getInputStream();
			result = readInputStream(inputStream);
			return result;
		} catch (Exception ex) {
			result = ex.getMessage();
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
				if (conn != null)
					conn.disconnect();
			} catch (Exception ex) {
			}
		}

		return result;
	}

	private static String readInputStream(InputStream stream) throws IOException {

		BufferedReader reader = null;
		StringBuffer result = new StringBuffer();
		String inputLine;
		try {
			reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
			while ((inputLine = reader.readLine()) != null) {
				result.append(inputLine);
			}
		} catch (Exception ex) {
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Exception e) {
			}
		}
		return result.toString();
	}

	@SuppressWarnings("unchecked")
	public static String doPost(String url, Map<String, Object> values) {
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			JSONObject json = new JSONObject();
			json.putAll(values);
			String postJsonData = json.toString();

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postJsonData);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String output;
			StringBuffer response = new StringBuffer();

			while ((output = in.readLine()) != null) {
				response.append(output);
			}
			in.close();
			return responseCode + "";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public static String sendReportParam(String method, Integer deviceid, String param) {
		String result = "";
		try {
			StringBuilder url = new StringBuilder();
			String urlPerfix = ConfigUtil.getConfig("ANALYSIS_SERVICES_URL");
			url.append(urlPerfix);
			url.append(method);
			url.append("device=" + deviceid);
			url.append("&vehicleparam=");
			url.append(URLEncoder.encode(param, "UTF-8"));
			result = doHttpRequest(url.toString(), "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static String doPost(Map<String, Object> map) throws UnsupportedEncodingException {
		String result = "";
		StringBuilder url = new StringBuilder("http://localhost:8081/vopen/rest/VWebService/processData/map/");
		if (map.get("params") != null) {
			JSONObject json1 = new JSONObject();
			json1.putAll((Map<String, Object>) map.get("params"));
			map.put("params", URLEncoder.encode(json1.toString(), "UTF-8"));
		}
		JSONObject json = new JSONObject();
		json.putAll(map);
		String tempParams = URLEncoder.encode(json.toString(), "UTF-8");
		url.append(tempParams);
		result = doPost(url.toString());
		return result;
	}
}
