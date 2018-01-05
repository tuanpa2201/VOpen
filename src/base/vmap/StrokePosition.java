package base.vmap;

public enum StrokePosition {
	CENTER("center"), INSIDE("inside"), OUTSIDE("outside");
	private String value;
	private StrokePosition(String value) {
		this.value = value;
	}
	
	protected String getValue(){
		return this.value;
	}
}
