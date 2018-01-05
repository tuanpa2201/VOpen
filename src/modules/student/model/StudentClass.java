/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 15, 2016
* Author: tuanpa
*
*/
package modules.student.model;

import java.util.HashMap;
import java.util.Map;

import base.model.VModel;
import base.model.One2ManyField;
import base.model.VField;

public class StudentClass extends VModel {

	private static final long serialVersionUID = -238506028192371794L;

	@Override
	public String getName() {
		return "Student.StudentClass";
	}

	@Override
	public Map<String, VField> getFields() {
		Map<String, VField> fields = new HashMap<>();
		fields.put("name", VField.string("Name", options("nullable", false)));
		fields.put("students", VField.one2many("Students", "Student.Student", "student_class", options("ondelete", One2ManyField.ON_DELETE_SET_NULL)));
		return fields;
	}

	@Override
	public Map<String, Object> getDefaults() {
		return super.getDefaults();
	}
	
}
