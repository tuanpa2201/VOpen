package base.view.editor;

import base.controller.VController;
import base.model.Many2OneField;
import base.model.VField;
import base.util.Filter;

public abstract class ReferenceEditor extends VEditor {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -559571151338013561L;
	public VController controller = null;
	public Filter filter;

	public ReferenceEditor(VField field) {
		super(field);
		filter = new Filter();
		filter.hqlWhereClause = "isActive = :isActive_to_check";
		filter.params.put("isActive_to_check", true);
		if (field instanceof Many2OneField) {
			Many2OneField mField = (Many2OneField)field;
			if (mField.filter != null) {
				filter = mField.filter;
			}
		}
	}
	
	abstract public void reset();
}
