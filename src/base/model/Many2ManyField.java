/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 18, 2016
* Author: tuanpa
*
*/
package base.model;

import org.hibernate.annotations.CascadeType;

public class Many2ManyField extends VField {
	public String friendModel;
	public String joinField;
	public String friendField;
	public CascadeType cascade;
	public String joinTable;
}
