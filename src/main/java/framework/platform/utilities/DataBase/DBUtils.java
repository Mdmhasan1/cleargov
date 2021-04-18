package framework.platform.utilities.DataBase;

import framework.Logger;
import framework.platform.utilities.JsonUtils;
import org.testng.annotations.AfterClass;

import java.sql.*;
import java.text.ParseException;

import static org.testng.FileAssert.fail;
import static pageObjects.allTemplates.BasePage.settings;

public class DBUtils {
    private static String CONNECTION;
    private static String USER;
    private static String PASSWORD;
    private static final String DRIVER = settings.getDbDriver();
    private Connection connection;
    private JsonUtils jsonUtils = new JsonUtils();

    protected synchronized Connection getConnection() {
        Logger.info("Checking if connection is opened");
        if (connection == null) {
            try {
                Class.forName(DRIVER);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            Logger.info("The connection has been closed. Opening a new one");
            try {
                CONNECTION = settings.getDBConnection();
                USER = settings.getDBUser();
                PASSWORD = settings.getPassword();
                connection = DriverManager.getConnection(CONNECTION, USER, PASSWORD);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    private <T> T processSql(String sql, ResultSetFunction<T> function) {
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            T result = function.apply(resultSet);
            Logger.info("Result from DB: " + result);
            return result;
        } catch (SQLException e) {
            fail("Test failed. See stack trace: " + e);
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
        @FunctionalInterface
        public interface ResultSetFunction<T> {
            T apply(ResultSet resultSet) throws SQLException, ParseException;
        }

        @AfterClass(alwaysRun = true)
        public void closeConnection() throws SQLException {
            if (connection != null) {
                Logger.info("Closing the connection");
                connection.close();
                Logger.info("Connection has been closed");
            }
        }
    }
