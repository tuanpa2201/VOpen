package base.vmap;

import com.google.maps.model.LatLng;

public class LatLngBounds {
	public LatLng southWest;
	public LatLng northEast;
	public LatLngBounds(LatLng southWest, LatLng northEast) {
		super();
		this.southWest = southWest;
		this.northEast = northEast;
	}
}
