/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 18, 2016
* Author: tuanpa
*
*/
package base.model;

import java.util.Map;

public class One2ManyField extends VField {
	public static String ON_DELETE_CASCADE = "cascade";
	public static String ON_DELETE_SET_NULL = "setnull";
	
	public String childModel;
	public String joinField;
	public String orderby = "id";
	public String ondelete;

	@Override
	public void parseOptions(Map<String, Object> options) {
		super.parseOptions(options);
		if (options.get("orderby") != null) {
			this.orderby = options.get("orderby").toString();
		}
		if (options.get("ondelete") != null) {
			this.ondelete = options.get("ondelete").toString();
		}
	}
}
