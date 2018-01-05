/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 13, 2016
* Author: tuanpa
*
*/
package base.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import base.controller.VActionResponse;
import base.controller.VController;
import base.controller.VEnv;
import base.exception.VException;
import base.util.Filter;
import base.util.HibernateUtil;
import base.util.StringAppUtils;
import base.util.StringConvertUtil;
import base.util.StringUtils;
import base.util.Translate;
import base.util.VClassLoader;

/**
 * Base class for model & controller, this's all business need on a module
 * (Persistence, Business logic, Restful api)
 * 
 * @author tuanpa
 *
 */
public abstract class VModel implements Serializable {

	private static final long serialVersionUID = -5135469157678472993L;
	public VController controller;

	/**
	 * Name of object, maybe not the same with class name
	 */
	public abstract String getName();

	public String getModelName() {
		if (getName() == null)
			return getInherit();
		else
			return getName();
	}

	public String getTitle() {
		return getModelName();
	}

	public Integer getPriority() {
		return 0;
	}

	public String getOrderBy() {
		return "id";
	}

	public String getDescription() {
		return getModelName();
	}

	public boolean isLog() {
		return false;
	}
	
	public boolean isGiganticTable() {
    return false;
  }

	/**
	 * Name table to persistence data
	 */
	final public String getTableName() {
		String tableName = getModelName();
		tableName = tableName.toLowerCase().replaceAll("\\.", "_");
		return tableName;

	}

	/**
	 * Override to inherit
	 */
	public String getInherit() {
		return null;
	}

	/**
	 * All field of object
	 */
	public Map<String, VField> getFields() {
		return new HashMap<>();
	}

	public Map<String, VField> getPreserveFields() {
		Map<String, VField> fields = new HashMap<>();
		fields.put("id", VField.integer("ID", options("readonly", true, "help", "Identify")));
		// fields.put("createBy", VField.many2one("Create By", "Sys.User",
		// options("readonly", true, "help", "Id User created this record")));
		// fields.put("createTime",
		// VField.datetime("Create Time", options("readonly", true, "help",
		// "Time this record was created")));
		// fields.put("updateBy", VField.many2one("Update By", "Sys.User",
		// options("readonly", true, "help", "Id User updated this record")));
		// fields.put("updateTime",
		// VField.datetime("Update Time", options("readonly", true, "help",
		// "Time this record was updated")));
		fields.put("isActive", VField.yesno("Active", options("help", "Enable/disable this record")));
		fields.put("uuid", VField.string("Unique Identify",
				options("readonly", true, "help", "Universal uniquid identify of this object")));
		return fields;
	}

	public Map<String, VField> getAllFields() {
		Map<String, VField> fields = new HashMap<>();
		fields.putAll(getPreserveFields());
		fields.putAll(getFields());
		return fields;
	}

	/**
	 * Default value of fields
	 * 
	 * @return
	 */
	public Map<String, Object> getDefaults() {
		Map<String, Object> retVal = new HashMap<>();
		for (String fieldName : getAllFields().keySet()) {
			VField field = getAllFields().get(fieldName);
			if (field instanceof YesNoField) {
				retVal.put(fieldName, false);
			}
		}
		if (controller.getVEnv().getCurrentCompany() != null) {
			retVal.put("company", controller.getVEnv().getCurrentCompany());
		}
		retVal.put("isActive", true);
		return retVal;
	}

	/**
	 * Create new object with valuessea
	 * 
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public VActionResponse create(Map<String, Object> values) {
		VActionResponse response = new VActionResponse();
		response.status = false;
		if (controller.getVEnv() == null || !controller.getVEnv().isCreatable(this.getModelName()))
			return response;
		applyDefaultValues(values);
		VObject newObj = null;
		try {
			newObj = (VObject) VClassLoader.getModelClass(getModelName()).newInstance();
			for (String fieldName : values.keySet()) {
				VField field = controller.getField(fieldName);
				if (field != null && !field.company_dependent) {
					this.setValue(newObj, fieldName, values.get(fieldName));
				}
			}
			newObj.save();
			// Tuanpa: Company dependent field save after save normal field
			for (String fieldName : values.keySet()) {
				VField field = controller.getField(fieldName);
				if (field != null && field.company_dependent) {
					this.setValue(newObj, fieldName, values.get(fieldName));
				}
			}
			// Log
			if (this.isLog()) {
				VController logController = VEnv.sudo().get("Sys.Log");
				Map<String, Object> logValues = new HashMap<>();
				logValues.put("modelName", getModelName());
				logValues.put("record_id", newObj.getId());
				logValues.put("displayString", controller.getDisplayString(newObj.getId()));
				logValues.put("actor", controller.getVEnv().user);
				logValues.put("action", "new");
				logValues.put("time", new Timestamp(System.currentTimeMillis()));
				logController.create(logValues);
			}
		} catch (Exception e) {
			e.printStackTrace();
			newObj = null;
			response.status = false;
//			response.typeresponse = "error";
//			response.messages.add("[ERROR]Create " + this.getModelName() + " false!");
			response.messages.add("[ERROR]Details: " + e.getMessage());
		}

		Integer id = -1;
		if (newObj != null) {
			id = (Integer) newObj.getValue("id");
			response.status = true;
			response.typeresponse = "info";
			response.id = id;
			response.obj = newObj;
			response.messages.add("[INFO]Save " + getDisplayString(id) + " done!");
		}
		return response;
	}

	public void applyDefaultValues(Map<String, Object> values) {
		Map<String, Object> defaults = getDefaults();
		for (String fieldName : defaults.keySet()) {
			if (!values.containsKey(fieldName)) {
				values.put(fieldName, defaults.get(fieldName));
			}
		}
	}

	/**
	 * Copy mode with id
	 * 
	 * @param uuid
	 * @param id
	 * @return copied model
	 */
	public VObject copy(Integer id) {
		return null;
	}

	/**
	 * Update object with id in ids with values
	 */
	public VActionResponse update(List<Integer> ids, Map<String, Object> values) {
		VActionResponse vresponse = new VActionResponse();
		vresponse.actionType = VActionResponse.ACTION_TYPE_SAVE;
		if (controller.getVEnv() == null || !controller.getVEnv().isUpdatable(this.getModelName())) {
			vresponse.status = false;
			return vresponse;
		}
		if (ids.size() == 0 || values.keySet().size() == 0) {
			vresponse.status = true;
			return vresponse;
		}
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Transaction transaction = session.beginTransaction();
		int retVal = 0;
		Map<String, Map<String, Object>> mapFieldUpdate = new HashMap<>();
		try {
			for (VObject obj : controller.browse(ids).values()) {
				obj.removeCacheReference();
				for (String fieldName : values.keySet()) {
					VField vField = controller.getField(fieldName);
					if (vField != null) {
						if (vField.isLog) {
							String old_value = StringConvertUtil.toString(vField, obj.getValue(fieldName));
							String new_value = StringConvertUtil.toString(vField, values.get(fieldName));
							Map<String, Object> mapUpdate = new HashMap<>();
							mapUpdate.put("fieldName", fieldName);
							mapUpdate.put("old_value", old_value);
							mapUpdate.put("new_value", new_value);
							mapFieldUpdate.put(fieldName, mapUpdate);
						}
						setValue(obj, fieldName, values.get(fieldName));
					}
				}
				obj.save();
				if (this.isLog()) {
					VController logController = VEnv.sudo().get("Sys.Log");
					Map<String, Object> logValues = new HashMap<>();
					logValues.put("modelName", getModelName());
					logValues.put("record_id", obj.getId());
					logValues.put("displayString", controller.getDisplayString(obj.getId()));
					logValues.put("actor", controller.getVEnv().user);
					logValues.put("action", "update");
					logValues.put("time", new Timestamp(System.currentTimeMillis()));
					VActionResponse logCreateResponse = logController.create(logValues);

					VController logDetailController = VEnv.sudo().get("Sys.Log.Detail");
					for (String fieldName : mapFieldUpdate.keySet()) {
						Map<String, Object> mapUpdate = mapFieldUpdate.get(fieldName);
						mapUpdate.put("log", logCreateResponse.obj);
						logDetailController.create(mapUpdate);
					}
				}
			}
			retVal++;
			transaction.commit();
		} catch (Exception e) {
			vresponse.messages.add("[ERROR] " + e.getMessage());
			transaction.rollback();
			retVal = -1;
		} finally {
			session.close();
		}
		vresponse.status = retVal > 0;
		return vresponse;
	}

	public void setValue(VObject obj, String fieldName, Object value) throws Exception {
		VField vfield = controller.getField(fieldName);
		/**
		 * Validation Value
		 */
		if (vfield != null) {
			if (vfield.validationField(value)) {
				if (vfield.unique && !checkUniqueValue(obj, fieldName, value)) {
					// Messagebox.show("" + fieldName +
					// Translate.translate(ZKEnv.getEnv(), null, " is exist!"),
					// "Error", Messagebox.OK, Messagebox.ERROR);
					throw new Exception(fieldName + VException.UNIQUE_EXCEPTION);

				}
				if (vfield.company_dependent) {
					VController cdController = VEnv.sudo().get("Sys.Company.Dependent.Value");
					cdController.execute("setCompanyDependentValue", controller.getVEnv().getCurrentCompany(), getModelName(),
							obj.getId(), fieldName, StringConvertUtil.toString(vfield, value));
				} else {
					obj.setValue(fieldName, value);
				}
			}
		}
	}

	public Object getValue(VObject obj, String fieldName) {
		Object retVal = null;
		VField vfield = controller.getField(fieldName);
		if (!vfield.company_dependent) {
			retVal = obj.getValue(fieldName);
		} else {
			VController cdController = VEnv.sudo().get("Sys.Company.Dependent.Value");
			String value = (String) cdController.execute("getCompanyDependentValue", controller.getVEnv().getCurrentCompany(),
					getModelName(), obj.getId(), fieldName);
			retVal = StringConvertUtil.toObject(vfield, value);
		}

		return retVal;
	}

	/**
	 * Check Exits Value
	 */
	public boolean checkUniqueValue(VObject obj, String fieldName, Object value) {
		Map<String, Object> params = new HashMap<>();
		params.put("paramsearch", value);
		VController sudoController = VEnv.sudo().get(controller.modelName);
		List<Integer> ids = sudoController
				.search(new Filter("upper(" + fieldName + ")" + " = upper (:paramsearch)", params));
		// List<Integer> ids = controller.search(new Filter(fieldName + "
		// =:paramsearch", params));
		Integer id = (Integer) obj.getValue("id");
		if (ids != null && ids.size() > 0 && id == null) {
			return false;
		}
		if (id != null && ids != null && id > 0 && ids.size() > 0 && !id.equals(ids.get(0))) {
			return false;
		}
		return true;
	}

	/**
	 * Search model
	 */
	@SuppressWarnings("rawtypes")
	public List<Integer> search(Filter filter, String orderBy, int firstResult, int maxResult) {
		if (controller.getVEnv() == null || !controller.getVEnv().isReadable(this.getModelName())) {
			return new ArrayList<>();
		}
		if (StringAppUtils.isEmpty(orderBy)) {
			orderBy = getOrderBy();
		}
		String hql = "select id from " + VClassLoader.getModelClass(getModelName()).getName() + " md";
		String whereStr = filter.hqlWhereClause != null ? filter.hqlWhereClause : "";
		Map<String, Object> params = new HashMap<>();
		params.putAll(filter.params);
		Filter applyRule = applyRuleForModel();
		whereStr += applyRule.hqlWhereClause;
		params.putAll(applyRule.params);
		if (StringAppUtils.isNotEmpty(whereStr)) {
			hql += " where " + StringUtils.Trim(whereStr.trim(), "and");
		}
		hql += " order by " + orderBy;

		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Query query = null;
		List<Integer> retVal = new ArrayList<>();
		List<?> result = null;
		try {
			query = session.createQuery(hql);
			query.setFirstResult(firstResult);
			if (maxResult > 0) {
				query.setMaxResults(maxResult);
			}
			if (params.keySet().size() > 0) {
				for (String param : params.keySet()) {
					Object paramVal = params.get(param);
					if (paramVal instanceof Collection) {
						query.setParameterList(param, (Collection) paramVal);
					} else {
						query.setParameter(param, paramVal);
					}
				}
			}
			result = query.list();
			for (Object obj : result) {
				if (obj instanceof Integer) {
					retVal.add((Integer) obj);
				}
			}
		} catch (Exception e) {
			System.out.println("HQL:" + hql);
			System.out.println("HQLEXCEPTION" + e);
			result = new ArrayList<>();
		} finally {
			session.close();
		}
		return retVal;
	}

	public List<Integer> search(Filter filter) {
		return search(filter, null, 0, 0);
	}

	public List<Integer> searchByString(Filter filter, String searchStr, int maxResult) {
		if (filter == null)
			filter = new Filter();
		Filter newFilter = new Filter(filter.hqlWhereClause, filter.params);
		List<Integer> retVal = new ArrayList<>();
		StringBuffer whereClause = new StringBuffer(newFilter.hqlWhereClause);
		boolean isInclude = false;
		if (whereClause.length() > 0) {
			whereClause.append(" and (");
			isInclude = true;
		}
		StringBuffer appendClause = new StringBuffer();
		Map<String, Object> params = newFilter.params;
		for (String fieldName : this.getAllFields().keySet()) {
			VField field = this.getAllFields().get(fieldName);
			if (field instanceof StringField) {
				if (appendClause.length() > 0)
					appendClause.append(" or ");
				String variable = fieldName + RandomUtils.nextInt(1000);
				if (StringUtils.isEmpty(searchStr)) {
					appendClause.append(fieldName + " is null");
					appendClause.append(" or ");
				}
				appendClause.append(fieldName + " like :" + variable);
				params.put(variable, "%" + searchStr + "%");
			}
		}
		whereClause.append(appendClause);
		if (isInclude) {
			whereClause.append(")");
		}
		newFilter.hqlWhereClause = whereClause.toString();
		retVal = search(newFilter, "", 0, maxResult);
		return retVal;
	}

	public Map<Integer, VObject> browse(List<Integer> idsAll) {
		HashMap<Integer, VObject> retVal = new HashMap<>();
		int idsLength = idsAll.size();
		//Maximum load 1000 item each time
		for (int i = 0; i <= idsLength / 1000; i ++) {
			List<Integer> ids = idsAll.subList((i * 1000), ((i + 1) * 1000) < idsLength ? ((i + 1) * 1000) : idsLength);
			String hql = "from " + VClassLoader.getModelClass(getModelName()).getName() + " where id in (:ids)";
			SessionFactory sf = HibernateUtil.getSessionFactory();
			Session session = sf.openSession();
	
			try {
				Query query = session.createQuery(hql);
				query.setParameterList("ids", ids);
				List<?> list = query.list();
				for (Object obj : list) {
					if (obj instanceof VObject) {
						VObject vobj = (VObject) obj;
						retVal.put((Integer) vobj.getValue("id"), vobj);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (session != null) {
					session.close();
				}
			}
		}

		return retVal;
	}

	public VObject browse(Integer id) {
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		if (id == 5032) {
			System.out.println("Fuck here");
		}
		VObject vobj = (VObject) session.get(VClassLoader.getModelClass(getModelName()), id);
		session.close();
		return vobj;
	}

	@SuppressWarnings("rawtypes")
	public Long count(Filter filter) {
		if (controller.getVEnv() == null || !controller.getVEnv().isReadable(this.getModelName())) {
			return 0L;
		}
		String hql = "select count(id) from " + VClassLoader.getModelClass(getModelName()).getName() + " md";
		String whereStr = filter.hqlWhereClause != null ? filter.hqlWhereClause : "";
		Map<String, Object> params = new HashMap<>();
		params.putAll(filter.params);
		Filter applyRule = applyRuleForModel();
		whereStr += applyRule.hqlWhereClause;
		params.putAll(applyRule.params);
		if (StringAppUtils.isNotEmpty(whereStr)) {
			hql += " where " + StringUtils.Trim(whereStr.trim(), "and");
		}
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Query query = null;
		Long retVal = null;
		try {
			query = session.createQuery(hql);
			if (params.keySet().size() > 0) {
				for (String param : params.keySet()) {
					Object paramVal = params.get(param);
					if (paramVal instanceof Collection) {
						query.setParameterList(param, (Collection) paramVal);
					} else {
						query.setParameter(param, paramVal);
					}
				}
			}

			retVal = (Long) query.uniqueResult();
		} catch (Exception e) {
			System.out.println("HQL:" + hql);
			System.out.println("HQLEXCEPTION" + e);
			retVal = 0L;
		} finally {
			session.close();
		}
		return retVal;
	}

	/**
	 * Delete object with given ids
	 */
	@SuppressWarnings("unchecked")
	public VActionResponse delete(List<Integer> ids) {
		VActionResponse response = new VActionResponse();
		response.actionType = VActionResponse.ACTION_TYPE_DELETE;
		response.status = Boolean.TRUE;
		if (controller.getVEnv() == null || !controller.getVEnv().isDeletable(this.getModelName())) {
			response.status = Boolean.FALSE;
			response.messages.add("[ERROR] - No ENV or not deletable!");
			return response;
		}
		if (ids.size() == 0) {
			return response;
		}
		Map<Integer, VObject> objs = controller.browse(ids);
		for (VObject obj : objs.values()) {
			try {
				// get value before delete
				Map<String, Object> logValues = new HashMap<>();
				logValues.put("modelName", getModelName());
				logValues.put("record_id", obj.getId());
				logValues.put("displayString", controller.getDisplayString(obj.getId()));
				logValues.put("actor", controller.getVEnv().user);
				logValues.put("action", "delete");
				logValues.put("time", new Timestamp(System.currentTimeMillis()));

				// Process ondelete for one2many field
				Map<String, VField> allFields = controller.getAllFields();
				for (String fieldName : allFields.keySet()) {
					VField field = allFields.get(fieldName);
					if (field instanceof One2ManyField) {
						One2ManyField oField = (One2ManyField) field;
						VController childController = VEnv.sudo().get(oField.childModel);
						ArrayList<Integer> childIds = new ArrayList<>();
						for (VObject childObj : (List<VObject>) obj.getValue(fieldName)) {
							childIds.add(childObj.getId());

						}
						if (One2ManyField.ON_DELETE_CASCADE.equals(oField.ondelete)) {
							childController.delete(childIds);
						} else if (One2ManyField.ON_DELETE_SET_NULL.equals(oField.ondelete)) {
							Map<String, Object> mapValues = new HashMap<>();
							mapValues.put(oField.joinField, null);
							childController.update(childIds, mapValues);
						}
					}
				}
				// and then, just delete
				boolean status = obj.delete();

				// if not issue exception => delete ok => create log
				if (status && this.isLog()) {
					VController logController = VEnv.sudo().get("Sys.Log");
					logController.create(logValues);
				}
				if (!status) {
					response.status = Boolean.FALSE;
//					response.messages.add("[ERROR] - Can not delete record [" + obj.getId() + "]");
					response.messages.add(Translate.translate(controller.getVEnv(), null, "[ERROR] - Can not delete record"));
				}
			} catch (Exception e) {
				e.printStackTrace();
				response.status = Boolean.FALSE;
				response.messages.add("[ERROR] - Can not delete record [" + obj.getId() + "]");
			}
		}
		return response;
	}

	public HashMap<String, Object> options(Object... params) {
		HashMap<String, Object> retVal = new HashMap<>();
		for (int i = 0; i < params.length; i += 2) {
			if (params.length > i + 1 && i % 2 == 0) {
				retVal.put(params[i].toString(), params[i + 1]);
			}
		}
		return retVal;
	}

	public LinkedHashMap<String, String> selections(String... params) {
		LinkedHashMap<String, String> retVal = new LinkedHashMap<>();
		for (int i = 0; i < params.length; i += 2) {
			if (params.length > i + 1 && i % 2 == 0) {
				retVal.put(params[i].toString(), params[i + 1]);
			}
		}
		return retVal;
	}

	public Map<Integer, String> getDisplayString(List<Integer> ids) {
		Map<Integer, String> retVal = new HashMap<>();
		if (ids.size() == 0)
			return retVal;
		Map<Integer, VObject> models = controller.browse(ids);
		for (Integer id : models.keySet()) {
			VObject obj = models.get(id);
			try {
				retVal.put(id, obj.toString());
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
		return retVal;
	}

	public String getDisplayString(Integer id) {
		List<Integer> ids = new ArrayList<>();
		if (id != null)
			ids.add(id);
		return getDisplayString(ids).get(id);
	}

	public Class<? extends VObject> getModelClass() {
		@SuppressWarnings("unchecked")
		Class<? extends VObject> modelClass = (Class<? extends VObject>) VClassLoader.getModelClass(getModelName());
		return modelClass;
	}

	public Object _super(String methodName, Object... params) {
		return controller.execute(true, getPriority(), methodName, params);
	}

	public VActionResponse dynamicState(Map<String, Object> values) {
		VActionResponse response = new VActionResponse();
		return response;
	}

	public String companyPermission() {
		String companyPermission = "";
		if (controller.getVEnv() == null || !getFields().keySet().contains("company"))
			return companyPermission;
		companyPermission = " and company.id in ($LIST_COMPANY_ID$)";
		return companyPermission;
	}

	public Filter applyRuleForModel() {
		Filter filter = new Filter();
		if (controller.getVEnv() != null && controller.getVEnv().isAdmin()) {
			return filter;
		}
		String whereStr = controller.getVEnv().getRuleForModelForRead(this.getModelName());
		whereStr = whereStr != null ? whereStr : "";
		whereStr = whereStr + companyPermission();
		if (!StringUtils.isEmpty(whereStr)) {
			int i = 0;
			while (whereStr.indexOf("$") > 0) {
				String param = whereStr.substring(whereStr.indexOf("$"),
						whereStr.indexOf("$", whereStr.indexOf("$") + 1) + 1);
				if (param.startsWith("$@")) {
					String name = param.substring(2, param.lastIndexOf("$"));
					String modelName = VClassLoader.getModelClass(name).getName();
					if (modelName != null) {
						whereStr = whereStr.replace(param, modelName);
					}
				} else {
					String paramName = "p" + i;
					whereStr = StringUtils.replaceFirst(param, " :" + paramName + " ", whereStr);
					// whereStr = whereStr.replace(param, " :? ");
					filter.params.put(paramName, controller.getVEnv().getContext(param));
					i++;
				}
			}
			// whereStr += " ";
			// StringBuilder sb = new StringBuilder();
			// String[] strSplit = whereStr.split("\\?");
			// int j = 0;
			// for (String paramname : filter.params.keySet()) {
			// sb.append(strSplit[j]).append(paramname);
			// j++;
			// }
			// sb.append(strSplit[filter.params.size()]);
			// whereStr = sb.toString();
		}
		filter.hqlWhereClause = " " + whereStr;
		return filter;
	}

	// phuln
	public boolean checkExistFieldValue(List<Integer> ids, Map<String, Object> values, String fieldNameUnique) {

		if (ids != null && ids.size() > 0) {
			for (Integer id : ids) {
				if (checkExist(id, values, fieldNameUnique)) {
					continue;
				} else {
					return false;
				}
			}
		} else {
			return checkExist(0, values, fieldNameUnique);
		}

		return true;
	}

	private boolean checkExist(Integer id, Map<String, Object> values, String fieldNameUnique) {

		Filter filter = new Filter();
		filter.hqlWhereClause = fieldNameUnique + "= :" + fieldNameUnique;
		filter.params.put(fieldNameUnique, values.get(fieldNameUnique));
		if (id > 0) {
			filter.hqlWhereClause += " AND id <> :id";
			filter.params.put("id", id);
		}

		List<Integer> idsSearched = this.search(filter);
		if (idsSearched != null && idsSearched.size() > 0) {
			// Clients.showNotification("Giá trị đã tồn tại, yêu cầu nhập lại");
			return false;
		} else {
			return true;
		}
	}
}
