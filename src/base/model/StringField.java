/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 15, 2016
* Author: tuanpa
*
*/
package base.model;

import java.util.Map;

import javax.persistence.Column;

import base.exception.VException;
import base.util.StringUtils;
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

public class StringField extends VField {
	public boolean password = false;
	public boolean isPhoneNumber = false;
	public boolean isEmail = false;
	public boolean isCode = false;
	public Integer maxlength = 255;

	public StringField() {
	}
	
	@Override
	public CtField getCtField(CtClass newHibernateModel, ClassPool pool, String fieldName) 
			throws NotFoundException, CannotCompileException {
		CtClass dataType = pool.get(String.class.getName());
		CtField field = new CtField(dataType, fieldName, newHibernateModel);
		ClassFile cfile = newHibernateModel.getClassFile();
        ConstPool cpool = cfile.getConstPool();
 
        AnnotationsAttribute attr =
                new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
        Annotation annot = new Annotation(Column.class.getName(), cpool);
        annot.addMemberValue("length", new IntegerMemberValue(cpool, maxlength));//new StringMemberValue("text", cpool));
        attr.addAnnotation(annot);
        field.getFieldInfo().addAttribute(attr);
		return field;
	}

	@Override
	public void parseOptions(Map<String, Object> options) {
		super.parseOptions(options);
		if (options.get("password") != null && options.get("password") instanceof Boolean) {
			this.password = (boolean) options.get("password");
		}
		if (options.get("isphonenumber") != null &&  options.get("isphonenumber") instanceof Boolean) {
			this.isPhoneNumber = (boolean)options.get("isphonenumber");
		}
		if (options.get("isemail") != null &&  options.get("isemail") instanceof Boolean) {
			this.isEmail = (boolean)options.get("isemail");
		}
		if (options.get("maxlength") != null && options.get("maxlength") instanceof Integer) {
			this.maxlength = (Integer)options.get("maxlength");
		}
		if (options.get("iscode") != null && options.get("iscode") instanceof Boolean) {
			this.isCode = (Boolean)options.get("iscode");
		}
	}
	@Override
	public boolean validationField(Object value) throws Exception {
	    super.validationField(value);
	    if (value != null) {
	    	if (!StringUtils.checkMaxLength((String)value,this.maxlength)) {
				throw new Exception(label + " vượt quá " + this.maxlength + " ký tự");
			}
			if (isPhoneNumber && !StringUtils.isValidPhoneNumber((String)value)) {
				throw new Exception(label + VException.PHONE_EXCEPTION);
			}
			if (isEmail && !StringUtils.isValidEmail((String)value)) {
				throw new Exception(label + VException.EMAIL_EXCEPTION);
			}
			if (isCode && StringUtils.isHasWhiteSpace((String)value)) {
				throw new Exception(label + VException.FORMAT_EXCEPTION +" (contains space character)");
			}
			if (StringUtils.isHasWhiteSpaceBeginEnd((String)value)) {
				throw new Exception(label + VException.FORMAT_EXCEPTION +" (contains space character begin-end)");
			}
		
		}
		return true;
	}
}
