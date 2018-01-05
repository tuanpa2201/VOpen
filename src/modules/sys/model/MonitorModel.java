package modules.sys.model;

import java.lang.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;

public class MonitorModel {
	private double javaCpuUsage;
	private double systemCpuUsage;
	private long activeThread;
	private long systemTotalMenory;
	private long javaTotalMenory;
	private long javaMaxMemory;
	private long javaUseMemory;
	private long javaFreeMemory;

	public double getJavaCpuUsage() {
		return javaCpuUsage;
	}

	public void setJavaCpuUsage(double javaCpuUsage) {
		this.javaCpuUsage = javaCpuUsage;
	}

	public double getSystemCpuUsage() {
		return systemCpuUsage;
	}

	public void setSystemCpuUsage(double systemCpuUsage) {
		this.systemCpuUsage = systemCpuUsage;
	}

	public long getActiveThread() {
		return activeThread;
	}

	public void setActiveThread(long activeThread) {
		this.activeThread = activeThread;
	}

	public long getSystemTotalMenory() {
		return systemTotalMenory;
	}

	public void setSystemTotalMenory(long systemTotalMenory) {
		this.systemTotalMenory = systemTotalMenory;
	}

	public long getJavaTotalMenory() {
		return javaTotalMenory;
	}

	public void setJavaTotalMenory(long javaTotalMenory) {
		this.javaTotalMenory = javaTotalMenory;
	}

	public long getJavaMaxMemory() {
		return javaMaxMemory;
	}

	public void setJavaMaxMemory(long javaMaxMemory) {
		this.javaMaxMemory = javaMaxMemory;
	}

	public long getJavaUseMemory() {
		return javaUseMemory;
	}

	public void setJavaUseMemory(long javaUseMemory) {
		this.javaUseMemory = javaUseMemory;
	}

	public long getJavaFreeMemory() {
		return javaFreeMemory;
	}

	public void setJavaFreeMemory(long javaFreeMemory) {
		this.javaFreeMemory = javaFreeMemory;
	}

	@SuppressWarnings("restriction")
	public static MonitorModel getMonitorModel() {
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
		long javaUseMemory = (runtime.totalMemory() - runtime.freeMemory()) / mb;
		MonitorModel model = new MonitorModel();
		model.setSystemCpuUsage(systemCpuUsage);
		model.setJavaCpuUsage(javaCpuUsage);
		model.setActiveThread(activeThread);
		model.setSystemTotalMenory(systemTotalMenory);
		model.setJavaTotalMenory(javaTotalMenory);
		model.setJavaMaxMemory(javaMaxMemory);
		model.setJavaFreeMemory(javaFreeMemory);
		model.setJavaUseMemory(javaUseMemory);
		return model;
	}
}
