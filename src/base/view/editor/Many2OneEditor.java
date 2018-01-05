/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 22, 2016
* Author: tuanpa
*
*/
package base.view.editor;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Image;

import base.controller.VActionResponse;
import base.controller.VController;
import base.model.Many2OneField;
import base.model.VField;
import base.model.VObject;
import base.util.Filter;
import base.util.StringAppUtils;
import base.util.StringUtils;
import base.util.Translate;
import base.util.VClassLoader;
import base.util.ZKEnv;
import base.view.VViewDefine;
import base.view.VViewType;

public class Many2OneEditor extends ReferenceEditor implements IFormViewPopupDelegate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1004198064404038881L;
	private Many2OneField mField = null;
	private int maxItemView = 5;
	private boolean isSeachMoreInProgress = false;
	private Component combobox;
	private Image zoomImage;

	public Many2OneEditor(VField field) {
		super(field);
		mField = (Many2OneField) field;
		controller = ZKEnv.getEnv().get(mField.parentModel);
		renderItem("");
	}
	
	private void renderValueItem() {
		if (this.value != null) {
			boolean isExist = false;
			for (int i = 0; i < ((Combobox) combobox).getItemCount(); i++) {
				Comboitem item = ((Combobox) combobox).getItemAtIndex(i);
				if (item.getValue().equals(((VObject) this.value).getValue("id"))) {
					((Combobox) combobox).setSelectedItem(item);
					isExist = true;
					break;
				}
			}
			if (!isExist) {
				Comboitem item = new Comboitem();
				item.setValue(((VObject) this.value).getValue("id"));
				item.setLabel(controller.getDisplayString((Integer) ((VObject) this.value).getValue("id")));
				Comboitem firstItem = null;
				if (((Combobox) combobox).getItemCount() > 0)
					firstItem = ((Combobox) combobox).getItems().get(0);
				if (firstItem == null) {
					((Combobox) combobox).appendChild(item);
				} else {
					((Combobox) combobox).insertBefore(item, firstItem);
				}
				((Combobox) combobox).setSelectedIndex(0);		
			} 
		} else {
			((Combobox) combobox).setSelectedItem(null);
			((Combobox) combobox).setText(null);
		}
	}

	@Override
	public void setValue(Object value) {
		isSeachMoreInProgress = false;
		super.setValue(value);
		if (value instanceof VObject) {
			this.value = (VObject) value;
			zoomImage.setVisible(true);
		} else {
			this.value = null;
			zoomImage.setVisible(false);
		}
		renderValueItem();
	}

	private String searchStr = "";

	@Override
	public void onEvent(Event event) throws Exception {
		if (isSeachMoreInProgress)
			return;
		if (event.getName().equals(Events.ON_CHANGING) && event.getTarget() == combobox) {
			InputEvent iEvent = (InputEvent) event;
			if (iEvent.isChangingBySelectBack())
				return;
			searchStr = iEvent.getValue();
			renderItem(searchStr);
		} else if (event.getName().equals(Events.ON_OPEN)) {
			if (StringAppUtils.isEmpty(searchStr)) {
				renderItem("");
			}
		} else if ((event.getName().equals(Events.ON_OK) || event.getName().equals(Events.ON_BLUR)
				|| event.getName().equals(Events.ON_SELECT)) && event.getTarget() == combobox) {
			Comboitem selectedItem = ((Combobox) combobox).getSelectedItem();
			if (selectedItem != null) {
				selectItem(selectedItem);
			} else {
				if (((Combobox) combobox).getText() != null && ((Combobox) combobox).getText().length() > 0
						&& ((Combobox) combobox).getItemCount() > 0) {
					((Combobox) combobox).setSelectedItem(((Combobox) combobox).getItemAtIndex(0));
					selectItem(((Combobox) combobox).getItemAtIndex(0));
				} else {
					this.setValue(null);
				}
			}
			searchStr = "";
		}
		else if (event.getTarget() == zoomImage) {
			zoomToItem();
		}
		super.onEvent(event);
	}
	
	private void zoomToItem() throws InstantiationException, IllegalAccessException {
		VObject obj = (VObject) this.getValue();
		if (formViewDefine == null) {
			if (mField.viewId != null) {
				formViewDefine = VViewDefine.getView(mField.viewId);
			}
			else {
				formViewDefine = VViewDefine.getViewForModel(mField.parentModel, VViewType.FORM);
			}
		}
		FormViewPopup popup = new FormViewPopup(formViewDefine, mField.parentModel, obj);
		popup.delegate = this;
		popup.setParent(this);
		popup.doModal();
	}
	
	private VViewDefine formViewDefine;

	public void selectItem(Comboitem item) throws InstantiationException, IllegalAccessException {
		if (item.getValue().equals(-1)) {
			value = null;
			isSeachMoreInProgress = true;
			Many2AnySelector selector = Many2AnySelector.getSelector(this, mField.parentModel, false, filter);
			selector.setParent(this);
			selector.doModal();
		}
		else if (item.getValue().equals(-2)) {
			this.setValue(null);
			VObject newObj = (VObject) VClassLoader.getModelClass(mField.parentModel).newInstance();
			if (formViewDefine == null) {
				formViewDefine = VViewDefine.getViewForModel(mField.parentModel, VViewType.FORM);
			}
			FormViewPopup popup = new FormViewPopup(formViewDefine, mField.parentModel, newObj);
			popup.delegate = this;
			popup.setParent(this);
			popup.doModal();
		} else {
			Integer id = item.getValue();
			VObject value = controller.browse(id);
			this.setValue(value);
		}
	}

	private void renderItem(String searchStr) {
		Comboitem selectedItem = ((Combobox) combobox).getSelectedItem();
		((Combobox) combobox).getItems().clear();
		if (selectedItem != null) {
			((Combobox) combobox).appendChild(selectedItem);
		}
		Map<Integer, String> searchItem = new HashMap<>();
		searchItem = controller.getDisplayString(controller.searchByString(filter, searchStr, maxItemView + 2));
		int count = 0;
		for (Integer id : searchItem.keySet()) {
			if (selectedItem != null && selectedItem.getValue().equals(id))
				continue;
			count++;
			if (count > maxItemView) {
				continue;
			}
			String itemDisplayStr = searchItem.get(id);
			Comboitem item = new Comboitem();
			item.setValue(id);
			item.setLabel(itemDisplayStr);
			((Combobox) combobox).appendChild(item);
		}
		if (count > maxItemView) {
			Comboitem item = new Comboitem();
			item.setValue(-1);
			item.setLabel(Translate.translate(ZKEnv.getEnv(), null, "Search more"));
			item.setClass("comboitem-searchmore");
			((Combobox) combobox).appendChild(item);
		}
		
		if (mField.isAllowedAddRecord) {
			Comboitem item = new Comboitem();
			item.setValue(-2);
			item.setLabel(Translate.translate(ZKEnv.getEnv(), null, "Add record"));
			item.setClass("comboitem-searchmore");
			((Combobox) combobox).appendChild(item);
		}
	}

	@Override
	public void initComponent() {
		component = new Hlayout();
		((Hlayout)component).setHflex("1");
		((Hlayout)component).setSpacing("0");
		((Hlayout)component).setValign("middle");
		combobox = new Combobox();
		combobox.setParent(component);
		((Combobox) combobox).setStyle("margin: 0px;");
		((Combobox) combobox).setHflex("1");
		combobox.addEventListener(Events.ON_CHANGING, this);
		combobox.addEventListener(Events.ON_OPEN, this);
		combobox.addEventListener(Events.ON_OK, this);
		combobox.addEventListener(Events.ON_BLUR, this);
		combobox.addEventListener(Events.ON_SELECT, this);
		if (field.placeholder != null) {
			((Combobox) combobox).setPlaceholder(field.placeholder);
		}
		((Combobox) combobox).setAutodrop(true);
		
		zoomImage = new Image("/themes/images/link16.png");
		zoomImage.setTooltip("Zoom to record");
		zoomImage.setStyle("position: absolute; right: 40px; top: -7px;"); 
		zoomImage.setParent(component);
		zoomImage.setVisible(false);
		zoomImage.addEventListener(Events.ON_CLICK, this);
	}

	@Override
	public void setReadonly(Boolean readonly) {
		((Combobox) combobox).setDisabled(readonly);
	}

	@Override
	public void reset() {
		((Combobox) combobox).getItems().clear();
		((Combobox) combobox).setSelectedItem(null);
		((Combobox) combobox).setText("");
		if (!checkValue()) {
			setValue(null);
		}
		else {
			renderValueItem();
		}
	}
	
	private boolean checkValue() {
		if (value == null || !(value instanceof VObject))
			return true;
		Filter newFilter = new Filter(filter.hqlWhereClause, filter.params);
		if (StringUtils.isEmpty(newFilter.hqlWhereClause)) {
			newFilter.hqlWhereClause = "id=:id_to_check";
		}
		else {
			newFilter.hqlWhereClause += " and id=:id_to_check";
		}
		newFilter.params.put("id_to_check", ((VObject) this.value).getValue("id"));
		return controller.count(newFilter) > 0;
	}

	@Override
	public VActionResponse onSave(VObject vobj) {
		VController controller = ZKEnv.getEnv().get(mField.parentModel);
		Map<String, Object> mapValues = new HashMap<>();
		for (String fieldName : controller.getAllFields().keySet()) {
			if (vobj.getValue(fieldName) != null) {
				mapValues.put(fieldName, vobj.getValue(fieldName));
			}
		}
		VActionResponse response = controller.create(mapValues); 
		if (response.status) {
			this.setValue(response.obj);
		}
		return response;
	}

	public Component getCombobox() {
		return combobox;
	}

	public void setCombobox(Component combobox) {
		this.combobox = combobox;
	}
}
