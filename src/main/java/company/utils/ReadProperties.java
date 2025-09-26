package company.utils;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;

/**
 * ReadProperties class provides methods for reading properties from a configuration file and secrets.
 * It includes methods for reading the configuration file, getting a value from the configuration, and reading secrets.
 */
public class ReadProperties {

    /**
     * This is the constructor for the ReadProperties class.
     */
    public ReadProperties() {
    }

    private static final Properties properties = new Properties();
    private static final Map<String, String> configMap = new HashMap<>();

    /**
     * This method is used to read the configuration file and store the properties in a map.
     */
    public static void readConfigFile(){
        String configPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "config.properties";
        try (FileInputStream file = new FileInputStream(configPath)) {
            properties.load(file);
            properties.forEach((key, value) -> configMap.put(String.valueOf(key).trim(), String.valueOf(value).trim()));
        } catch (IOException e) {
            e.printStackTrace();
            LoggerUtilities.error("Config read error.");
            System.exit(0);
        }
    }

    /**
     * This method is used to get a value from the configuration map.
     * @param key This is the key of the property to get.
     * @return String This returns the value of the property.
     * @throws RuntimeException If the key is not found in the configuration map, a RuntimeException is thrown.
     */
    public static String getValue(String key) {

        if (Objects.isNull(key) || Objects.isNull(configMap.get(key))) {
            throw new RuntimeException("Property with Key => " + key + " is not found!!! Please check the respective env.properties file...");
        }

        return configMap.get(key);
    }

  /**
     * This method is used to read secrets from either the dotenv or system properties.
     * @param local This indicates whether to read from dotenv (if true) or system properties (if false).
     * @param dotenv This is the dotenv to read from if local is true.
     * @return Function This returns a function that takes a key and returns the corresponding secret.
     */
    public static Function<String, String> readSecrets(String local, Dotenv dotenv){
        Properties systemProperties = System.getProperties();
        return local.equalsIgnoreCase("local") ? dotenv::get : systemProperties::getProperty;
    }

}