package base.util;

import org.codehaus.jackson.annotate.JsonIgnore;

public class JsonGoogleResponsePlaceGeometry {
	public JsonGoogleResponsePlaceGeometry() {
		// TODO Auto-generated constructor stub
	}

	private JsonGoogleResponsePlaceLocation location;

	private String location_type;

	@JsonIgnore
	private Object bounds;

	@JsonIgnore

	private Object viewport;

	public JsonGoogleResponsePlaceLocation getLocation() {
		return location;
	}

	public void setLocation(JsonGoogleResponsePlaceLocation location) {
		this.location = location;
	}

	public String getLocation_type() {
		return location_type;
	}

	public void setLocation_type(String location_type) {
		this.location_type = location_type;
	}

	public Object getBounds() {
		return bounds;
	}

	public void setBounds(Object bounds) {
		this.bounds = bounds;
	}

	public Object getViewport() {
		return viewport;
	}

	public void setViewport(Object viewport) {
		this.viewport = viewport;
	}
}
