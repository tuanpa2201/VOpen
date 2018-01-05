/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Sep 8, 2016
* Author: tuanpa
*
*/
package base.view.editor;

import base.controller.VActionResponse;
import base.model.VObject;

public interface IFormViewPopupDelegate {
	public VActionResponse onSave(VObject vobj);
}
