/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Jul 27, 2016
* Author: tuanpa
*
*/
package base.view;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlNativeComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.A;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Foot;
import org.zkoss.zul.Footer;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Span;
import org.zkoss.zul.event.ZulEvents;
import org.zkoss.zul.ext.Sortable;

import base.common.CommonUtils;
import base.controller.VController;
import base.model.FunctionField;
import base.model.Many2OneField;
import base.model.VField;
import base.model.VObject;
import base.util.StringUtils;
import base.util.Translate;
import base.util.ZKEnv;

/**
 * 
 * @author tuanpa
 *
 *         ListView: BorderLayout, main function is display list of record Can
 *         extends to have more info on this view
 *
 */
public class VListView extends VView implements EventListener<Event>, HandlerSearchEvent {

  public static final int PAGE_SIZE_DEFAULT = 10;
  public Integer pagingSize = PAGE_SIZE_DEFAULT;
  private VSearchManagement searchs;
  private Foot foot;

  public VListView(VViewDefine viewDef, VWindow window) {
    super(viewDef, window);
  }

  /**
   * 
   */
  private static final long serialVersionUID = 65196189269245684L;

  private Grid grid;
  private Columns columns;
  private Map<String, VField> mapField;

  Div listHeader;
  Div listFooter;

  @Override
  protected void initUI() {
    super.initUI();
    listHeader = new Div();
    listHeader.setWidth("100%");
    listHeader.setHflex("min");
    listHeader.setParent(this);

    HtmlNativeComponent lstBody = new HtmlNativeComponent();
    lstBody.setTag("div");
    lstBody.setDynamicProperty("class", "container-fluid input");
    lstBody.setDynamicProperty("style", "padding-left: 0px; padding-right: 0px");
    lstBody.setParent(this);
    if (pagingSize == null)
      pagingSize = PAGE_SIZE_DEFAULT;
    grid = new Grid();
    grid.setId("grid_" + RandomUtils.nextInt(100));
    grid.setParent(lstBody);
    grid.setWidth("100%");
    grid.setVflex("min");

    try {
      if (!window.getController().isGiganticTable()) {
        grid.setMold("paging");
        grid.setPageSize(pagingSize);
        grid.setSizedByContent(true);
      } else {
        A a = new A("Load More");
        a.setStyle(
            "float: left;width:100%;text-align:center;height: 25px;font-size: 14px;padding-right:20px;color: #187E5D;outline: none;margin-top:10px");
        a.setParent(lstBody);
        a.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
          @Override
          public void onEvent(Event arg0) throws Exception {
            window.loadMore();
          }
        });
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    // grid.setZclass("none");
    grid.setClientAttribute("xmlns:ca", "client/attribute");
    grid.setClientAttribute("ca:data-scrollable", "false");

    grid.setSpan(true);

    grid.addEventListener(Events.ON_SELECT, this);
    grid.addEventListener(ZulEvents.ON_PAGING, this);
    columns = new Columns();
    searchs = new VSearchManagement(VSearchManagement.isAND);
    searchs.setHandler(this);
    searchs.getHeadrelement().setParent(grid);
    columns.setParent(grid);
    columns.setSizable(false);
    columns.setMenupopup("auto");
    Column column = new Column();
    column.setWidth("20px");
    column.setParent(columns);
    Checkbox checkbox = new Checkbox();
    checkbox.setParent(column);
    checkbox.setAttribute("action", "selectAll");
    checkbox.addEventListener(Events.ON_CHECK, this);

    listFooter = new Div();
    listFooter.setWidth("100%");
    listFooter.setHflex("min");
    listFooter.setParent(this);
  }

  @Override
  public void parseXML() {
    mapField = new LinkedHashMap<>();
    searchs.addfieldSearch("stt", null);
    recursiveParseNode(viewDef.xmlNode, this);
    searchs.addfieldSearch(null, null);
    if (searchs.getHldetail() != null) {
      foot = new Foot();
      foot.setHeight("40px");
      foot.setParent(grid);
      foot.setVisible(false);
      Footer footer = new Footer();
      footer.setParent(foot);
      footer.appendChild(searchs.getHldetail());
      footer.setSpan(columns.getChildren().size());
    }
    VRowRenderer renderer = new VRowRenderer(mapField, window.getController(), this);
    Column column = new Column();
    column.setParent(columns);
    column.setWidth("30px");
    column.setAlign("center");
    Span span = new Span();
    span.setSclass("fa fa-lg fa-fw fa-download");
    span.setStyle("cursor: pointer;");
    span.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
      @Override
      public void onEvent(Event event) throws Exception {
        // TODO Auto-generated method stub
        CommonUtils.exportListboxToExcel(getGridToExportExcel(), viewDef.model + ".xlsx");

      }
    });
    span.setParent(column);
    grid.setRowRenderer(renderer);
    grid.setStyle("border: none; height:100% !important");
  }

  private Grid getGridToExportExcel() {
    Grid gridExcel = (Grid) grid.clone();
    gridExcel.renderAll();
    int sizeCol = gridExcel.getColumns().getChildren().size();
    gridExcel.getColumns().getChildren().get(sizeCol - 1).setParent(null);
    gridExcel.getColumns().getChildren().get(0).setParent(null);
    RowRenderer<Integer> renderForExcel = new RowRenderer<Integer>() {

      @Override
      public void render(Row row, Integer id, int index) throws Exception {
        for (String fieldName : mapField.keySet()) {
          Object value = window.getController().getValue(id, fieldName);
          if (value instanceof VObject && mapField.get(fieldName) instanceof Many2OneField) {
            Label lb = new Label();
            String modelName = ((Many2OneField) mapField.get(fieldName)).parentModel;
            VController fieldController = ZKEnv.getEnv().get(modelName);
            lb.setValue(fieldController.getDisplayString(((VObject) value).getId()));
            row.appendChild(lb);
          } else {
            row.appendChild(new Label(value + ""));
          }

        }
      }
    };
    gridExcel.setRowRenderer(renderForExcel);
    gridExcel.renderAll();
    return gridExcel;
  }

  private void recursiveParseNode(Element node, Component parentComponent) {
    if (node.getNodeName().equalsIgnoreCase("view") || node.getNodeName().equalsIgnoreCase("list")) {
      Component newParent = parentComponent;
      if (node.getNodeName().equalsIgnoreCase("list")) {
        newParent = columns;
      }
      Node childNode = node.getFirstChild();
      while (childNode != null) {
        if (childNode.getNodeType() == Node.ELEMENT_NODE) {
          recursiveParseNode((Element) childNode, newParent);
        }
        childNode = childNode.getNextSibling();
      }
    } else if (node.getNodeName().equalsIgnoreCase("field") && node.getNodeType() == Node.ELEMENT_NODE) {
      String fieldName = node.getAttribute("name");
      String editorClass = node.getAttribute("editor");
      VField field = window.getController().getField(fieldName);
      if (field != null) {
        if (StringUtils.isNotEmpty(editorClass)) {
          field.editorClass = editorClass;
        }
        mapField.put(fieldName, field);
        Column column = new Column();
        column.setLabel(Translate.translate(ZKEnv.getEnv(), viewDef.moduleId, field.label));
        column.setParent(parentComponent);
        try {
          column.setSort("auto(" + fieldName + ")");
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        column.addEventListener(Events.ON_AFTER_SIZE, this);
        searchs.addfieldSearch(fieldName, field);
      }
    } else if (node.getNodeName().equalsIgnoreCase("header") && node.getFirstChild() != null
        && node.getFirstChild().getNodeType() == Node.TEXT_NODE) {
      String headerClass = node.getFirstChild().getNodeValue();
      try {
        VListHeaderFooter header = (VListHeaderFooter) Class.forName(headerClass).newInstance();
        header.controller = this.window.getController();
        header.filter = this.window.filter;
        header.vListView = this;
        header.initUI();
        header.setParent(listHeader);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else if (node.getNodeName().equalsIgnoreCase("footer") && node.getFirstChild() != null
        && node.getFirstChild().getNodeType() == Node.TEXT_NODE) {
      String footerClass = node.getFirstChild().getNodeValue();
      try {
        VListHeaderFooter footer = (VListHeaderFooter) Class.forName(footerClass).newInstance();
        footer.controller = this.window.getController();
        footer.filter = this.window.filter;
        footer.vListView = this;
        footer.initUI();
        footer.setParent(listFooter);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public String getViewType() {
    return VViewType.LIST.getValue();
  }

  private ArrayList<Integer> selectedIds = new ArrayList<>();

  public ArrayList<Integer> getSelectedIds() {
    return selectedIds;
  }

  @Override
  public void onEvent(Event event) throws Exception {
    if (event.getName().equals(Events.ON_CLICK) && event.getTarget() instanceof Row) {
      int id = (int) event.getTarget().getAttribute("id");
      window.setCurrentId(id);
      window.showDetail();
    } else if (event.getName().equals(Events.ON_CHECK) && event.getTarget() instanceof Checkbox) {
      // Select all
      if (event.getTarget().getAttribute("action") != null
          && event.getTarget().getAttribute("action").equals("selectAll")) {
        Checkbox checkbox = (Checkbox) event.getTarget();
        for (Component comp : grid.getChildren()) {
          if (comp instanceof Rows) {
            for (Component rowChild : comp.getChildren()) {
              Row row = (Row) rowChild;
              if (row.getIndex() / grid.getPageSize() == grid.getActivePage()) {
                if (row.getFirstChild() instanceof Checkbox) {
                  Checkbox cb = (Checkbox) row.getFirstChild();
                  cb.setChecked(checkbox.isChecked());
                  checkboxHandler(cb);
                }
              }
            }
          }
        }
      }
      // Select row
      else if (event.getTarget().getAttribute("id") != null) {
        checkboxHandler((Checkbox) event.getTarget());
      }
      event.stopPropagation();
    } else if (event.getTarget().getAttribute("action") != null
        && event.getTarget().getAttribute("action").equals("showHelp")) {
      Clients.showNotification(window.getController().getDescription(), event.getTarget());
    } else if (event.getTarget() instanceof Combobox && event.getTarget().getAttribute("action") != null
        && event.getTarget().getAttribute("action").equals("pageSize")) {
      Combobox combobox = (Combobox) event.getTarget();
      Comboitem item = combobox.getSelectedItem();
      pagingSize = item.getValue();
      if (pagingSize > 0)
        grid.setPageSize(pagingSize.intValue());
      else {
        grid.setPageSize(Integer.MAX_VALUE);
      }
    } else if (event.getTarget().getAttribute("action") != null
        && event.getTarget().getAttribute("action").equals("new")) {
      window.newRecord();
    } else if (event.getTarget().getAttribute("action") != null
        && event.getTarget().getAttribute("action").equals("delete")) {
      if (event.getTarget().getAttribute("id") != null) {
        List<Integer> ids = new ArrayList<>();
        ids.add((Integer) event.getTarget().getAttribute("id"));
        window.delete(ids);
      } else {
        window.delete(selectedIds);
      }
    } else if (event.getName().equals(ZulEvents.ON_PAGING)) {
      applyTableStyle();
    }
  }

  private void checkboxHandler(Checkbox checkbox) {
    Integer id = (Integer) checkbox.getAttribute("id");
    if (checkbox.isChecked()) {
      if (!selectedIds.contains(id))
        selectedIds.add(id);
    } else {
      selectedIds.remove(id);
    }
    if (selectedIds.size() > 0)
      getCenterHeader().setVisible(true);
    else
      getCenterHeader().setVisible(false);
  }

  class ListModel extends AbstractListModel<Integer> implements Sortable<Object> {

    /**
     * 
     */
    private static final long serialVersionUID = -3587915228827886117L;

    @Override
    public Integer getElementAt(int index) {
      return window.getItemAt(index);
    }

    @Override
    public int getSize() {
      int size = 0;
      if (window.getController().isGiganticTable()) {
        size = window.getSizeGiganticTable();
      } else {
        size = window.getCount().intValue();
      }
      return size;
    }

    @Override
    public String getSortDirection(Comparator<Object> arg0) {
      return null;
    }

    @Override
    public void sort(Comparator<Object> comparator, boolean isAsc) {
      if (comparator instanceof FieldComparator) {
        FieldComparator fComparator = (FieldComparator) comparator;
        String sortby = fComparator.getRawOrderBy();
        if (window.getController().getField(sortby) instanceof FunctionField) {
          return;
        }
        if (!fComparator.isAscending()) {
          sortby += " desc";
        }
        window.setSortBy(sortby);
        window.refresh();
      }
    }

  }

  private void applyTableStyle() {
    // Clients.evalJavaScript("setTableStyle('" + grid.getId() + "',
    // 'table-striped table-hover');");
  }

  @Override
  public void setData() {
    grid.setModel(new ListModel());
    applyTableStyle();
    grid.invalidate();
  }

  Button btNew;

  @Override
  public Component getLeftHeader() {
    if (btNew == null) {
      btNew = new Button(Translate.translate(ZKEnv.getEnv(), viewDef.moduleId, "New"));
      btNew.setZclass("none");
      btNew.setSclass("btn btn-primary");
      btNew.setAttribute("action", "new");
      btNew.addEventListener(Events.ON_CLICK, this);
    }
    return btNew;
  }

  Button btDelete;

  @Override
  public Component getCenterHeader() {
    if (btDelete == null) {
      btDelete = new Button(Translate.translate(ZKEnv.getEnv(), viewDef.moduleId, "Delete"));
      btDelete.setAttribute("action", "delete");
      btDelete.setVisible(false);
      btDelete.addEventListener(Events.ON_CLICK, this);
      btDelete.setZclass("none");
      btDelete.setSclass("btn btn-danger");
    }
    return btDelete;
  }

  Hlayout layout;

  @Override
  public Component getRightHeader() {
    if (layout == null) {
      layout = new Hlayout();
      layout.setHflex("min");
      Combobox combobox = new Combobox();
      combobox.setAttribute("action", "pageSize");
      combobox.setMold("rounded");
      Comboitem item = new Comboitem("10");
      item.setValue(10);
      item.setParent(combobox);
      item = new Comboitem("20");
      item.setValue(20);
      item.setParent(combobox);
      item = new Comboitem("50");
      item.setValue(50);
      item.setParent(combobox);
      item = new Comboitem("All");
      item.setValue(0);
      item.setParent(combobox);
      combobox.setSelectedIndex(0);
      combobox.setReadonly(true);
      combobox.setWidth("50px");
      Label label = new Label("Page size: ");
      if (!window.getController().isGiganticTable()) {
        layout.appendChild(label);
        layout.appendChild(combobox);
        combobox.addEventListener(Events.ON_SELECT, this);
      }
    }
    return layout;
  }

  /**
   * <i class="fa-fw fa fa-home"></i> Dashboard <span>> My Dashboard</span>
   */
  @Override
  public Component getTitleComponent() {
    HtmlNativeComponent titleDiv = new HtmlNativeComponent("h1",
        "<i class=\"fa-fw fa fa-list-ul\"></i> " + Translate.translate(ZKEnv.getEnv(), null, "List") + " <span>> "
            + Translate.translate(ZKEnv.getEnv(), null, window.getController().getTitle()) + "</span>",
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
  public void executeSearch(String Whereclause, Map<String, Object> params) {
    window.filter.hqlWhereClause = Whereclause;
    window.filter.params = params;
    foot.setVisible(!Whereclause.equalsIgnoreCase(""));
    window.refresh(true);

  }
}
