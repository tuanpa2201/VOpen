package base.view.editor;

import java.util.Iterator;
import java.util.Map;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ListModel;

import base.util.StringUtils;
import modules.tracking.model.Vehicle;

public class ComboboxAutoComplete extends Combobox implements EventListener<Event> {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	private Map<Integer, String> listModel;
//	private Component component;
	
	public ComboboxAutoComplete(Map<Integer, String> model) {
		super();
		this.setAutodrop(true);
		this.setAutocomplete(true);
		listModel = model;
		renderItem(null);
		this.addEventListener(Events.ON_CHANGING, this);
		this.addEventListener(Events.ON_OPEN, this);
	}
	
	public ComboboxAutoComplete() {
		super();
		this.setAutodrop(true);
//		this.setAutocomplete(true);
		this.addEventListener(Events.ON_CHANGING, this);
		this.addEventListener(Events.ON_OPEN, this);
	}
	
	public void setModel(Map<Integer, String> model){
		listModel = model;
		renderItem(null);
	}
	
	public void renderItems(String searchString){
		if(getModel().getClass().equals(Vehicle.class)){
			ListModel<Vehicle> model = getModel();
			if(searchString!=null && searchString.length()>0){
			}
		}
	}
	
	private void renderItem(String searchString){
		this.getItems().clear();
		Iterator<Integer> keys = listModel.keySet().iterator();
		if(searchString != null){
			while(keys.hasNext()){
				Integer key = keys.next();
				String strModel = StringUtils.unAccent(listModel.get(key).trim().toLowerCase());
				
				if(strModel.contains(StringUtils.unAccent(searchString.trim().toLowerCase()))){
					Comboitem comboitem = new Comboitem(listModel.get(key));
					comboitem.setValue(key);
					if(this.getItems().size()>0)
						this.insertBefore(comboitem, this.getItems().get(0));
					else
						comboitem.setParent(this);
				}
//				else {
//					Comboitem comboitem = new Comboitem(listModel.get(key));
//					comboitem.setValue(key);
//					comboitem.setParent(this);
//				}
			}
		} else {
			while(keys.hasNext()){
				Integer key = keys.next();
				Comboitem comboitem = new Comboitem(listModel.get(key));
				comboitem.setValue(key);
				comboitem.setParent(this);
			}
		}
	}
	
	@Override
	public void onEvent(Event event) throws Exception {
		if(event.getName().equals(Events.ON_CHANGING)){
			InputEvent iEvent = (InputEvent) event;
//			if (iEvent.isChangingBySelectBack())
//				return;
			String searchStr = iEvent.getValue();
			renderItem(searchStr);
		} 
		if(event.getName().equals(Events.ON_OPEN)){
			if(this.getItems().size()<this.listModel.size())
				renderItem(null);
		}
	}

}
