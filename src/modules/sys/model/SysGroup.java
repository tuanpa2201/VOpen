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

public class SysGroup extends VModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5067514889590989827L;

	@Override
	public String getName() {
		return "Sys.Group";
	}
	@Override
	public Map<String, VField> getFields() {
		Map<String, VField> fields = new HashMap<>();
		fields.put("name", VField.string("Name", options("searchable", true,"nullable", false)));
		fields.put("permissions", VField.one2many("Permissions", "Sys.Permission", "group", options()));
		fields.put("users", VField.many2many("Users", "Sys.User", "sys_group_user_mapper", "sys_group_id", "sys_user_id", options()));
		fields.put("menus", VField.many2many("Menus", "Sys.Menu", "sys_group_menu_mapper", "sys_group_id", "sys_menu_id", options()));
		fields.put("rules", VField.many2many("Rules", "Sys.Rule", "sys_rule_group_mappper", "sys_rule_id", "sys_group_id", options()));
		return fields;
	}
}
