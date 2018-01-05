package base.vmap;

import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Messagebox;

import com.google.maps.model.LatLng;

import base.util.MapUtils;


public class VMaps extends VComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private LatLngBounds bounds;
	private LatLng center = new LatLng(16.3776538, 105.0040875);
	private String mapType = "ROADMAP";
	private int tilt = 0;
	private int zoom = 15;
	private LatLng mousePosition;
	private String mouseAddress;
	private VMaps self;
	
	public VMaps(Component parent, boolean isMarkerCluster) {
		super();
		setParent(parent);
		this.setHeight("100%");
		this.setWidth("100%");
		this.self = this;
		this.addJSScriptSynch("vietek.mapController.createMap('" + getId() + "', " + isMarkerCluster + ")");
		listenMethod();
	}
	
	

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.addJSScriptSynch("vietek.mapController.deleteMap('" + getId() + "')");
	}
	
	public String getMapTypeId(){
		return mapType;
	}
	
	public void startDrawPolygon(){
		this.addJSScriptSynch("vietek.mapController.drawPolygon('" + getId() + "')");
	}
	
	/**
	 * Set map type for map
	 * @param mapType : ROADMAP, SATELLITE, HYBRID, TERRAIN
	 */
	public void setMapType(MapType mapType){
		this.mapType = mapType.getValue();
		this.addJSScriptSynch("vietek.mapController.setMapTypeId('" + getId() + "', '" + this.mapType + "')");
	}
	
	/**
	 * Returns the current angle of incidence of the map, in degrees from the viewport plane to the map plane.
	 * The result will be 0 for imagery taken directly overhead or 45 for 45° imagery. 
	 * 45° imagery is only available for SATELLITE and HYBRID map types, within some locations, and at some zoom levels. 
	 * @return
	 */
	public int getTilt(){
		return this.tilt;
	}
	
	/**
	 * 
	 * @param tilt: number| 0 - 45
	 */
	public void setTilt(int tilt) {
		this.tilt = tilt;
		this.addJSScriptSynch("vietek.mapController.setTilt('" + getId() + "'," + tilt + ")");
	}
	
	public int getZoom(){
		return this.zoom;
	}
	
	public void setZoom(int zoom) {
		this.zoom = zoom;
		this.addJSScriptSynch("vietek.mapController.setZoom('" + getId() + "'," + zoom + ")");
	}

	/**
	 * Returns the position displayed at the center of the map
	 * @return
	 */
	public LatLng getCenter() {
		return center;
	}
	
	public LatLng getMousePosition(){
		return mousePosition;
	}

	/**
	 * Set position display at the center of the map
	 * @param center
	 */
	public void setCenter(LatLng center) {
		this.center = center;
		this.addJSScriptSynch("vietek.mapController.setCenter('" +getId() + "'," + center.lat + "," + center.lng + ")");
	}
	
	public void setCenter(double latitude, double longitude) {
		LatLng center = new LatLng(latitude, longitude);
		setCenter(center);
	}

	/**
	 * Returns the lat/lng bounds of the current viewport
	 */
	public LatLngBounds getBounds() {
		return bounds;
	}
	
	public void setBounds(LatLngBounds bounds){
		this.bounds = bounds;
	}
	
	/**
	 * Changes the center of the map by the given distance in pixels.
	 * If the distance is less than both the width and height of the map, the transition will be smoothly animated.
	 * Note that the map coordinate system increases from west to east (for x values) and north to south (for y values).
	 * @param x
	 * @param y
	 */
	public void panBy(int x, int y){
		this.addJSScriptSynch("vietek.mapController.panBy('" + getId() + "'," + x + "," + y + ")");
//		Events.postEvent(new Event("runJSScript", this, "vietek.mapController.panBy('" + getId() + "'," + x + "," + y + ")"));
	}
	
	/**
	 * Changes the center of the map to the given LatLng.
	 * If the change is less than both the width and height of the map, the transition will be smoothly animated.
	 * @param possion
	 */
	public void panTo(LatLng possion){
		double lat = possion.lat;
		double lng = possion.lng;
		this.addJSScriptSynch("vietek.mapController.panTo('" + getId() + "'," + lat + "," + lng + ")");
	}
	
	public void panTo(double latitude, double longitude){
		this.center = new LatLng(latitude, longitude);
		this.addJSScriptSynch("vietek.mapController.panTo('" + getId() + "'," + latitude + "," + longitude + ")");
	}
	
	public void closeAllInfo(){
		String jsScript = "vietek.mapController.closeAllInfo('" + getId() + "')";
		this.addJSScriptSynch(jsScript);
		for(Component comp : this.getChildren()){
			if(comp instanceof VMarker){
				VMarker marker = (VMarker)comp;
				if(marker.isOpen()){
					marker.setOpen(false);
				}
			}
		}
	}

	/**
	 * Pans the map by the minimum amount necessary to contain the given LatLngBounds
	 * @param bounds
	 */
	public void panToBounds(LatLngBounds bounds){
		if(bounds.southWest == null || bounds.southWest == null){
			String msg = "You must set value for both NorthEast and SouthWest of LatLngBounds";
			Messagebox.show(msg, "Error", Messagebox.OK, Messagebox.ERROR);
		} else {
			JSONObject jsonObject = new JSONObject();
			JSONObject jsonValue = new JSONObject();
			jsonValue.put("NorthEastLat", bounds.northEast.lat);
			jsonValue.put("NorthEastLng", bounds.northEast.lng);
			jsonValue.put("SouthWestLat", bounds.southWest.lat);
			jsonValue.put("SouthWestLng", bounds.southWest.lng);
			jsonObject.put("bounds", jsonValue);
			this.addJSScriptSynch("vietek.mapController.panToBounds('" + getId() + "','" + jsonObject + "')");
		}
	}
	
	/**
	 * Sets the viewport to contain the given bounds
	 * @param latLngs
	 */
	public void fitBounds(LatLngBounds bounds) {
		if(bounds.southWest == null || bounds.southWest == null){
			String msg = "You must set value for both NorthEast and SouthWest of LatLngBounds";
			Messagebox.show(msg, "Error", Messagebox.OK, Messagebox.ERROR);
		} else {
			JSONObject jsonObject = new JSONObject();
			JSONObject jsonValue = new JSONObject();
			jsonValue.put("NorthEastLat", bounds.northEast.lat);
			jsonValue.put("NorthEastLng", bounds.northEast.lng);
			jsonValue.put("SouthWestLat", bounds.southWest.lat);
			jsonValue.put("SouthWestLng", bounds.southWest.lng);
			jsonObject.put("bounds", jsonValue);
			this.addJSScriptSynch("vietek.mapController.fitBounds('" + getId() + "','" + jsonObject + "')");
		}
	}
	
	public boolean hideAllMarkers(boolean flag){
		for(Component comp : this.getChildren()){
			if(comp instanceof VMarker){
				((VMarker)comp).setVisible(!flag);
			}
		}
		return flag;
	}
	
	public String getMouseAddress() {
		if(mousePosition != null){
			mouseAddress = MapUtils.getAddressGoogle(mousePosition.lat, mousePosition.lng);
		}
		return mouseAddress;
	}



	private void listenMethod() {
		this.addEventListener("onIdle", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				VMapEvent mapEvent = new VMapEvent(VMapEvents.ON_VMAP_IDLE);
				Events.postEvent(self, mapEvent);
				event.stopPropagation();
			}
		});
		
		this.addEventListener("onClickVMap", new EventListener<Event>() {
			@Override
			public void onEvent(Event arg0) throws Exception {
				try {
					JSONObject jsonObject =(JSONObject)arg0.getData();
					JSONObject object = (JSONObject)jsonObject.get("data");
					Double lat = Double.valueOf(object.get("latitude")+"");
					Double lng = Double.valueOf(object.get("longtitude")+"");
					mouseAddress = object.get("address")+"";
					mousePosition = new LatLng(lat, lng);
					VMapEvent mapEvent = new VMapEvent(VMapEvents.ON_VMAP_CLICK);
					mapEvent.maps = self;
					mapEvent.component = self;
					if(mousePosition != null)
						mapEvent.setPosition(mousePosition.lat, mousePosition.lng);
					Events.postEvent(self, mapEvent);
					arg0.stopPropagation();
				}
				catch (Exception e) {
					//ignore
				}
			}
		});
		this.addEventListener("onMouseMoveVMap", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				try {
					JSONObject jsonObject =(JSONObject)event.getData();
					JSONObject object = (JSONObject)jsonObject.get("data");
					Double lat = Double.valueOf(object.get("latitude")+"");
					Double lng = Double.valueOf(object.get("longtitude")+"");
					mousePosition = new LatLng(lat, lng);
					VMapEvent event2 = new VMapEvent(VMapEvents.ON_VMAP_MOUSE_MOVE);
					event2.component = self;
					event2.maps = self;
					event2.setPosition(lat, lng);
					Events.postEvent(self, event2);
					event.stopPropagation();
				}
				catch (Exception e) {
					//ignore
				}
			}
		});
		this.addEventListener("onRightClickVMap", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				try {
					if(event.getTarget().getId().equals(getId())){
						JSONObject jsonObject =(JSONObject)event.getData();
						JSONObject object = (JSONObject)jsonObject.get("data");
						Double lat = (Double)object.get("latitude");
						Double lng = (Double)object.get("longtitude");
						mousePosition = new LatLng(lat, lng);
						VMapEvent mapEvent = new VMapEvent(VMapEvents.ON_VMAP_RIGHT_CLICK);
						mapEvent.component = self;
						mapEvent.maps = self;
						mapEvent.setPosition(lat, lng);
						Events.postEvent(self, mapEvent);
						event.stopPropagation();
					}
				}
				catch (Exception e) {
					//ignore
				}
			}
		});
		this.addEventListener("onZoomChangedVmap", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				try {
					zoom = (int)event.getData();
					VMapEvent mapEvent = new VMapEvent(VMapEvents.ON_VMAP_ZOOM_CHANGED);
					mapEvent.component = self;
					mapEvent.maps = self;
					if(mousePosition != null)
						mapEvent.setPosition(mousePosition.lat, mousePosition.lng);
					Events.postEvent(self, mapEvent);
					event.stopPropagation();
				}
				catch (Exception e) {
					//ignore
				}
			}
		});
		this.addEventListener("onDragVmap", new EventListener<Event>() {
			@Override
			public void onEvent(Event arg0) throws Exception {
				try {
					VMapEvent mapEvents = new VMapEvent(VMapEvents.ON_VMAP_DRAG);
					mapEvents.component = self;
					if(mousePosition != null)
						mapEvents.setPosition(mousePosition.lat, mousePosition.lng);
					Events.postEvent(self, mapEvents);
					arg0.stopPropagation();
				}
				catch (Exception e) {
					//ignore
				}
			}
		});
		this.addEventListener("onDragStartVmap", new EventListener<Event>() {
			@Override
			public void onEvent(Event arg0) throws Exception {
				try {
					VMapEvent mapEvents = new VMapEvent(VMapEvents.ON_VMAP_DRAG_START);
					mapEvents.component = self;
					mapEvents.maps = self;
					if(mousePosition != null)
						mapEvents.setPosition(mousePosition.lat, mousePosition.lng);
					Events.postEvent(self, mapEvents);
					arg0.stopPropagation();
				}
				catch (Exception e) {
					//ignore
				}
			}
		});
		this.addEventListener("onDragEndVmap", new EventListener<Event>() {
			@Override
			public void onEvent(Event arg0) throws Exception {
				try {
					VMapEvent mapEvents = new VMapEvent(VMapEvents.ON_VMAP_DRAG_END);
					mapEvents.component = self;
					mapEvents.maps = self;
					if(center != null)
						mapEvents.setPosition(center);
					Events.postEvent(self, mapEvents);
					arg0.stopPropagation();
				}
				catch (Exception e) {
					//ignore
				}
			}
		});
		this.addEventListener("onBoundsChangedVMap", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				try {
					JSONObject jsonObject = (JSONObject) event.getData();
					JSONObject object = (JSONObject) jsonObject.get("bounds");
					Double northEastLat = Double.parseDouble(object.get("NorthEastLat")+"");
					Double northEastLng = Double.parseDouble(object.get("NorthEastLng")+"");
					Double southWestLat = Double.parseDouble(object.get("SouthWestLat")+"");
					Double southWestLng = Double.parseDouble(object.get("SouthWestLng")+"");
					LatLng southWest = new LatLng(southWestLat, southWestLng);
					LatLng northEast = new LatLng(northEastLat, northEastLng);
					event.stopPropagation();
					if (bounds != null 
							&& bounds.southWest.lat == southWest.lat 
							&& bounds.southWest.lng == southWest.lng
							&& bounds.northEast.lat == northEast.lat
							&& bounds.northEast.lng == northEast.lng) {
						
						return;
					}
					bounds = new LatLngBounds(southWest, northEast);
					self.bounds = new LatLngBounds(southWest, northEast);
					VMapEvent mapEvent = new VMapEvent(VMapEvents.ON_VMAP_BOUNDS_CHANGED);
					mapEvent.maps = self;
					if(mousePosition != null){
						mapEvent.setPosition(mousePosition.lat, mousePosition.lng);
					}
					mapEvent.component = self;
					Events.postEvent(self, mapEvent);
				}
				catch (Exception e) {
					//ignore
				}
			}
		});
		this.addEventListener("onCenterChangeVMap", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				try {
					JSONObject jsonObject = (JSONObject) event.getData();
					JSONObject object = (JSONObject) jsonObject.get("center");
					Double lat = Double.parseDouble(object.get("latitude")+"");
					Double lng = Double.parseDouble(object.get("longtitude")+"");
					center = new LatLng(lat, lng);
					VMapEvent mapEvent = new VMapEvent(VMapEvents.ON_VMAP_CENTER_CHANGE);
					mapEvent.component = self;
					mapEvent.maps = self;
					mapEvent.setPosition(lat, lng);
					Events.postEvent(self, mapEvent);
					event.stopPropagation();
				}
				catch (Exception e) {
					//ignore
				}
			}
		});
		this.addEventListener("onTypeChangedVMap", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				try {
					self.mapType = ((String)event.getData()).toUpperCase();
					VMapEvent mapEvent = new VMapEvent(VMapEvents.ON_VMAP_TYPE_CHANGED);
					mapEvent.component = self;
					mapEvent.maps = self;
					mapEvent.setPosition(mousePosition);
					Events.postEvent(self, mapEvent);
					event.stopPropagation();
				}
				catch (Exception e) {
					//ignore
				}
			}
		});
		this.addEventListener("onVMarkerClick", new EventListener<Event>() {
			@Override
			public void onEvent(Event arg0) throws Exception {
				try {
					JSONObject jsonObject = (JSONObject) arg0.getData();
					JSONObject object = (JSONObject) jsonObject.get("data");
					String markerId = object.get("markerId")+"";
					VMarker marker = getMarker(markerId);
					VMapEvent mapEvents = new VMapEvent(VMapEvents.ON_VMARKER_CLICK);
					mapEvents.component = marker;
					mapEvents.maps = self;
					mapEvents.setPosition(marker.getPosition());
					Events.postEvent(self, mapEvents);
					arg0.stopPropagation();
				}
				catch (Exception e) {
					//ignore
				}
			}
		});
		this.addEventListener("onVMarkerDrag", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				try {
					JSONObject jsonObject = (JSONObject) event.getData();
					JSONObject object = (JSONObject) jsonObject.get("data");
					String markerId = String.valueOf(object.get("markerId")+"");
					Double lat = Double.parseDouble(object.get("latitude")+"");
					Double lng = Double.parseDouble(object.get("longtitude")+"");
					getMarker(markerId).setLatLng(new LatLng(lat, lng));
					VMapEvent mapEvent = new VMapEvent(VMapEvents.ON_VMARKER_DRAG);
					mapEvent.setPosition(lat, lng);
					mapEvent.maps = self;
					mapEvent.component = getMarker(markerId);
					Events.postEvent(self, mapEvent);
				}
				catch (Exception e) {
					//ignore
				}
			}
		});
		this.addEventListener("onVMarkerDragStart", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				try {
					JSONObject jsonObject = (JSONObject) event.getData();
					JSONObject object = (JSONObject) jsonObject.get("data");
					String markerId = String.valueOf(object.get("markerId")+"");
					Double lat = Double.parseDouble(object.get("latitude")+"");
					Double lng = Double.parseDouble(object.get("longtitude")+"");
					getMarker(markerId).setLatLng(new LatLng(lat, lng));
					VMapEvent mapEvent = new VMapEvent(VMapEvents.ON_VMARKER_DRAG_START);
					mapEvent.setPosition(lat, lng);
					mapEvent.maps = self;
					mapEvent.component = getMarker(markerId);
					Events.postEvent(self, mapEvent);
					event.stopPropagation();
				}
				catch (Exception e) {
					//ignore
				}
			}
		});
		this.addEventListener("onVMarkerDragEnd", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				try {
					JSONObject jsonObject = (JSONObject)event.getData();
					JSONObject object = (JSONObject)jsonObject.get("data");
					String markerId = (String)object.get("markerId");
					Double lat = Double.valueOf(object.get("latitude")+"");
					Double lng = Double.valueOf(object.get("longtitude")+"");
					getMarker(markerId).setLatLng(new LatLng(lat, lng));
					VMapEvent mapEvent = new VMapEvent(VMapEvents.ON_VMARKER_DRAG_END);
					mapEvent.component = getMarker(markerId);
					mapEvent.maps = self;
					mapEvent.setPosition(lat, lng);
					Events.postEvent(self, mapEvent);
					event.stopPropagation();
				}
				catch (Exception e) {
					//ignore
				}
			}
		});
		/**
		 * 
		 */
		addEventListener("onErrorMap", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				try {
					String msg = (String)event.getData();
					Messagebox.show(msg, "Error", Messagebox.OK, Messagebox.ERROR);
					event.stopPropagation();
				}
				catch (Exception e) {
					//ignore
				}
			}
		});
	}
	
	private VMarker getMarker(String markerID) {
		for (int i = 0; i < getChildren().size(); i++) {
			Component child = getChildren().get(i);
			if (child instanceof VMarker && ((VMarker)child).getId().equals(markerID)) {
				return ((VMarker)child);
			}
		}
		return null;
	}
	
	public synchronized void removeAllMarker(){
		this.addJSScriptSynch("vietek.mapController.removeAllMarker('" + getId() + "')");
		for (int i = 0; i < getChildren().size(); i++) {
			Component child = getChildren().get(i);
			if (child instanceof VMarker) {
				removeChild(child);
			}
		}
	}
	
	@Override
	public synchronized boolean removeChild(Component child) {
		this.addJSScriptSynch("vietek.mapController.removeChild('" + getId() + "','" + child.getId() + "')");
		return super.removeChild(child);
	}
	public synchronized void removeAllChild(){
		this.addJSScriptSynch("vietek.mapController.removeAllChild('" + getId() + "')");
		getChildren().clear();
	}
}
