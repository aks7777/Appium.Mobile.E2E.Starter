package company.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import company.base.BasePage;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.*;
import org.testng.annotations.ITestAnnotation;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class ExtentListeners implements ITestListener, ISuiteListener, IClassListener, IAnnotationTransformer
{
	static Date d = new Date();
	static String fileName;
	public static ExtentReports extent;
	public static ExtentTest test;

	public static ThreadLocal<ExtentTest> testReport = new ThreadLocal<>();

	public static ThreadLocal<Boolean> beforeSuiteflag = new ThreadLocal<>();
	public static ThreadLocal<Boolean> beforeClassflag = new ThreadLocal<>();
	public static ThreadLocal<Boolean> beforeTestflag = new ThreadLocal<>();

	public static ExtentTest getExtent() {
		String errMsg = "Extent Report handle null. Exiting.";

		try{
			if (Objects.nonNull(testReport.get()))
				return testReport.get();
			else
			{
				throw new RuntimeException(errMsg);
			}
		}catch (Exception e){
			LoggerUtilities.error(Arrays.toString(e.getStackTrace()));
		}
		return null;
	}

	public static void setBeforeClassflag(Boolean beforeclass) {
		beforeClassflag.set(beforeclass);
	}

	public static Boolean getBeforeClassflag() {

		return beforeClassflag.get();
	}

	public static void setBeforeSuiteflag(Boolean beforesuite) {
		beforeSuiteflag.set(beforesuite);
	}

	public static Boolean getBeforeSuiteflag() {
		return beforeSuiteflag.get();
	}

	public static void setBeforeTestflag(Boolean beforetest) {
		beforeTestflag.set(beforetest);
	}

	public static Boolean getBeforeTestflag() {
		return beforeTestflag.get();
	}

	public static void createTestPreReq(String className, String methodName){
		String errMsg = "ExtentReport setter handle is null. Exiting.";
		test = extent.createTest(className + " @Test Case : " + methodName);
		test.assignCategory(BasePage.getPlatform() + "-" + BasePage.getDeviceName());
		try{
			if (test != null)
				testReport.set(test);
			else
			{
				throw new RuntimeException(errMsg);
			}
		}catch (Exception e){
			LoggerUtilities.error(Arrays.toString(e.getStackTrace()));
		}
	}
	public void onTestStart(ITestResult result) {
		String testname = result.getMethod().getMethodName();
		LoggerUtilities.infoLoggerInFileAndReport("*** Test Case Started : " + testname);
		//set these flags to false to enable Extent report in @Test and later annotations.
		setBeforeSuiteflag(false);
		setBeforeClassflag(false);
		setBeforeTestflag(false);
	}

	public void onTestSuccess(ITestResult result) {
		String testname = result.getMethod().getMethodName();
		LoggerUtilities.info("*** Test Case PASSED : " + testname);
		String methodName = result.getMethod().getMethodName();
		String logText = "<b>" + "Test Case : " + methodName + " PASSED" + "</b>";
		Markup m = MarkupHelper.createLabel(logText, ExtentColor.GREEN);
		if (getExtent() != null)
			getExtent().pass(m);
	}

	private String getBase64Image(){
		String str = null;
		try{
			str = ((TakesScreenshot) BasePage.getDriver()).getScreenshotAs(OutputType.BASE64);
			if (str == null)
				throw new RuntimeException("Error in screenshot.");
		}catch (Exception e){
			LoggerUtilities.error("Error in screenshot : " + Arrays.toString(e.getStackTrace()));
		}
		return str;
	}

	public void onTestFailure(ITestResult result) {
		if(result.getThrowable() != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			result.getThrowable().printStackTrace(pw);
			LoggerUtilities.error(sw.toString());
			String testname = result.getMethod().getMethodName();
			LoggerUtilities.info("*** Test Case FAILED : " + testname);
		}
		if (getExtent() != null){
			String methodName = result.getMethod().getMethodName();
			String logText = "<b>" + "Test Case : " + methodName + " FAILED" + "</b>";
			getExtent().fail(result.getThrowable().getMessage());
			if (getBase64Image() != null)
				getExtent().fail("<b><font color=red>" + "Screenshot of failure" + "</font></b><br>",
					MediaEntityBuilder.createScreenCaptureFromBase64String(getBase64Image()).build());

			getExtent().fail(result.getThrowable());
			Markup m = MarkupHelper.createLabel(logText, ExtentColor.RED);
			getExtent().log(Status.FAIL, m);
		}
	}

	public void onTestSkipped(ITestResult result) {
		if (result.getThrowable() != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			result.getThrowable().printStackTrace(pw);
			LoggerUtilities.error(sw.toString());
			String methodName = result.getMethod().getMethodName();
			String logText = "<b>" + "Test Case : " + methodName + " SKIPPED due to : " + result.getThrowable().getMessage() + "</b>";
			if (getExtent() != null)
			{
				getExtent().fail(result.getThrowable().getMessage());
				if(getBase64Image() != null)
					getExtent().fail("<b><font color=orange>" + "Screenshot of failure" + "</font></b><br>",
						MediaEntityBuilder.createScreenCaptureFromBase64String(getBase64Image()).build());

				getExtent().skip(result.getThrowable());
				Markup m = MarkupHelper.createLabel(logText, ExtentColor.ORANGE);
				getExtent().skip(m);
			}
			LoggerUtilities.info("*** Test Case SKIPPED due to error in Pre-req. : " + methodName);
		}else
		{
			String methodName = result.getMethod().getMethodName();
			LoggerUtilities.info("*** Test Case SKIPPED : " + methodName);
			String logText = "<b>" + "Test Case : " + methodName + " Skipped" + "</b>";
			Markup m = MarkupHelper.createLabel(logText, ExtentColor.YELLOW);
			if (getExtent() != null) {
				getExtent().skip(m);
			}
		}
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		// TODO Auto-generated method stub

	}

	public void onStart(ITestContext context) {
		setBeforeTestflag(true);
		if (getBeforeClassflag() == null)
			setBeforeClassflag(false);
		if (getBeforeSuiteflag() == null)
			setBeforeSuiteflag(false);
	}

	public void onFinish(ITestContext context) {
		if (extent != null) {
			extent.flush();
		}
		LoggerUtilities.info("Total Passed Tests: " + context.getPassedTests().size());
		LoggerUtilities.info("Total Failed Tests: " + context.getFailedTests().size());
		String reportPath = System.getProperty("user.dir") + File.separator + "reports" + File.separator + fileName;
		LoggerUtilities.info("\nTestNG Report Generated at : " + reportPath + "\n");
	}

	public void onStart(ISuite suite) {
		ReadProperties.readConfigFile();
		setBeforeSuiteflag(true);
		if (getBeforeClassflag() == null)
			setBeforeClassflag(false);
		if (getBeforeTestflag() == null)
			setBeforeTestflag(false);
		String groups = (System.getProperty("groups") != null && System.getProperty("groups").trim().length() > 0) ? System.getProperty("groups").trim() : "";
		groups = groups.substring(13);
		fileName = "AppiumReport_"  + groups + "_" + d.getTime() + ".html";
        extent = ExtentManager.createInstance("./reports/" + fileName);
	}

	String messageBody;
	public void onFinish(ISuite suite) {
	}

	@Override
	public void onBeforeClass(ITestClass testClass) {
		setBeforeClassflag(true);
		if (getBeforeSuiteflag() == null)
			setBeforeSuiteflag(false);
		if (getBeforeTestflag() == null)
			setBeforeTestflag(false);
	}

	@Override
	public void onAfterClass(ITestClass testClass) {
	}

	@Override
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
		annotation.setRetryAnalyzer(RetryAnalyzer.class);
	}
}
