package base.util;

import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Initiator;

import base.controller.VEnv;

public class ModuleAuthenticationMonitorInit implements Initiator {

	public void doInit(Page page, Map<String, Object> args) throws Exception {
		
		VEnv env = ZKEnv.getEnv();
		if(env == null || (!env.isAdmin())){
			ZKEnv.setCurrentLocation("/monitor.zul");
			Executions.sendRedirect("/login.zul");
			return;
		}
	}
}