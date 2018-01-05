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
import org.zkoss.zul.Intbox;

import base.model.VField;

public class IntegerEditor extends VEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1004198064404038881L;

	public IntegerEditor(VField field) {
		super(field);
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof Integer)
			((Intbox) component).setValue((Integer) value);
		else
			((Intbox) component).setValue(null);
		super.setValue(value);
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getName().equals(Events.ON_CHANGE) && event.getTarget() == component) {
			Object value = ((Intbox) component).getValue();
			setValue(value);
		}
		super.onEvent(event);
	}

	@Override
	public void initComponent() {
		component = new Intbox();
		((Intbox) component).setHflex("1");
//		((Intbox) component).setConstraint(this.getVConstraint());
		component.addEventListener(Events.ON_CHANGE, this);
		if (field.placeholder != null) {
			((Intbox) component).setPlaceholder(field.placeholder);
		}
	}

	@Override
	public void setReadonly(Boolean readonly) {
		((Intbox) component).setReadonly(readonly);
	}
}
