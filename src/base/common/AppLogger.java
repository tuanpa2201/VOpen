package base.common;

import org.apache.log4j.Logger;

/**
 * 
 * @author DoVu Thiet lap va cau hinh cho log
 *
 */
public class AppLogger {
	public static Logger logDebug = Logger.getLogger(CommonDefine.LOG_DEBUG);
	public static Logger logTracking = Logger.getLogger(CommonDefine.LOG_TRACKING);
	public static Logger logAmqp = Logger.getLogger(CommonDefine.LOG_AMQP);
	public static Logger logExceptionAmqp = Logger.getLogger(CommonDefine.LOG_EXCEPTION_AMQP);
	public static Logger logDriver = Logger.getLogger(CommonDefine.LOG_DRIVER);
}
