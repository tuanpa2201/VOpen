/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 22, 2016
* Author: tuanpa
*
*/
package base.view.editor;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;

import base.model.SelectionField;
import base.model.VField;

public class SelectionEditor extends VEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1004198064404038881L;

	public SelectionEditor(VField field) {
		super(field);
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof String) {
			boolean found = false;
			for (Component item : ((Combobox) component).getChildren()) {
				if (item instanceof Comboitem && ((Comboitem) item).getValue() != null
						&& ((Comboitem) item).getValue().equals(value)) {
					((Combobox) component).setSelectedItem((Comboitem) item);
					found = true;
					break;
				}
			}
			if (!found) {
				value = null;
				((Combobox) component).setSelectedIndex(0);
			}
		} else {
			value = null;
			((Combobox) component).setSelectedIndex(0);
		}
		super.setValue(value);
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getName().equals(Events.ON_CHANGE) && event.getTarget() == component) {
			Object value = ((Combobox) component).getSelectedItem().getValue();
			setValue(value);
		}
		super.onEvent(event);
	}

	@Override
	public void initComponent() {
		component = new Combobox();
		SelectionField sField = (SelectionField) field;
		Comboitem item = new Comboitem();
		item.setValue(null);
		item.setLabel("- - -");
		((Combobox) component).appendChild(item);
		for (String key : sField.values.keySet()) {
			item = new Comboitem();
			item.setValue(key);
			item.setLabel(sField.values.get(key));
			((Combobox) component).appendChild(item);
		}
		((Combobox) component).setHflex("1");
		component.addEventListener(Events.ON_CHANGE, this);
		((Combobox) component).setReadonly(true);
		if (field.placeholder != null) {
			((Combobox) component).setPlaceholder(field.placeholder);
		}
	}

	@Override
	public void setReadonly(Boolean readonly) {
		((Combobox) component).setDisabled(readonly);
	}
}
