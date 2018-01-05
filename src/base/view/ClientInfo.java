package base.view;

import java.util.TimeZone;

public class ClientInfo {
	public static String SCREEN_WEB_MOBILE = "mobile";
	public static String SCREEN_PC = "pc";
	private int width = 0;
	private int height = 0;
	private String operationSystem = "";
	private boolean islandscape = false;
	private TimeZone timeZone;
	private boolean isChange = true;
	private boolean isTerminalLogin = false;

	public String getScreen() {

		String typeScreen = "";
		if (width > 750) {
			typeScreen = SCREEN_PC;
		} else {
			typeScreen = SCREEN_WEB_MOBILE;
		}
		return typeScreen;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getOperationSystem() {
		return operationSystem;
	}

	public void setOperationSystem(String operationSystem) {
		this.operationSystem = operationSystem;
	}

	public boolean isIslandscape() {
		return islandscape;
	}

	public void setIslandscape(boolean islandscape) {
		this.islandscape = islandscape;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	public void setChange(boolean isChange) {
		this.isChange = isChange;
	}

	public boolean isChange() {
		return isChange;
	}

	public void setTerminalLogin(boolean isTerminalLogin) {
		this.isTerminalLogin = isTerminalLogin;
	}

	public String getDeviceLogin() {
		String typeScreen = "";
		if (this.isTerminalLogin) {
			typeScreen = "Mobile";
		} else if (width > 750) {
			typeScreen = "PC";
		} else {
			typeScreen = "Web Mobile";
		}
		return typeScreen;
	}

}
