package base.view.editor;

import java.math.BigDecimal;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vlayout;

import com.google.maps.model.LatLng;

import base.controller.VController;
import base.controller.VEnv;
import base.model.VField;
import base.model.VObject;
import base.util.Address;
import base.vmap.VMaps;
import base.vmap.VMarker;

public class AddressEditor extends VEditor implements FindAddressHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3619500394850537222L;

	public AddressEditor(VField field) {
		super(field);
	}
	
	private FindAddressEditor findAddressEditor;
	private VMaps map;
	private VMarker marker;

	@Override
	public void initComponent() {
		component = new Vlayout();
		Vlayout layout = (Vlayout) component;
		layout.setWidth("100%");
		layout.setHeight("400px");
		layout.setSclass("z-address-editor");;
		findAddressEditor = new FindAddressEditor("search address", this);
		findAddressEditor.setSclass("z-address-editor-textbox");
		findAddressEditor.handler = this;
		findAddressEditor.setParent(layout);
		findAddressEditor.setHflex("1");

		Div mapHolder = new Div();
		mapHolder.setWidth("100%");
		mapHolder.setVflex("1");
		mapHolder.setParent(layout);
		
		map = new VMaps(mapHolder, false);
		marker = new VMarker();
	}
	
	private Address address;
	
	@Override
	public void setValue(Object inputValue) {
		super.setValue(inputValue);
		if (inputValue != null && inputValue instanceof VObject) {
			VObject addressObj = (VObject) inputValue;
			address = new Address();
			address.setName(addressObj.getValue("name").toString());
			address.setLatitude(((BigDecimal)addressObj.getValue("latitude")).doubleValue());
			address.setLongitude(((BigDecimal)addressObj.getValue("longitude")).doubleValue());
			setAddress(address);
			findAddressEditor.setValue(address);
			findAddressEditor.setText(address.getName());
		}
		else {
			findAddressEditor.setText("");
			setAddress(null);
		}
	}
	
	@Override
	public Object getValue() {
		return value;
	}
	
	@Override
	public void setReadonly(Boolean readonly) {

	}
	
	private void setAddress(Address address) {
		this.address = address;
		if (address != null) {
			LatLng center = new LatLng(address.getLatitude(), address.getLongitude());
			map.setCenter(center);
			map.setZoom(17);
			marker.setPosition(center);
			marker.setParent(map);
		}
		else {
			LatLng center = new LatLng(16.1553317, 106.5511746);
			map.setCenter(center);
			map.setZoom(6);
			map.removeAllMarker();
		}
	}
	
	@Override
	public void onChangeAddress(Address address, int type) {
		Object addressObj = null;
		if (address != null) {
			VController addController = VEnv.sudo().get("Sys.Address");
			addressObj = addController.execute("getAddress", address);
		} else {
			addressObj = null;
		}
		setValue(addressObj);
	}
	@Override
	public Component getEditorForListView() {
		Component editor = new Label();
		if (value != null) {
			((Label)editor).setValue(((VObject)value).getValue("name").toString());
		}
		return editor;
	}
}
