/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 22, 2016
* Author: tuanpa
*
*/
package base.view.editor;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Textbox;

import base.model.VField;

public class TextEditor extends VEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1004198064404038881L;

	public TextEditor(VField field) {
		super(field);
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof String)
			((Textbox) component).setValue((String) value);
		else
			((Textbox) component).setValue(null);
		super.setValue(value);
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getName().equals(Events.ON_CHANGE) && event.getTarget() == component) {
			Object value = ((Textbox) component).getValue();
			if (value != null && value.toString().trim().length() == 0) {
				value = null;
			}
			setValue(value);
		}
		super.onEvent(event);
	}

	@Override
	public void initComponent() {
		component = new Textbox();
		((Textbox) component).setHflex("1");
		if (field.placeholder != null) {
			((Textbox) component).setPlaceholder(field.placeholder);
		}
		((Textbox) component).setMultiline(true);
		((Textbox) component).setRows(5);
		component.addEventListener(Events.ON_CHANGE, this);
	}

	@Override
	public void setReadonly(Boolean readonly) {
		((Textbox) component).setReadonly(readonly);
	}
}
