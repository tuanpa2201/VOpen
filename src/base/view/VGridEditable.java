package base.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;

import base.controller.VController;
import base.model.VField;
import base.model.VObject;
import base.util.Translate;
import base.util.VClassLoader;
import base.util.ZKEnv;
import base.view.editor.One2ManyEditor;

public class VGridEditable extends Grid implements EventListener<Event> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3054641426685052440L;
	
	public static final int MODE_READONLY = 0;
	public static final int MODE_ADD_LAST = 1;
	public static final int MODE_ADD_FIRST = 2;
	public static final int MODE_ADD_POPUP = 3;
	
	private VViewDefine listViewDefine;
	private VViewDefine formViewDefine;
	private String model;
	private Map<String, VField> mapField;
	private Columns columns;
	private Rows rows;
	public VController controller;
	VEditableRowRenderer rendered;
	private int mode = 0;

	private One2ManyEditor ownerEditor;
	
	public void setOwner(One2ManyEditor editor) {
		ownerEditor = editor;
		this.addEventListener(Events.ON_BLUR, ownerEditor);
	}
	
	public VGridEditable(String model, int mode) {
		this.model = model;
		this.mode = mode;
		controller = ZKEnv.getEnv().get(model);
		initUI();
	}
	
	private void initUI() {
		this.setWidth("100%");
		this.setStyle("min-height: 200px; margin-top: 10px");
		this.setSizedByContent(true);
		this.setSpan(true);
		
		columns = new Columns();
		columns.setParent(this);
		rows = new Rows();
		rows.setParent(this);
	}
	
	public void setupGrid(Element node) {
		Node childNode = node.getFirstChild();
		while (childNode != null) {
			if (childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equalsIgnoreCase("view")) {
				Element xmlNode = (Element) childNode;
				VViewDefine view = new VViewDefine();
				view.xmlNode = xmlNode;
				view.type = VViewType.getValueOf(xmlNode.getAttribute("type"));
				if (view.type.equals(VViewType.LIST)) {
					listViewDefine = view;
				}
				else if (view.type.equals(VViewType.FORM)) {
					formViewDefine = view;
				}
			}
			childNode = childNode.getNextSibling();
		}
		if (listViewDefine == null) {
			listViewDefine = VViewDefine.getViewForModel(model, VViewType.LIST);
		}
		if (formViewDefine == null) {
			formViewDefine = VViewDefine.getViewForModel(model, VViewType.FORM);
		}
		
		if (listViewDefine != null) {
			parseListView(listViewDefine);
		}
		
		rendered = new VEditableRowRenderer(mapField, controller, this);
	}
	public void parseListView(VViewDefine viewDef) {
		mapField = new LinkedHashMap<>();
		
		Column column = new Column();
		column.setParent(columns);
		column.setWidth("0px");
		
		recursiveParseNode(viewDef.xmlNode, this);
		
		column = new Column();
		column.setParent(columns);
		column.setWidth("0px");
		
		column = new Column();
		column.setParent(columns);
		column.setWidth("20px");
	}
	
	private void recursiveParseNode(Element node, Component parentComponent) {
		if (node.getNodeName().equalsIgnoreCase("view") || node.getNodeName().equalsIgnoreCase("list")) {
			Component newParent = parentComponent;
			if (node.getNodeName().equalsIgnoreCase("list")) {
				newParent = columns;
			}
			Node childNode = node.getFirstChild();
			while (childNode != null) {
				if (childNode.getNodeType() == Node.ELEMENT_NODE) {
					recursiveParseNode((Element) childNode, newParent);
				}
				childNode = childNode.getNextSibling();
			}
		} else if (node.getNodeName().equalsIgnoreCase("field") && node.getNodeType() == Node.ELEMENT_NODE) {
			String fieldName = node.getAttribute("name");

			VField field = controller.getField(fieldName);
			if (field != null) {
				mapField.put(fieldName, field);
				Column column = new Column();
				column.setLabel(Translate.translate(ZKEnv.getEnv(), listViewDefine.moduleId, field.label));
				column.setParent(parentComponent);
			}
		}
	}
	List<VObject> lstVObj;
	Row newRow = new Row();
	
	public void setData(List<VObject> objs) {
		lstVObj = objs;
		lstDeleted = new ArrayList<>();
		if (lstVObj == null) {
			lstVObj = new ArrayList<>();
		}
		rendered.stopEditing(true);
		rows.getChildren().clear();
		newRow.getChildren().clear();
		for (VObject vobj : lstVObj) {
			Row row = new Row();
			row.setParent(rows);
			rendered.renderReadOnly(row, vobj);
			row.addEventListener(Events.ON_CLICK, this);
		}
		
		newRow.setParent(rows);
		
		Cell cell = new Cell();
		cell.setColspan(mapField.size() + 3);
		
		Button btNew = new Button("New");
		btNew.setParent(cell);
		btNew.setAttribute("action", "new");
		btNew.setZclass("none");
		btNew.setSclass("btn btn-link");
		btNew.addEventListener(Events.ON_CLICK, this);
		
		newRow.appendChild(cell);
		rendered.restoreEditing();
		changeHeight();
	}
	
	private VObject newObject() throws InstantiationException, IllegalAccessException {
		VObject newObj = (VObject) VClassLoader.getModelClass(model).newInstance();
		return newObj;
	}
	
	private Row newRow(VObject vobj) {
		Row row = new Row();
		rendered.renderReadOnly(row, vobj);
		return row;
	}
	
	
	private void addNew() throws InstantiationException, IllegalAccessException {
		if (rendered.stopEditing(false)) {
			VObject newObj = newObject();
			lstVObj.add(newObj);
			Row row = newRow(newObj);
			if (mode == MODE_ADD_LAST) {
				rows.insertBefore(row, newRow);
			}
			rendered.currentRow = row;
			rendered.startEditing();
			changeHeight();
		}
	}
	
	List<VObject> lstDeleted;
	
	private void delete(VObject vobj) throws Exception {
		lstDeleted.add(vobj);
		lstVObj.remove(vobj);
		rendered.delete(vobj);
		changeHeight();
		save();
	}
	
	public void editNextRow(VObject vobj) throws InstantiationException, IllegalAccessException {
		if (rendered.stopEditing(false)) {
			int index = lstVObj.indexOf(vobj);
			if (index < lstVObj.size() - 1) {
				Row row = (Row) rows.getChildren().get(index + 1);
				rendered.currentRow = row;
				rendered.startEditing();
			}
			else if (index == lstVObj.size() - 1) {
				addNew();
			}
		}
	}
	
	public void editPreviewRow(VObject vobj){
		if (rendered.stopEditing(false)) {
			int index = lstVObj.indexOf(vobj);
			if (index >0) {
				Row row = (Row) rows.getChildren().get(index - 1);
				rendered.currentRow = row;
				rendered.startEditing();
			}
		}
	}
	
	private void save() {
		if (ownerEditor != null) {
			ownerEditor.forceChanged(lstVObj);
			ownerEditor.onChangedValue();
		}
	}
	
	public void deleteDBRecord() throws Exception {
		for (VObject vobj : lstDeleted)  {
			vobj.delete();
		}
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getName().equals(Events.ON_CLICK) && event.getTarget() instanceof Row) {
			if (rendered.stopEditing(false)) {
				Row row = (Row) event.getTarget();
				rendered.currentRow = row;
				rendered.startEditing();
				changeHeight();
			}
		}
		else if (event.getTarget().getAttribute("action") != null 
				&& event.getTarget().getAttribute("action").equals("new")) {
			addNew();
		}
		else if (event.getTarget().getAttribute("action") != null 
				&& event.getTarget().getAttribute("action").equals("delete")
				&& event.getTarget().getAttribute("vobj") != null) {
			VObject vobj = (VObject) event.getTarget().getAttribute("vobj");
			delete(vobj);
		}
		else if (event.getName().equals("onSave")) {
			save();
		}
	}
	
	public boolean checkBeforeSave() {
		return rendered.checkBeforeSave();
	}
	
	public boolean isReadonly() {
		return ownerEditor.isReadonly();
	}
	
	private int height = 200;
	
	private void changeHeight() {
		int rowHeight = rows.getChildren().size() * 32 + 40;
		if (rendered.isEditing) {
			rowHeight += 30;
		}
		height = Integer.max(200, rowHeight);
		this.setHeight(height + "px");
	}
}
