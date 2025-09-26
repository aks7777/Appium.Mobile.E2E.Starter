package company.utils;

import io.appium.java_client.InteractsWithApps;
import company.base.BasePage;

import java.io.IOException;
import java.util.HashMap;

/**
 * AppInteractions class provides methods for interacting with the application.
 * It includes methods for removing, installing, and activating the application.
 */
public class AppInteractions {

    /**
     * This method is used to remove the application.
     * @return boolean This returns whether the application was successfully removed.
     */
    public boolean removeApp()
    {
        boolean isRemoved = false;
        if (BasePage.getPlatform().equalsIgnoreCase("android"))
            isRemoved = ((InteractsWithApps) BasePage.getDriver()).removeApp(ReadProperties.getValue("androidPackage"));
        else
            isRemoved = ((InteractsWithApps) BasePage.getDriver()).removeApp(ReadProperties.getValue("iosBundleId"));
        return isRemoved;
    }

    /**
     * This method is used to install the application.
     * @param appPath This is the path to the application to install.
     */
    public void installApp(String appPath){
        ((InteractsWithApps) BasePage.getDriver()).installApp(appPath);
    }

    /**
     * This method is used to activate the application.
     */
    public void activateApp()
    {
        if (BasePage.getPlatform().equalsIgnoreCase("android"))
            ((InteractsWithApps) BasePage.getDriver()).activateApp(ReadProperties.getValue("androidPackage"));
        else
            ((InteractsWithApps) BasePage.getDriver()).activateApp(ReadProperties.getValue("iosBundleId"));
    }

    public void autoGrantNotificationPopup(String deviceUdid) throws IOException {
        if(BasePage.getPlatform().equalsIgnoreCase("android")){
            BasePage.wait(3);
            String packageName = ReadProperties.getValue("androidPackage");
            Runtime.getRuntime().exec("adb -s " + deviceUdid + " shell pm grant " + packageName + " android.permission.POST_NOTIFICATIONS");
        }
    }

    public void terminateApp(){
        HashMap<String, Object> args = new HashMap<>();
        if(BasePage.getPlatform().equalsIgnoreCase("android")){
            args.put("appId", ReadProperties.getValue("androidPackage"));
        } else if(BasePage.getPlatform().equalsIgnoreCase("ios")){
            args.put("bundleId", ReadProperties.getValue("iosBundleId"));
        }
        BasePage.getDriver().executeScript("mobile: terminateApp", args);
        LoggerUtilities.info(">>> App terminated.");
    }

    public void killAppAndOpenApp() {
        terminateApp();
        activateApp();
    }
}