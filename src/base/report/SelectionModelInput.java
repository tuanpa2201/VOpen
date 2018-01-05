package base.report;

import java.io.Serializable;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.ListModelList;

import base.model.VObject;

public class SelectionModelInput extends ReferenceInput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int sizeView = 10;

	public SelectionModelInput(String modelName) {
		super(modelName);
		init();
	}

	public void init() {
		component = new Combobox();
		((Combobox) component).setHflex("1");
		((Combobox) component).setAutocomplete(false);
		component.addEventListener(Events.ON_SELECT, EVENT_SELECT);
		((Combobox) component).setItemRenderer(new ComboitemRenderer<ItemData>() {
			@Override
			public void render(Comboitem item, ItemData data, int index) throws Exception {
				if (index < sizeView) {
					item.setValue(data.getId());
					item.setLabel(data.getLabel());
				} else {
					item.setValue(-2);
					item.setLabel("  ...");
					item.setClass("comboitem-more");
					item.setDisabled(true);
				}

			}
		});
		component.addEventListener(Events.ON_CHANGING, EVENT_ON_SEARCH);
		component.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				((Combobox) event.getTarget()).open();
			}
		});
		loadAllData();
	}

	public void loadAllData() {
		loadData("");
	}

	@Override
	public void reset() {
		loadData("");
		((Combobox) component).setValue("");

	}

	private void loadData(String input) {
		((Combobox) component).setModel(new ListModelList<>(loadByString(input, sizeView + 1)));
	}

	@Override
	public void setValue(Object value) {
		// TODO Auto-generated method stub
		int id = ((VObject) value).getId();
		ItemData item = initItemData(id);
		((Combobox) component).setText(item.getLabel());
		super.setValue(value);
	}

	private EventListener<Event> EVENT_SELECT = new EventListener<Event>() {
		@Override
		public void onEvent(Event events) throws Exception {
			Comboitem item = ((Combobox) events.getTarget()).getSelectedItem();
			if (item != null) {
				Object value = item.getValue();
				if (value != null && !value.equals(-2)) {
					int id = (Integer) value;
					setInputValue(controller.browse(id));
				}
			} else {
				setInputValue(null);
			}
		}
	};

	private void setInputValue(Object value) {
		super.setValue(value);
	}

	private EventListener<Event> EVENT_ON_SEARCH = new EventListener<Event>() {

		@Override
		public void onEvent(Event event) throws Exception {
			// TODO Auto-generated method stub
			InputEvent inputEvent = (InputEvent) event;
			String input = inputEvent.getValue();
			loadData(input);
		}
	};

}
