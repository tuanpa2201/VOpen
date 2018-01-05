package base.view;

import org.zkoss.zul.Div;

import base.controller.VController;
import base.util.Filter;

public abstract class VListHeaderFooter extends Div {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4430052057294308573L;
	public VController controller;
	public Filter filter;
  public VListView vListView;
	abstract public void initUI();
}
