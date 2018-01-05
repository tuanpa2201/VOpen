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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;

import base.model.VField;
import base.util.StringAppUtils;

public class YesNoEditor extends VEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1004198064404038881L;

	public YesNoEditor(VField field) {
		super(field);
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof Boolean)
			((Checkbox) component).setChecked((Boolean) value);
		else {
			((Checkbox) component).setChecked(false);
			value = false;
		}
		super.setValue(value);
	}

	@Override
	public void setVSclass(String state) {
		if (field.isEnforce()) {
			this.setSclass(state);
		} else {
			this.setSclass("none" + state);
		}
	}

	@Override
	public Object getValue() {
		return ((Checkbox) component).isChecked();
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getName().equals(Events.ON_CHECK) && event.getTarget() == component) {
			Object value = ((Checkbox) component).isChecked();
			setValue(value);
		}
		super.onEvent(event);
	}

	@Override
	public void initComponent() {
		component = new Checkbox();
		component.addEventListener(Events.ON_CHECK, this);
	}

	@Override
	public void setReadonly(Boolean readonly) {
		((Checkbox) component).setDisabled(readonly);
	}

	@Override
	protected void initUI() {
		Div tmp = new Div();
		tmp.setStyle("padding-left: 5px");
		tmp.setParent(this);
		tmp.appendChild(component);
		((Checkbox) component).setLabel(field.label);
		if (!StringAppUtils.isEmpty(field.help)) {
			helpImage = new Image("/themes/images/help16.png");
			helpImage.addEventListener(Events.ON_CLICK, this);
			helpImage.setStyle("padding-left: 10px");
			tmp.appendChild(helpImage);
		}
		this.setReadonly(field.isReadOnly);
	}
}
