/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 21, 2016
* Author: tuanpa
*
*/
package base.view;

import java.util.Map;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

import base.model.VField;
import base.model.VObject;

public class VO2MRowRenderer implements RowRenderer<VObject> {
	private Map<String, VField> mapField;
	private EventListener<Event> eventListener;
	
	public VO2MRowRenderer(Map<String, VField> mapField, EventListener<Event> eventListener) {
		super();
		this.mapField = mapField;
		this.eventListener = eventListener;
	}

	@Override
	public void render(Row row, VObject vobj, int index) throws Exception {
		row.setAttribute("id", vobj.getId());
		row.addEventListener(Events.ON_CLICK, eventListener);
		Checkbox checkbox = new Checkbox();
		checkbox.setAttribute("id", vobj);
		checkbox.addEventListener(Events.ON_CHECK, eventListener);
		checkbox.setParent(row);
		for (String fieldName : mapField.keySet()) {
			Object value = vobj.getValue(fieldName);
			Label label = new Label(value != null? value.toString() : "");
			label.setHflex("1");
			label.setVflex("1");
			row.appendChild(label);
		}
		Image deleteButton = new Image("/themes/images/delete16.png");
		deleteButton.setAttribute("vobj", vobj);
		deleteButton.setAttribute("action", "delete");
		deleteButton.addEventListener(Events.ON_CLICK, eventListener);
		deleteButton.setParent(row);
	}
}
