/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 22, 2016
* Author: tuanpa
*
*/
package base.view.editor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkmax.zul.Chosenbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.ItemRenderer;
import org.zkoss.zul.ListModelList;

import base.model.Many2ManyField;
import base.model.VField;
import base.model.VObject;
import base.util.ZKEnv;

public class Many2ManyEditor extends ReferenceEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1004198064404038881L;
	private Many2ManyField mField = null;
	private boolean isSeachMoreInProgress = false;
	public Many2ManyEditor(VField field) {
		super(field);
		mField = (Many2ManyField) field;
		controller = ZKEnv.getEnv().get(mField.friendModel);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object value) {
		this.value = value;
		isSeachMoreInProgress = false;
		if (value instanceof Collection<?>) {
			Collection<VObject> vobjs = (Collection<VObject>) value;
			Set<ModelItem> selectedItems = new HashSet<>();
			for (VObject vobj : vobjs) {
				Integer id = (Integer) vobj.getValue("id");
				String label = controller.getDisplayString(id);
				ModelItem item = new ModelItem(id, label);
				if (model.contains(item)) {
					selectedItems.add(model.get(model.indexOf(item)));
				}
				else {
					model.add(item);
					selectedItems.add(item);
				}
			}
			Events.echoEvent("setSelectedObjects", this, selectedItems);
		}
		else {
			value = null;
			Events.echoEvent("setSelectedObjects", this, null);
		}
		isChanged = true;
	}
	
	public void setSelectedObjects(Event event) {
		Set<ModelItem> selectedItems = new HashSet<>();
		if (event.getData() == null)
			chosenbox.setSelectedObjects(selectedItems);
		else
			chosenbox.setSelectedObjects((Collection<?>) event.getData());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void onEvent(Event event) throws Exception {
		if (isSeachMoreInProgress)
			return;
		if (event.getName().equals(Events.ON_SELECT)
				&& event.getTarget() == chosenbox) {
			Collection val = (Collection) value;
			Set<ModelItem> selectedItems = chosenbox.getSelectedObjects();
			val.clear();
			for (ModelItem item : selectedItems) {
				val.add(controller.browse(item.id));
			}
			setValue(val);
		}
		if (event.getTarget().getAttribute("action") != null 
				&& event.getTarget().getAttribute("action").equals("select")) {
			isSeachMoreInProgress = true;
			Many2AnySelector selector = Many2AnySelector.getSelector(this, mField.friendModel, true, filter);
			selector.setParent(ZKEnv.getHomePage().getDivMain());
			selector.doModal();
		}
		isChanged = true;
		super.onChangedValue();
		super.onEvent(event);
	}
	
	@Override
	public void setOriginValue(Object value) {
		if (value == null)
		{
			value = new HashSet<>();
		}
		this.value = value;
		setValue(value);
		isChanged = false;
		originValue = value;
	}
	private boolean isChanged = false;
	
	@Override
	public boolean isChanged(){
		return isChanged;
	}
	ListModelList<ModelItem> model;

	Chosenbox chosenbox;
	Button btAdd;
	@Override
	public void initComponent() {
		model = new ListModelList<>();
		component = new Hlayout();
		((Hlayout) component).setSpacing("0.1");
		chosenbox = new Chosenbox();
		chosenbox.setHflex("1");
		chosenbox.addEventListener(Events.ON_SELECT, this);
		chosenbox.setItemRenderer(new ChosenBoxItemRenderer());
		chosenbox.setModel(model);
		chosenbox.setParent(component);
		
		btAdd = new Button();
		btAdd.setZclass("none");
		btAdd.setSclass("btn btn-default");
		btAdd.setImage("/themes/images/menu16.png");
		btAdd.setAttribute("action", "select");
		btAdd.addEventListener(Events.ON_CLICK, this);
		btAdd.setParent(component);
		btAdd.setWidth("32px");
		btAdd.setHeight("32px");
	}

	@Override
	public void setReadonly(Boolean readonly) {
		chosenbox.setDisabled(readonly);
		btAdd.setDisabled(readonly);
	}
	
	class ModelItem {
		Integer id;
		String label;
		public ModelItem(Integer id, String label) {
			super();
			this.id = id;
			this.label = label;
		}
		@Override
		public int hashCode() {
			return id;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ModelItem) {
				ModelItem toItem = (ModelItem)obj;
				return this.id.equals(toItem.id);
			}
			return false;
		}
		
	}
	
	class ChosenBoxItemRenderer implements ItemRenderer<ModelItem> {

		@Override
		public String render(Component comp, ModelItem item, int index) throws Exception {
			String retVal = item.label;
			return retVal;
		}
	}

	@Override
	public void reset() {
		model.clear();
		chosenbox.setModel(model);
	}
}
