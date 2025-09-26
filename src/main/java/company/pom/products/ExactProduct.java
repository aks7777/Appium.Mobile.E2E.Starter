package company.pom.products;

import company.base.BasePage;
import io.appium.java_client.pagefactory.AndroidBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

public class ExactProduct extends BasePage {

    public ExactProduct(){
        PageFactory.initElements(new AppiumFieldDecorator(getDriver()), this);
    }

    @AndroidBy(accessibility = "test-Price")
    private WebElement priceLabel;

    @AndroidBy(xpath = "//android.view.ViewGroup[@content-desc=\"test-ADD TO CART\"]")
    private WebElement addToCartBtn;

    @AndroidBy(accessibility = "test-REMOVE")
    private WebElement removeBtn;

    public String getPriceLabel() {
        return getTextFromAttribute(priceLabel, "price label");
    }

    public String clickAddToCart() {
        click(addToCartBtn, "Add to cart button");
        return getTextFromAttribute(removeBtn, "Remove button");
    }

}
