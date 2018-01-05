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

public class SysAction extends VModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3699288446532707480L;

	@Override
	public String getName() {
		return "Sys.Action";
	}
	
	@Override
	public Map<String, VField> getFields() {
		Map<String, VField> fields = new HashMap<>();
		fields.put("name", VField.string("Name", options("nullable", false, "searchable", true)));
		fields.put("window", VField.string("Window", options("searchable", true)));
		fields.put("zul", VField.string("Zul File", options("searchable", true)));
		fields.put("viewClass", VField.string("View Class", options("searchable", true)));
		fields.put("style", VField.selection("Style", selections("normal", "Normal", "popup", "Popup"), options()));
		return fields;
	}

	@Override
	public Map<String, Object> getDefaults() {
		Map<String, Object> fields = new HashMap<>();
		fields.put("style", "tab");
		return fields;
	}
}
