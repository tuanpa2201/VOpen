/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Jul 19, 2016
* Author: tuanpa
*
*/

package base.util;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WebApp;

import base.controller.VController;
import base.controller.VEnv;
import base.model.VObject;
import base.view.Index;;

public class ZKEnv {
	public static String VENV = "venv";
	public static String HOME_PAGE = "home_page";
	

	public static WebApp WEBAPP = null;
	
	public static WebApp getWebApp() {
		return WEBAPP;
	}
	
	public static void setWebApp(WebApp webApp) {
		WEBAPP = webApp;
	}
	
	public static VEnv getEnv() {
		VEnv venv = null;
		Session session = Sessions.getCurrent();
		if (session != null) {
			venv =(VEnv) session.getAttribute(VENV);
		}
		return venv;
	}
	public static Index getHomePage(){
		return (Index) getEnv().getContext(HOME_PAGE);
	}
	public static void login(VEnv env) {
		Session session = Sessions.getCurrent();
		session.setAttribute(VENV, env);
	}
	
	public static void login(VEnv env, boolean rememberMe) {
		login(env);
		if (rememberMe) {
			VController userTokenController = VEnv.sudo().get("Sys.User.Token");
			
			//Delete old token
			Filter filter = new Filter();
			filter.hqlWhereClause = "user = :user_to_delete";
			filter.params.put("user_to_delete", env.user);
			List<Integer> ids = userTokenController.search(filter);
			userTokenController.delete(ids);
			
			//Generate new token:
			String newToken = RandomUtils.randomString(50);
			Map<String, Object> values = new HashMap<>();
			long expiredDay = ConfigUtil.getConfig("REMEMBER_ME_EXPIRE_DAY", 30);
			Timestamp expireTime = new Timestamp(System.currentTimeMillis() + expiredDay * 24 * 60 * 60 * 1000);
			values.put("token", newToken);
			values.put("user", env.user);
			values.put("expired_time", expireTime);
			
			//Send back token to browser
			HttpServletResponse reponse = ZKEnv.getHttpServletResponse();
			Cookie cookie = new Cookie("token", newToken);
			cookie.setMaxAge(Integer.MAX_VALUE);
			reponse.addCookie(cookie);
			userTokenController.create(values);
		}
	}
	
	public static VEnv login(String token) {
		VController userTokenController = VEnv.sudo().get("Sys.User.Token");
		VEnv env = null;
		//Delete old token
		Filter filter = new Filter();
		filter.hqlWhereClause = "token = :token_to_check";
		filter.params.put("token_to_check", token);
		List<Integer> ids = userTokenController.search(filter);
		if (ids.size() > 0) {
			VObject userToken = userTokenController.browse(ids.get(0));
			Timestamp expired_time = (Timestamp) userToken.getValue("expired_time");
			if (expired_time.after(new Timestamp(System.currentTimeMillis()))) {
				VObject user = (VObject) userToken.getValue("user");
				env = VEnv.login(user);
				login(env);
				//Delete old token
				filter = new Filter();
				filter.hqlWhereClause = "user = :user_to_delete";
				filter.params.put("user_to_delete", env.user);
				ids = userTokenController.search(filter);
				userTokenController.delete(ids);
				
				//Generate new token:
				String newToken = RandomUtils.randomString(50);
				Map<String, Object> values = new HashMap<>();
				long expiredDay = ConfigUtil.getConfig("REMEMBER_ME_EXPIRE_DAY", 30);
				Timestamp expireTime = new Timestamp(System.currentTimeMillis() + expiredDay * 24 * 60 * 60 * 1000);
				values.put("token", newToken);
				values.put("user", env.user);
				values.put("expired_time", expireTime);
				
				//Send back token to browser
				HttpServletResponse reponse = ZKEnv.getHttpServletResponse();
				Cookie cookie = new Cookie("token", newToken);
				cookie.setMaxAge(Integer.MAX_VALUE);
				reponse.addCookie(cookie);
				userTokenController.create(values);
			}
		}
		return env;
	}
	
	public static void logout() {
		Session session = Sessions.getCurrent();
		session.removeAttribute(VENV);
		HttpServletResponse reponse = ZKEnv.getHttpServletResponse();
		Cookie cookie = new Cookie("token", "");
		cookie.setMaxAge(0);
		reponse.addCookie(cookie);
	}
	
	public static HttpServletResponse getHttpServletResponse() {
		if (Executions.getCurrent() == null)
			return null;
		return (HttpServletResponse) Executions.getCurrent().getNativeResponse();
	}
	
	public static HttpServletRequest getHttpServletRequest() {
		if (Executions.getCurrent() == null)
			return null;
		return (HttpServletRequest) Executions.getCurrent().getNativeRequest();
	}
	public static void setCurrentLocation(String location) {
		if (location.equals("/?")) {
			location = "/";
		}
		Executions.getCurrent().getSession().setAttribute("current_location", location);
	}
	
	public static String getCurrenLocation() {
		String retVal = (String) Executions.getCurrent().getSession().getAttribute("current_location");
		if (retVal == null) {
			retVal = "/";
		}
		return retVal;
	}
}
