/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Jul 28, 2016
* Author: tuanpa
*
*/
package base.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlNativeComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;

import base.model.VObject;
import base.util.Translate;
import base.util.ZKEnv;

public class VFormView extends VView implements EventListener<Event>, IVObjectChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7646314206411565293L;
	private Div logDiv;

	public VFormView(VViewDefine viewDef, VWindow window) {
		super(viewDef, window);
	}

	@Override
	protected void initUI() {
		super.initUI();
	}

	@Override
	public void parseXML() {
		window.helper = new VObjectEditHelper(this, window.controller, window.defaults);
		ParseFormViewUtil parseUtil = new ParseFormViewUtil(window.getController(), window.helper, window.helper.mapEditor, window.helper.mapEditorInvert,
				viewDef.moduleId, this);
		parseUtil.recursiveParseNode(viewDef.xmlNode, this);
		if (window.getController().isLog()) {
			logDiv = new Div();
			logDiv.setWidth("100%");
			logDiv.setVflex("min");
			logDiv.setParent(this);
		}
	}

	@Override
	public String getViewType() {
		return VViewType.FORM.getValue();
	}

	@Override
	public void setData() {
		window.helper.setData(window.getCurrentId());
		if (window.getCurrentId() == null) {
			btFirst.setVisible(false);
			btPreview.setVisible(false);
			btNext.setVisible(false);
			btLast.setVisible(false);
		} else {
			btFirst.setVisible(true);
			btPreview.setVisible(true);
			btNext.setVisible(true);
			btLast.setVisible(true);
		}
		currentPosition.setValue(
				window.getCurrentId() != null ? (window.getCurrentPosition() + 1) + " / " + (window.getCount())
						: Translate.translate(ZKEnv.getEnv(), null, "[new]"));
		window.refreshTitle(this);
		if (window.getController().isLog() && window.getCurrentId() != null && window.getCurrentId() > 0) {
			logDiv.getChildren().clear();
			LogView logView = new LogView(window.getController().modelName, window.getCurrentId());
			logView.setParent(logDiv);
		}
		checkDirty();
		this.invalidate();
	}

	HtmlNativeComponent layout;
	Button btNew;
	Button btSave;
	Button btCancel;
	Button btDelete;

	@Override
	public Component getLeftHeader() {
		if (layout == null) {
			layout = new HtmlNativeComponent("div");

			btNew = new Button(Translate.translate(ZKEnv.getEnv(), null, "New"));
			btNew.setAttribute("action", "new");
			btNew.addEventListener(Events.ON_CLICK, this);
			btNew.setParent(layout);
			btNew.setZclass("none");
			btNew.setSclass("btn btn-primary");

			btSave = new Button(Translate.translate(ZKEnv.getEnv(), null, "Save"));
			btSave.setAttribute("action", "save");
			btSave.addEventListener(Events.ON_CLICK, this);
			btSave.setParent(layout);
			btSave.setZclass("none");
			btSave.setSclass("btn btn-success");

			btCancel = new Button(Translate.translate(ZKEnv.getEnv(), null, "Cancel"));
			btCancel.setAttribute("action", "cancel");
			btCancel.addEventListener(Events.ON_CLICK, this);
			btCancel.setParent(layout);
			btCancel.setZclass("none");
			btCancel.setSclass("btn btn-link");

			btDelete = new Button(Translate.translate(ZKEnv.getEnv(), null, "Delete"));
			btDelete.setAttribute("action", "delete");
			btDelete.addEventListener(Events.ON_CLICK, this);
			btDelete.setParent(layout);
			btDelete.setZclass("none");
			btDelete.setSclass("btn btn-link");
		}
		return layout;
	}

	HtmlNativeComponent layoutCenter;
//	Button btPrint;
	@Override
	public Component getCenterHeader() {
		if (layoutCenter == null) {
			
			layoutCenter = new HtmlNativeComponent("div");
			
			if(window.reports.size() > 0) {
				
				Menubar menuBar = new Menubar();
				menuBar.setParent(layoutCenter);
				Menu menuPrint = new Menu(Translate.translate(ZKEnv.getEnv(), null, "Print"));
				menuPrint.setParent(menuBar);
				Menupopup menupopup = new Menupopup();
				menupopup.setParent(menuPrint);
				
				Iterator<Integer> it = window.reports.keySet().iterator();
				while(it.hasNext()) {
					
					Integer reportId = it.next();
					VObject objReport = window.reports.get(reportId); 					
					Menuitem item = new Menuitem(Translate.translate(ZKEnv.getEnv(), objReport.getValue("module_id").toString(), objReport.getValue("name").toString()));
					item.setAttribute("action", "print");
					item.setAttribute("reportId", reportId);
					item.addEventListener(Events.ON_CLICK, this);
					item.setParent(menupopup);
				}
			}			
		}
		return layoutCenter;
	}

	Hbox rightLayout;
	Button btFirst;
	Button btPreview;
	Button btNext;
	Button btLast;
	Label currentPosition;

	@Override
	public Component getRightHeader() {
		if (rightLayout == null) {
			rightLayout = new Hbox();
			rightLayout.setVflex("min");
			btFirst = new Button("<<");
			btFirst.setZclass("none");
			btFirst.setSclass("btn btn-link");
			btFirst.setParent(rightLayout);
			btFirst.setAttribute("action", "first");
			btFirst.addEventListener(Events.ON_CLICK, this);

			btPreview = new Button("<");
			btPreview.setParent(rightLayout);
			btPreview.setAttribute("action", "preview");
			btPreview.addEventListener(Events.ON_CLICK, this);
			btPreview.setZclass("none");
			btPreview.setSclass("btn btn-link");

			currentPosition = new Label();
			currentPosition.setParent(rightLayout);
			currentPosition.setStyle("white-space: nowrap;");

			btNext = new Button(">");
			btNext.setParent(rightLayout);
			btNext.setAttribute("action", "next");
			btNext.addEventListener(Events.ON_CLICK, this);
			btNext.setZclass("none");
			btNext.setSclass("btn btn-link");

			btLast = new Button(">>");
			btLast.setParent(rightLayout);
			btLast.setAttribute("action", "last");
			btLast.addEventListener(Events.ON_CLICK, this);
			btLast.setZclass("none");
			btLast.setSclass("btn btn-link");
		}
		return rightLayout;
	}

	@Override
	public Component getTitleComponent() {
		HtmlNativeComponent titleDiv = new HtmlNativeComponent("h1",
				"<i class=\"fa-fw fa fa-columns\"></i> "
						+ Translate.translate(ZKEnv.getEnv(), viewDef.moduleId, window.getController().getTitle())
						+ " <span>> " + window.getController().getDisplayString(window.getCurrentId()) + "</span>",
				null);
		titleDiv.setDynamicProperty("class", "txt-color-blueDark");
		Image helpImage = new Image("/themes/images/help16.png");
		helpImage.addEventListener(Events.ON_CLICK, this);
		helpImage.setAttribute("action", "showHelp");
		helpImage.setStyle("padding-left: 10px");
		titleDiv.appendChild(helpImage);
		return titleDiv;
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getTarget().getAttribute("action") != null
				&& event.getTarget().getAttribute("action").equals("showHelp")) {
			Clients.showNotification(window.getController().getDescription(), event.getTarget());
		} else if (event.getTarget().getAttribute("action") != null) {
			if (event.getTarget().getAttribute("action").equals("new")) {
				window.newRecord();
			} else if (event.getTarget().getAttribute("action").equals("save")) {
				window.save();
			} else if (event.getTarget().getAttribute("action").equals("delete")) {
				List<Integer> ids = new ArrayList<>();
				ids.add(window.getCurrentId());
				window.delete(ids);
			} else if (event.getTarget().getAttribute("action").equals("cancel")) {
				window.stopEditing();
				if (window.getCurrentId() == null) {
					window.showList();
				}
				else {
					window.controller.removeCache(window.getCurrentId());
					window.refresh();
				}
			} else if (event.getTarget().getAttribute("action").equals("print")) {				
				Integer reportId = (Integer) event.getTarget().getAttribute("reportId");
				window.print(reportId);
			} else if (event.getTarget().getAttribute("action").equals("first")) {
				window.moveFist();
			} else if (event.getTarget().getAttribute("action").equals("preview")) {
				window.movePreview();
			} else if (event.getTarget().getAttribute("action").equals("next")) {
				window.moveNext();
			} else if (event.getTarget().getAttribute("action").equals("last")) {
				window.moveLast();
			}
		}
	}

	private void checkDirty() {
		if (window.isEditing()) {
			btSave.setVisible(true);
			btCancel.setVisible(true);
			btNew.setVisible(false);
			btDelete.setVisible(false);
		} else {
			btSave.setVisible(false);
			btCancel.setVisible(false);
			btNew.setVisible(true);
			btDelete.setVisible(true);
		}
	}

	@Override
	public void onChanging(Map<String, Object> mapNewValue) {
		window.editing();
		checkDirty();
	}

	@Override
	public void onDiscard() {
		window.stopEditing();
		checkDirty();
	}
}
