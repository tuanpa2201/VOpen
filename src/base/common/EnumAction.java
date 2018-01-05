package base.common;

/**
 *
 * Jul 27, 2016
 *
 * @author VuD
 *
 */

public enum EnumAction {
	ADD("Add"), EDIT("Edit"), DELETE("Del"), IMPORT("Import"), EXPORT("Export"), VIEW("view");
	private final String value;

	private EnumAction(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
