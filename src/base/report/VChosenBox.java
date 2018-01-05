package base.report;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Bandpopup;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Span;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;

import base.util.StringUtils;
import base.util.Translate;
import base.util.ZKEnv;

public class VChosenBox extends Bandbox {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Set<ItemData> selectedObjects;
	private Textbox textBoxSearch;
	private AbstractListModel<ItemData> listModel;

	public VChosenBox() {
		// TODO Auto-generated constructor stub
		super();
		selectedObjects = new HashSet<>();
		initUI();
	}

	private Div divMain;

	private void initUI() {
		// TODO Auto-generated method stub
		this.addEventListener(Events.ON_OPEN, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				OpenEvent openEvent = (OpenEvent) event;
				if (openEvent.isOpen()) {
					textBoxSearch.setFocus(true);
					if (selectedObjects.size() > 0) {
						showListChosen(getSwitchButtion());
					} else {
						showListValues(getSwitchButtion());
					}
				}

			}
		});
		Bandpopup popup = new Bandpopup();
		popup.setStyle("max-width: 350px");
		this.setReadonly(true);
		popup.setParent(this);
		Vlayout vlayout = new Vlayout();
		vlayout.setSclass("z-vl-vchosen-conten");
		vlayout.setParent(popup);
		Hlayout hl = new Hlayout();
		hl.setParent(vlayout);
		initSearch(hl);
		divMain = new Div();
		divMain.setParent(vlayout);
		getListValues().setParent(divMain);
		getGridChosen().setParent(divMain);
		getGridChosen().setVisible(false);
	}

	private void initSearch(Hlayout hlayput) {
		textBoxSearch = new Textbox();
		textBoxSearch.setPlaceholder("Search...");
		textBoxSearch.setHflex("1");
		textBoxSearch.setParent(hlayput);
		textBoxSearch.addForward(Events.ON_OK, textBoxSearch, Events.ON_CHANGE);
		textBoxSearch.addEventListener(Events.ON_CHANGE, EVENT_ON_SEARCH);
		getSwitchButtion().setParent(hlayput);
	}

	private Span switchButtion;

	private Span getSwitchButtion() {
		if (switchButtion == null) {
			switchButtion = new Span();
			switchButtion.setSclass("fa fa-caret-right z-vchosen-refresh");
			switchButtion.setPopup(Translate.translate(ZKEnv.getEnv(), null, "Refresh List Vehicle"));
			switchButtion.addEventListener(Events.ON_CLICK, LISTENER_SWITCH_EVENT);
		}
		return switchButtion;
	}

	private Listbox listValues;

	private Listbox getListValues() {
		if (listValues == null) {
			listValues = new Listbox();
			listValues.setCheckmark(true);
			listValues.setMold("paging");
			listValues.setPageSize(8);
			listValues.addEventListener(Events.ON_SELECT, EVENT_ON_SELECT);
			listValues.setItemRenderer(new ListitemRenderer<ItemData>() {
				@Override
				public void render(Listitem item, ItemData data, int index) throws Exception {
					item.setLabel(data.getLabel());
					item.setValue(data);
					for (ItemData itemData : selectedObjects) {
						if (itemData.equals(data)) {
							item.setSelected(true);
							break;
						}
					}
				}
			});
		}
		return listValues;
	}

	private Grid gridChosen;

	private Grid getGridChosen() {
		if (gridChosen == null) {
			gridChosen = new Grid();
			gridChosen.setMold("paging");
			gridChosen.setPageSize(8);
			Columns cols = new Columns();
			cols.setParent(gridChosen);
			Column col = new Column();
			col.setParent(cols);
			col = new Column();
			col.setWidth("50px");
			col.setParent(cols);
			gridChosen.setRowRenderer(new RowRenderer<ItemData>() {
				@Override
				public void render(Row row, ItemData data, int index) throws Exception {
					// TODO Auto-generated method stub
					row.setValue(data.getId());
					Label label = new Label(data.getLabel());
					row.appendChild(label);
					Cell cell = new Cell();
					cell.setAlign("center");
					Span remove = new Span();
					remove.setStyle("font-size:18px;cursor: pointer");
					remove.setSclass("fa fa-trash");
					remove.setParent(cell);
					remove.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
						@Override
						public void onEvent(Event event) throws Exception {
							// TODO Auto-generated method stub
							removeSelection(data);
						}
					});
					cell.setParent(row);
				}
			});
		}
		return gridChosen;
	}

	private void removeSelection(ItemData data) {
		selectedObjects.remove(data);
		refreshSelections();
	}

	public void setModel(Collection<ItemData> model) {
		listModel = new ListModelList<>(model);
		listModel.setMultiple(true);
		getListValues().setModel(listModel);
	}

	public void setPagingModel(AbstractListModel<ItemData> pagingModel) {
		listModel = pagingModel;
		listModel.setMultiple(true);
		getListValues().setModel(listModel);
	}

	public void setSelectedObjects(Set<ItemData> itemsData) {
		this.selectedObjects = itemsData;
		refreshSelections();
	}

	@SuppressWarnings("unchecked")
	private EventListener<Event> EVENT_ON_SELECT = new EventListener<Event>() {
		@Override
		public void onEvent(Event event) throws Exception {
			// TODO Auto-generated method stub
			SelectEvent<Listbox, ItemData> selectedsEvent = (SelectEvent<Listbox, ItemData>) event;
			Set<ItemData> selecteds = selectedsEvent.getSelectedObjects();
			for (ItemData itemSelected : selecteds) {
				if (!selectedObjects.contains(itemSelected)) {
					selectedObjects.add(itemSelected);
				}
			}
			Set<ItemData> unselected = selectedsEvent.getUnselectedObjects();

			for (ItemData itemUnSelected : unselected) {
				if (selectedObjects.contains(itemUnSelected)) {
					selectedObjects.remove(itemUnSelected);
				}

			}
			setText();
			postChosenEvent();

		}
	};

	public void setText() throws org.zkoss.zk.ui.WrongValueException {
		String text = "";
		for (ItemData itemData : selectedObjects) {
			text = text + itemData.getLabel() + ",";
		}
		super.setText(StringUtils.Trim(text, ","));
	};

	private void postChosenEvent() {
		Events.postEvent("onChosen", this, selectedObjects);
	}

	private EventListener<Event> EVENT_ON_SEARCH = new EventListener<Event>() {
		@Override
		public void onEvent(Event event) throws Exception {
			// TODO Auto-generated method stub
			String input = ((Textbox) event.getTarget()).getValue();
			search(input);

		}
	};
	private EventListener<Event> LISTENER_SWITCH_EVENT = new EventListener<Event>() {
		@Override
		public void onEvent(Event event) throws Exception {
			// TODO Auto-generated method stub
			HtmlBasedComponent taget = ((HtmlBasedComponent) event.getTarget());
			if (taget.getSclass().contains("fa-caret-right")) {
				showListChosen(taget);
			} else {
				showListValues(taget);
			}
		}
	};

	private void showListValues(HtmlBasedComponent taget) {
		taget.setSclass("fa fa-caret-right z-vchosen-refresh");
		getListValues().setVisible(true);
		getListValues().setModel(listModel);
		hideListChosen();
	}

	private void showListChosen(HtmlBasedComponent taget) {
		taget.setSclass("fa fa-caret-left z-vchosen-refresh");
		getGridChosen().setVisible(true);
		getGridChosen().setModel(new ListModelList<>(selectedObjects));
		hideListValues();
	}

	private void hideListChosen() {
		getGridChosen().setVisible(false);
	}

	private void hideListValues() {
		getListValues().setVisible(false);
	}

	public void refresh() {
		if (listModel != null) {
			listModel.clearSelection();
		}
		selectedObjects.clear();
		textBoxSearch.setValue("");
		search("");
		this.setText("");
		postChosenEvent();
	}

	private void refreshSelections() {
		if (listModel != null) {
			listModel.setSelection(selectedObjects);
		}
		getGridChosen().setModel(new ListModelList<>(selectedObjects));
		setText();
		postChosenEvent();
	}

	private void search(String input) {
		// TODO Auto-generated method stub
		showListValues(getSwitchButtion());
		Events.postEvent("onSearch", this, input);
	}
}
