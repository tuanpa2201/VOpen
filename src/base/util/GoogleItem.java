package base.util;

import org.codehaus.jackson.annotate.JsonIgnore;

public class GoogleItem {
	public String getFormatted_address() {
		return formatted_address;
	}

	public void setFormatted_address(String formatted_address) {
		this.formatted_address = formatted_address;
	}

	public Object getAddress_components() {
		return address_components;
	}

	public void setAddress_components(Object address_components) {
		this.address_components = address_components;
	}

	public GoogleItemGeometry getGeometry() {
		return geometry;
	}

	public void setGeometry(GoogleItemGeometry geometry) {
		this.geometry = geometry;
	}

	private String formatted_address;
	@JsonIgnore
	private Object address_components;

	GoogleItemGeometry geometry;
}
