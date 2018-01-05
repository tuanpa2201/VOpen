package base.vmap;

import java.util.ArrayList;

import org.zkoss.zk.ui.Component;

import com.google.maps.model.LatLng;

public class VPolyline extends VComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7626273927491981110L;
	/**
	 * 
	 */
	private boolean dragable;
	private boolean editable;
	private String color;
	private double opacity;
	private boolean clickable;
	private int weight;
	
	private VMaps map;
	private ArrayList<LatLng> latlngs;
	private boolean visible;
	private VPolylineOptions options;
	
	public VPolyline() {
		super();
		init();
	}
	
	public VPolyline(VPolylineOptions options){
		super();
		setOption(options);
		init();
		
	}
	
	@Override
	public void setId(String arg0) {
//		super.setId(arg0);
	}
	
	private void init() {
		super.setId(IDGenerator.generateStringID());
		options = new VPolylineOptions();
		dragable = false;
		editable = false;
		visible = true;
		String script = "vietek.mapController.addVPolyline('" + getId() + "','" + options.toString() + "')";
		this.addJSScriptSynch(script);
		script = "vietek.mapController.setPathVPolyline('" + getId() + "','" + options.pathToString(options.getPath()) + "')";
		this.addJSScriptSynch(script);
	}

	public boolean isDragable() {
		return dragable;
	}

	public void setDragable(boolean dragable) {
		this.dragable = dragable;
		String script = "vietek.mapController.setDraggableVPolyline('" + getId() + "'," + dragable + ")";
		this.addJSScriptSynch(script);
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		String script = "vietek.mapController.setEditableVpolyline('" + getId() + "'," + editable + ")";
		this.addJSScriptSynch(script);
	}
	
	public void setColor(String color){
		this.color = color;
		String script = "vietek.mapController.setColorVPolyline('" +  getId() + "','" + color + "')";
		this.addJSScriptSynch(script);
	}
	
	public String getColor(){
		return color;
	}

	public VMaps getMap() {
		return map;
	}
	
	@Override
	public void setParent(Component maps) {
		if (maps != null && maps instanceof VMaps) {
			this.map = (VMaps)maps;
			String script = "vietek.mapController.setMapVPolyline('" + map.getId() + "','" + getId() + "')";
			this.addJSScriptSynch(script);
		} 
		super.setParent(maps);
	}

	public ArrayList<LatLng> getPath() {
		return latlngs;
	}

	public void setPath(ArrayList<LatLng> latlngs) {
		this.latlngs = latlngs;
		this.options.setPath(latlngs);
		String script = "vietek.mapController.setPathVPolyline('" + getId() + "','" + options.pathToString(latlngs) + "')";
		this.addJSScriptSynch(script);
	}

	public boolean isVisible() {
		return visible;
	}
	
	@Override
	public boolean setVisible(boolean visible) {
		this.visible = visible;
		String script = "vietek.mapController.setVisibleVPolyline('" + getId() + "'," + visible + ")";
		this.addJSScriptSynch(script);
		return visible;
	}

	public void setOption(VPolylineOptions options){
		this.options = options;
		this.dragable = options.isDraggable();
		this.editable = options.isEditable();
		this.map = options.getMaps();
		this.latlngs = options.getPath();
		this.visible = options.isVisible();
		String script = "vietek.mapController.setOptionsVPolyline('" + getId() + "','" + this.options.toString() + "')";
		this.addJSScriptSynch(script);
		script = "vietek.mapController.setPathVPolyline('" + getId() + "','" + this.options.pathToString(options.getPath()) + "')";
		this.addJSScriptSynch(script);
	}

	public double getOpacity() {
		return opacity;
	}

	public void setOpacity(double opacity) {
		if(opacity <0){
			this.opacity = 0;
		} else if(opacity >1){
			this.opacity = 1;
		} else {
			this.opacity = opacity;
		}
		String script = "vietek.mapController.setOpacityVPolyline('" + getId() + "'," + this.opacity + ")";
		this.addJSScriptSynch(script);
	}

	public boolean isClickable() {
		return clickable;
	}

	public void setClickable(boolean clickable) {
		this.clickable = clickable;
		
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
		String script = "vietek.mapController.setWeightVPolyline('" + getId() + "'," + weight + ")";
		this.addJSScriptSynch(script);
	}
	

}
