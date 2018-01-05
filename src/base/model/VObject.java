/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Jul 19, 2016
* Author: tuanpa
*
*/

package base.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.commons.lang.ClassUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import base.controller.VController;
import base.controller.VEnv;
import base.exception.NoSameTypeException;
import base.util.HibernateUtil;
import base.util.StringConvertUtil;
import base.util.VClassLoader;

/**
 * 
 * Base Object for all model
 *
 */
public class VObject implements Serializable {

	private static final long serialVersionUID = -5556954078222115952L;

	public int save() throws Exception {
		// Before save
		if (this.getIsActive() == null)
			this.setIsActive(true);

		// Save
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = null;

		Transaction tx = null;
		try {
			session = sf.openSession();
			tx = session.beginTransaction();
			session.saveOrUpdate(this);
			tx.commit();
			// Update reference obj
			for (Field field : this.getClass().getDeclaredFields()) {
				if (field.isAnnotationPresent(ManyToOne.class)) {
					field.setAccessible(true);
					Class<?> refClass = field.getType();
					VEnv.sudo().get(VClassLoader.getModelName(refClass)).refresh();
//					VObject obj = (VObject) field.get(this);
//					if (obj != null && obj.getId() != null) {
//						VEnv.sudo().get(VClassLoader.getModelName(obj.getClass())).removeCache(obj.getId());
//					}
//				} else if (field.isAnnotationPresent(ManyToMany.class) || field.isAnnotationPresent(OneToMany.class)) {
				} else if (field.isAnnotationPresent(ManyToMany.class)) {
					VEnv.sudo().get(VClassLoader.getModelName(field.getAnnotation(ManyToMany.class).targetEntity())).refresh();
//					@SuppressWarnings("unchecked")
//					Collection<VObject> objs = (Collection<VObject>) field.get(this);
//					if (objs != null) {
//						for (VObject obj : objs) {
//							if (obj.getId() != null) {
//								VEnv.sudo().get(VClassLoader.getModelName(obj.getClass())).removeCache(obj.getId());
//							}
//							if (field.isAnnotationPresent(OneToMany.class)) {
//								OneToMany o2mAnno = field.getAnnotation(OneToMany.class);
//								obj.setValue(o2mAnno.mappedBy(), this);
//								obj.save();
//							}
//						}
//					}
				}
				else if (field.isAnnotationPresent(OneToMany.class)) {
					VEnv.sudo().get(VClassLoader.getModelName(field.getAnnotation(OneToMany.class).targetEntity())).refresh();
					try {
						String linkField = field.getAnnotation(OneToMany.class).mappedBy();
						field.setAccessible(true);
						@SuppressWarnings("unchecked")
						List<VObject> childs = (List<VObject>) field.get(this);
						for (VObject child : childs) {
							child.setValue(linkField, this);
							child.save();
						}
					}
					catch (Exception e) {
						//ignore
					}
				}
			}
		}
		catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
		}
		finally {
			if (session != null) {
				session.close();
			}
		}
		return (int) this.getValue("id");
	}

	public boolean delete() throws Exception {
		boolean retVal = true;
		// Update reference obj
//		for (Field field : this.getClass().getDeclaredFields()) {
//			if (field.isAnnotationPresent(ManyToOne.class)) {
//				field.setAccessible(true);
//				VObject obj = (VObject) field.get(this);
//				if (obj != null && obj.getId() != null) {
//					VEnv.sudo().get(VClassLoader.getModelName(obj.getClass())).removeCache(obj.getId());
//				}
//			} else if (field.isAnnotationPresent(ManyToMany.class) || field.isAnnotationPresent(OneToMany.class)) {
//				field.setAccessible(true);
//				@SuppressWarnings("unchecked")
//				Collection<VObject> objs = (Collection<VObject>) field.get(this);
//				if (objs != null) {
//					for (VObject obj : objs) {
//						if (obj != null && obj.getId() != null) {
//							VEnv.sudo().get(VClassLoader.getModelName(obj.getClass())).removeCache(obj.getId());
//						}
//					}
//				}
//			}
//		}
		for (Field field : this.getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(ManyToOne.class)) {
				field.setAccessible(true);
				Class<?> refClass = field.getType();
				VEnv.sudo().get(VClassLoader.getModelName(refClass)).refresh();
			} else if (field.isAnnotationPresent(ManyToMany.class)) {
				VEnv.sudo().get(VClassLoader.getModelName(field.getAnnotation(ManyToMany.class).targetEntity())).refresh();
			}
			else if (field.isAnnotationPresent(OneToMany.class)) {
				VEnv.sudo().get(VClassLoader.getModelName(field.getAnnotation(OneToMany.class).targetEntity())).refresh();
			}
		}
		
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = null;
		Transaction tx = null;
		try {
			session = sf.openSession();
			tx = session.beginTransaction();
			session.delete(this);
			tx.commit();
		}
		catch (Exception e) {
			retVal = false;
			if (tx != null)
				tx.rollback();
		}
		finally {
			if (session != null)
			{
				session.close();
			}
		}
		return retVal;
	}
	
	public void removeCacheReference() throws IllegalArgumentException, IllegalAccessException{
//		for (Field field : this.getClass().getDeclaredFields()) {
//			if (field.isAnnotationPresent(ManyToOne.class)) {
//				field.setAccessible(true);
//				VObject obj = (VObject) field.get(this);
//				if (obj != null) {
//					VEnv.sudo().get(VClassLoader.getModelName(obj.getClass())).removeCache(obj.getId());
//				}
//			} else if (field.isAnnotationPresent(ManyToMany.class) || field.isAnnotationPresent(OneToMany.class)) {
//				field.setAccessible(true);
//				@SuppressWarnings("unchecked")
//				Collection<VObject> objs = (Collection<VObject>) field.get(this);
//				if (objs != null) {
//					for (VObject obj : objs) {
//						if (obj.getId() != null) {
//							VEnv.sudo().get(VClassLoader.getModelName(obj.getClass())).removeCache(obj.getId());
//						}
//					}
//				}
//			}
		for (Field field : this.getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(ManyToOne.class)) {
				field.setAccessible(true);
				Class<?> refClass = field.getType();
				VEnv.sudo().get(VClassLoader.getModelName(refClass)).refresh();
			} else if (field.isAnnotationPresent(ManyToMany.class)) {
				VEnv.sudo().get(VClassLoader.getModelName(field.getAnnotation(ManyToMany.class).targetEntity())).refresh();
			}
			else if (field.isAnnotationPresent(OneToMany.class)) {
				VEnv.sudo().get(VClassLoader.getModelName(field.getAnnotation(OneToMany.class).targetEntity())).refresh();
			}
		}
	}

	/**
	 * 
	 * @author VuD
	 * @param field
	 * @param value
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws NoSameTypeException
	 *             <i>Gia tri truyen vao khong dung kieu</i>
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public void setValue(String fieldName, Object value) throws Exception {
		Class<?> clazz = this.getClass();
		Field field = clazz.getDeclaredField(fieldName);
		if (value == null) {
			field.setAccessible(true);
			field.set(this, value);
			return;
		}
		Class<?>[] parameterTypes = new Class<?>[1];
		if (value instanceof Date) {
			value = new Timestamp(((Date) value).getTime());
		}
		parameterTypes[0] = value.getClass();
		parameterTypes = ClassUtils.primitivesToWrappers(parameterTypes);
		if (field.getType().isAssignableFrom(parameterTypes[0])) {
			if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
				field.setAccessible(true);
				field.set(this, value);
			}
		} else {
			throw new NoSameTypeException("[" + fieldName + "] - Value: " + value + " - Type: " 
							+ parameterTypes[0] + " - Expected Type: " + field.getType());
		}
	}

	/**
	 * 
	 * @author VuD
	 * @param field
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public Object getValue(String fieldName) {
		Object result = null;
		Field field = null;
		try {
			field = this.getClass().getDeclaredField(fieldName);
		} catch (Exception e) {
			field = null;
		}
		if (field == null)
			return null;
		field.setAccessible(true);

		try {
			result = field.get(this);
		} catch (Exception e) {
			result = null;
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass().equals(this.getClass())) {
			if (((VObject) obj).getValue("id") == null || this.getValue("id") == null)
				return obj == this;
			if (((VObject) obj).getValue("id").equals(this.getValue("id"))) {
				return true;
			}
		}
		return false;
	}
	
	

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {

		String retVal = null;
		try {
			Object name = this.getValue("name");
			if(name == null)
				name = this.getValue("documentno");
			retVal = name.toString();
		} catch (Exception e) {
			retVal = "[" + this.getId() != null ? this.getId().toString() : "new record" + "]";
		}
		return retVal;

	}

	public Integer getId() {
		return (Integer) getValue("id");
	}

	public Boolean getIsActive() {
		return (Boolean) getValue("isActive");
	}

	public void setIsActive(Boolean isActive) {
		try {
			this.setValue("isActive", isActive);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getUid() {
		return (String) this.getValue("uuid");
	}

	public void setUid(String uuid) {
		try {
			this.setValue("uuid", uuid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public JsonObject toJsonObject() {
		JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
		VController controller = VEnv.sudo().get(VClassLoader.getModelName(this.getClass()));
		Map<String, VField> allFields = controller.getAllFields();
		for (String fieldName : allFields.keySet()) {
			VField field = allFields.get(fieldName);
			Object fieldVal = this.getValue(fieldName);
			if (fieldVal != null) {
				if (StringConvertUtil.toString(field, fieldVal) != null)
					jsonObjBuilder.add(fieldName, StringConvertUtil.toString(field, fieldVal));
			}
		}
		return jsonObjBuilder.build();
	}
}
