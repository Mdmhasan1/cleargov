package pageObjects.allTemplates;

import cucumber.api.Scenario;
import framework.Element;
import framework.Logger;
import framework.platform.Settings;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.lightbody.bmp.BrowserMobProxy;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pageObjects.allTemplates.globalFunctionality.Header;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static framework.Element.defaultSleep;
import static framework.Logger.info;
import static framework.platform.Utils.waitFor;
import static pageObjects.allTemplates.BaseSelectors.map;

public class BasePage {

    public static WebDriver driver;
    public static BrowserMobProxy proxy;
    public static Proxy seleniumProxy;
    public static Scenario scenario;
    public static Settings settings;
    protected static RequestSpecification request;
    protected static Response response;
    public static Header header = new Header();

    public By a = By.cssSelector("a");
    public By p = By.cssSelector("p");
    public By body = By.cssSelector("body[data-product-id='277']");
    public By label = By.cssSelector("label");
    public By pageHeader = By.cssSelector("h1.page_header, h1.mpt-content-headline,header h1");

    public BasePage() {
        map.put("base page header", pageHeader);
    }

    private static final int PAGE_LOAD_MAXIMUM_SECONDS = 10;

    public void openPage(String url) {
        info("URL - " + settings.getEnvironment() + url);
        driver.get(settings.getEnvironment() + url);
        if (isDesktop()) {
            driver.manage().window().maximize();
        }
    }

    public void openClearGovPage(String url) {
        log("URL - " + settings.getEnvironment() + url);
        driver.get(settings.getEnvironment() + url);
        if (isDesktop()) {
            driver.manage().window().maximize();
        }
    }

    public static Element find(By by) {
        Element element = new Element(null);
        WebDriverWait wait = new WebDriverWait(driver, 15);
        if (waitForJSAndJQueryToLoad()) {
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(by));
                element = new Element(driver.findElement(by));
            } catch (WebDriverException | NullPointerException e) {
            }
        }
        return element;
    }

    public static List<Element> findAll(By by) {
        List<Element> list = new ArrayList<>();
        WebDriverWait wait = new WebDriverWait(driver, 15);
        if (waitForJSAndJQueryToLoad()) {
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(by));
                list.addAll(driver.findElements(by).stream().map(Element::new).collect(Collectors.toList()));
            } catch (WebDriverException | NullPointerException e) {
            }
        }
        return list;
    }

    public static List<String> getTextFromAllElements(By by) {
        List<Element> all = findAll(by);
        List<String> list = new ArrayList<>();
        for (Element el : all) {
            list.add(el.getText());
        }
        return list;
    }

    public int getVisibleElementsCount(List<Element> elements) {
        int visibleElementsCount = 0;
        int numberOfElements = elements.size();
        for (int elementIndex = 0; elementIndex < numberOfElements; elementIndex++) {
            if (elements.get(elementIndex).isVisible()) {
                visibleElementsCount++;
            }
        }
        return visibleElementsCount;
    }

    public void scrollDownThePage(int percentage) {
        ((JavascriptExecutor) driver)
                .executeScript("window.scrollBy({\n" +
                        "    top: document.body.scrollHeight*\n" + 0.01 * percentage + "," +
                        "    behavior: \"smooth\"\n" +
                        "});");
        sleep(1000);
    }

    public void scrollDownThePageByPixel(int pixels) {
        ((JavascriptExecutor) driver)
                .executeScript("window.scrollBy(0," + pixels + ")");
        sleep(1000);
    }

    public void scrollDownThePage() {
        ((JavascriptExecutor) driver)
                .executeScript("window.scrollBy(0,document.body.scrollHeight);");
        sleep(1000);
    }

    public void scrollDownThePageSlowly() {
        String jsResult = getJSResult(" return document.body.scrollHeight");
        for (int i = 0; i < (Integer.valueOf(jsResult) / 5); i++) {
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,5)", "");
            waitFor(3);
        }
    }

    public void scrollDownThePageFast() {
        String jsResult = getJSResult(" return document.body.scrollHeight");
        for (int i = 0; i < Integer.valueOf(jsResult); i += 30) {
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,25)", "");
            waitFor(1);
        }
    }

    public static String getJSResult(String script) {
        return ((JavascriptExecutor) driver).executeScript(script).toString();
    }

    public static void executeJS(String js) {
        ((JavascriptExecutor) driver).executeScript(js);
    }

    public Element quickFind(By by) {
        return quickFind(by, 15);
    }

    public Element quickFind(By by, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(by));
            return new Element(driver.findElement(by));
        } catch (TimeoutException | NoSuchElementException ignored) {
            return new Element(null);
        }
    }

    public static boolean waitForJSAndJQueryToLoad() {
        WebDriverWait wait = new WebDriverWait(driver, PAGE_LOAD_MAXIMUM_SECONDS);

        ExpectedCondition<Boolean> jsLoad = driver -> ((JavascriptExecutor) driver)
                .executeScript("return document.readyState")
                .toString().equals("complete");

        ExpectedCondition<Boolean> jQueryLoad = driver -> {
            if (settings.isMobile()) {
                return true;
            }
            try {
                return ((Long) ((JavascriptExecutor) driver).executeScript("return jQuery.active") == 0);
            } catch (NullPointerException | WebDriverException t) {
                return false;
            }
        };

        try {
            return wait.until(jsLoad) && wait.until(jQueryLoad);
        } catch (TimeoutException | NullPointerException t) {
            return true;
        }
    }

    public void scrollToSeePercentOfElement(Element element, int percent) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        String locator = element.getLocatorString();
        jse.executeScript("function scrollToElement(selector, percents = 50, speed = 1000) {\n" +
                "    $('html, body')\n" +
                "        .animate({\n" +
                "            scrollTop: ($(selector).offset().top - $(window).height() + ($(selector).height()*(percents/100)))\n" +
                "        }, speed);  \n" +
                "}\n" +
                "scrollToElement(\"" + locator + "\",\"" + percent + "\")");

    }

    public void scrollToSeePercentOfElementNew(Element element, int percent) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        String locator = element.getLocatorString();
        jse.executeScript("function scrollToElement(selector, percents = 50, speed = 1000){\n" +
                "const element = document.querySelector(selector);\n" +
                "if (element !== null) {\n" +
                "const elementTop = element.offsetTop;\n" +
                "const elementHeight = element.offsetHeight;\n" +
                "const scrollTo = Math.round(elementHeight * (percents/100));\n" +
                "window.scrollBy({top:elementTop - window.innerHeight + scrollTo, left: 0, behavior: 'smooth'});\n" +
                "}" +
                "}" +
                "scrollToElement(\"" + locator + "\",\"" + percent + "\")");

    }

    public void scrollToTopOfElement(Element element) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        String locator = element.getLocatorString();
        jse.executeScript("window.scrollTo(0,document.querySelector(\"" + locator + "\").getBoundingClientRect().bottom-document.querySelector(\"" + locator + "\").getBoundingClientRect().height)");
    }

    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void log(String log) {
        scenario.write(log);
    }

    public void takeScreenshot() {
        scenario.embed(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES), "image/png");
    }

    public static boolean isMobile() {
        return !settings.getDevice().equals("desktop");
    }

    public static boolean isDesktop() {
        return settings.getDevice().equals("desktop");
    }

    protected void switchToParentIframe() {
        driver.switchTo().parentFrame();
    }

    public void switchToLastOpenedWindow() {
        String currentWindow = driver.getWindowHandle();
        Set<String> windows = driver.getWindowHandles();
        if (windows.size() > 1) {
            for (String window : windows) {
                driver.switchTo().window(window);
                if (!driver.getWindowHandle().equals(currentWindow)) {
                    driver.switchTo().window(window);
                }
            }
        }
    }

    public void closeTab() {
        driver.close();
    }

    public boolean isCurrentURLContains(String text) {
        if (text.length() < 30) {
            Logger.info("Verify if current URL contains text '" + text + "'");
        }
        return driver.getCurrentUrl().contains(text);
    }

    public static void dragAndDrop(WebElement element, WebElement destination) {
        sleep(defaultSleep);
        new Actions(driver).dragAndDrop(element, destination).perform();
    }

    public static void dragAndDrop(WebElement element, int xOffset, int yOffsetn) {
        sleep(defaultSleep);
        new Actions(driver).dragAndDropBy(element, xOffset, yOffsetn).build().perform();
    }

    protected void waitForAjaxRequestToBeFinished(int timeoutInMilliseconds) {
        int sleepTime = 500;
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        try {
            for (int i = 0; i < timeoutInMilliseconds / sleepTime; i++) {
                waitFor(sleepTime / 2);
                if ((Boolean) jse.executeScript(
                        "return document.readyState == 'complete' && window.jQuery != undefined && jQuery.active == 0")) {
                    return;
                }
                waitFor(sleepTime / 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void acceptAlert() {
        driver.switchTo().alert().accept();
    }

    public void waitForAjaxRequestToBeFinished() {
        waitForAjaxRequestToBeFinished(5000);
    }

}

