package company.pom.login;

import company.base.BasePage;
import company.pom.products.Products;
import io.appium.java_client.pagefactory.*;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class Login extends BasePage {

    public Login() {
        PageFactory.initElements(new AppiumFieldDecorator(getDriver()), this);
    }

    @AndroidFindAll({
            @AndroidBy(accessibility = "test-Username")
    })
    private WebElement emailTextField;

    @AndroidFindAll({
            @AndroidBy(accessibility = "test-Password")
    })
    private WebElement passTextField;

    @AndroidFindAll({
            @AndroidBy(accessibility = "test-LOGIN")
    })
    private WebElement loginBtn;

    public Products do_login(String username, String password) {
        sendKeys(emailTextField, username,"email field");
        sendKeys(passTextField, password,"password field");
        click(loginBtn, "login button");
        return new Products();
    }
}
