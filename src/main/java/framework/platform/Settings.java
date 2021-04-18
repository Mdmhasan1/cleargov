package framework.platform;

import pageObjects.allTemplates.BasePage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static framework.Logger.info;

public class Settings extends BasePage {

    private Properties properties;
    private static final String PROPERTIES_FILE = "selenium.properties";

    public Settings() {
        properties = loadPropertiesFile();
    }

    public String getProperty(String name) {
        properties = loadPropertiesFile();

        String result = System.getProperty(name, null);
        if ((result) != null && result.length() > 0) {
            return result;
        }
        result = properties.getProperty(name);
        if ((result) != null && result.length() > 0) {
            return result;
        }
        return result;
    }

    public String getSitefinityStage3Url() {
        return getProperty("sitefinity.staging-3.url");
    }

    public String getSitefinityUser() {
        return getProperty("sitefinity.user");
    }

    public String getPropertyFromFile(String property) {
        return getProperty(property);
    }

    public String getProdUrl() {
        return getProperty("env.prod.url");
    }

    public String getStageUrl() {
        return getProperty("env.stage.url");
    }

    public String getQA1Url() {
        return getProperty("env.qa1.url");
    }

    public String getQA2Url() {
        return getProperty("env.qa2.url");
    }

    public String getEnv() {
        return getProperty("env");
    }

    public String getDheClfUrl() {
        return getProperty("dhe.image.url");
    }

    public String getRecircClfUrl() {
        return getProperty("recirc.image.url");
    }

    public String getDevice() {
        return getProperty("device");
    }

    public String getRemoteBrowserStackUsername() {
        return getProperty("remote.username");
    }

    public String getRemoteBrowserStackAutomateKey() {
        return getProperty("remote.automate.key");
    }

    public String getRemoteHubUrl() {
        if(isAutoScale()){
            return getProperty("remote.hub.url.autoscale");
        }else {
            return getProperty("remote.hub.url");
        }
    }

    public String getRemoteBrowserStackHost() {
        return getProperty("remote.browserstack.host");
    }

    private Properties loadPropertiesFile() {
        try {
            String filename = PROPERTIES_FILE;
            InputStream stream = getClass().getClassLoader().getResourceAsStream(filename);

            if (stream == null) {
                stream = new FileInputStream(new File(filename));
            }
            Properties result = new Properties();
            result.load(stream);
            return result;
        } catch (IOException e) {
            info("Properties file was not found. Check path.");
            return null;
        }
    }

    public String getEnvironment() {
        switch (getEnv()) {
            case "prod":
                return getProdUrl();
            case "stage":
                return getStageUrl();
            case "qa1":
                return getQA1Url();
            case "qa2":
                return getQA2Url();
            default:
                log("Incorrect or empty environment was added" + getEnv());
        }
        return null;
    }

    public String getEnvironmentORG() {
        return getEnvironment().replace(".com", ".org");
    }

    public boolean isBrowserLoggingEnabled() {
        return Boolean.parseBoolean(getProperty("browser.logs.enabled"));
    }

    public boolean isNetworkLoggingEnabled() {
        return Boolean.parseBoolean(getProperty("network.logs.enabled"));
    }

    public String getDefaultUserPassword() {
        return getProperty("default.user.password");
    }

    public String getMainUserEmail() {
        return getProperty("main.user.email");
    }

    public String getOldUserpassword() {
        return getProperty("old.user.password");
    }

    public String getDbDriver() {
        return getProperty("db.driver");
    }

    public String getDBConnection() {
        return getProperty("sql.connection." + BasePage.settings.getEnv());
    }

    public String getDBUser() {
        return getProperty("db.user." + BasePage.settings.getEnv());
    }

    public String getPassword() {
        return getProperty("db.password." + BasePage.settings.getEnv());
    }

    public boolean isQA() {
        return isQA1() || isQA2();
    }

    public boolean isQA1() {
        return getEnv().equals("qa1");
    }

    public boolean isQA2() {
        return getEnv().equals("qa2");
    }

    public boolean isStaging() {
        return getEnv().equals("stage");
    }

    public boolean isProd() {
        return getEnv().equals("prod");
    }

    public String getModifiedUrl(String subDomain) {
        String publicUrl = getEnvironment();
        if (isQA() || isStaging()) {
            publicUrl = publicUrl.replaceFirst("qa1", subDomain + "qa1").replaceFirst("qa2", subDomain + "qa2").replaceFirst("staging", subDomain + "staging");
        } else {
            publicUrl = publicUrl.replace("www", subDomain);
        }
        return publicUrl;
    }

    public String getInpBaseUrl() {
        switch (getEnv()) {
            case "qa1":
                return getQA1INPBaseUrl();
            case "qa2":
                return getQA2INPBaseUrl();
            case "stage":
                return getStageINPBaseUrl();
            default:
                return "";
        }
    }

    public String getAnamnesisXMLBaseUrl() {
        switch (getEnv()) {
            case "qa1":
            case "qa2":
                return getQAAnamnesisXMLBaseUrl();
            case "stage":
                return getStageAnamnesisXMLBaseUrl();
            default:
                return "";
        }
    }

    public String getSitemapIndexXMLBaseUrl() {
        return getProperty("sitemapIndex");
    }

    private String getStageINPBaseUrl() {
        return getProperty("inp.stage.baseUrl");
    }

    private String getQA1INPBaseUrl() {
        return getProperty("inp.qa1.baseUrl");
    }

    private String getQA2INPBaseUrl() {
        return getProperty("inp.qa2.baseUrl");
    }

    private String getQAAnamnesisXMLBaseUrl() {
        return getProperty("anamnesisXML.qa.baseUrl");
    }

    private String getStageAnamnesisXMLBaseUrl() {
        return getProperty("anamnesisXML.stage.baseUrl");
    }

    public String getBaseUrlForInpPage() {
        String baseUrl;
        if (settings.getEnv().equals("prod")) {
            baseUrl = settings.getEnvironment().replace("https://", "https://inp").replace("www", "");
        } else {
            baseUrl = settings.getEnvironment().replace("https://", "https://inp-");
        }
        return baseUrl;
    }
    public String getBaseUrlForPreviewPage() {
        String baseUrl;
        if (settings.getEnv().equals("prod")) {
            baseUrl = settings.getEnvironment().replace("https://", "https://preview").replace("www", "");
        } else {
            baseUrl = settings.getEnvironment().replace("https://", "https://preview-");
        }
        return baseUrl;
    }

    public String getBaseUrlForSubDomainPage(String subdomain) {
        String baseUrl;
        if (settings.getEnv().equals("prod")) {
            baseUrl = settings.getEnvironment().replace("https://", "https://" + subdomain).replace("www", "");
        } else {
            baseUrl = settings.getEnvironment().replace("https://", "https://" + subdomain + "-");
        }
        return baseUrl;
    }

    public boolean isHeadlessChrome() {
        return Boolean.parseBoolean(getProperty("headless"));
    }

    public boolean isAutoScale() {
        return Boolean.parseBoolean(getProperty("grid.autoScale"));
    }
}

