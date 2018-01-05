package modules.sys.view;

import java.util.Date;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Timer;

import base.VOpenStartup;
import modules.sys.model.MonitorModel;

public class SysMonitor extends Div {
	/**
	 * 
	 */
	private static final long serialVersionUID = -454023793926364391L;
	private Listbox lstbox;
	
	public SysMonitor() {
		// TODO Auto-generated constructor stub
		this.init();
		Timer timer = new Timer();
		timer.setParent(this);
		timer.setRepeats(true);
		timer.setDelay(10000);
		timer.addEventListener(Events.ON_TIMER, new EventListener<Event>() {
			@Override
			public void onEvent(Event arg0) throws Exception {
				renderListBox(lstbox);
			}
		});
	}
	
	private void init() {
		lstbox = new Listbox();
		lstbox.setParent(this);
		Listhead lstHead = new Listhead();
		lstHead.setParent(lstbox);
		Listheader lstHeader = new Listheader();
		lstHeader.setParent(lstHead);
		lstHeader.setLabel("Thông số");
		lstHeader.setHflex("5");
		lstHeader = new Listheader();
		lstHeader.setParent(lstHead);
		lstHeader.setLabel("Giá trị");
		lstHeader.setHflex("5");
		this.renderListBox(lstbox);
	}

	private void renderListBox(Listbox lstbox) {
		lstbox.getItems().clear();
		MonitorModel monitorModel = MonitorModel.getMonitorModel();
		Listitem lstItem = new Listitem();
		lstItem.setParent(lstbox);
		Listcell lstCell = new Listcell();
		lstCell.setParent(lstItem);
		lstCell.setLabel("Thời gian khởi động hệ thống");
		lstCell = new Listcell();
		lstCell.setParent(lstItem);
		lstCell.setLabel(VOpenStartup.startAppTime + "");

		lstItem = new Listitem();
		lstItem.setParent(lstbox);
		lstCell = new Listcell();
		lstCell.setParent(lstItem);
		lstCell.setLabel("System cpu usage");
		lstCell = new Listcell();
		lstCell.setParent(lstItem);
		lstCell.setLabel(monitorModel.getSystemCpuUsage() + "");

		lstItem = new Listitem();
		lstItem.setParent(lstbox);
		lstCell = new Listcell();
		lstCell.setParent(lstItem);
		lstCell.setLabel("Java cpu usage");
		lstCell = new Listcell();
		lstCell.setParent(lstItem);
		lstCell.setLabel(monitorModel.getJavaCpuUsage() + "");

		lstItem = new Listitem();
		lstItem.setParent(lstbox);
		lstCell = new Listcell();
		lstCell.setParent(lstItem);
		lstCell.setLabel("Active threads");
		lstCell = new Listcell();
		lstCell.setParent(lstItem);
		lstCell.setLabel(monitorModel.getActiveThread() + "");

		lstItem = new Listitem();
		lstItem.setParent(lstbox);
		lstCell = new Listcell();
		lstCell.setParent(lstItem);
		lstCell.setLabel("System total memory");
		lstCell = new Listcell();
		lstCell.setParent(lstItem);
		lstCell.setLabel(monitorModel.getSystemTotalMenory() + "");

		lstItem = new Listitem();
		lstItem.setParent(lstbox);
		lstCell = new Listcell();
		lstCell.setParent(lstItem);
		lstCell.setLabel("Java maximum memory");
		lstCell = new Listcell();
		lstCell.setParent(lstItem);
		lstCell.setLabel(monitorModel.getJavaMaxMemory() + "");

		lstItem = new Listitem();
		lstItem.setParent(lstbox);
		lstCell = new Listcell();
		lstCell.setParent(lstItem);
		lstCell.setLabel("Java total memory");
		lstCell = new Listcell();
		lstCell.setParent(lstItem);
		lstCell.setLabel(monitorModel.getJavaTotalMenory() + "");

		lstItem = new Listitem();
		lstItem.setParent(lstbox);
		lstCell = new Listcell();
		lstCell.setParent(lstItem);
		lstCell.setLabel("Java use memory");
		lstCell = new Listcell();
		lstCell.setParent(lstItem);
		lstCell.setLabel(monitorModel.getJavaUseMemory() + "");

		lstItem = new Listitem();
		lstItem.setParent(lstbox);
		lstCell = new Listcell();
		lstCell.setParent(lstItem);
		lstCell.setLabel("Java free memory");
		lstCell = new Listcell();
		lstCell.setParent(lstItem);
		lstCell.setLabel(monitorModel.getJavaFreeMemory() + "");

		lstItem = new Listitem();
		lstItem.setParent(lstbox);
		lstCell = new Listcell();
		lstCell.setParent(lstItem);
		lstCell.setLabel("Date");
		lstCell = new Listcell();
		lstCell.setParent(lstItem);
		lstCell.setLabel((new Date()).toString());
	}
}
