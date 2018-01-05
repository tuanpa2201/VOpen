/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Sep 1, 2016
* Author: tuanpa
*
*/
package modules.crm;

import java.util.ArrayList;

import base.VModuleDefine;
import modules.crm.model.Partner;
import modules.sys.SysModule;

public class CRMModule extends VModuleDefine {

	@Override
	public String getWebPath() {
		return "crm";
	}

	@Override
	public String getModuleId() {
		return "crm";
	}

	@Override
	public String getModuleName() {
		return "Customer Relationship Management";
	}

	@Override
	public ArrayList<Class<?>> getModelClasses() {
		ArrayList<Class<?>> retVal = new ArrayList<>();
		retVal.add(Partner.class);
		return retVal;
	}

	@Override
	public ArrayList<Class<?>> getServiceClasses() {
		ArrayList<Class<?>> retVal = new ArrayList<>();
		
		return retVal;
	}

	@Override
	public String getVersion() {
		return "0.1";
	}

	@Override
	public ArrayList<VModuleDefine> getDependencyModule() {
		ArrayList<VModuleDefine> retVal = new ArrayList<>();
		retVal.add(new SysModule());
		return retVal;
	}

	@Override
	public ArrayList<String> getDataFiles() {
		ArrayList<String> retVal = new ArrayList<>();
		retVal.add("modules/crm/data/crm_data.xml");
		return retVal;
	}

	@Override
	public ArrayList<String> getViewFiles() {
		ArrayList<String> retVal = new ArrayList<>();
		retVal.add("modules/crm/view/crm_view.xml");
		return retVal;
	}

	@Override
	public ArrayList<String> getResources() {
		return null;
	}

}
