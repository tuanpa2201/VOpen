/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Jul 20, 2016
* Author: tuanpa
*
*/
package modules.student;

import java.util.ArrayList;

import base.VModuleDefine;
import modules.student.model.Student;
import modules.student.model.StudentClass;
import modules.student.model.Subject;
import modules.sys.SysModule;

public class StudentModule extends VModuleDefine {

	@Override
	public String getModuleName() {
		return "Student Management";
	}

	@Override
	public ArrayList<Class<?>> getModelClasses() {
		ArrayList<Class<?>> retVal = new ArrayList<>();
		retVal.add(Student.class);
		retVal.add(StudentClass.class);
		retVal.add(Subject.class);
		return retVal;
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public ArrayList<VModuleDefine> getDependencyModule() {
		ArrayList<VModuleDefine> retVal = new ArrayList<>();
		retVal.add(new SysModule());
		return retVal;
	}

	@Override
	public ArrayList<String> getDataFiles() {
		ArrayList<String> retVal = new ArrayList<>();
		retVal.add("modules/student/data/student_data.xml");
		retVal.add("modules/student/data/student_perm.xml");
		return retVal;
	}

	@Override
	public ArrayList<String> getViewFiles() {
		ArrayList<String> retVal = new ArrayList<>();
		retVal.add("modules/student/view/student_view.xml");
		return retVal;
	}

	@Override
	public String getModuleId() {
		return "student_manager";
	}

	@Override
	public String getWebPath() {
		return "student";
	}

	@Override
	public ArrayList<String> getResources() {
		ArrayList<String> retVal = new ArrayList<>();
		return retVal;
	}

	@Override
	public ArrayList<Class<?>> getServiceClasses() {
		return null;
	}
	
	@Override
	public ArrayList<String> getLocaleFiles() {
		ArrayList<String> retVal = new ArrayList<>();
		retVal.add("modules/student/locale/vn");
		return retVal;
	}
}
