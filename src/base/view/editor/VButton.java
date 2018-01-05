/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 22, 2016
* Author: tuanpa
*
*/
package base.view.editor;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;

public class VButton extends Button {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6086784151147521838L;

	private String type; // action or function
	private String name;

	public VButton(String type, String  name, String label) {
		
		this.type = type;
		this.name = name;
		this.setLabel(label);
		this.setSclass("vbutton");
	}
}
