package base.view;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.impl.InputElement;

import base.model.DateField;
import base.model.StringField;
import base.model.VField;
import base.util.StringUtils;
import base.util.Translate;
import base.util.ZKEnv;
import base.view.editor.IEditorListenner;
import base.view.editor.Many2OneEditor;
import base.view.editor.StringEditor;
import base.view.editor.VEditor;

public class VSearch implements IEditorListenner {
	private String fieldName;
	private String title;
	private VEditor veditor;
	private HandlerSearchEvent handelSearch;
	private String whereClause;
	private String typeCompare = "=";
	private Hlayout hldetail;
	private Label lbdataSearch;
	private Map<String, Object> params;

	public VSearch(String fieldName, VField vfield, String typecompare) {
		this.fieldName = fieldName;
		title = vfield.label;
		if (vfield instanceof DateField) {
			veditor = new StringEditor(new StringField());
		} else {
			VField fieldCopy = vfield;
			fieldCopy.isAllowedAddRecord = false;
			fieldCopy.nullable = true;
			veditor = VEditor.getEditor(fieldCopy);
		}
		if (!(veditor instanceof Many2OneEditor)) {
			veditor.component.addForward(Events.ON_OK, veditor.component, Events.ON_CHANGE);
		}
		if (veditor.component instanceof InputElement) {
			String placeholder = ((InputElement) veditor.component).getPlaceholder();
			if (placeholder != null && !StringUtils.isEmpty(placeholder)
					&& !placeholder.toLowerCase().contains("Find")) {
				placeholder = "find " + placeholder;
				((InputElement) veditor.component).setPlaceholder(placeholder);
			}
		}
		veditor.setStyle("display: inline-flex;");
		params = new HashMap<>();
		veditor.onChangeListener = this;
		if (typecompare != null) {
			this.typeCompare = typecompare;
		} else {
			if (vfield instanceof StringField || vfield instanceof DateField) {
				this.typeCompare = "like";
			}
		}
		if (vfield instanceof StringField) {
			whereClause = "lower(" + fieldName + ") " + this.typeCompare + " lower(:" + this.fieldName + ")";
		} else if (vfield instanceof DateField) {
			whereClause = "to_char(" + fieldName + ",'dd/mm/yyyy hh:mm:ss') " + this.typeCompare + " :"
					+ this.fieldName;
		} else {
			whereClause = fieldName + " " + this.typeCompare + " :" + this.fieldName;
		}
		initDetail(vfield);
	}

	private void initDetail(VField vfield) {
		hldetail = new Hlayout();
		hldetail.setVisible(false);
		lbdataSearch = new Label();
		lbdataSearch.setValue(getDetailString(""));
		Image img = new Image("/themes/images/close-sh.png");
		img.setStyle("cursor:pointer; margin-right:2px");
		img.setTooltip("Click to Close Search in Column");
		img.setPopup("Click to Close Search in Column");
		hldetail.appendChild(img);
		img.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {

				refresh();
				event.getTarget().getParent().setVisible(false);
			}
		});

		hldetail.appendChild(lbdataSearch);
	}

	public void refresh() {
		veditor.setValue(null);
		veditor.onChangeListener.onChangedValue(veditor);
	}

	private String getDetailString(Object value) {

		String display = "";
		String vl = "";
		if (value == null) {
		} else if (veditor.component instanceof Combobox) {
			vl = ((Combobox) veditor.component).getValue() + "";
		} else {
			vl = value.toString() + "";
		}
		if (typeCompare.equalsIgnoreCase("like")) {
			display = "[ " + Translate.translate(ZKEnv.getEnv(), null, "Contains") + "(" + title + "," + vl + ")]";
		} else {
			display = "[ " + title + " " + typeCompare + "" + vl + "]";
		}
		return display;
	}

	@Override
	public void onChangedValue(VEditor editor) {
		Object valueSearch = editor.getValue();
		lbdataSearch.setValue(getDetailString(valueSearch));
		if (typeCompare.equalsIgnoreCase("like")) {
			params.put(fieldName, editor.getValue() == null ? null : "%" + editor.getValue() + "%");
		} else {
			params.put(fieldName, editor.getValue());
		}
		hldetail.setVisible(editor.getValue() != null);
		handelSearch.executeSearch(whereClause, params);

	}

	public VEditor getVeditor() {
		if (veditor != null) {
			veditor.hideLabel();
			// for (Component comp : veditor.getChildren()) {
			// if (!comp.equals(veditor.component)) {
			// comp.setParent(null);
			// }
			// }
		}
		return veditor;
	}

	public void setVeditor(VEditor veditor) {
		this.veditor = veditor;
	}

	public String getTypeCompare() {
		return typeCompare;
	}

	public void setTypeCompare(String typeCompare) {
		this.typeCompare = typeCompare;
	}

	public Hlayout getHldetail() {
		return hldetail;
	}

	public void setHldetail(Hlayout hldetail) {
		this.hldetail = hldetail;
	}

	public Label getLbdataSearch() {
		return lbdataSearch;
	}

	public void setLbdataSearch(Label lbdataSearch) {
		this.lbdataSearch = lbdataSearch;
	}

	public HandlerSearchEvent getHandelSearch() {
		return handelSearch;
	}

	public void setHandelSearch(HandlerSearchEvent handelSearch) {
		this.handelSearch = handelSearch;
	}

	public String getWhereClause() {
		return whereClause;
	}

	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public Hlayout getDetailSearch() {
		return hldetail;
	}

}
