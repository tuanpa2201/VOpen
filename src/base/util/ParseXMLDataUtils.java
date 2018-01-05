/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 1, 2016
* Author: tuanpa
*
*/
package base.util;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import base.controller.VController;
import base.controller.VEnv;
import base.model.Many2ManyField;
import base.model.Many2OneField;
import base.model.VField;
import base.model.VObject;

public class ParseXMLDataUtils {
	
	/**
	 * Disable record from xml define file
	 * @param xmlFile
	 */
	public static void disableModelXml(File xmlFile) {
		try {
			SAXBuilder saxBuilder = new SAXBuilder();
			Document document = saxBuilder.build(xmlFile);
			Element rootElement = document.getRootElement();
			List<Element> lstCategory = rootElement.getChildren();
			for (Element element : lstCategory) {
				try {
					Attribute modelAtt = element.getAttribute("model");
					System.out.println("Model:" + modelAtt.getValue());
					if (modelAtt.getValue().equalsIgnoreCase("Sys.Group")) {
						System.out.println("");
					}
					VController controller = VEnv.sudo().get(modelAtt.getValue());
					Attribute uidAtt = element.getAttribute("uuid");
					String uuid = uidAtt.getValue();
					Map<String, Object> values = new HashMap<>();
					if (uidAtt != null) {
						values.put("uuid", uuid);
						List<Integer> ids = controller.search(new Filter("uuid = :uuid", values));
						if (ids.size() > 0) {
							values.clear();
							values.put("isActive", false);
							controller.update(ids, values);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Create record from xml define file
	 * @param xmlFile
	 * @return
	 */
	public static List<VObject> createModelXml(File xmlFile) {
		Map<String, VObject> mapUid = new HashMap<>();
		List<VObject> result = new ArrayList<>();
		try {
			SAXBuilder saxBuilder = new SAXBuilder();
			Document document = saxBuilder.build(xmlFile);
			Element rootElement = document.getRootElement();
			List<Element> lstCategory = rootElement.getChildren();
			for (Element element : lstCategory) {
				try {
					Attribute modelAtt = element.getAttribute("model");
					System.out.println("Model:" + modelAtt.getValue());
					if (modelAtt.getValue().equalsIgnoreCase("Sys.Group")) {
						System.out.println("");
					}
					VController controller = VEnv.sudo().get(modelAtt.getValue());
					Class<?> clazzModel = VClassLoader.getModelClass(modelAtt.getValue());
					VObject record = (VObject) clazzModel.newInstance();
					Attribute uidAtt = element.getAttribute("uuid");
					String uuid = uidAtt.getValue();
					Map<String, Object> values = new HashMap<>();
					values.put("isActive", true);
					if (uidAtt != null) {
						try {
							Field field = clazzModel.getDeclaredField("uuid");
							VField vfield = controller.getField("uuid");
							Object convertValue = ParseXMLDataUtils.getValue(mapUid, record, field, vfield, uuid);
							if (convertValue != null) {
								values.put("uuid", convertValue);
							}
						} catch (Exception e) {
						}
					}
					List<Element> lstChild = element.getChildren();
					
					for (Element fieldElement : lstChild) {
						Attribute attName = fieldElement.getAttribute("name");
						try {
							Field field = clazzModel.getDeclaredField(attName.getValue());
							VField vField = controller.getField(attName.getValue());
							String value = fieldElement.getValue();
							Object convertValue = ParseXMLDataUtils.getValue(mapUid, record, field, vField, value);
							if (convertValue != null) {
								values.put(field.getName(), convertValue);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					Map<String, Object> params = new HashMap<>();
					params.put("uuid", uuid);
					List<Integer> ids = controller.search(new Filter("uuid = :uuid", params));
					if (ids.size() == 0) {
						record = controller.browse(controller.create(values).id);
					}
					else {
						controller.update(ids, values);
						try {
							record = controller.browse(ids.get(0));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (record != null) {
						mapUid.put(uuid, record);
					}
					result.add(record);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = null;
		}
		return result;
	}
	
	static private Object getValue(Map<String, VObject> mapUid, Object obj, Field field, VField vfield, String value) {
		Object result = null;
		field.setAccessible(true);
		try {
			if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
				Class<?> fieldType = field.getType();
				if (boolean.class.isAssignableFrom(fieldType)) {
					Boolean valueTmp = BooleanAppUtils.string2Boolean(value);
					if (valueTmp == null) {
						valueTmp = false;
					}
					return valueTmp;
				} else if (Boolean.class.isAssignableFrom(fieldType)) {
					Boolean valueTmp = BooleanAppUtils.string2Boolean(value);
					return valueTmp;
				} else if (int.class.isAssignableFrom(fieldType)) {
					Integer valueTmp = IntegerAppUtils.string2Integer(value);
					if (valueTmp == null) {
						valueTmp = 0;
					}
					return valueTmp;
				} else if (Integer.class.isAssignableFrom(fieldType)) {
					Integer valueTmp = IntegerAppUtils.string2Integer(value);
					return valueTmp;
				} else if (BigDecimal.class.isAssignableFrom(fieldType)) {
					BigDecimal valueTmp = BigdecimalAppUtils.string2Bigdecimal(value);
					return valueTmp;
				} else if (String.class.isAssignableFrom(fieldType)) {
					return value;
				} else if (Timestamp.class.isAssignableFrom(fieldType)) {
					Timestamp valueTmp = TimeStampAppUtils.string2Timestamp(value);
					field.set(obj, valueTmp);
				} else {
					if (field.isAnnotationPresent(ManyToOne.class)) {
//						VObject recordTmp = mapUid.get(value);
						Many2OneField mField = (Many2OneField) vfield;
						VObject recordTmp = getReferenceObject(value, mField.parentModel);
						return recordTmp;
					} else if (field.isAnnotationPresent(ManyToMany.class)) {
						Set<VObject> setValue = new HashSet<>();
						String[] arr = value.split(",");
						for (String keyTmp : arr) {
//							VObject recordTmp = mapUid.get(keyTmp.trim());
							Many2ManyField mField = (Many2ManyField) vfield;
							VObject recordTmp = getReferenceObject(keyTmp, mField.friendModel);
							if (recordTmp != null) {
								setValue.add(recordTmp);
							}
						}
						return setValue;
					}
				}
			}
		} catch (Exception e) {
			result = null;
			e.printStackTrace();
		}
		return result;
	}
	private static VObject getReferenceObject(String uuid, String referenceModel) {
		VObject retVal = null;
		VController controller = VEnv.sudo().get(referenceModel);
		Map<String, Object> params = new HashMap<>();
		params.put("uuid", uuid);
		List<Integer> ids = controller.search(new Filter("uuid = :uuid", params));
		if (ids.size() > 0) {
			retVal = controller.browse(ids.get(0));
		}
		return retVal;
	}
}
