package base.util;

import java.lang.management.ManagementFactory;

import org.apache.log4j.Logger;

import com.sun.management.OperatingSystemMXBean;

import base.VOpenStartup;
import base.common.CommonDefine;
import modules.sys.model.MonitorModel;

@SuppressWarnings("restriction")
public class SystemMonitor extends Thread {
	private final String name;
	private Logger logger;
	private final static int mb = 1024 * 1024;
	private Runtime runtime;
	private OperatingSystemMXBean osBean;
	private long timeSleep;
	
	public SystemMonitor() {
		super();
		this.name = "MonitorProcessor";
		this.timeSleep = 300000;
		// this.timeSleep = 10000;
		this.setName(name);
		this.logger = Logger.getLogger(CommonDefine.LOG_DEBUG);
		this.osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	}
	
	@Override
	public void run() {
		logger.info("StartSystem: " + VOpenStartup.startAppTime);
		while (!interrupted()) {
			try {
				runtime = Runtime.getRuntime();
				logger.info("{" + name + "}--->Start");
				logger.info("[" + name + "] - System usage CPU: "
						+ (Math.round(osBean.getSystemCpuLoad() * 10000d) / 100d));
				logger.info(
						"[" + name + "] - Java usage CPU: " + (Math.round(osBean.getProcessCpuLoad() * 10000d) / 100d));
				logger.info("[" + name + "] - Active thread: " + Thread.activeCount());
				logger.info(
						"[" + name + "] - System total memory: " + (osBean.getTotalPhysicalMemorySize() / mb) + "MB");
				logger.info("[" + name + "] - Java maximum memory: " + (runtime.maxMemory() / mb) + "MB");
				logger.info("[" + name + "] - Java total memory: " + (runtime.totalMemory() / mb) + "MB");
				logger.info("[" + name + "] - Java used memory: "
						+ ((runtime.totalMemory() - runtime.freeMemory()) / mb) + "MB");
				logger.info("[" + name + "] - Java free memory: " + (runtime.freeMemory() / mb) + "MB");
				logger.info("------------------------------------------------------------------");
			} catch (Exception e) {
				logger.error("", e);
			} finally {
				try {
					sleep(timeSleep);
				} catch (InterruptedException e) {
					logger.error("Sleep", e);
				}
			}
		}
	}
	
	public static String SystemHealthFeedback(){
		StringBuilder builder = new StringBuilder();
		MonitorModel monitorModel = MonitorModel.getMonitorModel();
		builder.append("############# System ################").append(System.lineSeparator());
		builder.append("--> Start time:").append(VOpenStartup.startAppTime).append(System.lineSeparator());
		builder.append("--> System usage CPU: ").append(Math.round(monitorModel.getSystemCpuUsage() * 10000d) / 100d)
				.append(System.lineSeparator());
		builder.append("--> Java usage CPU: ").append(Math.round(monitorModel.getJavaCpuUsage() * 10000d) / 100d)
				.append(System.lineSeparator());
		builder.append("--> Active thread: " + Thread.activeCount()).append(System.lineSeparator());
		builder.append("--> System total memory (in MB): ").append(monitorModel.getJavaFreeMemory() / mb)
				.append(System.lineSeparator());
		builder.append("--> Java maximum memory (in MB): ").append(monitorModel.getJavaMaxMemory() / mb)
				.append(System.lineSeparator());
		builder.append("--> Java total memory (in MB): ").append(monitorModel.getJavaTotalMenory() / mb)
				.append(System.lineSeparator());
		builder.append("--> Java used memory (in MB): ").append(((monitorModel.getJavaTotalMenory() - monitorModel.getJavaFreeMemory()) / mb))
				.append(System.lineSeparator());
		builder.append("--> Java free memory (in MB): ").append((monitorModel.getJavaFreeMemory() / mb))
				.append(System.lineSeparator());
		builder.append("#######################################").append(System.lineSeparator());
		return builder.toString();
	}	
}
