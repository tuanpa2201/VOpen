/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Sep 11, 2016
* Author: tuanpa
*
*/
package base.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.RandomUtils;
import org.zkoss.image.AImage;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.HtmlNativeComponent;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treecol;
import org.zkoss.zul.Treecols;
import org.zkoss.zul.Treeitem;

import base.common.VNode;
import base.controller.VController;
import base.controller.VEnv;
import base.model.VObject;
import base.util.ConfigUtil;
import base.util.StringAppUtils;
import base.util.Translate;
import base.util.ZKEnv;
import modules.sys.model.SysCompany;
import modules.sys.view.AccountInfo;
import modules.sys.view.NotificationScreen;

public class Index extends SelectorComposer<Component> implements EventListener<Event> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4809949715374262617L;

	Div divBody;

	@Wire
	Div div_nav;

	@Wire
	Div divMain;

	@Wire
	Div divLanguage;

	@Wire
	Div divCompany;

	@Wire
	Image user_avatar;

	@Wire
	Label username;

	@Wire
	Div shortcut_placeholder;

	@Wire
	Div divShowAllModule;

	@Wire
	Label lbModuleName;

	@Wire
	Label lblListNotification;

	@Wire
	Div divNotifications;

	@Wire
	Label total;

	private ClientInfo clientInfo = new ClientInfo();

	@WireVariable
	private Desktop desktop;

	private Boolean isTabStyle = ConfigUtil.getConfig("HOME_PAGE_STYLE", "SINGLE").equals("TAB");

	public Div getDivMain() {
		return divMain;
	}

	@Listen("onClientInfo=#divBody")
	public void onClientInfo(ClientInfoEvent evt) {
		if (clientInfo.isChange()) {
			clientInfo.setHeight(evt.getScreenHeight());
			clientInfo.setWidth(evt.getScreenWidth());
			clientInfo.setIslandscape(evt.isLandscape());
			clientInfo.setTimeZone(evt.getTimeZone());
			clientInfo.setChange(false);
			initUI();
			IndexSetup vdiv = new IndexSetup(divBody);
			vdiv.setParent(divBody);
			Events.echoEvent("onAfterLoaded", vdiv, null);
			desktop.setAttribute("index", this);
			div_nav.addEventListener("onSelectedMenu", this);
			div_nav.addEventListener("onLogout", this);
			div_nav.addEventListener("onRefresh", this);
			div_nav.addEventListener("onLanguage", this);
			div_nav.addEventListener("onCompany", this);
			div_nav.addEventListener("onPopState", this);
			div_nav.addEventListener("onMinifyMenu", this);
			div_nav.addEventListener("onMainSize", this);
			div_nav.addEventListener("onModuleChange", this);
			div_nav.addEventListener("onShowProfile", this);
			div_nav.addEventListener("onShowCompany", this);
			ZKEnv.getEnv().setContext("home_page", this);
		}
	}

	private NotificationScreen notificationScreen;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		ZKEnv.getEnv().setClientInfo(clientInfo);
		divBody = (Div) comp;
		if (!desktop.isServerPushEnabled()) {
			desktop.enableServerPush(true);
		}
		notificationScreen = new NotificationScreen(ZKEnv.getEnv(), divBody);
		divNotifications.setStyle("overflow: auto");
		divBody.addEventListener("onNotification", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				Object data = event.getData();
				if (data != null) {
					lblListNotification.setValue(String.valueOf(data));
					total.setValue("Tất cả (" + String.valueOf(data) + ")");
					divNotifications.getChildren().clear();
					List<Div> notes = notificationScreen.getNotificationDetail();
					for (Div div : notes) {
						div.setParent(divNotifications);
					}
				}
			}
		});
	}

	private int moduleCount = 0;
	private int firstModuleId = 0;

	private void initModuleShortcut() {
		HtmlNativeComponent ul = new HtmlNativeComponent("ul");
		ul.setParent(shortcut_placeholder);
		menus = ZKEnv.getEnv().getMenu();
		ArrayList<String> listColor = new ArrayList<>();
		listColor.add("bg-color-blue");
		listColor.add("bg-color-blueLight");
		listColor.add("bg-color-green");
		listColor.add("bg-color-greenDark");
		listColor.add("bg-color-red");
		listColor.add("bg-color-yellow");
		listColor.add("bg-color-orange");
		listColor.add("bg-color-pink");
		listColor.add("bg-color-darken");
		listColor.add("bg-color-lighten");
		listColor.add("bg-color-grayDark");
		listColor.add("bg-color-magenta");
		listColor.add("bg-color-teal");
		listColor.add("bg-color-redLight");
		listColor.add("bg-color-orangeDark");
		listColor.add("bg-color-blueDark");
		listColor.add("bg-color-greenLight");
		listColor.add("bg-color-pinkDark");
		listColor.add("bg-color-purple");

		for (VObject menu : menus) {
			if (menu.getValue("parentId") == null) {
				moduleCount++;
				firstModuleId = menu.getId();
				HtmlNativeComponent li = new HtmlNativeComponent("li");
				li.setParent(ul);
				HtmlNativeComponent a = new HtmlNativeComponent("a");
				a.setParent(li);
				a.setDynamicProperty("onClick", "changeModule(" + menu.getId() + ")");
				a.setDynamicProperty("href", "#");
				a.setDynamicProperty("class",
						"jarvismetro-tile big-cubes " + listColor.get(new Random().nextInt(listColor.size())));
				HtmlNativeComponent span = new HtmlNativeComponent("span");
				span.setParent(a);
				span.setDynamicProperty("class", "iconbox");
				HtmlNativeComponent i = new HtmlNativeComponent("i");
				i.setParent(span);
				i.setDynamicProperty("class", "fa fa-4x");
				if (menu.getValue("icon") != null) {
					i.setDynamicProperty("class", "fa fa-4x " + menu.getValue("icon").toString());
				} else if (menu.getValue("parentId") == null) {
					i.setDynamicProperty("class", "fa fa-4x fa-folder-o");
				}
				HtmlNativeComponent span2 = new HtmlNativeComponent("span",
						Translate.translate(ZKEnv.getEnv(), null, menu.getValue("name").toString()), null);
				span2.setParent(span);
			}
		}
	}

	private void initLanguageDiv() {
		HtmlNativeComponent ul = new HtmlNativeComponent("ul");
		ul.setParent(divLanguage);
		ul.setDynamicProperty("class", "header-dropdown-list");
		HtmlNativeComponent li = new HtmlNativeComponent("li");
		li.setParent(ul);
		Locale locale = ZKEnv.getEnv().getLocale();
		HtmlNativeComponent a = new HtmlNativeComponent("a");
		a.setDynamicProperty("class", "dropdown-toggle");
		a.setDynamicProperty("data-toggle", "dropdown");
		a.setParent(li);
		HtmlNativeComponent img = new HtmlNativeComponent("img");
		img.setParent(a);
		img.setDynamicProperty("src", "img/blank.gif");
		if (locale.getLanguage().equals("en")) {
			img.setDynamicProperty("class", "flag flag-us");
			img.setDynamicProperty("alt", "United States");
			HtmlNativeComponent span = new HtmlNativeComponent("span", " English (US) ", null);
			span.setParent(a);
		} else if (locale.getLanguage().equals("vn")) {
			img.setDynamicProperty("class", "flag flag-vn");
			img.setDynamicProperty("alt", "Tiếng Việt");
			HtmlNativeComponent span = new HtmlNativeComponent("span", " Tiếng Việt ", null);
			span.setParent(a);
		}
		HtmlNativeComponent i = new HtmlNativeComponent("i");
		i.setDynamicProperty("class", "fa fa-angle-down");
		i.setParent(a);

		ul = new HtmlNativeComponent("ul");
		ul.setDynamicProperty("class", "dropdown-menu pull-right");
		ul.setParent(li);
		// English
		li = new HtmlNativeComponent("li");
		li.setParent(ul);
		a = new HtmlNativeComponent("a", null, "English (US)");
		a.setParent(li);
		a.setDynamicProperty("onClick", "changeLanguate('en')");
		img = new HtmlNativeComponent("img");
		img.setParent(a);
		img.setDynamicProperty("class", "flag flag-us");
		img.setDynamicProperty("alt", "United States");

		// Vietnamese
		li = new HtmlNativeComponent("li");
		li.setParent(ul);
		a = new HtmlNativeComponent("a", null, "Tiếng Việt");
		a.setParent(li);
		a.setDynamicProperty("onClick", "changeLanguate('vn')");
		img = new HtmlNativeComponent("img");
		img.setParent(a);
		img.setDynamicProperty("class", "flag flag-vn");
		img.setDynamicProperty("alt", "Tiếng Việt");
	}

	private void initCompanyDiv() {
		HtmlNativeComponent div = new HtmlNativeComponent("div");
		div.setDynamicProperty("class", "project-context");
		div.setParent(divCompany);
		HtmlNativeComponent span = new HtmlNativeComponent("span", Translate.translate(ZKEnv.getEnv(), null, "Company"),
				null);
		span.setDynamicProperty("class", "label");
		span.setParent(div);
		span = new HtmlNativeComponent("span", ZKEnv.getEnv().getCurrentCompany().getValue("name").toString(), null);
		span.setDynamicProperty("class", "project-selector dropdown-toggle");
		span.setParent(div);
		HtmlNativeComponent i = new HtmlNativeComponent("i");
		i.setDynamicProperty("class", "fa fa-angle-down");
		i.setParent(span);
		span.setDynamicProperty("onClick", "showCompany()");
	}

	private void initUI() {
		// TUANPA
		// ShowNotice notice = new ShowNotice(ZKEnv.getEnv(), this.desktop,
		// this.divBody);
		// notice.start();
		initCompanyDiv();
		initLanguageDiv();
		initModuleShortcut();
		byte[] avatar = (byte[]) ZKEnv.getEnv().user.getValue("avatar");
		Integer rootMenuId = (Integer) ZKEnv.getEnv().user.getValue("current_root_menu_id");

		if (rootMenuId != null) {
			initNavMenu(rootMenuId);
		} else if (moduleCount > 1) {
			IndexSetup vdiv = new IndexSetup(divBody);
			vdiv.setParent(divBody);
			Events.echoEvent("onAfterInitModuleShortcut", vdiv, null);
		} else if (firstModuleId > 0) {
			initNavMenu(firstModuleId);
		}

		AImage decodedimg = null;
		if (avatar != null && avatar.length > 10) {
			try {
				decodedimg = new AImage("img", avatar);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (decodedimg != null) {
			user_avatar.setContent(decodedimg);
		} else {
			user_avatar.setSrc("./themes/images/noimage.png");
		}
		user_avatar.setHeight("32px");
		user_avatar.setHeight("32px");
		username.setValue(ZKEnv.getEnv().user.getValue("name").toString());

		if (isTabStyle) {
			initTabbedView();
		}
	}

	private Tabbox tabbox;
	private Tabs tabs;
	private Tabpanels tabpanels;

	private void initTabbedView() {
		tabbox = new Tabbox();
		tabbox.setParent(divMain);
		tabbox.setHflex("1");
		tabbox.setVflex("1");

		tabs = new Tabs();
		tabs.setParent(tabbox);

		tabpanels = new Tabpanels();
		tabpanels.setParent(tabbox);
	}

	private Collection<VObject> menus;

	private void clearNavMenu() {
		div_nav.getChildren().clear();
	}

	private VObject firstMenu = null;

	private void initNavMenu(Integer root_menu_id) {
		firstMenu = null;
		HtmlNativeComponent nav = new HtmlNativeComponent("nav");
		nav.setParent(div_nav);
		HtmlNativeComponent ul = new HtmlNativeComponent("ul");
		ul.setParent(nav);
		for (VObject menu : menus) {
			if (menu.getValue("parentId") != null
					&& ((VObject) menu.getValue("parentId")).getId().equals(root_menu_id)) {
				rescusiveAddMenu(menu, ul);
			}
		}
		if (firstMenu != null) {
			// onSelectedMenu(firstMenu.getId());
			Clients.evalJavaScript(
					"setTimeout(function() {$('#menu_" + firstMenu.getId() + "').trigger('click');}, 100);");
		}
		VController controller = VEnv.sudo().get("Sys.Menu");
		VObject rootMenu = controller.browse(root_menu_id);
		lbModuleName.setValue(Translate.translate(ZKEnv.getEnv(), null, rootMenu.getValue("name").toString()));

		divShowAllModule.getChildren().clear();
		HtmlNativeComponent span = new HtmlNativeComponent("span");
		span.setParent(divShowAllModule);
		HtmlNativeComponent a = new HtmlNativeComponent("a");
		a.setParent(span);
		a.setDynamicProperty("data-action", "toggleShortcut");
		a.setDynamicProperty("title", "All Module");
		HtmlNativeComponent i = new HtmlNativeComponent("i");
		i.setParent(a);
		if (rootMenu.getValue("icon") != null) {
			i.setDynamicProperty("class", "fa " + rootMenu.getValue("icon").toString());
		} else if (rootMenu.getValue("parentId") == null) {
			i.setDynamicProperty("class", "fa fa-folder-o");
		}
	}

	private void rescusiveAddMenu(VObject menu, Component parentComponent) {
		HtmlNativeComponent li = new HtmlNativeComponent("li");
		String liId = "li_" + RandomUtils.nextInt(1000);
		li.setDynamicProperty("id", liId);
		li.setParent(parentComponent);
		HtmlNativeComponent a = new HtmlNativeComponent("a");
		a.setDynamicProperty("title", Translate.translate(ZKEnv.getEnv(), null, menu.getValue("name").toString()));
		a.setParent(li);
		HtmlNativeComponent i = null;
		// if (menu.getValue("icon") != null) {
		// i = new HtmlNativeComponent("i");
		// i.setDynamicProperty("class", "fa fa-lg fa-fw " +
		// menu.getValue("icon").toString());
		// i.setParent(a);
		// } else if (menu.getValue("parentId") == null) {
		// i = new HtmlNativeComponent("i");
		// i.setDynamicProperty("class", "fa fa-lg fa-fw fa-folder-o");
		// i.setParent(a);
		// }
		// HtmlNativeComponent span = new HtmlNativeComponent("span",
		// Translate.translate(ZKEnv.getEnv(), null,
		// menu.getValue("name").toString()), null);
		// span.setDynamicProperty("class", "menu-item-parent");
		// span.setParent(a);java.util.ArrayList<VObject>

		ArrayList<VObject> childList = new ArrayList<>();
		for (VObject childMenu : menus) {
			if (menu.equals(childMenu.getValue("parentId"))) {
				childList.add(childMenu);
			}
		}
		if (menu.getValue("icon") != null) {
			i = new HtmlNativeComponent("i");
			i.setDynamicProperty("class", "fa fa-lg fa-fw " + menu.getValue("icon").toString());
			i.setParent(a);
		} else if (menu.getValue("parentId") == null || childList.size() > 0) {
			i = new HtmlNativeComponent("i");
			i.setDynamicProperty("class", "fa fa-lg fa-fw fa-folder-o");
			i.setParent(a);
		}
		HtmlNativeComponent span = new HtmlNativeComponent("span",
				Translate.translate(ZKEnv.getEnv(), null, menu.getValue("name").toString()), null);
		span.setDynamicProperty("class", "menu-item-parent");
		span.setParent(a);
		if (childList.size() > 0) {
			HtmlNativeComponent ul = new HtmlNativeComponent("ul");
			ul.setParent(li);
			for (VObject childMenu : childList) {
				rescusiveAddMenu(childMenu, ul);
			}
		} else {
			if (firstMenu == null) {
				firstMenu = menu;
			}
			a.setDynamicProperty("id", "menu_" + menu.getId());
			a.setDynamicProperty("onClick", "selectMenu(" + menu.getId() + ",'" + liId + "')");
		}
	}

	private void onSelectedMenu(Integer id) {
		if (currentWindow != null && currentWindow.isEditing()) {
			String msg = "Discard changed: [";
			msg += currentWindow.controller.getDisplayString(currentWindow.currentId) + ", ";
			msg = msg.substring(0, msg.length() - 2);
			msg += "]";
			Messagebox.show(msg, "Confirm?", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
					new EventListener<Event>() {
						@Override
						public void onEvent(Event event) throws Exception {
							if (event.getName().equals(Messagebox.ON_OK)) {
								doSelectMenu(id);
							}
						}
					});
		} else {
			doSelectMenu(id);
		}
	}

	private void doSelectMenu(Integer id) {
		if (id > 0) {
			VController menuController = VEnv.sudo().get("Sys.Menu");
			VObject action = (VObject) menuController.getValue(id, "action");
			if (action != null) {
				showAction(action);
				queryMap.clear();
				Map<String, String> params = new LinkedHashMap<>();
				params.put("action", action.getId().toString());
				if (currentWindow != null) {
					pushHistory(currentWindow.getTitle(), params);
				}
				else {
					pushHistory(action.getValue("name").toString(), params);
				}
			}
		}
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getName().equals("onSelectedMenu")) {
			JSONObject data = (JSONObject) event.getData();
			Integer id = (Integer) data.get("menuId");
			onSelectedMenu(id);
		} else if (event.getName().equals("onLogout")) {
			ZKEnv.logout();
			Executions.sendRedirect("/login.zul");
		} else if (event.getName().equals("onLanguage")) {
			JSONObject data = (JSONObject) event.getData();
			String language = data.get("language").toString();
			HttpServletResponse reponse = ZKEnv.getHttpServletResponse();
			Cookie cookie = new Cookie("language", language);
			cookie.setMaxAge(Integer.MAX_VALUE);
			reponse.addCookie(cookie);
			Locale locale = new Locale(language);
			ZKEnv.getEnv().setLocale(locale);
			Executions.sendRedirect("");
		} else if (event.getName().equals("onPopState")) {
			JSONObject data = (JSONObject) event.getData();
			String seachString = data.get("search").toString();
			restoreHistory(seachString);
		} else if (event.getName().equals("onMinifyMenu")) {
			Clients.evalJavaScript("window.dispatchEvent(new Event('resize'));");
		} else if (event.getName().equals("onMainSize")) {
			JSONObject data = (JSONObject) event.getData();
			// if (data.get("main_height") instanceof Double) {
			// Double height = (Double) data.get("main_height");
			// mainHeight = height.intValue();
			// } else {
			// mainHeight = (int) data.get("main_height");
			// }
			mainHeight = ((Number) data.get("main_height")).intValue();
			onChangeMainHeight();
		} else if (event.getName().equals("onModuleChange")) {
			JSONObject data = (JSONObject) event.getData();
			Integer root_menu_id = (Integer) data.get("root_menu_id");
			changeModule(root_menu_id);
		} else if (event.getName().equals("onShowProfile")) {
			showProfile();
		} else if (event.getName().equals(Events.ON_CLOSE) && event.getTarget() instanceof Tab) {
			Tab tab = (Tab) event.getTarget();
			VObject action = (VObject) tab.getAttribute("action");
			mapTabs.remove(action);
		} else if (event.getName().equals("onShowCompany")) {
			getCompanySelector().open(event.getTarget(), "at_pointer");
		}
	}

	private Popup selectCompany;

	private Popup getCompanySelector() {
		if (selectCompany == null) {
			selectCompany = new Popup();
			selectCompany.setParent(div_nav);
			selectCompany.setSclass("z-popup-company");
			Div divMainTree = new Div();
			divMainTree.setWidth("200px");
			divMainTree.setParent(selectCompany);
			Tree tree = new Tree();
			tree.setHflex("1");
			tree.setSclass("z-tree-list-company");
			tree.setParent(divMainTree);
			tree.setSizedByContent(false);
			tree.addEventListener(Events.ON_SELECT, EVENT_COMPANY_SELECT);
			loadCompanyTree(tree);
		}
		return selectCompany;
	}

	private EventListener<Event> EVENT_COMPANY_SELECT = new EventListener<Event>() {
		@Override
		public void onEvent(Event event) throws Exception {
			// TODO Auto-generated method stub
			Tree companyTree = (Tree) event.getTarget();
			Treeitem selectedItem = companyTree.getSelectedItem();
			if (selectedItem != null) {
				Integer selectData = selectedItem.getValue();
				if (selectData != null) {
					ZKEnv.getEnv().setCurrentCompany(ZKEnv.getEnv().get(SysCompany.modelName).browse(selectData));
					Executions.sendRedirect("");
				}
			}
		}
	};

	private void loadCompanyTree(Tree tree) {
		VNode vnode = ZKEnv.getEnv().getCompanyTreePermision();
		Integer company = ZKEnv.getEnv().getCurrentCompany().getId();
		Treecols treeCols = new Treecols();
		treeCols.setParent(tree);
		Treecol treeCol = new Treecol();
		treeCol.setParent(treeCols);
		manualRenderTree(tree, vnode, company);

	}

	private void manualRenderTree(Component parent, VNode node, Integer selectedNode) {
		Component parentChildren = parent;
		Treeitem treeItem = null;
		if (!node.isRoot()) {
			treeItem = initTreeItem(node);
			treeItem.setParent(parent);
			parentChildren = treeItem;
		}
		Set<VNode> children = node.getChildrens();
		if (children.size() > 0) {
			Treechildren treeChildren = new Treechildren();
			treeChildren.setParent(parentChildren);
			for (VNode nodeChildren : children) {
				manualRenderTree(treeChildren, nodeChildren, selectedNode);
			}
		}
		if (selectedNode != null && selectedNode.equals(node.getData())) {
			treeItem.setSelected(true);
			showSelectedNode(treeItem);
		}

	}

	private Treeitem initTreeItem(VNode node) {
		Treeitem item = new Treeitem();
		item.setValue(node.getData());
		item.setLabel(node.getLabel());
		item.setOpen(false);
		return item;
	}

	private void showSelectedNode(Treeitem item) {
		Treeitem parenItem = item.getParentItem();
		if (parenItem != null) {
			parenItem.setOpen(true);
			showSelectedNode(parenItem);
		}
	}

	private void showProfile() {
		showAction(null);
		AccountInfo acctInfo = new AccountInfo();
		divMain.appendChild(acctInfo);
	}

	public void showLinkAction(Component viewAction) {
		showAction(null);
		divMain.appendChild(viewAction);
		currentView = viewAction;
		onChangeMainHeight();
	}

	private void changeModule(Integer root_menu_id) {
		VObject user = ZKEnv.getEnv().user;
		if (user != null) {
			Map<String, Object> values = new HashMap<>();
			values.put("current_root_menu_id", root_menu_id);
			VController controller = VEnv.sudo().get("Sys.User");
			ArrayList<Integer> ids = new ArrayList<>();
			ids.add(user.getId());
			controller.update(ids, values);
		}
		clearNavMenu();
		initNavMenu(root_menu_id);
		IndexSetup vdiv = new IndexSetup(divBody);
		vdiv.setParent(divBody);
		Events.echoEvent("onAfterChangeModule", vdiv, null);
	}

	int mainHeight;

	private void onChangeMainHeight() {
		if (isTabStyle) {
			mainHeight -= 52;
			divMain.setHeight(mainHeight + "px");
			divMain.invalidate();
		} else {
			if (currentView != null && currentView instanceof HtmlBasedComponent) {
				HtmlBasedComponent hView = (HtmlBasedComponent) currentView;
				hView.setHeight(mainHeight + "px");
			}
		}
	}

	LinkedHashMap<String, String> queryMap = new LinkedHashMap<>();

	private void restoreHistory(String seachString) {
		queryMap = new LinkedHashMap<>();
		if (seachString.startsWith("?"))
			seachString = seachString.substring(1);
		String[] queries = seachString.split("&");
		for (String query : queries) {
			if (query.indexOf("=") > 0 && query.indexOf("=") < query.length()
					&& query.indexOf("=") == query.lastIndexOf("=")) {
				String[] keyValue = query.split("=");
				queryMap.put(keyValue[0], keyValue[1]);
			}
		}

		if (queryMap.containsKey("action")) {
			String actionStr = queryMap.get("action");
			if (StringAppUtils.isInteger(actionStr)) {
				VController controller = VEnv.sudo().get("Sys.Action");
				VObject action = controller.browse(Integer.parseInt(actionStr));
				if (action != null) {
					showAction(action);
					String viewType = null;
					String currentId = null;
					if (queryMap.containsKey("view_type")) {
						viewType = queryMap.get("view_type");
					}
					if (queryMap.containsKey("id")) {
						currentId = queryMap.get("id");
						if (!StringAppUtils.isInteger(currentId)) {
							currentId = null;
						}
					}
					if (currentWindow != null) {
						if (viewType != null) {
							currentWindow.setViewType(viewType);
						}
						if (currentId != null) {
							currentWindow.setCurrentId(Integer.parseInt(currentId));
						}

					}
				}
			}
		} else {
			// showDashboard();
		}
	}

	String currentState = "";

	public void pushHistory(String title, Map<String, String> queries) {
		queryMap.putAll(queries);
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("?");
		for (String key : queryMap.keySet()) {
			if (strBuf.length() > 1) {
				strBuf.append("&");
			}
			strBuf.append(key + "=").append(queryMap.get(key));
		}
		if (!currentState.equals(strBuf.toString())) {
			currentState = strBuf.toString();
			Clients.evalJavaScript("history.pushState(null, '" + title + "', '" + currentState + "');");
		}
	}

	VWindow currentWindow = null;
	Component currentView = null;

	HashMap<Integer, Tab> mapTabs = new HashMap<>();

	/**
	 * 
	 * @param windowId
	 */
	public void showAction(VObject action) {
		if (!isTabStyle) {
			currentWindow = null;
			currentView = null;
			divMain.getChildren().clear();
			if (action == null) {
				return;
			}
			String windowId = (String) action.getValue("window");
			String viewClass = (String) action.getValue("viewClass");
			try {
				if (windowId != null) {
					VWindow window = new VWindow(VWindowDefine.getWindow(windowId));
					// window.clearFilter(); huannn: bug filter?
					divMain.appendChild(window);
					currentWindow = window;
				} else if (viewClass != null) {
					try {
						Component view = (Component) Class.forName(viewClass).newInstance();
						divMain.appendChild(view);
						currentView = view;
						onChangeMainHeight();
					} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
						e.printStackTrace();
					}
					currentWindow = null;
				}
			} catch (Exception e) {
				Clients.showNotification("Error open action: " + action.toString());
			}
		} else {
			showTab(action);
		}
	}

	private void showTab(VObject action) {
		if (action == null) {
			return;
		}
		// Restore tab
		Tab tab = mapTabs.get(action.getId());
		if (tab != null) {
			tab.setSelected(true);
			return;
		}

		currentWindow = null;
		currentView = null;
		String windowId = (String) action.getValue("window");
		String viewClass = (String) action.getValue("viewClass");
		Component actionComponent = null;
		try {
			if (windowId != null) {
				VWindow window = new VWindow(VWindowDefine.getWindow(windowId));
				actionComponent = window;
				currentWindow = window;
			} else if (viewClass != null) {
				try {
					Component view = (Component) Class.forName(viewClass).newInstance();
					currentView = view;
					actionComponent = view;
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
					e.printStackTrace();
				}
				currentWindow = null;
			}
		} catch (Exception e) {
			Clients.showNotification("Error open action: " + action.toString());
			actionComponent = null;
		}

		if (actionComponent != null) {
			tab = new Tab(Translate.translate(ZKEnv.getEnv(), null, action.getValue("name").toString()));
			tab.setParent(tabs);
			tab.setClosable(true);
			tab.addEventListener(Events.ON_CLOSE, this);
			tab.setAttribute("action", action);

			Tabpanel panel = new Tabpanel();
			panel.setStyle("overflow: auto;");
			panel.setParent(tabpanels);

			actionComponent.setParent(panel);

			if (actionComponent instanceof HtmlBasedComponent) {
				((HtmlBasedComponent) actionComponent).setHeight("100%");
			}
			tab.setSelected(true);
			// put tab on maps
			mapTabs.put(action.getId(), tab);
		}
	}

	// private void showDashboard() {
	// divMain.getChildren().clear();
	// Include include = new Include("dashboard.zul");
	// include.setParent(divMain);
	// }


	public Div getDivNotifications() {
		return divNotifications;
	}

	public void setDivNotifications(Div divNotifications) {
		this.divNotifications = divNotifications;
	}

	public Label getLblListNotification() {
		return lblListNotification;
	}

	public void setLblListNotification(Label lblListNotification) {
		this.lblListNotification = lblListNotification;
	}

	public Desktop getDesktop() {
		return desktop;
	}

	public void setDesktop(Desktop desktop) {
		this.desktop = desktop;
	}

}
