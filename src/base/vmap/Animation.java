package base.vmap;

public enum Animation {
	DROP("DROP"), BOUNCE("BOUNCE");
	private String value;
	private Animation(String value) {
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}
}
