package framework.platform;

import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;

import static pageObjects.allTemplates.BasePage.*;

public class Utils {

    public static void waitFor(int miliseconds) {
        try {
            Thread.sleep(miliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getCurrentURL() {
        return driver.getCurrentUrl();
    }

    public static String getCurrentURLwithoutPrams() {
        String url = driver.getCurrentUrl();
        url = url.split("[?]")[0];
        return url;
    }

    public static boolean isPageSourceContains(String value) {
        if (value.length() < 50) {
            //	Logger.info("Verify " + value + " value (not) exists on page: " + driver.getCurrentUrl());
        }
        return driver.getPageSource().toLowerCase().contains(value.toLowerCase());
    }

    public static String getCanonicalLinkFromThePageSource() {
        String pagesource = driver.getPageSource();
        String canonicalLink = pagesource.split("<link rel=\"canonical\" href=\"")[1].split("\"")[0];
        try {
            canonicalLink = new URIBuilder(canonicalLink).clearParameters().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return canonicalLink;
    }

    public static String changeProdUrl(String url) {
        if (settings.isQA()) {
            if (settings.isQA1()) {
                return url.replaceFirst("www", "qa1");

            } else {
                return url.replaceFirst("www", "qa2");
            }
        } else if (settings.isStaging()) {
            return url.replaceFirst("www", "staging");
        } else {
            return url;
        }
    }


    public static String getItemFromSessionStorage(String name) {
        return getJSResult(String.format(
                "return window.sessionStorage.getItem('%s');", name));
    }
}

