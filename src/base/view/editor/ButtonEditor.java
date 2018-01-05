package base.view.editor;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;

import base.controller.VController;
import base.model.ButtonField;
import base.model.VField;
import base.util.StringAppUtils;
import base.view.VWindow;

public class ButtonEditor extends VEditor implements EventListener<Event> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4869679179447878892L;
	
	public VWindow window;
	public VController controller;
	private ButtonField bField;

	public ButtonEditor(VField field) {
		super(field);
		bField = (ButtonField) field;
	}

	@Override
	public void initComponent() {
		component = new Button();
		((Button)component).setZclass("btn");
		((Button)component).setStyle("padding-left: 10px; padding-right: 10px; padding-top: 5px; padding-bottom: 5px;");
		((Button)component).addEventListener(Events.ON_CLICK, this);
	}

	@Override
	public void setReadonly(Boolean readonly) {
		((Button)component).setDisabled(readonly);
	}
	
	@Override
	protected void initUI() {
		Div tmp = new Div();
		tmp.setStyle("padding-left: 5px");
		tmp.setParent(this);
		tmp.appendChild(component);
		((Button) component).setLabel(field.label);
		if (!StringAppUtils.isEmpty(field.help)) {
			helpImage = new Image("/themes/images/help16.png");
			helpImage.addEventListener(Events.ON_CLICK, this);
			helpImage.setStyle("padding-left: 10px");
			tmp.appendChild(helpImage);
		}
		this.setReadonly(field.isReadOnly);
	}
	
	@Override
	public void onEvent(Event event) throws Exception {
		event.stopPropagation();
		if (event.getName().equals(Events.ON_CLICK) && event.getTarget() == component) {
			if(window != null) {
				window.save();
				controller.execute(bField.functionName, controller.browse(window.currentId));
				window.refresh();
			}
		}
		else {
			super.onEvent(event);
		}
	}
}
