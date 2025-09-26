package company.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import company.base.BasePage;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebElement;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;

import static java.time.Duration.ofMillis;
import static org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT;
import static org.openqa.selenium.interactions.PointerInput.Origin.viewport;

/**
 * CommonGestures class provides methods for performing common gestures on the application.
 * It includes methods for scrolling, swiping, tapping, and long tapping.
 */
public class CommonGestures {
    static double SCROLL_RATIO = 0.6;
    static Duration SCROLL_DUR = Duration.ofMillis(700);

    /**
     * ScrollDirection enum represents the direction of scrolling.
     */
    public enum ScrollDirection {
        UP, DOWN, LEFT, RIGHT
    }

    /**
     * This method checks if the current page source is not the end of the page.
     * @return boolean This returns whether the current page source is not the end of the page.
     */
    private static boolean isNotEndOfPage(String previousPageSource) {
        return !previousPageSource.equals(BasePage.getDriver().getPageSource());
    }

    /**
     * This method scrolls down and clicks on the given element.
     * @param element This is the element to click on.
     * @param message This is the message to log.
     */
    public static void scrollDownAndClick(WebElement element, String message) {
        String previousPageSource = "";
        boolean clickflag = false;

        while (isNotEndOfPage(previousPageSource)) {
            previousPageSource = BasePage.getDriver().getPageSource();
            try {
                element.click();
                LoggerUtilities.infoLoggerInFileAndReport("Scroll and click success : " + element);
                clickflag = true;
                break;

            } catch (NoSuchElementException e) {
                scroll(ScrollDirection.DOWN, SCROLL_RATIO);

            }
        }
        if(!isNotEndOfPage(previousPageSource) && !clickflag) {
            throw new java.util.NoSuchElementException();
        }
    }

    public static void scrollDownAndClick(WebElement element, String message, boolean extraScroll, double scrollRatio) {
        String previousPageSource = "";
        boolean clickflag = false;
        while (isNotEndOfPage(previousPageSource)) {
            previousPageSource = BasePage.getDriver().getPageSource();
            try {
                if(element.isDisplayed() && extraScroll)
                {
                    swipeDirection("down", scrollRatio);
                    element.click();
                }
                LoggerUtilities.infoLoggerInFileAndReport("Scroll and click success : " + element);
                clickflag = true;
                break;
            } catch (NoSuchElementException e) {
                swipeDirection("down", scrollRatio);
            }
        }
        if(!isNotEndOfPage(previousPageSource) && !clickflag) {
            throw new java.util.NoSuchElementException();
        }
    }


    public static void scrollAndClickAmongTwoElements(WebElement primaryElement, WebElement secondaryElement, String message) {
        String previousPageSource = "";
        boolean clickSuccessful = false;

        while (isNotEndOfPage(previousPageSource)) {
            previousPageSource = BasePage.getDriver().getPageSource();

            if (tryClick(primaryElement, message) || tryClick(secondaryElement, message)) {
                clickSuccessful = true;
                break;
            }
            scroll(ScrollDirection.DOWN, SCROLL_RATIO);
        }

        if (!clickSuccessful) {
            throw new java.util.NoSuchElementException("Could not find clickable element: " + message);
        }
    }

    public static void scrollAndClickAmongTwoElements(WebElement primaryElement, WebElement secondaryElement, String message, double scrollRatio) {
        String previousPageSource = "";
        boolean clickSuccessful = false;

        while (isNotEndOfPage(previousPageSource)) {
            previousPageSource = BasePage.getDriver().getPageSource();

            boolean primaryVisible = false;
            boolean secondaryVisible = false;

            try {
                primaryVisible = primaryElement.isDisplayed();
            } catch (Exception ignored) {}

            try {
                secondaryVisible = secondaryElement.isDisplayed();
            } catch (Exception ignored) {}

            if (primaryVisible || secondaryVisible) {
                swipeDirection("down", scrollRatio);
            }

            if (tryClick(primaryElement, message) || tryClick(secondaryElement, message)) {
                clickSuccessful = true;
                break;
            }

            swipeDirection("down", scrollRatio);
        }

        if (!clickSuccessful) {
            throw new java.util.NoSuchElementException("Could not find clickable element: " + message);
        }
    }

    private static boolean tryClick(WebElement element, String message) {
        try {
            if (element != null && element.isDisplayed()) {
                swipeDirection("down", 0.4); //do a very small scroll to avoid AI Assistant click.
                element.click();
                LoggerUtilities.infoLoggerInFileAndReport("Scroll and click success: " + message);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static void scrollUpAndClick(WebElement element) {
        String previousPageSource = "";
        boolean clickflag = false;

        while (isNotEndOfPage(previousPageSource)) {
            previousPageSource = BasePage.getDriver().getPageSource();
            try {
                element.click();
                LoggerUtilities.infoLoggerInFileAndReport("Scroll and click success : " + element);
                clickflag = true;
                break;
            } catch (NoSuchElementException e) {
                scroll(ScrollDirection.UP, SCROLL_RATIO);
            }
        }
        if(!isNotEndOfPage(previousPageSource) && !clickflag) {
            throw new java.util.NoSuchElementException();
        }
    }

    /**
     * This method scrolls down until the given element is visible.
     * @param element This is the element to make visible.
     * @param message This is the message to log.
     */
    public static void scrollDownUntilElementIsVisible(WebElement element, String message) {
        String previousPageSource = "";
        boolean clickflag = false;

        while (isNotEndOfPage(previousPageSource)) {
            previousPageSource = BasePage.getDriver().getPageSource();
            try {
                element.isDisplayed();
                LoggerUtilities.infoLoggerInFileAndReport("Element displayed success : " + element);
                clickflag = true;
                break;
            } catch (NoSuchElementException e) {
                scroll(ScrollDirection.DOWN, SCROLL_RATIO);
            }
        }

        if(!isNotEndOfPage(previousPageSource) && !clickflag) {
            throw new java.util.NoSuchElementException();
        }
    }

    /**
     * This method scrolls up until the given element is visible.
     * @param element This is the element to make visible.
     */
    public static void scrollUpUntilElementIsVisible(WebElement element) {
        String previousPageSource = "";
        boolean clickflag = false;

        while (isNotEndOfPage(previousPageSource)) {
            previousPageSource = BasePage.getDriver().getPageSource();
            try {
                element.isDisplayed();
                LoggerUtilities.infoLoggerInFileAndReport("Element displayed success : " + element);
                clickflag = true;
                break;
            } catch (NoSuchElementException e) {
                scroll(ScrollDirection.UP, SCROLL_RATIO);
            }
        }

        if(!isNotEndOfPage(previousPageSource) && !clickflag) {
            throw new java.util.NoSuchElementException();
        }
    }
    public static void scrollUpUntilElementIsVisible(WebElement element,double scrollRatio) {
        String previousPageSource = "";
        boolean clickflag = false;

        while (isNotEndOfPage(previousPageSource)) {
            previousPageSource = BasePage.getDriver().getPageSource();
            try {
                element.isDisplayed();
                LoggerUtilities.infoLoggerInFileAndReport("Element displayed success : " + element);
                clickflag = true;
                break;
            } catch (NoSuchElementException e) {
               swipeDirection("up", scrollRatio);
            }
        }

        if(!isNotEndOfPage(previousPageSource) && !clickflag) {
            throw new java.util.NoSuchElementException();
        }
    }

    /**
     * This method scrolls until the end of the page.
     */
    public static void scrollUntilEndOfPage() {
        String previousPageSource = "";

        while (isNotEndOfPage(previousPageSource)) {
            previousPageSource = BasePage.getDriver().getPageSource();
            try {
                scroll(ScrollDirection.DOWN, SCROLL_RATIO);
                LoggerUtilities.infoLoggerInFileAndReport("Scrolling until end of page");
            } catch (Exception e) {
                LoggerUtilities.info(e.getMessage());
            }
        }
    }

    public static void scrollWithCount(int count, double scrollRatio) {
        int startNum = 0;
        while (startNum < count) {
            try {
                swipeDirection("down", scrollRatio);
                LoggerUtilities.infoLoggerInFileAndReport("Scroll " + ++startNum + "/" + count);
            } catch (Exception e) {
                LoggerUtilities.info(e.getMessage());
            }
        }
    }

    /**
     * This method swipes in the given direction.
     * @param direction This is the direction to swipe in.
     * @param scrollRatio This is the scroll ratio.
     */

    public static void swipeDirection(String direction, double scrollRatio) {
        if (scrollRatio <= 0)
        {
            scrollRatio = SCROLL_RATIO;
        }
        Dimension size = BasePage.getDriver().manage().window().getSize();
        Point midPoint = new Point((int) (size.width * 0.4), (int) (size.height * 0.4));
        int bottom = midPoint.y + (int) (midPoint.y * scrollRatio);
        int top = midPoint.y - (int) (midPoint.y * scrollRatio);
        int left = midPoint.x - (int) (midPoint.x * scrollRatio);
        int right = midPoint.x + (int) (midPoint.x * scrollRatio);

        if (direction.equalsIgnoreCase("up")) {
            swipe(new Point(midPoint.x, top), new Point(midPoint.x, bottom), SCROLL_DUR);
        } else if (direction.equalsIgnoreCase("down")) {
            swipe(new Point(midPoint.x, bottom), new Point(midPoint.x, top), SCROLL_DUR);
        } else if (direction.equalsIgnoreCase("left")) {
            swipe(new Point(left, midPoint.y), new Point(right, midPoint.y), SCROLL_DUR);
        } else if (direction.equalsIgnoreCase("right")){
            swipe(new Point(right, midPoint.y), new Point(left, midPoint.y), SCROLL_DUR);
        }
    }

    /**
     * This method gets the center of the given element.
     * @param e This is the element to get the center of.
     * @return Point This returns the center of the element.
     */
    public static Point getCenterOfElement(WebElement e) {
        Point location = e.getLocation();
        Dimension size = e.getSize();
        return new Point(location.getX() + size.getWidth() / 2, location.getY() + size.getHeight() / 2);
    }


    /**
     * This method swipes in the given direction on the given element.
     * @param element This is the element to swipe on.
     * @param direction This is the direction to swipe in.
     */
    public static void swipeFromCenterOfElement(WebElement element, String direction)
    {
        Point point = getCenterOfElement(element);
        int bottom = point.y + (int) (point.y * 0.7);
        int top = point.y - (int) (point.y * 0.7);
        int left = point.x - (int) (point.x * 0.7);
        int right = point.x + (int) (point.x * 0.7);

        if (direction.equalsIgnoreCase("up")) {
            CommonGestures.swipe(point, new Point(point.x, top), SCROLL_DUR);
        } else if (direction.equalsIgnoreCase("down")) {
            CommonGestures.swipe(point, new Point(point.x, bottom), SCROLL_DUR);
        } else if (direction.equalsIgnoreCase("left")) {
            swipe(new Point(left, point.y), new Point(right, point.y), SCROLL_DUR);
        } else if (direction.equalsIgnoreCase("right")){
            swipe(new Point(right, point.y), new Point(left, point.y), SCROLL_DUR);
        }
    }

    /**
     * This method scrolls in the given direction by the given ratio.
     * @param dir This is the direction to scroll in.
     * @param scrollRatio This is the ratio to scroll by.
     */
    public static void scroll(ScrollDirection dir, double scrollRatio) {
        if (scrollRatio < 0 || scrollRatio > 1) {
            throw new Error("Scroll distance must be between 0 and 1");
        }
        if (dir == ScrollDirection.UP) {
            swipeDirection("up", scrollRatio);
        } else if (dir == ScrollDirection.DOWN) {
            swipeDirection("down", scrollRatio);
        } else if (dir == ScrollDirection.LEFT) {
            swipeDirection("left", scrollRatio);
        } else {
            swipeDirection("right", scrollRatio);
        }
    }



    /**
     * This method swipes from the start point to the end point in the given duration.
     * @param start This is the start point of the swipe.
     * @param end This is the end point of the swipe.
     * @param duration This is the duration of the swipe.
     */
    public static void swipe(Point start, Point end, Duration duration) {
        PointerInput input = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        Sequence swipe = new Sequence(input, 0);
        swipe.addAction(input.createPointerMove(Duration.ZERO, viewport(), start.x, start.y));
        swipe.addAction(input.createPointerDown(LEFT.asArg()));

        swipe.addAction(input.createPointerMove(duration, viewport(), end.x, end.y));
        swipe.addAction(input.createPointerUp(LEFT.asArg()));
        BasePage.getDriver().perform(ImmutableList.of(swipe));
    }

    /**
     * This method hides the keyboard.
     */
    private static void hideKeyboard() {
        if (BasePage.getPlatform().equalsIgnoreCase("android")) {
            try {
                ((AndroidDriver) BasePage.getDriver()).hideKeyboard();
                LoggerUtilities.info("Hiding Keyboard.");
            } catch (NoSuchElementException e) {
                LoggerUtilities.error("Failed to hide keyboard.");
                e.printStackTrace();
            }
        }else {
            String hideKeyboardLoc = "type == \"XCUIElementTypeKeyboard\"";

            if(BasePage.getDriver().findElement(AppiumBy.iOSNsPredicateString(hideKeyboardLoc)).isDisplayed()) {
                BasePage.getDriver().findElement(AppiumBy.iOSNsPredicateString(hideKeyboardLoc)).click();
            }
        }
    }

    /**
     * This method taps on the given location.
     * @param x This is the x-coordinate of the location to tap on.
     * @param y This is the y-coordinate of the location to tap on.
     */
    public static void tapLocation(int x, int y) {

        PointerInput FINGER = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Point point = new Point((int) (x), (int) (y));
        try {
            Sequence tap = new Sequence(FINGER, 1)
                    .addAction(FINGER.createPointerMove(ofMillis(0), viewport(), point.getX(), point.getY()))
                    .addAction(FINGER.createPointerDown(LEFT.asArg())).addAction(new Pause(FINGER, ofMillis(200)))
                    .addAction(FINGER.createPointerUp(LEFT.asArg()));
            BasePage.getDriver().perform(Arrays.asList(tap));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method taps on the given location.
     * @param x This is the x-coordinate of the location to tap on.
     * @param y This is the y-coordinate of the location to tap on.
     */
    private void tapLocation_notYetUsable(int x, int y) {
        try {
            BasePage.getDriver().executeScript("mobile: clickGesture", ImmutableMap.of("left", x, "top", y));
            LoggerUtilities.info("Taping exact location x : " + x + ", y :" + y);
        } catch (Exception e) {
            LoggerUtilities.error(e.getMessage());
        }
    }

    /**
     * This method performs a long tap on the given element.
     * @param element This is the element to long tap on.
     */
    private void longTap(RemoteWebElement element) {
        try {
            ((JavascriptExecutor) BasePage.getDriver()).executeScript("mobile: longClickGesture",
                    ImmutableMap.of("elementId", ((RemoteWebElement) element).getId(), "duration", 1500));
            LoggerUtilities.infoLoggerInFileAndReport("Long tap on element " + element.getAttribute("text"));
        } catch (Exception e) {
            LoggerUtilities.errorLoggerInFileAndReport(e.getMessage());
        }
    }

    /**
     * This method scrolls the text into view.
     * @param locatorText This is the text to scroll into view.
     */
    private void scrollTextIntoView(String locatorText) {
        try {
            BasePage.getDriver().findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true))"
                    + ".scrollIntoView(new UiSelector().textContains(\"" + locatorText + "\"))"));
            LoggerUtilities.infoLoggerInFileAndReport( locatorText + " scrolled into view");
        } catch (Exception e) {
            LoggerUtilities.errorLoggerInFileAndReport("Failed to scroll and find " + locatorText);
        }
    }
    /**
     * This method scrolls the ID into view.
     * @param id This is the ID to scroll into view.
     */
    private void scrollIDIntoView(String id) {
        try {
            BasePage.getDriver().findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true))"
                    + ".scrollIntoView(new UiSelector().resourceIdMatches(\"" + id + "\"))"));
            LoggerUtilities.infoLoggerInFileAndReport(id + " scrolled into view");
        } catch (Exception e) {
            LoggerUtilities.errorLoggerInFileAndReport("Failed to scroll and find " + id);
        }
    }

    public static void pressEnter() {
        if(BasePage.getPlatform().equalsIgnoreCase("android"))
            ((AndroidDriver)BasePage.getDriver()).pressKey(new KeyEvent(AndroidKey.ENTER));
        else {
            HashMap<String, Object> args = new HashMap<>();
            args.put("key", "Return");
            BasePage.getDriver().executeScript("mobile: performEditorAction", args);
        }
        LoggerUtilities.infoLoggerInFileAndReport("Pressed enter key");
    }

    public static int getWidthOfScreen() {
        Dimension size = BasePage.getDriver().manage().window().getSize();
        return size.getWidth();
    }

    public static int getHeightOfScreen() {
        Dimension size = BasePage.getDriver().manage().window().getSize();
        return size.getHeight();
    }
}
