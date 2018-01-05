/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Jul 19, 2016
* Author: tuanpa
*
*/
package base.view;

import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlNativeComponent;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import base.controller.VEnv;
import base.model.VObject;
import base.util.AuthenticationService;
import base.util.Translate;
import base.util.ZKEnv;
import modules.sys.view.DialogNotification;

public class LoginController extends SelectorComposer<Component> implements EventListener<Event> {
	private static final long serialVersionUID = 1L;

	@Wire
	private Div main;
	@Wire
	private Div divMain;
	@Wire
	private Textbox tbUser;
	@Wire
	private Textbox tbPass;
	@Wire
	private Button btnLogin;
	@Wire
	Div divLanguage;
	@Wire
	Label lbllogin;
	@Wire
	Label lbluserName;
	@Wire
	Label lblPass;
	@Wire
	Label lblfoget;
	
	@Wire
	Checkbox cbRememberMe;
	// services
	AuthenticationService authService = new AuthenticationService();
	VObject user;
	private VEnv venv = new VEnv();

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		btnLogin.addEventListener(Events.ON_CLICK, this);
		tbUser.addEventListener(Events.ON_OK, this);
		tbPass.addEventListener(Events.ON_OK, this);
		Events.echoEvent("focus", tbUser, null);
		divLanguage.addEventListener("onLanguage", this);
		initLanguageDiv();
		initUI();
	}

	private void initUI() {
		DialogNotification notice = new DialogNotification(venv, this.divMain.getParent(), true);
		notice.show();
		btnLogin.setLabel(Translate.translate(venv, null, "Sign In"));
		lbllogin.setValue(Translate.translate(venv, null, "Login"));
		lbluserName.setValue(Translate.translate(venv, null, "UserName"));
		lblPass.setValue(Translate.translate(venv, null, "PassWord"));
		lblfoget.setValue(Translate.translate(venv, null, "Foget Password") + " ?");
		cbRememberMe.setLabel(Translate.translate(venv, null, "Remember Me"));
	}

	private void initLanguageDiv() {
		HtmlNativeComponent ul = new HtmlNativeComponent("ul");
		ul.setParent(divLanguage);
		ul.setDynamicProperty("class", "header-dropdown-list");
		HtmlNativeComponent li = new HtmlNativeComponent("li");
		li.setParent(ul);
		Locale locale = new Locale("vn");
		// HttpServletRequest request = ZKEnv.getHttpServletRequest();
		// if (request.getCookies() != null) {
		// for (Cookie cookie : request.getCookies()) {
		// if (cookie.getName().equals("language")) {
		// String language = cookie.getValue();
		// locale = new Locale(language);
		// }
		// }
		// }
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
		venv.setLocale(locale);
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
		a.setDynamicProperty("onClick", "changeLanguate_login('en')");
		img = new HtmlNativeComponent("img");
		img.setParent(a);
		img.setDynamicProperty("class", "flag flag-us");
		img.setDynamicProperty("alt", "United States");

		// Vietnamese
		li = new HtmlNativeComponent("li");
		li.setParent(ul);
		a = new HtmlNativeComponent("a", null, "Tiếng Việt");
		a.setParent(li);
		a.setDynamicProperty("onClick", "changeLanguate_login('vn')");
		img = new HtmlNativeComponent("img");
		img.setParent(a);
		img.setDynamicProperty("class", "flag flag-vn");
		img.setDynamicProperty("alt", "Tiếng Việt");
	}

	public void doLogin() {
		btnLogin.setDisabled(true);
		String nm = tbUser.getValue();
		String pd = tbPass.getValue();
		if (nm == "" || pd == "") {
			Clients.showNotification("Chưa nhập đủ thông tin đăng nhập", "error", null, "middle_center", 2000, true);
			btnLogin.setDisabled(false);
		} else {
			// SysUser user = authService.login(nm, pd);
			user = authService.login(nm, pd, cbRememberMe.isChecked());
			if (user == null) {
				String message = "Thông tin đăng nhập không đúng";
				Clients.showNotification(message, "error", null, "middle_center", 3000, true);
				btnLogin.setDisabled(false);
			} else {
				if (!user.getIsActive()) {
					String message = "Tài khoản của bạn đã bị khóa. Liên lạc với admin để biết thêm chi tiết";
					Clients.showNotification(message, "error", null, "middle_center", 3000, true);
					btnLogin.setDisabled(false);
				} else {
					Executions.sendRedirect(ZKEnv.getCurrenLocation());
				}
			}
		}
		ZKEnv.setWebApp(btnLogin.getDesktop().getWebApp());
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if ((event.getName().equals(Events.ON_CLICK) && event.getTarget() == btnLogin)
				|| (event.getName().equals(Events.ON_OK)
						&& ((event.getTarget() == tbPass) || (event.getTarget() == tbUser)))) {
			doLogin();
		} else if (event.getName().equals("onLanguage")) {
			JSONObject data = (JSONObject) event.getData();
			String language = data.get("language").toString();
			HttpServletResponse reponse = ZKEnv.getHttpServletResponse();
			Cookie cookie = new Cookie("language", language);
			cookie.setMaxAge(Integer.MAX_VALUE);
			reponse.addCookie(cookie);
			Executions.sendRedirect("");
		}
	}
}
