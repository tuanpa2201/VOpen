package base.vmap;

import org.zkoss.zk.ui.Component;

import com.google.maps.model.LatLng;

import base.util.MapUtils;

public class VMarker extends VComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1334323495061490893L;
	/**
	 * 
	 */
	private String content;
	private String image;
	private Double angle;
	private String label;
	private LatLng position;
	private boolean clickable;
	private boolean draggable;
	private boolean isOpen;
	private String address;
	private boolean visible = true;
	private static String ICON_URL = "./themes/images/gmaps_marker.png";
	private int clusterSize = 0;
	
	public VMarker() {
		super();
		init();
	}
	
	public VMarker(double latitude, double longitude) {
		super();
		init();
		this.setPosition(new LatLng(latitude, longitude));
	}
	
	public VMarker(int clusterSize) {
		super();
		this.clusterSize = clusterSize;
		init();
	}
	
	public VMarker(int clusterSize, double latitude, double longitude) {
		super();
		this.clusterSize = clusterSize;
		init();
		this.setPosition(new LatLng(latitude, longitude));
	}
	
	private void init() {
		content="";
		image = ICON_URL;
		angle = 0.0;
		label = "";
		clickable = true;
		draggable = false;
		visible = true;
		isOpen = false;
		if(position == null)
			position = new LatLng(0.0, 0.0);
		String script = null;
		if (clusterSize == 0) {
			script = "vietek.mapController.addMarkerWithLabel('" + getId() + "', "
				+ "{image:'" + image +"', "
				+ "position:{lat : " + position.lat + ", lng : " + position.lng + "}})";
		}else {
			script = "vietek.mapController.addMarker('" + getId() + "', "
					+ "{image:'" + image +"', "
					+ "position:{lat : " + position.lat + ", lng : " + position.lng + "}}, " + clusterSize +")";
		}
		this.addJSScriptSynch(script);
	}
	
	public void destroy() {
		if (this.getParent() instanceof VMaps) {
			((VMaps)this.getParent()).removeChild(this);
		}
		this.setParent(null);
		String script = "vietek.mapController.removeMarker('" + getId() + "')";
		this.addJSScriptSynch(script);
	}
	
	public String getContent() {
		return content;
	}

	public String getImage() {
		return image;
	}

	public Double getAngle() {
		return angle;
	}
	
	public String getLabel(){
		return this.label;
	}
	
	public double getLat(){
		return this.position.lat;
	}
	
	public double getLng(){
		return this.position.lng;
	}
	
	@Override
	public void setId(String arg0) {
		String id = super.getId();
		String script = "vietek.mapController.setIdMarker('"+ id + "','" + arg0 + "')";
		this.addJSScriptSynch(script);
		super.setId(arg0);
	}
	
	public void setLabelClass(String sclass){
		String script = "vietek.mapController.setLabelClass('"+ this.getId() + "','" + sclass + "')";
		this.addJSScriptSynch(script);
	}
	
	public void setContent(String content) {
		this.content = content;
		String cont = this.content;
		if(this.content.contains("'")){
				cont = this.content.replaceAll("'", "&#39;");
		}
		if(cont.contains("\\")){
			cont = cont.replaceAll("\\", "&#92;");
		}
		if(cont.contains("\"")){
			cont = cont.replaceAll("\"", "&quot;");
		}
		String script = "vietek.mapController.setContent('"+ this.getId() + "','" + cont + "')";
		this.addJSScriptSynch(script);
	}
	
//	public void setContent(VInfoWindow infoWindow){
//		
//	}
	
	public boolean setOpen(boolean flag){
		isOpen = flag;
		if(this.getParent() != null){
			String script = "vietek.mapController.setOpenContent('" + this.getParent().getId() + "','"+ this.getId() + "'," + flag + ")";
			this.addJSScriptSynch(script);
			return true;
		}
		return flag;
	}
	
	public boolean isOpen(){
		return isOpen;
	}
	
	public void setIconImage(String image) {
		String options = "{}";
		setIconImage(image, options);
	}
	
	public void setIconImage(String image, String options) {
		if (!image.equalsIgnoreCase(this.image)) {
			this.image = image;
			String script = "vietek.mapController.setIcon('" + getId() + "','" + image + "', " + options + ")";
			this.addJSScriptSynch(script);
		}
	}


	public void setAngle(Double angle) {
		if(!this.angle.equals(angle)){
			this.angle = angle;
			String script = "vietek.mapController.setRotate('" + this.getId() + "'," + angle + ")";
			this.addJSScriptSynch(script);
		}
	}
	
	public void setAngleSmooth(Double angle) {
		if(!this.angle.equals(angle)){
			this.angle = angle;
			String script = "vietek.mapController.setRotateSmooth('" + this.getId() + "'," + angle + ")";
			this.addJSScriptSynch(script);
		}
	}
	
	
	public void setLabel(String arg0){
		if(!this.label.equals(arg0)){
			this.label = arg0;
			String script = "vietek.mapController.setLabel('" + this.getId() + "','" + this.label + "')";
			this.addJSScriptSynch(script);
		}
	}

	public void setClickable(boolean flag){
		if(this.clickable != flag){
			this.clickable = flag;
			if(this.getParent() != null){
				String script = "vietek.mapController.setClickable('" +this.getParent().getId() + "','" + this.getId() + "'," + this.clickable + ")";
				this.addJSScriptSynch(script);
			}
		}
	}
	
	public void setDraggable(boolean flag){
		if(this.draggable != flag){
			this.draggable = flag;
				String script = "vietek.mapController.setDraggable('" + this.getId() + "'," + this.draggable + ")";
				this.addJSScriptSynch(script);
		}
	}
	
	/**
	 * Change animation for marker
	 * @param arg0 : DROP or BOUNCE
	 */
//	public void setAnimation(Animation animation){
//		if(!this.animation.equals(animation.getValue())){
//			this.animation = animation.getValue();
//			if(this.maps != null){
//				String script = "vietek.mapController.setAnimation('" + this.maps.getId() + "','" + this.getId() + "','" + this.animation + "')";
//				Events.postEvent(new Event("runJSScript", this, script));
//			}
//		}
//	}
	
//	public void setOpacity(int arg0){
//		if(this.opacity != arg0){
//			this.opacity = arg0;
//			if(this.maps != null){
//				String script = "vietek.mapController.setOpacity('" + this.maps.getId() + "','" + this.getId() + "'," + this.opacity + ")";
//				Events.postEvent(new Event("runJSScript", this, script));
//			}
//		}
//	}
	
	public boolean setVisible(boolean flag){
		if(this.visible != flag){
			this.visible = flag;
			String script = "vietek.mapController.setVisible('" + this.getId() + "'," + this.visible + ")";
			this.addJSScriptSynch(script);
		}
		return flag;
	}
	
	public void setLabelAnchor(int x, int y){
		String script = "vietek.mapController.setLabelAnchor('" + this.getId() + "'," + x + "," + y + ")";
		this.addJSScriptSynch(script);
	}
	
	public boolean getVisible(){
		return this.visible;
	}
	
	@Override
	public void setParent(Component maps) {
		if (this.getParent() != maps && maps != null && maps instanceof VMaps) {
			String script = "vietek.mapController.setMap('" + maps.getId() + "','" + getId() + "')";
			this.addJSScriptSynch(script);
		} 
		super.setParent(maps);
		
	}
	
	/**
	 * @return the position
	 */
	public LatLng getPosition() {
		return position;
	}
	
	protected void setAddress(String address) {
		this.address = address;
	}
	public String getAddress() {
		if(this.position.lat!=0 && this.position.lng!=0){
			address = MapUtils.getAddressFromIMap(position.lat, position.lng);
		}
		return address;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(LatLng position) {
		this.position = position;
		String script = "vietek.mapController.setPosition('" + getId() + "', " + position.lat + ", " + position.lng + ")";
		this.addJSScriptSynch(script);
	}
	
	protected void setLatLng(LatLng latLng){
		this.position = latLng;
	}
	public void updateCluster(int imgSize, LatLng position){
		this.clusterSize = imgSize;
		init();
		this.setPosition(position);
	}
}
