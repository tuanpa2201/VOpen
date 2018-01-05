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
import org.zkoss.zul.Datebox;

import base.model.VField;
import base.util.DateUtils;

public class DateEditor extends VEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1004198064404038881L;

	public DateEditor(VField field) {
		super(field);
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof Timestamp)
			((Datebox) component).setValue((Timestamp) value);
		else if (value instanceof Date) {
			((Datebox) component).setValue((Date) value);
		} else {
			((Datebox) component).setRawValue(null);
			value = null;
		}
		super.setValue(value);
	}

	@Override
	public Object getValue() {
		Date date = ((Datebox) component).getValue();
		return DateUtils.truncDate(date);
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getName().equals(Events.ON_CHANGE) && event.getTarget() == component) {
			Object value = ((Datebox) component).getValue();
			setValue(value);
		}
		super.onEvent(event);
	}

	@Override
	public void initComponent() {
		component = new Datebox();
		((Datebox) component).setFormat("dd/MM/yyyy");
		((Datebox) component).setHflex("1");
		component.addEventListener(Events.ON_CHANGE, this);
		if (field.placeholder != null) {
			((Datebox) component).setPlaceholder(field.placeholder);
		}
	}

	@Override
	public void setReadonly(Boolean readonly) {
		((Datebox) component).setDisabled(readonly);
	}
}
