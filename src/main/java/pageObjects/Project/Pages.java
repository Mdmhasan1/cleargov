package pageObjects.Project;

import pageObjects.allTemplates.BasePage;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URIBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.*;

import static framework.Logger.info;

public class Pages extends BasePage {

    Properties properties = new Properties();
    ArrayList<String> urls = new ArrayList<>();

    public String getUrl(String pageName) {
        properties = loadPropertiesFile();
        if (StringUtils.isEmpty(properties.getProperty(pageName))) {
            if (settings.isQA()) {
                return properties.getProperty(pageName + ".qa");
            } else {
                return properties.getProperty(pageName + "." + settings.getEnv());
            }
        } else {
            return properties.getProperty(pageName);
        }
    }

    public void goToPageWithParams(String URL) {
        if (!URL.contains(".com") && !URL.contains(".org")) {
            URL = settings.getEnvironment() + URL;
        }
        try {
            String newURL = new URIBuilder(URL)
                    .addParameter("xid", "test_xid")
                    .addParameter("test_ads", "onctest1")
                    .addParameter("cid", "my_cid").toString();
            driver.get(newURL);
        } catch (URISyntaxException e) {
            info("Failed to append URL with parameter");
        }
    }

    private Properties loadPropertiesFile() {
        try {
            InputStream stream = getClass().getClassLoader().getResourceAsStream("templates/urlTemplatesList.properties");
            if (stream == null) {
                stream = new FileInputStream(new File("templates/urlTemplatesList.properties"));
            }
            Properties result = new Properties();
            result.load(stream);
            return result;
        } catch (IOException e) {
            info("Properties file was not found. Check path.");
            return null;
        }
    }
}

