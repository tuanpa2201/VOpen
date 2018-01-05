/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 15, 2016
* Author: tuanpa
*
*/
package base.model;

import java.util.Map;

import base.exception.VException;

public class IntegerField extends VField {
	public Integer minvalue;
	public Integer maxvalue;

	public IntegerField() {
	}

	@Override
	public void parseOptions(Map<String, Object> options) {
		super.parseOptions(options);
		if (options.get("minvalue") != null) {
			this.minvalue = (Integer) options.get("minvalue");
		}
		if (options.get("maxvalue") != null) {
			this.maxvalue = (Integer) options.get("maxvalue");
		}
	}

	@Override
	public boolean validationField(Object value) throws Exception {
		    super.validationField(value);
			if ((this.minvalue != null && value != null && (Integer) value < this.minvalue)) {
				throw new Exception(label + VException.BOUND_EXCEPTION);
			}
			if ((this.maxvalue != null && value != null && (Integer) value > this.maxvalue)) {
				throw new Exception(label + VException.BOUND_EXCEPTION);
			}
		return true;
	}
}
