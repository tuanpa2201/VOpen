package base.report;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.AbstractListModel;

import base.model.VObject;
import base.util.StringUtils;

public class SelectionsModelInput extends ReferenceInput implements Serializable, EventListener<Event> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean isUserSelected = false;

	public SelectionsModelInput(String modelName) {
		super(modelName);
		init();
		value = new HashSet<>();
	}

	public void init() {
		component = new VChosenBox();
		((VChosenBox) component).setHflex("1");
		component.addEventListener("onSearch", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				String strSearch = event.getData() + "";
				search(strSearch);

			}
		});
		component.addEventListener("onChosen", EVENT_CHOSEN);
		loadAllData();
	}

	private void search(String strSearch) {
		if (fieldDisplay != null) {
			if (!StringUtils.isEmpty(strSearch)) {
				addSearch(fieldDisplay, strSearch);
			} else {
				clearSearch(fieldDisplay);
			}
			reset();
		}

	}

	@Override
	@SuppressWarnings("unchecked")
	public void setValue(Object value) {
		if (value instanceof Collection<?>) {
			Set<ItemData> itemdatas = new HashSet<>();
			for (VObject ob : (Collection<VObject>) value) {
				itemdatas.add(initItemData(ob.getId()));
			}
			((VChosenBox) component).setSelectedObjects(itemdatas);
		}
		super.setValue(value);
	}

	public void setValueInput(Object value) {
		super.setValue(value);
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		@SuppressWarnings("unchecked")
		Set<VObject> lstRes = (Set<VObject>) super.getValue();
		if (lstRes.size() == 0) {
			List<ItemData> data = getData();
			for (ItemData itemData : data) {
				lstRes.add(controller.browse(itemData.getId()));
			}
			isUserSelected = false;
		}
		return lstRes;
	}

	public void loadAllData() {
		((VChosenBox) component).setPagingModel(new Model());
	}

	@Override
	public void reset() {
		super.reset();
		((VChosenBox) component).setPagingModel(new Model());
		value = new HashSet<>();
		onChangeListener.onChangedValue(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void filterData(Object data) {
		if (referenceInput != null) {
			if (((Set<VObject>) data).size() > 0) {
				referenceInput.addSearch(this.paramName, (Set<VObject>) data);
			} else {
				referenceInput.clearSearch(this.paramName);
				;
			}
			referenceInput.reset();
		}
	}

	public void initValue() {
		onChangeListener.onChangedValue(this);
	}

	private EventListener<Event> EVENT_CHOSEN = new EventListener<Event>() {
		@SuppressWarnings("unchecked")
		@Override
		public void onEvent(Event events) throws Exception {
			Object value = events.getData();
			Set<VObject> vObs = new HashSet<>();
			if (value != null) {
				for (ItemData ob : (Set<ItemData>) value) {
					vObs.add(controller.browse(ob.getId()));
				}
			}
			if (vObs.size() > 0) {
				isUserSelected = true;
			}
			setValueInput(vObs);
		}
	};

	public boolean isUserSelected() {
		return isUserSelected;
	}

	@Override
	public void onEvent(Event arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	class Model extends AbstractListModel<ItemData> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public ItemData getElementAt(int index) {
			// TODO Auto-generated method stub

			return getItemDataAt(index);
		}

		@Override
		public int getSize() {
			// TODO Auto-generated method stub
			return getTotalSize();
		}

		public void search(String strSearch) {
		}
	}

}
