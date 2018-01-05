package base.view;

import java.io.StringWriter;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlNativeComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Vlayout;

import base.controller.VController;
import base.controller.VEnv;
import base.model.VField;
import base.util.StringAppUtils;
import base.util.StringUtils;
import base.util.Translate;
import base.util.ZKEnv;
import base.view.editor.ButtonEditor;
import base.view.editor.IEditorListenner;
import base.view.editor.VButton;
import base.view.editor.VEditor;

public class ParseFormViewUtil {
	public VController controller;
	public IEditorListenner onChangeListener;
	Map<String, VEditor> mapEditor;
	Map<VEditor, String> mapEditorInvert;
	public String moduleId;
	private VFormView formView;
	
	public ParseFormViewUtil(VController controller, IEditorListenner onChangeListener, Map<String, VEditor> mapEditor,
			Map<VEditor, String> mapEditorInvert, String moduleId, VFormView formView) {
		super();
		this.controller = controller;
		this.onChangeListener = onChangeListener;
		this.mapEditor = mapEditor;
		this.mapEditorInvert = mapEditorInvert;
		this.moduleId = moduleId;
		this.formView = formView;
	}

	public void recursiveParseNode(Element node, Component parentComponent) {
		if (node.getNodeName().equalsIgnoreCase("view")) {
			int columnNum = 1;
			if (StringAppUtils.isInteger(node.getAttribute("columns"))) {
				columnNum = Integer.parseInt(node.getAttribute("columns"));
			}
			HtmlNativeComponent form = new HtmlNativeComponent("form");
			form.setDynamicProperty("class", "container-fluid smart-form");
			form.setDynamicProperty("style", "padding-left: 0px; padding-right: 0px");
			form.setParent(parentComponent);
			form.setAttribute("columns", columnNum);
			form.setAttribute("multiple", (int) (12 / columnNum));

			Node childNode = node.getFirstChild();
			parseChildNode(childNode, form);
		} else if (node.getNodeName().equalsIgnoreCase("group") && node.getNodeType() == Node.ELEMENT_NODE) {
			String groupTitle = node.getAttribute("title");
			int colspan = 1;
			if (StringAppUtils.isInteger(node.getAttribute("colspan"))) {
				colspan = Integer.parseInt(node.getAttribute("colspan"));
			}
			Vlayout groupDiv = new Vlayout();
//			groupDiv.setWidth("96%");
			groupDiv.setStyle("height: auto!important;");
			groupDiv.setAttribute("colspan", colspan);
			appendGrid(parentComponent, groupDiv);

			if (groupTitle != null) {
				HtmlNativeComponent n = new HtmlNativeComponent("h4", Translate.translate(ZKEnv.getEnv(), moduleId, groupTitle), null);
				n.setParent(groupDiv);
			}

			Node childNode = node.getFirstChild();
			parseChildNode(childNode, groupDiv);
		} else if (node.getNodeName().equalsIgnoreCase("field") && node.getNodeType() == Node.ELEMENT_NODE) {
			try {
				String fieldName = node.getAttribute("name");
				String editorClass = node.getAttribute("editor");
				String style = node.getAttribute("style");
				VField field = controller.getField(fieldName);
				
				String viewId = node.getAttribute("view_id");
				
				if (StringUtils.isNotEmpty(editorClass)) {
					field.editorClass = editorClass;
				}
				
				if (StringUtils.isNotEmpty(viewId)) {
					field.viewId = viewId;
				}
				if (field == null)
					return;
				if (node.getAttribute("readonly") != null) {
					Boolean readonlye = new Boolean(node.getAttribute("readonly"));
					field.isReadOnly = readonlye;
				}
				field.moduleId = moduleId;
				VEditor editor = VEditor.getEditor(field, node);
				//Tuanpa: neu chi co 1 cty => ko hien thi
				if (fieldName.equalsIgnoreCase("company")) {
					VEnv env = ZKEnv.getEnv();
					if (env != null &&  env.getCompanies().size() == 1) {
						editor.setDisplay(false);
					}
				}
				editor.setViewStyle(style);

				if (editor != null) {
					if (editor instanceof ButtonEditor) {
						((ButtonEditor)editor).window = formView.window;
						((ButtonEditor)editor).controller = controller;
					}
					editor.onChangeListener = onChangeListener;
					editor.setParent(parentComponent);
					mapEditor.put(fieldName, editor);
					mapEditorInvert.put(editor, fieldName);
				}
			} catch (Exception e) {
				System.out.println(e.toString());
			}

		}else if (node.getNodeName().equalsIgnoreCase("button") && node.getNodeType() == Node.ELEMENT_NODE) {
			try {
				String type = node.getAttribute("type");
				String buttonName = node.getAttribute("name");
				String label = Translate.translate(ZKEnv.getEnv(), moduleId, node.getAttribute("label"));				
				String style = node.getAttribute("style");
				
				Label lb = new Label("dump");
				lb.setStyle("color:transparent;");
				lb.setParent(parentComponent);
				
				VButton button = new VButton(type, buttonName, label);
				button.setStyle(style);
				button.setParent(parentComponent);
				button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
					@Override
					public void onEvent(Event arg0) throws Exception {
						if("function".equals(type)) {
							if(formView != null) {
								formView.window.save();
								controller.execute(buttonName, formView.window.currentId);
								formView.window.refresh();
							}	
						}
					}
				});
			} catch (Exception e) {
				System.out.println(e.toString());
			}

		} else if (node.getNodeName().equalsIgnoreCase("tabbox") && node.getNodeType() == Node.ELEMENT_NODE) {
			int colspan = 1;
			if (StringAppUtils.isInteger(node.getAttribute("colspan"))) {
				colspan = Integer.parseInt(node.getAttribute("colspan"));
			}
			Tabbox tabbox = new Tabbox();
			tabbox.setAttribute("colspan", colspan);
			tabbox.setStyle("margin-top: 5px; margin-bottom: 5px;");
			appendGrid(parentComponent, tabbox);
			Tabs tabs = new Tabs();
			tabs.setParent(tabbox);
			Tabpanels tabpanels = new Tabpanels();
			tabpanels.setParent(tabbox);

			Node childNode = node.getFirstChild();
			parseChildNode(childNode, tabbox);
		} else if (node.getNodeName().equalsIgnoreCase("tab") && node.getNodeType() == Node.ELEMENT_NODE) {
			if (parentComponent instanceof Tabbox) {
				Tabbox tabbox = (Tabbox) parentComponent;
				String groupTitle = node.getAttribute("title");
				Tab tab = new Tab(groupTitle == null? "": Translate.translate(ZKEnv.getEnv(), moduleId, groupTitle));
				tab.setParent(tabbox.getTabs());
				Tabpanel tabpanel = new Tabpanel();
				tabpanel.setParent(tabbox.getTabpanels());
				
				int columnNum = 1;
				if (StringAppUtils.isInteger(node.getAttribute("columns"))) {
					columnNum = Integer.parseInt(node.getAttribute("columns"));
				}
				HtmlNativeComponent div = new HtmlNativeComponent("div");
				div.setDynamicProperty("class", "container-fluid");
				div.setDynamicProperty("style", "padding: 5px;");
				div.setParent(tabpanel);
				div.setAttribute("columns", columnNum);
				div.setAttribute("multiple", (int) (12 / columnNum));

				Node childNode = node.getFirstChild();
				parseChildNode(childNode, div);
			}
		} else if (node.getNodeType() == Node.ELEMENT_NODE) {
			HtmlNativeComponent n = new HtmlNativeComponent(node.getNodeName(), nodeToString(node), null);
			int colspan = 1;
			if (StringAppUtils.isInteger(node.getAttribute("colspan"))) {
				colspan = Integer.parseInt(node.getAttribute("colspan"));
			}
			n.setAttribute("colspan", colspan);
			appendGrid(parentComponent, n);
		}
		
	}
	
	private void parseChildNode(Node childNode, Component parent) {
		while (childNode != null) {
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				recursiveParseNode((Element) childNode, parent);
			} else if (childNode.getNodeType() == Node.TEXT_NODE && childNode.getNodeValue() != null
					&& childNode.getNodeValue().trim().length() > 0) {
				HtmlNativeComponent n = new HtmlNativeComponent("div", childNode.getNodeValue(), null);
				int colspan = 1;
				n.setAttribute("colspan", colspan);
				appendGrid(parent, n);
			}
			childNode = childNode.getNextSibling();
		}
	}
	private void appendGrid(Component parent, Component component) {
		if (parent instanceof HtmlNativeComponent) {
			int multiple = (int) parent.getAttribute("multiple");
			int colspan = (int) component.getAttribute("colspan");
			colspan = colspan * multiple;
			HtmlNativeComponent div = new HtmlNativeComponent("div");
			div.setDynamicProperty("class", "col-md-" + colspan);
			div.setParent(parent);
			div.appendChild(component);
		} else {
			component.setParent(parent);
		}
	}
	
	private static String nodeToString(Node node) {
		StringWriter sw = new StringWriter();
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new DOMSource(node), new StreamResult(sw));
		} catch (TransformerException te) {
			System.out.println("nodeToString Transformer Exception");
		}
		return sw.toString();
	}
}
