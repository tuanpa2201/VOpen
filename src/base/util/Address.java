package base.util;

public class Address {

	private String placeID = "";
	private String name = "";
	private double latitude = 0.0;
	private double longitude = 0.0;
	private boolean isByGoogle = true;
	private String TVT = "";
	private String note = "";
	
	public Address() {

	}
	private static Address unknowAddress = new Address("", 0.0, 0.0);
	public static Address getUnknowAddress() {
		return unknowAddress;
	}

	public Address(String name, double latitude, double longitude) {
		super();
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public boolean isByGoogle() {
		return isByGoogle;
	}

	public String getTVT() {
		return TVT;
	}

	public void setTVT(String tVT) {
		TVT = tVT;
	}

	public void setByGoogle(boolean isByGoogle) {
		this.isByGoogle = isByGoogle;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getPlaceID() {
		return placeID;
	}

	public void setPlaceID(String placeID) {
		this.placeID = placeID;
	}

	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}

}
