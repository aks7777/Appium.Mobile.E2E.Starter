package company.utils;

import org.json.JSONObject;
import org.json.JSONTokener;
import company.base.BasePage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * JsonReader class provides methods for reading data from a JSON file.
 * It includes methods for reading data from a JSON file, and getting nested String, Integer, and Boolean values.
 */
public class JsonReader {
    JSONObject jsonData;

    /**
     * This method is used to read data from a JSON file.
     * @param modulename This is the name of the module for which the test data is required.
     * @return JSONObject This returns the JSON data read from the file.
     * @throws IOException If an input or output exception occurred
     */
    public JSONObject readDataFromJson(String modulename) throws IOException {
        InputStream inputStream = null;
        try {
            //TBD - Pass testData Path as a param
            String testData = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
                    + File.separator + "resources" + File.separator + "testdata" + File.separator
                    + BasePage.getEnvName() + File.separator + BasePage.getCountry() + File.separator + modulename + File.separator + "testdata.json";
            inputStream = new FileInputStream(testData);
            JSONTokener jsonTokener = new JSONTokener(inputStream);
            jsonData = new JSONObject(jsonTokener);
            LoggerUtilities.info("Test Data read complete.");
        } catch(Exception e) {
            e.printStackTrace();
            LoggerUtilities.error("Error in reading login.json input data.");
            throw e;
        } finally {
            if(inputStream != null) {
                inputStream.close();
            }
        }
        return jsonData;
    }

    public static String getNestedString(JSONObject jsonObject, String... keys) {
        //todo : refactor and use 1 loop.
        for (int i = 0; i < keys.length - 1; i++) {
            for (String objKey : jsonObject.keySet()) {
                if (keys[i].equalsIgnoreCase(objKey)) {
                    jsonObject = jsonObject.getJSONObject(objKey);
                    break;
                }
            }
        }
        return jsonObject.getString(keys[keys.length - 1]);
    }


    public static JSONObject getNestedJson(JSONObject jsonData, String key) {
        JSONObject testDataTemp = new JSONObject(jsonData.toString());
        for (String objKey : testDataTemp.keySet()) {
                if (key.equalsIgnoreCase(objKey)) {
                    testDataTemp = testDataTemp.getJSONObject(objKey);
                    return testDataTemp;
                }
            }
        return null;
    }


    /**
     * This method is used to get a nested Integer value from a JSON object.
     * @param jsonObject This is the JSON object to get the nested Integer value from.
     * @param keys This is the sequence of keys to get the nested Integer value.
     * @return int This returns the nested Integer value.
     */
    public static int getNestedInt(JSONObject jsonObject, String... keys) {
        for (int i = 0; i < keys.length - 1; i++) {
            jsonObject = jsonObject.getJSONObject(keys[i]);
        }
        return jsonObject.getInt(keys[keys.length - 1]);
    }

    /**
     * This method is used to get a nested Boolean value from a JSON object.
     * @param jsonObject This is the JSON object to get the nested Boolean value from.
     * @param keys This is the sequence of keys to get the nested Boolean value.
     * @return boolean This returns the nested Boolean value.
     */
    public static boolean getNestedBoolean(JSONObject jsonObject, String... keys) {
        for (int i = 0; i < keys.length - 1; i++) {
            jsonObject = jsonObject.getJSONObject(keys[i]);
        }
        return jsonObject.getBoolean(keys[keys.length - 1]);
    }

    public static String toCamelCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String[] words = input.trim().split("\\s+");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i].toLowerCase();  // make everything lowercase first

            if (i == 0) {
                result.append(word);  // first word in lowercase
            } else {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1));
            }
        }

        return result.toString();
    }
}
