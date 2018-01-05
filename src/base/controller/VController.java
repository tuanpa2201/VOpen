/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 20, 2016
* Author: tuanpa
*
*/
package base.controller;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ClassUtils;

import base.model.ButtonField;
import base.model.FunctionField;
import base.model.VField;
import base.model.VModel;
import base.model.VObject;
import base.util.Filter;
import base.util.VCache;

public class VController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2746315806085512027L;
	public String modelName;
	private VEnv env;
	public static Map<String, ArrayList<Class<? extends VModel>>> mapModel = new HashMap<>();
//	private VObject currentObj = null;
	
	public VController(String modelName) {
		super();
		this.modelName = modelName;
	}
	
	public void removeCache(Integer id) {
		VCache.remove(modelName, id.toString());
	}
	
	public void addCache(Integer id, VObject value) {
		VCache.put(modelName, id.toString(), value);
	}
	
	public void addCache(HashMap<Integer, VObject> map) {
		for (Integer id : map.keySet()) {
			addCache(id, map.get(id));
		}
	}
	
	public VObject getCache(Integer id) {
		return VCache.get(modelName, id.toString());
	}
	
	public void refresh() {
		VCache.refresh(modelName);
	}

	public Long count(Filter filter) {
			return (Long) this.execute("count", filter);
	}
	
	@SuppressWarnings("unchecked")
	public Map<Integer, VObject> browse(List<Integer> ids) {
		List<Integer> tmpIds = new ArrayList<>();
		tmpIds.addAll(ids);
		HashMap<Integer, VObject> retVal = new HashMap<>();
		for (Integer id :tmpIds) {
			VObject obj = getCache(id);
			if (obj != null) {
				retVal.put(id, obj);
			}
		}
		tmpIds.removeAll(retVal.keySet());
		if (tmpIds.size() > 0) {
			retVal.putAll((Map<Integer, VObject>)this.execute("browse", tmpIds));
		}
		addCache(retVal);
		return retVal;
	}
	
	public VActionResponse delete(List<Integer> ids) {
		VActionResponse response =  (VActionResponse) this.execute("delete", ids);
		if (response.status) {
			for (Integer id : ids) {
				removeCache(id);
			}
		}
		return response;
	}
	
	public VObject browse(Integer id) {
		ArrayList<Integer> ids = new ArrayList<>();
		if (id != null)
			ids.add(id);
		Map<Integer, VObject> tmp = browse(ids);
		return tmp.get(id);
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> loadIds(Filter filter, String orderBy, int from, int maxRows) {
		List<Integer> ids = (List<Integer>) this.execute("search", filter, orderBy, from, maxRows);
		this.browse(ids);
		//Load all model with this ids for cache
		return ids;
	}
	
	public VActionResponse create(Map<String, Object> mapValues) {
		VActionResponse response =  (VActionResponse) this.execute("create", mapValues);
		return response;
	}
	
	public VActionResponse update(List<Integer> ids, Map<String, Object> mapValues) {
		return	(VActionResponse)this.execute("update", ids, mapValues);
	}
	
	public Object getValue(Integer id, String fieldName) {
		//IsActive: Default true for new obj
		VObject obj = browse(id);
		if (fieldName.equals("isActive") && obj == null) {
			return true;
		}
		VField field = getField(fieldName);
		Object retVal = null;
		if (obj == null || field == null)
			return null;
		if (field instanceof FunctionField) {
			FunctionField fField = (FunctionField) field;
			retVal = this.execute(fField.functionName, obj);
		}
		else if (field instanceof ButtonField) {
			retVal = null;
		}
		else {
			retVal = this.execute("getValue", obj, fieldName);
		}
		
		return retVal;
	}
	
	public String getDisplayString(Integer id) {
		ArrayList<Integer> ids = new ArrayList<>();
		if (id != null)
			ids.add(id);
		String retVal = getDisplayString(ids).get(id);
		return retVal;
	}
	
	@SuppressWarnings("unchecked")
	public Map<Integer, String> getDisplayString(List<Integer> ids) {
		return (Map<Integer, String>) execute("getDisplayString", ids);
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> searchByString(Filter filter, String searchStr, int maxResult) {
		return (List<Integer>) this.execute("searchByString", filter, searchStr, maxResult);
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> search(Filter filter) {
		return (List<Integer>) this.execute("search", filter);
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> search(Filter filter, String orderBy, int firstResult, int maxResult) {
		return (List<Integer>) this.execute("search", filter, orderBy, firstResult, maxResult);
	}
	
	public String getTitle() {
		return (String) this.execute("getTitle");
	}
	
	public String getDescription() {
		return (String) this.execute("getDescription");
	}
	
	public static void addModelClass(String modelName, Class<? extends VModel> modelClass) {
		ArrayList<Class<? extends VModel>> lst = mapModel.get(modelName);
		if (lst == null) {
			lst = new ArrayList<>();
			mapModel.put(modelName, lst);
		}
		if(lst.contains(modelClass))
			return;
		lst.add(modelClass);
		lst.sort((new Comparator<Class<? extends VModel>>() {

			@Override
			public int compare(Class<? extends VModel> o1, Class<? extends VModel> o2) {
				
				VModel model1 = null;
				VModel model2 = null;
				try {
					model1 = o1.newInstance();
					model2 = o2.newInstance();
				}
				catch (Exception e) {
				}
				if (model1 == null || model2 == null)
					return 0;
				return model1.getPriority().compareTo(model2.getPriority());
			}
		}).reversed());
	}
	
	public Object execute(String methodName, Object... params) {
		return execute(false, 0, methodName, params);
	}
	
	public Object execute(boolean callSuper, int priority, String methodName, Object... params) {
		VModel executeModel = null;
		Method methodInvoke = null;
		Object retVal = null;
		for (Class<? extends VModel> modelClass : mapModel.get(modelName)) {
			VModel model = null;
			try {
				model = modelClass.newInstance();
				model.controller = this;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!callSuper && priority > 0 && priority > model.getPriority())
				continue;
			if(callSuper && priority == model.getPriority())
				continue;
			
			Method method = getDeclaredMethod(model, methodName, params);
			if (method != null) {
				executeModel = model;
				methodInvoke = method;
				break;
			}
		}
		
		if (methodInvoke == null) {
			for (Class<? extends VModel> modelClass : mapModel.get(modelName)) {
				VModel model = null;
				try {
					model = modelClass.newInstance();
					model.controller = this;
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (!callSuper && priority > 0 && priority > model.getPriority())
					continue;
				if(callSuper && priority == model.getPriority())
					continue;
					
				Method method = getMethodIncludeInherit(model, methodName, params);
				if (method != null) {
					executeModel = model;
					methodInvoke = method;
					break;
				}
			}
		}
		
		if (methodInvoke != null) {
			try {
				retVal = methodInvoke.invoke(executeModel, params);
			} catch (Exception e) {
				retVal = null;
			}
		}
		return retVal;
	}
	
	public Method getMethodIncludeInherit(VModel model, String methodName, Object... params) {
		Method methodInvoke = null;
		for (Method method : model.getClass().getMethods()) {
	        if (!method.getName().equals(methodName)) {
	            continue;
	        }
	        Class<?>[] parameterTypes = method.getParameterTypes();
	        parameterTypes = ClassUtils.primitivesToWrappers(parameterTypes);
	        if (parameterTypes.length != params.length)
	        	continue;
	        boolean matches = true;
	        for (int i = 0; i < parameterTypes.length; i++) {
	            if (params[i] != null && !parameterTypes[i].isAssignableFrom(params[i].getClass())) {
	                matches = false;
	                break;
	            }
	        }
	        if (matches) {
	        	methodInvoke = method;
	        	break;
	        }
	    }
		return methodInvoke;
	}
	
	public Method getDeclaredMethod(VModel model, String methodName, Object... params) {
		Method methodInvoke = null;
		for (Method method : model.getClass().getDeclaredMethods()) {
	        if (!method.getName().equals(methodName)) {
	            continue;
	        }
	        Class<?>[] parameterTypes = method.getParameterTypes();
	        parameterTypes = ClassUtils.primitivesToWrappers(parameterTypes);
	        if (parameterTypes.length != params.length)
	        	continue;
	        boolean matches = true;
	        for (int i = 0; i < parameterTypes.length; i++) {
	            if (params[i] != null && !parameterTypes[i].isAssignableFrom(params[i].getClass())) {
	                matches = false;
	                break;
	            }
	        }
	        if (matches) {
	        	methodInvoke = method;
	        	break;
	        }
	    }
		return methodInvoke;
	}
	
	public VField getField(String fieldName) {
		VField field = null;
		for (Class<? extends VModel> modelClass : mapModel.get(modelName)) {
			VModel model = null;
			try {
				model = modelClass.newInstance();
				model.controller = this;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			field = model.getAllFields().get(fieldName);
			if (field != null)
				break;
		}
		return field;
	}
	
	public Map<String, Object> getDefaultValues() {
		Map<String, Object> value = new HashMap<>();
		for (Class<? extends VModel> modelClass : mapModel.get(modelName)) {
			VModel model = null;
			try {
				model = modelClass.newInstance();
				model.controller = this;
			} catch (Exception e) {
				e.printStackTrace();
			}
			Map<String, Object> defaults = model.getDefaults();
			for (String fieldName : defaults.keySet()) {
				if (!value.containsKey(fieldName)) {
					value.put(fieldName, defaults.get(fieldName));
				}
			}
		}
		return value;
	}
	
	public List<String> getNotNullFields() {
		List<String> retVal  = new ArrayList<>();
		for (Class<? extends VModel> modelClass : mapModel.get(modelName)) {
			VModel model = null;
			try {
				model = modelClass.newInstance();
				model.controller = this;
			} catch (Exception e) {
				e.printStackTrace();
			}
			Map<String, VField> fields = model.getFields();
			for (String fieldName : fields.keySet()) {
				VField field = fields.get(fieldName);
				if (!field.nullable) {
					retVal.add(fieldName);
				}
			}
		}
		return retVal;
	}
	
	public Map<String, VField> getAllFields() {
		Map<String, VField> fields = new HashMap<>();
		for (Class<? extends VModel> modelClass : mapModel.get(modelName)) {
			VModel model = null;
			try {
				model = modelClass.newInstance();
				model.controller = this;
			} catch (Exception e) {
				e.printStackTrace();
			}
			Map<String, VField> tmp = model.getAllFields();
			fields.putAll(tmp);
		}
		return fields;
	}
	
	public String getTableName() {
		String tableName = null;
		for (Class<? extends VModel> modelClass : mapModel.get(modelName)) {
			VModel model = null;
			try {
				model = modelClass.newInstance();
				model.controller = this;
			} catch (Exception e) {
				e.printStackTrace();
			}
			tableName = model.getTableName();
			if (tableName != null)
				break;
		}
		return tableName;
	}
	
	public static Set<String> getAllModelName() {
		return mapModel.keySet();
	}
	
	public VActionResponse dynamicState(Map<String, Object> values) {
		VActionResponse response = new VActionResponse();
//		VObject obj = null;
//		try {
//			obj = (VObject) VClassLoader.getModelClass(modelName).newInstance();
//			for (String fieldName : values.keySet()) {
//				if (!(getField(fieldName) instanceof FunctionField)) {
//					obj.setValue(fieldName, values.get(fieldName));
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		if (obj != null) {
//			response = (VActionResponse) execute("dynamicState", obj);
//		}
		response = (VActionResponse) execute("dynamicState", values);
		return response;
	}
	
	public boolean isLog() {
		return (boolean) execute("isLog");
	}
	

  public boolean isGiganticTable() {
    return (boolean) execute("isGiganticTable");
  }
  
  public VEnv getVEnv() {
	  if (env != null) {
		  env.setLastTimeUsed(new Timestamp(System.currentTimeMillis()));
	  }
	  return env;
  }
  public void setVEnv(VEnv env) {
	  this.env = env;
  }
}
