package modules.sys.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlNativeComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Toolbarbutton;

import base.controller.VController;
import base.controller.VEnv;
import base.model.VObject;
import base.util.DateUtils;
import base.util.Filter;
import base.util.Translate;
import base.vmap.IDGenerator;
import modules.sys.model.SysNoticeLog;
import modules.sys.model.SysNotification;

public class NotificationScreen implements EventListener<Event> {
	private VEnv vEnv;
	private Component parent;
	private List<Notification> listNotification = new ArrayList<>();
	private List<Integer> screens;
	private List<Div> NotificationDetail;
	private int currentShow = -1;
	private boolean loadBegin = true;

	public NotificationScreen(VEnv env, Component parent) {
		this.parent = parent;
		this.vEnv = env;
		LoadWorker worker = new LoadWorker();
		worker.start();
	}

	HtmlNativeComponent notificationScreen;
	private Html titleNotification;
	private Html bodyNotification;
	private Button btnNext;
	private Button btnPrevious;

	private void initScreen() {
		screens = new ArrayList<>();
		notificationScreen = new HtmlNativeComponent("div");
		notificationScreen.setDynamicProperty("class", "notificationScreen");
		Toolbarbutton close = new Toolbarbutton();
		close.setParent(notificationScreen);
		close.setImage("./themes/images/close_24.png");
		close.setSclass("z-notification-close");
		close.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				close();
			}
		});

		HtmlNativeComponent form = new HtmlNativeComponent("div");
		form.setDynamicProperty("class", "msform");
		form.setParent(notificationScreen);

		HtmlNativeComponent progressbar = new HtmlNativeComponent("ul");
		progressbar.setDynamicProperty("id", "progressbar");
		progressbar.setParent(form);
		HtmlNativeComponent fieldset = new HtmlNativeComponent("fieldset");
		fieldset.setParent(form);
		Div contaner = new Div();
		contaner.setSclass("z-notification-detail");
		contaner.setParent(form);
		for (int i = 0; i < listNotification.size(); i++) {
			listNotification.get(i).setNumberId();
			Notification notification = listNotification.get(i);
			if (loadBegin) {
				if (notification.isNecessary()) {
					screens.add(i);
				}
			} else {
				screens.add(i);
			}

		}
		int sizeScreen = this.getSize();
		double elementwidth = 100.0 / Double.parseDouble(sizeScreen + "");
		for (int i = 0; i < this.getSize(); i++) {
			int indexData = screens.get(i);
			HtmlNativeComponent numberElement = new HtmlNativeComponent("li");
			numberElement.setId(listNotification.get(indexData).getNumberId());
			numberElement.setPrologContent("Thông báo " + (i + 1));
			numberElement.setDynamicProperty("style", "width:" + elementwidth + "%");
			numberElement.setParent(progressbar);
		}
		titleNotification = new Html();
		titleNotification.setParent(contaner);
		bodyNotification = new Html();
		bodyNotification.setParent(contaner);
		Html footer = new Html(NotificationTemplate.getNotificationFooter());
		// Html footer = new Html("<div style=\" width:100%\"></div>");
		footer.setParent(contaner);
		Toolbar control = new Toolbar();
		control.setAlign("center");
		control.setParent(contaner);
		btnPrevious = new Button(Translate.translate(vEnv, null, "Previous"));
		btnPrevious.setSclass("z-notification-buttion");
		btnPrevious.setParent(control);
		btnPrevious.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				showNotification(currentShow - 1);
			}
		});
		btnNext = new Button(Translate.translate(vEnv, null, "Next"));
		btnNext.setSclass("z-notification-buttion");
		btnNext.setParent(control);
		btnNext.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				showNotification(currentShow + 1);
			}
		});
		btnNext.setStyle("margin-left:10px");
		int initPage = 0;
		if (currentShow > 0) {
			initPage = currentShow;
		}
		if (this.getSize() > 0) {
			notificationScreen.setParent(parent);
			showNotification(initPage);
		}
		loadBegin = false;
	}

	public void showNotificationScreen() {
		Executions.schedule(parent.getDesktop(), new EventListener<Event>() {
			@Override
			public void onEvent(Event arg0) throws Exception {
				initScreen();
			}
		}, null);
	}

	public void postEvent() {
		Executions.schedule(parent.getDesktop(), new EventListener<Event>() {
			@Override
			public void onEvent(Event arg0) throws Exception {
				Events.postEvent("onNotification", parent, listNotification.size());
			}
		}, null);
	}

	public void showNotification(int indexScreen) {
		int size = this.getSize();
		if (indexScreen >= 0 && indexScreen < size) {
			int indexNoti = screens.get(indexScreen);
			Notification data = listNotification.get(indexNoti);
			titleNotification.setContent(data.getTitleElement());
			bodyNotification.setContent(data.getConten());
			for (int i = 0; i < size; i++) {
				String js = "";
				int indexData = screens.get(i);
				if (i <= indexScreen) {
					js = "$(\"#" + listNotification.get(indexData).getNumberId() + "\").addClass(\"active\")";
				} else {
					js = "$(\"#" + listNotification.get(indexData).getNumberId() + "\").removeClass(\"active\")";
				}
				Clients.evalJavaScript(js);
			}
			currentShow = indexScreen;
			data.reader(true);
			showControl();

		}

	}

	public int getSize() {
		return screens.size();
	}

	private void showControl() {
		int size = this.getSize();
		if (currentShow > 0 && currentShow < size - 1) {
			btnPrevious.setVisible(true);
			btnNext.setVisible(true);
		} else if (currentShow == 0) {
			btnNext.setVisible(size > 1);
			btnPrevious.setVisible(false);
		} else if (currentShow == size - 1) {
			btnNext.setVisible(false);
			btnPrevious.setVisible(true);
		}

	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getTarget().getAttribute("action").equals("close")) {
			close();
		}

	}

	private void close() {
		boolean checkClose = true;
		for (Notification note : listNotification) {
			checkClose = checkClose && note.isClosable();
			if (!note.isClosable()) {
				checkClose = false;
				break;
			}
		}
		if (checkClose && notificationScreen != null) {
			notificationScreen.setParent(null);
			Clients.evalJavaScript("$(\".notificationScreen\").fadeOut();");
		} else {
			Clients.showNotification("Bạn còn thông báo cần phải đọc!", "info", notificationScreen, "middle_center",
					2000);
		}
	}

	public List<Div> getNotificationDetail() {
		if (NotificationDetail == null) {
			NotificationDetail = new ArrayList<>();
			for (int i = 0; i < listNotification.size(); i++) {
				NotificationDetail.add(initDetailElement(listNotification.get(i), i));
			}
		}
		return NotificationDetail;
	}

	private Div initDetailElement(Notification note, int index) {
		Div div = new Div();
		div.setStyle("padding: 5px; border-bottom: groove 1px; cursor:pointer;");
		Image image = new Image();
		Label label = new Label();
		if (note.isImportant()) {
			image.setSrc("img/imposition.png");
		} else {
			image.setSrc("img/no_imposition.png");
		}
		image.setParent(div);
		image.setStyle("margin-right: 5px;");
		label.setStyle("font-family: 'San Francisco'; font-size: 14px; text-transform: uppercase;");
		label.setValue(note.getTitle());
		label.setParent(div);
		div.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				currentShow = index;
				showNotificationScreen();
			}
		});
		return div;
	}

	class LoadWorker extends Thread {
		public LoadWorker() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			List<VObject> noties = getNotificationForUser(vEnv.user);
			if (noties.size() > 0) {
				for (VObject notie : noties) {
					Notification data = new Notification();
					data.setId(notie.getId());
					data.setImportant(notie.getValue("important"));
					data.setTitle(notie.getValue("title"));
					data.setConten(notie.getValue("content") + "");
					if (data.isImportant()) {
						data.setReader(isReaded(vEnv.user.getId(), notie.getId()));
					}
					listNotification.add(data);
				}
				showNotificationScreen();
			}
			postEvent();
		}

		private List<VObject> getNotificationForUser(VObject user) {
			List<VObject> list = new ArrayList<>();
			Map<String, Object> params = new HashMap<>();
			String sqlFilter = "isactive = true ";
			sqlFilter += "and (:dateNow BETWEEN startDate and endDate or endDate is null) ";
			sqlFilter += "and :user in elements(users) ";
			params.put("user", user);
			params.put("dateNow", DateUtils.getDateNow());
			List<Integer> ids = VEnv.sudo().get(SysNotification.MODEL_NAME).search(new Filter(sqlFilter,params));
			if (ids != null) {
				for (Integer id : ids) {
					list.add(VEnv.sudo().get(SysNotification.MODEL_NAME).browse(id));
				}
			}
			return list;
		}

		private boolean isReaded(Integer userId, Integer notifID) {
			boolean res = false;
			VController noticelogs = VEnv.sudo().get(SysNoticeLog.MODEL_NAME);
			if (noticelogs != null) {
				Map<String, Object> params = new HashMap<>();
				params.put("userid", userId);
				params.put("noticeid", notifID);
				params.put("timelog", DateUtils.getDateNow());
				Filter filter = new Filter("userid = :userid and noticeid = :noticeid and timelog = :timelog", params);
				List<Integer> ids = noticelogs.search(filter);
				if (ids.size() > 0) {
					res = true;
				}
			}
			return res;
		}

	}

	class Notification {
		private Integer id = 0;
		private String title = "Thông báo";
		private String conten = "";
		private String numberId;
		private boolean isReader = false;
		private boolean isImportant = false;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public boolean isReader() {
			return isReader;
		}

		public void reader(boolean isReader) {
			if (!this.isReader && isReader && isImportant) {
				saveReaded(vEnv.user.getId(), id);
			}
			this.isReader = isReader;
		}

		public void setReader(boolean isReader) {
			this.isReader = isReader;
		}

		public boolean isImportant() {
			return isImportant;
		}

		public void setImportant(Object isImportant) {
			if (isImportant != null) {
				this.isImportant = (Boolean) isImportant;
			}
		}

		public String getTitleElement() {
			return "<h1 class = \"txt-color-blueDark text-center well v-notification-title\" >" + title + "</h1>";
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(Object title) {
			if (title != null) {
				this.title = title + "";
			}
		}

		public String getConten() {
			return conten;
		}

		public void setConten(String conten) {
			this.conten = conten;
		}

		public boolean isClosable() {
			return !isImportant || (isImportant && isReader);
		}

		public boolean isNecessary() {
			return !isImportant || (isImportant && !isReader);
		}

		public String getNumberId() {
			return numberId;
		}

		public void setNumberId() {
			this.numberId = IDGenerator.generateStringID();
		}

		private boolean saveReaded(Integer userId, Integer notifID) {
			boolean res = false;
			VController noticelogs = VEnv.sudo().get(SysNoticeLog.MODEL_NAME);
			if (noticelogs != null) {
				Map<String, Object> params = new HashMap<>();
				params.put("userid", userId);
				params.put("noticeid", notifID);
				params.put("timelog", DateUtils.getDateNow());
				res = noticelogs.create(params).status;
			}
			return res;
		}
	}
}
