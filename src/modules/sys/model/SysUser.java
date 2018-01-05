/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 29, 2016
* Author: tuanpa
*
*/
package modules.sys.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.zul.Messagebox;

import base.controller.VActionResponse;
import base.controller.VController;
import base.controller.VEnv;
import base.model.VField;
import base.model.VModel;
import base.model.VObject;
import base.util.Filter;
import base.util.SecurityUtils;
import base.util.StringUtils;
import base.util.ZKEnv;

public class SysUser extends VModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6895138921073313926L;

	@Override
	public String getName() {
		return "Sys.User";
	}

	@Override
	public Map<String, VField> getFields() {
		Map<String, VField> fields = new HashMap<>();
		fields.put("name", VField.string("Full Name", options("nullable", false, "searchable", true, "maxlength", 30)));
		fields.put("username", VField.string("Username",
				options("searchable", true, "nullable", false, "isLog", true, "unique", true, "maxlength", 30)));
		fields.put("password", VField.string("Password", options("nullable", false, "password", true, "isLog", true, "maxlength", 50)));
		fields.put("reset_password",
				VField.button("Reset PassWord", "resetPassWord", options("help", "Reset Password to default")));
		fields.put("companies", VField.many2many("Companys", "Sys.Company", "sys_user_company_mapper", "sys_user_id",
				"sys_company_id", options("searchable", true, "nullable", false)));
		fields.put("groups", VField.many2many("Groups", "Sys.Group", "sys_group_user_mapper", "sys_user_id",
				"sys_group_id", options("searchable", true)));
		fields.put("rules", VField.many2many("Rules", "Sys.Rule", "sys_rule_user_mappper", "sys_user_id", "sys_rule_id",
				options()));
		fields.put("current_root_menu_id", VField.integer("Current Root Menu", options()));
		fields.put("avatar", VField.image("Avatar", options()));
		return fields;
	}

	public void resetPassWord(VObject obj) {
		Map<String, Object> values = new HashMap<>();
		values.put("password", SecurityUtils.encryptMd5("123456"));
		update(Arrays.asList(obj.getId()), values);
	}

	public VObject login(String username, String password) {
		VObject user = null;
		String where = "lower(username) = :username and password = :password";
		Map<String, Object> params = new HashMap<>();
		params.put("username", username.toLowerCase());
		params.put("password", SecurityUtils.encryptMd5(password));
		List<Integer> ids = search(new Filter(where, params));
		if (ids.size() == 1) {
			user = browse(ids.get(0));
		}
		return user;
	}

	@Override
	public String companyPermission() {
		String companyPermission = "";
		if (ZKEnv.getEnv() == null || ZKEnv.getEnv().isAdmin())
			return companyPermission;
		companyPermission = " and $LIST_COMPANY$ in elements(companies)";
		return companyPermission;
	}

	@Override
	public VActionResponse create(Map<String, Object> values) {
		VActionResponse vres = new VActionResponse();
		boolean isCheck = isCheckValid(vres, null, values);
		if (isCheck)
			return vres;
		return super.create(values);
	}
	
	@Override
	public VActionResponse update(List<Integer> ids, Map<String, Object> values) {
		VActionResponse vres = new VActionResponse();
		boolean isCheck = isCheckValid(vres, ids, values);
		if (isCheck)
			return vres;
		return super.update(ids, values);
	}

	@Override
	public Map<String, Object> getDefaults() {
		// TODO Auto-generated method stub
		Map<String, Object> defaults = super.getDefaults();
		Object company = defaults.get("company");
		Set<Object> comp = new HashSet<>();
		comp.add(company);
		defaults.put("companies", comp);
		return defaults;
	}

	@Override
	public VActionResponse dynamicState(Map<String, Object> values) {
		// TODO Auto-generated method stub
		VActionResponse response = super.dynamicState(values);
		Object obId = values.get("id");
		if (obId != null && (Integer) obId > 0) {
			response.readonlys.put("password", true);
			response.readonlys.put("username", true);
			response.readonlys.put("reset_password", false);
		} else {
			response.readonlys.put("reset_password", true);
		}
		return response;
	}

	@Override
	public boolean isLog() {
		return true;
	}
	
	private boolean isCheckValid(VActionResponse vres, List<Integer> ids, Map<String, Object> values) {
		if(values != null){
			String fullName = null;
			String userName = null;
			
			if(values.containsKey("name"))
				fullName = (String) values.get("name");
			if(values.containsKey("username"))
				userName = (String) values.get("username");
			
			if(fullName != null && fullName.length() > 30){
				vres.status = false;
				vres.typeresponse = "error";
				Messagebox.show("Tên vượt quá 30 ký tự!", "Thông báo lỗi", Messagebox.OK, Messagebox.ERROR);
				return true;
			}
			
			if(userName != null && userName.length() > 30){
				vres.status = false;
				vres.typeresponse = "error";
				Messagebox.show("Tên đăng nhập vượt quá 30 ký tự!", "Thông báo lỗi", Messagebox.OK, Messagebox.ERROR);
				return true;
			}
			
			String regex = "^[a-z0-9_-]{3,30}$";
			
			if(userName != null && !(StringUtils.isValidEmail(userName.trim())
					|| StringUtils.isValidPhoneNumber(userName.trim())
					|| StringUtils.checkRegexStr(userName.trim(), regex))){
				vres.status = false;
				vres.typeresponse = "error";
				Messagebox.show("Tên đăng nhập sai định dạng!", "Thông báo lỗi", Messagebox.OK, Messagebox.ERROR);
				return true;
			}
		}
		return false;
	}
	public VActionResponse delete(List<Integer> ids) {
		VController tokenController = VEnv.sudo().get("Sys.User.Token");
		Filter filter = new Filter();
		filter.hqlWhereClause = "user.id in (:user_to_delete)";
		filter.params.put("user_to_delete", ids);
		List<Integer> lstToken = tokenController.search(filter);
		if(lstToken != null && lstToken.size() > 0){
			VActionResponse vres = new VActionResponse();
			vres = tokenController.delete(lstToken);
			if(vres.status){
				return super.delete(ids);
			}
		}
		return super.delete(ids);
	}
}
