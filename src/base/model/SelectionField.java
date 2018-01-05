/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 18, 2016
* Author: tuanpa
*
*/
package base.model;

import java.util.Map;

public class SelectionField extends VField {
	public Map<String, String> values;
	public SelectionField(Map<String, String> values) {
		this.values = values;
	}
}
