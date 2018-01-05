/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 16, 2016
* Author: tuanpa
*
*/
package base.model;

import java.util.Map;

import javax.persistence.ManyToOne;

import org.hibernate.annotations.CascadeType;

import base.util.Filter;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.EnumMemberValue;

public class Many2OneField extends VField {
	public String parentModel;
	public CascadeType cascade;
	public Filter filter;
	
	public Many2OneField() {
	}
	@Override
	public CtField getCtField(CtClass newHibernateModel, ClassPool pool,
			String fieldName) throws NotFoundException, CannotCompileException {
		if (cascade == null) {
        	cascade = CascadeType.ALL;
        }
		CtClass dataType = pool.get(HolderClass.class.getName());
		CtField field = new CtField(dataType, fieldName, newHibernateModel);
		ClassFile cfile = newHibernateModel.getClassFile();
        ConstPool cpool = cfile.getConstPool();
 
        AnnotationsAttribute attr =
                new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
        Annotation annot = new Annotation(ManyToOne.class.getName(), cpool);
        attr.addAnnotation(annot);
        EnumMemberValue cascadeEnum = new EnumMemberValue(cpool);
        cascadeEnum.setType(CascadeType.class.getName());
        cascadeEnum.setValue(cascade.name());
        annot.addMemberValue("cascade", cascadeEnum);
//        
//        cascadeEnum = new EnumMemberValue(cpool);
//        cascadeEnum.setType(FetchType.class.getName());
//        cascadeEnum.setValue(FetchType.LAZY.name());
//        annot.addMemberValue("fetch", cascadeEnum);
        field.getFieldInfo().addAttribute(attr);
		return field; 
	}
	@Override
	public void parseOptions(Map<String, Object> options) {
		super.parseOptions(options);
		if (options.get("cascade") != null) {
			this.cascade = (CascadeType) options.get("cascade");
		}
		else if (options.get("filter") != null && options.get("filter") instanceof Filter) {
			this.filter = (Filter) options.get("filter");
			if (this.filter.hqlWhereClause.length() > 0) {
				this.filter.hqlWhereClause += " and";
			}
			this.filter.hqlWhereClause += " isActive = :isActive_to_check";
			this.filter.params.put("isActive_to_check", true);
		}
	}

}
