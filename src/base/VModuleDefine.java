/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Jul 20, 2016
* Author: tuanpa
*
*/
package base;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import base.controller.VController;
import base.controller.VEnv;
import base.model.VObject;
import base.util.AppUtils;
import base.util.FileSystemUtils;
import base.util.Filter;
import base.util.HibernateUtil;
import base.util.ParseXMLDataUtils;
import base.util.ParseXMLViewUtils;
import base.util.Translate;

public abstract class VModuleDefine {
	abstract public String getWebPath();
	abstract public String getModuleId();
	abstract public String getModuleName();
	abstract public ArrayList<Class<?>> getModelClasses();
	abstract public ArrayList<Class<?>> getServiceClasses();
	abstract public String getVersion();
	abstract public ArrayList<VModuleDefine> getDependencyModule();
	abstract public ArrayList<String> getDataFiles();
	abstract public ArrayList<String> getViewFiles();
	abstract public ArrayList<String> getResources();
	public ArrayList<String> getLocaleFiles() {
		ArrayList<String> retVal = new ArrayList<>();
		
		return retVal;
	}
	public String getReportDir() {
		
		return "";
	}
	
	public boolean isAutoInstall() {
		return false;
	}
	@Override
	public int hashCode() {
		return getModuleId().hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VModuleDefine) {
			VModuleDefine moDef = (VModuleDefine) obj;
			return moDef.getModuleId().equals(this.getModuleId());
		}
		return false;
	}
	
	public boolean isInstalled() {
		return VModuleManager.isInstalled(this);
	}
	
	public void loadView() {
		//import view
		if (this.getViewFiles() != null) {
			for (String viewFileUri : this.getViewFiles()) {
				ParseXMLViewUtils.parseXMLFile(this.getModuleId(), viewFileUri);
			}
		}
	}
	
	public void loadLocale() {
		for (String resourceUrl : getLocaleFiles()) {
			try {
				Locale locale = new Locale(resourceUrl.substring(resourceUrl.length() - 2));
				InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceUrl);
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
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
							if (locales.get(key) == null) {
								locales.put(key, value);
							}
							key = getModuleId() + "_" + key;
							locales.put(key, value);
						}
					}
				}
				reader.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public void startup() {
		this.loadView();
		loadLocale();
		FileSystemUtils.copyResource(this);
		if (this.getServiceClasses() != null) {
			for (Class<?> serviceClass : this.getServiceClasses()) {
				VOpenStartup.instance.addService(serviceClass);
			}
		}
	}
	
	public void install() {
		//reload hibernate
		HibernateUtil.reloadSessionFactory(this);
		//import data file
		if (this.getDataFiles() != null) {
			for (String dataFileUri : this.getDataFiles()) {
				String filePath = AppUtils.getSourcePath() + dataFileUri;
				File file = new File(filePath);
				ParseXMLDataUtils.createModelXml(file);
			}
		}
		
		this.startup();
	}
	
	
	public void uninstall() {
		//Disable data file record
		if (this.getDataFiles() != null) {
			for (String dataFileUri : this.getDataFiles()) {
				String filePath = AppUtils.getSourcePath() + dataFileUri;
				File file = new File(filePath);
				ParseXMLDataUtils.disableModelXml(file);
			}
		}
		//remove import view
		
		//remove resource
		FileSystemUtils.deleteResource(this);
	}
	
	public void upgrade() {
		install();
	}
	static private VController moduleController = VEnv.sudo().get("base.module");
	public VObject getModuleDB() {
		HashMap<String, Object> params = new HashMap<>();
		params.put("moduleId", this.getModuleId());
		Map<Integer, VObject> tmps = moduleController.browse(moduleController.search(new Filter("moduleId = :moduleId ", params), "", 0, 0));
		VObject module = null;
		tmps.keySet().iterator();
		if (tmps.keySet().size() > 0) {
			module = tmps.get(tmps.keySet().iterator().next());
		}
		else {
			
			HashMap<String, Object> values = new HashMap<>();
			values.put("moduleId", this.getModuleId());
			values.put("isActive", true);
			values.put("isInstalled", false);
			module = moduleController.browse(moduleController.create(values).id);
		}
		return module;
	}
}
