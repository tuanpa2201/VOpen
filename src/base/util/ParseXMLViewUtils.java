/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 1, 2016
* Author: tuanpa
*
*/
package base.util;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import base.view.VViewDefine;
import base.view.VWindowDefine;

public class ParseXMLViewUtils {
	public static void parseXMLFile(String moduleId, String uri) {
		try {
			InputStream xmlFile = Thread.currentThread().getContextClassLoader().getResourceAsStream(uri);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document document = dBuilder.parse(xmlFile);
			Node rootNode = document.getFirstChild();
			while (rootNode != null && !rootNode.getNodeName().equalsIgnoreCase("vopen")) {
				rootNode = rootNode.getNextSibling();
			}
			if (rootNode.getNodeName().equalsIgnoreCase("vopen")) {
				Node node = rootNode.getFirstChild();
				while (node != null) {
					if (node.getNodeName().equalsIgnoreCase("view") && node.getNodeType() == Node.ELEMENT_NODE) {
						Element xmlNode = (Element) node;
						VViewDefine.addView(moduleId, xmlNode);
					}
					else if (node.getNodeName().equalsIgnoreCase("window") && node.getNodeType() == Node.ELEMENT_NODE) {
						Element xmlNode = (Element) node;
						VWindowDefine.addWindow(moduleId, xmlNode);
					} 
					node = node.getNextSibling();
				}
			}
			
		} catch (Exception e) {
			System.out.println("URI:" + uri);
			e.printStackTrace();
		}
	}
}
