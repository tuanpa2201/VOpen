/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 18, 2016
* Author: tuanpa
*
*/
package modules.student.model;

import java.util.HashMap;
import java.util.Map;

import base.model.VModel;
import base.model.VField;

public class Subject extends VModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3301580670609192418L;

	@Override
	public String getName() {
		return "Student.Subject";
	}

	@Override
	public Map<String, VField> getFields() {
		HashMap<String, VField> fields = new HashMap<>();
		fields.put("name", VField.string("Subject Name", options()));
		fields.put("students", VField.many2many("Student", "Student.Student", "student_student_subject", "subject_id", "student_id", options()));
		return fields;
	}
}
