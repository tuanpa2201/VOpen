package base.vmap;

import java.util.ArrayList;

import com.google.maps.model.LatLng;

public class VPolygonOptions {
	private boolean clickable;
	private boolean draggable;
	private boolean editable;
	private String fillColor;
	private double fillOpacity;
	private boolean geodesic;
	private VMaps map;
	private ArrayList<ArrayList<LatLng>> paths;
	private String strokeColor;
	private double strokeOpacity;
	private StrokePosition strokePosition;
	private int strokeWeight;
	private boolean visible;
	
	public VPolygonOptions() {
		clickable = true;
		draggable = false;
		editable = false;
		fillColor = "#000000";
		fillOpacity = 0.3;
		geodesic = false;
		paths = new ArrayList<>();
		strokeColor = "#000000";
		strokeOpacity = 1.0;
		strokePosition = StrokePosition.CENTER;
		strokeWeight = 2;
	}
	
	public boolean isClickable() {
		return clickable;
	}
	public void setClickable(boolean clickable) {
		this.clickable = clickable;
	}
	public boolean isDraggable() {
		return draggable;
	}
	public void setDraggable(boolean draggable) {
		this.draggable = draggable;
	}
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	public String getFillColor() {
		return fillColor;
	}
	public void setFillColor(String fillColor) {
		this.fillColor = fillColor;
	}
	public double getFillOpacity() {
		return fillOpacity;
	}
	public void setFillOpacity(double fillOpacity) {
		this.fillOpacity = fillOpacity;
	}
	public boolean isGeodesic() {
		return geodesic;
	}
	public void setGeodesic(boolean geodesic) {
		this.geodesic = geodesic;
	}
	public VMaps getMap() {
		return map;
	}
	public void setMap(VMaps map) {
		this.map = map;
	}
	public ArrayList<ArrayList<LatLng>> getPaths() {
		return paths;
	}
	public void setPaths(ArrayList<ArrayList<LatLng>> paths) {
		this.paths = paths;
	}
	public String getStrokeColor() {
		return strokeColor;
	}
	public void setStrokeColor(String strokeColor) {
		this.strokeColor = strokeColor;
	}
	public double getStrokeOpacity() {
		return strokeOpacity;
	}
	public void setStrokeOpacity(double strokeOpacity) {
		this.strokeOpacity = strokeOpacity;
	}
	public StrokePosition getStrokePosition() {
		return strokePosition;
	}
	public void setStrokePosition(StrokePosition strokePosition) {
		this.strokePosition = strokePosition;
	}
	public int getStrokeWeight() {
		return strokeWeight;
	}
	public void setStrokeWeight(int strokeWeight) {
		this.strokeWeight = strokeWeight;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		
		return buffer.toString();
	}
}
