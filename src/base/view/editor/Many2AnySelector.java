/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 28, 2016
* Author: tuanpa
*
*/
package base.view.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.ZulEvents;

import base.controller.VController;
import base.model.VField;
import base.model.VObject;
import base.util.Translate;
import base.util.Filter;
import base.util.ZKEnv;
import base.view.HandlerSearchEvent;
import base.view.VSearchManagement;
import base.view.VViewDefine;
import base.view.VViewType;

public class Many2AnySelector extends Window implements EventListener<Event>, HandlerSearchEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3194123459808325945L;
	private String modelname;
	private Boolean isMultiSelect;
	private VController controller;
	private Listbox listbox;
	private Listhead listhead;
	private VViewDefine viewDef;
	private Map<String, VField> mapField;
	private VEditor editor;
	private Filter filter = new Filter();
	private VSearchManagement searchs;

	public Many2AnySelector(VEditor editor, String modelname, Boolean isMultiSelect, Filter filter) {
		super();
		this.editor = editor;
		this.modelname = modelname;
		this.isMultiSelect = isMultiSelect;
		this.filter = filter;
		controller = ZKEnv.getEnv().get(this.modelname);
		viewDef = VViewDefine.getViewForModel(modelname, VViewType.getValueOf("list"));
		initUI();
		searchs = new VSearchManagement(VSearchManagement.isAND);
		searchs.setHandler(this);
		searchs.getHeadrelement().setParent(listbox);
		parseXML();
		ListModel model = new ListModel();
		model.setMultiple(isMultiSelect);
		listbox.setModel(model);
		listbox.setItemRenderer(new VItemRenderer(this));
	}

	private void parseXML() {
		mapField = new LinkedHashMap<>();
		recursiveParseNode(viewDef.xmlNode, this);
		for (String fileName : mapField.keySet()) {
			searchs.addfieldSearch(fileName, mapField.get(fileName));
		}
	}

	private void recursiveParseNode(Element node, Component parentComponent) {
		if (node.getNodeName().equalsIgnoreCase("view") || node.getNodeName().equalsIgnoreCase("list")) {
			Component newParent = parentComponent;
			if (node.getNodeName().equalsIgnoreCase("list")) {
				newParent = listhead;
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
				Listheader listHeader = new Listheader();
				listHeader.setLabel(Translate.translate(ZKEnv.getEnv(), null, field.label));
				listHeader.setParent(parentComponent);
				mapField.put(fieldName, field);
			}
		}
	}

	private void initUI() {
		this.setWidth("600px");
		this.setHeight("400px");
		Vlayout layout = new Vlayout();
		layout.setVflex("1");
		layout.setHflex("1");
		layout.setParent(this);

		Label label = new Label();
		label.setStyle("font-weight: bold; font-size: 16px");
		label.setParent(layout);
		label.setValue("Select: " + controller.getTitle());

		listbox = new Listbox();
		listbox.setParent(layout);
		listbox.setWidth("100%");
		listbox.setVflex("1");
		listbox.setCheckmark(true);
		listbox.setMold("paging");
		// listbox.setAutopaging(true);
		listbox.setPageSize(7);
		listbox.setSizedByContent(true);
		listbox.setSpan(true);
		listbox.addEventListener(Events.ON_SELECT, this);
		listbox.addEventListener(ZulEvents.ON_PAGING, this);
		listbox.setMultiple(isMultiSelect);

		listhead = new Listhead();
		listhead.setParent(listbox);
		listhead.setSizable(true);

		Hbox buttonBox = new Hbox();
		buttonBox.setHflex("1");
		buttonBox.setVflex("min");
		buttonBox.setPack("center");
		buttonBox.setParent(layout);

		Button bt = new Button();
		bt.setAttribute("action", "ok");
		bt.addEventListener(Events.ON_CLICK, this);
		bt.setParent(buttonBox);
		bt.setLabel("OK");

		bt = new Button();
		bt.setAttribute("action", "cancel");
		bt.addEventListener(Events.ON_CLICK, this);
		bt.setParent(buttonBox);
		bt.setLabel("Cancel");
	}

	public static Many2AnySelector getSelector(VEditor editor, String modelName, Boolean isMultiSelect, Filter filter) {
		return new Many2AnySelector(editor, modelName, isMultiSelect, filter);
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getTarget().getAttribute("action") != null) {
			if (event.getTarget().getAttribute("action").equals("ok")) {
				if (isMultiSelect) {
					@SuppressWarnings("unchecked")
					Collection<VObject> value = (Collection<VObject>) editor.getValue();
					if (value == null) {
						value = new HashSet<>();
					}
					ArrayList<VObject> tmp = new ArrayList<>();
					for (VObject obj : value) {
						for (Component item : listbox.getChildren()) {
							if (item instanceof Listitem) {
								if (item.getAttribute("id") != null
										&& item.getAttribute("id").equals(obj.getValue("id"))) {
									tmp.add(obj);
									break;
								}
							}
						}
					}
					value.removeAll(tmp);

					for (Listitem item : listbox.getSelectedItems()) {
						value.add(controller.browse((Integer) item.getAttribute("id")));
					}
					setValue(value);
				} else {
					Listitem item = listbox.getSelectedItem();
					if (item != null) {
						setValue(controller.browse((Integer) item.getAttribute("id")));
					}
				}
			} else if (event.getTarget().getAttribute("action").equals("cancel")) {
				if (isMultiSelect) {
					@SuppressWarnings("unchecked")
					Collection<VObject> value = (Collection<VObject>) editor.getValue();
					setValue(value);
				} else {
					setValue(null);
				}
			}
		} else if (event.getTarget().getAttribute("id") != null) {
			Integer id = (Integer) event.getTarget().getAttribute("id");
			if (isMultiSelect) {
				@SuppressWarnings("unchecked")
				Collection<VObject> value = (Collection<VObject>) editor.getValue();
				value.clear();
				value.add(controller.browse(id));
				setValue(value);
			} else {
				setValue(controller.browse(id));
			}
		} else if (event.getName().equals(ZulEvents.ON_PAGING)) {
			this.invalidate();
		}
		event.stopPropagation();
	}

	private void setValue(Object value) {
		editor.setValue(value);
		this.setParent(null);
		this.detach();
	}

	class VItemRenderer implements ListitemRenderer<Integer> {
		EventListener<Event> listener;
		Map<String, VEditor> mapEditor;

		public VItemRenderer(EventListener<Event> listener) {
			super();
			this.listener = listener;
			mapEditor = new LinkedHashMap<>();
			for (String fieldName : mapField.keySet()) {
				VField field = mapField.get(fieldName);
				VEditor editor = VEditor.getEditor(field);
				mapEditor.put(fieldName, editor);
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public void render(Listitem item, Integer id, int index) throws Exception {
			item.setAttribute("id", id);
			for (String fieldName : mapEditor.keySet()) {
				Listcell cell = new Listcell();
				VEditor editor = mapEditor.get(fieldName);
				Object value = controller.getValue(id, fieldName);
				editor.setValue(value);
				cell.appendChild(editor.getEditorForListView());
				item.appendChild(cell);
			}
			if (editor.getValue() instanceof Collection) {
				if (((Collection<VObject>) editor.getValue()).contains(controller.browse(id))) {
					item.setSelected(true);
				}
			}
			item.addEventListener(Events.ON_DOUBLE_CLICK, listener);
		}
	}

	class ListModel extends AbstractListModel<Integer> {
		private Long count;

		/**
		 * 
		 */
		private static final long serialVersionUID = -3587915228827886117L;
		private int startIndex = 0;
		private int cacheSize = 30;
		private List<Integer> cache;

		private void loadData(int index) {
			startIndex = (index / cacheSize) * cacheSize;
			cache = controller.loadIds(filter, null, startIndex, cacheSize);
		}

		public Integer getItemAt(int index) {
			if (cache == null || index < startIndex || index >= startIndex + cacheSize)
				loadData(index);
			int indexTmp = index - startIndex;
			if (indexTmp < 0) {
				indexTmp = 0;
			}
			return (Integer) cache.get(indexTmp);
		}

		@Override
		public Integer getElementAt(int index) {
			return this.getItemAt(index);
		}

		@Override
		public int getSize() {
			if (count == null)
				count = controller.count(filter);
			return count.intValue();
		}
	}

	@Override
	public void executeSearch(String Whereclause, Map<String, Object> params) {
		// TODO Auto-generated method stub
		filter.hqlWhereClause = Whereclause;
		filter.params = params;
		ListModel model = new ListModel();
		model.setMultiple(isMultiSelect);
		listbox.setModel(model);

	}
}
