package modules.student.view;

import org.zkoss.zul.Label;

import base.view.VListHeaderFooter;

public class StudentSummary extends VListHeaderFooter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8491435884451642977L;
	@Override
	public void initUI() {
		this.appendChild(new Label("Total student: " + this.controller.count(filter)));
	}
}
