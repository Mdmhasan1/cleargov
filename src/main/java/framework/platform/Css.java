package framework.platform;

import org.openqa.selenium.WebElement;

import static framework.Logger.info;

public class Css {

    private String fontFamily;
    private String fontSize;
    private String textAlign;
    private String color;
    private String fontWeight;

    public Css(WebElement element) {
        this.fontFamily = element.getCssValue("font-family").split(",")[0].replaceAll("\"", "");
        this.fontSize = element.getCssValue("font-size");
        this.textAlign = element.getCssValue("text-align");
        this.color = element.getCssValue("color");
        this.fontWeight = element.getCssValue("font-weight");
    }

    public Css(String fontFamily, String fontSize, String textAlign, String color, String fontWeight) {
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
        this.textAlign = textAlign;
        this.color = color;
        this.fontWeight = fontWeight;
    }

    public Css(String textFormatting) {
        String[] formatting = textFormatting.split(";");
        for (String format : formatting) {
            if (format.contains("font-family")) {
                this.fontFamily = format.split(":")[1].trim();
                continue;
            }
            if (format.contains("font-size")) {
                this.fontSize = format.split(":")[1].trim();
                continue;
            }
            if (format.contains("text-align")) {
                this.textAlign = format.split(":")[1].trim();
                continue;
            }
            if (format.contains("font-weight")) {
                this.fontWeight = format.split(":")[1].trim();
                continue;
            }
            if (format.contains("color")) {
                this.color = format.split(":")[1].trim();
            }
        }
    }

    public boolean equals(Css css) {
        boolean check = true;
        if (css.fontFamily != null && !this.fontFamily.equals(css.fontFamily)) {
            info("font-family attributes are not equals");
            info("Expected " + this.fontFamily);
            info("Actual " + css.fontFamily);
            check = false;
        }
        if (css.fontSize != null && !this.fontSize.equals(css.fontSize)) {
            info("font-size attributes are not equals");
            info("Expected " + this.fontSize);
            info("Actual " + css.fontSize);
            check = false;
        }
        if (css.textAlign != null && !this.textAlign.equals(css.textAlign)) {
            info("text-align attributes are not equals");
            info("Expected " + this.textAlign);
            info("Actual " + css.textAlign);
            check = false;
        }
        if (css.color != null && !this.color.equals(css.color)) {
            info("color attributes are not equals");
            info("Expected " + this.color);
            info("Actual " + css.color);
            check = false;
        }
        if (css.fontWeight != null && !this.fontWeight.equals(css.fontWeight)) {
            info("font-weight attributes are not equals");
            info("Expected " + this.fontWeight);
            info("Actual " + css.fontWeight);
            check = false;
        }

        return check;
    }
}

