/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 29, 2016
* Author: tuanpa
*
*/
package modules.sys.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import base.controller.VActionResponse;
import base.controller.VController;
import base.controller.VEnv;
import base.model.VField;
import base.model.VModel;
import base.model.VObject;
import base.util.Address;
import base.util.Filter;

public class SysAddress extends VModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3699288446532707480L;

	@Override
	public String getName() {
		return "Sys.Address";
	}
	
	@Override
	public Map<String, VField> getFields() {
		Map<String, VField> fields = new HashMap<>();
		fields.put("name", VField.string("Name", options("nullable", false)));
		fields.put("latitude", VField.decimal("Latitude", options("scale", 10, "precision", 19)));
		fields.put("longitude", VField.decimal("Longitude", options("scale", 10, "precision", 19)));
		return fields;
	}
	
	public VObject getAddress(Address address) {
		VObject addressObj = null;
		if (address != null) {
			BigDecimal lat = BigDecimal.valueOf((address.getLatitude()));
//			lat = lat.setScale(10, BigDecimal.ROUND_HALF_UP);
			BigDecimal lng = BigDecimal.valueOf((address.getLongitude()));
//			lng = lng.setScale(10, BigDecimal.ROUND_HALF_UP);
			VController addController = VEnv.sudo().get("Sys.Address");
			Filter filter = new Filter();
			filter.hqlWhereClause = "name = :name and latitude=:latitude and longitude=:longitude";
			filter.params.put("name", address.getName());
			filter.params.put("latitude", lat);
			filter.params.put("longitude", lng);
			List<Integer> addressIds = addController.search(filter);
			if (addressIds.size() > 0) {
				addressObj = addController.browse(addressIds.get(0));
			}
			else {
				Map<String, Object> values = new HashMap<>();
				values.put("name", address.getName());
				values.put("latitude", lat);
				values.put("longitude", lng);
				VActionResponse response = addController.create(values);
				addressObj = response.obj;
			}
		} else {
			addressObj = null;
		}
		return addressObj;
	}
}
