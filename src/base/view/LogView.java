package base.view;

import java.util.ArrayList;

import org.zkoss.zul.Label;
import org.zkoss.zul.Vlayout;

import base.controller.VEnv;
import base.model.VObject;

public class LogView extends Vlayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6692016715892861983L;
	private String modelName;
	private Integer recordId;
	private ArrayList<VObject> logs;
	public LogView(String modelName, Integer recordId) {
		this.modelName = modelName;
		this.recordId = recordId;
		loadData();
		initUI();
	}
	@SuppressWarnings("unchecked")
	private void loadData() {
		logs = (ArrayList<VObject>) VEnv.sudo().get("Sys.Log").execute("getLogs", modelName, recordId, 0, 5);
	}
	private void initUI() {
		this.setWidth("100%");
		this.setVflex("min");
		for (VObject log : logs) {
			String display = VEnv.sudo().get("Sys.Log").getDisplayString(log.getId());
			Label label = new Label(display);
			label.setParent(this);
		}
	}
}
