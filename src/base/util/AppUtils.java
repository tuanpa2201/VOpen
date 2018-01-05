package base.util;

import static java.lang.Math.PI;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import base.model.VObject;

/**
 * 
 * @author VuD
 * 
 */

public class AppUtils {

	/**
	 * Ban kinh trai dat; R = 6372800 m;
	 */
	public static final double R = 6372.8 * 1000;

	/**
	 * He do chuyen doi tu do sang radian
	 */
	public static final double deg2Rad = Math.PI / 180d;

	/**
	 * He so chuyen doi tu radian sang do
	 */
	public static final double rad2Deg = 180d / Math.PI;

	/**
	 * Tinh khoang cach hai diem tren he toa do cau
	 *
	 * @author VuD
	 * @param lon1
	 * @param lat1
	 * @param lon2
	 * @param lat2
	 * @return
	 */
	public static double getDistance(double lon1, double lat1, double lon2, double lat2) {
		double cosd = sin(lat1 * PI / (double) 180) * sin(lat2 * PI / (double) 180) + cos(lat1 * PI / (double) 180)
				* cos(lat2 * PI / (double) 180) * cos((lon1 - lon2) * PI / (double) 180);
		if (cosd >= 1) {
			return 0.0;
		}
		double d = acos(cosd);
		double distance = d * R;
		return distance;
	}

	/**
	 * Tinh goc bearing cua hai diem. Tinh theo do chinh xac tuyet doi voi he
	 * toa do cau
	 *
	 * @author VuD
	 * @param latitude1
	 * @param longitude1
	 * @param latitude2
	 * @param longitude2
	 * @return Goc bearing cua hai diem
	 */
	public static double getBearing2Point(double latitude1, double longitude1, double latitude2, double longitude2) {
		double lat1 = latitude1 * deg2Rad;
		double lon1 = longitude1 * deg2Rad;
		double lat2 = latitude2 * deg2Rad;
		double lon2 = longitude2 * deg2Rad;
		double deltaLon = lon2 - lon1;
		double x = Math.cos(lat2) * Math.sin(deltaLon);
		double y = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(deltaLon);
		double b = Math.atan2(x, y);
		return ((b * rad2Deg) + 360) % 360;
	}

	/**
	 * 111320 va 110540 La hai hang so do su khac biet ve do det cua trai dat.
	 * Cu the la chu vi duong xich dao va chu vi di qua hai cuc trai dat khac
	 * nhau Nguon tham khao: <a href=
	 * "http://stackoverflow.com/questions/2187657/calculate-second-point-knowing-the-starting-point-and-distance">
	 * Nguon tham khao</a>
	 * 
	 * @param lat
	 * @param lon
	 * @param distance
	 * @return Toa do lat,lon cua hai dinh cua hinh vuong lan luot la diem duoi
	 *         goc phai va diem tren goc trai
	 */
	public static double[] getTwoPointRegtangle(double lat, double lon, double distance) {
		double duongcheo = distance / Math.cos(Math.PI / 4.0);
		double dx1 = duongcheo * Math.cos(315.0 * Math.PI / 180.0);
		double dy1 = duongcheo * Math.sin(315.0 * Math.PI / 180.0);
		double deltaLon1 = dx1 / (double) 111320;
		double deltaLat1 = dy1 / (double) 110540;
		double rightDownLon = lon + deltaLon1;
		double rightDownLat = lat + deltaLat1;
		double dx2 = duongcheo * Math.cos(135.0 * Math.PI / 180.0);
		double dy2 = duongcheo * Math.sin(135.0 * Math.PI / 180.0);
		double deltaLon2 = dx2 / (double) 111320;
		double deltaLat2 = dy2 / (double) 110540;
		double leftTopLon = lon + deltaLon2;
		double leftTopLat = lat + deltaLat2;
		return new double[] { rightDownLat, rightDownLon, leftTopLat, leftTopLon };
	}

	/**
	 * Lay da duong dan cua source
	 * 
	 * @author VuD
	 * @return sourcePath
	 */
	public static String getSourcePath() {
		String filePath = "";
		try {
			filePath = AppUtils.class.getClassLoader().getResource("").toURI().getPath();
			filePath = URLDecoder.decode(filePath, "utf-8");
			filePath = filePath.replace("\\", "/");
			filePath = filePath.replace("file:/", "");
		} catch (URISyntaxException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return filePath;
	}

	public static List<VObject> createModelXml(Class<? extends VObject> clazz, File xmlFile) {
		List<VObject> result = new ArrayList<>();
		try {
			SAXBuilder saxBuilder = new SAXBuilder();
			Document document = saxBuilder.build(xmlFile);
			Element rootElement = document.getRootElement();
			List<Element> lstCategory = rootElement.getChildren();
			for (Element element : lstCategory) {
				Attribute modelAtt = element.getAttribute("model");
				Class<?> clazzModel = Class.forName(modelAtt.getValue());
				VObject record = (VObject) clazzModel.newInstance();
				Attribute uidAtt = element.getAttribute("uuid");
				if (uidAtt != null) {
					try {
						Field field = clazzModel.getDeclaredField("uuid");
						AppUtils.setValue(record, field, uidAtt.getValue());
					} catch (Exception e) {
					}
				}
				List<Element> lstChild = element.getChildren();
				for (Element fieldElement : lstChild) {
					Attribute attName = fieldElement.getAttribute("name");
					try {
						Field field = clazzModel.getDeclaredField(attName.getValue());
						String value = fieldElement.getValue();
						AppUtils.setValue(record, field, value);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				result.add(record);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = null;
		}
		return result;
	}

	private static boolean setValue(Object obj, Field field, String value) {
		boolean result = false;
		field.setAccessible(true);
		try {
			if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
				Class<?> fieldType = field.getType();
				if (boolean.class.isAssignableFrom(fieldType)) {
					Boolean valueTmp = BooleanAppUtils.string2Boolean(value);
					if (valueTmp == null) {
						valueTmp = false;
					}
					field.set(obj, valueTmp);
				} else if (Boolean.class.isAssignableFrom(fieldType)) {
					Boolean valueTmp = BooleanAppUtils.string2Boolean(value);
					field.set(obj, valueTmp);
				} else if (char.class.isAssignableFrom(fieldType)) {
					Character valueTmp = CharacterAppUtils.string2Character(value);
					if (valueTmp == null) {
						valueTmp = 0;
					}
					field.set(obj, valueTmp);
				} else if (Character.class.isAssignableFrom(fieldType)) {
					Character valueTmp = CharacterAppUtils.string2Character(value);
					field.set(obj, valueTmp);
				} else if (byte.class.isAssignableFrom(fieldType)) {
					Byte valueTmp = ByteAppUtils.string2Byte(value);
					if (valueTmp == null) {
						valueTmp = 0;
					}
					field.set(obj, valueTmp);
				} else if (Byte.class.isAssignableFrom(fieldType)) {
					Byte valueTmp = ByteAppUtils.string2Byte(value);
					field.set(obj, valueTmp);
				} else if (short.class.isAssignableFrom(fieldType)) {
					Short valueTmp = ShortAppUtils.string2Short(value);
					if (valueTmp == null) {
						valueTmp = 0;
					}
					field.set(obj, valueTmp);
				} else if (Short.class.isAssignableFrom(fieldType)) {
					Short valueTmp = ShortAppUtils.string2Short(value);
					field.set(obj, valueTmp);
				} else if (int.class.isAssignableFrom(fieldType)) {
					Integer valueTmp = IntegerAppUtils.string2Integer(value);
					if (valueTmp == null) {
						valueTmp = 0;
					}
					field.set(obj, valueTmp);
				} else if (Integer.class.isAssignableFrom(fieldType)) {
					Integer valueTmp = IntegerAppUtils.string2Integer(value);
					field.set(obj, valueTmp);
				} else if (long.class.isAssignableFrom(fieldType)) {
					Long valueTmp = LongAppUtils.string2Long(value);
					if (valueTmp == null) {
						valueTmp = 0l;
					}
					field.set(obj, valueTmp);
				} else if (Long.class.isAssignableFrom(fieldType)) {
					Long valueTmp = LongAppUtils.string2Long(value);
					field.set(obj, valueTmp);
				} else if (float.class.isAssignableFrom(fieldType)) {
					Float valueTmp = FloatAppUtils.string2Float(value);
					if (valueTmp == null) {
						valueTmp = 0.0f;
					}
					field.set(obj, valueTmp);
				} else if (Float.class.isAssignableFrom(fieldType)) {
					Float valueTmp = FloatAppUtils.string2Float(value);
					field.set(obj, valueTmp);
				} else if (double.class.isAssignableFrom(fieldType)) {
					Double valueTmp = DoubleAppUtils.string2Double(value);
					if (valueTmp == null) {
						valueTmp = 0d;
					}
					field.set(obj, valueTmp);
				} else if (Double.class.isAssignableFrom(fieldType)) {
					Double valueTmp = DoubleAppUtils.string2Double(value);
					field.set(obj, valueTmp);
				} else if (BigDecimal.class.isAssignableFrom(fieldType)) {
					BigDecimal valueTmp = BigdecimalAppUtils.string2Bigdecimal(value);
					field.set(obj, valueTmp);
				} else if (String.class.isAssignableFrom(fieldType)) {
					field.set(obj, value);
				} else if (Array.class.isAssignableFrom(fieldType)) {

				} else if (Date.class.isAssignableFrom(fieldType)) {
					Date valueTmp = DateAppUtils.string2Date(value);
					field.set(obj, valueTmp);
				} else if (Timestamp.class.isAssignableFrom(fieldType)) {
					Timestamp valueTmp = TimeStampAppUtils.string2Timestamp(value);
					field.set(obj, valueTmp);
				} else if (Collection.class.isAssignableFrom(fieldType)) {

				}
			}
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
		return result;
	}

	public static void main(String[] args) {
		try {
			String filePath = AppUtils.class.getClassLoader().getResource("").toURI().toString();
			filePath = URLDecoder.decode(filePath, "utf-8");
			filePath = filePath.replace("\\", "/");
			filePath = filePath.replace("file:/", "");
			filePath += "com/vietek/vopen/sys/data/sys_data.xml";
			File file = new File(filePath);
			List<VObject> lst = createModelXml(null, file);
			for (VObject vObject : lst) {
				System.out.println(vObject.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class Demo {
	private int value;

	public void print() {
		System.out.println("Ok|" + value);
	}
}
