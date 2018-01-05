/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 19, 2016
* Author: tuanpa
*
*/
package base.model;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;

public class ButtonField extends VField {
	public String functionName;
	public ButtonField() {
		
	}
	@Override
	public CtField getCtField(CtClass newHibernateModel, ClassPool pool, String fieldName)
			throws NotFoundException, CannotCompileException {
		return null;
	}
}
