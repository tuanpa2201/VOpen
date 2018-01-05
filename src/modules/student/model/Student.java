/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 15, 2016
* Author: tuanpa
*
*/
package modules.student.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import base.controller.VActionResponse;
import base.model.VField;
import base.model.VModel;
import base.model.VObject;

public class Student extends VModel {

	private static final long serialVersionUID = -238506028192371794L;

	@Override
	public String getName() {
		return "Student.Student";
	}
	@Override
	public String getTitle() {
		return "Student";
	}
	
	@Override
	public String getDescription() {
		return "Student management";
	}
	
	

	@Override
	public boolean isLog() {
		return true;
	}
	@Override
	public Map<String, VField> getFields() {
		Map<String, VField> fields = new HashMap<>();
		fields.put("name", VField.string("Name", options("help", "Student fullname", "nullable", false, "isLog", true)));
		fields.put("note", VField.text("Note", options()));
		fields.put("toan", VField.decimal("Math", options("scale", 10, "precision", 19)));
		fields.put("ly", VField.decimal("Physics", options()));
		fields.put("hoa", VField.decimal("Chemistry", options()));
		fields.put("tong_diem", VField.function("Total points", "tongDiem", BigDecimal.class, options()));
		fields.put("student_class", VField.many2one("Class", "Student.StudentClass", options()));
		fields.put("ngay_nhap_hoc", VField.date("Join date", options()));
		fields.put("subjects", VField.many2many("Subjects", "Student.Subject", "student_student_subject", "student_id", "subject_id", options()));
		fields.put("xep_loai", VField.selection("Rank", selections("gioi", "Best", "kha", "Good", "tb", "Normal", "yeu", "Weak"), options()));
		fields.put("button_test", VField.button("TEST", "test", options()));
		return fields;
	}
	
	public void test(VObject obj) {
		System.out.println("Button Test: " + obj);
	}
	
	public BigDecimal tongDiem(VObject obj) {
		BigDecimal toan = (BigDecimal) controller.getValue(obj.getId(), "toan");
		BigDecimal ly = (BigDecimal) controller.getValue(obj.getId(), "ly");
		BigDecimal hoa = (BigDecimal) controller.getValue(obj.getId(), "hoa");
		BigDecimal tong_diem = toan.add(ly).add(hoa);
		return tong_diem;
	}
	@Override
	public Map<String, Object> getDefaults() {
		Map<String, Object> retVal = new HashMap<>();
		retVal.put("toan", BigDecimal.ONE);
		retVal.put("ly", BigDecimal.ONE);
		retVal.put("hoa", BigDecimal.ONE);
		return retVal;
	}
	@Override
	public VActionResponse dynamicState(Map<String, Object> values) {
		VActionResponse response = super.dynamicState(values);
		Map<String, Object> currentClass = controller.getVEnv().getCurrentVObjectEditingValue("Student.StudentClass");
		return response;
	}
}
