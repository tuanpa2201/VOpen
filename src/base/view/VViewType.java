/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Jul 27, 2016
* Author: tuanpa
*
*/
package base.view;


public enum VViewType {
	LIST("list"),
	FORM("form"),
	CALENDAR("calendar"),
	GRAPH("graph"),
	KANBAN("kanban");
	
	private String value;
	VViewType(String viewType) {
		this.setValue(viewType);
	}
	public String getValue() {
		return value;
	}
	public void setValue(String viewType) {
		this.value = viewType;
	}
	public static VViewType getValueOf(String type) {
		if (type.equalsIgnoreCase("list")) {
			return LIST;
		} else if (type.equalsIgnoreCase("form")) {
			return VViewType.FORM;
		} else if (type.equalsIgnoreCase("calendar")) {
			return VViewType.CALENDAR;
		} else if (type.equalsIgnoreCase("graph")) {
				return VViewType.GRAPH;
		} else if (type.equalsIgnoreCase("kanban")) {
			return VViewType.KANBAN;
		}
		return null;
	}
}
