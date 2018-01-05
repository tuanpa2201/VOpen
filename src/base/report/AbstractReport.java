package base.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlNativeComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vlayout;

import base.util.DateUtils;
import base.util.ExportExcel;
import base.util.StringUtils;
import base.util.Translate;
import base.util.ZKEnv;
import base.view.VReportViewer;

public abstract class AbstractReport extends HtmlNativeComponent implements IReportEListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Div divBody;
	private Button btnReport;
	private Button btnReportJasper;
	private Combobox cbPageSize;
	private Map<String, InputReport> inputValues;
	private int pageSize = 10;
	private List<InputReport> inputs;
	protected int reportJasperId;

	public AbstractReport() {
		super();
		inputValues = new HashMap<>();
		initUI();
	}

	public String getTitle() {
		return "Report";
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
		HtmlNativeComponent divMainControl = new HtmlNativeComponent("div");
		divMainControl.setDynamicProperty("class", "container-fluid");
		divMainControl.setParent(this);

		HtmlNativeComponent titleLayout = new HtmlNativeComponent("div");
		titleLayout.setDynamicProperty("class", "container-fluid");
		titleLayout.setDynamicProperty("style", "padding-left:1px !important ");
		titleLayout.setParent(divMainControl);
		creatTitleDiv(titleLayout);

		HtmlNativeComponent Control = new HtmlNativeComponent("div");
		Control.setDynamicProperty("class", "container-fluid");
		Control.setDynamicProperty("style", "padding-left: 0px; padding-right: 0px");
		Control.setParent(divMainControl);
		for (HtmlNativeComponent col : initColumns()) {
			col.setParent(Control);
		}
		HtmlNativeComponent divHeader = new HtmlNativeComponent("div");
		divHeader.setDynamicProperty("class", "container-fluid");
		divHeader.setParent(this);

		HtmlNativeComponent hlayout = new HtmlNativeComponent("div");
		hlayout.setDynamicProperty("class", "container-fluid");
		hlayout.setDynamicProperty("style", "padding-left: 0px; padding-right: 0px");
		hlayout.setParent(divHeader);

		HtmlNativeComponent divLeft = new HtmlNativeComponent("div");
		divLeft.setDynamicProperty("class", "col-md-6");
		divLeft.setDynamicProperty("style", "padding-left: 0px; padding-right: 0px");
		divLeft.setParent(hlayout);

		HtmlNativeComponent divRight = new HtmlNativeComponent("div");
		divRight.setDynamicProperty("class", "col-md-6");
		divRight.setDynamicProperty("style", "padding-left: 0px; padding-right: 0px");
		divRight.setParent(hlayout);

		this.initLeftHeader(divLeft);
		this.initRightHeader(divRight);
	}

	private List<HtmlNativeComponent> initColumns() {
		List<HtmlNativeComponent> columns = new ArrayList<>();
		inputs = getInputCondition();
		HtmlNativeComponent control = new HtmlNativeComponent("div");
		if (inputs.size() <= 4) {
			for (int i = 0; i < 2; i++) {
				HtmlNativeComponent inputElement = new HtmlNativeComponent("div");
				inputElement.setDynamicProperty("class", "col-md-5");
				inputElement.setDynamicProperty("style", "padding-left: 0px;padding-right: 0px");
				columns.add(inputElement);
			}
			control.setDynamicProperty("class", "col-md-2");
		} else {
			for (int i = 0; i < 3; i++) {
				HtmlNativeComponent controlElement = new HtmlNativeComponent("div");
				controlElement.setDynamicProperty("class", "col-md-3");
				controlElement.setDynamicProperty("style", "padding-left: 0px;padding-right: 0px");
				columns.add(controlElement);
			}
			control.setDynamicProperty("class", "col-md-3");
		}
		control.setDynamicProperty("style", "padding-left: 0px; padding-right: 0px ; text-align: center");
		creatControl(control);
		columns.add(control);
		int totalCol = columns.size() - 1;
		int index = 0;
		for (int i = 0; i < inputs.size(); i++) {
			creatVlInput(inputs.get(i)).setParent(columns.get(index));
			index++;
			if (index >= totalCol) {
				index = 0;
			}
		}
		return columns;
	}

	private Vlayout creatVlInput(InputReport inputElement) {
		Vlayout vl = new Vlayout();
		vl.setSclass("z-vl-report-item");
		Hlayout hl = new Hlayout();
		hl.setSclass("z-hl-report-item");
		hl.setParent(vl);
		hl.appendChild(new Label(Translate.translate(ZKEnv.getEnv(), null, inputElement.getTitle().trim())));
		hl.appendChild(inputElement.component);
		return vl;
	}

	public List<InputReport> getInputCondition() {
		List<InputReport> inputs = new ArrayList<>();
		inputs.add(InputReport.DateTime("From: ", "timeBegin", this, DateUtils.getDateNow()));
		inputs.add(InputReport.DateTime("To: ", "timeStop", this, new Date(System.currentTimeMillis())));
		return inputs;
	};

	private void creatControl(HtmlNativeComponent controlright) {
		btnReport = new Button(Translate.translate(ZKEnv.getEnv(), null, "Query"));
		btnReport.setSclass("none");
		btnReport.setSclass("btn btn-primary v-btn-report");
		btnReport.setParent(controlright);
		btnReport.addEventListener(Events.ON_CLICK, EVENT_EXCUTE_REPORT);

		if (reportJasperId > 0) {

			btnReportJasper = new Button(Translate.translate(ZKEnv.getEnv(), null, "Print"));
			btnReportJasper.setStyle("margin-left:2px;");
			btnReportJasper.setSclass("none");
			btnReportJasper.setSclass("btn btn-primary");
			btnReportJasper.setParent(controlright);
			btnReportJasper.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

				@Override
				public void onEvent(Event arg0) throws Exception {

					Map<String, Object> mapParams = new HashMap<>();
					for (InputReport ip : inputs) {

						mapParams.put(ip.getParamName(), ip.getValueConverted());
					}

					VReportViewer reportViewer = new VReportViewer(reportJasperId, mapParams);
					reportViewer.setParent(divBody);
					reportViewer.doModal();
				}
			});
		}
	}

	private EventListener<Event> EVENT_EXCUTE_REPORT = new EventListener<Event>() {
		@Override
		public void onEvent(Event arg0) throws Exception {
			String validate = validateInput(inputValues);
			if (StringUtils.isEmpty(validate)) {
				if (gridReport.getModelReport() == null) {
					gridReport.setModelReport(getModel());
				}
				gridReport.execute(inputValues);
				lblResultReport.setValue(
						Translate.translate(ZKEnv.getEnv(), null, "Total Result") + ": " + gridReport.getSize());
			} else {
				showNotification(validate);
			}
		}
	};

	protected void executeReport() {
		Events.sendEvent(Events.ON_CLICK, btnReport, null);
	}

	public String validateInput(Map<String, InputReport> inputs) {
		String result = "";
		InputReport begin = inputs.get("timeBegin");
		InputReport end = inputs.get("timeStop");
		if (begin != null && end != null) {
			long bg = ((Date) begin.getValue()).getTime();
			long en = ((Date) end.getValue()).getTime();
			if (en <= bg) {
				result += " - " + Translate.translate(ZKEnv.getEnv(), null, "Time input incorrect");
			}
		} else {
			result += " - " + Translate.translate(ZKEnv.getEnv(), null, "please select the time");
		}
		return result;
	}

	public void showNotification(String msg) {
		Clients.showNotification(msg, true);
	}

	public List<VColumn> getVColumns() {
		List<VColumn> vColumns = new ArrayList<>();
		vColumns.add(new VColumn("stt", "STT", 40, false));
		return vColumns;
	};

	public AbstractModelReport getModel() {
		return null;
	}

	private void creatTitleDiv(HtmlNativeComponent titleLayout) {
		HtmlNativeComponent titleDiv = new HtmlNativeComponent("h1", "<i class=\"fa-fw fa fa-calendar\"></i> "
				+ Translate.translate(ZKEnv.getEnv(), null, getTitle()) + "</span>", null);
		titleDiv.setDynamicProperty("class", "txt-color-blueDark");
		titleDiv.setDynamicProperty("style", "margin-bottom: 15px");
		titleLayout.appendChild(titleDiv);
	}

	private Label lblResultReport;

	private void initLeftHeader(Component parent) {
		Hbox hbox = new Hbox();
		hbox.setParent(parent);
		hbox.setHflex("1");
		hbox.setHeight("min");
		hbox.setPack("start");
		Button btnexportexcel = new Toolbarbutton();
		btnexportexcel.setTooltip(Translate.translate(ZKEnv.getEnv(), null, "Export Excel"));
		btnexportexcel.setImage("./themes/images/excelex.png");
		btnexportexcel.setParent(hbox);
		btnexportexcel.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			@Override
			public void onEvent(Event arg0) throws Exception {
				if (gridReport.getSize() > 0) {
					exportToExcel();
				} else {
					showNotification("No Data To Excel!");
				}
			}
		});
		lblResultReport = new Label();
		lblResultReport.setParent(hbox);
		lblResultReport.setSclass("z-label-result-report");

	}

	private void exportToExcel() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date begin = (Date) inputValues.get("timeBegin").getValue();
		Date end = (Date) inputValues.get("timeStop").getValue();
		String timerange = "Từ thời điểm " + dateFormat.format(begin) + " tới thời điểm " + dateFormat.format(end);
		ExportExcel export = new ExportExcel("exportdata.xlsx", getTitle(), timerange, null);
		export.setAddExportTime(true);
		export.exportExcel(gridReport.getvColumns(), gridReport.getReports());
	}

	Hbox rightHeaderHolder;
	HtmlNativeComponent viewTypeHolder;

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
		Hlayout layout = new Hlayout();
		layout.setHflex("min");
		cbPageSize = new Combobox();
		cbPageSize.setMold("rounded");
		Comboitem item = new Comboitem("10");
		item.setValue(10);
		item.setParent(cbPageSize);
		item = new Comboitem("20");
		item.setValue(20);
		item.setParent(cbPageSize);
		item = new Comboitem("50");
		item.setValue(50);
		item.setParent(cbPageSize);
		item = new Comboitem(Translate.translate(ZKEnv.getEnv(), null, "All"));
		item.setValue(Integer.MAX_VALUE);
		item.setParent(cbPageSize);
		cbPageSize.setSelectedIndex(0);
		cbPageSize.setReadonly(true);
		cbPageSize.setWidth("50px");
		cbPageSize.addEventListener(Events.ON_SELECT, EVENT_PAGESIZE_SELECT);
		Label label = new Label(Translate.translate(ZKEnv.getEnv(), null, "Page size:"));
		layout.appendChild(label);
		layout.appendChild(cbPageSize);
		viewTypeHolder = new HtmlNativeComponent("div");
		viewTypeHolder.setParent(box);
		viewTypeHolder.setDynamicProperty("class", "btn-group");
		Button listview = new Button();
		listview.setImage("themes/images/list.png");
		listview.setZclass("none");
		listview.setSclass("btn btn-default");
		listview.setParent(viewTypeHolder);
		listview.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				gridReport.setVisible(true);
			}
		});
		Button printView = new Button();
		printView.setZclass("none");
		printView.setSclass("btn btn-default");
		printView.setParent(viewTypeHolder);
		printView.setImage("themes/images/printer.png");
		printView.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				gridReport.setVisible(false);
			}
		});
		rightHeaderHolder.appendChild(layout);
	}

	private EventListener<Event> EVENT_PAGESIZE_SELECT = new EventListener<Event>() {
		@Override
		public void onEvent(Event event) throws Exception {
			Comboitem item = ((Combobox) event.getTarget()).getSelectedItem();
			if (item != null && item.getValue() != null) {
				pageSize = item.getValue();
				gridReport.setPageSize(pageSize);
			}

		}
	};

	private void initBody() {
		divBody = new Div();
		divBody.setZclass("none");
		divBody.setSclass("col-md-12");
		divBody.setWidth("100%");
		divBody.setHeight("auto");
		divBody.setParent(this);
		initListView();
	}

	private VListViewReport gridReport;

	private void initListView() {
		gridReport = new VListViewReport(getVColumns());
		gridReport.setParent(divBody);
		gridReport.setHflex("1");
		gridReport.setClientAttribute("xmlns:ca", "client/attribute");
		gridReport.setClientAttribute("ca:data-scrollable", "false");
		gridReport.setMold("paging");
		gridReport.setPageSize(pageSize);
		gridReport.setSizedByContent(true);
		gridReport.setSpan(true);
		gridReport.setEmptyMessage(Translate.translate(ZKEnv.getEnv(), null, "No Data"));
		gridReport.setAction("show: slideDown;hide: slideUp");
	}

	protected EventListener<Event> listener = new EventListener<Event>() {
		@Override
		public void onEvent(Event event) throws Exception {
			Object data = event.getTarget().getAttribute("rowData");
			executeFunction(data);
		}
	};

	public void executeFunction(Object data) {

	}

	public List<?> getData(Map<String, Object> params) {
		return null;
	}

	public void setDefaultsValue(Map<String, Object> params) {
		for (InputReport inputReport : inputs) {
			Object defaultVal = params.get(inputReport.paramName);
			if (defaultVal != null) {
				inputReport.setValue(defaultVal);
			}
		}

	}

	@Override
	public void onChangedValue(InputReport editor) {
		if (editor != null) {
			inputValues.put(editor.getParamName(), editor);
		}
	}
}
