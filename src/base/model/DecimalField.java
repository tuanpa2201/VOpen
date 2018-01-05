/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 18, 2016
* Author: tuanpa
*
*/
package base.model;

import java.math.BigDecimal;
import java.util.Map;

import javax.persistence.Column;

import base.exception.VException;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.IntegerMemberValue;

public class DecimalField extends VField {
	Integer precision;
	Integer scale;
	BigDecimal minvalue;
	BigDecimal maxvalue;

	public DecimalField() {
	}

	@Override
	public void parseOptions(Map<String, Object> options) {
		super.parseOptions(options);
		if (options.get("minvalue") != null) {
			this.minvalue = (BigDecimal) options.get("minvalue");
		}
		if (options.get("maxvalue") != null) {
			this.maxvalue = (BigDecimal) options.get("maxvalue");
		}
		if (options.get("precision") != null) {
			this.precision = (Integer) options.get("precision");
		}
		if (options.get("scale") != null) {
			this.scale = (Integer) options.get("scale");
		}
	}

	@Override
	public boolean validationField(Object value) throws Exception {
		super.validationField(value);
		if (value != null) {
			if ((this.minvalue != null && value != null && ((BigDecimal) value).compareTo(this.minvalue) < 0)) {
				throw new Exception(label + VException.BOUND_EXCEPTION);
			}
			if ((this.maxvalue != null && value != null && ((BigDecimal) value).compareTo(this.maxvalue) > 0)) {
				throw new Exception(label + VException.BOUND_EXCEPTION);
			}
		}
		return true;
	}

	public CtField getCtField(CtClass newHibernateModel, ClassPool pool, String fieldName)
			throws NotFoundException, CannotCompileException {
		CtClass dataType = pool.get(BigDecimal.class.getName());
		CtField field = new CtField(dataType, fieldName, newHibernateModel);
		ClassFile cfile = newHibernateModel.getClassFile();
		ConstPool cpool = cfile.getConstPool();

		AnnotationsAttribute attr = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
		Annotation annot = new Annotation(Column.class.getName(), cpool);
		if (precision != null) {
			annot.addMemberValue("precision", new IntegerMemberValue(cpool, precision));
		}
		if (scale != null) {
			annot.addMemberValue("scale", new IntegerMemberValue(cpool, scale));
		}
		attr.addAnnotation(annot);
		field.getFieldInfo().addAttribute(attr);
		return field;
	}
}
