package base.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.hibernate.collection.internal.PersistentBag;

import base.controller.VController;
import base.controller.VEnv;
import base.model.DateField;
import base.model.DateTimeField;
import base.model.DecimalField;
import base.model.IntegerField;
import base.model.Many2ManyField;
import base.model.Many2OneField;
import base.model.One2ManyField;
import base.model.SelectionField;
import base.model.StringField;
import base.model.TimeField;
import base.model.VField;
import base.model.VObject;
import base.model.YesNoField;

public class StringConvertUtil {
	public static String toString(VField field, Object value) {
		String retVal = null;
		if (value == null) {
			retVal = "null";
		}else if (field instanceof StringField) {
			retVal = (String) value;
		}else if (field instanceof IntegerField) {
			retVal = value.toString();
		} else if (field instanceof DecimalField) {
			retVal = value.toString();
		} else if (field instanceof DateField) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			retVal = dateFormat.format(value);
		} else if (field instanceof DateTimeField) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			retVal = dateFormat.format(value);
		} else if (field instanceof TimeField) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			retVal = dateFormat.format(value);
		} else if (field instanceof YesNoField) {
			retVal = (Boolean) value ? "1" : "0";
		} else if (field instanceof SelectionField) {
			retVal = value.toString();
		} else if (field instanceof Many2OneField) {
			retVal = ((VObject) value).getId().toString();
		} else if (field instanceof One2ManyField) {
			@SuppressWarnings("unchecked")
			List<VObject> objs = (List<VObject>) value;
			retVal = "";
			for (VObject vobj : objs) {
				if (retVal.length() > 0) {
					retVal += ",";
				}
				retVal += vobj.getId();
			}
		} else if (field instanceof Many2ManyField) {
			@SuppressWarnings("unchecked")
			List<VObject> objs = (List<VObject>) value;
			retVal = "";
			for (VObject vobj : objs) {
				if (retVal.length() > 0) {
					retVal += ",";
				}
				retVal += vobj.getId();
			}
		}
		return retVal;
	}

	public static Object toObject(VField field, String value) {
		Object retVal = null;
		if (field instanceof StringField) {
			retVal = (String) value;
		} else if (field instanceof IntegerField) {
			retVal = new Integer(value);
		} else if (field instanceof DecimalField) {
			retVal = new BigDecimal(value);
		} else if (field instanceof DateField) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			Date parsedDate = null;
			try {
				parsedDate = dateFormat.parse(value);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			retVal = new Timestamp(parsedDate.getTime());
		} else if (field instanceof DateTimeField) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			Date parsedDate = null;
			try {
				parsedDate = dateFormat.parse(value);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			retVal = new Timestamp(parsedDate.getTime());
		} else if (field instanceof TimeField) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			Date parsedDate = null;
			try {
				parsedDate = dateFormat.parse(value);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			retVal = new Timestamp(parsedDate.getTime());
		} else if (field instanceof YesNoField) {
			if (value.equals("1") || value.equalsIgnoreCase("true")) {
				retVal = true;
			} else {
				retVal = false;
			}
		} else if (field instanceof SelectionField) {
			retVal = value;
		} else if (field instanceof Many2OneField) {
			Many2OneField mField = (Many2OneField) field;
			VController controller = VEnv.sudo().get(mField.parentModel);
			retVal = controller.browse(Integer.parseInt(value));
		} else if (field instanceof One2ManyField) {
			One2ManyField mField = (One2ManyField) field;
			retVal = new PersistentBag();
			String[] strIds = value.split(",");
			List<Integer> ids = new ArrayList<>();
			VController controller = VEnv.sudo().get(mField.childModel);
			for (String str : strIds) {
				ids.add(Integer.parseInt(str));
			}
			((PersistentBag) retVal).addAll(controller.browse(ids).values());
		} else if (field instanceof Many2ManyField) {
			Many2ManyField mField = (Many2ManyField) field;
			retVal = new PersistentBag();
			String[] strIds = value.split(",");
			List<Integer> ids = new ArrayList<>();
			VController controller = VEnv.sudo().get(mField.friendModel);
			for (String str : strIds) {
				ids.add(Integer.parseInt(str));
			}
			((PersistentBag) retVal).addAll(controller.browse(ids).values());
		}
		return retVal;
	}

	public static Object toObject(String type, String value) {
		Object retVal = null;
		if (type.equals("Boolean")) {
			retVal = new Boolean(value);
		} else if (type.equals("Integer")) {
			retVal = new Integer(value);
		} else if (type.equals("BigDecimal")) {
			retVal = new BigDecimal(value);
		} else if (type.equals("String")) {
			retVal = value;
		} else if (type.startsWith("VObject")) {
			String modelName = type.substring(type.indexOf("[") + 1, type.indexOf("]"));
			VController controller = VEnv.sudo().get(modelName);
			Map<String, Object> params = new HashMap<>();
			params.put("uuid", value);
			retVal = controller.browse(controller.search(new Filter("uuid=:uuid", params)).get(0));
		}
		return retVal;
	}

	public static JsonObject toJsonString(Object obj, String key) {
		JsonObject retVal = null;
		JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
		if (obj instanceof Collection<?>) {
			JsonArrayBuilder jsonArr = Json.createArrayBuilder();
			for (Object child : (Collection<?>) obj) {
				JsonObject jsonObj = toJsonString(child, null);
				jsonArr.add(jsonObj);
			}
			jsonObjBuilder.add(key == null ? "list" : key, jsonArr);
		} else {
			if (obj instanceof VObject) {
				return ((VObject) obj).toJsonObject();
			} else {
				for (Field field : obj.getClass().getDeclaredFields()) {
					field.setAccessible(true);
					if (field.getType().equals(VObject.class)) {
						try {
							VObject vobj;
							vobj = (VObject) field.get(obj);
							if (vobj != null) {
								jsonObjBuilder.add(field.getName(), vobj.toJsonObject());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {

					}
				}
			}
		}

		retVal = jsonObjBuilder.build();
		return retVal;
	}
}
