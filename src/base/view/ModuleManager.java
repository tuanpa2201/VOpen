/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 1, 2016
* Author: tuanpa
*
*/
package base.view;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;

import base.VModuleDefine;
import base.VModuleManager;

public class ModuleManager extends SelectorComposer<Component> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6425181913599423396L;
	@Wire
	Div container;
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		List<VModuleDefine> listModule = VModuleManager.getAllModules();
		for (int i = 0; i < listModule.size(); i++) {
			VModuleDefine modulDef = listModule.get(i);
			VModuleCard module = new VModuleCard(modulDef, this);
			module.setParent(container);
		}
	}
	
	public void refresh() {
		for (Component comp : container.getChildren()) {
			if (comp instanceof VModuleCard) {
				VModuleCard module = (VModuleCard)comp;
				module.checkModuleState();
			}
		}
	}
}