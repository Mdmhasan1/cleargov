package pageObjects.Project;

import org.openqa.selenium.By;
import pageObjects.allTemplates.BasePage;

import static pageObjects.allTemplates.BaseSelectors.map;

public class HomePage extends BasePage {
        private final By searchButton = By.cssSelector(".top-nav-panel-content.nomob .form-control.header-search");
        private final By selectOption = By.xpath("(//div[@class='top-nav-panel-content nomob']//div[@class='angucomplete-title'])[1]");

    public HomePage() {
        map.put("search button", searchButton);
        map.put("First Search Result", selectOption);
    }
}
