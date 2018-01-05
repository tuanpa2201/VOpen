package base.webservice;

import java.util.Map;

public class ObjectParams {
		private String model;
		private String method;
		private Map<String, Object> mapParams;
		public String getModel() {
			return model;
		}
		public void setModel(String model) {
			this.model = model;
		}
		public String getMethod() {
			return method;
		}
		public void setMethod(String method) {
			this.method = method;
		}
		public Map<String, Object> getMapParams() {
			return mapParams;
		}
		public void setMapParams(Map<String, Object> mapParams) {
			this.mapParams = mapParams;
		}
}
