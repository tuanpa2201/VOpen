package base.report;

public class ItemData {

	private int id;
	private String label;

	public ItemData(int id, String label) {
		// TODO Auto-generated constructor stub
		super();
		this.id = id;
		this.label = label;
	}

	public ItemData() {
		// TODO Auto-generated constructor stub
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return ((ItemData) obj).getId() == this.id;
	}
}
