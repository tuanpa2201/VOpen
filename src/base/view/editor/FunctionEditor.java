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

public class FunctionEditor extends VEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1004198064404038881L;
	public FunctionEditor(VField field) {
		super(field);
	}

	@Override
	public void setValue(Object value) {
		this.value = value;
		if (value != null)
			((Textbox)component).setValue(value.toString());
		else {
			((Textbox)component).setValue("");
		}
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getName().equals(Events.ON_CHANGE)
				&& event.getTarget() == component) {
			
		}
		super.onEvent(event);
	}

	@Override
	public void initComponent() {
		component = new Textbox();
		((Textbox)component).setHflex("1");
		component.addEventListener(Events.ON_CHANGE, this);
	}

	@Override
	public void setReadonly(Boolean readonly) {
		((Textbox)component).setReadonly(readonly);
	}
}
