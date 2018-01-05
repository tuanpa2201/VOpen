package base.view;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.util.Clients;

import base.controller.VActionResponse;
import base.controller.VController;
import base.model.FunctionField;
import base.view.editor.IEditorListenner;
import base.view.editor.One2ManyEditor;
import base.view.editor.ReferenceEditor;
import base.view.editor.VEditor;

public class VObjectEditHelper implements IEditorListenner {
	public Map<String, VEditor> mapEditor;
	public Map<VEditor, String> mapEditorInvert;
	public Map<String, Object> mapNewValue;
	
	private VController controller;
	private IVObjectChangeListener listener;
	public Map<String, Object> defaults;
	
	public VObjectEditHelper(IVObjectChangeListener listener, VController controller, Map<String, Object> defaults) {
		mapEditor = new LinkedHashMap<>();
		mapEditorInvert = new HashMap<>();
		mapNewValue = new HashMap<>();
		this.controller = controller;
		this.listener = listener;
		this.defaults = defaults;
	}
	
	Integer objId;
	
	public void setData(Integer objId) {
		this.objId = objId;
		for (String fieldName : mapEditor.keySet()) {
			VEditor editor = mapEditor.get(fieldName);
			Object value = controller.getValue(objId, fieldName);
			editor.setOriginValue(value);
		}

		if (objId == null) {
			setDefaultValues();
			listener.onChanging(mapNewValue);
		}
		for (String fieldName : mapEditor.keySet()) {
			VEditor editor = mapEditor.get(fieldName);
			if (!editor.field.isReadOnly) {
				if (editor.component instanceof HtmlBasedComponent) {
					((HtmlBasedComponent) editor.component).focus();
					break;
				}
			}
		}
		dynamicState();
	}
	
	private void setDefaultValues() {
		Map<String, Object> modelDefault = controller.getDefaultValues();
		for (String key : modelDefault.keySet()) {
			if (defaults.get(key) == null) {
				defaults.put(key, modelDefault.get(key));
			}
		}
		for (String fieldName : defaults.keySet()) {
			VEditor veditor = mapEditor.get(fieldName);
			if (veditor != null) {
				veditor.setOriginValue(modelDefault.get(fieldName));
			}
		}
	}

	@Override
	public void onChangedValue(VEditor editor) {
		String fieldName = mapEditorInvert.get(editor);
		mapNewValue.put(fieldName, editor.getValue());
		boolean isEditing = false;
		for (String field : mapNewValue.keySet()) {
			VEditor editor2 = mapEditor.get(field);
			if (editor2 != null && editor2.isChanged()) {
				isEditing = true;
				break;
			}
		}
		if (objId == null) {
			isEditing = true;
		}
		
		if (!isEditing) {
			mapNewValue.clear();
			listener.onDiscard();
		}
		else {
			listener.onChanging(mapNewValue);
		}
		
		dynamicState();
	}
	
	public void dynamicState() {
		//IsActive field behavior
		Boolean isActive = null;
		if (mapNewValue.containsKey("isActive")) {
			isActive = (Boolean) mapNewValue.get("isActive");
		}
		if (isActive == null) {
			isActive = (Boolean) controller.getValue(objId, "isActive");
		}
		if (isActive == null) {
			isActive = true;
		}
		if (!isActive) {
			for (String fieldName : mapEditor.keySet()) {
				VEditor editor = mapEditor.get(fieldName);
				if (fieldName.equals("isActive")) {
					editor.setReadonly(false);
				}
				else {
					editor.setReadonly(true);
				}
			}
		}
		else {
			for (String fieldName : mapEditor.keySet()) {
				VEditor editor = mapEditor.get(fieldName);
				editor.setReadonly(false);
			}
		}
		Map<String, Object> values = new HashMap<>();
		values.put("id", objId);
		for (String fieldName : mapEditor.keySet()) {
			values.put(fieldName, mapEditor.get(fieldName).getValue());
		}
		controller.getVEnv().setCurrentVObjectEditingValue(controller.modelName, values);
		VActionResponse response = controller.dynamicState(values);
//		if((objId == null || objId <= 0) && defaults != null && defaults.size() > 0) {
//			Iterator<String> it = defaults.keySet().iterator();
//			while(it.hasNext()) {
//				String fieldNameDefault = it.next();
//				response.values.put(fieldNameDefault, defaults.get(fieldNameDefault));
//			}
//		}
		handleReponse(response);
	}

	private void handleReponse(VActionResponse response) {
		if (response != null) {
			for (String fieldName : response.displays.keySet()) {
				VEditor editor = mapEditor.get(fieldName);
				if (editor != null) {
					editor.setDisplay(response.displays.get(fieldName));
				}
			}
	
			for (String fieldName : response.readonlys.keySet()) {
				VEditor editor = mapEditor.get(fieldName);
				if (editor != null) {
					editor.setReadonly(response.readonlys.get(fieldName));
				}
			}
	
			for (String fieldName : response.values.keySet()) {
				VEditor editor = mapEditor.get(fieldName);
				if (editor != null) {
					editor.setValue(response.values.get(fieldName));
				}
			}
	
			for (String fieldName : response.filters.keySet()) {
				VEditor editor = mapEditor.get(fieldName);
				if (editor != null && (editor instanceof ReferenceEditor)) {
					ReferenceEditor reditor = (ReferenceEditor) editor;
					reditor.filter = response.filters.get(fieldName);
					reditor.reset();
				}
			}
		}
		
		for (String fieldName : mapEditor.keySet()) {
			VEditor editor = mapEditor.get(fieldName);
			if (editor.field instanceof FunctionField) {
				editor.setReadonly(true);
			}
		}
	}
	
	public boolean checkBeforeSave(boolean showNoti) {
		 for (String fieldName : mapEditor.keySet()) {
			 VEditor editor = mapEditor.get(fieldName);
			 if (editor != null && editor.isVisible() 
					 && editor.getSclass() != null && editor.getSclass().contains("state-error")) {
				 if (showNoti) {
					 Clients.showNotification((String) editor.getAttribute("StringDetail"), Clients.NOTIFICATION_TYPE_ERROR,
							editor, "end_center", 3000);
					 if (editor.component instanceof HtmlBasedComponent) {
						 ((HtmlBasedComponent)editor.component).focus();
					 }
				 }
			 	return false;
			 }
			 if (editor instanceof One2ManyEditor) {
				 return ((One2ManyEditor)editor).checkBeforeSave();
			 }
		 }
		return true;
	}
}
