package base.util;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * Jul 13, 2016
 *
 * @author VuD
 *
 */

public class GoogleItemGeometry {
	GoogleItemGeometryLocation location;

	public GoogleItemGeometryLocation getLocation() {
		return location;
	}

	public void setLocation(GoogleItemGeometryLocation location) {
		this.location = location;
	}

	public Object getBounds() {
		return bounds;
	}

	public void setBounds(Object bounds) {
		this.bounds = bounds;
	}

	public String getLocation_type() {
		return location_type;
	}

	public void setLocation_type(String location_type) {
		this.location_type = location_type;
	}

	public Object getViewport() {
		return viewport;
	}

	public void setViewport(Object viewport) {
		this.viewport = viewport;
	}

	@JsonIgnore
	Object bounds;
	@JsonIgnore
	String location_type;
	@JsonIgnore
	Object viewport;
}
