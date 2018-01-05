/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Jul 20, 2016
* Author: tuanpa
*
*/

package base.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author tuanpa
 *
 */
public class VModule extends VModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3123506575970479995L;

	@Override
	public String getName() {
		return "base.module";
	}

	@Override
	public Map<String, VField> getFields() {
		Map<String, VField> fields = new HashMap<>();
		fields.put("moduleId", VField.string("Module Identify", options("nullable", false)));
		fields.put("isInstalled", VField.yesno("Is Installed", options()));
		return fields;
	}
	
	@Override
	public Map<String, VField> getAllFields() {
		Map<String, VField> fields = getFields();
		fields.put("id", VField.integer("ID", options("readonly", true, "help", "Identify")));
		fields.put("isActive", VField.yesno("Active", options("help", "Enable/disable this record")));
		fields.put("uuid", VField.string("Unique Identify", options("readonly", true, "help", "Universal uniquid identify of this object")));
		return fields;
	}

	@Override
	public Map<String, Object> getDefaults() {
		Map<String, Object> mapDefault = new HashMap<>();
		mapDefault.put("isInstalled", false);
		return mapDefault;
	}
}
