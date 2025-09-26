package company.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * LoggerUtilities class provides methods for logging information and error messages.
 * It includes methods for logging messages to both the console and the extent report.
 */
public class LoggerUtilities {

    /**
     * This method is used to log an information message to both the console and the extent report.
     * @param msg This is the message to log.
     */
    public static void infoLoggerInFileAndReport(String msg) {
        info(msg);
        if (null != ExtentListeners.getExtent())
            ExtentListeners.getExtent().info(msg);
    }

    /**
     * This method is used to log an error message to both the console and the extent report.
     * @param msg This is the message to log.
     */
    public static void errorLoggerInFileAndReport(String msg) {
        error(msg);
        if (null != ExtentListeners.getExtent())
            ExtentListeners.getExtent().fail(msg);
    }

    /**
     * This method is used to log an information message to the console.
     * @param msg This is the message to log.
     */
    public static void info(String msg) {
        Logger logger = LogManager.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
        logger.info(msg);
    }

    /**
     * This method is used to log an error message to the console.
     * @param msg This is the message to log.
     */
    public static void error(String msg) {
        Logger logger = LogManager.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
        logger.error(msg);
    }

    /**
     * This method is used to attach an information message to the extent report.
     * @param msg This is the message to attach.
     */
    public static void attachAsInfo(String msg) {
        if (null != ExtentListeners.getExtent())
            ExtentListeners.getExtent().info(msg);
    }

}