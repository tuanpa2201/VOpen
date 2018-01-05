package base.vmap;

import java.util.ArrayList;

import com.google.maps.model.LatLng;

public class VPolylineOptions {
	
	private boolean clickable;
	private boolean draggable;
	private boolean editable;
	private boolean geodesic;
	private VMaps maps;
	private ArrayList<LatLng> path;
	private String strokeColor;
	private double strokeOpacity;
	private int strokeWeight;
	private boolean visible;
	
	public VPolylineOptions() {
		init();
	}
	
	private void init(){
		clickable = true;
		draggable = false;
		editable = false;
		geodesic = false;
		maps = null;
		path = new ArrayList<>();
		strokeColor = "#000000";
		strokeOpacity = 1.0;
		strokeWeight = 2;
		visible = true;
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

	public boolean isGeodesic() {
		return geodesic;
	}

	public void setGeodesic(boolean geodesic) {
		this.geodesic = geodesic;
	}

	public VMaps getMaps() {
		return maps;
	}

	public void setMaps(VMaps maps) {
		this.maps = maps;
	}

	public ArrayList<LatLng> getPath() {
		return path;
	}

	public void setPath(ArrayList<LatLng> path) {
		this.path = path;
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
	
	protected String pathToString(ArrayList<LatLng> path) {
		StringBuffer strPath = new StringBuffer();
		strPath.append("[");
		if(path.size()>0){
			for(int i=0; i<path.size();i++){
				strPath.append("{ &quot;lat&quot;: ").append(path.get(i).lat).append(", &quot;lng&quot;: ").append(path.get(i).lng).append("},");
			}
			strPath.deleteCharAt(strPath.lastIndexOf(","));
		} 
		strPath.append("]");
		return strPath.toString();
	}
	
	
	@Override
	public String toString() {
		StringBuffer option = new StringBuffer();
		option.append("{ &quot;clickable&quot;:").append(clickable).append(", &quot;draggable&quot;:").append(draggable);
		option.append(", &quot;editable&quot;:").append(editable);
		option.append(", &quot;strokeColor&quot;: &quot;").append(strokeColor).append("&quot;, &quot;strokeOpacity&quot;: ").append(strokeOpacity);
		option.append(", &quot;strokeWeight&quot;: ").append(strokeWeight).append(", &quot;geodesic&quot;:").append(geodesic);
		option.append(", &quot;visible&quot;: ").append(visible).append(", &quot;map&quot;:").append(maps).append("}"); //.append(", map:").append(maps)
		return option.toString();
	}
}
