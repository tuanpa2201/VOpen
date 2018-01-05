package base.webservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.apache.commons.net.ntp.TimeStamp;

import base.controller.VController;
import base.controller.VEnv;

public class RPCHelper {
	String modelName;
	String methodName;
	Object[] params;
	VEnv env;
	public static RPCHelper parseJson(JsonObject json) {
		RPCHelper rpc = new RPCHelper();
		try {
			rpc.modelName = json.getString("model");
			rpc.methodName = json.getString("method");
			JsonArray jsonArray = json.getJsonArray("params");
			List<Object> lstParams = new ArrayList<>();
			for (int i = 0; i < jsonArray.size(); i++) {
				JsonObject paramJson = jsonArray.getJsonObject(i);
				String dtype = paramJson.getString("dtype");
				String valueString = paramJson.getString("value");
				if (dtype.equals("String")) {
					lstParams.add(valueString);
				}
				else if (dtype.equals("Integer")) {
					lstParams.add(Integer.parseInt(valueString));
				}
				else if (dtype.equals("BigDecimal")) {
					lstParams.add(new BigDecimal(valueString));
				}
				else if (dtype.equals("Date")) {
					lstParams.add(new TimeStamp(valueString));
				}
				//TODO: Tuanpa - add support datatype here
			}
			rpc.params = new Object[lstParams.size()];
			rpc.params = lstParams.toArray(rpc.params);
		}
		catch (Exception e) {
			rpc = null;
		}
		return rpc;
	}
	
	public Object execute() {
		VController controller = env.get(modelName);
		Object retVal = controller.execute(methodName, params);
		return retVal;
	}
}
