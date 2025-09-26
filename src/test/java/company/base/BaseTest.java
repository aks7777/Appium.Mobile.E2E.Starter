package company.base;

import company.pom.products.Products;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;
import company.driver.DriverFactory;
import company.pom.login.Login;
import company.utils.*;
import org.apache.logging.log4j.ThreadContext;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
public class BaseTest {
    protected Login login;
    protected JSONObject testdata;
    protected RecordVideo recordVideo;
    protected Dotenv dotenv;
    DriverFactory driverFactory;
    String appPath;
    public String className;
    public JSONObject getJsonData;
    private static final Object lock = new Object();
    boolean flag = false;
    private boolean beforeClassSetupFailed = false;
    static Map<String, DevicePicker.Emulator> newStartedDevices;

    private void fireUpEnvVars(Map.Entry<String, DevicePicker.Emulator> entryMap) {
        BasePage.setUdid(entryMap.getValue().udid);
        BasePage.setDeviceName(entryMap.getKey());
        //logger
        String loggingFile = "logs" + File.separator + BasePage.getPlatform() + "_" + BasePage.getDeviceName();
        File logFile = new File(loggingFile);
        if (!logFile.exists()) {
            logFile.mkdirs();
        }
        //route logs to a separate file for each thread
        ThreadContext.put("ROUTINGKEY", loggingFile);
    }

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() throws IOException {
        DevicePicker devicePicker = new DevicePicker();
        //call appium server
//        devicePicker.startAppiumServer();
        String country = (System.getProperty("country") != null && System.getProperty("country").trim().length() > 0) ? System.getProperty("country").trim() : (String) ReadProperties.getValue("country");
        String env = (System.getProperty("env") != null && System.getProperty("env").trim().length() > 0) ? System.getProperty("env").trim() : (String) ReadProperties.getValue("env");
        String target = (System.getProperty("target") != null && System.getProperty("target").trim().length() > 0) ? System.getProperty("target").trim() : (String) ReadProperties.getValue("target");
        String platformName = (System.getProperty("platformName") != null && System.getProperty("platformName").trim().length() > 0) ? System.getProperty("platformName").trim() : (String) ReadProperties.getValue("platformName");
        Integer threadCount = Integer.parseInt((System.getProperty("threadCount") != null && System.getProperty("threadCount").trim().length() > 0) ? System.getProperty("threadCount").trim() : (String) ReadProperties.getValue("threadCount"));

        //Read env values - PI - passwords.
        if (target.equalsIgnoreCase("local")) {
            dotenv = Dotenv.configure()
                    .directory(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
                            + File.separator + "resources" + File.separator + ".env")
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();
        }
        BasePage.setThreadCount(threadCount);
        BasePage.setPlatform(platformName);
        BasePage.setSecret(ReadProperties.readSecrets(target, dotenv));
        BasePage.setTestExecutionLocation(target);
        BasePage.setCountry(country);
        BasePage.setEnvName(env);

        LoggerUtilities.info("\nSelected target : " + BasePage.getTestExecutionLocation() + "\nSelected platform : " + BasePage.getPlatform() + "\nSelected country : " + BasePage.getCountry() + "\nSelected env : " + BasePage.getEnvName() + "\nSelected ThreadCount : " + BasePage.getThreadCount() + "\n");

        switch (target) {
            case "local": {
                if (platformName.equalsIgnoreCase("android")) {
                    //Get the list of available devices.
                    newStartedDevices = devicePicker.getEmulatorsWhichAreON();
                } else {
                    //Get the list of available devices.
                    newStartedDevices = devicePicker.getMappingOfBootedSimulators();
                }
                break;
            }
            case "runner": {
                if (platformName.equalsIgnoreCase("android")) {
                    //Boot up all available android devices equals to thread count.
                    newStartedDevices = devicePicker.setupAndroidDevices();
                    BasePage.wait(20);
                } else {
                    //Boot up all available ios devices equals to thread count.
                    newStartedDevices = devicePicker.setupIOSDevices();
                }
                break;
            }
            case "bs":
                //todo
                break;
            default: {
                LoggerUtilities.error("Invalid target value.");
                System.exit(0);
            }
        }
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() throws IOException, InterruptedException {
        switch (BasePage.getTestExecutionLocation()) {
            case "local": {
                //no need to close the devices.
                break;
            }
            case "runner": {
                for (DevicePicker.Emulator emul : newStartedDevices.values())
                    //Shutdown all the devices.
                    DevicePicker.shutDownDevice(emul.udid);
                break;
            }
            case "bs":
                //todo
                break;
            default: {
                LoggerUtilities.error("Invalid target value.");
                System.exit(0);
            }
        }
//        DevicePicker.stopAppiumServer();
    }

    public void setupLoginProcess(Method method) {
        LoggerUtilities.info("******************************");
        LoggerUtilities.infoLoggerInFileAndReport(">>> Login started for Test case : " + method.getName());
        JSONObject getLoginJson = JsonReader.getNestedJson(testdata, className);
        String user = JsonReader.getNestedString(getLoginJson, "username");
        String password = JsonReader.getNestedString(getLoginJson, "password");
        login = new Login();
        Products products = login.do_login(user, password);
//        Assert.assertEquals(products.get_headerText(), "PRODUCTS", "Login not successful.");
        LoggerUtilities.infoLoggerInFileAndReport(">>> Login completed for Test case : " + method.getName());
    }

    public void unlockAppUsingPassword(){
        JSONObject getLoginJson = JsonReader.getNestedJson(testdata, className);
        String password = JsonReader.getNestedString(getLoginJson, "password");
        //unlock app here
        LoggerUtilities.infoLoggerInFileAndReport(">>> unlockAppUsingPassword started for Test case.");
        LoggerUtilities.infoLoggerInFileAndReport(">>> unlockAppUsingPassword completed for Test case.");
    }

    @BeforeClass(alwaysRun=true)
    public void setUp() throws Exception {
        className = getClass().getSimpleName();
        String platformName = (System.getProperty("platformName") != null && System.getProperty("platformName").trim().length() > 0) ? System.getProperty("platformName").trim() : (String) ReadProperties.getValue("platformName");
        JsonReader jsonReader = new JsonReader();
        testdata = jsonReader.readDataFromJson(getClass().getPackageName().split("\\.")[1]);
        driverFactory = new DriverFactory();
        try{
            switch (BasePage.getTestExecutionLocation()){
                case "local":
                case "runner":
                {
                    if(platformName.equalsIgnoreCase("android") || platformName.equalsIgnoreCase("ios")) {
                        synchronized (lock) {
                            LoggerUtilities.info(Thread.currentThread().getName() + " is performing the task.");
                            BasePage.wait(3);
                            for (Map.Entry<String, DevicePicker.Emulator> entry : newStartedDevices.entrySet()) {
                                if (!entry.getValue().deviceUsed) {
                                    LoggerUtilities.info("Emulator picked : " + entry.getValue().udid);
                                    fireUpEnvVars(entry);
                                    entry.getValue().deviceUsed = true;
                                    break;
                                }
                            }
                            LoggerUtilities.info(Thread.currentThread().getName() + " completed the task.");
                        }
                    }
                    break;
                }
                case "bs":
                    //todo
                    break;
                default:
                {
                    LoggerUtilities.error("Invalid target value.");
                    System.exit(0);
                }
            }

            String appium_host = (System.getProperty("appium_host") != null && System.getProperty("appium_host").trim().length() > 0) ? System.getProperty("appium_host").trim() : (String) ReadProperties.getValue("appium_host");
            String appium_port = (System.getProperty("appium_port") != null && System.getProperty("appium_port").trim().length() > 0) ? System.getProperty("appium_port").trim() : (String) ReadProperties.getValue("appium_port");
            String appiumUrl = "http://" + appium_host + ":" + appium_port;

            String appName = platformName.equalsIgnoreCase("android") ? ReadProperties.getValue("androidAppName") : ReadProperties.getValue("iosAppName");
            appPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator
                    + "resources" + File.separator + "app" + File.separator + BasePage.getEnvName() + File.separator + BasePage.getCountry() + File.separator + appName;
            LoggerUtilities.info("Setting up the baseTest.");
            driverFactory.selectDriver(appPath, appiumUrl, BasePage.getUdid(), null);
        } catch (Exception e)
        {
            ExtentListeners.createTestPreReq("Execution Error", "BaseTest Setup");
            LoggerUtilities.errorLoggerInFileAndReport("Error in setting up the baseTest. Check if appium server started. Exiting. " + e.getMessage());
            beforeClassSetupFailed = true;
//            throw new Exception(e.getMessage());
        }
    }
    boolean firsttime = true;
    boolean recordFlag = true;

    public void startRecordingAndLoginToApp(Method method) {
        if (beforeClassSetupFailed) {
            throw new RuntimeException("Failing test due to @BeforeClass setup failure");
        }
        LoggerUtilities.info("Starting recording of Method : " + method.getName());
        try{
            recordVideo = new RecordVideo();
            recordVideo.startRecording();
        } catch (Exception e) {
            recordFlag = false;
            LoggerUtilities.infoLoggerInFileAndReport("Exception in recording - " + e.getMessage());
        }

        ExtentListeners.createTestPreReq(getClass().getSimpleName(), method.getName());
        if(!getClass().getPackageName().split("\\.")[1].equalsIgnoreCase("login")) {
            if(firsttime || login == null){
                setupLoginProcess(method);
                firsttime = false;
            } else {
                refreshApp(method);
            }
        }
    }

    public void refreshApp(Method method) {
        AppInteractions appInteractions = new AppInteractions();
        LoggerUtilities.infoLoggerInFileAndReport(">>> refreshApp is started for Test case.");
        appInteractions.terminateApp();
        appInteractions.activateApp();
        unlockAppUsingPassword();
        LoggerUtilities.infoLoggerInFileAndReport(">>> refreshApp is completed for Test case.");
    }

    @AfterMethod(alwaysRun=true)
    public void afterMethod(ITestResult result) throws IOException, InterruptedException {
        if(recordFlag && !beforeClassSetupFailed) {
            String timestamp = recordVideo.generateUniqueFolderName();
            String path = "videos" + File.separator + BasePage.getPlatform() + "_" + BasePage.getUdid()
                    + File.separator + timestamp + File.separator + result.getTestClass().getRealClass().getSimpleName();
            recordVideo.stopRecording(result, path);
        }
        LoggerUtilities.info("*** Test Case execution DONE : " + result.getName() + "\n");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown(){
        driverFactory.tearDown();
    }
}
