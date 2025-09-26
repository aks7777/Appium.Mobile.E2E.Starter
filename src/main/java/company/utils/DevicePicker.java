package company.utils;
import org.json.JSONArray;
import org.json.JSONObject;
import company.base.BasePage;

import java.io.*;
import java.util.*;

/**
 * This class is used to manage and interact with emulators.
 */
public class DevicePicker {

    /**
     * This method is used to get a list of all emulators.
     * @return List<String> This returns a list of all emulators.
     * @throws IOException On input error.
     */
    public Map<String, Emulator> getEmulatorsWhichAreON() throws IOException {
        //map deviceName and emulatorName in hashmap.
        Map<String, Emulator> map = new HashMap<>();
        Process process = Runtime.getRuntime().exec("adb devices");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("emulator")) {
                String udid = line.split("\\s+")[0];
                String deviceName = getDeviceName(udid);
                boolean isDeviceInUseFlag = checkIfDeviceInUse(udid);
                map.put(deviceName, new Emulator(udid, isDeviceInUseFlag));
            }
        }
        return map;
    }

    public static String getDeviceName(String emulatorName) throws IOException {
        Process process = Runtime.getRuntime().exec("adb -s " + emulatorName + " shell getprop ro.boot.qemu.avd_name");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        return reader.readLine();
    }

    /**
     * This method is used to get a list of devices in use.
     * @param devicesAvailable This is the list of available devices.
     * @return List<Emulator> This returns a list of emulators not in use.
     * @throws IOException On input error.
     */
    public List<Emulator> getDeviceNotInUse(List<String> devicesAvailable) throws IOException {
        String appium_host = (System.getProperty("appium_host") != null && System.getProperty("appium_host").trim().length() > 0) ? System.getProperty("appium_host").trim() : (String) ReadProperties.getValue("appium_host");
        String appium_port = (System.getProperty("appium_port") != null && System.getProperty("appium_port").trim().length() > 0) ? System.getProperty("appium_port").trim() : (String) ReadProperties.getValue("appium_port");
        String appiumUrl = "http://" + appium_host + ":" + appium_port;

        Process process = Runtime.getRuntime().exec("curl " + appiumUrl + "/sessions");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            LoggerUtilities.info(line);
            JSONObject jsonObject = new JSONObject(line);
            JSONArray jsonArray = jsonObject.getJSONArray("value");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject session = jsonArray.getJSONObject(i);
                String deviceInUse = (String) session.getJSONObject("capabilities").get("udid");
                LoggerUtilities.info("Device in use: " + deviceInUse);
                devicesAvailable.remove(deviceInUse);
            }
        }
        List<Emulator> listEmulatorsNotInUse = new ArrayList<>();

        for (String device : devicesAvailable)
        {
            listEmulatorsNotInUse.add(new Emulator(device, false));
        }
        return listEmulatorsNotInUse;
    }

    public boolean checkIfDeviceInUse(String udidTemp) throws IOException {
        String appium_host = (System.getProperty("appium_host") != null && System.getProperty("appium_host").trim().length() > 0) ? System.getProperty("appium_host").trim() : (String) ReadProperties.getValue("appium_host");
        String appium_port = (System.getProperty("appium_port") != null && System.getProperty("appium_port").trim().length() > 0) ? System.getProperty("appium_port").trim() : (String) ReadProperties.getValue("appium_port");
        String appiumUrl = "http://" + appium_host + ":" + appium_port;

        Process process = Runtime.getRuntime().exec("curl " + appiumUrl + "/sessions");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.contains("[]"))
                LoggerUtilities.info(line);
            JSONObject jsonObject = new JSONObject(line);
            JSONArray jsonArray = jsonObject.getJSONArray("value");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject session = jsonArray.getJSONObject(i);
                String deviceInUse = (String) session.getJSONObject("capabilities").get("udid");
                if (udidTemp.equalsIgnoreCase(deviceInUse))
                {
                    LoggerUtilities.info("Device in use: " + deviceInUse);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This class represents an emulator.
     */
    public class Emulator {
        public String udid;
        public boolean deviceUsed;
        public Emulator(String udid, Boolean deviceUsed) {
            this.udid = udid;
            this.deviceUsed = deviceUsed;
        }
    }

    // Method to retrieve a list of available emulators
    private static List<String> getListOfAllEmulators() throws IOException {
        List<String> emulatorList = new ArrayList<>();
        String command = "emulator -list-avds";
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.contains("INFO"))
                emulatorList.add(line);
        }
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (emulatorList.isEmpty()) {
            LoggerUtilities.info("No emulators found. Please create an AVD first.");
        }
        LoggerUtilities.info("Available Emulators:");
        for (int i = 0; i < emulatorList.size(); i++) {
            LoggerUtilities.info((i + 1) + ". " + emulatorList.get(i));
        }
        return emulatorList;
    }

    /**
     * This method is used to start a specific emulator.
     * @param emulatorName This is the name of the emulator to be started.
     * @throws IOException On input error.
     */
private static String startEmulator(String emulatorName) throws IOException, InterruptedException {
    String emulatorPath = System.getenv("ANDROID_HOME") + "/emulator/emulator";
    String[] command = {emulatorPath, "-avd", emulatorName, "-no-snapshot-save", "-no-boot-anim", "-wipe-data"};
    LoggerUtilities.info("Running emulator command: " + String.join(" ", command));
    ProcessBuilder pb = new ProcessBuilder(command);
    pb.redirectErrorStream(true);
    pb.start();

    // Poll every 5 seconds to check if it appears in 'adb devices'
    String udid = null;
    int maxTries = 60; // 5 minutes
    while (maxTries-- > 0) {
        Process adbList = Runtime.getRuntime().exec("adb devices");
        BufferedReader reader = new BufferedReader(new InputStreamReader(adbList.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("emulator-")) {
                String potentialUdid = line.split("\\s+")[0];
                String avdNameFromUdid = getDeviceName(potentialUdid);
                if (emulatorName.equalsIgnoreCase(avdNameFromUdid)) {
                    udid = potentialUdid;
                    LoggerUtilities.info("Found matching emulator udid: " + udid);
                    break;
                }
            }
        }
        if (udid != null) break;
        BasePage.wait(5);
    }

    if (udid == null) {
        LoggerUtilities.error("Failed to detect emulator " + emulatorName + " in adb devices");
        return null;
    }

    // Check boot status
    int bootTries = 60;
    while (bootTries-- > 0) {
        Process bootCheck = Runtime.getRuntime().exec("adb -s " + udid + " shell getprop sys.boot_completed");
        BufferedReader reader = new BufferedReader(new InputStreamReader(bootCheck.getInputStream()));
        String bootFlag = reader.readLine();
        if ("1".equals(bootFlag != null ? bootFlag.trim() : null)) {
            LoggerUtilities.info("Emulator " + udid + " booted!");
            return udid;
        }
        BasePage.wait(15);
    }

    LoggerUtilities.error("Emulator " + emulatorName + " failed to boot in time.");
    return null;
}



    private static void startSimulator(String simulatorName) throws IOException {
        String command = "xcrun simctl boot " + simulatorName;
        Process process = Runtime.getRuntime().exec(command);
        LoggerUtilities.info("Simulator " + simulatorName + " is starting...");
    }

    /**
     * This method is used to shut down a specific device.
     * @param udid This is the unique identifier of the device to be shut down.
     */
    public static void shutDownDevice(String udid) {
        try {
            String command = "";
            if (BasePage.getPlatform().equalsIgnoreCase("ios"))
                command = "xcrun simctl shutdown " + udid;
            else if (BasePage.getPlatform().equalsIgnoreCase("android"))
                command = "adb -s " + udid + " emu kill";

            Process process = Runtime.getRuntime().exec(command);
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                    LoggerUtilities.info(line); // Print output
            }
            process.waitFor();
            LoggerUtilities.info("Device " + udid + " has been stopped.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to check if a specific emulator has booted up.
     * @param newEmulatorId This is the unique identifier of the emulator to be checked.
     * @return boolean This returns true if the emulator has booted up, false otherwise.
     * @throws IOException On input error.
     */
    private static boolean isEmulatorBooted(String newEmulatorId) throws IOException {
        String cmdArray = "adb -s " + newEmulatorId + " shell getprop sys.boot_completed";
        Process process = Runtime.getRuntime().exec(cmdArray);

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().equals("1")) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method is used to set up Android devices for testing.
     * It retrieves a list of available emulators, starts the necessary number of emulators based on the thread count,
     * and waits for the emulators to boot up before proceeding.
     * If an emulator is already running, it will not be started again.
     * @throws IOException On input error.
     * @throws InterruptedException If the thread sleep is interrupted.
     */
public Map<String, Emulator> setupAndroidDevices() {
    Map<String, Emulator> finalEmulatorUdidsWhichAreON = new HashMap<>();
    try {
        List<String> listOfAndroidDevices = getListOfAllEmulators();
        Map<String, Emulator> initialEmulatorUdidsWhichAreON = getEmulatorsWhichAreON();

        LoggerUtilities.info("Available AVDs: " + listOfAndroidDevices);
        LoggerUtilities.info("Already running AVDs: " + initialEmulatorUdidsWhichAreON.keySet());

        int thread_count = BasePage.getThreadCount();
        LoggerUtilities.info("Thread count: " + thread_count);

        for (String avdName : listOfAndroidDevices) {
            if (thread_count <= 0) break;

            if (avdName.toLowerCase().contains("skip")) {
                LoggerUtilities.info("Skipping device: " + avdName);
                continue;
            }

            if (initialEmulatorUdidsWhichAreON.containsKey(avdName)) {
                LoggerUtilities.info("Device already running: " + avdName);
                continue;
            }

            LoggerUtilities.info("Starting emulator: " + avdName);
            String startedUdid = startEmulator(avdName);

            if (startedUdid != null) {
                finalEmulatorUdidsWhichAreON.put(avdName, new Emulator(startedUdid, false));
                LoggerUtilities.info("Emulator started: " + startedUdid + " (" + avdName + ")");
                thread_count--;
            } else {
                LoggerUtilities.error("Failed to start emulator: " + avdName);
            }
        }

        if (finalEmulatorUdidsWhichAreON.isEmpty()) {
            LoggerUtilities.error("No emulators started. Please check AVD configuration or timeout.");
        }

    } catch (IOException | InterruptedException e) {
        LoggerUtilities.error("Exception in setupAndroidDevices: " + e.getMessage());
        e.printStackTrace();
    }

    return finalEmulatorUdidsWhichAreON;
}



    public Map<String, Emulator> setupIOSDevices() {
        Map<String, Emulator> finalSimulatorUdidsWhichAreON = null;
        try {
            Map<String, Emulator> listOfIPhoneDevices = getMappingOfAllAvailableSimulators();
            // Get the list of running emulators before starting the new one
            Map<String, Emulator> initialSimulatorUdidsWhichAreON = getMappingOfBootedSimulators();
            int k = 1;
            LoggerUtilities.info("Existing Booted Simulators : ");
            for (Map.Entry<String, Emulator> entry : initialSimulatorUdidsWhichAreON.entrySet()) {
                LoggerUtilities.info(k + ") " + entry.getKey() + " : " + entry.getValue().udid);
                k++;
            }
            //map deviceName and emulatorName in hashmap.
            List<String> listOfAvailableSimulators = new ArrayList<>();
            for (Emulator simulator : listOfIPhoneDevices.values()){
                listOfAvailableSimulators.add(simulator.udid);
            }
            List<String> initialSimulatorUdidsWhichAreONList = new ArrayList<>();
            for (Emulator simulator : initialSimulatorUdidsWhichAreON.values()){
                initialSimulatorUdidsWhichAreONList.add(simulator.udid);
            }
            if (!listOfAvailableSimulators.isEmpty()) {
                int thread_count = BasePage.getThreadCount();
                LoggerUtilities.info("Thread count: " + thread_count);
                // Automatically choose the first emulator to start
                for (int i = 0; i < listOfAvailableSimulators.size() && thread_count > 0; i++)
                {
                   String iphoneUdid = listOfAvailableSimulators.get(i);
                   if (initialSimulatorUdidsWhichAreONList.contains(iphoneUdid)){
                        LoggerUtilities.info("Device is already UP and running : " + iphoneUdid);
                        continue;
                    }else {
                        LoggerUtilities.info("Starting simulator: " + iphoneUdid);
                        startSimulator(iphoneUdid);
                        thread_count--;
                    }
                    BasePage.wait(5);
                    LoggerUtilities.info("New simulator(s) started : " + iphoneUdid);
                    // Wait for the emulator to boot
                    LoggerUtilities.info("Waiting for Simulator to boot : " + iphoneUdid);
                    boolean booted = false;
                    int timeout = Integer.parseInt(ReadProperties.getValue("wdaLaunchTimeout"));
                    while (!booted && timeout > 0) {
                        BasePage.wait(5);  // Wait for 5 seconds before checking again
                        booted = isSimulatorBooted(iphoneUdid);
                        timeout -= 5;
                    }
                    if (booted)
                        LoggerUtilities.info("Emulator booted successfully!");
                    else
                        LoggerUtilities.error("Emulator failed to boot!");
                }
            }
            finalSimulatorUdidsWhichAreON = getMappingOfBootedSimulators();
            if (finalSimulatorUdidsWhichAreON != null)
                finalSimulatorUdidsWhichAreON.keySet().removeAll(initialSimulatorUdidsWhichAreON.keySet());

            k = 1;
            LoggerUtilities.info("New Booted Simulators : ");
            for (Map.Entry<String, Emulator> entry : finalSimulatorUdidsWhichAreON.entrySet()) {
                LoggerUtilities.info(k + ") " + entry.getKey() + " : " + entry.getValue().udid);
                k++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return finalSimulatorUdidsWhichAreON;
    }
    public Map<String, Emulator> getMappingOfAllAvailableSimulators() {
        return getMappingOfSimulators("all", null);
    }

    public Map<String, Emulator> getMappingOfBootedSimulators() {
        return getMappingOfSimulators("booted", null);
    }

    public boolean isSimulatorBooted(String simulatorUdid) {
        Map<String, Emulator> map = getMappingOfSimulators("booted", simulatorUdid);
        if (map.isEmpty())
            return false;
        else
            return true;
    }

    public Map<String, Emulator> getMappingOfSimulators(String isBooted, String udid) {
        Map<String, Emulator> mappingAllSimulatorsWithUdid = new HashMap<>();
        Map<String, Emulator> isDeviceBooted = new HashMap<>();
        Map<String, Emulator> mappingBootedSimulatorsWithUdid = new HashMap<>();
        try {
            Process process = Runtime.getRuntime().exec("xcrun simctl list devices");
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            boolean hasOutput = false;
            String previousLineOSVersion = null;
            while ((line = reader.readLine()) != null) {
                if (line.contains("-- iOS")) {
                    previousLineOSVersion = line.split(" ")[2];
                }
                if ((line.contains("iPhone") && !line.contains("unavailable")) || line.contains("(Booted)")) {
                    String udidTemp = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
                    if (udidTemp.contains("generation")) {
                        String[] parts = line.split("\\)");
                        // Check if the second part exists and has the UUID in it
                        if (parts.length > 1) {
                            // Trim the second part and extract the content between parentheses
                            udidTemp = parts[1].substring(parts[1].lastIndexOf('(') + 1);
                        }
                    }
                    boolean isDeviceInUseFlag = checkIfDeviceInUse(udidTemp);
                    if (line.contains("(Booted)") && isBooted.equalsIgnoreCase("booted")) {
                        mappingBootedSimulatorsWithUdid.put(line.substring(line.indexOf("i"), line.indexOf("(") - 1) + "_" + previousLineOSVersion, new Emulator(udidTemp, isDeviceInUseFlag));
                    } else {
                        mappingAllSimulatorsWithUdid.put(line.substring(line.indexOf("i"), line.indexOf("(") - 1) + "_" + previousLineOSVersion, new Emulator(udidTemp, isDeviceInUseFlag));
                    }
                    hasOutput = true;
                    //isDeviceBooted checker.
                    if (udid != null)
                    {
                        if (line.contains("Booted") && isBooted.equalsIgnoreCase("booted") && line.contains(udid)) {
                            isDeviceBooted.put(line.substring(line.indexOf("i"), line.indexOf("(") - 1) + "_" + previousLineOSVersion, new Emulator(udidTemp, isDeviceInUseFlag));
                            break;
                        }
                    }
                }
            }
            // If no output, check for errors
            if (!hasOutput) {
                LoggerUtilities.info("No simulators found. Checking for errors...");
                while ((line = errorReader.readLine()) != null) {
                    System.err.println("Error: " + line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isBooted.equalsIgnoreCase("booted") && udid == null) {
            return mappingBootedSimulatorsWithUdid;
        } else if (isBooted.equalsIgnoreCase("all") && udid == null) {
            LoggerUtilities.info("All available Simulators : ");
            int k = 1;
            for (Map.Entry<String, Emulator> entry : mappingAllSimulatorsWithUdid.entrySet()) {
                LoggerUtilities.info(k + ") " + entry.getKey() + " : " + entry.getValue().udid);
                k++;
            }
            return mappingAllSimulatorsWithUdid;
        } else if (udid != null) {
            return isDeviceBooted;
        }
        return null;
    }

    public void startAppiumServer() throws IOException {
        String appium_host = (System.getProperty("appium_host") != null && System.getProperty("appium_host").trim().length() > 0) ? System.getProperty("appium_host").trim() : (String) ReadProperties.getValue("appium_host");
        String appium_port = (System.getProperty("appium_port") != null && System.getProperty("appium_port").trim().length() > 0) ? System.getProperty("appium_port").trim() : (String) ReadProperties.getValue("appium_port");

        String appServer = "appium -a " + appium_host + " -p " + appium_port;
        Process process = Runtime.getRuntime().exec(appServer);

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().contains("Appium REST http interface listener started on")) {
                LoggerUtilities.info("***** Started : " + line);
                break;
            }
        }
    }

    public static void stopAppiumServer() throws IOException {
        String appServer = "pkill -9 -f appium";
        Process process = Runtime.getRuntime().exec(appServer);
        LoggerUtilities.info("Appium server is stopped. Thank you.");
    }
}
