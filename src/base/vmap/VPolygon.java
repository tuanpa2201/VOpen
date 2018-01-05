package base.vmap;

import java.util.ArrayList;

import com.google.maps.model.LatLng;

public class VPolygon extends VComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private VMaps maps;
	private boolean draggable;
	private boolean editable;
	private VPolygonOptions options;
	private ArrayList<LatLng> path;
	private ArrayList<ArrayList<LatLng>> paths;
	private boolean visible;

	public VPolygon() {
		super();
		init();
	}
	
	public VPolygon(VPolygonOptions options){
		super();
		init();
	}
	
	private void init(){
		draggable = false;
		editable = false;
		path = new ArrayList<>();
		paths = new ArrayList<>();
		visible = true;
	}

	public VMaps getMaps() {
		return maps;
	}

	public void setMaps(VMaps maps) {
		this.maps = maps;
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

	public VPolygonOptions getOptions() {
		return options;
	}

	public void setOptions(VPolygonOptions options) {
		this.options = options;
	}

	public ArrayList<LatLng> getPath() {
		return path;
	}

	public void setPath(ArrayList<LatLng> path) {
		this.path = path;
	}

	public ArrayList<ArrayList<LatLng>> getPaths() {
		return paths;
	}

	public void setPaths(ArrayList<ArrayList<LatLng>> paths) {
		this.paths = paths;
	}

	public boolean isVisible() {
		return visible;
	}

	public boolean setVisible(boolean visible) {
		this.visible = visible;
		return visible;
	}


}
