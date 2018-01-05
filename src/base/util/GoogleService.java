package base.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import base.common.AppLogger;
import base.vmap.LatLngBounds;

public class GoogleService {
	private static final String URL = "https://maps.googleapis.com/maps/api/geocode/json";

	public static ArrayList<Address> searchPlaceGoogleMap(String input, LatLngBounds bounds) {
		ArrayList<Address> addresses = new ArrayList<>();
		try {
			Client client = Client.create();
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(URL).append("?address=");
			stringBuilder.append(URLEncoder.encode(input, "UTF-8"));
			stringBuilder.append("&components=country:vn&language=vn");
			stringBuilder.append("&key=").append(GoogleKey.getKey());

			if (bounds != null) {
				stringBuilder.append("&bounds=");
				stringBuilder.append(bounds.northEast.lat).append(",").append(bounds.northEast.lng).append("%7C")
						.append(bounds.southWest.lat).append(bounds.southWest.lng);
			}

			WebResource webResource = client.resource(stringBuilder.toString());
			client.setReadTimeout(10000);
			client.setConnectTimeout(10000);

			ClientResponse clientResponse = webResource.accept("application/json").get(ClientResponse.class);
			String output = clientResponse.getEntity(String.class);
			JsonReader reader = Json.createReader(new StringReader(output));
			JsonObject jsonObj = reader.readObject();
			if (jsonObj.getString("status").equals("OK")) {
				JsonArray addArray = jsonObj.getJsonArray("results");
				for (int i = 0; i < addArray.size(); i++) {
					JsonObject addObj = addArray.getJsonObject(i);
					Address address = new Address();
					address.setName(addObj.getString("formatted_address"));
					address.setLatitude(addObj.getJsonObject("geometry").getJsonObject("location").getJsonNumber("lat")
							.doubleValue());
					address.setLongitude(addObj.getJsonObject("geometry").getJsonObject("location").getJsonNumber("lng")
							.doubleValue());
					addresses.add(address);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return addresses;
	}

	public static String getAddressGoogle(double latitude, double longitude) {
		String address = "unknow";
		URLConnection conn = null;
		InputStream in = null;
		try {
			if (latitude == 0 || longitude == 0) {
				return address;
			}
			String latlongString = latitude + "," + longitude;
			for (int i = 0; i < GoogleKey.getSizeKey(); i++) {
				URL url = new URL(URL + "?latlng=" + latlongString + "&key=" + GoogleKey.getKey(i));
				conn = url.openConnection();
				in = conn.getInputStream();
				com.google.gson.stream.JsonReader reader = new com.google.gson.stream.JsonReader(
						new BufferedReader(new InputStreamReader(in, "UTF-8")));
				GoogleResponseAddress r = (new Gson().fromJson(reader, GoogleResponseAddress.class));
				if (r.getStatus().equals("OK"))
					if (r.getResults().length > 0) {
						address = r.getResults()[0].getFormatted_address();
						break;
					}
			}

		} catch (Exception e) {
			AppLogger.logDebug.error(
					"GoogleMapUntil|convertLatLongToAddrest|longitude:" + longitude + "|latitude:" + latitude, e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					AppLogger.logDebug.error(
							"GoogleMapUntil|convertLatLongToAddrest|longitude:" + longitude + "|latitude:" + latitude,
							e);
				}
			}
		}
		return address;
	}

	public static String getAddressImap(double latitude, double longitude) {
		String address = "unknow";
		try {
			if (latitude == 0 || longitude == 0) {
				return address;
			}
			SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
			SOAPConnection sconn = scf.createConnection();
			String url = ConfigUtil.getConfig("IMAP_SERVICE_URL", "nothing");
			SOAPMessage respond = sconn.call(createSoapRequest(latitude, longitude), url);
			SOAPBody sbody = respond.getSOAPBody();
			sconn.close();
			address = sbody.getLastChild().getLastChild().getLastChild().getTextContent();
			if (address == null || address.trim().length() <= 0) {
				return address;
			}
		} catch (Exception e) {
			if (e instanceof NullPointerException) {
				AppLogger.logDebug.error("unknow");
			} else {
				AppLogger.logDebug.error("AddressService", e);
			}
		}
		return address;
	}

	private static SOAPMessage createSoapRequest(double latitude, double longitude) throws SOAPException, IOException {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();

		String URI = new String("http://imap.com.vn/");
		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration("imap", URI);
		SOAPBody soapBody = envelope.getBody();

		SOAPElement soapBodyElem = soapBody.addChildElement("GetGeocodeInfor", "imap");
		SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("key", "imap");
		soapBodyElem1.addTextNode("WnjK32E24GChTuDxaXEStjlInrZaImdXyx2uHAhLt/k=");
		SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("geoType", "imap");
		soapBodyElem2.addTextNode("reserve_geocode");
		SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("coords", "imap");
		soapBodyElem3.addTextNode(longitude + "|" + latitude);

		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", URI + "GetGeocodeInfor");

		soapMessage.saveChanges();

		return soapMessage;
	}
}
