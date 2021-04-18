package framework.platform.utilities;

import framework.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;

import static org.testng.Assert.fail;
import static pageObjects.allTemplates.BasePage.driver;

public class JsonUtils {
    private String stringJson = "";


    public void setStringJson(String stringJson) {
        this.stringJson = stringJson;
    }

    public String getJsonObjectValue(String objectName) {
        JSONObject json1 = new JSONObject(stringJson);
        if (json1.has(objectName)) {
            return json1.get(objectName).toString();
        }
        return "";
    }

    public String getJsonObjectValues(String objectName) {
        Logger.info("Getting " + objectName + " from json body");
        JSONObject obj = getJSONObject(0);
        try {
            return obj.getString(objectName);
        } catch (JSONException e) {
            fail("Object " + objectName + " not found");
        }
        return "";
    }

    private JSONObject getJSONObject(int objectNumber) {
        String json = driver.findElement(By.xpath("//body")).getText();
        JSONArray message;
        try {
            if (!json.startsWith("[")) {
                message = new JSONArray("[" + json + "]");
            } else {
                message = new JSONArray(json);
            }
            JSONObject obj = (JSONObject) message.get(objectNumber);
            return obj;
        } catch (JSONException e) {
            json = stringJson;
            try {
                if (!json.startsWith("[")) {
                    message = new JSONArray("[" + json + "]");
                } else {
                    message = new JSONArray(json);
                }
                JSONObject obj = (JSONObject) message.get(objectNumber);
                return obj;
            } catch (JSONException e1) {
                Logger.info("Object was not found");
            }
        }
        return null;
    }
}



