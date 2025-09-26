package company.utils;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;
import java.util.Base64;

public class DatabaseConnections {
    /**
     * Connects to a database and returns the connection
     * Credentials are configured in the config.properties file
     *
     * @param dbHost the identifier for the database connection type (e.g., "host_1", "host_2", etc.)
     * @return the connection
     */

    private Connection connectDB(String dbHost) {

        String country = ReadProperties.getValue("country");
        String env = ReadProperties.getValue("env");

        dbHost = dbHost + "." + country + "." + env;
        String url = ReadProperties.getValue(dbHost);

        //TBD. Read from env.
        String username = new String(Base64.getDecoder().decode(ReadProperties.getValue(dbHost + ".username")));
        String password = new String(Base64.getDecoder().decode(ReadProperties.getValue(dbHost + ".password")));

        Connection connection = null;
        try {
            // Establish a connection
            connection = DriverManager.getConnection(url, username, password);
           LoggerUtilities.info("DB connection established successfully!");
        } catch (SQLException e) {
            LoggerUtilities.error("Error connecting to DB: "+url+"\n" + e.getMessage());
            System.exit(1);
        }
        return connection;
    }

    /**
     * Connects to a database, executes the specified query, and prints the results to the console.
     * Credentials are configured in the config.properties file
     *
     * @param dbHost     the identifier for the database connection type (e.g., "host_1", "host_2", etc.)
     * @param query      a SQL query string to execute
     * @param columnName a column name to retrieve a specific column results
     * @return the data from specified column in the result set or cachedrowset or integer based on the query and conditions used
     */
    public <T> T
    queryDB(String query, String dbHost, String columnName) {
        Object response = null;
        try {
            Connection connection = connectDB(dbHost);
            Statement statement = connection.createStatement();
            ResultSet resultSet = null;
            String queryParam = query.substring(0, 9);
            if (queryParam.contains("SELECT")) {
                resultSet = statement.executeQuery(query);
                if (!columnName.equals("")) {
                    if (resultSet.next()) {
                        response = resultSet.getObject(columnName);
                        System.out.println(response);
                    }
                    closeDB(connection, statement, resultSet);
                    return (T) response;
                }
                CachedRowSet cachedRowSet = RowSetProvider.newFactory().createCachedRowSet();
                cachedRowSet.populate(resultSet);
                response = cachedRowSet;
            } else {
                response = statement.executeUpdate(query);
            }
            closeDB(connection, statement, resultSet);
        } catch (SQLException e) {
            LoggerUtilities.errorLoggerInFileAndReport("Error: " + e.getMessage());
        }
        return (T) response;
    }

    /**
     * Executes the specified query, and prints the results to the console.
     * Credentials are configured in the config.properties file
     *
     * @param dbHost the identifier for the database connection type (e.g., "host_1", "host_2", etc.)
     * @param query  a SQL query string to execute
     * @return the cachedrowset or integer based on query sent
     */
    public <T> T queryDB(String query, String dbHost) {
        return queryDB(query, dbHost, "");
    }

    /**
     * Closes all the connections related to database
     *
     * @param connection the connection that needs to be closed
     * @param statement  the statement that needs to be closed
     * @param resultSet  the result set that needs to be closed
     */
    private void closeDB(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            // Handle the exception, For example, print the error message
            LoggerUtilities.errorLoggerInFileAndReport("Error closing connection, statement, or result set: " + e.getMessage());
        }
    }
}