package com.example.demo.dbConnection;
import com.example.demo.utils.ConfigLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class dataConnection {
    public static Connection getConnection() throws SQLException {
        String url = ConfigLoader.get("DB_URL");
        String user = ConfigLoader.get("DB_USER");
        String password = ConfigLoader.get("DB_PASSWORD");
        return DriverManager.getConnection(url, user, password);
    }

}
