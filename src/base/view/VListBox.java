package base.view;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Image;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import base.controller.VController;
import base.model.VField;
import base.model.VObject;
import base.util.Filter;
import base.view.editor.IEditorListenner;
import base.view.editor.VEditor;

public class VListBox extends Listbox implements IEditorListenner {

	/**
	 * @author NgocHung
	 */
	private static final long serialVersionUID = 1L;
	private Listhead listHead;
	protected Auxhead auxhead;
	private boolean searchable = false;
	private boolean paging = false;
	private boolean render = false;
	protected Map<Integer, VEditor> mapSearch;
	private ListModel<?> original;
	private List<ColumnProperties> columns;
	private VController controller;
	private HashMap<Integer, Integer> collection;
	private Integer totalSize;

	public VListBox(boolean searchable) {
		super();
		listHead = new Listhead();
		listHead.setSizable(true);
		listHead.setParent(this);
		auxhead = new Auxhead();
		mapSearch = new HashMap<>();
		columns = new ArrayList<>();
		collection = new HashMap<>();
		this.searchable = searchable;
	}

	public Listheader addIndex(boolean searchable) {
		Listheader listHeader = new Listheader();
		listHeader.setLabel("STT");
		listHeader.setParent(listHead);
		if (this.searchable && searchable) {
			if (auxhead.getParent() == null) {
				auxhead.setParent(this);
			}
			Auxheader auxheader = creatAxHeaderSearch();
			auxheader.setParent(auxhead);
		} else if (this.searchable && !searchable) {
			Auxheader auxheader = new Auxheader();
			auxheader.setParent(auxhead);
		}
		ColumnProperties column = new ColumnProperties(Integer.class, "STT", "lbx_index", null, null);
		columns.add(column);
		return listHeader;
	}

	public Listheader addColumn(String label, String fieldName, boolean searchable) {
		Listheader listHeader = new Listheader();
		listHeader.setLabel(label);
		listHeader.setParent(listHead);
		if (this.searchable && searchable) {
			if (auxhead.getParent() == null) {
				auxhead.setParent(this);
			}
			Auxheader auxheader = creatAxHeaderSearch();
			auxheader.setParent(auxhead);
		} else if (this.searchable && !searchable) {
			Auxheader auxheader = new Auxheader();
			auxheader.setParent(auxhead);
		}
		return listHeader;
	}

	public Listheader addColumn(String label, String fieldName, String displayName, String format, Class<?> clazz,
			boolean searchable) {
		Listheader listHeader = addColumn(label, fieldName, searchable);
		ColumnProperties column = new ColumnProperties(clazz, label, fieldName, displayName, format);
		columns.add(column);
		return listHeader;
	}

	protected Auxheader creatAxHeaderSearch() {
		Auxheader header = new Auxheader();
		VField field = VField.string("", new HashMap<String, Object>());
		StringSearchEditor editor = new StringSearchEditor(field);
		editor.onChangeListener = this;
		Image img = new Image("./themes/images/filter.png");
		img.setStyle("margin-right:3px");
		header.appendChild(img);
		editor.component.addForward(Events.ON_OK, editor.component, Events.ON_CHANGE);
		header.appendChild(editor.component);
		// editor.setAttribute("columnIndex", auxhead.getChildren().size());
		mapSearch.put(auxhead.getChildren().size(), editor);
		return header;
	}

	@Override
	public void selectAll() {
	}

	@Override
	public boolean appendChild(Component child) {
		return super.appendChild(child);
	}

	public Listhead getListHead() {
		return listHead;
	}

	public void setListHead(Listhead listHead) {
		this.listHead = listHead;
	}

	public Auxhead getAuxhead() {
		return auxhead;
	}

	public void setAuxhead(Auxhead auxhead) {
		this.auxhead = auxhead;
	}

	public boolean isSearchable() {
		return searchable;
	}

	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}

	@Override
	public void renderAll() {
		if (!render) {		
			super.renderAll();
			render = true;
		}
	}

	@Override
	public void setModel(ListModel<?> listModel) {
		super.setModel(listModel);
		if (!isPaging()) {
			this.renderAll();
		}
		if (original == null) {
			this.original = listModel;
		}
	}

	public Integer getReportAt(int index) {
		if (getCollection().get(index) == null) {
			int pagesize = this.getPaginal().getPageSize();
			int pageactive = index/pagesize; //this.getPaginal().getActivePage();
			List<Integer> result = getController().search(new Filter(), "id asc", pagesize * pageactive, pagesize);
			if (result.size() == 0) {
				return -1;
			}
			int count = 0;
			for (Integer val : result) {
				getCollection().put(index + count++, val);
			}
		}
		return getCollection().get(index);
	}

	private boolean needSeaching() {
		boolean hasVal = false;
		for (Integer keyeditor : mapSearch.keySet()) {
			Object value = mapSearch.get(keyeditor).getValue();
			if (value != null) {
				hasVal = true;
				break;
			}
		}
		return hasVal;
	}

	@Override
	public void onChangedValue(VEditor editor) {
		if (!needSeaching()) {
			setModel(original);
		} else {			
			this.renderAll();
			List<Listitem> listitem = this.getItems();
			List<?> listcopy = new ArrayList<>();
			if (listitem.size() > 0) {
				for (Listitem item : listitem) {
					boolean isShow = true;
					for (Integer keyeditor : mapSearch.keySet()) {
						Object value = mapSearch.get(keyeditor).getValue();
						if (value != null) {
							String strSearch = (String) value;
							if (item.getChildren() != null) {
								if (item.getChildren().size() > keyeditor) {
									Listcell listcell = (Listcell) item.getChildren().get(keyeditor);
									if (listcell.getLabel() != null) {
										String content = listcell.getLabel().toLowerCase();
										isShow = isShow && content.contains(strSearch.toLowerCase());
										if (!isShow) {
											continue;
										} else {
											listcopy.add(item.getValue());
											break;
										}
									}
								}
							}
						}
					}
				}
				setModel(new ListModelList<>(listcopy));
			}
		}
	}

	private void renderItem() {
		this.setItemRenderer(new ListitemRenderer<Integer>() {
			@Override
			public void render(Listitem item, Integer id, int index) throws Exception {
				for (ColumnProperties column : columns) {
					item.setValue(id);
					if (column.getName() == "lbx_index") {
						new Listcell(++index + "").setParent(item);
					} else {
						Object object = getController().getValue(id, column.getName());
						if (object == null) {
							new Listcell("").setParent(item);
						} else if (object instanceof VObject) {
							if (column.getDisplay() != null) {
								VObject refer = (VObject) object;
								String display = (String) refer.getValue(column.getDisplay());
								new Listcell(display == null ? "" : display).setParent(item);
							}
						} else if (Date.class.isAssignableFrom(column.getClazz())) {
							SimpleDateFormat sdf = new SimpleDateFormat(column.getFormat());
							new Listcell(object == null ? "" : sdf.format((Date) object)).setParent(item);
						} else if (Double.class.isAssignableFrom(column.getClass())
								|| Float.class.isAssignableFrom(column.getClass())) {
							DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
							decimalFormatSymbols.setDecimalSeparator('.');
							decimalFormatSymbols.setGroupingSeparator(','); // "#,##0.00"
							DecimalFormat decimalFormat = new DecimalFormat(column.getFormat(), decimalFormatSymbols);
							new Listcell(object == null ? "" : decimalFormat.format(object)).setParent(item);
						} else {
							new Listcell("" + object).setParent(item);
						}
					}
				}
			}
		});
	}

	public void renderListbox(VController controller, boolean paging, boolean multichoise, Integer pagesize) {
		this.setController(controller);
		this.setPaging(paging);
		this.renderItem();
		this.setVflex(true);
		this.setCheckmark(multichoise);
		this.setMold("paging");
		this.setSizedByContent(true);
		this.setSpan(true);

		if (!paging) {
			List<Integer> ids = controller.search(new Filter());
			if (ids != null && ids.size() > 0) {
				ListModelList<?> model = new ListModelList<>(ids);
				model.setMultiple(multichoise);
				this.setModel(model);
			}
		} else {
			if (pagesize > 0) {
				this.setPageSize(pagesize);
			}
			Long size = controller.count(new Filter());
			this.setTotalSize(Math.toIntExact(size.longValue()));
			ModelList modelList = getModelList();
			modelList.setMultiple(multichoise);
			modelList.setActivePage(0);
			this.setModel(modelList);
		}
	}

	public boolean isPaging() {
		return paging;
	}

	public void setPaging(boolean paging) {
		this.paging = paging;
	}

	public ListModel<?> getOriginal() {
		return original;
	}

	public void setOriginal(ListModel<?> original) {
		this.original = original;
	}

	public VController getController() {
		return controller;
	}

	public void setController(VController controller) {
		this.controller = controller;
	}

	public HashMap<Integer, Integer> getCollection() {
		return collection;
	}

	public void setCollection(HashMap<Integer, Integer> collection) {
		this.collection = collection;
	}

	public Integer getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(Integer totalSize) {
		this.totalSize = totalSize;
	}

	public ModelList getModelList() {
		ModelList model = new ModelList();
		return model;
	}

	class ModelList extends AbstractListModel<Integer> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5366006447656022384L;

		@Override
		public Integer getElementAt(int index) {
			return getReportAt(index);
		}

		@Override
		public int getSize() {
			return getTotalSize();
		}

		@Override
		public void setMultiple(boolean multi) {
			super.setMultiple(multi);
		}
	}

	class ColumnProperties {
		private Class<?> clazz;
		private String label;
		private String name;
		private String display;
		private String format;

		public ColumnProperties(Class<?> clazz, String label, String name, String display, String format) {
			// TODO Auto-generated constructor stub
			this.clazz = clazz;
			this.label = label;
			this.name = name;
			this.format = format;
			this.display = display;
		}

		public Class<?> getClazz() {
			return clazz;
		}

		public void setClazz(Class<?> clazz) {
			this.clazz = clazz;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getFormat() {
			return format;
		}

		public void setFormat(String format) {
			this.format = format;
		}

		public String getDisplay() {
			return display;
		}

		public void setDisplay(String display) {
			this.display = display;
		}
	}
}
