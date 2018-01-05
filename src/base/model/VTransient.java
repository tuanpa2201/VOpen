/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Sep 28, 2016
* Author: tuanpa
*
*/
package base.model;

import java.util.List;
import java.util.Map;

import base.controller.VActionResponse;

abstract public class VTransient extends VModel {

	private static final long serialVersionUID = -1031278773447303922L;

	@Override
	public VActionResponse create(Map<String, Object> values) {
		VActionResponse response = new VActionResponse();
		response.status = false;
		response.messages.add("Can not create transient object");
		return response;
	}

	@Override
	public Map<Integer, VObject> browse(List<Integer> ids) {
		return super.browse(ids);
	}

	@Override
	public VObject browse(Integer id) {
		return super.browse(id);
	}

	@Override
	public VActionResponse delete(List<Integer> ids) {
		return new VActionResponse();
	}
	
	abstract public String viewHqlQuerry();
}
