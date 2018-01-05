/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Sep 12, 2016
* Author: tuanpa
*
*/
package base.view;

import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;
import org.zkoss.zul.Script;

public class IndexSetup extends Div {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6127374662563516055L;
	Div div;
	public IndexSetup(Div div) {
		this.div = div;
	}
	public void onAfterLoaded() {
		Script script = new Script();
		script.setSrc("js/app.min.js");
		script.setParent(div);
		this.setParent(null);
	}
	
	public void onAfterChangeModule() {
		Clients.evalJavaScript("buildJarvisMenu();");
		this.setParent(null);
	}
	
	public void onAfterInitModuleShortcut() {
		Clients.evalJavaScript("setTimeout(function() {$('#show-shortcut').trigger('click');}, 100);");
		this.setParent(null);
	}
}
