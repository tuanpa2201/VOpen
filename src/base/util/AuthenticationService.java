/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Jul 19, 2016
* Author: tuanpa
*
*/
package base.util;

import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import base.controller.VEnv;
import base.model.VObject;

public class AuthenticationService {

	public VObject login(String name, String pass, boolean rememberMe) {
		VEnv env = VEnv.login(name, pass);
		if (env != null) {
			if (ZKEnv.getHttpServletRequest() != null) {
				HttpServletRequest request = ZKEnv.getHttpServletRequest();
				if (request != null && request.getCookies() != null) {
					for (Cookie cookie : request.getCookies()) {
						if (cookie.getName().equals("language")) {
							String language = cookie.getValue();
							Locale locale = new Locale(language);
							env.setLocale(locale);
						}
					}
				}
			}
			ZKEnv.login(env, rememberMe);
		}
		return env == null ? null : env.user;
	}
	
	public VObject login(String name, String pass) {
		return login(name, pass, false);
	}

	public void logout() {
		ZKEnv.logout();
	}
	
	public boolean quickLogin(String name, String pwd){
		VObject user = (VObject) VEnv.sudo().get("Sys.User").execute("login", name, pwd);
		if (user != null) {
			return true;
		} else{
			return false;
		}
	}
}