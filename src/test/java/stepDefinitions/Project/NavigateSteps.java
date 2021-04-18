package stepDefinitions.Project;

import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import framework.Logger;
import framework.platform.utilities.DataBase.DBUtils;
import framework.platform.utilities.StringUtils;
import io.cucumber.datatable.DataTable;
import org.apache.http.client.utils.URIBuilder;
import pageObjects.Project.Pages;
import pageObjects.allTemplates.BasePage;

import java.net.URISyntaxException;
import java.util.List;

import static framework.Logger.info;
import static pageObjects.allTemplates.BasePage.*;

public class NavigateSteps {
    DBUtils dbUtils = new DBUtils();
    BasePage basePage = new BasePage();
    Pages pages = new Pages();

    @When("^I go to main page$")
    public void iGoToMainPage() {
        Logger.info("Navigate to main page");
        if (isDesktop()) {
            BasePage.driver.manage().window().maximize();
        }
        BasePage.driver.navigate().to(BasePage.settings.getEnvironment());
        waitForJSAndJQueryToLoad();
    }


    @When("^I go to \"([^\"]*)\"$")
    public void goTo(String page) {
        basePage.openPage(page);
    }

    @And("I refresh page")
    public void iRefreshPage() {
        driver.navigate().refresh();
    }

    @And("I slowly scroll page down")
    public void iSlowlyScrollPageDown() {
        info("Scrolling down the page slowly");
        basePage.scrollDownThePageSlowly();
    }

    @And("I scroll page down fast")
    public void iScrollPageDownFast() {
        info("Scrolling down the page fast");
        basePage.scrollDownThePageFast();
    }

    @And("^I go to \"([^\"]*)\" page with params$")
    public void iGoToPageWithParams(String pageName, DataTable dataTable) {
        String url = pages.getUrl(pageName);
        List<List<String>> lists = dataTable.asLists();
        if (pageName.toLowerCase().contains("inp")) {
            url = settings.getBaseUrlForInpPage() + url;
        } else if (pageName.toLowerCase().contains("preview")) {
            url = settings.getBaseUrlForPreviewPage() + url;
        } else {
            url = settings.getEnvironment() + url;
        }
        for (int i = 1; i < lists.size(); i++) {
            try {
                url = new URIBuilder(url).setParameter(lists.get(i).get(0), lists.get(i).get(1)).toString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        url = StringUtils.decodeString(url);
        driver.navigate().to(url);
        Logger.info("Navigating to " + url);
    }
}
