package base.report;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.ClassUtils;

import base.exception.NoSameTypeException;

public class RowReport {

	public RowReport() {
	}

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
			throw new NoSameTypeException();
		}
	}

	public Object getValue(String fieldName) {
		Object result = null;
		Field field = null;
		try {
			field = getField(this.getClass(), fieldName);
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

	private Field getField(Class<?> clazz, String name) throws NoSuchFieldException {
		Field field = null;
		while (clazz != null && field == null) {
			try {
				field = clazz.getDeclaredField(name);
				return field;
			} catch (Exception e) {
			}
			clazz = clazz.getSuperclass();
		}
		throw new NoSuchFieldException(name);
	}

	public Object getValueFromMethod(String methodName) {
		Object retVal = null;
		Method method = null;
		try {
			method = getMethod(this.getClass(), methodName);
		} catch (Exception e) {
			method = null;
		}
		if (method == null)
			return null;
		try {
			retVal = method.invoke(this, (Object[]) null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retVal;
	}

	private Method getMethod(Class<?> clazz, String methodName) throws NoSuchFieldException {
		Method method = null;
		while (clazz != null && method == null) {
			try {
				method = clazz.getMethod(methodName, (Class<?>[]) null);
				return method;
			} catch (Exception e) {
			}
			clazz = clazz.getSuperclass();
		}
		throw new NoSuchFieldException(methodName);
	}
}
