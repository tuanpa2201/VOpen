/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 13, 2016
* Author: tuanpa
*
*/
package base.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;

import base.exception.VException;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;

public abstract class VField {
	public String moduleId;
	public String label;
	public Class<?> valueType;
	public String help;
	public String placeholder;
	public String viewId;
	public boolean nullable = true;
	public boolean unique = false;
	public boolean isStore = true;
	public boolean isReadOnly = false;
	public boolean searchable = false;
	public boolean company_dependent = false;
	public boolean isLog = false;
	public String editorClass = null;
	public boolean isAllowedAddRecord = true;
	public String groups;

	public CtField getCtField(CtClass newHibernateModel, ClassPool pool, String fieldName)
			throws NotFoundException, CannotCompileException {
		CtClass dataType = pool.get(this.valueType.getName());
		CtField field = new CtField(dataType, fieldName, newHibernateModel);
		return field;
	}

	public void parseOptions(Map<String, Object> options) {
		if (options == null)
			return;
		if (options.get("help") != null) {
			this.help = (String) options.get("help");
		}
		if (options.get("isLog") != null) {
			this.isLog = (Boolean) options.get("isLog");
		}
		if (options.get("placeholder") != null) {
			this.placeholder = (String) options.get("placeholder");
		}
		if (options.get("nullable") != null) {
			this.nullable = (Boolean) options.get("nullable");
		}
		if (options.get("readonly") != null) {
			this.isReadOnly = (Boolean) options.get("readonly");
		}
		if (options.get("searchable") != null) {
			this.searchable = (Boolean) options.get("searchable");
		}
		if (options.get("unique") != null && options.get("unique") instanceof Boolean) {
			this.unique = (Boolean) options.get("unique");
		}
		if (options.get("company_dependent") != null && options.get("company_dependent") instanceof Boolean) {
			this.company_dependent = (boolean) options.get("company_dependent");
		}
		if (options.get("groups") != null) {
			this.groups = (String) options.get("groups");
		}

	}

	public static VField string(String label, Map<String, Object> options) {
		StringField field = new StringField();
		field.valueType = String.class;
		field.label = label;
		field.parseOptions(options);
		return field;
	}

	public static VField integer(String label, Map<String, Object> options) {
		IntegerField field = new IntegerField();
		field.valueType = Integer.class;
		field.label = label;
		field.parseOptions(options);
		return field;
	}

	public static VField decimal(String label, Map<String, Object> options) {
		DecimalField field = new DecimalField();
		field.valueType = BigDecimal.class;
		field.label = label;
		field.parseOptions(options);
		return field;
	}

	public static VField date(String label, Map<String, Object> options) {
		DateField field = new DateField();
		field.valueType = Timestamp.class;
		field.label = label;
		field.parseOptions(options);
		return field;
	}

	public static VField datetime(String label, Map<String, Object> options) {
		DateTimeField field = new DateTimeField();
		field.valueType = Timestamp.class;
		field.label = label;
		field.parseOptions(options);
		return field;
	}

	public static VField time(String label, Map<String, Object> options) {
		TimeField field = new TimeField();
		field.valueType = Timestamp.class;
		field.label = label;
		field.parseOptions(options);
		return field;
	}

	public static VField yesno(String label, Map<String, Object> options) {
		YesNoField field = new YesNoField();
		field.valueType = Boolean.class;
		field.label = label;
		field.parseOptions(options);
		return field;
	}

	public static VField selection(String label, Map<String, String> values, Map<String, Object> options) {
		SelectionField field = new SelectionField(values);
		field.valueType = String.class;
		field.label = label;
		field.parseOptions(options);
		return field;
	}

	public static VField many2one(String label, String parentModel, Map<String, Object> options) {
		Many2OneField field = null;
		try {
			field = new Many2OneField();
			field.parentModel = parentModel;
			field.label = label;
			field.parseOptions(options);
			return field;
		} catch (Exception e) {
			field = null;
		}
		return field;
	}

	public static VField one2many(String label, String childModel, String joinField, Map<String, Object> options) {
		One2ManyField field = null;
		try {
			field = new One2ManyField();
			field.valueType = Collection.class;
			field.childModel = childModel;
			field.label = label;
			field.joinField = joinField;
			field.parseOptions(options);
			return field;
		} catch (Exception e) {
			field = null;
		}
		return field;
	}

	public static VField many2many(String label, String friendModel, String mappedTable, String joinField,
			String friendField, Map<String, Object> options) {
		Many2ManyField field = null;
		try {
			field = new Many2ManyField();
			field.valueType = Collection.class;
			field.friendModel = friendModel;
			field.joinTable = mappedTable;
			field.label = label;
			field.joinField = joinField;
			field.friendField = friendField;
			field.parseOptions(options);
			return field;
		} catch (Exception e) {
			field = null;
		}
		return field;
	}

	public static VField function(String label, String functionName, Class<?> valueType, Map<String, Object> options) {
		FunctionField field = new FunctionField();
		field.functionName = functionName;
		field.valueType = valueType;
		field.label = label;
		field.isReadOnly = true;
		field.parseOptions(options);
		return field;
	}

	public static VField button(String label, String functionName, Map<String, Object> options) {
		ButtonField field = new ButtonField();
		field.functionName = functionName;
		field.label = label;
		field.isReadOnly = true;
		field.parseOptions(options);
		return field;
	}

	public static VField file(String label, Map<String, Object> options) {
		FileField field = new FileField();
		field.valueType = String.class;
		field.label = label;
		field.parseOptions(options);
		return field;
	}

	public static VField image(String label, Map<String, Object> options) {
		ImageField field = new ImageField();
		field.label = label;
		field.parseOptions(options);
		return field;
	}

	public boolean validationField(Object value) throws Exception {
		if (!nullable && value == null) {
			throw new Exception(label + VException.NULL_EXCEPTION);
		}
		return true;
	}

	public static VField text(String label, Map<String, Object> options) {
		TextField field = new TextField();
		field.label = label;
		field.parseOptions(options);
		return field;
	}

	public static VField address(String label, String parentModel, Map<String, Object> options) {
		AddressField field = null;
		try {
			field = new AddressField();
			field.parentModel = parentModel;
			field.label = label;
			field.parseOptions(options);
			return field;
		} catch (Exception e) {
			field = null;
		}
		return field;
	}

	public boolean isEnforce() {
		return !nullable;
	}
}
