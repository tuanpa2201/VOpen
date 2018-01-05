/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 22, 2016
* Author: tuanpa
*
*/
package base.view.editor;

import java.sql.Timestamp;
import java.util.Date;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Timebox;

import base.model.VField;

public class TimeEditor extends VEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1004198064404038881L;

	public TimeEditor(VField field) {
		super(field);
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof Timestamp)
			((Timebox) component).setValue((Timestamp) value);
		else if (value instanceof Date) {
			((Timebox) component).setValue((Date) value);
		} else {
			((Timebox) component).setRawValue(null);
			value = null;
		}
		super.setValue(value);
	}

	@Override
	public Object getValue() {
		return ((Timebox) component).getValue();
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getName().equals(Events.ON_CHANGE) && event.getTarget() == component) {
			Object value = ((Timebox) component).getValue();
			setValue(value);
		}
		super.onEvent(event);
	}

	@Override
	public void initComponent() {
		component = new Timebox();
		((Timebox) component).setFormat("short");
		((Timebox) component).setHflex("1");
		if (field.placeholder != null) {
			((Timebox) component).setPlaceholder(field.placeholder);
		}
		component.addEventListener(Events.ON_CHANGE, this);
	}

	@Override
	public void setReadonly(Boolean readonly) {
		((Timebox) component).setDisabled(readonly);
	}
}
