/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Jul 21, 2016
* Author: tuanpa
*
*/
package modules.sys;

import java.util.ArrayList;

import base.VModuleDefine;
import modules.sys.model.SysAction;
import modules.sys.model.SysAddress;
import modules.sys.model.SysCompany;
import modules.sys.model.SysCompanyDependentValue;
import modules.sys.model.SysFile;
import modules.sys.model.SysGroup;
import modules.sys.model.SysLog;
import modules.sys.model.SysLogDetail;
import modules.sys.model.SysMenu;
import modules.sys.model.SysNoticeLog;
import modules.sys.model.SysNotification;
import modules.sys.model.SysPermission;
import modules.sys.model.SysReport;
import modules.sys.model.SysRule;
import modules.sys.model.SysSequence;
import modules.sys.model.SysSequenceDetail;
import modules.sys.model.SysUser;
import modules.sys.model.SysUserToken;
import modules.sys.view.SysMonitor;

public class SysModule extends VModuleDefine{

	@Override
	public String getModuleName() {
		return "System";
	}

	@Override
	public ArrayList<Class<?>> getModelClasses() {
		ArrayList<Class<?>> retVal = new ArrayList<>();
		retVal.add(SysAction.class);
		retVal.add(SysAddress.class);
		retVal.add(SysCompany.class);
		retVal.add(SysGroup.class);
		retVal.add(SysMenu.class);
		retVal.add(SysRule.class);
		retVal.add(SysUser.class);
		retVal.add(SysFile.class);
		retVal.add(SysPermission.class);
		retVal.add(SysCompanyDependentValue.class);
		retVal.add(SysLog.class);
		retVal.add(SysLogDetail.class);
		retVal.add(SysNotification.class);
		retVal.add(SysNoticeLog.class);
		retVal.add(SysSequence.class);
		retVal.add(SysSequenceDetail.class);
		retVal.add(SysMonitor.class);
		retVal.add(SysReport.class);
		retVal.add(SysUserToken.class);
		return retVal;
	}

	@Override
	public String getVersion() {
		return "1.0";
	}
	
	@Override
	public boolean isAutoInstall() {
		return true;
	}

	@Override
	public ArrayList<VModuleDefine> getDependencyModule() {
		return new ArrayList<>();
	}

	@Override
	public ArrayList<String> getDataFiles() {
		ArrayList<String> retVal = new ArrayList<>();
		retVal.add("modules/sys/data/sys_data.xml");
		return retVal;
	}

	@Override
	public ArrayList<String> getViewFiles() {
		ArrayList<String> retVal = new ArrayList<>();
		retVal.add("modules/sys/view/sys_view.xml");
		return retVal;
	}

	@Override
	public String getModuleId() {
		return "system";
	}

	@Override
	public String getWebPath() {
		// TODO Auto-generated method stub
		return "sys";
	}

	@Override
	public ArrayList<String> getResources() {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

	@Override
	public ArrayList<Class<?>> getServiceClasses() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ArrayList<String> getLocaleFiles() {
		ArrayList<String> retVal = new ArrayList<>();
		retVal.add("modules/sys/locale/vn");
		return retVal;
	}
}
