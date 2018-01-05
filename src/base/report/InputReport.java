package base.report;

import java.util.Date;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;

import base.model.VObject;

public class InputReport extends Div {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Object value;
	private String title;
	protected String paramName;
	public Component component;
	protected IReportEListener onChangeListener;

	public Object getValue() {
		return value;
	}

	public Object getValueConverted() {

		if (this instanceof DateInput) {

			return value;
		} else if (this instanceof DateTimeInput) {

			return value;
		} else if (this instanceof SelectionModelInput) {
			if (value == null)
				return null;
			VObject obj = (VObject) value;
			return obj.getId();
		} else if (this instanceof SelectionInput) {

			return value;
		}

		return value;
	}

	public boolean validateInput(Object value) throws Exception {
		// if (value == null) {
		// throw new Exception("Not Null");
		// }
		return true;
	}

	public void setValue(Object value) {
		try {
			validateInput(value);
		} catch (Exception e) {
			Clients.showNotification(e.getMessage(), Clients.NOTIFICATION_TYPE_ERROR, this.component, "end_center", 0);
		}
		this.value = value;
		onChangeListener.onChangedValue(this);
	}

	public static InputReport Date(String title, String paramName, IReportEListener listener, Date dateInit) {
		DateInput dateTime = new DateInput();
		dateTime.setParamName(paramName);
		dateTime.setOnChangeListener(listener);
		dateTime.setValue(dateInit);
		dateTime.setTitle(title);
		return dateTime;
	}

	public static InputReport DateTime(String title, String paramName, IReportEListener listener, Date dateInit) {
		DateTimeInput dateTime = new DateTimeInput();
		dateTime.setParamName(paramName);
		dateTime.setOnChangeListener(listener);
		// dateTime.onChangeListener = listener;
		dateTime.setValue(dateInit);
		dateTime.setTitle(title);
		return dateTime;
	}

	public static ReferenceInput ReferenceSelectionInput(String title, String paramName, String modelName,
			IReportEListener listener) {
		SelectionModelInput choosenInput = new SelectionModelInput(modelName);
		choosenInput.setParamName(paramName);
		choosenInput.setTitle(title);
		// choosenInput.onChangeListener = listener;
		choosenInput.setOnChangeListener(listener);
		return choosenInput;
	}

	/**
	 * 
	 * @param title
	 * @param paramName
	 * @param modelName
	 * @param listener
	 * @return
	 */

	public static ReferenceInput selectionModels(String title, String paramName, String modelName, String fieldShow,
			IReportEListener listener) {
		SelectionsModelInput choosenInput = new SelectionsModelInput(modelName);
		choosenInput.setParamName(paramName);
		choosenInput.setTitle(title);
		choosenInput.setOnChangeListener(listener);
		// choosenInput.onChangeListener = listener;
		choosenInput.setFieldDisplay(fieldShow);
		return choosenInput;
	}

	public static ReferenceInput ReferenceComboInput(String title, String paramName, String model,
			IReportEListener listener) {
		// TODO Auto-generated method stub
		SelectionModelInput comboInput = new SelectionModelInput(model);
		comboInput.setParamName(paramName);
		comboInput.setTitle(title);
		// comboInput.onChangeListener = listener;
		comboInput.setOnChangeListener(listener);
		return comboInput;
	}

	public static ReferenceInput selectionModel(String title, String paramName, String modelName,
			IReportEListener listener) {
		SelectionModelInput modelInput = new SelectionModelInput(modelName);
		modelInput.setParamName(paramName);
		modelInput.setTitle(title);
		// modelInput.onChangeListener = listener;
		modelInput.setOnChangeListener(listener);
		return modelInput;
	}

	public static InputReport SelectionInput(String title, String paramName, IReportEListener listener,
			Object... values) {
		SelectionInput selectionInput = new SelectionInput();
		selectionInput.setParamName(paramName);
		selectionInput.setTitle(title);
		// selectionInput.onChangeListener = listener;
		selectionInput.setOnChangeListener(listener);
		selectionInput.setItems(values);
		return selectionInput;

	}

	public void setOnChangeListener(IReportEListener onChangeListener) {
		this.onChangeListener = onChangeListener;
		this.onChangeListener.onChangedValue(this);
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
