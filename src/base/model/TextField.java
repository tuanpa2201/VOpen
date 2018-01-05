/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 15, 2016
* Author: tuanpa
*
*/
package base.model;

import org.hibernate.annotations.Type;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

public class TextField extends VField {
	public TextField() {
	}
	
	@Override
	public CtField getCtField(CtClass newHibernateModel, ClassPool pool,
			String fieldName) throws NotFoundException, CannotCompileException {
		CtClass dataType = pool.get(String.class.getName());
		CtField field = new CtField(dataType, fieldName, newHibernateModel);
		ClassFile cfile = newHibernateModel.getClassFile();
        ConstPool cpool = cfile.getConstPool();
 
        AnnotationsAttribute attr =
                new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
        Annotation annot = new Annotation(Type.class.getName(), cpool);
        annot.addMemberValue("type", new StringMemberValue("text", cpool));
        attr.addAnnotation(annot);
        field.getFieldInfo().addAttribute(attr);
		return field;
	}
}
