package base.util;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Initiator;

import base.controller.VEnv;

public class AuthenticationInit implements Initiator {

	public void doInit(Page page, Map<String, Object> args) throws Exception {
		
		VEnv env = ZKEnv.getEnv();
		if (env == null) {
			String token = null;
			if (ZKEnv.getHttpServletRequest() != null) {
				HttpServletRequest request = ZKEnv.getHttpServletRequest();
				if (request != null && request.getCookies() != null) {
					for (Cookie cookie : request.getCookies()) {
						if (cookie.getName().equals("token")) {
							token = cookie.getValue();
							env = ZKEnv.login(token);
						}
					}
				}
			}
		}
		
		if(env == null){
			String currentLocation = "/?";
			for (String param : Executions.getCurrent().getParameterMap().keySet()) {
				if (currentLocation.length() > 2) {
					currentLocation += "&";
				}
				currentLocation += param + "=" + Executions.getCurrent().getParameter(param);
			}
			ZKEnv.setCurrentLocation(currentLocation);
			Executions.sendRedirect("/login.zul");
			return;
		}
	}
}