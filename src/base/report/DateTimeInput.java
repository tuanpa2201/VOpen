package base.report;

import java.io.Serializable;
import java.util.Date;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Datebox;

public class DateTimeInput extends InputReport implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DateTimeInput() {
		super();
		init();
	}

	public void init() {
		component = new Datebox();
		((Datebox) component).setHflex("1");
		((Datebox) component).setFormat("dd/MM/yyyy HH:mm");
		component.addEventListener(Events.ON_CHANGE, EVENT_CHANGE);
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof Date) {
			((Datebox) component).setValue((Date) value);
		}
		super.setValue(value);
	}

	private EventListener<Event> EVENT_CHANGE = new EventListener<Event>() {
		@Override
		public void onEvent(Event events) throws Exception {
			if (((Datebox) events.getTarget()).getValue() != null) {
				Object value = ((Datebox) events.getTarget()).getValue();
				setValue(value);
			} else {
				setValue(null);
			}
		}
	};

}
