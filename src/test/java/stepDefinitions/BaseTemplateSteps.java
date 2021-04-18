package stepDefinitions;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import framework.Element;
import framework.Logger;
import framework.SoftAssertion;
import framework.platform.Css;
import framework.platform.DatePatterns;
import framework.platform.Settings;
import framework.platform.Utils;
import framework.platform.utilities.DataBase.DBUtils;
import framework.platform.utilities.DateUtils;
import framework.platform.utilities.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import pageObjects.allTemplates.BasePage;
import pageObjects.allTemplates.BaseSelectors;

import java.net.URISyntaxException;
import java.util.*;

import static framework.Element.defaultSleep;
import static framework.Logger.info;
import static org.apache.commons.lang.StringEscapeUtils.unescapeJava;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.testng.Assert.*;
import static pageObjects.allTemplates.BasePage.*;

public class BaseTemplateSteps extends BaseSelectors {
    JavascriptExecutor executor = (JavascriptExecutor) driver;
    BasePage basePage = new BasePage();
    DBUtils dbUtils = new DBUtils();
    SoftAssertion softAssertion = new SoftAssertion();
    public static Map<String, SavedObjects> data = new HashMap<>();

    public Element getElement(String element) {
        return find(map.get(element));
    }

    public List<Element> getElements(String element) {
        return findAll(map.get(element));
    }

    private Element getElementByText(String text) {
        return find(By.xpath("//*[text()='" + text + "']"));
    }

    private List<Element> getElementsByText(String text) {
        return findAll(By.xpath("//*[text()='" + text + "']"));
    }

    private By getSelector(String selector) {
        if (selector.indexOf("/") == 0 || selector.indexOf("./") == 0) {
            info("Selector " + selector + " looks like xpath selector");
            return By.xpath(selector);
        } else {
            info("Selector " + selector + " looks like css selector");
            return By.cssSelector(selector);
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    @When("^I wait for (\\d+) seconds( on mobile| on desktop|)$")
    public void waitSeconds(int timeout, String device) {
        if ((device.contains("desktop") && BasePage.isDesktop() || (device.contains("mobile") && BasePage.isMobile()) || device.isEmpty())) {
            Logger.info("Waiting for " + timeout + " seconds");
            sleep(timeout * 1000);
        }
    }

    @Then("^I (soft |)check that(?: saved text| text|) \"([^\"]*)\" is( not|) visible( on desktop| on mobile|)$")
    public void isElementVisibleUpd(String soft, String element, String visibility, String device) {
        Logger.info("Check that " + element + " is " + visibility + " visible");
        boolean isVisible = !visibility.equals(" not");
        boolean softToggle = soft.contains("soft");
        if (device.contains("mobile") && BasePage.isMobile()) {
            verifyElementVisibility(element, isVisible, device, softToggle);
        } else if (device.contains("desktop") && BasePage.isDesktop()) {
            verifyElementVisibility(element, isVisible, device, softToggle);
        } else if (device.isEmpty()) {
            verifyElementVisibility(element, isVisible, device, softToggle);
        }
    }

    @Then("^I (soft |)check that \"([^\"]*)\" is (enabled|disabled)( on desktop| on mobile|)$")
    public void isElementEnabled(String soft, String element, String enabled, String device) {
        boolean accessibility = enabled.equals("enabled");
        boolean softToggle = soft.contains("soft");
        if (device.contains("mobile") && BasePage.isMobile()) {
            verifyElementAccessibility(element, accessibility, device, softToggle);
        } else if (device.contains("desktop") && BasePage.isDesktop()) {
            verifyElementAccessibility(element, accessibility, device, softToggle);
        } else if (device.isEmpty()) {
            verifyElementAccessibility(element, accessibility, device, softToggle);
        }
    }

    public void verifyElementVisibility(String element, boolean visibility, String device, boolean softToggle) {
        Element elem = map.get(element) == null ? getElementByText(element) : getElement(element);
        if (visibility) {
            if (softToggle) {
                softAssertion.assertTrue(elem.isVisible(), "Element '" + element + "' is not visible" + device);
            } else {
                assertTrue(elem.isVisible(), "Element '" + element + "' is not visible" + device);
            }
        } else {
            if (softToggle) {
                softAssertion.assertFalse(elem.isVisible(), "Element '" + element + "' not visible" + device);
            } else {
                assertFalse(elem.isVisible(), "Element '" + element + "' not visible" + device);
            }
        }
    }

    private void verifyElementAccessibility(String element, boolean accessibility, String device, boolean softToggle) {
        Element elem = map.get(element) == null ? getElementByText(element) : getElement(element);
        boolean enabled = elem.isEnabled();

        try {
            if (elem.getAttribute("disabled").equals("true")) {
                enabled = false;
            }
        } catch (Exception e) {
        }
        try {
            if (elem.getAttribute("class").contains("inactive") || elem.getAttribute("class").contains("inactive-btn")) {
                enabled = false;
            }
        } catch (Exception e) {

        }
        if (accessibility) {
            if (softToggle) {
                softAssertion.assertTrue(enabled, "Element '" + element + "' is enabled" + device);
            } else {
                assertTrue(enabled, "Element '" + element + "' is enabled" + device);
            }
        } else {
            if (softToggle) {
                softAssertion.assertFalse(enabled, "Element '" + element + "' is disabled on" + device);
            } else {
                assertFalse(enabled, "Element '" + element + "' is disabled on" + device);
            }
        }
    }

    @Then("^I (soft |)check that \\*(.*)\\* is( not|) visible( on desktop| on mobile|)$")
    public void isSelectorVisibleUpd(String soft, String selector, String visibility, String device) {
        boolean isVisible = !visibility.equals(" not");
        boolean softToggle = soft.contains("soft");
        if (device.contains("mobile") && BasePage.isMobile()) {
            verifySelectorVisibility(selector, isVisible, device, softToggle);
        } else if (device.contains("desktop") && BasePage.isDesktop()) {
            verifySelectorVisibility(selector, isVisible, device, softToggle);
        } else if (device.isEmpty()) {
            verifySelectorVisibility(selector, isVisible, device, softToggle);
        }
    }

    public void verifySelectorVisibility(String selector, boolean visibility, String device, boolean softToggle) {
        if (visibility) {
            if (softToggle) {
                softAssertion.assertTrue(find(getSelector(selector)).isVisible(),
                        "Element with selector '" + selector + "' is not visible" + device);
            } else {
                assertTrue(find(getSelector(selector)).isVisible(),
                        "Element with selector '" + selector + "' is not visible" + device);
            }
        } else {
            if (softToggle) {
                softAssertion.assertFalse(find(getSelector(selector)).isVisible(),
                        "Element with selector '" + selector + "' is visible" + device);
            } else {
                assertFalse(find(getSelector(selector)).isVisible(),
                        "Element with selector '" + selector + "' is visible" + device);
            }
        }
    }

    @Then("^I (soft |)check that \'([^\']*)\' is( not|) visible( on desktop| on mobile|)$")
    public void isLinkTextVisibleUpd(String soft, String linkText, String visibility, String device) {
        boolean isVisible = !visibility.equals(" not");
        boolean softToggle = soft.contains("soft");
        linkText = linkText.replaceAll("\"", "'");
        if (device.contains("mobile") && BasePage.isMobile()) {
            verifyLinkVisibility(linkText, isVisible, device, softToggle);
        } else if (device.contains("desktop") && BasePage.isDesktop()) {
            verifyLinkVisibility(linkText, isVisible, device, softToggle);
        } else if (device.isEmpty()) {
            verifyLinkVisibility(linkText, isVisible, device, softToggle);
        }
    }

    public void verifyLinkVisibility(String linkText, boolean visibility, String device, boolean softToggle) {
        if (visibility) {
            if (softToggle) {
                softAssertion.assertTrue(find(By.linkText(linkText)).isVisible(),
                        "Link with text '" + linkText + "' is not visible" + device);
            } else {
                assertTrue(find(By.linkText(linkText)).isVisible(),
                        "Link with text '" + linkText + "' is not visible" + device);
            }
        } else {
            if (softToggle) {
                softAssertion.assertFalse(find(By.linkText(linkText)).isVisible(),
                        "Link with text '" + linkText + "' is visible" + device);
            } else {
                assertFalse(find(By.linkText(linkText)).isVisible(),
                        "Link with text '" + linkText + "' is visible" + device);
            }
        }
    }

    @Then("^I (soft |)check that \"([^\"]*)\" has \"([^\"]*)\" color on desktop$")
    public void validateColorOnDesktop(String soft, String element, String color) {
        boolean softToggle = soft.contains("soft");
        if (BasePage.isDesktop()) {
            String cssColor = getElement(element).getColor();
            if (softToggle) {
                softAssertion.assertEquals(cssColor, color, "Color is incorrect for element " + element);
            } else {
                assertEquals(cssColor, color, "Color is incorrect for element " + element);
            }
        } else {
            info("Environment is not desktop skipping this check");
        }
    }

    @Then("^I (soft |)check that \\[(.*?)\\] have order from left to right( on mobile| on desktop|)$")
    public void validateOrderFromLeftToRight(String soft, String elements, String device) {
        boolean softToggle = soft.contains("soft");
        if (device.contains("mobile") && BasePage.isMobile()) {
            verifyOrderFromLeftToRight(elements, softToggle);
        } else if (device.contains("desktop") && BasePage.isDesktop()) {
            verifyOrderFromLeftToRight(elements, softToggle);
        } else if (device.isEmpty()) {
            verifyOrderFromLeftToRight(elements, softToggle);
        }
    }

    public void verifyOrderFromLeftToRight(String elements, boolean softToggle) {
        List<Element> elementList = getStringToElement(elements);
        boolean check = true;
        for (int i = 1; i < elementList.size(); i++) {
            if (elementList.get(i - 1).getXCoordinate() > elementList.get(i).getXCoordinate()) {
                info("Element number " + (i - 1) + " is closer to right than element number " + (i));
                check = false;
            }
        }
        if (softToggle) {
            softAssertion.assertTrue(check, "Orders of elements " + elements + " is incorrect");
        } else {
            assertTrue(check, "Orders of elements " + elements + " is incorrect");
        }
    }

    @Then("^I (soft |)check that \"([^\"]*)\" have order from top to bottom( on mobile| on desktop|)$")
    public void validateOrderFromTopToBottom(String soft, String element, String device) {
        boolean softToggle = soft.contains("soft");
        if (device.contains("mobile") && BasePage.isMobile()) {
            verifyOrderFromTopToBottom(element, softToggle);
        } else if (device.contains("desktop") && BasePage.isDesktop()) {
            verifyOrderFromTopToBottom(element, softToggle);
        } else if (device.isEmpty()) {
            verifyOrderFromTopToBottom(element, softToggle);
        }
    }

    @Then("^I (soft |)check that \"([^\"]*)\" are ordered alphabetically( ignoring case|)( on mobile| on desktop|)$")
    public void validateOrderAlphabetically(String soft, String element, String ignoreCase, String device) {
        boolean softToggle = soft.contains("soft");
        if (device.contains("mobile") && BasePage.isMobile()) {
            verifyOrderAlphabetically(element, ignoreCase, softToggle);
        } else if (device.contains("desktop") && BasePage.isDesktop()) {
            verifyOrderAlphabetically(element, ignoreCase, softToggle);
        } else if (device.isEmpty()) {
            verifyOrderAlphabetically(element, ignoreCase, softToggle);
        }
    }

    public void verifyOrderFromTopToBottom(String element, boolean softToggle) {
        info("Checking if all '" + element + "' are placed from top to bottom");
        boolean check = true;
        int elementsCount = getElements(element).size();
        info("There are " + elementsCount + " " + element + "s on page");
        for (int i = 1; i < elementsCount - 1; i++) {
            if (getElements(element).get(i - 1).getLocation().getY() > getElements(element).get(i).getLocation().getY()) {
                info("Element number " + (i - 1) + " is not below element number " + (i));
                check = false;
            }
        }
        if (softToggle) {
            softAssertion.assertTrue(check, "Elements " + element + " should be placed from top to bottom");
        } else {
            assertTrue(check, "Elements " + element + " should be placed from top to bottom");
        }
    }

    public void verifyOrderAlphabetically(String element, String ignoreCase, boolean softToggle) {
        info("Checking if all '" + element + "' are ordered alphabetically");
        List<Element> elements = getElements(element);
        List<String> values = new ArrayList<>();
        if (isNotEmpty(ignoreCase)) {
            elements.forEach(it -> values.add(it.getText().toLowerCase()));
        } else {
            elements.forEach(it -> values.add(it.getText()));
        }
        List<String> sortedValues = new ArrayList<>(values);
        Collections.sort(sortedValues, Comparator.naturalOrder());
        if (softToggle) {
            softAssertion.assertEquals(values, sortedValues, "Values for element " + element + " are not ordered alphabetically");
        } else {
            assertEquals(values, sortedValues, "Values for element " + element + "  are not ordered alphabetically");
        }
    }

    public List<Element> getStringToElement(String elements) {
        List<String> elementListString = Arrays.asList(elements.split(","));
        List<Element> elementList = new ArrayList<>();
        for (String element : elementListString) {
            if (element.contains("'")) {
                elementList.add(find(By.linkText(element.split("'")[1])));
            } else {
                elementList.add(getElement(element.split("\"")[1]));
            }
        }
        return elementList;
    }

    @Then("^I (soft |)check that \"([^\"]*)\" url is not empty$")
    public void isUrlNotEmpty(String soft, String element) {
        boolean softToggle = soft.contains("soft");
        try {
            if (softToggle) {
                softAssertion.assertFalse(getElement(element).getAttribute("href").isEmpty(), "href param of link is empty");
            } else {
                assertFalse(getElement(element).getAttribute("href").isEmpty(), "href param of link is empty");
            }
        } catch (NullPointerException n) {
            if (softToggle) {
                softAssertion.fail("href param is not present or whole element is not present");
            } else {
                fail("href param is not present or whole element is not present");
            }
        }
    }

    @Then("^I (soft |)check that \"(.*)\"(?: number (\\d+)|) text( is| is not| equals to| not equals to| contains| not contains| equals ignore case to) \"(.*)\"( on mobile| on desktop|)$")
    public void textIsEqualTo(String soft, String element, Integer number, String isEquals, String expectedText, String device) {
        int num = number == null ? 1 : number;
        boolean softToggle = soft.contains("soft");
        if (device.contains("mobile") && BasePage.isMobile()) {
            verifyElementTextEqualsTo(element, num, isEquals, expectedText, softToggle);
        } else if (device.contains("desktop") && BasePage.isDesktop()) {
            verifyElementTextEqualsTo(element, num, isEquals, expectedText, softToggle);
        } else if (device.isEmpty()) {
            verifyElementTextEqualsTo(element, num, isEquals, expectedText, softToggle);
        }
    }

    private void verifyElementTextEqualsTo(String element, Integer num, String isEquals, String expectedText, boolean softToggle) {
        if (num == 0) {
            num = 1;
        }
        String elementText = getElements(element).get(num - 1).getText().replaceAll("\n", "");
        expectedText = getSavedTextData(expectedText);
        info("Validation is " + element + " text " + isEquals + " to: " + expectedText);
        if (isEquals.contains("equals") || isEquals.contains("is")) {
            if (!isEquals.contains("not")) {
                if (isEquals.contains("ignore")) {
                    elementText = elementText.toLowerCase();
                    expectedText = expectedText.toLowerCase();
                }
                if (softToggle) {
                    softAssertion.assertEquals(elementText, expectedText, "Element " + element + " number " + num + " should be equal to text " + expectedText);
                } else {
                    assertEquals(elementText, expectedText, "Element " + element + " number " + num + " should be equal to text " + expectedText);
                }
            } else {
                if (softToggle) {
                    softAssertion.assertNotEquals(elementText, expectedText, "Element " + element + " number " + num + " should not be equal to text " + expectedText);
                } else {
                    assertNotEquals(elementText, expectedText, "Element " + element + " number " + num + " should not be equal to text " + expectedText);
                }
            }
        } else {
            if (!isEquals.contains("not")) {
                if (softToggle) {
                    softAssertion.assertTrue(elementText.contains(expectedText), "Element " + element + " number " + num + " should contain text " + expectedText);
                } else {
                    assertTrue(elementText.contains(expectedText), "Element " + element + " number " + num + " should contain text " + expectedText);
                }
            } else {
                if (softToggle) {
                    softAssertion.assertFalse(elementText.contains(expectedText), "Element " + element + " number " + num + " should not contain text " + expectedText);
                } else {
                    assertFalse(elementText.contains(expectedText), "Element " + element + " number " + num + " should not contain text " + expectedText);
                }
            }
        }
    }

    @Then("^I (soft |)check that \"([^\"]*)\"(?: number (\\d+)|)(| css) attribute \"([^\"]*)\"( is| is not| equals to| not equals to| contains| not contains) \"([^\"]*)\"( on mobile| on desktop|)$")
    public void attributeIsEqualTo(String soft, String element, Integer number, String isCSS, String attribute, String isEquals, String expectedText, String device) {
        int num = number == null ? 1 : number;
        if (num == 0) {
            num = 1;
        }
        boolean softToggle = soft.contains("soft");
        if (!element.equals("-")) {
            if (device.contains("mobile") && BasePage.isMobile()) {
                verifyAttributeOfElement(element, attribute, isEquals, isCSS, expectedText, num, softToggle);
            } else if (device.contains("desktop") && BasePage.isDesktop()) {
                verifyAttributeOfElement(element, attribute, isEquals, isCSS, expectedText, num, softToggle);
            } else if (device.isEmpty()) {
                verifyAttributeOfElement(element, attribute, isEquals, isCSS, expectedText, num, softToggle);
            }
        }

    }

    private void verifyAttributeOfElement(String element, String attribute, String isEquals, String isCss, String text, int num, boolean softToggle) {
        String actual;
        if (isCss.contains("css")) {
            actual = getElements(element).get(num - 1).getCssValue(attribute);
        } else {
            actual = getElements(element).get(num - 1).getAttribute(attribute);
        }
        String expectedText = getSavedTextData(text);
        if (isEquals.contains("equals") || isEquals.contains("is")) {
            if (!isEquals.contains("not")) {
                if (softToggle) {
                    softAssertion.assertEquals(actual, expectedText, "Attribute " + attribute + " of element " + element + " number " + num + " should be equal to " + expectedText);
                } else {
                    assertEquals(actual, expectedText, "Attribute " + attribute + " of element " + element + " number " + num + " should be equal to " + expectedText);
                }
            } else {
                if (softToggle) {
                    softAssertion.assertNotEquals(actual, expectedText, "Attribute " + attribute + " of element " + element + " number " + num + " should not be equal to " + expectedText);
                } else {
                    assertNotEquals(actual, expectedText, "Attribute " + attribute + " of element " + element + " number " + num + " should not be equal to " + expectedText);
                }
            }
        } else {
            if (!isEquals.contains("not")) {
                if (softToggle) {
                    softAssertion.assertTrue(actual.contains(expectedText), "Text not contains\n Expected: " + expectedText + "\nActual: " + actual);
                } else {
                    assertTrue(actual.contains(expectedText), "Text not contains\n Expected: " + expectedText + "\nActual: " + actual);
                }
            } else {
                if (softToggle) {
                    softAssertion.assertFalse(actual.contains(expectedText), "Text contains but shouldn't \n Expected: " + expectedText + "\nActual: " + actual);
                } else {
                    assertFalse(actual.contains(expectedText), "Text contains but shouldn't \n Expected: " + expectedText + "\nActual: " + actual);
                }
            }
        }
    }

    @Then("^I (soft |)check that(?: every| each| all) \"([^\"]*)\"(| css) attribute \"([^\"]*)\"( is| is not| equals to| not equals to| contains| not contains) \"([^\"]*)\"( on mobile| on desktop|)$")
    public void verifyEveryElementAttributeEquals(String soft, String element, String isCss, String attribute, String action, String expectedText, String device) {
        boolean softToggle = soft.contains("soft");
        if (device.contains("mobile") && BasePage.isMobile()) {
            everyElementAttributeEquals(element, isCss, attribute, action, expectedText, softToggle);
        } else if (device.contains("desktop") && BasePage.isDesktop()) {
            everyElementAttributeEquals(element, isCss, attribute, action, expectedText, softToggle);
        } else if (device.isEmpty()) {
            everyElementAttributeEquals(element, isCss, attribute, action, expectedText, softToggle);
        }
    }

    private void everyElementAttributeEquals(String element, String isCss, String attribute, String action, String expectedText, boolean softToggle) {
        boolean check = true;
        int elementsCount = getElements(element).size();
        List<Element> elements = getElements(element);

        info("Verify if " + isCss + " attribute " + attribute + " of every " + element + " " + action + " " + expectedText);
        for (int i = 0; i < elementsCount; i++) {
            String attr = attribute.contains("css") ? elements.get(i).getCssValue(attribute) : elements.get(i).getAttribute(attribute);
            if (action.contains("is") || action.contains("equals")) {
                if (action.contains("not")) {
                    if (attr.equals(expectedText)) {
                        Logger.info(isCss + " attribute of element " + element + " number " + i + " is equals to " + expectedText);
                        check = false;
                    }
                } else {
                    if (!attr.equals(expectedText)) {
                        Logger.info(isCss + " attribute of element " + element + " number " + i + " is not equals to " + expectedText);
                        check = false;
                    }
                }
            } else {
                if (action.contains("not")) {
                    if (attr.contains(expectedText)) {
                        Logger.info(isCss + " attribute of element " + element + " number " + i + " contains " + expectedText);
                        check = false;
                    }
                } else {
                    if (!attr.contains(expectedText)) {
                        Logger.info(isCss + " attribute of element " + element + " number " + i + " not contains " + expectedText);
                        check = false;
                    }
                }
            }

        }
        if (softToggle) {
            softAssertion.assertTrue(check, isCss + " attribute " + attribute + " of every " + element + " does not " + action + " " + expectedText);
        } else {
            assertTrue(check, isCss + " attribute " + attribute + " of every " + element + " does not " + action + " " + expectedText);
        }
    }

    @Then("^I check that \'([^\']*)\' will open in a new tab$")
    public void isLinkTextHasTargetBlank(String linkByText) {
        assertTrue(find(By.linkText(linkByText.replaceAll("\"", "'"))).hasTargetBlank());
    }

    @Then("^I check that \\*([^*]*)\\* will( not|) open in a new tab$")
    public void isSelectorHasTargetBlank(String selector, String notNewTab) {
        if (notNewTab.isEmpty()) assertTrue(find(getSelector(selector)).hasTargetBlank());
        else assertFalse(find(getSelector(selector)).hasTargetBlank());
    }

    @Then("^I (soft |)check that \"([^\"]*)\" has background color \"([^\"]*)\"$")
    public void assertBackgroundColor(String soft, String element, String color) {
        String backgroundColor = getElement(element).getBackgroundColor();
        boolean softToggle = soft.contains("soft");
        if (softToggle) {
            softAssertion.assertEquals(backgroundColor, color, "Background color is incorrect");
        } else {
            assertEquals(backgroundColor, color, "Background color is incorrect");
        }
    }

    @Then("^I (soft |)check that \"([^\"]*)\" has content icon \"([^\"]*)\"( on mobile| on desktop|)$")
    public void assertCssContentDesktop(String soft, String element, String content, String device) {
        boolean softToggle = soft.contains("soft");
        if (device.contains("mobile") && BasePage.isMobile()) {
            cssContentDesktop(element, content, softToggle);
        } else if (device.contains("desktop") && BasePage.isDesktop()) {
            cssContentDesktop(element, content, softToggle);
        } else if (device.isEmpty()) {
            cssContentDesktop(element, content, softToggle);
        }
    }

    private void cssContentDesktop(String element, String expectedContent, boolean softToggle) {
        String cssContent = getElement(element).getCssContentIcon();
        if (softToggle) {
            softAssertion.assertEquals(cssContent, expectedContent, "Content icon is incorrect");
        } else {
            assertEquals(cssContent, expectedContent, "Content icon is incorrect");
        }
    }

    @Then("^I (soft |)check that \"([^\"]*)\" has format \"([^\"]*)\"$")
    public void assertElementCssFormat(String soft, String element, String format) {
        boolean softToggle = soft.contains("soft");
        Css actualCss = getElement(element).getElementCss();
        if (softToggle) {
            softAssertion.assertTrue(actualCss.equals(new Css(format)), "Css of given element " + element + " is not equals to expected");
        } else {
            assertTrue(actualCss.equals(new Css(format)), "Css of given element " + element + " is not equals to expected");
        }
    }

    @Then("^I (soft |)check that \\*([^*]*)\\* has format \"([^\"]*)\"$")
    public void assertSelectorCssFormat(String soft, String selector, String format) {
        boolean softToggle = soft.contains("soft");
        Css actualCss = find(getSelector(selector)).getElementCss();
        if (softToggle) {
            softAssertion.assertTrue(actualCss.equals(new Css(format)), "Css of given selector " + selector + " is not equals to expected");
        } else {
            assertTrue(actualCss.equals(new Css(format)), "Css of given selector " + selector + " is not equals to expected");
        }
    }

    @Then("^I (soft |)check that \'*([^\"]*)\' has format \"([^\"]*)\"$")
    public void assertCssFormatWithLinkText(String soft, String linkByText, String format) {
        boolean softToggle = soft.contains("soft");
        Css actualCss = find(By.linkText(linkByText)).getElementCss();
        if (softToggle) {
            softAssertion.assertTrue(actualCss.equals(new Css(format)), "Format of given link " + linkByText + " is not equals to expected:" + format);
        } else {
            assertTrue(actualCss.equals(new Css(format)), "Format of given link " + linkByText + " is not equals to expected:" + format);
        }
    }

    @Then("^I (soft |)check that \"([^\"]*)\" leads to \"([^\"]*)\"$")
    public void assertElementUrlHrefParam(String soft, String element, String url) {
        boolean softToggle = soft.contains("soft");
        String linkUrl = getElement(element).getAttribute("href");
        if (url.contains("http")) {
            if (softToggle) {
                softAssertion.assertEquals(linkUrl, url, "Element " + element + " does not lead to " + url);
            } else {
                assertEquals(linkUrl, url, "Element " + element + " does not lead to " + url);
            }
        } else {
            if (softToggle) {
                softAssertion.assertTrue(linkUrl.contains(url), "Url is not equal to expected\nActual " + linkUrl + "\nExpected " + url);
            } else {
                assertTrue(linkUrl.contains(url), "Url is not equal to expected\nActual " + linkUrl + "\nExpected " + url);
            }
        }
    }

    @Then("^I (soft |)check that \\*([^*]*)\\* leads to \"([^\"]*)\"$")
    public void assertSelectorUrlHrefParam(String soft, String selector, String url) {
        boolean softToggle = soft.contains("soft");
        String linkUrl = find(getSelector(selector)).getAttribute("href");
        if (url.contains("http")) {
            if (softToggle) {
                softAssertion.assertEquals(linkUrl, url, "Link url " + linkUrl + "does not equal to " + url);
            } else {
                assertEquals(linkUrl, url, "Link url " + linkUrl + "does not equal to " + url);
            }
        } else {
            if (softToggle) {
                softAssertion.assertTrue(linkUrl.contains(url), "Url is not equal to expected\nActual " + linkUrl + "\nExpected " + url);
            } else {
                assertTrue(linkUrl.contains(url), "Url is not equal to expected\nActual " + linkUrl + "\nExpected " + url);
            }
        }
    }

    @Then("^I (soft |)check that \'([^\']*)\' link leads to \"([^\"]*)\"$")
    public void assertLinkText(String soft, String linkByText, String url) {
        boolean softToggle = soft.contains("soft");
        String linkUrl = find(By.linkText(linkByText.replaceAll("\"", "'"))).getAttribute("href");
        if (url.contains("http")) {
            if (softToggle) {
                softAssertion.assertEquals(linkUrl, url, linkByText + " link does not lead to " + url);
            } else {
                assertEquals(linkUrl, url, linkByText + " link does not lead to " + url);
            }
        } else {
            if (softToggle) {
                softAssertion.assertTrue(linkUrl.contains(url), "Url is not equal to expected\nActual " + linkUrl + "\nExpected " + url);
            } else {
                assertTrue(linkUrl.contains(url), "Url is not equal to expected\nActual " + linkUrl + "\nExpected " + url);
            }
        }
    }

    @Then("^I (soft |)check that \"([^\"]*)\" leads to \"([^\"]*)\" on desktop$")
    public void assertUrlHrefParamOnDesktop(String soft, String element, String url) {
        boolean softToggle = soft.contains("soft");
        String linkUrl = getElement(element).goToFirstATag().getAttribute("href");
        if (Settings.isDesktop()) {
            if (url.contains("http")) {
                if (softToggle) {
                    softAssertion.assertEquals(linkUrl, url, element + " link does not lead to " + url);
                } else {
                    assertEquals(linkUrl, url, element + " link does not lead to " + url);
                }
            } else {
                if (softToggle) {
                    softAssertion.assertTrue(linkUrl.contains(url), element + " link does not lead to " + url);
                } else {
                    assertTrue(linkUrl.contains(url), element + " link does not lead to " + url);
                }
            }
        } else {
            info("Environment is not desktop skipping this check");
        }
    }

    @Then("^I (soft |)check that \'([^\']*)\' link leads to \"([^\"]*)\" on desktop$")
    public void assertLinkTextOnDesktop(String soft, String linkByText, String url) {
        boolean softToggle = soft.contains("soft");
        String linkUrl = find(By.linkText(linkByText)).goToFirstATag().getAttribute("href");
        if (Settings.isDesktop()) {
            if (url.contains("http")) {
                if (softToggle) {
                    softAssertion.assertEquals(linkUrl, url, linkByText + " link does not lead to " + url);
                } else {
                    assertEquals(linkUrl, url, linkByText + " link does not lead to " + url);
                }
            } else {
                if (softToggle) {
                    softAssertion.assertTrue(linkUrl.contains(url), linkByText + " link does not lead to " + url);
                } else {
                    assertTrue(linkUrl.contains(url), "Url is not equal to expected\nActual " + linkByText + "\nExpected " + url);
                }
            }
        } else {
            info("Environment is not desktop skipping this check");
        }
    }

    @Then("^I (soft |)check that \'([^\']*)\' link in \"([^\"]*)\" leads to \"([^\"]*)\"$")
    public void assertLinkTextWithParentElement(String soft, String linkByText, String parentElement, String url) {
        boolean softToggle = soft.contains("soft");
        Element parent = getElement(parentElement);
        String linkUrl = parent.findElement(By.linkText(linkByText)).goToFirstATag().getAttribute("href");
        if (url.contains("http")) {
            if (softToggle) {
                softAssertion.assertEquals(linkUrl, url, linkByText + " link does not lead to " + url);
            } else {
                assertEquals(linkUrl, url, linkByText + " link does not lead to " + url);
            }
        } else {
            if (softToggle) {
                softAssertion.assertTrue(linkUrl.contains(url), linkByText + " link does not lead to " + url);
            } else {
                assertTrue(linkUrl.contains(url), linkByText + " link does not lead to " + url);
            }
        }
    }

    @Then("^I (soft |)check that \\*([^*]*)\\* in( saved element|) \"([^\"]*)\" is checked$")
    public void isElementChecked(String soft, String selector, String isSaved, String name) {
        boolean softToggle = soft.contains("soft");
        if (name.isEmpty()) {
            info("Check that selector " + selector + " has attribute 'checked'");
            if (softToggle) {
                softAssertion.assertTrue(find(getSelector(selector)).isSelected(), "Selector " + selector + " doesn't have attribute 'checked'");
            } else {
                assertTrue(find(getSelector(selector)).isSelected(), "Selector " + selector + " doesn't have attribute 'checked'");
            }
        } else if (!isSaved.isEmpty()) {
            info("Check that selector " + selector + " in saved element with key" + name + " has attribute 'checked'");
            Element element = data.get(name).returnSaved();
            if (softToggle) {
                softAssertion.assertTrue(element.findElement(getSelector(selector)).isSelected(), "Selector " + selector + " doesn't have attribute 'checked'");
            } else {
                assertTrue(element.findElement(getSelector(selector)).isSelected(), "Selector " + selector + " doesn't have attribute 'checked'");
            }
        } else {
            info("Check that selector " + selector + " in element " + name + " has attribute 'checked'");
            if (softToggle) {
                softAssertion.assertTrue(getElement(name).findElement(getSelector(selector)).isSelected(), "Selector " + selector + " doesn't have attribute 'checked'");
            } else {
                assertTrue(getElement(name).findElement(getSelector(selector)).isSelected(), "Selector " + selector + " doesn't have attribute 'checked'");
            }
        }
    }

    @When("^I click( with javascript| with action|) on \"([^\"]*)\"( on mobile| on desktop|)$")
    public void click(String action, String element, String device) {
        Logger.info("Clicking on " + element);
        if (device.contains("mobile") && BasePage.isMobile()) {
            clickOn(element, action);
        } else if (device.contains("desktop") && BasePage.isDesktop()) {
            clickOn(element, action);
        } else if (device.isEmpty()) {
            clickOn(element, action);
        }
        basePage.waitForAjaxRequestToBeFinished();
    }

    public void clickOn(String element, String action) {
        if (action.isEmpty()) {
            getElement(element).click();
        } else if (action.equals(" with action")) {
            getElement(element).actionClick();
        } else if (action.equals(" with javascript")) {
            getElement(element).clickWithJS();
        }
    }

    @When("^I click on \'([^\"]*)\'$")
    public void clickOnLinkText(String linkByText) {
        linkByText = getSavedTextData(linkByText);
        Element element = find(By.linkText(linkByText));
        element.scrollIntoViewport();
        element.click();
    }

    @When("^I click(?: on|) \\*([^*]*)\\* if visible$")
    public void clickSelectorIfVisible(String selector) {
        try {
            find(getSelector(selector)).click();
        } catch (ElementNotVisibleException | org.openqa.selenium.NoSuchElementException | NullPointerException e) {
            info("Element is not present. Skipping this step.");
        }
    }

    @When("^I drag \\*(.*)\\* and drop to \\*([^*]*)\\*$")
    public void dragAndDropSelectorToSelector(String selectorDrag, String selectorDrop) {
        info("Drag " + selectorDrag + " selector and drop to " + selectorDrop + " selector");
        dragAndDrop(driver.findElement(getSelector(selectorDrag)), driver.findElement(getSelector(selectorDrop)));
    }

    @When("^I drag \"([^\"]*)\" and drop to \"([^\"]*)\"$")
    public void dragAndDropElementToElement(String targetElem, String destinationElem) {
        info("Drag '" + targetElem + "' selector and drop to '" + destinationElem + "' selector");
        dragAndDrop(getElement(targetElem).getWebElement(), getElement(destinationElem).getWebElement());
    }

    @When("^I drag \\*(.*)\\* and drop to \"([^\"]*)\"$")
    public void dragAndDropSelectorToElement(String selectorDrag, String elementDrop) {
        info("Drag " + selectorDrag + " selector and drop to " + elementDrop + " element");
        dragAndDrop(driver.findElement(getSelector(selectorDrag)), getElement(elementDrop));
    }

    @When("^I switch to the last opened tab$")
    public void switchToLastTab() {
        if (BasePage.isDesktop()) {
            ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
            driver.switchTo().window(tabs.get(tabs.size() - 1));
        } else {
            info("Environment is not desktop. Skipping this step.");
        }
    }

    @When("^I switch to the first tab$")
    public void switchToFirstTab() {
        if (BasePage.isDesktop()) {
            ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
            driver.switchTo().window(tabs.get(0));
        } else {
            info("Environment is not desktop. Skipping this step.");
        }
    }

    @When("^I close current tab$")
    public void closeLastTab() {
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(tabs.size() - 1)).close();
        tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(tabs.size() - 1));
    }

    @When("^I click on \\*([^*]*)\\*$")
    public void clickOnSelector(String selector) {
        find(getSelector(selector)).click();
    }

    @When("^I click on \"([^\"]*)\"(?: with|) (?:index|number) \"([^\"]*)\"$")
    public void clickOnElementNumber(String element, String index) {
        if (Integer.valueOf(index) == 0) {
            index = "1";
        }
        getElements(element).get(getSavedNumericData(index) - 1).click();
    }

    @When("^I click on \\*([^*]*)\\*(?: with|)( saved|) (?:index|number) \"([^\"]*)\"$")
    public void clickOnSelectorNumber(String selector, String isSaved, String index) {
        int elementIndex = isSaved.isEmpty() ? Integer.valueOf(index) : data.get(index).returnSaved();
        findAll(getSelector(selector)).get(elementIndex).click();
    }

    @When("^I click( with javascript| with action|) on link like \'([^\"]*)\'$")
    public void clickOnPartialLinkText(String action, String linkByText) {
        info("Click on " + linkByText + " partial link text");
        linkByText = getSavedTextData(linkByText);
        if (action.isEmpty()) {
            find(By.partialLinkText(linkByText)).click();
        } else if (action.equals(" with action")) {
            find(By.partialLinkText(linkByText)).actionClick();
        } else if (action.equals(" with javascript")) {
            find(By.partialLinkText(linkByText)).clickWithJS();
        }
    }

    @When("^I switch to frame \"([^\"]*)\"$")
    public void switchToFrame(String element) {
        getElement(element).switchToIframe();
    }

    @When("^I switch to frame \\*([^*]*)\\*$")
    public void switchToFrameSelector(String selector) {
        find(getSelector(selector)).switchToIframe();
    }

    @When("^I switch to parent frame$")
    public void switchToParentFrame() {
        driver.switchTo().parentFrame();
    }

    @Then("^I (soft |)check that \"([^\"]*)\" has font weight \"([^\"]*)\"$")
    public void assertFontWeight(String soft, String element, String fontWeight) {
        String weight = getElement(element).getFontWeight();
        boolean softToggle = soft.contains("soft");
        if (softToggle) {
            softAssertion.assertEquals(weight, fontWeight, "Font weight is incorrect");
        } else {
            assertEquals(weight, fontWeight, "Font weight is incorrect");
        }
    }

    @Then("^I (soft |)check that \'([^\"]*)\' link has font weight \"([^\"]*)\"$")
    public void assertFontWeightLink(String soft, String linkByText, String fontWeight) {
        String weight = find(By.linkText(linkByText)).getFontWeight();
        boolean softToggle = soft.contains("soft");
        if (softToggle) {
            softAssertion.assertEquals(weight, fontWeight, "Font weight is incorrect");
        } else {
            assertEquals(weight, fontWeight, "Font weight is incorrect");
        }
    }

    @When("^I type on \"([^\"]*)\" text \"([^\"]*)\"$")
    public void typeElement(String element, String text) {
        String value = getSavedTextData(text);
        info("Type '" + value + "' into '" + element + "'");
        getElement(element).sendKeysAfterClear(value);
    }

    @When("^I type on \"([^\"]*)\" value (less than|more than|same as) \"([^\"]*)\" (on (\\d+)|)$")
    public void typeElementMoreLessValue(String element, String action, String text, int decrement) {
        String value = getSavedTextData(text);
        if (action.equals("more than")) {
            value = String.valueOf(Integer.valueOf(value) + decrement);

        } else if (action.equals("less than")) {
            value = String.valueOf(Integer.valueOf(value) - decrement);
        }
        info("Type '" + value + "' into '" + element + "'");
        getElement(element).sendKeysAfterClear(value);
    }

    @When("^I save current time(?: in format \"([^\"]*)\"|) value as \"([^\"]*)\"$")
    public void saveCurrentTimeValue(String pattern, String key) {
        String formatter = pattern == null ? "MMM-dd-yyyy, HH:mm:ss" : pattern;
        String time = DateUtils.parseStringFromDate(new Date(), DatePatterns.valueOf(formatter));
        info("Saving current time value - '" + time + "' as '" + key + "'");
        data.put(key, new SavedObjects(time));
    }

    @When("^I type on \\*([^*]*)\\* text \"([^\"]*)\"$")
    public void typeSelector(String selector, String text) {
        find(getSelector(selector)).sendKeysAfterClear(text);
    }

    @When("^I save response of(?: javascript|js|) code \"([^\"]*)\" as \"([^\"]*)\"$")
    public void saveJsResponse(String jsCode, String key) {
        data.put(key, new SavedObjects(executor.executeScript(jsCode).toString()));
    }

    @When("^I type on \"([^\"]*)\" element number (\\d+) text \"([^\"]*)\"$")
    public void typeElementNumber(String selector, int elementNumber, String text) {
        getElements(selector).get(elementNumber).sendKeysAfterClear(text);
    }

    @When("^I type on \\*([^*]*)\\* element number (\\d+) text \"([^\"]*)\"$")
    public void typeSelectorElementNumber(String selector, int elementNumber, String text) {
        findAll(getSelector(selector)).get(elementNumber).sendKeysAfterClear(text);
    }

    @Then("^I (soft |)check that \"([^\"]*)\" elements count is equal to '(.*)'$")
    public void assertNumberOfLinks(String soft, String element, int linksNumber) {
        boolean softToggle = soft.contains("soft");
        int elementsNumber = getElements(element).size();
        if (softToggle) {
            softAssertion.assertEquals(elementsNumber, linksNumber, element + " element number is incorrect");
        } else {
            assertEquals(elementsNumber, linksNumber, element + " element number is incorrect");
        }
    }

    @Then("^I (soft |)check that \\*([^*]*)\\* text is equal to \"([^\"]*)\"$")
    public void assertSelectorTextWithExpected(String soft, String selector, String expectedText) {
        String actualText = find(getSelector(selector)).getText();
        boolean softToggle = soft.contains("soft");
        if (softToggle) {
            softAssertion.assertEquals(actualText, unescapeJava(expectedText), selector + " selector text is incorrect");
        } else {
            assertEquals(actualText, unescapeJava(expectedText), selector + " selector text is incorrect");
        }
    }

    @Then("^I (soft |)check that title is equal to \"([^\"]*)\"$")
    public void assertTitleWithText(String soft, String expectedText) {
        boolean softToggle = soft.contains("soft");
        String titleText = driver.getTitle();
        if (softToggle) {
            softAssertion.assertEquals(titleText, expectedText, "Title is incorrect");
        } else {
            assertEquals(titleText, expectedText, "Title is incorrect");
        }
    }

    @Then("^I (soft |)check that \"([^\"]*)\" param \"([^\"]*)\" is equal to \"([^\"]*)\"$")
    public void assertSelectorAttributeWithExpectedText(String soft, String selector, String param, String expectedText) {
        boolean softToggle = soft.contains("soft");
        String paramValue = find(getSelector(selector)).getAttribute(param);
        if (softToggle) {
            softAssertion.assertEquals(paramValue, expectedText, selector + " attribute " + param + " is incorrect");
        } else {
            assertEquals(paramValue, expectedText, selector + " attribute " + param + " is incorrect");
        }
    }

    @Then("^I (soft |)check that(?: all | )\"([^\"]*)\" contain text \"([^\"]*)\"$")
    public void assertAllElementsContainText(String soft, String element, String text) {
        List<Element> elements = getElements(element);
        boolean softToggle = soft.contains("soft");
        if (elements.size() > 0) {
            String textToCompare = getSavedTextData(text);
            info("Verify that all '" + element + "' contain '" + textToCompare + "'");
            boolean check = true;
            for (int i = 0; i < elements.size(); i++) {
                if (!elements.get(i).getText().contains(textToCompare)) {
                    check = false;
                    info("Element number " + i + " doesn't contain needed text");
                    info("Actual " + elements.get(i).getText());
                    info("Expected " + text);
                }
            }
            if (softToggle) {
                softAssertion.assertTrue(check, "All elements " + element + " do not contain text " + text);
            } else {
                assertTrue(check, "All elements " + element + " do not contain text " + text);
            }
        } else {
            if (softToggle) {
                softAssertion.fail("Elements: " + element + " were not found");
            } else {
                fail("Elements: " + element + " were not found");
            }
        }
    }

    @When("^I click on text \"([^\"]*)\"(?: number (\\d+)|)$")
    public void clickOnText(String text, Integer number) {
        int num = number == null ? 0 : number;
        getElementsByText(text).get(num).click();
    }

    @When("^I double click on text \"([^\"]*)\"(?: number (\\d+)|)$")
    public void doubleClickOnText(String text, Integer number) {
        int num = number == null ? 0 : number;
        Element element = getElementsByText(text).get(num);
        element.scrollIntoViewport();
        element.doubleClick();
    }

    @When("^I hover over \"([^\"]*)\" and click on \"([^\"]*)\"(?: number (\\d+)|)$")
    public void hoverOverElementAndClick(String element1, String element2, Integer number) {
        getElement(element1).scrollIntoViewport();
        getElement(element1).mouseHover();
        int num = number == null ? 0 : number;
        getElements(element2).get(num).clickWithJS();
    }

    @When("^I hover over \"([^\"]*)\"(?: element number (\\d+)|)( on desktop|)$")
    public void hoverOverElement(String element1, Integer num, String device) {
        int number = num == null ? 1 : num;
        if (device.contains("desktop") && BasePage.isDesktop()) {
            getElements(element1).get(number - 1).mouseHover();
        } else if (device.isEmpty()) {
            getElements(element1).get(number - 1).mouseHover();
        } else {
            info("Environment is mobile. Skipping this step.");
        }
    }

    @When("^I wait for visibility of \"([^\"]*)\"")
    public void waitForElementToAppear(String element) {
        info("I wait for " + element + " to appear");
        getElement(element).waitUntilVisible();
    }

    @When("^I scroll down")
    public void scrollPageDown() {
        basePage.scrollDownThePage();
    }

    @When("^I scroll page down to (\\d+) percent")
    public void scrollPageDownToPercent(int percent) {
        basePage.scrollDownThePage(percent);
    }

    @When("^I scroll page down by (-?\\d+) pixels")
    public void scrollPageDownByPixels(int pixels) {
        basePage.scrollDownThePageByPixel(pixels);
    }

    @When("^I save \"([^\"]*)\"(?: with index \"([^\"]*)\"|) element text as \"([^\"]*)\"( in lowercase|)$")
    public void saveElementText(String element, String index, String key, String lowecase) {
        int elementIndex = index == null ? 0 : getSavedNumericData(index);
        if (elementIndex == 0) {
            elementIndex = 1;
        }
        String elementText = lowecase.isEmpty() ?
                getElements(element).get(elementIndex - 1).getText() :
                getElements(element).get(elementIndex - 1).getText().toLowerCase();
        info("Save '" + elementText + "' as '" + key + "'");
        data.put(key, new SavedObjects(elementText));
    }

    @When("^I save \"([^\"]*)\"(?: with index \"([^\"]*)\"|) text of parent \"([^\"]*)\"(?: with index \"([^\"]*)\"|) as \"([^\"]*)\"$")
    public void saveChildElementWithIndexText(String child, String childIndex, String parent, String parentIndex, String key) {
        int cIndex = childIndex == null ? 1 : data.get(childIndex).returnSaved();
        int pIndex = parentIndex == null ? 1 : data.get(parentIndex).returnSaved();
        data.put(key, new SavedObjects(getElements(parent).get(pIndex).findElements(map.get(child)).get(cIndex).getText()));
    }

    @When("^I save number of elements \"([^\"]*)\" of parent \"([^\"]*)\"(?: with index \"([^\"]*)\"|) as \"([^\"]*)\"$")
    public void saveChildElementsCount(String child, String parent, String parentIndex, String key) {
        int pIndex = parentIndex == null ? 0 : getSavedNumericData(parentIndex);
        data.put(key, new SavedObjects(getElements(parent).get(pIndex).findElements(map.get(child)).size()));
    }

    @When("^I click on \"([^\"]*)\"(?: with index \"([^\"]*)\"|) of parent \"([^\"]*)\"(?: with index \"([^\"]*)\"|)$")
    public void clickOnChildElementWithIndex(String child, String childIndex, String parent, String parentIndex) {
        int cIndex = childIndex == null ? 0 : data.get(childIndex).returnSaved();
        int pIndex = parentIndex == null ? 0 : data.get(parentIndex).returnSaved();
        info("Click on '" + child + "' #" + childIndex + " on '" + parent + "' #" + parentIndex);
        getElements(parent).get(pIndex).findElements(map.get(child)).get(cIndex).click();
    }

    @When("^I save( css|) attribute \"([^\"]*)\" of \"([^\"]*)\"(?: with index \"([^\"]*)\"|) as \"([^\"]*)\"$")
    public void saveElementAttribute(String isCss, String attr, String element, String index, String key) {
        int elementIndex = index == null ? 1 : getSavedNumericData(index);
        String attributeValue = isCss.isEmpty() ?
                getElements(element).get(elementIndex - 1).getAttribute(attr) :
                getElements(element).get(elementIndex - 1).getCssValue(attr);
        info("Save '" + attributeValue + "' as '" + key + "'");
        data.put(key, new SavedObjects(attributeValue));
    }

    @When("^I remove domain from \"([^\"]*)\"$")
    public void removeDomainFromSavedUrl(String key) {
        String url = (data.get(key).returnSaved());
        try {
            data.put(key, new SavedObjects(new URIBuilder(url).getPath()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    @Then("^I (soft |)check that \"([^\"]*)\"( not|) contains \"([^\"]*)\"( on desktop|)$")
    public void verifySavedElementContains(String soft, String key1, String contains, String key2, String isDesktop) {
        boolean softToggle = soft.contains("soft");
        boolean notContains = contains.equals(" not");
        if (BasePage.isDesktop() && !isDesktop.isEmpty()) {
            verifySavedTextContainsText(key1, key2, notContains, softToggle);
        } else if (isDesktop.isEmpty()) {
            verifySavedTextContainsText(key1, key2, notContains, softToggle);
        } else {
            info("Environment is mobile. Skipping this step.");
        }
    }

    public void verifySavedTextContainsText(String key1, String key2, boolean notContains, boolean softToggle) {
        String value1 = getSavedTextData(key1);
        String value2 = getSavedTextData(key2);
        info("Verify if '" + value1 + "' contains '" + value2 + "'");
        if (notContains) {
            if (softToggle) {
                softAssertion.assertFalse(value1.contains(value2), key1 + " should not contain " + key2);
            } else {
                assertFalse(value1.contains(value2), key1 + " should not contain " + key2);
            }
        } else {
            if (softToggle) {
                softAssertion.assertTrue(value1.contains(value2), key1 + " should contain " + key2);
            } else {
                assertTrue(value1.contains(value2), key1 + " should contain " + key2);
            }
        }
    }

    @Then("^I (soft |)check that \"([^\"]*)\" ends with \"([^\"]*)\"$")
    public void verifySavedElementEndsWithText(String soft, String key1, String key2) {
        boolean softToggle = soft.contains("soft");
        String value1 = getSavedTextData(key1);
        String value2 = getSavedTextData(key2);
        info("Verify if '" + value1 + "' ends with '" + value2 + "'");
        if (softToggle) {
            softAssertion.assertTrue(value1.endsWith(value2), value1 + " should end with '" + value2 + "'");
        } else {
            assertTrue(value1.endsWith(value2), value1 + " should end with '" + value2 + "'");
        }
    }

    @Then("^I (soft |)check that \"([^\"]*)\"( not|) equals( without spaces|)( ignore case|) to \"([^\"]*)\"( on mobile| on desktop|)$")
    public void verifySavedElementEqualsToText(String soft, String key1, String notToggle, String spaces, String ignoreCase, String key2, String device) {
        boolean softToggle = soft.contains("soft");
        boolean withoutSpaces = spaces.contains("without");
        boolean ignoringCase = ignoreCase.contains("ignore");
        boolean notEquals = notToggle.contains("not");
        if (device.contains("mobile") && BasePage.isMobile()) {
            savedElementEqualstoText(key1, key2, softToggle, withoutSpaces, ignoringCase, notEquals);
        } else if (device.contains("desktop") && BasePage.isDesktop()) {
            savedElementEqualstoText(key1, key2, softToggle, withoutSpaces, ignoringCase, notEquals);
        } else if (device.isEmpty()) {
            savedElementEqualstoText(key1, key2, softToggle, withoutSpaces, ignoringCase, notEquals);
        }
    }

    private void savedElementEqualstoText(String key1, String key2, boolean softToggle, boolean withoutSpaces, boolean ignoreCase, boolean notToggle) {
        String value1 = getSavedTextData(key1);
        String value2 = getSavedTextData(key2);
        if (withoutSpaces) {
            value1 = value1.replaceAll(" ", "").trim();
            value2 = value2.replaceAll(" ", "").trim();
        }
        if (ignoreCase) {
            value1 = value1.toLowerCase();
            value2 = value2.toLowerCase();
        }
        info("Verify if '" + value1 + "' equals to '" + value2 + "'");
        if (notToggle) {
            if (softToggle) {
                softAssertion.assertNotEquals(value1, value2, value1 + " should not be equal to '" + value2 + "'");
            } else {
                assertNotEquals(value1, value2, value1 + " should not be equal to '" + value2 + "'");
            }
        } else {
            if (softToggle) {
                softAssertion.assertEquals(value1, value2, value1 + " should be equal to '" + value2 + "'");
            } else {
                assertEquals(value1, value2, value1 + " should be equal to '" + value2 + "'");
            }
        }
    }

    @When("^I save \\*(.*)\\* element as \"([^\"]*)\"$")
    public void saveElement(String selector, String key) {
        data.put(key, new SavedObjects(find(getSelector(selector))));
    }

    @And("^I (soft |)check that rss feed loads properly with \"([^\"]*)\"$")
    public void iCheckThatHeadlinesRssFeedLoadsProperly(String soft, String adSlot) {
        boolean softToggle = soft.contains("soft");
        if (softToggle) {
            softAssertion.assertTrue(Utils.isPageSourceContains(adSlot), "RSS feed not loaded successfully");
        } else {
            assertTrue(Utils.isPageSourceContains(adSlot), "RSS feed not loaded successfully");
        }

    }

    @When("^I save current url as \"([^\"]*)\"( without params| without protocol|)$")
    public void saveCurrentURLs(String key, String params) {
        String url = driver.getCurrentUrl();
        if (params.contains("without params")) {
            try {
                url = new URIBuilder(url).clearParameters().toString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else if (params.contains("without protocol")) {
            if (url.contains("https")) {
                url = url.substring(8);
            } else if (url.contains("http")) {
                url = url.substring(7);
            }
        }
        data.put(key, new SavedObjects(url));
    }

    @When("^I save random(?: \"([^\"]*)\" of parent|) \"([^\"]*)\"(?: with index \"([^\"]*)\"|) element index as \"([^\"]*)\"$")
    public void saveChildElementIndex(String child, String parent, String parentIndex, String key) {
        if (child == null && parentIndex == null) {
            data.put(key, new SavedObjects(new Random().nextInt(getElements(parent).size())));
        } else if (child != null && parentIndex == null) {
            data.put(key, new SavedObjects(new Random().nextInt(getElement(parent).findElements(map.get(child)).size())));
        } else if (child != null && parentIndex != null) {
            int pIndex = data.get(parentIndex).returnSaved();
            data.put(key, new SavedObjects(new Random().nextInt(getElements(parent).get(pIndex).findElements(map.get(child)).size())));
        }
    }

    @When("^I save \"([^\"]*)\" with text \"([^\"]*)\" element index as \"([^\"]*)\"$")
    public void saveElementWithTextIndex(String element, String text, String key) {
        int elementsCount = getElements(element).size();
        String expectedText = getSavedTextData(text);
        for (int elementIndex = 0; elementIndex < elementsCount; elementIndex++) {
            if (getElements(element).get(elementIndex).getText().equals(expectedText)) {
                info("Save '" + elementIndex + "' as '" + key + "'");
                data.put(key, new SavedObjects(elementIndex));
                break;
            }
        }
    }

    @When("^I save \"([^\"]*)\" elements count as \"([^\"]*)\"$")
    public void saveElementsCount(String element, String key) {
        int elementsCount = getElements(element).size();
        info("There are " + elementsCount + " '" + element + "' elements on page");
        data.put(key, new SavedObjects(elementsCount));
    }

    @When("^I click \\*([^*]*)\\* on \"([^\"]*)\" if visible$")
    public void clickElementOnParentIfVisible(String element1, String element2) {
        try {
            getElement(element2).findElement(getSelector(element1)).click();
        } catch (ElementNotVisibleException | org.openqa.selenium.NoSuchElementException | NullPointerException e) {
            info("Element is not present. Skipping this step.");
        }
    }


    @When("I click(?: on|) random \"([^\"]*)\"$")
    public void clickRandomElement(String elements) {
        getElements(elements).get(new Random().nextInt(getElements(elements).size())).click();
    }

    @When("I click(?: on|) random \"([^\"]*)\" and save element text as \"([^\"]*)\"( on mobile| on desktop|)$")
    public void clickRandomElementAndSave(String elements, String key, String device) {
        String elementText = "";
        int elementIndex;
        if (device.contains("mobile") && BasePage.isMobile()) {
            elementIndex = StringUtils.generateRandomIntInRange(1, getElements(elements).size());
            elementText = getElements(elements).get(elementIndex).getText();
            getElements(elements).get(elementIndex).click();
            data.put(key, new SavedObjects(elementText));
        } else if (device.contains("desktop") && BasePage.isDesktop()) {
            elementIndex = StringUtils.generateRandomIntInRange(1, getElements(elements).size());
            elementText = getElements(elements).get(elementIndex).getText();
            getElements(elements).get(elementIndex).click();
            data.put(key, new SavedObjects(elementText));
        } else if (device.isEmpty()) {
            elementIndex = StringUtils.generateRandomIntInRange(1, getElements(elements).size());
            elementText = getElements(elements).get(elementIndex).getText();
            getElements(elements).get(elementIndex).click();
            data.put(key, new SavedObjects(elementText));
        }
    }

    @When("^I click(?: on|) \"([^\"]*)\" if visible$")
    public void clickElementIfVisible(String element) {
        try {
            getElement(element).click();
        } catch (NoSuchElementException | ElementNotInteractableException | NullPointerException | JavascriptException e) {
            info("Element is not present. Skipping this step.");
        }
    }

    @Then("^I (soft |)check that( every| each| all|) \"([^\"]*)\" has visible \"([^\"]*)\"( on desktop| on mobile|)$")
    public void verifyEveryElementHasChild(String soft, String isEveryElement, String parentElement, String childElement, String device) {
        boolean softToggle = soft.contains("soft");
        if (device.contains("mobile") && BasePage.isMobile()) {
            everyElementHasChild(isEveryElement, parentElement, childElement, softToggle);
        } else if (device.contains("desktop") && BasePage.isDesktop()) {
            everyElementHasChild(isEveryElement, parentElement, childElement, softToggle);
        } else if (device.isEmpty()) {
            everyElementHasChild(isEveryElement, parentElement, childElement, softToggle);
        }
    }

    private void everyElementHasChild(String isEveryElement, String parentElement, String childElement, boolean softToggle) {
        boolean check = true;
        int parentElementsCount = isEveryElement.isEmpty() ? 1 : getElements(parentElement).size();
        info("Verify if every '" + parentElement + "' has visible '" + childElement + "'");
        for (int parentIndex = 0; parentIndex < parentElementsCount; parentIndex++) {
            Element parent = getElements(parentElement).get(parentIndex);
            parent.scrollIntoViewport();
            int childElementsCount = parent.findElements(map.get(childElement)).size();
            if (childElementsCount == 0) {
                info("Child elements were not found on " + parentElement);
                check = false;
                break;
            }
            for (int childIndex = 0; childIndex < childElementsCount; childIndex++) {
                Element child = new Element(parent.findElements(map.get(childElement)).get(childIndex));
                child.scrollIntoViewport();
                if (!child.isVisible()) {
                    info(childElement + " #" + childIndex + " is not visible on " + parentElement + " #" + parentIndex);
                    check = false;
                    break;
                }
            }
        }
        if (softToggle) {
            softAssertion.assertTrue(check, "Not all elements " + parentElement + " have visible child element " + childElement);
        } else {
            assertTrue(check, "Not all elements " + parentElement + " have visible child element " + childElement);
        }
    }

    @Then("^I (soft |)check that(?: every| each| all|) \"([^\"]*)\" has (non-empty|empty)(| css) \"([^\"]*)\" attribute( on desktop| on mobile|)$")
    public void verifyEveryElementHasNonEmptyAttribute(String soft, String element1, String verification, String isCss, String attribute, String device) {
        boolean softToggle = soft.contains("soft");
        if (device.contains("mobile") && BasePage.isMobile()) {
            everyElementHasEmptyNonEmptyAttr(element1, verification, isCss, attribute, softToggle);
        } else if (device.contains("desktop") && BasePage.isDesktop()) {
            everyElementHasEmptyNonEmptyAttr(element1, verification, isCss, attribute, softToggle);
        } else if (device.isEmpty()) {
            everyElementHasEmptyNonEmptyAttr(element1, verification, isCss, attribute, softToggle);
        }
    }

    private void everyElementHasEmptyNonEmptyAttr(String element1, String verification, String isCss, String attribute, boolean softToggle) {
        boolean check = true;
        int elementsCount = getElements(element1).size();
        List<Element> elements = getElements(element1);
        for (int elementIndex = 0; elementIndex < elementsCount; elementIndex++) {
            if (isCss.contains("css")) {
                if (elements.get(elementIndex).getCssValue(attribute).isEmpty()) {
                    info(element1 + " has empty attribute - '" + attribute + "'");
                    check = false;
                }
            } else {
                if (elements.get(elementIndex).getAttribute(attribute).isEmpty()) {
                    info(element1 + " has empty attribute - '" + attribute + "'");
                    check = false;
                }
            }
        }
        if (verification.equals("non-empty")) {
            if (softToggle) {
                softAssertion.assertTrue(check, "Not all elements: " + element1 + " has " + verification + " " + isCss + " attribute");
            } else {
                assertTrue(check, "Not all elements: " + element1 + " has " + verification + " " + isCss + " attribute");
            }
        } else {
            if (softToggle) {
                softAssertion.assertFalse(check, "Not all elements: " + element1 + " has " + verification + " " + isCss + " attribute");
            } else {
                assertFalse(check, "Not all elements: " + element1 + " has " + verification + " " + isCss + " attribute");
            }
        }
    }

    @Then("^I check that saved value \"([^\"]*)\"(?: is greater than| is more than| >) (\\d+)$")
    public void verifyValueIsMoreThanGiven(String value, int expectedValue) {
        int savedValue = data.get(value).returnSaved();
        assertTrue(savedValue > expectedValue, savedValue + " should be greater than " + expectedValue);
    }

    @Then("^I (soft |)check that \"([^\"]*)\"(?: with index \"([^\"]*)\"|) is in view port$")
    public void verifyElementIsInViewPort(String soft, String element, String index) {
        boolean softToggle = soft.contains("soft");
        int elementIndex = index == null ? 0 : getSavedNumericData(index);
        info("Verify if element '" + element + "' with index '" + elementIndex + "' is in view port");
        if (softToggle) {
            softAssertion.assertTrue(getElements(element).get(elementIndex).isInViewPort(), element + " is not in viewport");
        } else {
            assertTrue(getElements(element).get(elementIndex).isInViewPort(), element + " is not in viewport");
        }
    }

    @Then("^I (soft |)check that \\*([^*]*)\\*(?: with index \"([^\"]*)\"|) is in view port$")
    public void verifySelectorIsInViewPort(String soft, String selector, String index) {
        boolean softToggle = soft.contains("soft");
        int elementIndex = index == null ? 0 : getSavedNumericData(index);
        info("Verify if selector '" + selector + "' with index '" + elementIndex + "' is in view port");
        if (softToggle) {
            softAssertion.assertTrue(findAll(getSelector(selector)).get(elementIndex).isInViewPort(), "Element woth selector " + selector + " is not in viewport");
        } else {
            assertTrue(findAll(getSelector(selector)).get(elementIndex).isInViewPort(), "Element woth selector " + selector + " is not in viewport");
        }
    }

    @Then("^I (soft |)check that \"([^\"]*)\" is( not|) equal to \"([^\"]*)\"$")
    public void verifySavedCountIsEqual(String soft, String key1, String equals, String key2) {
        boolean softToggle = soft.contains("soft");
        int value1 = getSavedNumericData(key1);
        int value2 = getSavedNumericData(key2);
        info("Verify if '" + value1 + " is equal to '" + value2 + "'");
        if (equals.contains("not")) {
            if (softToggle) {
                softAssertion.assertNotEquals(value1, value2, key1 + " and " + key2 + " should not be equal");
            } else {
                assertNotEquals(value1, value2, key1 + " and " + key2 + " should not be equal");
            }
        } else {
            if (softToggle) {
                softAssertion.assertEquals(value1, value2, key1 + " and " + key2 + " should be equal");
            } else {
                assertEquals(value1, value2, key1 + " and " + key2 + " should be equal");
            }
        }
    }

    @When("^I set timeout for all actions for (\\d+) ms$")
    public void setTimeoutForAllActions(Integer timeout) {
        defaultSleep = timeout;
    }

    @When("^I scroll \"([^\"]*)\"(?: with index \"([^\"]*)\"|) into viewport$")
    public void scrollElementIntoViewPort(String element, String index) {
        int elementIndex = isEmpty(index) ? 1 : getSavedNumericData(index);
        getElements(element).get(elementIndex - 1).scrollIntoViewport();
        waitForJSAndJQueryToLoad();
    }

    @When("^I click on (?:all|every) \"([^\"]*)\"$")
    public void clickOnEveryElement(String element) {
        int elementsCount = getElements(element).size();
        info("There are " + elementsCount + " " + element + "s on page");
        while (getElements(element).size() > 0) {
            getElements(element).get(0).click();
        }
        waitForJSAndJQueryToLoad();
    }

    @When("^I scroll to \"([^\"]*)\" with \"([^\"]*)\" percent into viewport$")
    public void scrollToSeePercentElement(String element, int percent) {
        if (!element.equals("-")) {
            Element el = getElement(element);
//			basePage.scrollToSeePercentOfElement(el, percent);
            basePage.scrollToSeePercentOfElementNew(el, percent);
        }
    }

    @When("^I scroll to top of element \"([^\"]*)\" into viewport$")
    public void scrollToSeeTopOfElement(String element) {
        if (!element.equals("-")) {
            Element el = getElement(element);
            basePage.scrollToTopOfElement(el);
        }
    }

    @And("^I add to current url key \"([^\"]*)\"(?:| value \"([^\"]*)\")( in qa| in staging|)$")
    public void addParamsToCurrentUrl(String key, String value, String env) {
        if (env.contains("qa") && settings.isQA()) {
            adParamToUrl(key, value);
        } else if (env.contains("staging") && settings.isStaging()) {
            adParamToUrl(key, value);
        } else if (isEmpty(env)) {
            adParamToUrl(key, value);
        }
    }

    private void adParamToUrl(String key, String value) {
        String currentUrl = driver.getCurrentUrl();
        try {
            driver.get(new URIBuilder(currentUrl).addParameter(key, value).toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @And("^I select in \"([^\"]*)\" drop down text \"([^\"]*)\"$")
    public void selectTextInDropdown(String element, String text) {
        getElement(element).selectByVisibleText(text);
    }

    @Then("^I (soft |)check that \"([^\"]*)\" list is( not|) equal to \"([^\"]*)\"$")
    public void verifySavedListIsEqual(String soft, String key1, String equals, String key2) {
        boolean softToggle = soft.contains("soft");
        ArrayList list1 = getSavedList(key1);
        ArrayList list2 = getSavedList(key2);
        info("Verify if '" + list1 + " is equal to '" + list2 + "'");
        if (equals.contains("not")) {
            if (softToggle) {
                softAssertion.assertNotEquals(list1, list2, key1 + " and " + key2 + " should not be equal");
            } else {
                assertNotEquals(list1, list2, key1 + " and " + key2 + " should not be equal");
            }
        } else {
            if (softToggle) {
                softAssertion.assertEquals(list1, list2, key1 + " and " + key2 + " should be equal");
            } else {
                assertEquals(list1, list2, key1 + " and " + key2 + " should be equal");
            }
        }
    }

    public String getSavedTextData(String key) {
        try {
            info("Get saved value for '" + key + "'");
            return data.get(key).returnSaved();
        } catch (NullPointerException e) {
            try {
                info("Given data was not saved before this step. Looking for an element with this name.");
                return getElement(key).getText();
            } catch (NullPointerException | NoSuchSessionException e1) {
                info("Given data was not saved before this step. Using as plain text.");
                return key;
            }
        }
    }

    public ArrayList<List> getSavedList(String key) {
        try {
            info("Get saved list for '" + key + "'");
            return data.get(key).returnSaved();
        } catch (NullPointerException e) {
            Logger.info("Given data was not saved before this step. Using as it is.");
            return null;
        }
    }

    public int getSavedNumericData(String key) {
        try {
            info("Get saved value for '" + key + "'");
            return data.get(key).returnSaved();
        } catch (NullPointerException e) {
            info("Given data was not saved before this step. Using as it is.");
            return Integer.valueOf(key);
        }
    }

    @And("^I check that (\\d+) window(?:|s) (?:is|are) opened$")
    public void iCheckThatWindowIsOpened(int number) {
        Logger.info("Check that " + number + " tab(s) is(are) opened");
        int windowHandles = driver.getWindowHandles().size();
        assertEquals(windowHandles, number, number + " window(s) should be opened");
    }

    @And("I save random string with length {int} as {string}")
    public void iSaveRandomStringWithLengthAs(int length, String key) {
        String s = StringUtils.generateRandomStrAlphabetic(length);
        data.put(key, new SavedObjects(s));
    }

    @And("^I (soft |)check that current url( is| is not| equals to| not equals to| contains| not contains| ends with) text \"([^\"]*)\"( without params|)$")
    public void iCheckCurrentUrlContains(String soft, String isEquals, String text, String withoutParams) {
        boolean softToggle = soft.contains("soft");
        text = getSavedTextData(text);
        info("Validation is current url text " + isEquals + " to\n" + text);
        waitForJSAndJQueryToLoad();
        String currentUrl = Utils.getCurrentURL();
        if (withoutParams.contains("without params")) {
            currentUrl = currentUrl.split("[?]")[0];
            text = text.split("[?]")[0];
        }
        if (isEquals.contains("equals") || isEquals.contains("is")) {
            if (!isEquals.contains("not")) {
                if (softToggle) {
                    softAssertion.assertEquals(currentUrl, text, "Current url is not equals to " + text);
                } else {
                    assertEquals(currentUrl, text, "Current url is not equals to " + text);
                }
            } else {
                if (softToggle) {
                    softAssertion.assertNotEquals(currentUrl, text, "Current url should not be  equals to " + text);
                } else {
                    assertNotEquals(currentUrl, text, "Current url should not be  equals to " + text);
                }
            }
        } else if (isEquals.contains("ends with")) {
            if (softToggle) {
                softAssertion.assertTrue(currentUrl.endsWith(text), "Current url: " + currentUrl + " should end with text: " + text);
            } else {
                assertTrue(currentUrl.endsWith(text), "Current url: " + currentUrl + " should end with text: " + text);
            }
        } else {
            if (!isEquals.contains("not")) {
                if (softToggle) {
                    softAssertion.assertTrue(currentUrl.contains(text), "Current url: " + currentUrl + " should contain text: " + text);
                } else {
                    assertTrue(currentUrl.contains(text), "Current url: " + currentUrl + " should contain text: " + text);
                }
            } else {
                if (softToggle) {
                    softAssertion.assertFalse(currentUrl.contains(text), "Current url: " + currentUrl + " should contain text: " + text);
                } else {
                    assertFalse(currentUrl.contains(text), "Current url: " + currentUrl + " should contain text: " + text);
                }
            }
        }
    }

    @And("^I (soft |)check that page source (contains|does not contain) text \"(.*)\"$")
    public void iCheckPageSourceContainsText(String soft, String containsToggle, String text) {
        boolean softToggle = soft.contains("soft");
        if (containsToggle.contains("not")) {
            if (!text.isEmpty()) {
                if (softToggle) {
                    softAssertion.assertFalse(Utils.isPageSourceContains(text), "Page source does not contain text: " + text);
                } else {
                    assertFalse(Utils.isPageSourceContains(text), "Page source does not contain text: " + text);
                }
            }
        } else {
            if (!text.isEmpty()) {
                if (softToggle) {
                    softAssertion.assertTrue(Utils.isPageSourceContains(text), "Page source does not contain text: " + text);
                } else {
                    assertTrue(Utils.isPageSourceContains(text), "Page source does not contain text: " + text);
                }
            }
        }
    }

    @And("^I (soft |)check elements list \"([^\"]*)\" (contains|does not contain) text \"([^\"]*)\"( on mobile| on desktop|)$")
    public void iCheckElementsListContainsTest(String soft, String elements, String containTag, String text, String device) {
        text = getSavedTextData(text);
        boolean softToggle = soft.contains("soft");
        if (device.contains("mobile") && BasePage.isMobile()) {
            verifyListContains(elements, containTag, text, softToggle);
        } else if (device.contains("desktop") && BasePage.isDesktop()) {
            verifyListContains(elements, containTag, text, softToggle);
        } else if (device.isEmpty()) {
            verifyListContains(elements, containTag, text, softToggle);
        }
    }

    private void verifyListContains(String elements, String containTag, String text, boolean softToggle) {
        List<Element> elementsList = getElements(elements);
        List<String> list = new ArrayList<>();
        elementsList.forEach(it -> list.add(it.getText()));
        if (containTag.contains("not")) {
            if (softToggle) {
                softAssertion.assertFalse(list.contains(text), "Elements list should not contain text " + text);
            } else {
                assertFalse(list.contains(text), "Elements list should not contain text " + text);
            }
        } else {
            if (softToggle) {
                softAssertion.assertTrue(list.contains(text), "Elements list should not contain text " + text);
            } else {
                assertTrue(list.contains(text), "Elements list should not contain text " + text);
            }
        }
    }



    @And("I assert all verifications")
    public void iAssertAll() {
        softAssertion.assertAll();
    }

    @And("^I set screen(?: height to \"([^\"]*)\"|)(?: and|)(?: width to \"([^\"]*)\"|)$")
    public void iSetScreenHeightTo(String height, String width) {
        if (isDesktop()) {
            Dimension dimension;
            int currentHeight;
            int currentWidth;
            if (isEmpty(width)) {
                width = String.valueOf(driver.manage().window().getSize().getWidth());
            } else if (isEmpty(height)) {
                height = String.valueOf(driver.manage().window().getSize().getHeight());
            }
            //wee need to add additional values as it takes into account window header(80 px), footer(10px) and automation header height(42px)
            currentHeight = (Integer.parseInt(height) + 90 + 42);
            currentWidth = Integer.parseInt(width);
            dimension = new Dimension(currentWidth, currentHeight);
            driver.manage().window().setSize(dimension);
        }
    }

    @And("I open a new blank tab")
    public void iOpenANewTab() {
        ((JavascriptExecutor) driver).executeScript("window.open('about:blank','_blank');");
    }

    @And("I add possible issue to next step \"([^\"]*)\"")
    public void iAddPossibleIssueToNextStep(String issue) {
        Logger.knownIssue(issue);
    }
}
