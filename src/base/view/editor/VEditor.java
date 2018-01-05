/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 22, 2016
* Author: tuanpa
*
*/
package base.view.editor;

import java.util.Collection;
import java.util.Date;

import org.w3c.dom.Element;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vlayout;

import base.controller.VController;
import base.controller.VEnv;
import base.model.AddressField;
import base.model.ButtonField;
import base.model.DateField;
import base.model.DateTimeField;
import base.model.DecimalField;
import base.model.FileField;
import base.model.FunctionField;
import base.model.ImageField;
import base.model.IntegerField;
import base.model.Many2ManyField;
import base.model.Many2OneField;
import base.model.One2ManyField;
import base.model.SelectionField;
import base.model.StringField;
import base.model.TextField;
import base.model.TimeField;
import base.model.VField;
import base.model.VObject;
import base.model.YesNoField;
import base.util.DateUtils;
import base.util.StringAppUtils;
import base.util.Translate;
import base.util.ZKEnv;

public abstract class VEditor extends Vlayout implements EventListener<Event> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6086784151147521838L;

	private Label label;
	protected Image helpImage;
	public VField field;
	private String style;
	public Component component;
	protected Object originValue;
	protected Object value;
	public IEditorListenner onChangeListener;
	public String moduleId;

	/**
	 * Set style of this editor forcus: editor is important blur: not important
	 * editor
	 */
	public void setViewStyle(String style) {
		this.style = style;
		if (this.style.equalsIgnoreCase("focus")) {
			if (component instanceof HtmlBasedComponent) {
				HtmlBasedComponent hcomponent = (HtmlBasedComponent) component;
				hcomponent.setSclass("focus");
			}
		} else if (this.style.equalsIgnoreCase("blur")) {
			if (component instanceof HtmlBasedComponent) {
				HtmlBasedComponent hcomponent = (HtmlBasedComponent) component;
				hcomponent.setSclass("blur");
			}
		} else {
			this.style = "normal";
		}
	}

	public VEditor(VField field) {
		this.field = field;
		this.moduleId = field.moduleId;
		initComponent();
		initUI();
	}

	protected void initUI() {
//		if (!(this instanceof One2ManyEditor)) {
		this.setSclass("input");
//		}
		this.setHflex("1");
		if(!(this instanceof One2ManyEditor)) {
			Div labelDiv = new Div();
			this.appendChild(labelDiv);
			label = new Label(Translate.translate(ZKEnv.getEnv(), moduleId, field.label));
			label.setStyle("margin-left: 5px;");
			labelDiv.appendChild(label);
			if (!StringAppUtils.isEmpty(field.help)) {
				helpImage = new Image("/themes/images/help16.png");
				helpImage.addEventListener(Events.ON_CLICK, this);
				helpImage.setStyle("margin-left: 10px");
				labelDiv.appendChild(helpImage);
			}	
		}
		
		this.appendChild(component);
		if (component instanceof HtmlBasedComponent) {
			HtmlBasedComponent hComponent = (HtmlBasedComponent)component;
			hComponent.setStyle("margin-left: 5px; margin-right: 5px;");
		}
		this.setReadonly(field.isReadOnly);
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getTarget().equals(helpImage)) {
			Clients.showNotification(field.help, helpImage);
		}
	}

	public abstract void initComponent();

	public void setValue(Object value) {
		validateValue(value);
		if ((value == null && this.value == null) || (value != null && value.equals(this.value))) {
			return;
		}
		this.value = value;
		onChangedValue();
	}
	
	public void validateValue(Object value){
		if (!(this instanceof One2ManyEditor) || !(this instanceof YesNoEditor)) {
			try {
				
				field.validationField(value);
				setVSclass("state-success");
				this.setAttribute("StringDetail","");
			} catch (Exception e) {
				setVSclass("state-error");
				this.setAttribute("StringDetail",translateLanguage(e.getMessage()));
			}
		}
	}
	
	private String translateLanguage(String conten) {
		  StringBuilder builder = new StringBuilder(Translate.translate(ZKEnv.getEnv(), null, field.label) + " ");
		  String errorString = conten.replaceFirst(field.label, "").trim();
		  builder.append(Translate.translate(ZKEnv.getEnv(), null, errorString));
		  return builder.toString();
		 }
	
    public void setVSclass(String state){
    	if (field.isEnforce()) {
    		this.setSclass("input " + state);
		}else {
			this.setSclass("input " + "none" + state);
		}
    }
	public Object getValue() {
		return this.value;
	}

	public abstract void setReadonly(Boolean readonly);

	public void setDisplay(Boolean isDisplay) {
		this.setVisible(isDisplay);
	}

	public void setOriginValue(Object value) {
		originValue = value;
		this.setValue(value);
	}

	public void onChangedValue() {
		if (onChangeListener != null)
			onChangeListener.onChangedValue(this);
	}

	public boolean isChanged() {
		if (value == null && originValue == null)
			return false;
		if (value == null && originValue != null)
			return true;
		return !value.equals(originValue);
	}
	
	public static VEditor getEditor(VField field) {
		return getEditor(field, null);
	}

	public static VEditor getEditor(VField field, Element node) {
		VEditor editor = null;
		if (field.editorClass != null) {
			try {
				Class<?> eClass = Class.forName(field.editorClass);
				editor = (VEditor) eClass.getDeclaredConstructor(VField.class).newInstance(field);
			} catch (Exception e) {
				editor = null;
				e.printStackTrace();
			}
			if (editor != null) {
				return editor;
			}
		}
		if (field instanceof StringField) {
			editor = new StringEditor(field);
		} else if (field instanceof IntegerField) {
			editor = new IntegerEditor(field);
		} else if (field instanceof DecimalField) {
			editor = new DecimalEditor(field);
		} else if (field instanceof DateField) {
			editor = new DateEditor(field);
		} else if (field instanceof DateTimeField) {
			editor = new DateTimeEditor(field);
		} else if (field instanceof YesNoField) {
			editor = new YesNoEditor(field);
		} else if (field instanceof SelectionField) {
			editor = new SelectionEditor(field);
		} else if (field instanceof AddressField) {
			editor = new AddressEditor(field);
		} else if (field instanceof Many2OneField) {
			editor = new Many2OneEditor(field);
		}else if (field instanceof One2ManyField) {
			editor = new One2ManyEditor(field);
			if (node != null) {
				((One2ManyEditor) editor).buildView(node);
			}
		} else if (field instanceof Many2ManyField) {
			editor = new Many2ManyEditor(field);
		} else if (field instanceof FunctionField) {
			editor = new FunctionEditor(field);
		} else if (field instanceof FileField) {
			editor = new FileEditor(field);
		} else if (field instanceof ImageField) {
			editor = new ImageEditor(field);
		} else if (field instanceof TimeField) {
			editor = new TimeEditor(field);
		}
		else if (field instanceof TextField) {
			editor = new TextEditor(field);
		}
		else if (field instanceof ButtonField) {
			editor = new ButtonEditor(field);
		}
		return editor;
	}

	public Label getLabel() {
		return label;
	}
	
	@SuppressWarnings("unchecked")
	public Component getEditorForListView() {
		Component editor = null;
		if (field.editorClass != null) {
			try {
				VEditor customEditor = (VEditor) Class.forName(field.editorClass).newInstance();
				customEditor.setValue(this.getValue());
				editor = customEditor.getEditorForListView();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			if (editor == null) {
				editor = new Label("Error parsing editor");
			}
			return editor;
		}
		if (this instanceof YesNoEditor) {
			editor = new Checkbox();
			if (value instanceof Boolean)
				((Checkbox)editor).setChecked((Boolean)value);
			((Checkbox)editor).setDisabled(true);
		}
		else if (this instanceof SelectionEditor) {
			editor = new Label();
			if (value != null) {
				((Label)editor).setValue(((Combobox) component).getText());
			}
		}
		else if (this instanceof Many2OneEditor) {
			editor = new Label();
			if (value != null && value instanceof VObject) {
				Many2OneField mField = (Many2OneField) field;
				VController controller = VEnv.sudo().get(mField.parentModel);
				String display = controller.getDisplayString(((VObject)value).getId());
				((Label)editor).setValue(display);
			}
		}
		else if (this instanceof Many2ManyEditor) {
			editor = new Label();
			if (value != null && value instanceof Collection<?>) {
				Many2ManyField mField = (Many2ManyField) field;
				VController controller = VEnv.sudo().get(mField.friendModel);
				String display = "";
				for (VObject obj : (Collection<VObject>)value) {
					if (display.length() > 0) {
						display += ", ";
					}
					display += controller.getDisplayString(obj.getId());
				}
				((Label)editor).setValue(display);
			}
		}
		else {
			editor = new Label();
			if (value != null) {
				if(field instanceof DateField)
					((Label)editor).setValue(DateUtils.formatDate((Date)value));
				else
					((Label)editor).setValue(value.toString());
			}
		}
		return editor;
	}
	
	public void hideLabel() {
		if (label != null && label.getParent() != null) {
			label.getParent().setVisible(false);
		}
	}
	
	public void showLabel() {
		if (label != null && label.getParent() != null) {
			label.getParent().setVisible(true);
		}
		
	}
	@Override
	public boolean setVisible(boolean visible) {
		if (component != null) {
			component.setVisible(visible);
		}
		return super.setVisible(visible);
	}
//	public boolean checkValueInput() throws Exception{
//		return field.validationField(this.value);
//	}
//	public Constraint getVConstraint(){
//		Constraint constraint = new Constraint() {
//			
//			@Override
//			public void validate(Component comp, Object value) throws WrongValueException {
//				try {
//					field.validationField(value);
//				} catch (Exception e) {
//					throw new WrongValueException(comp,e.getMessage());
//				}
//			}
//		};
//		return constraint;
//	}
}
