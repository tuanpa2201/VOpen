package base.webservice;

import java.io.InputStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.json.JSONObject;

import base.controller.VEnv;
import base.util.StringConvertUtil;
import base.view.ClientInfo;

@Path("/terminal")
public class VTerminal {
	static int LOGIN_SUCCESS = 1;
	static int LOGIN_FAIL = 0;

	@POST
	@Path("/login/{username}/{password}")
	public String login(@PathParam("username") String username, @PathParam("password") String password) {
		VEnv env = VEnv.login(username, password);
		JsonObjectBuilder jsonBuilder = Json.createObjectBuilder().add("status",
				env != null ? LOGIN_SUCCESS : LOGIN_FAIL);
		if (env != null) {
			jsonBuilder.add("sessionKey", env.getSessionKey());
			ClientInfo clientInfo = new ClientInfo();
			clientInfo.setTerminalLogin(true);
			env.setClientInfo(clientInfo);
		} else {
			jsonBuilder.add("message", "Bad username/password");
		}
		String retVal = jsonBuilder.build().toString();
		return (new JSONObject(retVal)).toString(4);

	}

	@POST
	@Path("/logout/{sessionKey}")
	public String logout(@PathParam("sessionKey") String sessionKey) {
		VEnv.logout(sessionKey);
		return (new JSONObject("{status: 1}")).toString(4);
	}

	@POST
	@Path("/rpc/{sessionKey}")
	public String rpc(@PathParam("sessionKey") String sessionKey, InputStream incomingData) {
		VEnv env = VEnv.getSession(sessionKey);
		JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
		if (env == null) {
			jsonObjBuilder.add("status", 0);
			jsonObjBuilder.add("message", "Invalid session key");
			return jsonObjBuilder.build().toString();
		}
		JsonObject jsonObj;
		JsonReader reader = Json.createReader(incomingData);
		jsonObj = reader.readObject();
		System.out.println(jsonObj.toString());

		RPCHelper rpcHelper = RPCHelper.parseJson(jsonObj);
		rpcHelper.env = VEnv.getSession(sessionKey);
		Object retVal = null;
		if (rpcHelper != null) {
			retVal = rpcHelper.execute();
		}
		if (retVal != null) {
			System.out.println(retVal.toString());
		}

		jsonObjBuilder.add("status", 1);
		jsonObjBuilder.add("return", StringConvertUtil.toJsonString(retVal, null));
		return jsonObjBuilder.build().toString();
	}
}