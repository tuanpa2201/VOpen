/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 1, 2016
* Author: tuanpa
*
*/
package base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.reflections.Reflections;

import base.controller.VController;
import base.controller.VEnv;
import base.model.VObject;
import base.util.Translate;

public class VModuleManager {

	/**
	 * @return: Cac module duoc cai dat
	 */
	static public ArrayList<VModuleDefine> getModuleInstalled() {
		ArrayList<VModuleDefine> retVal = new ArrayList<>();
		for (VModuleDefine modDef : listModule) {
			VObject modDB = modDef.getModuleDB();
			if (modDB != null && (boolean) modDB.getValue("isInstalled")) {
				retVal.add(modDef);
			}
		}
		return retVal;
	};

	private static VController getController() {
		VController controller = VEnv.sudo().get("base.module");
		return controller;
	}

	// static private VController controller = VEnv.sudo().get("base.module");

	static private List<VModuleDefine> listModule;

	/**
	 * Get all modules avaiable, check status, install autoinstall module
	 */
	static public void getActiveModules() {
		listModule = new ArrayList<>();
		Reflections reflection = new Reflections("modules");
		Set<Class<? extends VModuleDefine>> modules = reflection.getSubTypesOf(VModuleDefine.class);
		try {
			for (Class<? extends VModuleDefine> moduleClass : modules) {
				VModuleDefine moduleDef = moduleClass.newInstance();
				moduleDef.getModuleDB();
				boolean isFound = false;
				List<VModuleDefine> listModule2 = new ArrayList<>();
				listModule2.addAll(listModule);
				for (int i = 0; i < listModule2.size(); i ++) {
					VModuleDefine module = listModule2.get(i);
					if (module.getDependencyModule().contains(moduleDef)) {
						isFound = true;
						int index_current = listModule.indexOf(moduleDef);
						if (i < index_current) {
							listModule.remove(moduleDef);
							listModule.add(i, moduleDef);
						}
						else if (index_current == -1) {
							listModule.add(i, moduleDef);
						}
						break;
					}
				}
				if (!isFound) {
					listModule.add(moduleDef);
				}
				System.out.println(listModule);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static public List<VModuleDefine> getAllModules() {
		return listModule;
	}

	static public void loadBaseLocale() {
		String newURL = Thread.currentThread().getContextClassLoader().getResource("base/locale").getPath();
		File dir = new File(newURL);
		for (File localFile : dir.listFiles()) {
			if (localFile.isFile()) {
				try {
					Locale locale = new Locale(localFile.getName());
					BufferedReader reader = new BufferedReader(new FileReader(localFile));
					String line = null;

					HashMap<String, String> locales = Translate.mapLocale.get(locale);
					if (locales == null) {
						locales = new HashMap<>();
						Translate.mapLocale.put(locale, locales);
					}

					while ((line = reader.readLine()) != null) {
						if (line.indexOf("=") > 0) {
							String key = line.substring(0, line.indexOf("=")).toLowerCase();
							String value = line.substring(line.indexOf("=") + 1);
							if (key.length() > 0 && value.length() > 0) {
								locales.put(key, value);
							}
						}
					}
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	static public void validateModules() {
		loadBaseLocale();
		VModuleManager.getActiveModules();
		for (VModuleDefine modDef : listModule) {
			VObject modDB = modDef.getModuleDB();
			if (modDB != null && modDB.getValue("isInstalled") != null && (boolean) modDB.getValue("isInstalled")) {
				modDef.startup();
			} else if (modDef.isAutoInstall() && !modDef.isInstalled()) {
				installModule(modDef);
			}
		}
	}

	static public boolean isInstalled(VModuleDefine molDef) {
		VObject modb = molDef.getModuleDB();
		if (modb != null) {
			return (boolean) modb.getValue("isInstalled");
		}
		return false;
	}

	static public void installModule(VModuleDefine modDef) {
		if (modDef.isInstalled())
			return;
		// Install module
		for (VModuleDefine depenModule : modDef.getDependencyModule()) {
			if (!depenModule.isInstalled()) {
				VModuleManager.installModule(depenModule);
			}
		}
		modDef.install();
		// update DB
		VObject module = modDef.getModuleDB();
		HashMap<String, Object> values = new HashMap<>();
		List<Integer> ids = new ArrayList<>();
		ids.add(module.getId());
		values.put("isInstalled", true);
		getController().update(ids, values);
	}

	static public void uninstallModule(VModuleDefine modDef) {
		// Uninstall module
		modDef.uninstall();
		// update DB
		VObject module = modDef.getModuleDB();
		HashMap<String, Object> values = new HashMap<>();
		List<Integer> ids = new ArrayList<>();
		ids.add(module.getId());
		values.put("isInstalled", false);
		getController().update(ids, values);
	}

	static public void upgradeModule(VModuleDefine modDef) {
		modDef.upgrade();
		VObject module = modDef.getModuleDB();
		HashMap<String, Object> values = new HashMap<>();
		List<Integer> ids = new ArrayList<>();
		ids.add(module.getId());
		values.put("isInstalled", true);
		getController().update(ids, values);
	}
}
