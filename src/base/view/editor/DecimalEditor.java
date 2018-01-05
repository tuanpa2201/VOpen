/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 22, 2016
* Author: tuanpa
*
*/
package base.view.editor;

import java.math.BigDecimal;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Decimalbox;

import base.model.VField;

public class DecimalEditor extends VEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1004198064404038881L;

	public DecimalEditor(VField field) {
		super(field);
	}

	@Override
	public void setValue(Object value) {
		// if (value != null && value.equals(((Decimalbox)
		// component).getValue()))
		// return;
		if (value instanceof BigDecimal)
			((Decimalbox) component).setValue((BigDecimal) value);
		else {
			((Decimalbox) component).setRawValue(null);
		}
		super.setValue(value);
	}

	@Override
	public Object getValue() {
		return ((Decimalbox) component).getValue();
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getName().equals(Events.ON_CHANGE) && event.getTarget() == component) {
			Object value = ((Decimalbox) component).getValue();
			setValue(value);
		}
		super.onEvent(event);
	}

	@Override
	public void initComponent() {
		component = new Decimalbox();
		((Decimalbox) component).setHflex("1");
		component.addEventListener(Events.ON_CHANGE, this);
		if (field.placeholder != null) {
			((Decimalbox) component).setPlaceholder(field.placeholder);
		}
	}

	@Override
	public void setReadonly(Boolean readonly) {
		((Decimalbox) component).setReadonly(readonly);
	}
}
