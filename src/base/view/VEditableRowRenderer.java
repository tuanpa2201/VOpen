/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 21, 2016
* Author: tuanpa
*
*/
package base.view;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;

import base.controller.VController;
import base.model.DecimalField;
import base.model.IntegerField;
import base.model.VField;
import base.model.VObject;
import base.util.Translate;
import base.util.ZKEnv;
import base.view.editor.VEditor;

public class VEditableRowRenderer {
	private Map<String, VField> mapField;
	public Row currentRow;
	private VController controller;
	private VGridEditable gridEditable;
	public boolean isEditing = false;
	private EditableRow editableRow;
	public void startEditing() {
		isEditing = true;
		currentRow.getChildren().clear();
		editableRow = new EditableRow((VObject)currentRow.getAttribute("vobj"), currentRow);
	}
	
	public boolean checkBeforeSave() {
		try {
			return editableRow.helper.checkBeforeSave(false);
		}
		catch (Exception e){
			return true;
		}
	}
	
	public void delete(VObject vobj) {
		Component deleteRow = null;
		for (Component comp : gridEditable.getRows().getChildren()) {
			if (comp instanceof Row && comp.getAttribute("vobj") == vobj ) {
				deleteRow = comp;
				break;
			}
		}
		deleteRow.setParent(null);
		if (deleteRow == currentRow) {
			stopEditing(true);
		}
	}
	
	public boolean stopEditing(boolean isDiscard) {
		if (currentRow != null) {
			boolean isOk = false;
			try {
				if (editableRow != null && !isDiscard) {
					isOk = editableRow.updateData(true);
				}
				else {
					isOk = true;
				}
			} catch (Exception e) {
				isOk = false;
				e.printStackTrace();
			}
			if (isOk) {
				isEditing = false;
				renderReadOnly(currentRow, (VObject) currentRow.getAttribute("vobj"));
				currentRow = null;
				editableRow = null;
			}
		}
		return !isEditing;
	}
	
	public void restoreEditing() {
		if (isEditing) {
			startEditing();
		}
	}
	
	public VEditableRowRenderer(Map<String, VField> mapField, VController controller, VGridEditable gridEditable) {
		super();
		this.controller = controller;
		this.mapField = mapField;
		this.gridEditable = gridEditable;
	}
	
	public void renderReadOnly(Row row, VObject vobj) {
		row.getChildren().clear();
		row.setAttribute("id", vobj.getId());
		row.setAttribute("vobj", vobj);
		row.appendChild(new Cell());
		for (String fieldName : mapField.keySet()) {			
			Cell cell = new Cell();
			VField field = mapField.get(fieldName);
			Object value = vobj.getValue(fieldName);
			if(value != null && (field instanceof IntegerField || field instanceof DecimalField)) {
				value = NumberFormat.getNumberInstance(Locale.GERMAN).format(value);
				cell.setStyle("text-align:right;");
			}
			
			Label label = new Label(value != null? value.toString() : "");
			label.setHflex("1");
			label.setVflex("1");			
			cell.appendChild(label);
			row.appendChild(cell);
		}
		row.appendChild(new Cell());
		
		if(!gridEditable.isReadonly()) {
			
			Cell cell = new Cell();
			cell.setSclass("text-center");
			row.appendChild(cell);
		
			Button deleteButton = new Button();
			deleteButton.setImage("/themes/images/delete16.png");
			deleteButton.setTooltiptext(Translate.translate(ZKEnv.getEnv(), null, "Delete"));
			deleteButton.setAttribute("vobj", vobj);
			deleteButton.setAttribute("action", "delete");
			deleteButton.addEventListener(Events.ON_CLICK, gridEditable);
			deleteButton.setParent(cell);
			deleteButton.setZclass("none");
			deleteButton.setSclass("btn btn-link");	
		}		
	}
	
	class EditableRow implements EventListener<Event>, IVObjectChangeListener{
		VObject vobj;
		Row row;
		Textbox firstFlag = new Textbox();
		Textbox lastFlag = new Textbox();
		private VObjectEditHelper helper;
		
		boolean isDirty = false;
		
		public EditableRow(VObject vobj, Row row) {
			this.vobj = vobj;
			this.row = row;
			row.addEventListener("onSave", gridEditable);
			render();
			fillData();
			firstFlag.addEventListener(Events.ON_FOCUS, this);
			lastFlag.addEventListener(Events.ON_FOCUS, this);
		}
		
		private void render() {
			helper = new VObjectEditHelper(this, controller, new HashMap<>());
			row.appendChild(firstFlag);
			boolean isFirst = true;
			for (String fieldName : mapField.keySet()) {
				Cell cell = new Cell();
				cell.setParent(row);
				VField field = controller.getField(fieldName);
				if (field == null)
					continue;
				VEditor editor = VEditor.getEditor(field);
				
				if (editor != null) {
					editor.hideLabel();
					editor.onChangeListener = helper;
					editor.setParent(cell);
					editor.component.addEventListener(Events.ON_CLICK, this);
					helper.mapEditor.put(fieldName, editor);
					if (editor.component instanceof HtmlBasedComponent) {
						HtmlBasedComponent hComponent = (HtmlBasedComponent)editor.component;
						hComponent.setStyle("margin: 0px");
						if (isFirst) {
							hComponent.focus();
							isFirst = false;
						}
					}
					helper.mapEditorInvert.put(editor, fieldName);
				}
			}
			row.appendChild(lastFlag);
			
			Cell cell = new Cell();
			cell.setSclass("text-center");
			cell.setParent(row);
			Button deleteButton = new Button();
			deleteButton.setImage("/themes/images/delete16.png");
			deleteButton.setTooltip(Translate.translate(ZKEnv.getEnv(), null, "Delete"));
			deleteButton.setAttribute("vobj", vobj);
			deleteButton.setAttribute("action", "delete");
			deleteButton.addEventListener(Events.ON_CLICK, gridEditable);
			deleteButton.setParent(cell);
			deleteButton.setZclass("none");
			deleteButton.setSclass("btn btn-link");
		}
		
		private void fillData() {
			helper.setData(vobj.getId());
		}
		
		@Override
		public void onEvent(Event event) throws Exception {
			event.stopPropagation();
			if (event.getTarget().equals(firstFlag)) {
				if (updateData(helper.mapNewValue, true)) {
					gridEditable.editPreviewRow(vobj);
				}
			}
			else if (event.getTarget().equals(lastFlag)) {
				if (updateData(helper.mapNewValue, true)) {
					gridEditable.editNextRow(vobj);
				}
			}
		}
		
		private boolean updateData(boolean showNoti) throws Exception {
			return updateData(helper.mapNewValue, showNoti);
		}
		
		private boolean updateData(Map<String, Object> mapNewValue, boolean showNoti) throws Exception {
			for (String fieldName : mapNewValue.keySet()) {
				vobj.setValue(fieldName, mapNewValue.get(fieldName));
			}
			fireSaveEvent();
			if (!helper.checkBeforeSave(showNoti))
				return false;
			return true;
		}
		
		private void fireSaveEvent() {
			Event event = new Event("onSave", row, vobj);
			Events.postEvent(event);
		}

		@Override
		public void onChanging(Map<String, Object> mapNewValue) {
			try {
				updateData(mapNewValue, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onDiscard() {
			//ignore
		}
	}
}
