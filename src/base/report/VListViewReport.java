package base.report;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.AfterSizeEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Frozen;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Toolbarbutton;

import base.model.VField;
import base.util.StringUtils;
import base.util.Translate;
import base.util.ZKEnv;
import base.view.StringSearchEditor;
import base.view.editor.IEditorListenner;
import base.view.editor.VEditor;

public class VListViewReport extends Grid implements IEditorListenner {
	/**
	 * @author NgocHung
	 */
	private static final long serialVersionUID = 1L;
	private List<VColumn> vColumns;
	private Auxhead auxhead;
	private Frozen frozen;
	private Map<String, VEditor> mapSearch;
	private int widthConten = 0;
	private boolean isFirstLoad = true;
	private AbstractModelReport modelReport;

	public VListViewReport(List<VColumn> vcolumns) {
		super();
		vColumns = vcolumns;
		auxhead = new Auxhead();
		mapSearch = new HashMap<>();
		frozen = new Frozen();
		this.addEventListener(Events.ON_AFTER_SIZE, new EventListener<Event>() {
			@Override
			public void onEvent(Event events) throws Exception {
				AfterSizeEvent event = (AfterSizeEvent) events;
				widthConten = event.getWidth();
				if (isFirstLoad) {
					initColumn(vColumns);
					isFirstLoad = false;
				}
			}
		});
		this.setRowRenderer(new ReportRenderer());
	}

	private void initColumn(List<VColumn> vcolumns) {
		if (vcolumns != null && vcolumns.size() > 0) {
			Columns columns = new Columns();
			columns.setSizable(true);
			columns.setParent(this);
			Iterator<VColumn> ite = vcolumns.iterator();
			int totalWidth = 0;
			while (ite.hasNext()) {
				VColumn vCol = ite.next();
				totalWidth += vCol.getColumnSize();
				Column col = new Column(Translate.translate(ZKEnv.getEnv(), null, vCol.getColumnTitle().trim()));

				if (vCol.isSearchable()) {
					Auxheader header = creatAxHeaderSearch(vCol);
					header.setParent(auxhead);
					if (auxhead.getParent() == null) {
						auxhead.setParent(this);
					}
				} else {
					creatAxHeaderSearch(vCol).setParent(auxhead);
				}
				if (!ite.hasNext() && totalWidth < widthConten) {
					col.setHflex("1");
				} else {
					col.setWidth(vCol.getColumnSize() + "px");
				}
				if (vCol.isFrozen()) {
					int index = vcolumns.indexOf(vCol);
					frozen.setColumns(index + 1);
					frozen.setStart(1);
					if (frozen.getParent() == null) {
						frozen.setParent(this);
					}
				}
				if (vCol.getAlign() != null) {
					col.setAlign(vCol.getAlign());
				}
				col.setParent(columns);
				col.setSclass("grid-th-bonus");
			}

		}
	}

	public int getSize() {
		int res = 0;
		if (modelReport != null) {
			res = modelReport.getCount();
		}
		return res;
	}

	private Auxheader creatAxHeaderSearch(VColumn vCol) {
		Auxheader header = new Auxheader();
		if (vCol.getColumnName() != null && vCol.getColumnName().toUpperCase().equals("STT")) {
			Image img = new Image("./themes/images/filter.png");
			img.setStyle("margin-right:3px");
			header.appendChild(img);
		} else {
			if (vCol.isSearchable()) {
				VField field = VField.string("", new HashMap<String, Object>());
				StringSearchEditor editor = new StringSearchEditor(field);
				editor.onChangeListener = this;
				editor.component.addForward(Events.ON_OK, editor.component, Events.ON_CHANGE);
				header.appendChild(editor.component);
				mapSearch.put(vCol.getColumnName(), editor);
			}
		}
		return header;
	}

	@Override
	public boolean appendChild(Component child) {
		return super.appendChild(child);
	}

	public Auxhead getAuxhead() {
		return auxhead;
	}

	public void setAuxhead(Auxhead auxhead) {
		this.auxhead = auxhead;
	}

	@Override
	public void onChangedValue(VEditor editor) {
		Map<String, Object> valuesSearch = new HashMap<>();
		for (String key : mapSearch.keySet()) {
			VEditor edittorTmp = mapSearch.get(key);
			valuesSearch.put(key, edittorTmp.getValue());
		}
		modelReport.executeSearch(valuesSearch);
		this.refresh();

	}

	class ReportRenderer implements RowRenderer<RowReport> {
		@Override
		public void render(Row row, RowReport rowdata, int index) throws Exception {
			if (vColumns != null && vColumns.size() > 0) {
				for (VColumn vColumn : vColumns) {
					if (vColumn.getColumnName() != null && vColumn.getColumnName().equals("stt")) {
						Label lb = new Label(index + 1 + "");
						row.appendChild(lb);
					} else {
						if (vColumn.isFunction()) {
							Toolbarbutton btnFunction = new Toolbarbutton();
							btnFunction.setAttribute("rowData", rowdata);
							btnFunction.setLabel(vColumn.getFunctionName());
							btnFunction.setSclass("z-btnreport-function");
							btnFunction.addEventListener(Events.ON_CLICK, vColumn.getEventListener());
							row.appendChild(btnFunction);
						} else {
							Label lb = new Label("");
							Object obValue = null;
							if (vColumn.getColumnName().startsWith("get")) {
								obValue = rowdata.getValueFromMethod(vColumn.getColumnName());
							} else {
								obValue = rowdata.getValue(vColumn.getColumnName());
							}
							if (obValue != null) {
								lb.setValue(String.valueOf(obValue));
								if (!StringUtils.isEmpty(vColumn.getFormat())) {
									if (obValue instanceof Double || obValue instanceof Float) {
										lb.setValue(String.format(vColumn.getFormat(),
												Double.parseDouble((String) obValue)));
									} else if (obValue instanceof Timestamp || obValue instanceof Date) {
										DateFormat dt = new SimpleDateFormat(vColumn.getFormat());
										dt.setLenient(false);
										Date date = new Date(((Timestamp) obValue).getTime());
										lb.setValue(dt.format(date));
									}
								}
							}
							row.appendChild(lb);
						}
					}

				}

			}

		}
	}

	public void setModelReport(AbstractModelReport model) {
		this.modelReport = model;
	}

	public AbstractModelReport getModelReport() {
		return modelReport;
	}

	public void execute(Map<String, InputReport> inputs) {
		modelReport.initInputs(inputs);
		this.setModel(modelReport.getModel());
	}

	public List<?> getReports() {
		return modelReport.getDataToExport();
	}

	public void refresh() {
		this.setModel(modelReport.getModel());
	}

	public List<VColumn> getvColumns() {
		return vColumns;
	}

}
