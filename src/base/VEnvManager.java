package base;

import java.sql.Timestamp;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import base.controller.VEnv;
import base.util.ConfigUtil;

public class VEnvManager {
	static ConcurrentHashMap<String, VEnvItem> allEnv = new ConcurrentHashMap<>();
	private static ScheduledExecutorService scheduler;
	public static VEnv getEnv(String sessionKey) {
		VEnv env = null;
		VEnvItem item = allEnv.get(sessionKey);
		if (item != null && item.object != null) {
			item.object.setLastTimeUsed(new Timestamp(System.currentTimeMillis()));
			env = item.object;
		}
		return env;
	}
	public static void setEnv(String sessionKey, VEnv env) {
		if (scheduler == null) {
			scheduler = Executors.newScheduledThreadPool(1);
			scheduler.scheduleWithFixedDelay(new CleanUpSession(), 60, 60, TimeUnit.SECONDS);
		}
		VEnvItem item = new VEnvItem(env);
		allEnv.put(sessionKey, item);
	}
	public static void removeEnv(String sessionKey) {
		allEnv.remove(sessionKey);
	}
}

class CleanUpSession implements Runnable {
	@Override
	public void run() {
		Object[] keys = VEnvManager.allEnv.keySet().toArray();
		for (Object key : keys) {
			VEnvItem item = VEnvManager.allEnv.get(key);
			if (item!= null && item.isReadyToRelease()) {
				VEnvManager.allEnv.remove(key);
			}
		}
	}
}


class VEnvItem {
	public final int timeout = (ConfigUtil.getConfig("SESSION_TIMEOUT", 30) * 60 * 1000);
	VEnv object;
	public VEnvItem(VEnv object) {
		this.object = object;
	}
	
	public boolean isReadyToRelease() {
		boolean retVal = false;
		long delta = System.currentTimeMillis() - object.getLastTimeUsed().getTime();
		if (delta > timeout)
			retVal = true;
		return retVal;
	}
}