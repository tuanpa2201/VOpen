/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 29, 2016
* Author: tuanpa
*
*/
package modules.sys.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import base.model.VField;
import base.model.VModel;
import base.model.VObject;
import base.util.Filter;

public class SysLog extends VModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3699288446532707480L;

	@Override
	public String getName() {
		return "Sys.Log";
	}
	
	@Override
	public Map<String, VField> getFields() {
		Map<String, VField> fields = new HashMap<>();
		fields.put("modelName", VField.string("Model Name", options()));
		fields.put("record_id", VField.integer("Record Id", options()));
		fields.put("displayString", VField.string("Display String", options()));
		fields.put("actor", VField.many2one("Actor", "Sys.User", options()));
		fields.put("action", VField.selection("Action", 
				selections("new", "Create New", 
						   "update", "Update", 
						   "delete", "Delete"), options()));
		fields.put("time", VField.datetime("Time", options()));
		fields.put("details", VField.one2many("Details", "Sys.Log.Detail", "log", options()));
		return fields;
	}

	public ArrayList<VObject> getLogs(String modelName, Integer recordId) {
		ArrayList<VObject> retVal = new ArrayList<>();
		Filter filter = new Filter();
		filter.hqlWhereClause = "modelName = :modelName_to_check and record_id = :record_id_to_check";
		filter.params.put("modelName_to_check", modelName);
		filter.params.put("record_id_to_check", recordId);
		List<Integer> logIds = controller.search(filter, "time DESC", 0, 0);
		for (Integer logId : logIds) {
			retVal.add(controller.browse(logId));
		}
		return retVal;
	}
	
	public ArrayList<VObject> getLogs(String modelName, Integer recordId, int first, int count) {
		ArrayList<VObject> retVal = new ArrayList<>();
		Filter filter = new Filter();
		filter.hqlWhereClause = "modelName = :modelName_to_check and record_id = :record_id_to_check";
		filter.params.put("modelName_to_check", modelName);
		filter.params.put("record_id_to_check", recordId);
		List<Integer> logIds = controller.search(filter, "time DESC", first, count);
		for (Integer logId : logIds) {
			retVal.add(controller.browse(logId));
		}
		return retVal;
	}
	
	public ArrayList<VObject> getActionLogs(VObject user) {
		ArrayList<VObject> retVal = new ArrayList<>();
		Filter filter = new Filter();
		filter.hqlWhereClause = "actor = :actor_to_check";
		filter.params.put("actor_to_check", user);
		List<Integer> logIds = controller.search(filter, "time DESC", 0, 0);
		for (Integer logId : logIds) {
			retVal.add(controller.browse(logId));
		}
		return retVal;
	}
	public ArrayList<VObject> getActionLogs(VObject user, int first, int count) {
		ArrayList<VObject> retVal = new ArrayList<>();
		Filter filter = new Filter();
		filter.hqlWhereClause = "actor = :actor_to_check";
		filter.params.put("actor_to_check", user);
		List<Integer> logIds = controller.search(filter, "time DESC", first, count);
		for (Integer logId : logIds) {
			retVal.add(controller.browse(logId));
		}
		return retVal;
	}
	public ArrayList<VObject> getActionLogs(VObject user, String modelName) {
		ArrayList<VObject> retVal = new ArrayList<>();
		Filter filter = new Filter();
		filter.hqlWhereClause = "modelName = :modelName_to_check and actor = :actor_to_check";
		filter.params.put("modelName_to_check", modelName);
		filter.params.put("actor_to_check", user);
		List<Integer> logIds = controller.search(filter, "time DESC", 0, 0);
		for (Integer logId : logIds) {
			retVal.add(controller.browse(logId));
		}
		return retVal;
	}
	public ArrayList<VObject> getActionLogs(VObject user, String modelName, int first, int count) {
		ArrayList<VObject> retVal = new ArrayList<>();
		Filter filter = new Filter();
		filter.hqlWhereClause = "modelName = :modelName_to_check and actor = :actor_to_check";
		filter.params.put("modelName_to_check", modelName);
		filter.params.put("actor_to_check", user);
		List<Integer> logIds = controller.search(filter, "time DESC", first, count);
		for (Integer logId : logIds) {
			retVal.add(controller.browse(logId));
		}
		return retVal;
	}

	@Override
	public Map<Integer, String> getDisplayString(List<Integer> ids) {
		Map<Integer, String> retVal = new HashMap<>();
		if (ids.size() == 0)
			return retVal;
		Map<Integer, VObject> models = controller.browse(ids);
		for (Integer id : models.keySet()) {
			VObject obj = models.get(id);
			VObject actor = (VObject) obj.getValue("actor");
			String actorName = actor != null ? (String) actor.getValue("name") : "Administrator";
			String display = "[" + actorName + "]-[" + obj.getValue("action").toString() + "]-[" + obj.getValue("time").toString() + "]";
			retVal.put(id, display);
		}
		return retVal;
	}
	
}
