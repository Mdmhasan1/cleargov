package framework;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import pageObjects.allTemplates.BasePage;
import pageObjects.Project.Pages;
import framework.platform.*;

import java.util.List;

import static pageObjects.allTemplates.BasePage.*;
import static framework.Logger.info;
import static framework.platform.Utils.waitFor;

public class Element implements WebElement {

    private WebElement element;
    private BasePage page = new BasePage();

    public Element(WebElement element) {
        this.element = element;
    }

    //Sleep before each action for slow and unstable pages
    public static int defaultSleep = 0;

    @Override
    public void click() {
        sleep(defaultSleep);
        Pages.waitForJSAndJQueryToLoad();
        scrollIntoViewport();
        try {
            element.click();
        } catch (StaleElementReferenceException e) {
            sleep(1000);
            element.click();
        }
        Pages.waitForJSAndJQueryToLoad();
    }

    public WebElement getWebElement() {
        return element;
    }

    public void rightClick() {
        new Actions(driver).contextClick(element).build().perform();
    }

    public Css getElementCss() {
        return new Css(element);
    }

    public void scrollIntoView() {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(false);", element);
        sleep(500);
    }

    public void scrollIntoViewport() {
        if (!BasePage.isMobile()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
            sleep(500);
        } else {
            int elementY = element.getLocation().getY();
            int currentLocation = Integer.parseInt(BasePage.getJSResult("return document.body.scrollTop;"));
            int visibleY = 500;
            Logger.debug("elementY: " + elementY);
            Logger.debug("currentLocation: " + currentLocation);
            if (elementY > visibleY || currentLocation > 10) {
                BasePage.executeJS("window.scrollBy(0," + (elementY - 450) + ")");
                waitFor(500);
            }
        }
    }

    public void doubleClick() {
        sleep(defaultSleep);
        new Actions(driver).doubleClick(getWebElement()).build().perform();
    }

    public void mouseHover() {
        new Actions(driver).moveToElement(element).build().perform();
    }

    public boolean isEmpty() {
        try {
            return element.getText().trim().length() == 0;
        } catch (NullPointerException | TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    public int getNumberOfChildElements(By by) {
        return element.findElements(by).size();
    }

    public boolean isChildElementVisible(By by) {
        try {
            Element element = findElement(by);
            element.scrollIntoView();
            return element.isVisible();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    @Override
    public void submit() {
        element.submit();
//		page.takeScreenshot();
    }

    public void switchToIframe() {
        try {
            driver.switchTo().frame(element);
        } catch (InvalidSelectorException | NullPointerException n) {
            try {
                driver.switchTo().frame(element.getAttribute("name"));
            } catch (NullPointerException n1) {
                info("Driver is unable to switch to frame, maybe because it's already switched");
            } catch (InvalidSelectorException n3) {
                info("Invalid selector or frame doesn't contains name or id element. Trying find by Element");
                driver.switchTo().frame((WebElement) element);
            }
        }
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        sleep(defaultSleep);
        scrollIntoViewport();
        element.sendKeys(keysToSend);
        //page.takeScreenshot();
        Pages.waitForJSAndJQueryToLoad();
    }

    public void sendKeysAfterClear(CharSequence... keysToSend) {
        element.click();
        element.clear();
        sendKeys(keysToSend);
    }

    public String getBackgroundColor() {
        return element.getCssValue("background-color");
    }

    public String getColor() {
        return element.getCssValue("color");
    }

    public int getXCoordinate() {
        return element.getLocation().getX();
    }

    public int getWidth() {
        return element.getSize().getWidth();
    }
    public int getHeight() {
        return element.getSize().getHeight();
    }

    public int getYCoordinate() {
        return element.getLocation().getY();
    }

    public String getFontWeight() {
        return element.getCssValue("font-weight");
    }

    @Override
    public void clear() {
        element.clear();
    }

    @Override
    public String getTagName() {
        return element.getTagName();
    }


    @Override
    public String getAttribute(String name) {
        return element.getAttribute(name);
    }

    @Override
    public boolean isSelected() {
        return element.isSelected();
    }

    @Override
    public boolean isEnabled() {
        return element.isEnabled();
    }

    @Override
    public String getText() {
        return element.getText();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return element.findElements(by);
    }

    @Override
    public Element findElement(By by) {
        return new Element(element.findElement(by));
    }

    @Override
    public boolean isDisplayed() {
        try {
            return element.isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isVisible() {
        try {
            return element.isDisplayed();
        } catch (NullPointerException e) {
            return false;
        } catch (StaleElementReferenceException i) {
            return true;
        }
    }

    @Override
    public Point getLocation() {
        return element.getLocation();
    }

    @Override
    public Dimension getSize() {
        return element.getSize();
    }

    @Override
    public Rectangle getRect() {
        return element.getRect();
    }

    @Override
    public String getCssValue(String propertyName) {
        return element.getCssValue(propertyName);
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        return element.getScreenshotAs(target);
    }

    public void selectByVisibleText(String value) {
        new Select(element).selectByVisibleText(value);
    }

    public void selectByNumber(int number) {
        new Select(element).selectByIndex(number);
    }

    public void clickWithJS() {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", element);
    }

    public Element waitUntilVisible() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOf(element));
        return this;
    }

    public Element waitUntilInVisible() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.invisibilityOf(element));
        return this;
    }

    public void actionClick() {
        sleep(defaultSleep);
        new Actions(driver).click(element).build().perform();
    }

    public String getCssContent() {
        return ((JavascriptExecutor) driver).executeScript
                ("return window.getComputedStyle(arguments[0], '::before').content;", element).toString();
    }

    public String getCssContentIcon() {
        return ((JavascriptExecutor) driver).executeScript
                ("return window.getComputedStyle(arguments[0], '::before').content.charCodeAt(1).toString(16);", element).toString();
    }

    public WebElement goToFirstATag() {
        if (element.getTagName().equals("a")) {
            return element;
        } else {
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            return (WebElement) executor.executeScript("return arguments[0].parentNode;", element);
        }
    }

    public boolean isInViewPort() {
        boolean jsResult = (Boolean) ((JavascriptExecutor) driver).executeScript("return $(arguments[0]).is(':in-viewPort')", element);
        return jsResult;
    }

    public boolean hasTargetBlank() {
        try {
            return element.getAttribute("target").equals("_blank");
        } catch (NullPointerException n) {
            info("Element has no attribute 'target' or element is not present");
            return false;
        }
    }

    public void dragAndDrop(int x, int y) {
        new Actions(driver).dragAndDropBy(element, x, y);
    }

    public String getLocatorString() {
        String locator = getWebElement().toString().split("->")[1].replace(" css selector:", "").replace("xpath:", "").trim();
        locator = locator.substring(0, locator.lastIndexOf("]"));
        log("Element locator is " + locator);
        return locator;
    }

    public int getRectYForElementNumber(int number) {
        String locatorString = getLocatorString();
        return Math.round(Float.valueOf(BasePage.getJSResult(" return document.querySelectorAll(\"" + locatorString + "\")[" + (number) + "].getBoundingClientRect().y")));
    }

    public String getValue() {
        return element.getAttribute("value").trim();
    }

    public int getCenterCoordinate() {
        return getXCoordinate() + getWidth() / 2;
    }
}