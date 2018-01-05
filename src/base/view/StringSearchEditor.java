package base.view;

import base.model.VField;
import base.view.editor.StringEditor;

public class StringSearchEditor extends StringEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StringSearchEditor(VField field) {
		super(field);
	}

	@Override
	public void onChangedValue() {
		onChangeListener.onChangedValue(this);
	}
}
