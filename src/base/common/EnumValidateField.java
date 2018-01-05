package base.common;

/**
 *
 * Aug 11, 2016
 *
 * @author VuD
 *
 */

public enum EnumValidateField {
	TITLE("title"), NULLABLE("nullable"), IS_EMAIL("isemail"), MIN_LENGTH("minlength"), MAX_LENGTH("maxlength"), REGEX(
			"regex"), LABEL("label");

	private final String value;

	private EnumValidateField(String name) {
		this.value = name;
	}

	public String getValue() {
		return value;
	}

}
