/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 15, 2016
* Author: tuanpa
*
*/
package base.model;

import javax.persistence.Lob;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;

public class ImageField extends VField {

	@Override
	public CtField getCtField(CtClass newHibernateModel, ClassPool pool, String fieldName) 
			throws NotFoundException, CannotCompileException {
		byte[] bytes = new byte[0];
		CtClass dataType = pool.get(bytes.getClass().getName());
		CtField field = new CtField(dataType, fieldName, newHibernateModel);
		ClassFile cfile = newHibernateModel.getClassFile();
        ConstPool cpool = cfile.getConstPool();
		AnnotationsAttribute attr = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
		Annotation annot = new Annotation(Lob.class.getName(), cpool);
		attr.addAnnotation(annot);
        field.getFieldInfo().addAttribute(attr);
		return field;
	}
}
