package pageObjects.allTemplates.globalFunctionality;

import org.openqa.selenium.By;
import pageObjects.allTemplates.BasePage;

import java.util.ArrayList;
import java.util.Arrays;

import static pageObjects.allTemplates.BaseSelectors.map;

public class Header extends BasePage {

    private final By mptHeaderLogo = By.cssSelector(".mpt_logo");

    private ArrayList<String> moreOptionSubmenuLinksTitles = new ArrayList<>(Arrays.asList("AAD Reading Room", "ACR Reading Room", "AGA Reading Room", "ASCO Reading Room", "Endocrine Society Reading Room", "IDSA Reading Room"));

    public Header() {
        map.put("MPT header logo", mptHeaderLogo);
    }
}
