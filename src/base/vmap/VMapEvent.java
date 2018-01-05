package base.vmap;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

import com.google.maps.model.LatLng;

public class VMapEvent extends Event {
	
	public VMapEvent(String name) {
		super(name);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected LatLng position;
	protected double lat;
	protected double lng;
	protected Component component;
	protected VMaps maps;

	
	public Component getComponent(){
		return this.component;
	}
	public LatLng getPosition() {
		return position;
	}
	public double getLat(){
		return lat;
	}
	public double getLng(){
		return lng;
	}
	
	protected void setPosition(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
		this.position = new LatLng(lat, lng);
	}
	
	public final VMaps getVMaps(){
		return maps;
	}
	
	protected void setPosition(LatLng latLng) {
		this.lat = latLng.lat;
		this.lng = latLng.lng;
		this.position = latLng;
	}
	
	
}
