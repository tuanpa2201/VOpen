/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 29, 2016
* Author: tuanpa
*
*/
package modules.sys.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import base.controller.VActionResponse;
import base.model.VField;
import base.model.VModel;
import base.util.ZKEnv;

public class SysCompany extends VModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4894094351468556756L;
	public static final String modelName = "Sys.Company";

	@Override
	public String getName() {
		return modelName;
	}

	@Override
	public Map<String, VField> getFields() {
		Map<String, VField> fields = new HashMap<>();
		fields.put("name", VField.string("Company Name", options("searchable", true, "nullable", false)));
		fields.put("parentId", VField.many2one("Parent Company", "Sys.Company", options("searchable", true)));
		return fields;
	}

	@Override
	public Map<String, Object> getDefaults() {
		// TODO Auto-generated method stub
		Map<String, Object> defaults = super.getDefaults();
		defaults.put("name", null);
		return defaults;
	}

	@Override
	public String companyPermission() {
		String companyPermission = "";
		if (ZKEnv.getEnv() == null || controller.getVEnv().isAdmin())
			return companyPermission;
		companyPermission = " and id in ($LIST_COMPANY_ID$)";
		return companyPermission;
	}

	@Override
	public VActionResponse create(Map<String, Object> values) {

		VActionResponse response = super.create(values);
		ZKEnv.getEnv().reloadCompanyTreePermision();
		return response;
	}

	@Override
	public VActionResponse update(List<Integer> ids, Map<String, Object> values) {

		VActionResponse response = super.update(ids, values);
		ZKEnv.getEnv().reloadCompanyTreePermision();
		return response;
	}

	@Override
	public VActionResponse delete(List<Integer> ids) {
		VActionResponse retVal = super.delete(ids);
		ZKEnv.getEnv().reloadCompanyTreePermision();
		return retVal;
	}
}
