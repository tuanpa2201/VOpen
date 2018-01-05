package base;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.ngi.zhighcharts.SimpleExtXYModel;
import org.ngi.zhighcharts.ZHighCharts;
import org.zkoss.zhtml.Input;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.sun.management.OperatingSystemMXBean;

import base.view.ClientInfo;

@SuppressWarnings("restriction")
public class VMonitor extends Window {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Input txtMem, txtCpu;

	public void onCreate() throws Exception {
		Label lbsuc = (Label) getFellow("label1");
		Label lbjuc = (Label) getFellow("label2");
		Label lbat = (Label) getFellow("label3");
		Label lbstm = (Label) getFellow("label4");
		Label lbjmm = (Label) getFellow("label5");
		Label lbjtm = (Label) getFellow("label6");
		Label lbjum = (Label) getFellow("label7");
		Label lbjfm = (Label) getFellow("label8");
		Grid grid = (Grid) getFellow("grid");
		grid.setRowRenderer(new RowRenderer<VEnvItem>() {
			@Override
			public void render(Row row, VEnvItem item, int index) throws Exception {
				// TODO Auto-generated method stub
				Label lb11 = new Label(item.object.getSessionKey());
				String user = "";
				if (item.object.user != null) {
					user = item.object.user.toString();
				}
				Label lb21 = new Label(user);
				Label lb31 = new Label(String.valueOf(item.object.getLastTimeUsed()));
				Label lb51 = new Label(String.valueOf(item.object.getTimeLogined()));
				ClientInfo info = item.object.getClientInfo();
				Label lb61 = new Label(info == null ? "unknown" : info.getDeviceLogin());
				lb11.setParent(row);
				lb21.setParent(row);
				lb31.setParent(row);
				lb51.setParent(row);
				lb61.setParent(row);
			}
		});
		loadChartMem();
		loadChartCpu();
		loadListSession();
		Timer timer = new Timer();
		timer.setDelay(1000);
		timer.setRepeats(true);
		timer.setParent(this);
		timer.addEventListener(Events.ON_TIMER, new EventListener<Event>() {

			public void onEvent(Event arg0) throws Exception {
				int mb = 1024 * 1024;
				OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
				Runtime runtime = Runtime.getRuntime();
				double systemCpuUsage = Math.round(osBean.getSystemCpuLoad() * 10000d) / 100d;
				double javaCpuUsage = Math.round(osBean.getProcessCpuLoad() * 10000d) / 100d;
				long activeThread = Thread.activeCount();
				long systemTotalMenory = osBean.getTotalPhysicalMemorySize() / mb;
				long javaTotalMenory = runtime.totalMemory() / mb;
				long javaMaxMemory = runtime.maxMemory() / mb;
				long javaFreeMemory = runtime.freeMemory() / mb;
				long memNew = (runtime.totalMemory() - runtime.freeMemory()) / mb;
				lbsuc.setValue("System usage CPU: " + systemCpuUsage);
				lbjuc.setValue("Java usage CPU: " + javaCpuUsage);
				lbat.setValue("Active thread: " + activeThread);
				lbstm.setValue("System total memory: " + systemTotalMenory + " MB");
				lbjmm.setValue("Java maximum memory:" + javaMaxMemory + " MB");
				lbjtm.setValue("Java total memory: " + javaTotalMenory + " MB");
				lbjum.setValue("Java used memory: " + memNew + " MB");
				lbjfm.setValue("Java free memory: " + javaFreeMemory + " MB");
				Label lbCPu = (Label) getFellow("lbCpu");
				lbCPu.setValue("System usage CPU: " + systemCpuUsage + "%");
				Label lbMem = (Label) getFellow("lbMem");
				lbMem.setValue("Java total memory: " + javaTotalMenory + " MB");
				Label lbCPu1 = (Label) getFellow("lbCpu1");
				lbCPu1.setValue("Java usage CPU: " + javaCpuUsage + "%");
				Label lbMem1 = (Label) getFellow("lbMem1");
				lbMem1.setValue("Java used memory: " + memNew + " MB");
				txtMem.setValue(String.valueOf(memNew));
				txtCpu.setValue(String.valueOf(systemCpuUsage));
			}
		});
		timer.start();
		Button revertBtn = (Button) getFellow("refreshBtn");
		revertBtn.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			@Override
			public void onEvent(Event paramT) throws Exception {
				loadListSession();
			}
		});
	}

	private void loadListSession() {
		Grid grid = (Grid) getFellow("grid");
		List<VEnvItem> items = new ArrayList<>();
		for (String key : VEnvManager.allEnv.keySet()) {
			VEnvItem item = VEnvManager.allEnv.get(key);
			items.add(item);
		}
		grid.setModel(new ListModelList<>(items));
		grid.setPageSize(20);
		// Rows rows = (Rows) getFellow("rows");
		// rows.getChildren().clear();
		// rows.invalidate();
		// Object[] keys = VEnvManager.allEnv.keySet().toArray();
		// for (Object key : keys) {
		// VEnvItem item = VEnvManager.allEnv.get(key);
		// if (item != null) {
		// Row r = new Row();
		// Label lb11 = new Label(item.object.getSessionKey());
		// String user = "";
		// if (item.object.user != null) {
		// user = item.object.user.toString();
		// }
		// Label lb21 = new Label(user);
		// Label lb31 = new Label(String.valueOf(item.object.getLastTimeUsed()));
		// Label lb51 = new Label(String.valueOf(item.object.getTimeLogined()));
		// ClientInfo info = item.object.getClientInfo();
		// Label lb61 = new Label(info == null ? "nnknown" : info.getDeviceLogin());
		// lb11.setParent(r);
		// lb21.setParent(r);
		// lb31.setParent(r);
		// lb51.setParent(r);
		// lb61.setParent(r);
		// r.setParent(rows);
		// rows.invalidate();
		// }
		// }
	}

	private void loadChartMem() {

		txtMem = new Input();
		txtMem.setValue("0");
		txtMem.setId("txtMem");
		txtMem.setParent(this);
		txtMem.setVisible(false);
		ZHighCharts chartMem = (ZHighCharts) getFellow("chartMem");
		SimpleExtXYModel dataChartMem = new SimpleExtXYModel();
		chartMem.setOptions("{" + "marginRight: 10," + "events: {" + "load: function() {"
				+ "var series = this.series[0];" + "setInterval(function() {"
				+ "var milliseconds = new Date().getTime() + (7 * 60 * 60 * 1000);"
				+ "var x = (new Date(milliseconds)).getTime(); y = parseInt($('#txtMem').val());" + "console.log(y);"
				+ "series.addPoint([x,y], true, true);" + "}," + " 1000);" + "}" + "}" + "}");
		chartMem.setTitle("Monitor");
		chartMem.setType("area");
		chartMem.setxAxisOptions("{ " + "type: 'datetime'," + "tickPixelInterval: 150" + "}");
		chartMem.setyAxisOptions(
				"{" + "plotLines: [" + "{" + "value: 0," + "width: 1," + "color: '#808080'" + "}" + "]" + "}");
		chartMem.setYAxisTitle("MB");
		chartMem.setTooltipFormatter("function formatTooltip(obj){" + "return '<b>'+ obj.series.name +'</b><br/>"
				+ "'+Highcharts.dateFormat('%d-%m-%Y %I:%M %p', obj.x) +'<br/>" + "'+Highcharts.numberFormat(obj.y, 2);"
				+ "}");
		chartMem.setPlotOptions("{" + "area:{" + "fillColor:{" + "linearGradient:{" + "x1:0," + "y1:0," + "x2:0,"
				+ "y2:1" + "}," + "stops:[" + "[0,Highcharts.getOptions().colors[0]]," + "[1,'rgba(2,0,0,0)']" + "]"
				+ "}," + "lineWidth:1," + "marker:{" + "enabled:false," + "states:{" + "hover:{" + "enabled:true,"
				+ "radius:5" + "}" + "}" + "}," + "shadow:false," + "states:{" + "hover:{" + "lineWidth:1" + "}" + "}"
				+ "}" + "}");
		chartMem.setLegend("{" + "enabled: false " + "}");

		chartMem.setModel(dataChartMem);

		int mb = 1024 * 1024;
		Runtime runtime = Runtime.getRuntime();
		long javaUseMemory = (runtime.totalMemory() - runtime.freeMemory()) / mb;
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR_OF_DAY, 7);
		for (int i = 0; i <= 360; i++) {
			dataChartMem.addValue("User heap", c.getTimeInMillis(), javaUseMemory);
		}
	}

	private void loadChartCpu() {
		txtCpu = new Input();
		txtCpu.setValue("0");
		txtCpu.setId("txtCpu");
		txtCpu.setParent(this);
		txtCpu.setVisible(false);
		ZHighCharts chartCpu = (ZHighCharts) getFellow("chartCpu");
		SimpleExtXYModel dataChartCpu = new SimpleExtXYModel();
		chartCpu.setOptions("{" + "marginRight: 10," + "events: {" + "load: function() {"
				+ "var series = this.series[0];" + "setInterval(function() {"
				+ "var milliseconds = new Date().getTime() + (7 * 60 * 60 * 1000);"
				+ "var x = (new Date(milliseconds)).getTime(); y = parseInt($('#txtCpu').val());" + "console.log(x);"
				+ "series.addPoint([x,y], true, true);" + "}," + " 1000);" + "}" + "}" + "}");
		chartCpu.setTitle("CPU");
		chartCpu.setType("area");
		chartCpu.setxAxisOptions("{ " + "type: 'datetime'," + "tickPixelInterval: 150" + "}");
		chartCpu.setyAxisOptions(
				"{" + "plotLines: [" + "{" + "value: 0," + "width: 1," + "color: '#808080'" + "}" + "]" + "}");
		chartCpu.setYAxisTitle("%");
		chartCpu.setTooltipFormatter("function formatTooltip(obj){" + "return '<b>'+ obj.series.name +'</b><br/>"
				+ "'+Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', obj.x) +'<br/>" + "'+Highcharts.numberFormat(obj.y, 2);"
				+ "}");
		chartCpu.setPlotOptions("{" + "area:{" + "fillColor:{" + "linearGradient:{" + "x1:0," + "y1:0," + "x2:0,"
				+ "y2:1" + "}," + "stops:[" + "[0,Highcharts.getOptions().colors[0]]," + "[1,'rgba(2,0,0,0)']" + "]"
				+ "}," + "lineWidth:1," + "marker:{" + "enabled:false," + "states:{" + "hover:{" + "enabled:true,"
				+ "radius:5" + "}" + "}" + "}," + "shadow:false," + "states:{" + "hover:{" + "lineWidth:1" + "}" + "}"
				+ "}" + "}");
		chartCpu.setLegend("{" + "enabled: false " + "}");

		chartCpu.setModel(dataChartCpu);

		OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		double systemCpuUsage = Math.round(osBean.getSystemCpuLoad() * 10000d) / 100d;
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR_OF_DAY, 7);
		for (int i = 0; i <= 360; i++) {
			dataChartCpu.addValue("User heap", c.getTimeInMillis(), systemCpuUsage);
		}
	}

}
