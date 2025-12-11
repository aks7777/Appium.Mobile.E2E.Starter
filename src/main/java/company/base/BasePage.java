package company.base;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import company.utils.ReadProperties;
import company.utils.LoggerUtilities;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;

/**
 * BasePage class provides the basic functionalities for the application pages.
 * It includes methods for setting and getting driver, platform, device, udid, environment, country, and secret.
 * It also includes methods for finding elements, waiting for visibility, clicking, sending keys, and other common actions.
 */
public class BasePage {
    protected static ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();
    protected static ThreadLocal<String> device = new ThreadLocal<>();
    protected static ThreadLocal<String> udid = new ThreadLocal<>();
    protected static String platform;
    protected static String country;
    protected static String env;
    protected static String testExecutionLocation;
    protected static ThreadLocal<Function<String, String>> secret = new ThreadLocal<Function<String, String>>();
    private static final String FINGER1 = "finger1";
    protected static Integer threadCount;
    /**
     * This method is used to get the test execution location.
     * @return boolean This returns the test execution location.
     */
    public static String getTestExecutionLocation(){
        return testExecutionLocation;
    }
    /**
     * This method is used to set the test execution location.
     * @param target This is the test execution location to set.
     */
    public static void setTestExecutionLocation(String target){
        testExecutionLocation = target;
    }

    /**
     * This method is used to get the driver.
     * @return AppiumDriver This returns the current driver.
     */
    public static AppiumDriver getDriver() {
        String errMsg = "Driver is null. Exiting.";
        try{
            if (driver != null && driver.get() != null) {
                return driver.get();
            }else
            {
                LoggerUtilities.errorLoggerInFileAndReport(errMsg);
                throw new RuntimeException(errMsg);
            }
        } catch (Exception e){
            LoggerUtilities.error("Caught : " + errMsg + " : " + Arrays.toString(e.getStackTrace()));
            throw new RuntimeException("Driver access failed. Cannot continue test.", e);
//            LoggerUtilities.error("Caught : " + errMsg + " : " + Arrays.toString(e.getStackTrace()) + ". Exiting forcefully.");
//            System.exit(-1);
        }
//        return null;
    }

    /**
     * This method is used to set the driver.
     * @param driver_temp This is the driver to set.
     */
    public static void setDriver(AppiumDriver driver_temp) {
        if (Objects.nonNull(driver_temp))
            driver.set(driver_temp);
    }

    /**
     * This method is used to get the platform.
     * @return String This returns the current platform.
     */
    public static String getPlatform() {
        return platform;
    }

    /**
     * This method is used to set the platform.
     * @param platformName This is the platform to set.
     */
    public static void setPlatform(String platformName) {
        if (Objects.nonNull(platformName))
            platform = platformName;
    }

    public static Integer getThreadCount() {
        return threadCount;
    }

    public static void setThreadCount(Integer threads) {
        if (Objects.nonNull(threads))
            threadCount = threads;
    }

    /**
     * This method is used to get the device name.
     * @return String This returns the current device name.
     */
    public static String getDeviceName() {
        return device.get();
    }

    /**
     * This method is used to set the device name.
     * @param deviceName This is the device name to set.
     */
    public static void setDeviceName(String deviceName) {
        if (Objects.nonNull(deviceName))
            device.set(deviceName);
    }

    /**
     * This method is used to get the UDID.
     * @return String This returns the current UDID.
     */
    public static String getUdid() {
        return udid.get();
    }

    /**
     * This method is used to set the UDID.
     * @param udidValue This is the UDID to set.
     */
    public static void setUdid(String udidValue) {
        if (Objects.nonNull(udidValue))
            udid.set(udidValue);
    }

    /**
     * This method is used to get the environment name.
     * @return String This returns the current environment name.
     */
    public static String getEnvName() {
        return env;
    }

    /**
     * This method is used to set the environment name.
     * @param envName This is the environment name to set.
     */
    public static void setEnvName(String envName) {
        env = envName;
    }

    /**
     * This method is used to get the country.
     * @return String This returns the current country.
     */
    public static String getCountry() {
        return country;
    }

    /**
     * This method is used to set the country.
     * @param countryName This is the country to set.
     */
    public static void setCountry(String countryName) {
        country = countryName;
    }

    private static final Object lock = new Object();
    /**
     * This method is used to quit the driver.
     */
    public static void quitDriver() {
        String errMsg = "quitDriver : Driver is null. Exiting.";
        synchronized (lock) {
            try {
                if (getDriver() != null) {
                    getDriver().quit();
                } else {
                    LoggerUtilities.error(errMsg);
                    throw new IllegalStateException(errMsg);
                }
            } catch (Exception e) {
                LoggerUtilities.error(errMsg + " : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is used to set the secret.
     * @param value This is the secret to set.
     */
    public static void setSecret(Function<String, String> value) {
        secret.set(value);
    }

    /**
     * This method is used to get the secret.
     * @param key This is the key of the secret to get.
     * @return String This returns the secret associated with the given key.
     */
    public static String getSecret(String key) {
        return secret.get().apply(key);
    }

    /**
     * This method is used to find an element by its locator.
     * @param by This is the locator of the element.
     * @return WebElement This returns the found element.
     */
    public WebElement findElement(By by)
    {
        return getDriver().findElement(by);
    }

    /**
     * This method is used to clear the given element.
     * @param element This is the element to clear.
     * @param message This is the message to log.
     */
    public void clear(WebElement element, String message) {
        try {
//            waitForVisibility(element);
            element.clear();
            LoggerUtilities.infoLoggerInFileAndReport("Clearing text on " + message + " field: " + element);
        } catch (Throwable t) {
            LoggerUtilities.infoLoggerInFileAndReport("Failed in Clearing an Element : " + element + " error message is :" + t.getMessage());
            //consiciously marking this as non-fail.
//            Assert.fail(t.getMessage());  //remove?? this adds to the number of tests run.
        }
    }

    /**
     * This method is used to wait for the given element to be visible.
     * @param element This is the element to wait for.
     */
    public void waitForVisibility(WebElement element) {
        Duration time = Duration.ofSeconds(Long.parseLong(ReadProperties.getValue("wait")));
        try{
            WebDriverWait wait = new WebDriverWait(getDriver(), time);
            wait.until(ExpectedConditions.visibilityOf(element));
        }catch (Exception e)
        {
            LoggerUtilities.error("Error in Wait For Visibility : " + element + " after " + time + " secs.");
        }
    }

    /**
     * This method is used to wait for the given element to be invisible.
     * Loader element to be disappeared
     */
    public void waitForLoaderToDisappear() {
        String locator = getPlatform().equalsIgnoreCase("android")? "loadingImageView" : "In progress";
        Duration time = Duration.ofSeconds(Long.parseLong(ReadProperties.getValue("wait")));
        try{
            WebDriverWait wait = new WebDriverWait(getDriver(), time);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(locator)));
        } catch (Exception e) {
            LoggerUtilities.error("Error in Wait For Loader to disappear");
        }
    }

    /**
     * This method is used to wait for the given element to be clickable.
     * @param element This is the element to wait for.
     */
    public void waitUntilClickable(WebElement element) {
        try{
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(Long.parseLong(ReadProperties.getValue("wait"))));
            wait.until(ExpectedConditions.elementToBeClickable(element));
        }catch (Exception e)
        {
            LoggerUtilities.error("Error in Wait For Clickable : " + element);
        }
    }

    /**
     * This method is used to click on the given element.
     * @param element This is the element to click on.
     * @param message This is the message to log.
     */
    public void click(WebElement element, String message) {
//        waitForVisibility(element);
        element.click();
        LoggerUtilities.infoLoggerInFileAndReport("Clicked on " + message + ": " + element);
    }

    /**
     * This method is used to send keys to the given element.
     * @param element This is the element to send keys to.
     * @param txt This is the text to send.
     * @param message This is the message to log.
     */
    public void sendKeys(WebElement element, String txt, String message) {
//        waitForVisibility(element);
        element.sendKeys(txt);
        LoggerUtilities.infoLoggerInFileAndReport("Entered " + message + " text: " + element);
    }

    /**
     * This method is used to enter text into the given element.
     * @param element This is the element to enter text into.
     * @param txt This is the text to enter.
     * @param msg This is the message to log.
     */
    public void enter(WebElement element, String txt, String msg) {
        clear(element, msg);
        sendKeys(element, txt, msg);
    }

    /**
     * This method is used to navigate back.
     */
    public void back() {
        getDriver().navigate().back();
    }

    /**
     * This method is used to get a locator by text.
     * @param elementForVisibility This is the element to wait for visibility.
     * @param value This is the text value to find.
     * @param index This is the index of the element to find.
     * @return WebElement This returns the found element.
     */
    public WebElement getLocatorByText(WebElement elementForVisibility, String value, String index) {
        waitForVisibility(elementForVisibility);
        if(getPlatform().equalsIgnoreCase("android")){
            return getDriver().findElement(By.xpath("(//android.widget.TextView[@text=\"" + value + "\"])[" + index + "]"));
        }
        else {
            return getDriver().findElement(By.xpath("(//XCUIElementTypeStaticText[@name=\"" + value + "\"])[" + index + "]"));
        }

    }

    /**
     * This method is used to check if the given element is displayed.
     * @param element This is the element to check.
     * @return Boolean This returns whether the element is displayed.
     */
    public Boolean isElementDisplayed(WebElement element) {
        waitForVisibility(element);
        try{
            return element.isDisplayed();
        }
        catch (Exception e){
            LoggerUtilities.error("Element is not displayed : " + Arrays.toString(e.getStackTrace()));
            return false;
        }
    }

    /**
     * This method is used to check if the given element is enabled.
     * @param element This is the element to check.
     * @return Boolean This returns whether the element is enabled.
     */
    public Boolean isElementEnabled(WebElement element) {
        waitForVisibility(element);
        try{
            return element.isEnabled();
        }
        catch (Exception e){
            LoggerUtilities.error("Element is not enabled : " + Arrays.toString(e.getStackTrace()));
            return false;
        }
    }

    /**
     * This method is used to check if the given element is selected.
     * @param element This is the element to check.
     * @return Boolean This returns whether the element is selected.
     */
    public Boolean isElementSelected(WebElement element) {
        waitForVisibility(element);
        try{
            return element.isSelected();
        }
        catch (Exception e){
            LoggerUtilities.error("Element is not selected : " + Arrays.toString(e.getStackTrace()));
            return false;
        }
    }

    /**
     * This method is used to get the attribute of the given element.
     * @param element This is the element to get the attribute from.
     * @param attribute This is the attribute to get.
     * @return String This returns the attribute of the element.
     */
    public String getAttribute(WebElement element, String attribute) {
        waitForVisibility(element);
        return element.getAttribute(attribute);
    }

    /**
     * This method is used to get the text from the attribute of the given element.
     * @param e This is the element to get the text from.
     * @param msg This is the message to log.
     * @return String This returns the text from the attribute of the element.
     */
    public String getTextFromAttribute(WebElement e, String msg) {
        waitForVisibility(e);
        String txt = null;
        switch(getPlatform()) {
            case "android":
                txt = getAttribute(e, "text");
                break;
            case "ios":
                txt = getAttribute(e, "label");
                break;
        }
        LoggerUtilities.infoLoggerInFileAndReport("Fetched text from " + msg + " attribute: " + txt);
        return txt;
    }

    /**
     * This method is used to generate a random port number between 8100 and 8200.
     * @return int This returns the generated port number.
     */
    public static int generateRandomPort() {
        int MIN_PORT = 8100;
        int MAX_PORT = 8200;
        Random RANDOM = new Random();
        Set<Integer> USED_PORTS = new HashSet<>();
        int port;
        do {
            port = RANDOM.nextInt((MAX_PORT - MIN_PORT) + 1) + MIN_PORT;
        } while (USED_PORTS.contains(port));
        USED_PORTS.add(port);
        return port;
    }

    public static void wait(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void implicitWait() {
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(Long.parseLong(ReadProperties.getValue("implicitWait"))));
    }

    public String getTodaysDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return currentDate.format(formatter);
    }

    public WebElement getElementFromList(List<WebElement> element, int index, String message) {
        try {
            return element.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(
                    "Unable to access the " + message +
                            ". Index " + index + " is out of bounds for list size " + element.size()
            );
        }
    }

    public void closeKeyboard() {
        if (getDriver() instanceof AndroidDriver) {
            ((AndroidDriver) getDriver()).hideKeyboard();
        } else if (getDriver() instanceof IOSDriver) {
            try{
                click(getDriver().findElement(By.xpath("//*[@label=\"Done\"]")), "Clicked on Done button");
            }
            catch (Exception e) {
                click(getDriver().findElement(By.xpath("//*[@label=\"Return\"]")), "Clicked on Done button");
                LoggerUtilities.error("Done button not found, clicked on Return button");
            }
        }
    }

    /**
     /**
     * This method is used to check if the given element is disabled.
     * @param element This is the element to check.
     * @return Boolean This returns whether the element is disabled.
     */
    public Boolean isElementDisabled(WebElement element) {
        waitForVisibility(element);
        try{
            return !element.isEnabled();
        }
        catch (Exception e){
            LoggerUtilities.error("Element is not disabled : " + Arrays.toString(e.getStackTrace()));
            return false;
        }
    }

    /**
     * This method is used to waitInSeconds for the given element to be visible.
     *
     * @param element This is the element to waitInSeconds for.
     * @param time   This is the duration to waitInSeconds.
     */
    public void waitForVisibility(WebElement element, Duration time) {
        WebDriverWait wait = new WebDriverWait(getDriver(), time);
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * This method is used to waitInSeconds for the given element to be invisible.
     * @param element This is the element to waitInSeconds for.
     */
    public void waitForInvisibility(WebElement element) {
        Duration time = Duration.ofSeconds(Long.parseLong(ReadProperties.getValue("wait")));
        try{
            WebDriverWait wait = new WebDriverWait(getDriver(), time);
            wait.until(ExpectedConditions.invisibilityOf(element));
        } catch (Exception e) {
            LoggerUtilities.error("Error in Wait For Invisibility : " + element + " after " + time + " secs.");
        }
    }

    /**
     * This method is used to click on the given element.
     *
     * @param element This is the element to click on.
     * @param message This is the message to log.
     * @param timeoutInSeconds This is the timeout in seconds to waitInSeconds for visibility.
     */
    public void click(WebElement element, String message, int timeoutInSeconds) {
        Duration time = Duration.ofSeconds(timeoutInSeconds);
        waitForVisibility(element, time);
        element.click();
        LoggerUtilities.infoLoggerInFileAndReport("Clicked on " + message);
    }

    public WebElement explicitWaitForVisibilityByLocator(By locator) {
        long waitSeconds = Long.parseLong(ReadProperties.getValue("wait"));
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(waitSeconds));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}