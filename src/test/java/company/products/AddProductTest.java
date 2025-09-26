package company.products;

import company.base.BaseTest;
import company.pom.products.ExactProduct;
import company.pom.products.Products;
import company.utils.JsonReader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

@Test(groups = {"Company.Mobile.Module.Regression"})
public class AddProductTest extends BaseTest {
    Products products;
    ExactProduct exactProduct;
    String password;
    String productName;
    String exp_price;

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Method method) {
        // Read test data
        getJsonData = JsonReader.getNestedJson(testdata, className);
        productName = JsonReader.getNestedString(getJsonData, "productName");
        exp_price = JsonReader.getNestedString(getJsonData, "exp_price");
        startRecordingAndLoginToApp(method);
    }

    @Test(groups = {"Company.Mobile.Module.addProductToCartTest"})
    public void addProductToCartTest() {
        products = new Products();
        exactProduct = products.searchProduct(productName);
        String act_price = exactProduct.getPriceLabel();
        Assert.assertEquals(act_price, exp_price, "Product price is wrong. Actual : " + act_price + ", Expected : " + exp_price);
        String act_removeBtn = exactProduct.clickAddToCart();
        Assert.assertEquals(act_removeBtn, "REMOVE", "Product not added to cart");
    }
}
