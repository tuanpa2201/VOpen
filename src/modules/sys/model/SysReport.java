/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 29, 2016
* Author: tuanpa
*
*/
package modules.sys.model;

import java.util.HashMap;
import java.util.Map;

import base.model.VField;
import base.model.VModel;

public class SysReport extends VModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3699288446532707480L;

	@Override
	public String getName() {
		return "Sys.Report";
	}
	
	@Override
	public Map<String, VField> getFields() {
		Map<String, VField> fields = new HashMap<>();
		fields.put("module_id", VField.string("Module", options("nullable", false, "searchable", true)));
		fields.put("name", VField.string("Name", options("nullable", false, "searchable", true)));
		fields.put("model", VField.string("Model", options()));
		fields.put("jasper_name", VField.string("Jasper Name", options()));		
		
		return fields;
	}

	@Override
	public Map<String, Object> getDefaults() {
		Map<String, Object> fields = new HashMap<>();
		return fields;
	}
}
