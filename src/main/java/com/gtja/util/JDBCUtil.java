package com.gtja.util;

import java.sql.*;

public class JDBCUtil {
    public static Connection getConnection(){
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "1874");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void closeResouce(Connection conn){
        try {
            if(conn!=null){
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void closeResouce(Connection conn, PreparedStatement statement){
        closeResouce(conn);
        try {
            if(statement!=null){
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void closeResouce(Connection conn, PreparedStatement statement, ResultSet resultSet){
        closeResouce(conn,statement);
        try {
            if(resultSet!=null){
                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
