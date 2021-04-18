package framework.platform.utilities;

import framework.Logger;
import framework.platform.DatePatterns;
import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtils {
    private static DateTime currentDate = new DateTime();

    public static String convertOnePatternToOther(String dateString, DatePatterns datePattern1, DatePatterns datePattern2) {
        Date date = parseDateFromString(dateString, datePattern1);
        return parseStringFromDate(date, datePattern2);
    }

    public int getDifferenceInYears(int day, int month, int year) {
        return (int) ChronoUnit.YEARS.between(
                LocalDate.of(year, month, day),
                LocalDate.now());
    }

    public String getCurrentDateString() {
        return currentDate.toString(DatePatterns.EEEE_MMMMMMMMM_d.getPattern(), Locale.US);
    }

    public static String getCurrentDate(DatePatterns datePattern) {
        return new DateTime().toString(datePattern.getPattern(), Locale.US);
    }

    public static Date getCurrentDate() {
        GregorianCalendar now = new GregorianCalendar();
        return now.getTime();
    }

    public static Date parseDateFromString(String dateAsString, DatePatterns datePatterns) {
        Logger.debug("Parsing date from string " + dateAsString + " with expected date pattern " + datePatterns.getPattern() + ".");
        try {
            return new SimpleDateFormat(datePatterns.getPattern(), Locale.US).parse(dateAsString);
        } catch (ParseException ignored) {
            throw new RuntimeException("Date could not be parsed.");
        }
    }

    public static String getDateInFuture(int days, DatePatterns pattern) {
        return currentDate.plusDays(days).toString(pattern.getPattern(), Locale.US);
    }

    public static String getDateInFuture(int days) {
        return getDateInFuture(days, DatePatterns.EEEE_MMMMMMMMM_d);
    }

    public String getDayInFuture(int days) {
        return currentDate.plusDays(days).toString("d", Locale.US);
    }

    public String getDateInPast(int days) {
        return getDateInPast(days, DatePatterns.EEEE_MMMMMMMMM_d);
    }

    private String getDateInPast(int days, DatePatterns pattern) {
        return currentDate.minusDays(days).toString(pattern.getPattern(), Locale.US);
    }

    public String getDayInPast(int days) {
        return currentDate.minusDays(days).toString("d", Locale.US);
    }

    public int getIntDayInPast(int days) {
        return Integer.parseInt(currentDate.minusDays(days).toString("d", Locale.US));
    }

    public DateTime getPastDate(int month) {
        return currentDate.minusMonths(month);
    }

    public DateTime getFutureDate(int month) {
        return currentDate.plusMonths(month);
    }

    public Date addDaysToDate(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public int getCurrentHours(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public boolean isWeekEndsInTheSameMonth(String weekDates) {
        boolean inOneMonth = true;
        String weekEnd = weekDates.split("-")[1].trim();
        if (Character.isLetter(weekEnd.charAt(0))) {
            inOneMonth = false;
        }
        return inOneMonth;
    }

    public String formattedDateTime() {
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MMddyyyHHmmSS");
        return dateFormat.format(currentDate);
    }

    public static String parseStringFromDate(Date date, DatePatterns datePatterns) {
        DateFormat destDf = new SimpleDateFormat(datePatterns.getPattern(), Locale.US);
        String defDate = destDf.format(date);
        return defDate;
    }
}

