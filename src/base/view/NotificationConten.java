package base.view;

import org.zkoss.zul.Div;
import org.zkoss.zul.Html;

public class NotificationConten extends Html {
	/**
	 * @author HungDang
	 */
	private static final long serialVersionUID = 1L;
	private Integer notificationID;
	private String title;
	private Div divDetail = null;
	private String conten;

	public Integer getNotificationID() {
		return notificationID;
	}

	public void setNotificationID(Integer notificationID) {
		this.notificationID = notificationID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getConten() {
		return conten;
	}

	public void setConten(String conten) {
		this.conten = conten;
	}

	public Div getDivDetail() {
		return divDetail;
	}

}
