package company.utils;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import company.base.BasePage;

import java.util.HashMap;
import java.util.Map;

/**
 * IOSGestures class provides methods for interacting with elements on iOS devices.
 * It includes methods for scrolling and clicking on an element.
 */
public class IOSGestures {
    /**
     * This method is used to scroll and click on a given element.
     * If the element is not found, it will scroll down and retry up to 10 times.
     * @param element This is the element to scroll to and click on.
     */
    public static void scrollAndClick(WebElement element) {
        int retryCount = 0;
        while (retryCount < 10) {  // Limit retries to avoid infinite loop
            try {
                element.click();
                System.out.println("Clicked on the element: " + element);
                LoggerUtilities.infoLoggerInFileAndReport("Scroll and click success : " + element);
                break;
            } catch (NoSuchElementException e) {
                // Scroll down
                System.out.println("Scrolling down...");
                scrollDown();
                retryCount++;
            }
        }
    }

    /**
     * This method is used to scroll down on the screen.
     * It uses the 'mobile: scroll' script with the direction set to 'down'.
     */
    public static void scrollDown() {
        Map<String, Object> params = new HashMap<>();
        params.put("direction", "down");
        BasePage.getDriver().executeScript("mobile: scroll", params);
    }
}
