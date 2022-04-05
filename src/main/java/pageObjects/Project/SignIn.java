package pageObjects.Project;

import org.openqa.selenium.By;
import pageObjects.allTemplates.BasePage;

import static pageObjects.allTemplates.BaseSelectors.map;

public class SignIn extends BasePage {
        private final By SignIn = By.xpath("(//a[@class='nav-btn btn white narrow'])[2]");
        private final By Email = By.id("email");
    private final By Password = By.id("password");
    private final By signInButton= By.cssSelector(".cg-button.blue");
    private final By Name= By.xpath("(//div/div/input[@class='form-control ui-grid-filter-input-0'])[2]");


        public SignIn() {
            map.put("Sign In", SignIn);
            map.put("EMAIL", Email);
            map.put("PASSWORD", Password);
            map.put("SignIn Button", signInButton);
            map.put("NAME", Name);
         }

    }
