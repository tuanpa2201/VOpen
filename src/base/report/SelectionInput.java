package base.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.ItemRenderer;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Selectbox;

public class SelectionInput extends InputReport implements Serializable {

	private static final long serialVersionUID = 1L;

	public SelectionInput() {
		super();
		init();
	}

	public void init() {
		component = new Selectbox();
		((Selectbox) component).setHflex("1");
		((Selectbox) component).setStyle("height: 24px");
		((Selectbox) component).setItemRenderer(new ItemRenderer<InputData>() {
			@Override
			public String render(Component arg0, InputData input, int arg2) throws Exception {
				// TODO Auto-generated method stub
				return input.getLabel();
			}
		});
		component.addEventListener(Events.ON_SELECT, EVENT_SELECT);
	}

	@Override
	public void setValue(Object value) {
		super.setValue(value);
	}

	private EventListener<Event> EVENT_SELECT = new EventListener<Event>() {
		@Override
		public void onEvent(Event event) throws Exception {
			int select = ((Selectbox) event.getTarget()).getSelectedIndex();
			if (select >= 0) {
				Object ob = ((Selectbox) event.getTarget()).getModel().getElementAt(select);
				setValue(((InputData) ob).getValue());
			} else {
				setValue(null);
			}
		}
	};

	public void setItems(Object... params) {
		List<InputData> data = new ArrayList<>();
		for (int i = 0; i < params.length; i += 2) {
			if (params.length > i + 1 && i % 2 == 0) {
				InputData inputData = new InputData();
				inputData.setLabel(params[i].toString());
				inputData.setValue(params[i + 1]);
				data.add(inputData);
			}
		}
		ListModelList<InputData> model = new ListModelList<>(data);
		if (data.size() > 0) {
			model.addToSelection(data.get(0));
			setValue(data.get(0).getValue());
		}
		((Selectbox) component).setModel(model);
	}

	class InputData {
		private Object value;
		private String label;

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

	}
}
