package base.common;

public enum EnumLevelUser {
	ADMIN("admin"), SUPER_USER("SuperUser");

	private final String text;

	/**
	 * @param text
	 */
	private EnumLevelUser(final String text) {
		this.text = text;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return text;
	}
}