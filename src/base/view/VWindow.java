/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Jul 27, 2016
* Author: tuanpa
*
*/
package base.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.zkoss.zul.Messagebox;

import base.controller.VActionResponse;
import base.controller.VController;
import base.model.VObject;
import base.util.Filter;
import base.util.Translate;
import base.util.ZKEnv;
import base.view.editor.One2ManyEditor;
import base.view.editor.VEditor;

public class VWindow extends HtmlNativeComponent implements Serializable, EventListener<Event> {

  /**
   * 
   */
  private static final long serialVersionUID = -4958267619550229599L;
  private VWindowDefine winDef;
  private Div divBody;
  public VController controller;
  public Integer currentId = -1;
  private List<Integer> cache;
  private Map<Integer, Integer> mapPosition = new HashMap<>();
  private Map<String, VViewDefine> mapView;
  public Filter filter = new Filter();
  public Map<String, Object> defaults = new HashMap<>();
  public Map<Integer, VObject> reports;
  private String sortBy;

  public String getSortBy() {
    return sortBy;
  }

  public void setSortBy(String sortBy) {
    this.sortBy = sortBy;
  }

  public VObjectEditHelper helper;

  private boolean isEditing = false;

  public boolean isEditing() {
    return isEditing;
  }

  public void editing() {
    isEditing = true;
  }

  public void stopEditing() {
    isEditing = false;
  }

  public VWindow(VWindowDefine vWindowDefine) {
    this.winDef = vWindowDefine;
    filter = vWindowDefine.filter.clone();
    defaults = vWindowDefine.getDefaults();
    sortBy = vWindowDefine.sortBy;
    controller = ZKEnv.getEnv().get(this.winDef.model);
    mapView = new LinkedHashMap<>();
    initUI();
    initViews();
    getReports();
  }

  public String getTitle() {
    return winDef.title;
  }

  public void setViewType(String viewType) {
    if (mapView.get(viewType) != null)
      attachView(mapView.get(viewType));
  }

  private void initUI() {
    this.setTag("div");
    this.setDynamicProperty("class", "container-fluid");
    this.setDynamicProperty("style", "padding-left: 0px; padding-right: 0px");
    this.initHeader();
    this.initBody();
  }

  Div titleDiv;

  private void initHeader() {
    HtmlNativeComponent divHeader = new HtmlNativeComponent("div");
    divHeader.setDynamicProperty("class", "container-fluid");
    divHeader.setDynamicProperty("style", "border-bottom: 1px dashed #ccc; margin-bottom: 5px; padding-bottom: 5px;");
    divHeader.setParent(this);

    HtmlNativeComponent titleLayout = new HtmlNativeComponent("div");
    divHeader.setDynamicProperty("class", "col-md-12");
    titleLayout.setParent(divHeader);

    titleDiv = new Div();
    titleDiv.setZclass("none");
    titleDiv.setHflex("1");
    titleDiv.setParent(titleLayout);

    HtmlNativeComponent hlayout = new HtmlNativeComponent("div");
    hlayout.setDynamicProperty("class", "container-fluid");
    hlayout.setDynamicProperty("style", "padding-left: 0px; padding-right: 0px");
    hlayout.setParent(divHeader);

    HtmlNativeComponent divLeft = new HtmlNativeComponent("div");
    divLeft.setDynamicProperty("class", "col-md-4");
    divLeft.setDynamicProperty("style", "padding-left: 0px; padding-right: 0px");
    divLeft.setParent(hlayout);

    HtmlNativeComponent divCenter = new HtmlNativeComponent("div");
    divCenter.setDynamicProperty("class", "col-md-4");
    divCenter.setDynamicProperty("style", "padding-left: 0px; padding-right: 0px");
    divCenter.setParent(hlayout);

    HtmlNativeComponent divRight = new HtmlNativeComponent("div");
    divRight.setDynamicProperty("class", "col-md-4");
    divRight.setDynamicProperty("style", "padding-left: 0px; padding-right: 0px");
    divRight.setParent(hlayout);

    this.initLeftHeader(divLeft);
    this.initCenterHeader(divCenter);
    this.initRightHeader(divRight);
  }

  Hbox leftHeaderHolder = null;

  private void initLeftHeader(Component parent) {
    leftHeaderHolder = new Hbox();
    leftHeaderHolder.setParent(parent);
    leftHeaderHolder.setHflex("1");
    leftHeaderHolder.setHeight("min");
    leftHeaderHolder.setPack("start");
  }

  Hbox centerHeaderHolder = null;

  private void initCenterHeader(Component parent) {
    centerHeaderHolder = new Hbox();
    centerHeaderHolder.setParent(parent);
    centerHeaderHolder.setHflex("1");
    centerHeaderHolder.setHeight("min");
    centerHeaderHolder.setPack("center");
  }

  Hbox rightHeaderHolder;
  HtmlNativeComponent viewTypeHolder;

  Image btRefresh;

  private void initRightHeader(Component parent) {
    Hbox box = new Hbox();
    box.setParent(parent);
    box.setHflex("1");
    box.setHeight("min");
    box.setPack("end");

    rightHeaderHolder = new Hbox();
    rightHeaderHolder.setParent(box);
    rightHeaderHolder.setHflex("min");
    rightHeaderHolder.setHeight("min");
    rightHeaderHolder.setPack("end");

    viewTypeHolder = new HtmlNativeComponent("div");
    viewTypeHolder.setParent(box);
    viewTypeHolder.setDynamicProperty("class", "btn-group");

    btRefresh = new Image("themes/images/refresh-24.png");
    btRefresh.setTooltip("Refresh");
    btRefresh.addEventListener(Events.ON_CLICK, this);
    btRefresh.setParent(box);
  }

  private void initBody() {
    divBody = new Div();
    divBody.setZclass("none");
    divBody.setSclass("col-md-12");
    divBody.setWidth("100%");
    divBody.setHeight("auto");
    divBody.setParent(this);
  }

  private String defaultView;

  public void initViews() {
    String[] tmp = winDef.strViews.split(",");
    for (String view : tmp) {
      boolean isSupportedView = false;
      for (VViewType viewType : VViewType.values()) {
        if (view.startsWith(viewType.getValue())) {
          addView(view);
          isSupportedView = true;
          break;
        }
      }
      if (!isSupportedView) {
        System.out.println("View not supported yet!: " + view);
      }
    }

    if (mapView.keySet().size() > 0) {
      attachView(mapView.get(defaultView));
    }

    // if (mapView.keySet().size() == 1) {
    // viewTypeHolder.setParent(null);
    // }

  }

  private void addView(String view) {
    VViewDefine viewDef = null;

    if (view.contains("[")) { // Specify view id
      String viewId = null;
      if (view.contains("]")) {
        viewId = view.substring(view.indexOf("[") + 1, view.indexOf("]"));
        viewId.replaceAll("\"", "");
        viewId.replaceAll("'", "");
        viewDef = VViewDefine.getView(viewId);
      }
    } else { // find highest priority view
      viewDef = VViewDefine.getViewForModel(winDef.model, VViewType.getValueOf(view));
    }

    if (viewDef == null) {
      System.out.println("View not supported yet!: " + view);
      return;
    }

    Button viewType = new Button();
    if (viewDef.type.getValue().equals("list")) {
      viewType.setImage("themes/images/list.png");
    } else if (viewDef.type.getValue().equals("form")) {
      viewType.setImage("themes/images/form.png");
    }
    viewType.setZclass("none");
    viewType.setSclass("btn btn-default");
    viewType.setAttribute("view", view);
    viewType.addEventListener(Events.ON_CLICK, this);
    viewType.setParent(viewTypeHolder);
    mapView.put(view, viewDef);
    if (defaultView == null)
      defaultView = view;
  }

  private VView currentView;

  private void attachView(VViewDefine viewDef) {
    if (this.isEditing()) {
      String msg = "Discard changed: [";
      msg += controller.getDisplayString(this.currentId) + ", ";
      msg = msg.substring(0, msg.length() - 2);
      msg += "]";
      Messagebox.show(msg, "Confirm?", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
          new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
              if (event.getName().equals(Messagebox.ON_OK)) {
                doAttachView(viewDef);
              }
            }
          });
    } else {
      doAttachView(viewDef);
    }
  }

  private void doAttachView(VViewDefine viewDef) {
    divBody.getChildren().clear();
    VView view = VView.getView(viewDef, this);
    view.setParent(divBody);
    view.setData();
    leftHeaderHolder.getChildren().clear();
    centerHeaderHolder.getChildren().clear();
    rightHeaderHolder.getChildren().clear();
    titleDiv.getChildren().clear();
    if (view.getLeftHeader() != null)
      leftHeaderHolder.appendChild(view.getLeftHeader());
    if (view.getCenterHeader() != null)
      centerHeaderHolder.appendChild(view.getCenterHeader());
    if (view.getRightHeader() != null)
      rightHeaderHolder.appendChild(view.getRightHeader());
    if (view.getTitleComponent() != null)
      titleDiv.appendChild(view.getTitleComponent());

    currentView = view;

    refresh();

    if (divBody.getDesktop() != null && divBody.getDesktop().getAttribute("index") != null) {
      Index index = (Index) divBody.getDesktop().getAttribute("index");
      Map<String, String> params = new LinkedHashMap<>();
      String viewStr = null;
      for (String viewStr1 : mapView.keySet()) {
        if (mapView.get(viewStr1) == viewDef) {
          viewStr = viewStr1;
          break;
        }
      }
      if (viewStr != null) {
        params.put("view_type", viewStr);
      }
      if (currentId != null && currentId > 0) {
        params.put("id", currentId.toString());
      }
      index.pushHistory(winDef.title + "_" + currentView, params);
    }
    stopEditing();
  }

  public void refreshTitle(VView view) {
    titleDiv.getChildren().clear();
    if (view.getTitleComponent() != null)
      titleDiv.appendChild(view.getTitleComponent());
  }

  public VWindowDefine getWinDef() {
    return winDef;
  }

  @Override
  public void onEvent(Event event) throws Exception {
    if (event.getName().equals(Events.ON_CLICK) && event.getTarget().getAttribute("view") != null) {
      attachView(mapView.get(event.getTarget().getAttribute("view").toString()));
    } else if (event.getTarget() == btRefresh) {
      refresh(true);
    }
  }

  public VController getController() {
    return controller;
  }

  public Integer getCurrentId() {
    if (currentId != null && currentId == -1 && getCount() > 0)
      currentId = getItemAt(0);
    return currentId;
  }

  public void setCurrentId(Integer currentIndex) {
    if (this.isEditing()) {
      String msg = "Discard changed: [";
      msg += controller.getDisplayString(this.currentId) + ", ";
      msg = msg.substring(0, msg.length() - 2);
      msg += "]";
      Messagebox.show(msg, "Confirm?", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
          new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
              if (event.getName().equals(Messagebox.ON_OK)) {
                refresh();
                doSetCurrentId(currentIndex);
              }
            }
          });
    } else {
      doSetCurrentId(currentIndex);
    }
  }

  public void doSetCurrentId(Integer currentIndex) {
    this.isEditing = false;
    this.currentId = currentIndex;
    if (count == null) {
      count = 0L;
    }
    if (mapPosition.get(currentId) == null && getCount() > mapPosition.size()) {
      mapPosition.put(currentId, count.intValue() - 1);
    }
    if (divBody.getDesktop() != null && divBody.getDesktop().getAttribute("index") != null) {
      Index index = (Index) divBody.getDesktop().getAttribute("index");
      Map<String, String> params = new LinkedHashMap<>();
      if (currentId != null && currentId > 0) {
        params.put("id", currentId.toString());
      }
      index.pushHistory(winDef.title + " [" + currentId + "]", params);
    }
    if (currentView instanceof VFormView) {
      currentView.setData();
    }
  }

  Long count = null;

  public Long getCount() {
    if (count == null || count == 0) {
        count = controller.count(filter);
    }
    return count;
  }

  public int getSizeGiganticTable() {
    loadCache();
    return cache.size();
  }

  private int startIndex = 0;
  private int cacheSize = 30;

  public Integer getItemAt(int index) {
    if (cache == null || index < startIndex || index >= startIndex + cacheSize)
      loadData(index);
    int indexTmp = index - startIndex;
    if (indexTmp < 0) {
      indexTmp = 0;
    }
    Integer value = (Integer) cache.get(indexTmp);
    mapPosition.put(value, index);
    return value;
  }

  private void loadData(int index) {
    if (!controller.isGiganticTable()) {
      startIndex = (index / cacheSize) * cacheSize;
      cache = controller.loadIds(filter, sortBy, startIndex, cacheSize);
    } else {
      loadCache();
    }

  }

  private void loadCache() {
    if (cache == null) {
      if (sortBy.indexOf("desc") < 0) {
        sortBy = sortBy + " desc";
      }
      cache = controller.loadIds(filter, sortBy, startIndex, 10);
    }
  }

  public void loadMore() {
    startIndex = startIndex + 10;
    List<Integer> list = controller.loadIds(filter, sortBy, startIndex, 10);
    cache.addAll(list);
    currentView.setData();
  }

  public boolean showDetail() {
    if (currentView instanceof VFormView)
      return true;
    boolean isShowDetail = false;
    for (String view : mapView.keySet()) {
      if (view.startsWith("form")) {
        attachView(mapView.get(view));
        isShowDetail = true;
        break;
      }
    }
    return isShowDetail;
  }

  public void newRecord() {
    setCurrentId(null);
    showDetail();
  }

  public boolean save() throws Exception {

    boolean retVal = true;
    if (helper.checkBeforeSave(true)) {
      if (getCurrentId() == null) {
        VActionResponse res = create(helper.mapNewValue);
        if (res.status) {
          // Delete Child record (One2Many)
          for (VEditor editor : helper.mapEditor.values()) {
            if (editor instanceof One2ManyEditor) {
              ((One2ManyEditor) editor).deleteChildObj();
            }
          }
          doSetCurrentId(res.id);
          // currentView.setData();
        } else {
          showNotification(res);
        }
      } else {
        List<Integer> ids = new ArrayList<>();
        ids.add(getCurrentId());
        VActionResponse res = update(ids, helper.mapNewValue);

        if (ids.size() > 0 && res.status) {
          // Delete Child record (One2Many)
          for (VEditor editor : helper.mapEditor.values()) {
            if (editor instanceof One2ManyEditor) {
              ((One2ManyEditor) editor).deleteChildObj();
            }
          }
          setCurrentId(ids.get(0));
          showDetail();
        } else {
          showNotification(res);
        }
      }
    } else {
      retVal = false;
    }

    return retVal;
  }

  public void showList() {
    doShowList();
  }

  public void doShowList() {
    for (String view : mapView.keySet()) {
      if (view.startsWith("list")) {
        attachView(mapView.get(view));
        break;
      }
    }
  }

  public void refresh() {
    refresh(false);
  }

  public void refresh(boolean refreshCache) {
    isEditing = false;
    cache = null;
    count = null;
    startIndex = 0;
    if (refreshCache) {
      controller.refresh();
    }
    if (currentView != null)
      currentView.setData();
  }

  public void clearFilter() {
    filter = new Filter();
  }

  public void delete(List<Integer> ids) {
    String msg = Translate.translate(ZKEnv.getEnv(), null, "Delete:") + " [";
    for (Integer id : ids) {
      msg += controller.getDisplayString(id) + ", ";
    }
    msg = msg.substring(0, msg.length() - 2);
    msg += "]";
    Messagebox.show(msg, Translate.translate(ZKEnv.getEnv(), null, "Confirm?"), Messagebox.OK | Messagebox.CANCEL,
        Messagebox.QUESTION, new EventListener<Event>() {

          @Override
          public void onEvent(Event event) throws Exception {
            if (event.getName().equals(Messagebox.ON_OK)) {
              VActionResponse response = controller.delete(ids);
              if (response.status) {
                cache = null;
                count = null;
                showList();
              } else {
                showNotification(response);
              }
            }
          }
        });
  }

  public VActionResponse update(List<Integer> ids, Map<String, Object> values) {
    VActionResponse response = new VActionResponse();
    if (ids == null || ids.size() == 0) {
      response.status = true;
    } else {
      response = controller.update(ids, values);
    }
    if (response.status) {
      isEditing = false;
    }
    return response;
  }

  public VActionResponse create(Map<String, Object> values) {
    VActionResponse response = controller.create(values);
    if (response.status) {
      count = null;
      cache = null;
    }
    return response;
  }

  public void moveFist() {
    setCurrentId(getItemAt(0));
  }

  public void movePreview() {
    int currentPosition = mapPosition.get(currentId) - 1;
    if (currentPosition < 0)
      currentPosition = 0;
    setCurrentId(getItemAt(currentPosition));
  }

  public Integer getCurrentPosition() {
    if (currentId == null)
      return -1;
    return mapPosition.get(currentId) != null ? mapPosition.get(currentId) : (getCount().intValue() - 1);
  }

  public void moveNext() {
    int currentPosition = mapPosition.get(currentId) + 1;
    if (currentPosition >= getCount())
      currentPosition = getCount().intValue() - 1;
    setCurrentId(getItemAt(currentPosition));
  }

  public void moveLast() {
    setCurrentId(getItemAt(getCount().intValue() - 1));
  }

  public void showNotification(VActionResponse response) {
    if (response.messages.size() > 0) {

      String detailnof = "";
      for (String notification : response.messages) {
        detailnof = detailnof + notification + "</br>";
      }
      Clients.showNotification(detailnof);
    }
  }

  private void getReports() {

    Filter filter = new Filter();
    filter.hqlWhereClause = "model = :model AND jasper_name IS NOT NULL";
    filter.params.put("model", this.controller.modelName);
    VController sysReport = this.controller.getVEnv().get("Sys.Report");
    List<Integer> lstIds = sysReport.search(filter);
    reports = sysReport.browse(lstIds);
  }

  public VReportViewer print(int reportId) throws Exception {

    Map<String, Object> mapParams = new HashMap<>();
    mapParams.put("RECORD_ID", this.getCurrentId());
    VReportViewer reportViewer = new VReportViewer(reportId, mapParams);
    reportViewer.setParent(divBody);
    reportViewer.doModal();

    return reportViewer;
  }
}