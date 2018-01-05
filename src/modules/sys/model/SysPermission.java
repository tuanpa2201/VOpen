/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Sep 8, 2016
* Author: tuanpa
*
*/
package modules.sys.model;

import java.util.HashMap;
import java.util.Map;

import base.model.VField;
import base.model.VModel;

public class SysPermission extends VModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7650293526460497795L;

	@Override
	public String getName() {
		return "Sys.Permission";
	}

	@Override
	public Map<String, VField> getFields() {
		Map<String, VField> fields = new HashMap<>();
		fields.put("model", VField.string("Model", options("nullable", false)));
		fields.put("group", VField.many2one("User group", "Sys.Group", options()));
		fields.put("readable", VField.yesno("Readable", options()));
		fields.put("creatable", VField.yesno("Creatable", options()));
		fields.put("updatable", VField.yesno("Updatable", options()));
		fields.put("deletable", VField.yesno("Deletable", options()));
		return fields;
	}
}
