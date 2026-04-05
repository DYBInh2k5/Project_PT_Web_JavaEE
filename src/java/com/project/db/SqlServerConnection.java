package com.project.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public final class SqlServerConnection {

    private static final String JNDI_NAME = "java:comp/env/jdbc/QLBanSachDS";
    private static final String FALLBACK_URL = "jdbc:sqlserver://localhost:1433;databaseName=QLBanSach;encrypt=true;trustServerCertificate=true";
    private static final String FALLBACK_USER = "sa";
    private static final String FALLBACK_PASSWORD = "1";

    private SqlServerConnection() {
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = getConnectionFromJndi();
        if (connection != null) {
            return connection;
        }

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ex) {
            throw new SQLException("SQL Server JDBC Driver is not available", ex);
        }

        return DriverManager.getConnection(FALLBACK_URL, FALLBACK_USER, FALLBACK_PASSWORD);
    }

    private static Connection getConnectionFromJndi() throws SQLException {
        try {
            Context initialContext = new InitialContext();
            Context envContext = (Context) initialContext.lookup("java:comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/QLBanSachDS");
            return dataSource.getConnection();
        } catch (NamingException ex) {
            return null;
        }
    }
}
