/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Sep 5, 2016
* Author: tuanpa
*
*/
package modules.sys.model;

import java.util.HashMap;
import java.util.Map;

import base.model.VField;
import base.model.VModel;

public class SysUserToken extends VModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3792538917978302942L;

	@Override
	public String getName() {
		return "Sys.User.Token";
	}

	@Override
	public Map<String, VField> getFields() {
		Map<String, VField> fields = new HashMap<>();
		fields.put("token", VField.string("Token", options("nullable", false)));
		fields.put("user", VField.many2one("User", "Sys.User", options()));
		fields.put("expired_time", VField.datetime("Expired Time", options()));
		return fields;
	}
}
