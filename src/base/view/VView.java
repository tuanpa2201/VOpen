/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Jul 27, 2016
* Author: tuanpa
*
*/
package base.view;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlNativeComponent;

public abstract class VView extends HtmlNativeComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9089601797173058672L;
	protected VViewDefine viewDef;
	public VWindow window;
	public VView(VViewDefine viewDef, VWindow window) {
		super();
		this.window = window;
		this.viewDef = viewDef;
		initUI();
		parseXML();
		setData();
	}
	
	protected void initUI() {
		this.setTag("div");
		this.setDynamicProperty("class", "container-fluid");
		this.setDynamicProperty("style", "padding-left: 0px; padding-right: 0px");
		getLeftHeader();
		getCenterHeader();
		getRightHeader();
	}
	abstract public Component getLeftHeader();
	abstract public Component getCenterHeader();
	abstract public Component getRightHeader();
	abstract public Component getTitleComponent();
	abstract public void parseXML();
	abstract public String getViewType();
	abstract public void setData();
	
	public static VView getView(VViewDefine viewDef, VWindow window) {
		VView view = null;
		if (viewDef.type.getValue().equals(VViewType.LIST.getValue())) {
			view = new VListView(viewDef, window);
		}
		else if (viewDef.type.getValue().equals(VViewType.FORM.getValue())) {
			view = new VFormView(viewDef, window);
		}
		return view;
	}

	public void setWindow(VWindow window) {
		this.window = window;
	}
}
