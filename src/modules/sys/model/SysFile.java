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

public class SysFile extends VModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3792538917978302942L;

	@Override
	public String getName() {
		return "Sys.File";
	}

	@Override
	public Map<String, VField> getFields() {
		Map<String, VField> fields = new HashMap<>();
		fields.put("name", VField.string("File Name", options("nullable", false)));
		fields.put("type", VField.selection("Store type", selections("db", "Store in Database", "ftp", "Store in FTP server"), options()));
		fields.put("fileString", VField.file("File String", options()));
		return fields;
	}

	@Override
	public Map<String, Object> getDefaults() {
		Map<String, Object> fields = new HashMap<>();
		fields.put("type", "db");
		return fields;
	}
}
