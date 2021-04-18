package pageObjects.allTemplates;

import org.openqa.selenium.By;
import pageObjects.allTemplates.globalFunctionality.Header;

import java.util.HashMap;

public class BaseSelectors {
    public static HashMap<String, By> map = new HashMap();

    public BaseSelectors() {
        new Header();
    }
}

