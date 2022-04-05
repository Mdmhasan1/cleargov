package pageObjects.allTemplates.globalFunctionality;

import org.openqa.selenium.By;
import pageObjects.allTemplates.BasePage;

import java.util.ArrayList;
import java.util.Arrays;

import static pageObjects.allTemplates.BaseSelectors.map;

public class Header extends BasePage {
        private final By CaseStudies = By.xpath(".top-nav-panel-content.nomob .form-control.header-search");
        //private final By selectOption = By.xpath("(//div[@class='top-nav-panel-content nomob']//div[@class='angucomplete-title'])[1]");

    public Header() {

        map.put("Case Studies", CaseStudies);
    }
}
