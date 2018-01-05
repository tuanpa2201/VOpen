/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 21, 2016
* Author: tuanpa
*
*/
package base.view;

import java.util.LinkedHashMap;
import java.util.Map;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

import base.controller.VController;
import base.model.VField;
import base.view.editor.VEditor;

public class VRowRenderer implements RowRenderer<Integer> {
	private Map<String, VEditor> mapEditor;
	private VController controller;
	private EventListener<Event> eventListener;
	
	public VRowRenderer(Map<String, VField> mapField, VController controller, EventListener<Event> eventListener) {
		super();
		mapEditor = new LinkedHashMap<>();
		for (String fieldName : mapField.keySet()) {
			VField field = mapField.get(fieldName);
			VEditor editor = VEditor.getEditor(field);
			mapEditor.put(fieldName, editor);
		}
		this.controller = controller;
		this.eventListener = eventListener;
	}

	@Override
	public void render(Row row, Integer id, int index) throws Exception {
		row.setAttribute("id", id);
		row.addEventListener(Events.ON_CLICK, eventListener);
		Checkbox checkbox = new Checkbox();
		checkbox.setAttribute("id", id);
		checkbox.addEventListener(Events.ON_CHECK, eventListener);
		checkbox.setParent(row);
		for (String fieldName : mapEditor.keySet()) {
			VEditor editor = mapEditor.get(fieldName);
			Object value = controller.getValue(id, fieldName);
			editor.setValue(value);
			row.appendChild(editor.getEditorForListView());
		}
		Image deleteButton = new Image("/themes/images/delete16.png");
		deleteButton.setStyle("cursor: pointer;");
		deleteButton.setAttribute("id", id);
		deleteButton.setAttribute("action", "delete");
		deleteButton.addEventListener(Events.ON_CLICK, eventListener);
		deleteButton.setParent(row);
	}
}
