package modules.sys.model;

import java.util.HashMap;
import java.util.Map;

import base.model.VField;
import base.model.VModel;

public class SysNoticeLog extends VModel {

	private static final long serialVersionUID = 1L;
	public static final String MODEL_NAME = new String("Sys.NoticeLog");

	@Override
	public String getName() {
		return MODEL_NAME;
	}

	@Override
	public Map<String, VField> getFields() {
		Map<String, VField> fields = new HashMap<>();
		fields.put("userid", VField.integer("UserId", options()));
		fields.put("noticeid", VField.integer("NoticeId", options()));
		fields.put("timelog", VField.date("Timelog", options()));
		return fields;
	}
}
