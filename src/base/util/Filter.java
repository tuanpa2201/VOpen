package base.util;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class Filter {
	
	public Filter(String hqlWhereClause, Map<String, Object> params) {
		super();
		this.hqlWhereClause = hqlWhereClause;
		this.params = new HashMap<>();
		this.params.putAll(params);
	}
	public String hqlWhereClause = "";
	public Map<String, Object> params = new HashMap<>();
	
	public Filter() {
		
	}
	public static Filter createFilter(Element filterNode) {
		Filter filter = new Filter();
		Node node = filterNode.getFirstChild();
		while (node != null) {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getNodeName().equalsIgnoreCase("hql") && node.getFirstChild() != null) {
					filter.hqlWhereClause = node.getFirstChild().getNodeValue();
				}
				else if (node.getNodeName().equalsIgnoreCase("param") && node.getFirstChild() != null) {
					Element eNode = (Element)node;
					String type = eNode.getAttribute("type");
					String varName = eNode.getAttribute("name");
					String value = eNode.getFirstChild().getNodeValue();
					Object obj = StringConvertUtil.toObject(type, value);
					filter.params.put(varName, obj);
				}
			}
			node = node.getNextSibling();
		}
		return filter;
	}
	
	public static Filter createFilter(String xml) {
		Filter filter = new Filter();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document document = dBuilder.parse(new InputSource(new StringReader(xml)));
			Element node = (Element) document.getFirstChild();
			filter = createFilter(node);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filter;
	}
	
	public Filter clone() {
		Filter clone = new Filter();
		clone.hqlWhereClause = this.hqlWhereClause;
		clone.params.putAll(this.params);
		return clone;
	}
}
