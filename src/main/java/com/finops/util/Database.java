package com.finops.util;

import java.sql.*;

public class Database {

    public static ResultSet query(String sql, Connection connection) throws SQLException {
        Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        return stmt.executeQuery(sql);
    }

    public static PreparedStatement preparedStatement(String sql, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(sql);
        return stmt;
    }
}
