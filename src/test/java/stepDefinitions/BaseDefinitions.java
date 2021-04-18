package stepDefinitions;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import framework.Logger;
import framework.platform.Settings;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import pageObjects.allTemplates.BasePage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static pageObjects.allTemplates.BasePage.*;

public class BaseDefinitions {

    private static Settings settings = new Settings();
    BasePage page = new BasePage();

    private String getRemoteUrl() {
        return "https://" + settings.getRemoteBrowserStackUsername() + ":" + settings.getRemoteBrowserStackAutomateKey() + settings.getRemoteBrowserStackHost();
    }

    @Before(order = 1)
    public void baseSetup(Scenario scenario) {
        BasePage.scenario = scenario;
    }

    @Before(value = "not @noWeb and not @proxy", order = 2)
    public void setUp() throws MalformedURLException {
        BasePage.settings = settings;
        DesiredCapabilities caps = new DesiredCapabilities();
        switch (settings.getProperty("device")) {
            case "desktop":
                switch (settings.getProperty("browser")) {
                    case "chrome":
                        ChromeOptions chromeOptions = new ChromeOptions();
                        LoggingPreferences loggingPreferences = new LoggingPreferences();
                        if (settings.isBrowserLoggingEnabled()) {
                            loggingPreferences.enable(LogType.BROWSER, Level.ALL);
                        }
                        if (settings.isNetworkLoggingEnabled()) {
                            loggingPreferences.enable(LogType.PERFORMANCE, Level.ALL);
                        }
                        chromeOptions.setCapability("goog:loggingPrefs", loggingPreferences);
                        chromeOptions.setCapability(CapabilityType.SUPPORTS_ALERTS, true);
                        chromeOptions.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
                        if (settings.isHeadlessChrome()) {
                            chromeOptions.setHeadless(true);
                            chromeOptions.addArguments("--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors", "--silent");
                        }
                        chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
                        if (settings.isAutoScale()) {
                            setAutoscaleParams(chromeOptions);
                        }
                        driver = new RemoteWebDriver(new URL(settings.getRemoteHubUrl()), chromeOptions);
                        driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
                        driver.manage().timeouts().setScriptTimeout(120, TimeUnit.SECONDS);
                        driver.manage().window().maximize();
                        break;
                    case "firefox":
                        caps.setCapability("browser", "Firefox");
                        break;
                    case "safari":
                        caps.setCapability("browser", "Safari");
                        break;
                    case "edge":
                        caps.setCapability("browser", "Edge");
                        break;
                    case "ie11":
                        caps.setCapability("browser", "IE");
                        caps.setCapability("browser_version", "11.0");
                        break;
                    default:
                        ChromeOptions options = new ChromeOptions();
                        LoggingPreferences loggingPreferencesDefault = new LoggingPreferences();
                        System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/chromedriver");
                        System.setProperty("webdriver.chrome.silentOutput", "true");
                        if (settings.isBrowserLoggingEnabled()) {
                            loggingPreferencesDefault.enable(LogType.BROWSER, Level.ALL);
                        }
                        if (settings.isNetworkLoggingEnabled()) {
                            loggingPreferencesDefault.enable(LogType.PERFORMANCE, Level.ALL);
                        }
                        options.setCapability("goog:loggingPrefs", loggingPreferencesDefault);
                        options.setCapability(CapabilityType.SUPPORTS_ALERTS, true);
                        if (settings.isHeadlessChrome()) {
                            options.setHeadless(true);
                            options.addArguments("--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors", "--silent");
                        }
                        if (settings.isAutoScale()) {
                            setAutoscaleParams(options);
                        }
                        options.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
                        options.setPageLoadStrategy(PageLoadStrategy.NONE);

                        driver = new ChromeDriver(options);
                        driver.manage().window().maximize();
                        break;
                }
            case "mobile":
                caps.setCapability("realMobile", "true");
                caps.setCapability("browserstack.local", "true");
                caps.setCapability("browserstack.debug", "true");
                caps.setCapability("browserstack.console", "info");
                caps.setCapability("browserstack.networkLogs", "true");

                switch (settings.getProperty("browser")) {
                    case "iOS":
                        caps.setCapability("os_version", "11");
                        caps.setCapability("device", "iPhone 8");
                        driver = new IOSDriver(new URL(getRemoteUrl()), caps);
                        break;
                    case "Android":
                        caps.setCapability("device", "Google Pixel 2");
                        caps.setCapability("os_version", "8.0");
                        LoggingPreferences loggingPreferencesDefault = new LoggingPreferences();
                        loggingPreferencesDefault.enable(LogType.BROWSER, Level.ALL);
                        driver = new AndroidDriver(new URL(getRemoteUrl()), caps);
                        break;
                }
                break;
            case "tablet":
                caps.setCapability("realMobile", "true");
                caps.setCapability("browserstack.local", "true");
                caps.setCapability("browserstack.debug", "true");
                caps.setCapability("browserstack.console", "info");
                caps.setCapability("browserstack.networkLogs", "true");
                switch (settings.getProperty("browser")) {
                    case "iOS":
                        caps.setCapability("device", "iPad 6th");
                        caps.setCapability("os_version", "11.3");
                        driver = new IOSDriver(new URL(getRemoteUrl()), caps);
                        break;
                    case "Android":
                        caps.setCapability("device", "Samsung Galaxy Tab S3");
                        caps.setCapability("os_version", "7.0");
                        driver = new AndroidDriver(new URL(getRemoteUrl()), caps);
                        break;
                }
                break;
        }
        driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
    }

    private void setAutoscaleParams(ChromeOptions chromeOptions) {
        chromeOptions.addArguments("--disable-notifications");
        chromeOptions.addArguments("--whitelisted-ips");
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--disable-extensions");
        chromeOptions.setHeadless(true);
        chromeOptions.addArguments("--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors", "--silent");
    }

    //is supported only in chrome
    @Before(value = "@proxy", order = 2)
    public void setUpProxy() throws MalformedURLException {
        BasePage.settings = settings;
        ChromeOptions chromeOptions = new ChromeOptions();
        LoggingPreferences loggingPreferences = new LoggingPreferences();
        if (settings.getProperty("browser").equals("chrome")) {
            if (settings.isBrowserLoggingEnabled()) {
                loggingPreferences.enable(LogType.BROWSER, Level.ALL);
            }
            if (settings.isNetworkLoggingEnabled()) {
                loggingPreferences.enable(LogType.PERFORMANCE, Level.ALL);
            }
            chromeOptions.setCapability("goog:loggingPrefs", loggingPreferences);
            chromeOptions.setCapability(CapabilityType.SUPPORTS_ALERTS, true);
            if (settings.isHeadlessChrome()) {
                chromeOptions.setHeadless(true);
                chromeOptions.addArguments("--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors", "--silent");
            }
            if (settings.isAutoScale()) {
                setAutoscaleParams(chromeOptions);
            }
            chromeOptions.setCapability("goog:loggingPrefs", loggingPreferences);
            chromeOptions.setCapability(CapabilityType.SUPPORTS_ALERTS, true);
            chromeOptions.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
            proxy = createProxy();
            seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
            chromeOptions.setCapability(CapabilityType.PROXY, ClientUtil.createSeleniumProxy(proxy));
            chromeOptions.setPageLoadStrategy(PageLoadStrategy.NONE);
            chromeOptions.setAcceptInsecureCerts(true);
            driver = new RemoteWebDriver(new URL(settings.getRemoteHubUrl()), chromeOptions);
        } else {
            LoggingPreferences loggingPreferencesDefault = new LoggingPreferences();
            System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/chromedriver");
            System.setProperty("webdriver.chrome.silentOutput", "true");
            if (settings.isBrowserLoggingEnabled()) {
                loggingPreferencesDefault.enable(LogType.BROWSER, Level.ALL);
            }
            if (settings.isNetworkLoggingEnabled()) {
                loggingPreferencesDefault.enable(LogType.PERFORMANCE, Level.ALL);
            }
            chromeOptions.setCapability("goog:loggingPrefs", loggingPreferencesDefault);
            chromeOptions.setCapability(CapabilityType.SUPPORTS_ALERTS, true);
            chromeOptions.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
            if (settings.isHeadlessChrome()) {
                chromeOptions.setHeadless(true);
                chromeOptions.addArguments("--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors", "--silent");
            }
            proxy = createProxy();
            chromeOptions.setCapability(CapabilityType.PROXY, ClientUtil.createSeleniumProxy(proxy));
            chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
            chromeOptions.setAcceptInsecureCerts(true);
            driver = new ChromeDriver(chromeOptions);
        }
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
    }

    @After("not @noWeb")
    public void tearDown() {
        if (isDesktop()) {
            if (scenario.isFailed()) {
                page.takeScreenshot();
            }
            driver.close();
            driver.quit();
        } else {
            try {
                driver.close();
                driver.quit();
            } catch (Exception e) {
                Logger.info("Driver is already closed");
            }
        }
        if (proxy != null) {
            proxy.stop();
        }
    }

    public BrowserMobProxyServer createProxy() {
        BrowserMobProxyServer proxyServer = new BrowserMobProxyServer();
        proxyServer.start(0);
        proxyServer.setHarCaptureTypes(CaptureType.getAllContentCaptureTypes());
        proxyServer.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT, CaptureType.RESPONSE_HEADERS);
        return proxyServer;
    }
}
