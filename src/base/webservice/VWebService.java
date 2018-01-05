package base.webservice;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import base.model.VObject;
import base.util.StringAppUtils;

@Path("/VWebService")
public class VWebService {
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/test")
	public Tracking testWS(){
		Tracking temp = new Tracking();
		temp.name = "VOpen";
		return temp;
	}
	
	class Tracking{
		public String name;
	}
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/processData/map/{mapParams}")
	public JSonContent processData(@PathParam("mapParams") String mapParams) throws UnsupportedEncodingException{
		JSonContent json = new JSonContent();
		json = getData(mapParams);
		return json;
	}
	
	protected JSonContent getData(String mapParams){
		JSonContent jsonContent = new JSonContent();
		List<VObject> lstContent = new ArrayList<VObject>();
		try {
			if(StringAppUtils.isNotEmpty(mapParams)){
				JSONObject jObject  = new JSONObject(mapParams);
				String model = jObject.getString("model");
				String modelName = model.replace("student.model.", "");
				String method = jObject.getString("method");
				String params ="";
				if(!jObject.isNull("params")){
					params = jObject.getString("params");
				}
				VObject obj = null;
				JSONObject jObjectParams = null;
				HashMap<String, Object> map = new HashMap<String, Object>();
				if(StringAppUtils.isNotBlank(params)){
					map = new HashMap<String, Object>();
					String paramsEncode = "";
					try {
						paramsEncode = URLDecoder.decode(params,"UTF-8");
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
					
					Class<?> clasModel = Class.forName(model);
					try {
						obj = (VObject) clasModel.newInstance();
					} catch (InstantiationException e) {
						e.printStackTrace();
					}
					jObjectParams = new JSONObject(paramsEncode);
			        Iterator<?> keys = jObjectParams.keys();
			        while( keys.hasNext() ){
			            String key = (String)keys.next();
			            String value = jObjectParams.getString(key); 
			            map.put(key, value);
			            try {
			            	obj.setValue(key, value);
						} catch (Exception e) {
							e.printStackTrace();
						}
			        }
			        
				}
				if(StringAppUtils.equals(method, "searchData")){
					String sql = "from " + modelName;
					List<VObject> lst = null;//(List<VObject>) VObject.query(sql, 1, -1, map);
					jsonContent.setStatus(1);
			        jsonContent.setLstContent(lst);
			        return jsonContent;
				}
				if(StringAppUtils.equals(method, "insert") || StringAppUtils.equals(method, "update")){
			        try {
						obj.save();
					} catch (Exception e) {
						e.printStackTrace();
					}
			        jsonContent.setStatus(1);
			        jsonContent.setLstContent(lstContent);
			        return jsonContent;
				}else if(StringAppUtils.equals(method, "delete")){
//					obj.delete();
//					VObject.delete(modelName, jObjectParams.getString("id"));
				}else if(StringAppUtils.equals(method, "searchData")){
					String sql = "from" + modelName;
					List<VObject> lst = null;//(List<VObject>) VObject.query(sql, 1, -1, map);
					jsonContent.setStatus(-1);
			        jsonContent.setLstContent(lst);
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch(SecurityException
				| IllegalArgumentException | IllegalAccessException e){
			e.printStackTrace();
		}
		jsonContent.setStatus(-1);
        jsonContent.setLstContent(lstContent);
		return jsonContent;
		
	}
	
	public class JSonContent{
		private int status;
		private List<VObject> lstContent;
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		public List<VObject> getLstContent() {
			return lstContent;
		}
		public void setLstContent(List<VObject> lstContent) {
			this.lstContent = lstContent;
		}
	} 
}
