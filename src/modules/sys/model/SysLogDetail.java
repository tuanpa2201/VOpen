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

public class SysLogDetail extends VModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3699288446532707480L;

	@Override
	public String getName() {
		return "Sys.Log.Detail";
	}
	
	@Override
	public Map<String, VField> getFields() {
		Map<String, VField> fields = new HashMap<>();
		fields.put("log", VField.many2one("Log", "Sys.Log", options()));
		fields.put("fieldName", VField.string("Field Name", options()));
		fields.put("old_value", VField.string("Old Value", options()));
		fields.put("new_value", VField.string("New Value", options()));
		return fields;
	}
}
