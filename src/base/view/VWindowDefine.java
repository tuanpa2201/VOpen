/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Jul 27, 2016
* Author: tuanpa
*
*/
package base.view;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import base.controller.VController;
import base.controller.VEnv;
import base.model.VField;
import base.util.Filter;
import base.util.StringConvertUtil;

public class VWindowDefine implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5571838965470828028L;
	
	private static HashMap<String, VWindowDefine> mapWindow = new HashMap<>();
	
	public String id;
	public String title;
	public String model;
	public Filter filter = new Filter();
	public String windowId;
	public String sortBy;
	public String strViews;
	public String moduleId;
	public Node defaults;
	
	public Map<String, Object> getDefaults() {
		Map<String, Object> retVal = new HashMap<>();
		
		if (defaults != null) {
			Node childNode = defaults.getFirstChild();
			VController controller = VEnv.sudo().get(model);
			while (childNode != null) {
				if (childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equalsIgnoreCase("field")) {
					Element nNode = (Element)childNode;
					String fieldName = nNode.getAttribute("name");
					String value = nNode.getFirstChild().getNodeValue();
					VField field = controller.getField(fieldName);
					Object obj = StringConvertUtil.toObject(field, value);
					retVal.put(fieldName, obj);
				}
				childNode = childNode.getNextSibling();
			}
		}
		return retVal;
	}
	
	public static void addWindow(String moduleId, Element xmlNode) {
		//Check view must have some attr
		if (xmlNode.getAttribute("model") == null 
				|| xmlNode.getAttribute("id") == null) {
			System.out.println("Window is invalid: " + xmlNode.getNodeValue());
			return;
		}
		Node node = xmlNode.getFirstChild();
		Filter filter = new Filter();
		String sortBy = null;
		String strViews = null;
		VWindowDefine window = new VWindowDefine();
		window.moduleId = moduleId;
		window.model = xmlNode.getAttribute("model");
		window.id = xmlNode.getAttribute("id");
		
		while (node != null) {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getNodeName().equalsIgnoreCase("filter") && node.getFirstChild() != null) {
					filter = Filter.createFilter((Element)node);
				}
				else if (node.getNodeName().equalsIgnoreCase("sort_by") && node.getFirstChild() != null) {
					sortBy = node.getFirstChild().getNodeValue();
				}
				else if (node.getNodeName().equalsIgnoreCase("views") && node.getFirstChild() != null) {
					strViews = node.getFirstChild().getNodeValue();
				}
				else if (node.getNodeName().equalsIgnoreCase("default") && node.getFirstChild() != null) {
					window.defaults = node;
				}
			}
			node = node.getNextSibling();
		}
		window.strViews = strViews;
		window.filter = filter;
		window.strViews = strViews;
		window.sortBy = sortBy;
		window.title = window.model;
		if (xmlNode.getAttribute("title") != null) {
			window.title = xmlNode.getAttribute("title");
		}
		mapWindow.put(window.id, window);
	}
	
	public static VWindowDefine getWindow(String windowId) {
		return mapWindow.get(windowId);
	}
}
