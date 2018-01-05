/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 19, 2016
* Author: tuanpa
*
*/
package base.util;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import base.model.VObject;

public class VCache {
	protected static ConcurrentHashMap<String, Map<String, VCacheItem>> cacheHashMap = new ConcurrentHashMap<>();
	private static ScheduledExecutorService scheduler;
	public static void put(String cacheName, String key, VObject value) {
		if (scheduler == null) {
			scheduler = Executors.newScheduledThreadPool(1);
			scheduler.scheduleWithFixedDelay(new CleanUpCache(), 60, 60, TimeUnit.SECONDS);
		}
		Map<String, VCacheItem> map = cacheHashMap.get(cacheName);
		if (map == null) {
			map = new ConcurrentHashMap<>();
			cacheHashMap.put(cacheName, map);
		}
		VCacheItem item = new VCacheItem(new Timestamp(System.currentTimeMillis()), value);
		map.put(key, item);
	}
	
	public static VObject get(String cacheName, String key) {
		VObject retVal = null;
		Map<String, VCacheItem> map = cacheHashMap.get(cacheName);
		if (map == null) {
			map = new ConcurrentHashMap<>();
			cacheHashMap.put(cacheName, map);
		}
		VCacheItem item = map.get(key);
		if (item != null) {
			retVal = item.object;
			item.updateTime();
		}
		return retVal;
	}
	
	public static void remove(String cacheName, String key){
		Map<String, VCacheItem> map = cacheHashMap.get(cacheName);
		if (map == null) {
			map = new ConcurrentHashMap<>();
			cacheHashMap.put(cacheName, map);
		}
		map.remove(key);
	}
	
	public static void refresh(String cacheName) {
		Map<String, VCacheItem> map = cacheHashMap.get(cacheName);
		if (map != null)
			map.clear();
	}
	
	public static void refreshAll() {
		cacheHashMap.clear();
	}
}

class CleanUpCache implements Runnable {
	@Override
	public void run() {
		for (String modelName : VCache.cacheHashMap.keySet()) {
			Map<String, VCacheItem> map = VCache.cacheHashMap.get(modelName);
			Object[] keys = map.keySet().toArray();
			for (Object key : keys) {
				VCacheItem item = map.get(key);
				if (item!= null && item.isReadyToRelease()) {
					map.remove(key);
				}
			}
		}
	}
}

class VCacheItem {
	public final int timeout = (ConfigUtil.getConfig("CACHE_TIMEOUT", 30) * 60 * 1000);
	Timestamp time;
	VObject object;
	public VCacheItem(Timestamp time, VObject object) {
		this.time = time;
		this.object = object;
	}
	
	public void updateTime() {
		this.time = new Timestamp(System.currentTimeMillis());
	}
	
	public boolean isReadyToRelease() {
		boolean retVal = false;
		long delta = System.currentTimeMillis() - this.time.getTime();
		if (delta > timeout)
			retVal = true;
		return retVal;
	}
}
