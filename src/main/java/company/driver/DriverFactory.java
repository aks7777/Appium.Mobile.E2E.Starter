package company.driver;

import org.openqa.selenium.remote.DesiredCapabilities;
import company.base.BasePage;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import company.utils.ReadProperties;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;

/**
 * DriverFactory class is responsible for creating and managing AppiumDriver instances.
 */
public class DriverFactory {
     /**
     * This method is used to select and initialize the appropriate driver based on the platform.
     * @param appPath This is the path to the application.
     * @param appiumUrl This is the URL of the Appium server.
     * @param isLocal This indicates whether the test is running locally or on BrowserStack.
     * @param udid This is the unique device identifier.
     * @param bundleId This is the bundle identifier for iOS applications.
     * @return AppiumDriver This returns the initialized driver.
     * @throws Exception If an incorrect platform is set, an exception is thrown.
     */
    public AppiumDriver selectDriver(String appPath, String appiumUrl, String udid, String bundleId) throws Exception {
        AppiumDriver driver;
        switch (BasePage.getPlatform().toLowerCase()) {
            case "android":
                UiAutomator2Options options = new UiAutomator2Options();
                if(BasePage.getTestExecutionLocation().equalsIgnoreCase("local") || BasePage.getTestExecutionLocation().equalsIgnoreCase("runner")) {
                    options.setUdid(udid)
                            .setPlatformName("Android")
                            .setAutomationName("UiAutomator2")
                            .setAutoGrantPermissions(true)
                            .setAppActivity(ReadProperties.getValue("appActivity"))
                            .setAppPackage(ReadProperties.getValue("appPackage"))
                            .setNewCommandTimeout(Duration.ofSeconds(150))
                            .setUiautomator2ServerInstallTimeout(Duration.ofSeconds(150))
                            .setApp(appPath)
                            .setAdbExecTimeout(Duration.ofSeconds(60))
                            .setCapability("appium:waitForIdleTimeout", 20000);
                    driver = new AndroidDriver(new URL(appiumUrl), options);
                } else {
                    HashMap<String, Object> browserstackOptions = new HashMap<String, Object>();
                    browserstackOptions.put("userName", BasePage.getSecret("BROWSERSTACK_USER"));
                    browserstackOptions.put("accessKey", BasePage.getSecret("BROWSERSTACK_KEY"));
//                    browserstackOptions.put("appiumVersion", "2.6.0"); //verify if needed.

                    options.setDeviceName("dummy") //tbd
                            .setPlatformVersion("14.0")
                            .setPlatformName("Android")
                            .setAutomationName("UiAutomator2")
                            .setAutoGrantPermissions(true)
                            .setApp(appPath)
                            .setCapability("bstack:options", browserstackOptions);
                    appiumUrl = "https://" + BasePage.getSecret("BROWSERSTACK_USER") + ":" + BasePage.getSecret("BROWSERSTACK_KEY") + "@hub.browserstack.com/wd/hub";
                    driver = new AndroidDriver(new URL(appiumUrl), options);
                }
                if (driver != null) {
                    BasePage.setDriver(driver);
                }else
                {
                    throw new Exception("Android Driver Not Found");
                }
                break;
            case "ios":
                XCUITestOptions options1 = new XCUITestOptions();
                DesiredCapabilities iosCapabilities = new DesiredCapabilities();
               if(BasePage.getTestExecutionLocation().equalsIgnoreCase("local") || BasePage.getTestExecutionLocation().equalsIgnoreCase("runner")) {
                    options1.setUdid(udid)
                            .setPlatformName("IOS")
                            .setAutomationName("XCUITest")
                            .setBundleId(bundleId)
                            .setAutoAcceptAlerts(true)
                            .useNewWDA()
                            .setWdaStartupRetryInterval(Duration.ofSeconds(Long.parseLong(ReadProperties.getValue("wdaRetryInterval"))))
                            .setWdaStartupRetries(Integer.parseInt(ReadProperties.getValue("wdaStartupRetries")))
                            .setWdaLocalPort(BasePage.generateRandomPort())
                            .setWdaLaunchTimeout(Duration.ofSeconds(Long.parseLong(ReadProperties.getValue("wdaLaunchTimeout"))))
                            .setWdaConnectionTimeout(Duration.ofSeconds(Long.parseLong(ReadProperties.getValue("wdaConnectionTimeout"))))
                            .setShowXcodeLog(true)
                            .setApp(appPath);
                    driver = new IOSDriver(new URL(appiumUrl), options1);
                }
                else {
                    HashMap<String, Object> browserstackOptions = new HashMap<String, Object>();
                    browserstackOptions.put("userName", BasePage.getSecret("BROWSERSTACK_USER"));
                    browserstackOptions.put("accessKey", BasePage.getSecret("BROWSERSTACK_KEY"));
//                    browserstackOptions.put("appiumVersion", "2.6.0"); //verify if needed.

                    options1.setDeviceName("dummy") //tbd
                            .setPlatformVersion("14.0")
                            .setPlatformName("iOS")
                            .setAutomationName("XCUITest")
                            .setApp(appPath)
                            .setCapability("bstack:options", browserstackOptions);
                    appiumUrl = "https://" + BasePage.getSecret("BROWSERSTACK_USER") + ":" + BasePage.getSecret("BROWSERSTACK_KEY") + "@hub.browserstack.com/wd/hub";
                    driver = new IOSDriver(new URL(appiumUrl), options1);
                }
                if (driver != null) {
                    BasePage.setDriver(driver);
                }else
                {
                    throw new Exception("IOS Driver not set. Exiting.");
                }
                break;
            default:
                throw new Exception("Incorrect platform set - " + BasePage.getPlatform());
        }
        BasePage.implicitWait();
        return BasePage.getDriver();
    }

    /**
     * This method is used to quit the driver.
     */
    public void tearDown() {
        BasePage.quitDriver();
    }
}
