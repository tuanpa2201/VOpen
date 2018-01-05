/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Sep 6, 2016
* Author: tuanpa
*
*/
package base.view.editor;

import java.util.List;

import org.w3c.dom.Element;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Messagebox;

import base.controller.VActionResponse;
import base.controller.VController;
import base.model.One2ManyField;
import base.model.VField;
import base.model.VObject;
import base.util.VClassLoader;
import base.util.ZKEnv;
import base.view.VGridEditable;
import base.view.VViewDefine;

public class One2ManyEditor extends VEditor implements IFormViewPopupDelegate{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1736677343190858853L;
	private VViewDefine formViewDefine;
	private One2ManyField oField;
	private VGridEditable grid;
	public VController controller = null;
	
	public One2ManyEditor(VField field) {
		super(field);
		oField = (One2ManyField) field;
		controller = ZKEnv.getEnv().get(oField.childModel);
		initGrid();
	}
	
	@Override
	public void initComponent() {
		Div div = new Div();
		div.setHflex("1");
//		div.setVflex("min");
		component = div;
	}
	
	public void buildView(Element node) {
		grid.setupGrid(node);
 	}
	
	private void initGrid() {
		grid = new VGridEditable(oField.childModel, VGridEditable.MODE_ADD_LAST);
		grid.setParent(component);
		grid.setOwner(this);
	}
	
	List<VObject> objs;

	@Override
	public void setValue(Object value) {
		isChanged = true;
		super.setValue(value);
		setValue1(value);
		invalidate();
	}
	
	@SuppressWarnings("unchecked")
	private void setValue1(Object value) {
		objs = (List<VObject>) value;
		grid.setData(objs);
	}
	
	
	boolean isChanged = false;
	@Override
	public void setOriginValue(Object value) {
		originValue = value;
		this.value = value;
		isChanged = false;
		setValue1(value);
	}

	public void forceChanged(List<VObject> lst) {
		this.value = lst;
		isChanged = true;
	}
	@Override
	public boolean isChanged() {
		return isChanged;
	}

	@Override
	public Object getValue() {
		return super.getValue();
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getTarget().getAttribute("action") != null) {
			if (event.getTarget().getAttribute("action").equals("new")) {
				VObject newObj = (VObject) VClassLoader.getModelClass(oField.childModel).newInstance();
				FormViewPopup popup = new FormViewPopup(formViewDefine, oField.childModel, newObj);
				popup.delegate = this;
				popup.setParent(this);
				popup.doModal();
			}
			else if (event.getTarget().getAttribute("action").equals("delete") && event.getName().equals(Events.ON_CLICK)) {
				if (event.getTarget().getAttribute("vobj") != null) {
					VObject vobj = (VObject) event.getTarget().getAttribute("vobj");
					String msg = "Delete: [";
						msg += controller.getDisplayString(vobj.getId()) + ", ";
					msg = msg.substring(0, msg.length() - 2);
					msg += "]";
					Messagebox.show(msg, "Confirm?", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new EventListener<Event>() {

						@Override
						public void onEvent(Event event) throws Exception {
							if (event.getName().equals(Messagebox.ON_OK)) {
								objs.remove(vobj);
								if (vobj.getId() != null)
									vobj.delete();
								setValue(objs);
							}
						}
					});
				}
			}
		}
		else if (event.getName().equals(Events.ON_BLUR) && event.getTarget() == grid) {
			System.out.println("Save here");
		}
		else {
			super.onEvent(event);
		}
	}

	@Override
	public VActionResponse onSave(VObject vobj) {
		if (!objs.contains(vobj))
			objs.add(vobj);
		setValue(objs);
		onChangedValue();
		VActionResponse response = new VActionResponse();
		response.obj = vobj;
		return response;
	}

	@Override
	public void setReadonly(Boolean readonly) {
		
	}
	
	public boolean isReadonly() {
		return oField.isReadOnly;
	}
	
	public void deleteChildObj() throws Exception {
		grid.deleteDBRecord();
	}
	
	public boolean checkBeforeSave() {
		return grid.checkBeforeSave();
	}
}
