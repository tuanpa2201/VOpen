/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Sep 1, 2016
* Author: tuanpa
*
*/
package modules.crm.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zul.Messagebox;

import base.controller.VActionResponse;
import base.model.VField;
import base.model.VModel;
import base.util.Filter;
import base.util.StringUtils;
import base.util.Translate;
import base.util.ZKEnv;

public class Partner extends VModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9091110365737996504L;

	@Override
	public String getName() {
		return "Crm.Partner";
	}

	@Override
	public String getTitle() {
		return "Partner";
	}

	@Override
	public String getDescription() {
		return "Include all kind of partner: Customer, Supplier, Organization, individial...";
	}

	@Override
	public Map<String, VField> getFields() {
		Map<String, VField> fields = new HashMap<>();
		fields.put("company", VField.many2one("Company", "Sys.Company", options()));
		fields.put("avatar", VField.image("Avatar", options()));
		fields.put("name", VField.string("Partner Name",
				options("nullable", false, "searchable", true, "help", "Partner Full Name", "maxlength", 30)));
		fields.put("email", VField.string("Email", options("searchable", true)));
		fields.put("phone", VField.string("Số điện thoại", options("searchable", true)));
		fields.put("address", VField.string("Address", options()));
		fields.put("note", VField.text("Note", options()));
		fields.put("isOrg", VField.yesno("Is Organization", options()));
		fields.put("isCustomer", VField.yesno("Is Customer", options()));
		fields.put("isVendor", VField.yesno("Is Vendor", options()));
		Filter filter = new Filter();
		filter.hqlWhereClause = "isOrg = :is_organization";
		filter.params.put("is_organization", true);
		fields.put("organization", VField.many2one("Organization", "Crm.Partner", options("filter", filter)));
		return fields;
	}

	@Override
	public Map<String, Object> getDefaults() {

		Map<String, Object> retVal = new HashMap<>();
		retVal.put("name", null);
		retVal.put("isCustomer", true);
		return retVal;

	}

	@Override
	public VActionResponse dynamicState(Map<String, Object> values) {
		VActionResponse response = super.dynamicState(values);
		Boolean isOrg = (Boolean) values.get("isOrg");
		if (isOrg == null)
			isOrg = false;
		if (isOrg) {
			response.displays.put("organization", false);
			response.values.put("organization", null);
		} else {
			response.displays.put("organization", true);
		}
		return response;
	}

	@Override
	public Map<Integer, String> getDisplayString(List<Integer> ids) {

		Map<Integer, String> mapdisplay = new HashMap<>();
		for (Integer id : ids) {
			mapdisplay.put(id, (String) controller.getValue(id, "name"));
		}
		return mapdisplay;
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

	private boolean isCheckValid(VActionResponse vres, List<Integer> ids, Map<String, Object> values) {
		if (values != null) {
			String phone = null;
			String email = null;

			if (values.containsKey("phone"))
				phone = (String) values.get("phone");
			if (values.containsKey("email"))
				email = (String) values.get("email");
			
			if(email != null && email.length() > 0){

			if (!StringUtils.isValidEmail(email.trim())) {
				vres.status = false;
				vres.typeresponse = "error";
				Messagebox.show("Email lỗi định dạng", "Thông báo lỗi", Messagebox.OK, Messagebox.ERROR);
				return true;
			} else if (email.length() > 30) {
				vres.status = false;
				vres.typeresponse = "error";
				Messagebox.show("Email vượt quá 30 ký tự!", "Thông báo lỗi", Messagebox.OK, Messagebox.ERROR);
				return true;
			}
			}
			
			if(phone != null && phone.length() > 0){

			if (!StringUtils.isValidPhoneNumber(phone.trim())) {
				vres.status = false;
				vres.typeresponse = "error";
				Messagebox.show("Số điện thoại sai định dạng!", "Thông báo lỗi", Messagebox.OK, Messagebox.ERROR);
				return true;
			} else if (phone.length() < 10 || phone.length() > 12) {
				vres.status = false;
				vres.typeresponse = "error";
				Messagebox.show("Số điện thoại chỉ từ 10 đến 12 số", "Thông báo lỗi", Messagebox.OK, Messagebox.ERROR);
				return true;
			}
			}

			// Thêm validate cho các trường không được update trong nghiệp vụ
			// update
			if (ids != null && ids.size() > 0) {
				for (Integer id : ids) {
					String strEmail = (String) controller.getValue(id, "email");
					String strPhone = (String) controller.getValue(id, "phone");

					if (email == null) {

						if (strEmail != null && !StringUtils.isValidEmail(strEmail.trim())) {
							vres.status = false;
							vres.typeresponse = "error";
							Messagebox.show("Email lỗi định dạng", "Thông báo lỗi", Messagebox.OK, Messagebox.ERROR);
							return true;
						} else if (strEmail != null && strEmail.length() > 30) {
							vres.status = false;
							vres.typeresponse = "error";
							Messagebox.show("Email vượt quá 30 ký tự!", "Thông báo lỗi", Messagebox.OK,
									Messagebox.ERROR);
							return true;
						}
					}

					if (phone == null) {

						if (strPhone != null && !StringUtils.isValidPhoneNumber(strPhone.trim())) {
							vres.status = false;
							vres.typeresponse = "error";
							Messagebox.show("Số điện thoại sai định dạng!", "Thông báo lỗi", Messagebox.OK,
									Messagebox.ERROR);
							return true;
						} else if (strPhone != null && (strPhone.length() < 10 || strPhone.length() > 12)) {
							vres.status = false;
							vres.typeresponse = "error";
							Messagebox.show("Số điện thoại chỉ từ 10 đến 12 số", "Thông báo lỗi", Messagebox.OK,
									Messagebox.ERROR);
							return true;
						}
					}
				}
			}
		}
		return false;
	}

}
