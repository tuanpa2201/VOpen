package base.util;

import java.util.List;

import com.google.maps.model.LatLng;

import base.vmap.LatLngBounds;
import base.vmap.VMaps;
import base.vmap.VMarker;
import base.vmap.VPolyline;

public class MapUtils {
	public static final double R = 6372.8 * 1000; // In kilometers

	/**
	 * Calculate distance of two point
	 * 
	 * @param lon1
	 * @param lat1
	 * @param lon2
	 * @param lat2
	 * @return
	 */
	public static double distance(double lon1, double lat1, double lon2, double lat2) {
		if (lon1 == 0 || lon2 == 0 || lat1 == 0 || lat2 == 0) {
			return 0.0;
		}
		double cosd = Math.sin(lat1 * Math.PI / (double) 180) * Math.sin(lat2 * Math.PI / (double) 180)
				+ Math.cos(lat1 * Math.PI / (double) 180) * Math.cos(lat2 * Math.PI / (double) 180)
						* Math.cos((lon1 - lon2) * Math.PI / (double) 180);
		if (cosd >= 1) {
			return 0.0;
		}
		double d = Math.acos(cosd);
		double distance = d * R;
		return distance;
	}

	/**
	 * Calculate distance of two point
	 * 
	 * @param lon1
	 * @param lat1
	 * @param lon2
	 * @param lat2
	 * @return
	 */
	public static double distanceBirdFly(double lon1, double lat1, double lon2, double lat2) {
		if (lon1 == 0 || lon2 == 0 || lat1 == 0 || lat2 == 0) {
			return 0.0;
		}
		double cosd = Math.sin(lat1 * Math.PI / (double) 180) * Math.sin(lat2 * Math.PI / (double) 180)
				+ Math.cos(lat1 * Math.PI / (double) 180) * Math.cos(lat2 * Math.PI / (double) 180)
						* Math.cos((lon1 - lon2) * Math.PI / (double) 180);
		if (cosd >= 1) {
			return 0.0;
		}
		double d = Math.acos(cosd);
		double distance = d * R;
		return distance;
	}

	public static void scaleMap(VMaps map) {
		Double minLat = null;
		Double maxLat = null;
		Double minLng = null;
		Double maxLng = null;
		for (Object o : map.getChildren()) {
			if (o instanceof VMarker) {
				VMarker g = (VMarker) o;
				if (g.getVisible()) {
					if (g.getLat() + g.getLng() == 0.0)
						continue;
					if ((minLat == null) || (g.getLat() < minLat)) {
						minLat = g.getLat();
					}
					if ((maxLat == null) || (g.getLat() > maxLat)) {
						maxLat = g.getLat();
					}
					if ((minLng == null) || (g.getLng() < minLng)) {
						minLng = g.getLng();
					}
					if ((maxLng == null) || (g.getLng() > maxLng)) {
						maxLng = g.getLng();
					}
				}

			} else if (o instanceof VPolyline) {
				VPolyline line = (VPolyline) o;
				for (LatLng point : line.getPath()) {
					if (point.lat + point.lng == 0.0)
						continue;
					if ((minLat == null) || (point.lat < minLat)) {
						minLat = point.lat;
					}
					if ((maxLat == null) || (point.lat > maxLat)) {
						maxLat = point.lat;
					}
					if ((minLng == null) || (point.lng < minLng)) {
						minLng = point.lng;
					}
					if ((maxLng == null) || (point.lng > maxLng)) {
						maxLng = point.lng;
					}
				}
			}
		}

		if (minLat == null) {
			return;
		}
		// Double ctrLng = (maxLng + minLng) / 2;
		// Double ctrLat = (maxLat + minLat) / 2;
		// Double interval = 0.0;
		// int mapDisplay = 600; // Minimum of height or width of map in pixels
		// if ((maxLat - minLat) > (maxLng - minLng)) {
		// interval = (maxLat - minLat) / 2;
		// minLng = ctrLng - interval;
		// maxLng = ctrLng + interval;
		// } else {
		// interval = (maxLng - minLng) / 2;
		// minLat = ctrLat - interval;
		// maxLat = ctrLat + interval;
		// }
		// Double dist = (6371
		// * Math.acos(Math.sin(minLat / 57.2958) * Math.sin(maxLat / 57.2958) +
		// (Math.cos(minLat / 57.2958)
		// * Math.cos(maxLat / 57.2958) * Math.cos((maxLng / 57.2958) - (minLng
		// / 57.2958)))));
		// Double zoom = Math.floor(7 - Math.log(1.6446 * dist / Math.sqrt(2 *
		// (mapDisplay * mapDisplay))) / Math.log(2));
		LatLngBounds bounds = new LatLngBounds(new LatLng(minLat, minLng), new LatLng(maxLat, maxLng));
		map.fitBounds(bounds);
		// map.setCenter(ctrLat, ctrLng);
		// map.setZoom(zoom.intValue() < 5 ? 15 : zoom.intValue());
	}

	public static String getAddressGoogle(double latitude, double longitude) {
		String address = "";
		return address;
	}

	public static String getAddressFromIMap(double longitude, double latitude) {
		String value = "";
		return value;
	}

	public static void setCenter(List<LatLng> points, VMaps vmap, Double padding) {
		Double minLat = null;
		Double maxLat = null;
		Double minLng = null;
		Double maxLng = null;
		for (LatLng point : points) {
			if ((minLat == null) || (point.lat < minLat)) {
				minLat = point.lat;
			}
			if ((maxLat == null) || (point.lat > maxLat)) {
				maxLat = point.lat;
			}
			if ((minLng == null) || (point.lng < minLng)) {
				minLng = point.lng;
			}
			if ((maxLng == null) || (point.lng > maxLng)) {
				maxLng = point.lng;
			}
		}

		if (minLat == null) {
			return;
		}
		LatLng southWest = new LatLng(minLat - padding, minLng - padding);
		LatLng northEast = new LatLng(maxLat + padding, maxLng + padding);
		LatLngBounds bound = new LatLngBounds(southWest, northEast);
		vmap.setBounds(bound);
		// vmap.setCenter(new LatLng(ctrLat, ctrLng));
	}

	public static boolean checkInMapView(VMaps map, LatLng position) {
		if (map.getBounds() == null)
			return false;
		Double maxLat = map.getBounds().northEast.lat;
		Double minLat = map.getBounds().southWest.lat;
		Double maxLng = map.getBounds().northEast.lng;
		Double minLng = map.getBounds().southWest.lng;
		return (minLat <= position.lat && maxLat >= position.lat && minLng <= position.lng && maxLng >= position.lng);
	}

	public static boolean checkInBounds(LatLngBounds bounds, LatLng position) {
		boolean result = false;
		if (bounds != null && position != null) {
			Double curLon = position.lng;
			Double maxLat = bounds.northEast.lat;
			Double minLat = bounds.southWest.lat;
			Double maxLng = bounds.northEast.lng;
			Double minLng = bounds.southWest.lng;
			if (minLng <= maxLng) {
				result = (minLat <= position.lat && maxLat >= position.lat && minLng <= curLon && maxLng >= curLon);
			} else {
				if ((minLng > 0 && maxLng > 0) || (minLng < 0 && maxLng < 0)) {
					if (minLng < 0 || maxLng < 0) {
						if (curLon >= minLng || curLon <= maxLng) {
							result = minLat <= position.lat && maxLat >= position.lat;
						}
					} else {
						if (curLon <= 0 || curLon >= minLng || curLon <= maxLng) {
							result = minLat <= position.lat && maxLat >= position.lat;
						}
					}
				} else {
					if (curLon >= minLng || curLon <= maxLng) {
						result = minLat <= position.lat && maxLat >= position.lat;
					}
				}
			}
		}
		return result;
	}

	public static double angleFromCoordinate(double lat1, double long1, double lat2, double long2) {

		double dLon = (long2 - long1);

		double y = Math.sin(dLon) * Math.cos(lat2);
		double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);

		double brng = Math.atan2(y, x);

		brng = Math.toDegrees(brng);
		brng = (brng + 360) % 360;
		brng = 360 - brng;

		return brng;
	}
}
