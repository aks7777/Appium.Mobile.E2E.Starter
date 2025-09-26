package company.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.util.Objects;

/**
 * ExtentManager class provides methods for managing the ExtentReports instance.
 * It includes a method for creating an instance of ExtentReports.
 */
public class ExtentManager {

	private static ExtentReports extent;
	public static String fileName;

	/**
	 * This method is used to create an instance of ExtentReports.
	 * If an instance already exists, it returns the existing instance.
	 * Otherwise, it creates a new instance, configures the reporter, and attaches the reporter to the instance.
	 * It also sets system information for the report.
	 * @param fileName This is the name of the file where the report will be saved.
	 * @return ExtentReports This returns the created or existing instance of ExtentReports.
	 */
	public static ExtentReports createInstance(String fileName) {
		if (Objects.isNull(extent))
		{
			ExtentSparkReporter htmlReporter = new ExtentSparkReporter(fileName);

			htmlReporter.config().setTheme(Theme.STANDARD);
			htmlReporter.config().setDocumentTitle(fileName);
			htmlReporter.config().setEncoding("utf-8");
			htmlReporter.config().setReportName(fileName);

			extent = new ExtentReports();
			extent.attachReporter(htmlReporter);
			extent.setSystemInfo("Automation Tester", "QA");
			String organisation = (System.getProperty("organisation") != null && System.getProperty("organisation").trim().length() > 0) ? System.getProperty("organisation").trim() : ReadProperties.getValue("organisation");
			extent.setSystemInfo("Organization", organisation);
			String env = (System.getProperty("env") != null && System.getProperty("env").trim().length() > 0) ? System.getProperty("env").trim() : ReadProperties.getValue("env");
			extent.setSystemInfo("Environment", env);
			String buildNumber = (System.getProperty("buildNumber") != null && System.getProperty("buildNumber").trim().length() > 0) ? System.getProperty("buildNumber").trim() : ReadProperties.getValue("buildNumber");
			extent.setSystemInfo("Build Number", buildNumber);
		}
		return extent;
	}
}
