/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 1, 2016
* Author: tuanpa
*
*/
package base.view;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vlayout;

import base.VModuleDefine;
import base.VModuleManager;

public class VModuleCard extends Div implements EventListener<Event> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1611550278525645358L;
	private VModuleDefine modulDef;
	private Button btInstall;
	private Button btUpgrage;
	private Button btUninstall;
	private ModuleManager mm;
	public VModuleCard(VModuleDefine modulDef, ModuleManager mm) {
		super();
		this.modulDef = modulDef;
		this.mm = mm;
		initUI();
	}
	
	private void initUI() {
		this.setVflex("min");
		this.setSclass("col-md-4");
		Vlayout container = new Vlayout();
		container.setParent(this);
		container.setSclass("box-shadow-preview");
		container.setStyle("margin: 10px; padding: 10px;");
		
		Label title = new Label(modulDef.getModuleName());
		title.setSclass("h4");
		title.setStyle("font-weight: bold");
		container.appendChild(title);
		Label version = new Label(modulDef.getVersion());
		version.setSclass("h6");
		container.appendChild(version);
		btInstall = new Button("Install");
		btInstall.setZclass("none");
		btInstall.setSclass("btn btn-success");
		btInstall.addEventListener(Events.ON_CLICK, this);
		btInstall.setHflex("1");
		
		btUpgrage = new Button("Upgrade");
		btUpgrage.addEventListener(Events.ON_CLICK, this);
		btUpgrage.setZclass("none");
		btUpgrage.setSclass("btn btn-link");
		btUpgrage.setHflex("1");
		
		btUninstall = new Button("Uninstall");
		btUninstall.addEventListener(Events.ON_CLICK, this);
		btUninstall.setZclass("none");
		btUninstall.setSclass("btn btn-link");
		btUninstall.setHflex("1");
		
		Hlayout btnHolder = new Hlayout();
		btnHolder.setParent(container);
		
		btnHolder.setWidth("100%");
		btnHolder.appendChild(btInstall);
		btnHolder.appendChild(btUpgrage);
		btnHolder.appendChild(btUninstall);
		checkModuleState();
	}
	
	public void checkModuleState() {
		btInstall.setDisabled(true);
		btUpgrage.setDisabled(true);
		btUninstall.setDisabled(true);
		
		if (VModuleManager.isInstalled(modulDef)) {
			btUpgrage.setDisabled(false);
			btUninstall.setDisabled(false);
		}
		else {
			btInstall.setDisabled(false);
		}
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getTarget() == btInstall) {
			VModuleManager.installModule(this.modulDef);
		}
		else if (event.getTarget() == btUpgrage) {
			VModuleManager.upgradeModule(modulDef);
		}
		else if (event.getTarget() == btUninstall) {
			VModuleManager.uninstallModule(modulDef);
		}
		mm.refresh();
	}
}
