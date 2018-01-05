package modules.sys.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zhtml.Br;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;

import base.controller.VController;
import base.controller.VEnv;
import base.model.VObject;
import base.util.DateUtils;
import base.util.Translate;
import base.util.ZKEnv;
import base.view.Index;
import modules.sys.model.SysNoticeLog;
import modules.sys.model.SysNotification;

public class DialogNotification extends Window implements EventListener<Event> {

	private static final long serialVersionUID = 1L;
	private List<VObject> listNotification = new ArrayList<>();
	private Tabbox tabbox;
	private Tabs tabs;
	private Tab tab;
	private Tabpanels tabpanels;
	private Tabpanel tabpanel;
	private Button btnExit;
	private Map<Tab, VObject> tabMap = new HashMap<>();
	private Map<VObject, Tab> notificationMap = new HashMap<>();
	private StringBuilder strNotification = new StringBuilder("Thông báo phải đọc: ");
	private VEnv vEnv;
	private Component compParent;

	public DialogNotification(VEnv vEnv, Component parent) {
		this.setSclass("notification");
		this.setStyle("font-family : Times New Roman; font-weight : bold; font-size : 14px");
		this.setWidth("70%");
		this.setBorder("normal");
		this.setBorder(false);
		this.setCompParent(parent);
		this.vEnv = vEnv;
	}	

	
	public DialogNotification(VEnv vEnv, Component parent, boolean ispublic) {
		this.setSclass("notification");
		this.setStyle("font-family : Times New Roman; font-weight : bold; font-size : 14px");
		this.setWidth("70%");
		this.setBorder("normal");
		this.setBorder(false);
		this.setCompParent(parent);
		this.vEnv = vEnv;
		getNotices(ispublic);
	}	

	public DialogNotification(VEnv vEnv, Component parent, VObject notifcation) {
		this.setSclass("notification");
		this.setStyle("font-family : Times New Roman; font-weight : bold; font-size : 14px");
		this.setWidth("70%");
		this.setHeight("70%");
		this.setBorder("normal");
		this.setBorder(false);
		this.setCompParent(parent);
		this.listNotification.add(notifcation);
		this.vEnv = vEnv;
	}
	
	public void show(Desktop desktop) {
		Executions.schedule(desktop, new EventListener<Event>() {
			@Override
			public void onEvent(Event arg0) throws Exception {
				//Tuanpa: TODO
//				initPopupNotices();
			}
		}, null);
	}

	public void getNotices(boolean ispublic) {
		SysNotification sysNotification = new SysNotification();
//		listNotification = sysNotification.getNotifications(this.vEnv.user, ispublic);		
	}

	public void show() {
		if (listNotification != null && listNotification.size() > 0) {
			initNoticeTabs(listNotification);
		}

	}

	
	private void initNoticeTabs(List<VObject> notices) {
		tabbox = new Tabbox();
		tabbox.setParent(this);
		tabbox.setWidth("100%");
		tabbox.setHeight("100%");
		tabs = new Tabs();
		tabs.setParent(tabbox);
		tabs.setSclass("nav nav-tabs");
		tabpanels = new Tabpanels();
		tabpanels.setParent(tabbox);
		tabpanels.setHeight("100%");

		Div divBottom = new Div();
		divBottom.setParent(this);
		divBottom.setWidth("100%");

		btnExit = new Button(Translate.translate(vEnv, null, "Exit"));
		btnExit.setParent(divBottom);
		btnExit.setStyle("float: right; margin-top: 5px; margin-right: 15px; font-size: 14px;");
		btnExit.setSclass("btn btn-success");
		btnExit.setZclass("none");
		btnExit.setWidth("100px");
		btnExit.addEventListener(Events.ON_CLICK, this);
		
		if (notices.size() > 0) {
			initNotification(notices);
			setPosition("center,center");
			setMode(Mode.OVERLAPPED);
			setParent(getCompParent());
			doModal();
		}		
	}

	public void initNotification(List<VObject> notices) {
		for (int i = 0; i < notices.size(); i++) {
			VObject notification = notices.get(i);			
			// chi hien thi cac thong bao rieng danh cho nguoi dung
			tab = new Tab(Translate.translate(vEnv, null, "Notification") + " " + (i + 1));
			tab.setParent(tabs);
			tabMap.put(tab, notification);
			if ((Boolean) notification.getValue("important") && (!tab.isSelected() || i != 0)) {
				notificationMap.put(notification, tab);
				strNotification.append(tab.getLabel() + ", ");
			}
			initTabPanel(notification);
			tab.addEventListener(Events.ON_SELECT, this);		
		}
	}

	private void initTabPanel(VObject notification) {
		tabpanel = new Tabpanel();
		tabpanel.setParent(tabpanels);
		tabpanel.setWidth("100%");
		tabpanel.setStyle("border:none;");
		tabpanel.setZclass("none");
		Div divNotification = new Div();
		divNotification.setParent(tabpanel);
		Div divTitle = new Div();
		divTitle.setParent(divNotification);
		divTitle.setWidth("96%");
		divTitle.setHeight("25%");
		divTitle.setStyle("text-align:center; border-bottom: double #518de8; margin: auto; padding: 7px 0px;");
		Label title = new Label(notification.getValue("title").toString());
		title.setStyle("text-align: center; font-size: 20px; text-transform: uppercase; font-weight: bold;"
				+ "word-wrap: break-word;white-space: pre-wrap; color: #518de8;");
		title.setParent(divTitle);

		Div divContent = new Div();
		divContent.setParent(divNotification);
		divContent.setWidth("92%");
		divContent.setHeight("250px");
		divContent.setStyle("position: relative; top:10px; overflow: auto; margin: auto");
		String strContent = "";
		if (notification.getValue("content") != null)
			strContent = notification.getValue("content").toString();
		String[] words = strContent.split("</br>");
		for (String w : words) {
			Label content = new Label(w);
			content.setWidth("100%");
			content.setParent(divContent);
			content.setStyle("word-wrap: break-word;white-space: pre-wrap;");
			Br br = new Br();
			br.setParent(divContent);
		}

		Div divAuthor = new Div();
		divAuthor.setParent(divNotification);
		divAuthor.setWidth("100%");
		divAuthor.setHeight("15%");
		divAuthor.setStyle("text-align: right; position: absolute; bottom: 10px; left: 0;");		
	}

	public List<VObject> getListNotification() {
		return listNotification;
	}

	public Component getCompParent() {
		return compParent;
	}

	public void setCompParent(Component compParent) {
		this.compParent = compParent;
	}

//	private void initPopupNotices() {
//		Index homepage = (Index) vEnv.getContext("home_page");
//		homepage.getDivNotifications().setStyle("overflow: auto");
//		SysNotification sysNotification = new SysNotification();
//		List<VObject> lstNotification = sysNotification.getNotifications(vEnv.user, false);
//		homepage.getLblListNotification().setValue(String.valueOf(lstNotification.size()));
//		List<VObject> lstGlobal = new ArrayList<VObject>();
//		List<VObject> lstPersonal = new ArrayList<VObject>();
//		for (VObject notification : lstNotification) {
//			int type = Integer.parseInt((String) notification.getValue("type"));
//			if (type == 0) {
//				lstGlobal.add(notification);
//			} else {
//				lstPersonal.add(notification);
//			}
//		}
//		homepage.getGlobal().setValue("Chung (" + String.valueOf(lstGlobal.size()) + ")");
//		homepage.getPersonal().setValue("Riêng (" + String.valueOf(lstPersonal.size()) + ")");
//		homepage.getTotal().setValue("Tất cả (" + String.valueOf(lstNotification.size()) + ")");
//		for (VObject object : lstPersonal) {
//			Notificaiton notificaiton = new Notificaiton(object, vEnv);
//			notificaiton.setParent(homepage.getDivNotifications());
//		}
//		
//		homepage.getTotal().addEventListener(Events.ON_CLICK, new EventListener<Event>() {
//
//			@Override
//			public void onEvent(Event arg0) throws Exception {
//				homepage.getDivNotifications().getChildren().clear();
//				for (VObject object : lstNotification) {
//					Notificaiton notificaiton = new Notificaiton(object, vEnv);
//					notificaiton.setParent(homepage.getDivNotifications());
//				}
//			}
//		});
//		
//		homepage.getGlobal().addEventListener(Events.ON_CLICK, new EventListener<Event>() {
//
//			@Override
//			public void onEvent(Event arg0) throws Exception {
//				homepage.getDivNotifications().getChildren().clear();
//				for (VObject object : lstGlobal) {
//					Notificaiton notificaiton = new Notificaiton(object, vEnv);
//					notificaiton.setParent(homepage.getDivNotifications());
//				}
//			}
//		});
//		
//		homepage.getPersonal().addEventListener(Events.ON_CLICK, new EventListener<Event>() {
//
//			@Override
//			public void onEvent(Event arg0) throws Exception {
//				homepage.getDivNotifications().getChildren().clear();
//				for (VObject object : lstPersonal) {
//					Notificaiton notificaiton = new Notificaiton(object, vEnv);
//					notificaiton.setParent(homepage.getDivNotifications());
//				}
//			}
//		});
//		
//		int check = 0;
//		for (VObject vObject : lstPersonal) {
//			int type = Integer.parseInt((String) vObject.getValue("type"));	
//			// thong bao toi han tra phi thue bao GPS		
//			if (type == 2) {			
//				Date date = (Date) vObject.getValue("startDate");
//				if (date.equals(DateUtils.getDateNow())) {
//					check++;
//				}
//			}
//		}
//		if (check == 0) {
//			VObject vObject = getSpecialNotices();
//			if (vObject != null) {
//				lstPersonal.add(0, vObject);
//			}			
//		} 
//				
//		initNoticeTabs(lstPersonal);
//				
//	}
	
	private VObject getSpecialNotices(){		
		VObject vObject = null;
		VController sysController = VEnv.sudo().get("Sys.User");
		Object[] params = new Object[0];
		vObject = (VObject) sysController.execute("getVehiclesExpireInfomation", params);
		return vObject;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onEvent(Event evt) throws Exception {
		if (evt.getTarget().equals(btnExit) && evt.getName().equals(Events.ON_CLICK)) {
			if (notificationMap.isEmpty()) {
				if (this.vEnv.user != null) {
					VController controller = VEnv.sudo().get(SysNoticeLog.MODEL_NAME);					
					for (VObject object : this.listNotification) {
						Object users = object.getValue("users");
						if (users != null && !((List<VObject>)users).isEmpty()) {
							if (((List<VObject>)users).contains(this.vEnv.user)) {
								Map<String, Object> params = new HashMap<>();
								params.put("userid", this.vEnv.user.getId());
								params.put("noticeid", object.getId());
								params.put("timelog", DateUtils.getDateNow());
								controller.create(params);
							}
							
						}
					}
				}
				DialogNotification.this.setVisible(false);
			} else {
				Messagebox.show(strNotification.toString(), "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
			}
		}
		for (Tab tab : tabMap.keySet()) {
			if (evt.getTarget().equals(tab) && evt.getName().equals(Events.ON_SELECT)) {
				if (!notificationMap.isEmpty() && (Boolean)tabMap.get(tab).getValue("important")) {
					notificationMap.remove(tabMap.get(tab));
					tabMap.get(tab).setValue("important", false);					
					try {
						strNotification.delete(strNotification.indexOf(tab.getLabel()),
								strNotification.indexOf(tab.getLabel()) + tab.getLabel().length() + 1);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		}
	}

	@SuppressWarnings("serial")
    class Notificaiton extends Div {

		public Notificaiton(VObject notice, VEnv vEnv) {
			this.setStyle("padding: 5px; border-bottom: groove 1px; cursor:pointer;");
			Image image = new Image();
			Label label = new Label();
			if ((Boolean) notice.getValue("important")) {
				image.setSrc("img/imposition.png");
				image.setParent(this);
			} else {
				image.setSrc("img/no_imposition.png");
				image.setParent(this);
			}
			image.setStyle("margin-right: 5px;");
			label.setStyle("font-family: 'San Francisco'; font-size: 14px;");
			label.setValue(notice.getValue("title").toString());
			label.setParent(this);
			this.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					DialogNotification dialogNotification = new DialogNotification(vEnv, ZKEnv.getHomePage().getDivMain().getParent(), notice);
					dialogNotification.show();
				}
			});
		}
	}

}
