package base.view.editor;

import java.util.ArrayList;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;

import base.common.AppLogger;
import base.util.Address;
import base.util.GoogleService;
import base.vmap.IDGenerator;

public class FindAddressEditor extends Combobox implements EventListener<Event> {

	private static final long serialVersionUID = -319085561813037060L;
	private static int TYPING_DELAY = 1000;
	private Combobox component;
	private Address value;
	private String placeHolder;
	ArrayList<Address> address = null;
	public FindAddressHandler handler;
	private Component ctrShowBusy = null;
	HandleTyping handleTyping;

	String searchText = "";
	String oldValue = "";

	public Component getCtrShowBusy() {
		return ctrShowBusy;
	}

	public void setCtrShowBusy(Component ctrShowBusy) {
		this.ctrShowBusy = ctrShowBusy;
	}


	private int searchType;

	public int getSearchType() {
		return searchType;
	}

	public void setSearchType(int searchType) {
		this.searchType = searchType;
	}

	public FindAddressEditor(Address value, String placeHolder, Component ctrShowBusy) {
		super();
		this.value = value;
		this.placeHolder = placeHolder;
		this.ctrShowBusy = ctrShowBusy;
		init();
	}

	public FindAddressEditor(String placeHolder, Component ctrShowBusy) {
		super();
		this.placeHolder = placeHolder;
		this.placeHolder = placeHolder;
		this.ctrShowBusy = ctrShowBusy;
		init();
	}

	private void init() {
		component = this;
		this.setId(IDGenerator.generateStringID());
		component.setPlaceholder(this.placeHolder);
		component.addEventListener(Events.ON_CHANGING, this);
		component.addEventListener(Events.ON_CHANGE, this);
		component.addEventListener(Events.ON_SELECT, this);
		component.setAutodrop(true);
	}

	public Combobox getComponent() {
		return component;
	}

	public void setComponent(Combobox component) {
		this.component = component;
	}

	public Address getSearchValue() {
		if (value == null) {
			value = Address.getUnknowAddress();
		}
		if (value.getLatitude() == 0 && searchText.trim().length() > 0) {
			value.setName(searchText);
		}
		if (value.getLatitude() == 0 && value.getLatitude() == 0 && searchText.trim().length() == 0) {
			value.setName(searchText.trim());
		}

		return value;
	}

	public void setValue(Address value) {
		this.value = value;
		this.searchText = value.getName();
	}

	public void refreshList() {
		component.getItems().clear();

		if (address != null) {
			for (Address address2 : address) {
				Comboitem item = new Comboitem();
				item.setLabel(address2.getName());
				item.setValue(address2);
				component.getItems().add(item);
			}
		} else {
			component.setSelectedIndex(-1);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onEvent(Event event) throws Exception {
		if (this.getDesktop() != null && !this.getDesktop().isServerPushEnabled()) {
			this.getDesktop().enableServerPush(true);
		}
		if (event.getName().equalsIgnoreCase(Events.ON_SELECT)) {
			onSelectAddress((SelectEvent) event);
		}
		if (!(event instanceof InputEvent)) {
			return;
		}

		InputEvent inputEvent = (InputEvent) event;

		if (inputEvent.getName().equalsIgnoreCase(Events.ON_CHANGING)) {
			oldValue = searchText;
			searchText = inputEvent.getValue();
			
			if (searchText.trim().length() == 0) {
				clearComboboxItem();
				return;
			}

			if (searchText.length() < oldValue.length())
				return;
			if (!inputEvent.isChangingBySelectBack()) {
				try {
					if (ctrShowBusy != null) {
						Clients.showBusy(ctrShowBusy, "");
					}
					if (searchText.endsWith("=")) {
						if (handleTyping != null && handleTyping.isAlive()) {
							handleTyping.interrupt();
							handleTyping = null;
						}
						Thread thread = new Thread(new SearchRun(searchText, this));
						thread.start();
					} else {
						if (handleTyping != null && handleTyping.isAlive()) {
							handleTyping.justTyping(searchText);
						} else {
							handleTyping = new HandleTyping(this);
							handleTyping.justTyping(searchText);
							handleTyping.start();
						}
					}
				} catch (Exception e) {
					AppLogger.logDebug.debug(e.toString());
				}
			}
		} else if (inputEvent.getName().equalsIgnoreCase(Events.ON_CHANGE)) {

			changeValue(component.getValue());
		}
	}

	private void clearComboboxItem() {
		component.getItems().clear();
		Comboitem item = new Comboitem();
		item.setLabel(Address.getUnknowAddress().getName());
		item.setValue(Address.getUnknowAddress());
		component.getItems().add(item);
	}

	private void onSelectAddress(@SuppressWarnings("rawtypes") SelectEvent event) {
		if (component.getSelectedItem() != null) {
			value = component.getSelectedItem().getValue();
		} else {
			value = Address.getUnknowAddress();
		}
	}

	private void changeValue(String searchText) {
		if (searchText == null || searchText.trim().isEmpty()) {
			value = new Address();
			component.setSelectedIndex(-1);
		} else {

			if (address != null) {
				if (component.getSelectedIndex() == -1 && component.getItemCount() > 0) {
					value = new Address();
					value.setName(searchText);
				} else {

					if (component.getSelectedItem() != null)
						value = component.getSelectedItem().getValue();
					else {
						value = new Address();
						component.setSelectedIndex(-1);
					}
				}
			} else {
				if (component.getSelectedIndex() > -1) {
					value = component.getSelectedItem().getValue();
				} else
					value = new Address();
			}
		}
		onChangeValue();
	}

	public void onChangeValue() {
		value.setNote(searchText);
		if (handler != null)
			handler.onChangeAddress(value, searchType);
	}

	private ArrayList<Address> searchGooglePlace(String seachText) {
		ArrayList<Address> retVal = GoogleService.searchPlaceGoogleMap(searchText, null);
		if (seachText == null)
			return retVal;
		return retVal;
	}

	class SearchRun implements Runnable {

		private String searchText = "";
		private FindAddressEditor findAddressEditor;

		public SearchRun(String searchText, FindAddressEditor findAddressEditor) {
			this.searchText = searchText;
			this.findAddressEditor = findAddressEditor;
		}

		@Override
		public void run() {
			AppLogger.logDebug.debug("SearchRun|start thread");
			try {
				if (searchText == null) {
					return;
				}
				if (searchText.trim() == "") {
					return;
				}
				String endWith = searchText.trim();
				try {
					if (endWith.endsWith("=")) {
						// Search trong bang viet tat
						// address = searchInBTV(seachText);
//						address = searchInBTV(searchText, findAddressEditor.getListCompany());
					} else if (searchText.length() >= 3) {
						// Search google place or vietbando 
						address = searchGooglePlace(searchText);
					}
				} catch (Exception e) {
					address = null;
				}
				
				Executions.schedule(findAddressEditor.getDesktop(), new EventListener<Event>() {

					@Override
					public void onEvent(Event arg0) throws Exception {
						findAddressEditor.refreshList();
						if (ctrShowBusy != null) {
							Clients.clearBusy(ctrShowBusy);
						}
					}
				}, null);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
			AppLogger.logDebug.debug("SearchRun|end thread");
		}

		@Override
		protected void finalize() throws Throwable {
			super.finalize();
		}

	}

	class HandleTyping extends Thread {
		long lastTimeTyping = System.currentTimeMillis();
		String searchText = "";
		private FindAddressEditor findAddressEditor;

		public HandleTyping(FindAddressEditor findAddressEditor) {
			super();
			this.findAddressEditor = findAddressEditor;
		}

		@Override
		public void run() {
			long lastTimeTyping = System.currentTimeMillis();
			while (true) {
				if (System.currentTimeMillis() - lastTimeTyping > TYPING_DELAY) {
					
					SearchRun s = new SearchRun(searchText, findAddressEditor);
					s.run();
					return;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					return;
				}
			}
		}

		public void justTyping(String searchText) {
			this.searchText = searchText;
			lastTimeTyping = System.currentTimeMillis();
		}
	}
}
