/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Jul 27, 2016
* Author: tuanpa
*
*/
package base.view;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.xpath.XPathEvaluator;

import base.controller.VController;
import base.model.VObject;
import base.util.StringAppUtils;
import base.util.ZKEnv;

public class VViewDefine implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1519562141857234343L;
	private static HashMap<String, VViewDefine> mapView = new HashMap<>();
	public String model;
	public String id;
	public Element xmlNode;
	public int priority = 10;
	public VViewType type;
	public String moduleId;
	
	private static void inheritView(String viewId, Element xmlNode) {
		VViewDefine viewDef = mapView.get(viewId);
		if (viewDef == null) {
			return;
		}
		Node childNode = xmlNode.getFirstChild();
		while (childNode != null) {
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element node = (Element) childNode;
				if (node.getNodeName().equalsIgnoreCase("xpath")) {
					String path = node.getAttribute("path");
					Node editNode = searchNode(viewDef.xmlNode, path);
					if (editNode == null)
						continue;
					String action = node.getAttribute("action");
					if (action.equalsIgnoreCase("append")) {
						Node tmp = childNode.getFirstChild();
						Document doc = editNode.getOwnerDocument();
						while (tmp != null) {
							Node tmp2 = doc.importNode(tmp, true);
							editNode.appendChild(tmp2);
							tmp = tmp.getNextSibling();
						}
					}
					else if(action.equalsIgnoreCase("delete")) {
						Node tmp = editNode.getFirstChild();
						while (tmp != null) {
							editNode.removeChild(tmp);
							tmp = editNode.getFirstChild();
						}
					}
					else if(action.equalsIgnoreCase("replace")) {
						Node tmp = editNode.getFirstChild();
						while (tmp != null) {
							editNode.removeChild(tmp);
							tmp = editNode.getFirstChild();
						}
						
						tmp = childNode.getFirstChild();
						Document doc = editNode.getOwnerDocument();
						while (tmp != null) {
							Node tmp2 = doc.importNode(tmp, true);
							editNode.appendChild(tmp2);
							tmp = tmp.getNextSibling();
						}
					}
					else if(action.equalsIgnoreCase("insertbefore")) {
						Node tmp1 = editNode.getFirstChild();
						Node tmp = childNode.getFirstChild();
						Document doc = editNode.getOwnerDocument();
						while (tmp != null) {
							Node tmp2 = doc.importNode(tmp, true);
							editNode.insertBefore(tmp1, tmp2);
							tmp = tmp.getNextSibling();
						}
					}
					nodeToString(editNode);
					nodeToString(viewDef.xmlNode);
				}
			}
			childNode = childNode.getNextSibling();
		}
	}
	
	private static Node searchNode(Element xmlNode, String path) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node retVal = null;
		try {
			retVal = (Node) xpath.evaluate(path, xmlNode, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return retVal;
	}
	
	public static void addView(String moduleId, Element xmlNode) {
		if (StringAppUtils.isNotEmpty(xmlNode.getAttribute("inherit_id"))) {
			String viewId = xmlNode.getAttribute("inherit_id");
			inheritView(viewId, xmlNode);
			return;
		}
		if (xmlNode.getAttribute("model") == null 
				|| xmlNode.getAttribute("type") == null
				|| xmlNode.getAttribute("id") == null) {
			System.out.println("View is invalid: " + xmlNode.getNodeValue());
			return;
		}
		VViewDefine view = new VViewDefine();
		view.moduleId = moduleId;
		view.xmlNode = xmlNode;
		view.model = xmlNode.getAttribute("model");
		view.id = xmlNode.getAttribute("id");
		view.type = VViewType.getValueOf(xmlNode.getAttribute("type"));
		if (StringAppUtils.isInteger(xmlNode.getAttribute("priority"))) {
			view.priority = Integer.parseInt(xmlNode.getAttribute("priority"));
		}
		if (mapView.get(view.id) == null)
			mapView.put(view.id, view);
	}
	
	public static VViewDefine getView(String viewId) {
		return mapView.get(viewId);
	}
	
	public static VViewDefine getViewForModel(String model, VViewType type) {
		VViewDefine retView = null;
		for (VViewDefine view : mapView.values()) {
			if (view.model.equalsIgnoreCase(model) && view.type.getValue().equals(type.getValue())) {
				if (retView == null || retView.priority < view.priority) {
					retView = view;
				}
			}
		}
		return retView;
	}
	
	private static String nodeToString(Node node) {
		StringWriter sw = new StringWriter();
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new DOMSource(node), new StreamResult(sw));
		} catch (TransformerException te) {
		}
		System.out.println(sw.toString());
		return sw.toString();
	}
}
