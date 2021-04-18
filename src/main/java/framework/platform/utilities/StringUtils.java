package framework.platform.utilities;

import framework.Logger;
import framework.platform.DatePatterns;
import framework.platform.Utils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.Assert.assertEquals;
import static pageObjects.allTemplates.BasePage.log;

public class StringUtils {

    private static Set<String> uniqueEmails = new HashSet<>();

    public static synchronized String generateMPTQATimestampEmail() {
        String email = mptEmail();
        while (uniqueEmails.contains(email)) {
            Utils.waitFor(1000);
            email = mptEmail();
        }
        uniqueEmails.add(email);
        return email;
    }

    private static String mptEmail() {
        return ("TestAutomation" + DateUtils.getCurrentDate(DatePatterns.MMDDYYYYHHmmss) + StringUtils.generateRandomNumeric(3) + "@mailinator.com").toLowerCase();
    }

    public static int generateRandomIntInRange(int lowerBound, int upperBound) {
        return ThreadLocalRandom.current().nextInt(lowerBound, upperBound);
    }

    public static synchronized String generateRandomEmail() {
        return (DateUtils.getCurrentDate(DatePatterns.MM_dd_HH_mm_ss) + RandomStringUtils.randomAlphanumeric(7) + "@mailinator.com").toLowerCase();
    }

    public static String convertHTMLCharacters(String text) {
        text = Jsoup.parse(text).text();
        return text;
    }

    public boolean isAtLeastOneWordPresentInSentence(String word, String sentence) {
        boolean isPartPresent = false;
        for (String part : word.split(" ")) {
            if (sentence.contains(part)) {
                isPartPresent = true;
                break;
            }
        }
        return isPartPresent;
    }

    public boolean isSortedAsc(String firstStr, String secondStr) {
        String digitsString = "1234567890";
        firstStr = firstStr.toLowerCase().replace(" ", "");
        secondStr = secondStr.toLowerCase().replace(" ", "");
        if (digitsString.contains(firstStr.substring(0, 1))) {
            if (!digitsString.contains(secondStr.substring(0, 1))) {
                return true; //digit is greater, than other character in postgres
            }
        }
        if (!digitsString.contains(firstStr.substring(0, 1))) {
            if (digitsString.contains(secondStr.substring(0, 1))) {
                return false; //digit is greater, than other character in postgres
            }
        }
        for (int i = 0; i < (Math.min(firstStr.length(), secondStr.length())); i++) {
            if (firstStr.charAt(i) == ' ') {
                if (secondStr.charAt(i) != ' ') {
                    return false; //space is greater, than other character in postgres
                }
            }
            if (firstStr.charAt(i) != secondStr.charAt(i)) {
                return firstStr.charAt(i) < secondStr.charAt(i); //different chars on one position
            }
        }
        return firstStr.length() <= secondStr.length(); //one of strings contains another
    }

    public boolean isSortedAscLexicographically(String firstStr, String secondStr) {
        return firstStr.compareToIgnoreCase(secondStr) <= 0; // second word is equal or lexicographically follows firstStr
    }

    public ArrayList<String> getAlphabetCharacters() {
        ArrayList<String> alphabetCharacters = new ArrayList<>();
        for (char letter = 'A'; letter <= 'Z'; letter++) {
            alphabetCharacters.add(String.valueOf(letter));
        }
        return alphabetCharacters;
    }

    public static String decodeString(String decodeStr) {
        String firstStr = null;
        try {
            firstStr = java.net.URLDecoder.decode(decodeStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String secondStr = null;
        try {
            assert firstStr != null;
            secondStr = java.net.URLDecoder.decode(firstStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return secondStr;
    }

    public static String getHexColor(String rgb) {
        log("Get hex color code for rgb value");
        int i = Integer.parseInt((rgb.replace("rgba(", "")).replace(")", "").split(",")[0]);
        int j = Integer.parseInt((rgb.replace("rgba(", "")).replace(")", "").split(",")[1].trim());
        int k = Integer.parseInt((rgb.replace("rgba(", "")).replace(")", "").split(",")[2].trim());

        Color c = new Color(i, j, k);
        String hexValue = "#" + Integer.toHexString(c.getRGB() & 0x00ffffff);
        String colour = "";
        if (hexValue.equals("#333333")) {
            colour = "black";
        } else if (hexValue.equals("#999999")) {
            colour = "grey";
        }
        Logger.info("Hex color code for rgb " + rgb + " is " + hexValue + " " + colour);
        return hexValue;
    }

    private String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    public int countSubstring(String subStr, String str) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.substring(i).startsWith(subStr)) {
                count++;
            }
        }
        return count;
    }

    public String convertToSha1Hash(String data) {
        byte[] sha1hash = new byte[40];
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(data.getBytes(StandardCharsets.UTF_8), 0, data.length());
            sha1hash = md.digest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toHexString(sha1hash);
    }

    public String decodeHTMLString(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            Logger.info("Decoding failed. Returning raw string.");
        }
        return value;
    }


    public static String generateRandomNumeric(int length) {
        return RandomStringUtils.randomNumeric(length);
    }

    public static String generateRandomStrAlphabetic(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }

    public static String convertASCICharacters(String s) {
        Pattern pattern = Pattern.compile("u(\\p{XDigit}{4})");
        Matcher matcher = pattern.matcher(s);

        while (matcher.find()) {
            s = s.replace(matcher.group(), "\\" + matcher.group());
        }
        return StringEscapeUtils.unescapeJava(s);
    }

    public static String convertASCICharacters2(String s) {
        Pattern pattern = Pattern.compile("&#(\\d{3});");
        Matcher matcher = pattern.matcher(s);

        while (matcher.find()) {
            String value = matcher.group().split("&#")[1].split(";")[0];
            if (value.startsWith("0")) {
                value = value.replaceFirst("0", "");
            }
            s = s.replace(matcher.group(), Character.toString((char) (int) Integer.valueOf(value)));
        }
        return StringEscapeUtils.unescapeJava(s);
    }

    public static boolean compareWithoutSpaces(String s1, String s2) {
        return s1.replaceAll("\\s+|<br />", "").equals(s2.replaceAll("\\s+|<br />", ""));
    }

    public static void compareWithoutSpaces(String s1, String s2, String assertionMessage) {
        assertEquals(s1.replaceAll("\\s+|<br />", ""), s2.replaceAll("\\s+|<br />", ""), assertionMessage);
    }

    public static boolean startsWithSpecialCharacter(String s) {
        String first = s.substring(0, 1);
        return (first == null) ? false : first.matches("[^A-Za-z0-9 ]");
    }

}
