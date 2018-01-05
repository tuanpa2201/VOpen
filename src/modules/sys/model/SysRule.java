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

public class SysRule extends VModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8913841581977991601L;

	@Override
	public String getName() {
		return "Sys.Rule";
	}
	
	@Override
	public Map<String, VField> getFields() {
		Map<String, VField> fields = new HashMap<>();
		fields.put("name", VField.string("Name", options("nullable", false)));
		fields.put("model", VField.string("Model", options("nullable", false)));
		fields.put("hql", VField.string("HQL", options("nullable", false)));
		fields.put("groups", VField.many2many("Groups", "Sys.Group", "sys_rule_group_mappper", "sys_rule_id", "sys_group_id", options()));
		fields.put("users", VField.many2many("Users", "Sys.User", "sys_rule_user_mappper", "sys_rule_id", "sys_user_id", options()));
		fields.put("priority", VField.integer("Priority", options()));
		fields.put("apply_create", VField.yesno("Apply for Create", options()));
		fields.put("apply_read", VField.yesno("Apply for Read", options()));
		fields.put("apply_update", VField.yesno("Apply for Update", options()));
		fields.put("apply_delete", VField.yesno("Apply for Delete", options()));
		return fields;
	}
}
