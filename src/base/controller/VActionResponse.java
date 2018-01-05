/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Sep 14, 2016
* Author: tuanpa
*
*/
package base.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import base.model.VObject;
import base.util.Filter;

public class VActionResponse {
	public static int ACTION_TYPE_SAVE = 1;
	public static int ACTION_TYPE_DELETE = 2;
	public int actionType = 1;
	public Boolean status = Boolean.TRUE;
	public String typeresponse = "info";
	public List<String> messages = new ArrayList<>();
	public Integer id;
	public VObject obj;
	public Map<String, Object> values = new HashMap<>();
	public Map<String, Boolean> readonlys = new HashMap<>();
	public Map<String, Filter> filters = new HashMap<>();
	public Map<String, Boolean> displays = new HashMap<>();
	public Map<String, Boolean> nullables = new HashMap<>();
}
