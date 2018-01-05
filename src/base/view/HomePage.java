/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Jul 19, 2016
* Author: tuanpa
*
*/
package base.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlNativeComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Navbar;
import org.zkoss.zkmax.zul.Navitem;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;

import base.controller.VController;
import base.controller.VEnv;
import base.model.VObject;
import base.util.AuthenticationService;
import base.util.ZKEnv;

public class HomePage extends SelectorComposer<Component> implements EventListener<Event> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4190744877618873422L;

	final static Logger logger = Logger.getLogger("LOG_USER_ACTION");
	private int unknow = 0;
	@Wire
	Div div_content;
	@Wire
	Navbar nav_bar_left;
	@Wire
	Tabbox tbMain;
	@Wire
	Div divTab;
	@Wire
	Menupopup menuPopupTop;
	@WireVariable
	private Desktop desktop;

	private List<VObject> lstTreeMenu;
	List<VObject> lstMenuPermission = new ArrayList<>();
	private static HomePage instance = null;

	public static HomePage getInstance() {
		return instance;
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		initUI();
	}

	private void initUI() {
		initUITopMenu();
	}

	private void initUITopMenu() {
		HtmlNativeComponent div_header = new HtmlNativeComponent("header");
		div_header.setDynamicProperty("sclass",
				"demo-header mdl-layout__header mdl-color--grey-100 mdl-color-text--grey-600 mdl-layout__header_edit");
		div_header.setParent(div_content);
		HtmlNativeComponent div1_nav_top_menu = new HtmlNativeComponent("div");
		div1_nav_top_menu.setDynamicProperty("sclass", "mdl-layout__header-row mdl-layout__header-row_edit");
		div1_nav_top_menu.setParent(div_header);
		HtmlNativeComponent div2_nav_top_menu = new HtmlNativeComponent("div");
		div2_nav_top_menu.setDynamicProperty("sclass", "android-navigation-container");
		div2_nav_top_menu.setParent(div1_nav_top_menu);
		Navbar nav_bar = new Navbar();
		nav_bar.setZclass("none");
		nav_bar.setSclass("android-navigation mdl-navigation mdl-navigation");
		div2_nav_top_menu.appendChild(nav_bar);

		// text search
		HtmlNativeComponent div_text_search = new HtmlNativeComponent("div");
		div_text_search.setDynamicProperty("sclass", "top_menu_search_box");
		Textbox textSearch = new Textbox();
		textSearch.setPlaceholder("TÃ¬m kiáº¿m ...");
		textSearch.setSclass("top_search_box");
		div_text_search.appendChild(textSearch);
		div_text_search.setParent(div1_nav_top_menu);

		initUIParentSysmenu(nav_bar, div_text_search);

		// menu responsive
		Button btn_menu = new Button();
		btn_menu.setId("bt_menu");
		btn_menu.setSclass("btn_menu_reponsive");
		btn_menu.setImage("images/ico_list_menu.png");
		btn_menu.setParent(div_text_search);
		btn_menu.setPopup("sys_menu_top, after_end");
		// User info
		HtmlNativeComponent div_user_info = new HtmlNativeComponent("div");
		div_user_info.setDynamicProperty("sclass", "top_menu_user_info");
		div_user_info.setParent(div_header);
		HtmlNativeComponent div_user_info_content = new HtmlNativeComponent("div");
		div_user_info_content.setDynamicProperty("sclass", "top_menu_user_info_content");
		div_user_info_content.setParent(div_user_info);

		HtmlNativeComponent div_img_fadeOut = new HtmlNativeComponent("div");
		div_img_fadeOut.setDynamicProperty("sclass", "div_img_fadeOut");
		div_img_fadeOut.setParent(div_user_info);
		HtmlNativeComponent img_fadeOut = new HtmlNativeComponent("img");
		img_fadeOut.setDynamicProperty("src", "images/arr_right.png");
		img_fadeOut.setParent(div_img_fadeOut);
		img_fadeOut.setDynamicProperty("onClick", "showHideLeftMenu('1')");

		HtmlNativeComponent div_img_fadeIn = new HtmlNativeComponent("div");
		div_img_fadeIn.setDynamicProperty("sclass", "div_img_fadeIn");
		div_img_fadeIn.setParent(div_user_info);
		HtmlNativeComponent img_fadeIn = new HtmlNativeComponent("img");
		img_fadeIn.setDynamicProperty("src", "images/arr_left.png");
		img_fadeIn.setParent(div_img_fadeIn);
		img_fadeIn.setDynamicProperty("onClick", "showHideLeftMenu('2')");

		// account info
		HtmlNativeComponent div_menu_right = new HtmlNativeComponent("div");
		div_menu_right.setId("account_info");
		div_menu_right.setDynamicProperty("sclass", "div_user_account_info");
		div_menu_right.setParent(div_user_info);
		HtmlNativeComponent div_menu_right_content = new HtmlNativeComponent("div");
		div_menu_right_content.setId("account_setting");
		div_menu_right_content.setParent(div_menu_right);
		Menupopup menuPopup = new Menupopup();
		menuPopup.setId("menu_user_setting");
		menuPopup.setParent(div_menu_right_content);
		Menuitem menuItemAccountInfo = new Menuitem();
		menuItemAccountInfo.setId("mnu_account_info");
		menuItemAccountInfo.setLabel("ThÃ´ng tin tÃ i khoáº£n");
		menuItemAccountInfo.setParent(menuPopup);
		Menuitem menuItemLogout = new Menuitem();
		menuItemLogout.setId("mnu_logout");
		menuItemLogout.setLabel("Ä�Äƒng xuáº¥t");
		menuItemLogout.setParent(menuPopup);
		HtmlNativeComponent div_btn_menu_popup = new HtmlNativeComponent("div");
		div_btn_menu_popup.setDynamicProperty("sclass", "btn_group btn-group-xs");
		Label labelUserName = new Label();
		labelUserName.setValue(ZKEnv.getEnv().user.getValue("username").toString());
		labelUserName.setParent(div_menu_right_content);
		Button btn_account_info = new Button();
		btn_account_info.setDir("reverse");
		btn_account_info.setId("btn_user_menu");
		btn_account_info.setSclass("btn-success btn_account_menu_top");
		btn_account_info.setPopup("menu_user_setting, after_end");
		btn_account_info.setImage("images/arr_down.png");
		btn_account_info.setParent(div_menu_right_content);

		// Content
		initUIContent(div_content);
	}

	private void initUIContent(Component parent) {
		HtmlNativeComponent div_left_content = new HtmlNativeComponent("div");
		div_left_content.setDynamicProperty("sclass",
				"demo-drawer mdl-layout__drawer mdl-color--white-grey-900 mdl-color-text--blue-grey-50");
		div_left_content.setParent(parent);
		HtmlNativeComponent div_button_responsive = new HtmlNativeComponent("div");
		div_button_responsive.setDynamicProperty("sclass", "mdl-layout__drawer-button mdl-layout__drawer-button_edit");
		div_button_responsive.setParent(div_left_content);
		HtmlNativeComponent img = new HtmlNativeComponent("img");
		img.setDynamicProperty("src", "themes/images/menubg.png");
		img.setParent(div_button_responsive);
		HtmlNativeComponent div_logo = new HtmlNativeComponent("header");
		div_logo.setDynamicProperty("sclass", "demo-drawer-header header_logo");
		div_logo.setParent(div_left_content);

		HtmlNativeComponent img_logo = new HtmlNativeComponent("img");
		img_logo.setDynamicProperty("sclass", "demo-avatar");
		img_logo.setDynamicProperty("src", "images/logo.png");
		img_logo.setParent(div_logo);
		HtmlNativeComponent div_separator = new HtmlNativeComponent("div");
		div_separator.setDynamicProperty("sclass", "right_menu_seprator_horizontal");
		div_separator.setParent(div_left_content);
		nav_bar_left = new Navbar();
		nav_bar_left.setZclass("none");
		nav_bar_left.setSclass("demo-navigation mdl-navigation mdl-navigation_left_menu mdl-color--white-grey-800");
		nav_bar_left.setParent(div_left_content);

		// Main content
		HtmlNativeComponent main_content = new HtmlNativeComponent("main");
		main_content.setDynamicProperty("sclass", "mdl-layout__content mdl-color--grey-100");
		main_content.setParent(parent);
		divTab = new Div();
		divTab.setId("divTab");
		divTab.setParent(main_content);
		tbMain = new Tabbox();
		tbMain.setId("tbMain");
		tbMain.setParent(divTab);
		tbMain.setHeight("1000px");
		Tabs tab = new Tabs();
		tab.setId("tabs");
		tab.setParent(tbMain);
		Tabpanels tbPanels = new Tabpanels();
		tbPanels.setParent(tbMain);
		Tabpanel tbpanel = new Tabpanel();
		tbpanel.setStyle("overflow:auto");
		HtmlNativeComponent div_tab_main_content = new HtmlNativeComponent("div");
		div_tab_main_content.setParent(tbpanel);

	}

	private void initUIParentSysmenu(Component parent, Component parent2) {
		VController controller = VEnv.sudo().get("Sys.Menu");
		Map<Integer, VObject> lstModel = controller.browse(controller.searchByString(null, "", 0));
		lstTreeMenu = new ArrayList<>();
		lstTreeMenu.addAll(lstModel.values());

		List<VObject> lstRoot = new ArrayList<VObject>();
		for (VObject sysMenu : lstTreeMenu) {
			if (sysMenu.getValue("parentId") == null || sysMenu.getValue("parentId").equals(0)) {
				lstRoot.add(sysMenu);
			}
		}
		menuPopupTop = new Menupopup();
		menuPopupTop.setId("sys_menu_top");
		menuPopupTop.setParent(parent2);
		for (VObject sysMenu : lstRoot) {
			if (sysMenu.getIsActive()) {
				Navitem item = new Navitem();
				item.setZclass("none");
				item.setLabel(sysMenu.getValue("name").toString());
				item.setAttribute("sysMenu", sysMenu);
				item.setSclass("mdl-navigation__link");
				item.addEventListener(Events.ON_CLICK, this);
				item.setParent(parent);
				Menuitem menuItemAccountInfo = new Menuitem();
				menuItemAccountInfo.setAttribute("sysMenu", sysMenu);
				menuItemAccountInfo.setLabel(sysMenu.getValue("name").toString());
				menuItemAccountInfo.setParent(menuPopupTop);
				menuItemAccountInfo.addEventListener(Events.ON_CLICK, this);
			}
		}
	}

	private void createChildLeftNabar(List<VObject> lstModel, Navitem parent, VObject sysMenu) {
		List<VObject> lstChild = new ArrayList<VObject>();
		for (VObject child : lstModel) {
			if (child.getValue("parentId") != null && child.getValue("parentId").equals(sysMenu)) {
				lstChild.add(child);
			}
		}
		if (lstChild.isEmpty()) {
			Navitem item = new Navitem();
			item.setLabel(sysMenu.getValue("name").toString());
			item.setAttribute("action", sysMenu.getValue("action"));
			item.addEventListener(Events.ON_CLICK, this);
			if (parent == null) {
				item.setId("nb_item" + unknow);
				unknow++;
				item.setParent(nav_bar_left);
			} else {
				item.setStyle("font-style: italic; padding-left:20px;");
				item.setParent(nav_bar_left);
			}
		} else {
			Navitem parentTmp = new Navitem();
			parentTmp.setLabel(sysMenu.getValue("name").toString());
			parentTmp.setAttribute("action", sysMenu.getValue("action"));
			parentTmp.addEventListener(Events.ON_CLICK, this);
			if (sysMenu.getValue("parentId") != null) {
				if (parent == null) {
					parentTmp.setId("nb_item" + unknow);
					unknow++;
					parentTmp.setParent(nav_bar_left);
				} else {
					parentTmp.setParent(nav_bar_left);
				}
				parentTmp.setStyle("font-weight: bold !important;");
			}
			for (VObject child : lstChild) {
				this.createChildLeftNabar(lstModel, parentTmp, child);
			}
		}
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getTarget() instanceof Navitem) {
			VObject sysMenu = (VObject) event.getTarget().getAttribute("sysMenu");
			if (sysMenu != null) {
				if (nav_bar_left.getChildren() != null) {
					nav_bar_left.getChildren().clear();
				}
				this.createChildLeftNabar(lstTreeMenu, null, sysMenu);
			}
			VObject action = (VObject) event.getTarget().getAttribute("action");
			if (action != null)
				showAction(action);
		} else if (event.getTarget() instanceof Menuitem) {
			VObject sysMenu = (VObject) event.getTarget().getAttribute("sysMenu");
			if (sysMenu != null) {
				if (nav_bar_left.getChildren() != null) {
					nav_bar_left.getChildren().clear();
				}
				this.createChildLeftNabar(lstTreeMenu, null, sysMenu);
			}
			VObject action = (VObject) event.getTarget().getAttribute("action");
			if (action != null)
				showAction(action);
		}
	}

	@Listen("onClick=#mnu_account_info")
	public void showAccountInfo() {
	}

	@Listen("onClick=#mnu_logout")
	public void logout() {
		AuthenticationService authService = new AuthenticationService();
		authService.logout();
		Executions.sendRedirect("/");
	}

	private boolean showTab(String uuid) {
		boolean isNew = true;
		List<Tab> lstTabs = tbMain.getTabs().getChildren();
		for (Tab tab : lstTabs) {
			if (tab.getValue().equals(uuid)) {
				tbMain.setSelectedTab(tab);
				isNew = false;
				break;
			}
		}
		return !isNew;
	}

	/**
	 * 
	 * @param component
	 */
	private void addTab(String title, String uuid, Component component) {
		Tab tab = new Tab();
		tab.setValue(uuid);
		tab.setLabel(title);
		tab.setSelected(true);
		tab.setClosable(true);
		tbMain.getTabs().appendChild(tab);
		Tabpanel tabPanel = new Tabpanel();
		tabPanel.appendChild(component);
		tbMain.getTabpanels().appendChild(tabPanel);
	}

	/**
	 * 
	 * @param windowId
	 */
	private void showAction(VObject action) {
		String windowId = (String) action.getValue("window");
		if (windowId != null) {
			if (showTab(windowId))
				return;
			if (VWindowDefine.getWindow(windowId) != null) {
				VWindow window = new VWindow(VWindowDefine.getWindow(windowId));
				addTab(window.getWinDef().title, windowId, window);
			} else {
				this.showNotification("Window not definded: " + windowId, Clients.NOTIFICATION_TYPE_WARNING);
			}
		}
	}
	
	public Desktop getDesktop() {
		return desktop;
	}

	public void setDesktop(Desktop desktop) {
		this.desktop = desktop;
	}

	public void showNotification(String msg, String type) {
		Clients.showNotification(msg, type, null, "bottom_right", 3000);
	}

	public void showNotificationErrorSelect(String msg, String type) {
		Clients.showNotification(msg, type, null, "middle_center", 3000);
	}

	public void showValidateForm(String msg, String type) {
		Clients.showNotification(msg, type, null, "middle_center", 30000, true);
	}

}
