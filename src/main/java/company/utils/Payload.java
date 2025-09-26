package company.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class Payload {

    /**
     * Extracts the keyword value from a json file
     *
     * @param path the path of the JSON file to be read
     * @param keyword key name of the value to retrieve
     * @return the key value as a string
     * @throws IOException if the specified JSON file cannot be found
     */
    public static String getJsonValue(String path, String keyword) {
        String value = null;
        try {
            File file = new File("src/test/resources/" + path);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(file);
            value = rootNode.get(keyword).asText();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * Loads the template of request payload from src/test/resources/templates folder
     * @param templateName path of filename
     * @return instance of Template
     */
    public Template getPayloadTemplate(String templateName) {
        return getPayloadTemplate(templateName, "");
    }

    /**
     * Loads the template of request payload from src/test/resources/templates folder
     * @param templateName path of filename
     * @param templatePath path of directory where the file is located
     * @return instance of Template
     */
    public Template getPayloadTemplate(String templateName, String templatePath) {
        //Load the template file
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
        configuration.setClassForTemplateLoading(Payload.class, "/templates/" + templatePath);
        Template template = null;

        try {
            template = configuration.getTemplate(templateName);
        } catch (IOException e) {
            Assert.fail("Error occurred while getting " + templateName + " file: " + e);
        }

        return template;
    }

    /**
     * Returns the request payload created with proper values for the keys
     *
     * @param valueTemplate  contains values to be added to the template of payload
     * @param template   template for payload
     * @return content of payload that can be added as request body
     */
    public String getPayload(Map<String, Object> valueTemplate, Template template) {
        StringWriter stringWriter = new StringWriter();
        try {
            template.process(valueTemplate, stringWriter);
        } catch (TemplateException e) {
            Assert.fail("Error occurred while processing template : " + e);
        } catch (IOException e) {
            Assert.fail("Error occurred while writing template : " + e);
        }
        return stringWriter.toString();
    }

    public String getPayload(Template template) {
        StringWriter stringWriter = new StringWriter();
        try {
            template.dump(stringWriter);
        } catch (IOException e) {
            Assert.fail("Error occurred while writing template : " + e);
        }
        return stringWriter.toString();
    }
}
