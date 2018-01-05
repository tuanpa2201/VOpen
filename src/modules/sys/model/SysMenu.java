/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 29, 2016
* Author: tuanpa
*
*/
package modules.sys.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import base.controller.VActionResponse;
import base.model.VField;
import base.model.VModel;
import base.model.VObject;

public class SysMenu extends VModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5459227466771246110L;

	@Override
	public String getName() {
		return "Sys.Menu";
	}

	@Override
	public String getOrderBy() {
		return "fullsequence";
	}

	@Override
	public Map<String, VField> getFields() {
		Map<String, VField> fields = new HashMap<>();
		fields.put("name", VField.string("Name", options("nullable", false, "searchable", true)));
		fields.put("icon", VField.string("Icon", options()));
		fields.put("fullname", VField.string("Full Name", options("nullable", false)));
		fields.put("action", VField.many2one("Action", "Sys.Action", options()));
		fields.put("parentId", VField.many2one("Parent Menu", "Sys.Menu", options("searchable", true)));
		fields.put("sequence", VField.integer("Sequence", options()));
		fields.put("fullsequence", VField.decimal("Full Sequence", options()));
		fields.put("screen", VField.selection("Screen", selections("both", "Both", "mobile", "Mobile", "pc", "PC"),
				options("help", "Type Screen", "searchable", true)));
		return fields;
	}

	@Override
	public VActionResponse create(Map<String, Object> values) {
		if (values.containsKey("name")) {
			String fullname = values.get("name").toString();
			BigDecimal fullsequence = new BigDecimal((int) values.get("sequence"));
			fullsequence = fullsequence.multiply(new BigDecimal(1000000));
			VObject parentMenu = (VObject) values.get("parentId");
			while (parentMenu != null) {
				fullname = parentMenu.getValue("name").toString() + "/" + fullname;
				BigDecimal parenSequence = new BigDecimal((int) parentMenu.getValue("sequence"));
				parenSequence = parenSequence.multiply(new BigDecimal(1000000));
				fullsequence = parenSequence.add(fullsequence.divide(BigDecimal.valueOf(1000)));
				parentMenu = (VObject) parentMenu.getValue("parentId");
			}
			values.put("fullname", fullname);
			values.put("fullsequence", fullsequence);
		}
		return super.create(values);
	}

	@Override
	public VActionResponse update(List<Integer> ids, Map<String, Object> values) {
		VActionResponse rs = new VActionResponse();
		rs.status = true;
		for (Integer id : ids) {
			VObject menu = browse(id);
			String fullname;
			BigDecimal fullsequence;
			if (values.containsKey("name")) {
				fullname = values.get("name").toString();

			} else {
				fullname = menu.getValue("name").toString();

			}

			if (values.containsKey("sequence")) {
				fullsequence = new BigDecimal((int) values.get("sequence"));
			} else {
				fullsequence = new BigDecimal((int) menu.getValue("sequence"));
			}

			fullsequence = fullsequence.multiply(new BigDecimal(1000000));
			VObject parentMenu = (VObject) menu.getValue("parentId");
			while (parentMenu != null) {
				fullname = parentMenu.getValue("name").toString() + " / " + fullname;
				BigDecimal parenSequence = new BigDecimal((int) parentMenu.getValue("sequence"));
				parenSequence = parenSequence.multiply(new BigDecimal(1000000));
				fullsequence = parenSequence.add(fullsequence.divide(BigDecimal.valueOf(1000)));
				parentMenu = (VObject) parentMenu.getValue("parentId");
			}
			values.put("fullname", fullname);
			values.put("fullsequence", fullsequence);
			List<Integer> idsTmp = new ArrayList<>();
			idsTmp.add(id);
			super.update(idsTmp, values);
		}
		return rs;
	}

}
