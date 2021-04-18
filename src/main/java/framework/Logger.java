package framework;

import org.slf4j.LoggerFactory;

/**
 * Logger
 */
public class Logger {

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(Logger.class);

    public static void info(String log) {
        logger.info(log);
        //BasePage.log(log);
    }

    public static void err(String log) {
        logger.error(log);
    }

    public static void knownIssue(String log) {
        logger.info("Possible issue in next step: " + log);
        logger.info("BUG: https://everydayhealth.atlassian.net/browse/" + log /*+ "</br>"*/);
    }

    public static void debug(String log) {
        logger.debug(log);
    }
}
