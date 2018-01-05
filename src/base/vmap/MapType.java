package base.vmap;

public enum MapType {
	ROADMAP("ROADMAP"), SATELLITE("SATELLITE"), HYBRID("HYBRID"), TERRAIN("TERRAIN");
	private String value;
	MapType(String value){
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}
}
