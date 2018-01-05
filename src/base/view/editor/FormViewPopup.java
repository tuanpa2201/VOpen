/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Sep 7, 2016
* Author: tuanpa
*
*/
package base.view.editor;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import base.controller.VActionResponse;
import base.controller.VController;
import base.model.VObject;
import base.util.Translate;
import base.util.ZKEnv;
import base.view.IVObjectChangeListener;
import base.view.ParseFormViewUtil;
import base.view.VObjectEditHelper;
import base.view.VViewDefine;

public class FormViewPopup extends Window implements EventListener<Event>, IVObjectChangeListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3181380311498329689L;
	VController controller;
	VViewDefine viewDef;
	VObject vobj;
	public IFormViewPopupDelegate delegate;

	public VObjectEditHelper helper;
	
	private void parseXML(Component form) {
		ParseFormViewUtil parseUtil = new ParseFormViewUtil(controller, helper, helper.mapEditor, helper.mapEditorInvert, viewDef.moduleId, null);
		parseUtil.recursiveParseNode(viewDef.xmlNode, form);
	}

	public FormViewPopup(VViewDefine viewDef, String modelName, VObject vobj) {
		this.viewDef = viewDef;
		this.vobj = vobj;
		controller = ZKEnv.getEnv().get(modelName);
		helper = new VObjectEditHelper(this, controller, controller.getDefaultValues());
		initUI();
		parseXML(form);
		refreshData();
	}

	Div form;
	Button btSave;
	Button btCancel;

	private void initUI() {
		this.setWidth("600px");
		this.setVflex("min");
		Vlayout container = new Vlayout();
		container.setHflex("1");
		container.setVflex("min");
		container.setParent(this);
		form = new Div();
		form.setHflex("1");
		form.setVflex("min");
		form.setParent(container);
		Hbox boxBottom = new Hbox();
		boxBottom.setParent(container);
		boxBottom.setHflex("1");
		boxBottom.setVflex("min");
		boxBottom.setPack("center");
		
		btSave = new Button(Translate.translate(ZKEnv.getEnv(), null, "Save"));
		btSave.setParent(boxBottom);
		btSave.addEventListener(Events.ON_CLICK, this);
		btSave.setZclass("none");
		btSave.setSclass("btn btn-success");
		
		btCancel = new Button(Translate.translate(ZKEnv.getEnv(), null, "Cancel"));
		btCancel.addEventListener(Events.ON_CLICK, this);
		btCancel.setParent(boxBottom);
		btCancel.setZclass("none");
		btCancel.setSclass("btn btn-link");
	}

	private void refreshData() {
		helper.setData(vobj.getId());
		this.invalidate();
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getTarget() == btCancel) {
			this.detach();
		}
		else if (event.getTarget() == btSave) {
			if (helper.checkBeforeSave(true)) {
				for (String fieldName : helper.mapNewValue.keySet()) {
					vobj.setValue(fieldName, helper.mapNewValue.get(fieldName));
				}
				VActionResponse response = delegate.onSave(vobj);
				if (response.status) {
					this.detach();
				}
				else {
					for (String message : response.messages) {
						Clients.showNotification(message);
					}
				}
			}
		}
		event.stopPropagation();
	}

	@Override
	public void onChanging(Map<String, Object> mapNewValue) {
		
	}

	@Override
	public void onDiscard() {
		
	}
}
