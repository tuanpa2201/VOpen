package base.vmap;

import com.google.maps.model.LatLng;

public class VInfoWindowOptions {
	private String content;
	private boolean disableAutoPan;
	private Integer maxWidth;
	private LatLng position;
	
	public VInfoWindowOptions() {
		content = "";
		disableAutoPan = true;
		maxWidth = null;
		position = null;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public boolean isDisableAutoPan() {
		return disableAutoPan;
	}
	public void setDisableAutoPan(boolean disableAutoPan) {
		this.disableAutoPan = disableAutoPan;
	}
	public Integer getMaxWidth() {
		return maxWidth;
	}
	public void setMaxWidth(Integer maxWidth) {
		this.maxWidth = maxWidth;
	}
	public LatLng getPosition() {
		return position;
	}
	public void setPosition(LatLng position) {
		this.position = position;
	}
	
	
}
