package pageObjects.allTemplates;

import org.openqa.selenium.By;
import pageObjects.Project.HomePage;
import pageObjects.Project.SignIn;
import pageObjects.allTemplates.globalFunctionality.Header;

import java.util.HashMap;

public class BaseSelectors {
    public static HashMap<String, By> map = new HashMap();

    public BaseSelectors() {
        new Header();
        new HomePage();
        new SignIn();

    }
}

