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

import base.model.VField;
import base.model.VModel;
import base.model.VObject;
import base.util.Filter;

public class SysCompanyDependentValue extends VModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4894094351468556756L;

	@Override
	public String getName() {
		return "Sys.Company.Dependent.Value";
	}
	@Override
	public Map<String, VField> getFields() {
		Map<String, VField> fields = new HashMap<>();
		fields.put("company", VField.many2one("Company", "Sys.Company", options()));
		fields.put("modelName", VField.string("Model Name", options("nullable", false)));
		fields.put("record_id", VField.integer("Record ID", options()));
		fields.put("fieldName", VField.string("Field Name", options()));
		fields.put("value", VField.string("Value", options()));
		return fields;
	}
	
	public void setCompanyDependentValue(VObject company, String modelName, int recordId, String fieldName, String value) {
		Filter filter = new Filter();
		filter.hqlWhereClause = "company = :company"
							  + " and modelName = :modelName"
							  + " and record_id = :record_id"
							  + " and fieldName = :fieldName";
		filter.params.put("company", company);
		filter.params.put("modelName", modelName);
		filter.params.put("record_id", recordId);
		filter.params.put("fieldName", fieldName);
		List<Integer> ids = controller.search(filter);
		Map<String, Object> values = new HashMap<>();
		values.put("value", value);
		if (ids.size() > 0) {
			controller.update(ids, values);
		}
		else {
			values.put("company", company);
			values.put("modelName", modelName);
			values.put("record_id", recordId);
			values.put("fieldName", fieldName);
			controller.create(values);
		}
	}
	
	public String getCompanyDependentValue(VObject company, String modelName, int recordId, String fieldName) {
		String retVal = null;
		Filter filter = new Filter();
		filter.hqlWhereClause = "company = :company"
							  + " and modelName = :modelName"
							  + " and record_id = :record_id"
							  + " and fieldName = :fieldName";
		filter.params.put("company", company);
		filter.params.put("modelName", modelName);
		filter.params.put("record_id", recordId);
		filter.params.put("fieldName", fieldName);
		List<Integer> ids = controller.search(filter);
		if (ids.size() > 0) {
			VObject obj = controller.browse(ids.get(0));
			retVal = (String) obj.getValue("value");
		}
		
		return retVal;
	}
}
