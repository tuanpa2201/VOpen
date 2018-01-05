/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Sep 8, 2016
* Author: tuanpa
*
*/
package base.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import base.VEnvManager;
import base.common.VNode;
import base.model.VObject;
import base.util.Filter;
import base.util.VClassLoader;
import base.util.ZKEnv;
import base.view.ClientInfo;
import modules.sys.model.SysCompany;

public class VEnv {
	private String sessionKey = UUID.randomUUID().toString();
	ConcurrentHashMap<String, Object> context = new ConcurrentHashMap<>();
	private Timestamp lastTimeUsed = new Timestamp(System.currentTimeMillis());
	private Timestamp timeLogined = new Timestamp(System.currentTimeMillis());
	HashMap<String, VController> mapController = new HashMap<>();
	public VObject user;
	private boolean isAdmin = false;
	public static final String LIST_COMPANY_ID = "$LIST_COMPANY_ID$";
	public static final String USER = "$USER$";
	public boolean isDebug = false;
	private ClientInfo clientInfo;

	public VEnv() {
		super();
		timeLogined = new Timestamp(System.currentTimeMillis());
	}

	public Timestamp getTimeLogined() {
    return timeLogined;
  }

  public String getSessionKey() {
		return sessionKey;
	}

	public void setContext(String key, Object value) {
		context.put(key, value);
		setLastTimeUsed(new Timestamp(System.currentTimeMillis()));
	}

	public void removeContext(String key) {
		context.remove(key);
	}

	public Object getContext(String key) {
		setLastTimeUsed(new Timestamp(System.currentTimeMillis()));
		return context.get(key);
	}

	public VController get(String modelName) {
		VController controller = mapController.get(modelName);
		if (controller == null) {
			controller = new VController(modelName);
			controller.setVEnv(this);
			mapController.put(modelName, controller);
		}
		return controller;
	}

	public static VEnv getSession(String sessionKey) {
		return (VEnv) VEnvManager.getEnv(sessionKey);
	}

	public static VEnv login(String username, String password) {
		VEnv env = null;
		VObject user = (VObject) sudo().get("Sys.User").execute("login", username, password);
		if (user != null) {
			env = new VEnv();
			env.user = user;
			if (user.getUid() != null
					&& (user.getUid().equals("sys_user_admin") || user.getUid().equals("sys_user_superuser"))) {
				env.isAdmin = true;
			}
			VEnvManager.setEnv(env.sessionKey, env);
			env.initParamEnvironment();
		}
		return env;
	}

	public static VEnv login(VObject user) {
		VEnv env = null;
		if (user != null) {
			env = new VEnv();
			env.user = user;
			if (user.getUid() != null
					&& (user.getUid().equals("sys_user_admin") || user.getUid().equals("sys_user_superuser"))) {
				env.isAdmin = true;
			}
			VEnvManager.setEnv(env.sessionKey, env);
			env.initParamEnvironment();
		}
		return env;
	}

	public void initParamEnvironment() {
		this.setContext(USER, user);
		VNode fullPermision = getCompanyTree(sudo().get(SysCompany.modelName));
		this.setContext(LIST_COMPANY_ID, fullPermision.toList());
	}

	public void reloadPermission() {
		this.setContext(LIST_COMPANY_ID, getCompanyWithCurrentPrivilege());
	}

	private List<Integer> getCompanyWithCurrentPrivilege() {
		List<Integer> companyIds = new ArrayList<>();
		VObject companySelected = getCurrentCompany();
		if (companySelected != null) {
			Integer selectedId = companySelected.getId();
			List<Integer> companyIdChil = getCompanyTreePermision().toList(selectedId);
			companyIds.addAll(companyIdChil);
		}
		if (companyIds.size() == 0) {
			companyIds.add(-1);
		}
		return companyIds;
	}

	private void cleanSession() {
		VEnvManager.removeEnv(sessionKey);
	}

	public static void logout(String sessionKey) {
		VEnv env = getSession(sessionKey);
		if (env != null) {
			env.cleanSession();
		}
	}

	Map<String, String> cachePerm = new HashMap<>();

	/**
	 * @param modelName
	 * @return "creatable | readable | updatable | deletable"
	 */
	public String getPermissionForModel(String modelName) {
		if (isAdmin) {
			return "creatable readable updatable deletable";
		}
		if (cachePerm.get(modelName) != null) {
			return cachePerm.get(modelName);
		}
		VController permController = sudo().get("Sys.Permission");
		Map<String, Object> params = new HashMap<>();
		params.put("isActive", true);
		params.put("model", modelName);
		Object groups = user.getValue("groups");
		params.put("group", groups);
		String retVal = "";
	    if (groups != null && ((Collection<?>) groups).size() > 0) {
	    	for (VObject perm : permController
					.browse(permController
							.search(new Filter("isActive = :isActive and model = :model and group in (:group)", params)))
					.values()) {
				if (Boolean.TRUE.equals(perm.getValue("creatable"))) {
					if (retVal.indexOf("creatable") == -1) {
						retVal += "creatable ";
					}
				}
				if (Boolean.TRUE.equals(perm.getValue("readable"))) {
					if (retVal.indexOf("readable") == -1) {
						retVal += "readable ";
					}
				}
				if (Boolean.TRUE.equals(perm.getValue("updatable"))) {
					if (retVal.indexOf("updatable") == -1) {
						retVal += "updatable ";
					}
				}
				if (Boolean.TRUE.equals(perm.getValue("deletable"))) {
					if (retVal.indexOf("deletable") == -1) {
						retVal += "deletable ";
					}
				}
			}
		}
		cachePerm.put(modelName, retVal);
		return retVal;
	}

	public boolean isCreatable(String modelName) {
		return getPermissionForModel(modelName).indexOf("creatable") >= 0;
	}

	public boolean isReadable(String modelName) {
		return getPermissionForModel(modelName).indexOf("readable") >= 0;
	}

	public boolean isUpdatable(String modelName) {
		return getPermissionForModel(modelName).indexOf("updatable") >= 0;
	}

	public boolean isDeletable(String modelName) {
		return getPermissionForModel(modelName).indexOf("deletable") >= 0;
	}

	HashMap<String, String> cacheRule = new HashMap<>();

	public String getRuleForModelForRead(String modelName) {
		if (cacheRule.get(modelName) != null) {
			return cacheRule.get(modelName);
		}
		if (isAdmin)
			return null;
		VController permController = sudo().get("Sys.Rule");
		Map<String, Object> params = new HashMap<>();
		params.put("model", modelName);
		params.put("apply_read", true);
		params.put("user1", user);
		params.put("user2", user);

		String sysGroupClass = VClassLoader.getModelClass("Sys.Group").getName();
		String hql = "model = :model and apply_read = :apply_read and (:user1 in elements(users)  " + " or exists "
				+ "	(from " + sysGroupClass + " g " + "		where :user2 in elements(g.users) "
				+ "		and g in elements(md.groups)" + " ))";

		List<Integer> ruleIds = permController.search(new Filter(hql, params), "priority desc", 0, 1);
		String retVal = "";
		if (!ruleIds.isEmpty()) {
			VObject rule = permController.browse(ruleIds.get(0));
			retVal = " and " + rule.getValue("hql").toString();
		}
		cacheRule.put(modelName, retVal);

		return retVal;
	}

	public Locale getLocale() {
		if (getContext("language") == null) {
			setContext("language", "vn");
		}
		Locale locale = new Locale((String) getContext("language"));
		return locale;
	}

	public void setLocale(Locale locale) {
		setContext("language", locale.getLanguage());
	}

	private static VEnv quest;

	public static VEnv gudo() {
		if (quest == null) {
			quest = new VEnv();
		}
		return quest;
	}

	private static VEnv su;

	public static VEnv sudo() {
		if (su == null) {
			su = new VEnv();
			su.isAdmin = true;
		}
		return su;
	}

	public void setCurrentCompany(VObject company) {
		setContext("current_company", company);
		reloadPermission();
	}

	public VObject getCurrentCompany() {
		VObject company = (VObject) getContext("current_company");
		if (company == null) {
			if (user == null) {
				VController comController = VEnv.sudo().get("Sys.Company");
				Filter filter = new Filter();
				filter.hqlWhereClause = "uuid = :uid_company_root";
				filter.params.put("uid_company_root", "company_root");
				if (comController.search(filter).size() > 0) {
					company = comController.browse(comController.search(filter)).values().iterator().next();
				}
			} else {
				@SuppressWarnings("unchecked")
				Collection<VObject> companies = (Collection<VObject>) user.getValue("companies");
				if (companies != null && companies.size() > 0) {
					company = companies.iterator().next();
				} else {
					VController comController = VEnv.sudo().get("Sys.Company");
					Filter filter = new Filter();
					filter.hqlWhereClause = "uuid = :uid_company_root";
					filter.params.put("uid_company_root", "company_root");
					if (comController.search(filter).size() > 0) {
						company = comController.browse(comController.search(filter)).values().iterator().next();
					}
				}
			}
			if (company != null)
				setCurrentCompany(company);
		}
		return company;
	}

	private VNode companyTree;

	public VNode getCompanyTreePermision() {
		if (companyTree == null) {
			companyTree = getCompanyTree(get(SysCompany.modelName));
		}
		return companyTree;
	}

	public void reloadCompanyTreePermision() {
		companyTree = getCompanyTree(get(SysCompany.modelName));
		reloadPermission();
	}

	private VNode getCompanyTree(VController controller) {
		VNode retTree = new VNode(controller);
		List<VObject> roots = getDependedCompany();
		for (VObject root : roots) {
			retTree.addNode(root.getId());
		}
		return retTree;
	}

	public List<VObject> getCompanies() {
		List<VObject> companys = new ArrayList<>();
		List<Integer> companyids = getCompanyTreePermision().toList();
		for (Integer id : companyids) {
			companys.add(ZKEnv.getEnv().get(SysCompany.modelName).browse(id));
		}
		return companys;
	}

	public List<VObject> getDependedCompany() {
		List<VObject> companies = new ArrayList<>();
		if (isAdmin) {
			VController comController = sudo().get("Sys.Company");
			Filter filter = new Filter();
			filter.hqlWhereClause = " parentId is Null";
			companies.addAll(comController.browse(comController.search(filter)).values());
		} else {
			@SuppressWarnings("unchecked")
			Collection<VObject> userCompanies = (List<VObject>) user.getValue("companies");
			if (userCompanies.size() > 0) {
				companies.addAll(userCompanies);
			} else {
				VController comController = get("Sys.Company");
				Filter filter = new Filter();
				filter.hqlWhereClause = "uuid = :uid_company_root";
				filter.params.put("uid_company_root", "company_root");
				companies.addAll(comController.browse(comController.search(filter)).values());
			}
		}
		return companies;
	}

	@SuppressWarnings("unchecked")
	public List<VObject> getMenu() {
		List<VObject> menus = new ArrayList<>();
		VController controller = VEnv.sudo().get("Sys.Menu");
		if (isAdmin) {
			Filter filter = new Filter();
			filter.hqlWhereClause = "isActive = :isActiveTrue and (screen is null or screen='both' or screen =:typeScreen) ";
			filter.params.put("isActiveTrue", true);
			filter.params.put("typeScreen", clientInfo.getScreen());
			Map<Integer, VObject> mapMenus = controller.browse(controller.search(filter));
			menus.addAll(mapMenus.values());
		} else {
			menus = new ArrayList<>();
			for (VObject group : (Collection<VObject>) user.getValue("groups")) {
				for (VObject menu : (Collection<VObject>) group.getValue("menus")) {
					if (!menus.contains(menu) && menu.getIsActive() && checkScreen(menu)) {
						menus.add(menu);
					}
				}
			}
			List<VObject> menus2 = new ArrayList<>();
			menus2.addAll(menus);
			for (VObject menu : menus) {
				VObject parent = (VObject) menu.getValue("parentId");
				while (parent != null) {
					if (!menus2.contains(parent)) {
						menus2.add(parent);
					}
					parent = (VObject) parent.getValue("parentId");
				}
			}
			menus = menus2;
		}
		Collections.sort(menus, new Comparator<VObject>() {

			@Override
			public int compare(VObject o1, VObject o2) {
				BigDecimal v1 = (BigDecimal) o1.getValue("fullsequence");
				BigDecimal v2 = (BigDecimal) o2.getValue("fullsequence");
				return v1.compareTo(v2);
			}
		});
		return menus;
	}

	private boolean checkScreen(VObject menu) {
		boolean resVal = true;
		Object screen = menu.getValue("screen");
		if (screen != null && !screen.equals("both") && !screen.equals(ZKEnv.getEnv().getClientInfo().getScreen())) {
			resVal = false;
		}
		return resVal;
	}

	public ClientInfo getClientInfo() {
		return clientInfo;
	}

	public void setClientInfo(ClientInfo clientInfo) {
		this.clientInfo = clientInfo;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setCurrentVObjectEditingValue(String modelName, Map<String, Object> mapValue) {
		setContext("current_edit_" + modelName, mapValue);
	}

	public void removeCurrentVObjectEditingValue(String modelName) {
		removeContext("current_edit_" + modelName);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getCurrentVObjectEditingValue(String modelName) {
		Map<String, Object> retVal = (Map<String, Object>) getContext("current_edit_" + modelName);
		if (retVal == null) {
			retVal = new HashMap<>();
		}
		return retVal;
	}

	public Timestamp getLastTimeUsed() {
		return lastTimeUsed;
	}

	public void setLastTimeUsed(Timestamp lastTimeUsed) {
		this.lastTimeUsed = lastTimeUsed;
	}
}