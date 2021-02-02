package com.search.sdasearch.utility;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestJDBC {

    public static void main(String[] args) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/sda-search?useSSL=false&serverTimezone=UTC";
        String user = "devuser";
        String pass = "devuser";

        try {
            System.out.println("Connecting to database: " + jdbcUrl);
            Connection myConn = DriverManager.getConnection(jdbcUrl, user, pass);
            System.out.println("Connection successful!");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
