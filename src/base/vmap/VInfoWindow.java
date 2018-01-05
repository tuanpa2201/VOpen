package base.vmap;

import com.google.maps.model.LatLng;

public class VInfoWindow extends VComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private VInfoWindowOptions options;
	private String content;
	private LatLng position;
	private boolean disableAutoPan;
	
	public VInfoWindow() {
		init();
	}
	
	public VInfoWindow(VInfoWindowOptions options) {
		init();
	}
	
	private void init(){
		if(this.options == null)
			this.options = new VInfoWindowOptions();
		content = options.getContent();
		position = this.options.getPosition();
		disableAutoPan = this.options.isDisableAutoPan();
	}
	
	public VInfoWindowOptions getOptions() {
		return options;
	}
	public void setOptions(VInfoWindowOptions options) {
		this.options = options;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public LatLng getPosition() {
		return position;
	}
	public void setPosition(LatLng position) {
		this.position = position;
	}
	public boolean isDisableAutoPan() {
		return disableAutoPan;
	}
	public void setDisableAutoPan(boolean disableAutoPan) {
		this.disableAutoPan = disableAutoPan;
	}
	
	public void open(){
		
	}
	
	public void close(){
		
	}

}
