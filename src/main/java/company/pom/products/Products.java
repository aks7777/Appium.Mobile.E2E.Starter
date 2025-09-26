package company.pom.products;

import company.utils.LoggerUtilities;
import io.appium.java_client.pagefactory.*;
import company.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class Products extends BasePage {

    public Products(){
        PageFactory.initElements(new AppiumFieldDecorator(getDriver()), this);
    }

    @AndroidBy(xpath = "//*[@text='PRODUCTS']")
    private WebElement headerText;


    public String get_headerText()
    {
        return headerText.getText();
    }

    public ExactProduct searchProduct(String productName)
    {
        LoggerUtilities.infoLoggerInFileAndReport("Searching for product: " + productName);
        click(findElement(By.xpath("//*[@text='" + productName + "']")), productName);
        return new ExactProduct();
    }
}
