package modules.sys.model;

import java.util.HashMap;
import java.util.Map;

import base.model.VField;
import base.model.VModel;
import base.util.ZKEnv;

public class SysNotification extends VModel {

	private static final long serialVersionUID = 1L;
	public static final String MODEL_NAME = new String("Sys.Notification");

	@Override
	public String getName() {
		return MODEL_NAME;
	}

	@Override
	public Map<String, VField> getFields() {
		Map<String, VField> fields = new HashMap<>();
		fields.put("title", VField.string("Title", options("nullable", true)));
		fields.put("content", VField.text("Content", options("nullable", true)));
		fields.put("startDate", VField.date("StartDate", options("nullable", false)));
		fields.put("endDate", VField.date("EndDate", options()));
		fields.put("important", VField.yesno("Important", options("nullable", false)));
		fields.put("users", VField.many2many("Users", "Sys.User", "sys_notification_user", "sysnotificationid",
				"sysuserid", options()));
		fields.put("author", VField.many2one("Author", "Sys.User", options()));
		fields.put("type",
				VField.selection("Type", selections("0", "Public", "1", "Private"), options("nullable", false)));
		return fields;
	}

	@Override
	public Map<String, Object> getDefaults() {
		// TODO Auto-generated method stub
		Map<String, Object> defaults = super.getDefaults();
		defaults.put("author", controller.getVEnv().user);
		defaults.put("important", false);
		return defaults;
	}
}
