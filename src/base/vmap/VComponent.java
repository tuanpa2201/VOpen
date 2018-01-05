package base.vmap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;

public abstract class VComponent extends Div implements EventListener<Event> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5179608487646890216L;
	private LinkedList<String> scriptQueue = new LinkedList<>();
	private VComponent self = this;
	private boolean isRendered = false;
	
	protected synchronized void addJSScriptSynch(String jsScript) {
		scriptQueue.add(jsScript);
		runJSScriptSynch();
	}
	
	public synchronized void runJSScriptSynch() {
		if (scriptQueue.isEmpty())
			return;
		if (isRendered) {
			while (!scriptQueue.isEmpty()) {
				String script = scriptQueue.pop();
				Clients.evalJavaScript(script);
//				System.out.println("Call JS Script: " + script);
			}
			for (Component child : self.getChildren()) {
				if (child instanceof VComponent) {
					VComponent vchild = (VComponent) child;
					vchild.isRendered = true;
					vchild.runJSScriptSynch();
				}
			}
		}
	}

	public VComponent() {
		super();
		this.setId(IDGenerator.generateStringID());
	}
	
	private List<Component> children = new ArrayList<>();
	
	Component parent;
	
	@Override
	public Component getParent() {
		// TODO Auto-generated method stub
		return parent;
	}

	@Override
	public void setParent(Component parent) {
		this.parent = parent;
		if (parent instanceof VComponent) {
			((VComponent)parent).getChildren().add(this);
			if (((VComponent)parent).isRendered) {
				isRendered = true;
				runJSScriptSynch();
			}
		}
		if (parent != null) {
			parent.addEventListener(Events.ON_AFTER_SIZE, this);
		}
		if (this instanceof VMaps) {
			super.setParent(parent);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Component> List<T> getChildren() {
		return (List<T>) children;
	}
	
	@Override
	public boolean appendChild(Component child) {
		children.add(child);
		return true;
	}
	
	@Override
	public boolean removeChild(Component child) {
		children.remove(child);
		return true;
	};

	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getName().equals(Events.ON_AFTER_SIZE)) {
			isRendered = true;
			runJSScriptSynch();
		}
	}
}
