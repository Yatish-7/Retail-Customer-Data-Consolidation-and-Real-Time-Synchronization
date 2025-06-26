package com.newmeksolutions.config;

import java.sql.*;

public class DBConfig 
{
    // this code configure the database to the java code and this has a method which returns the connection//
	private static final String POS_DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=PointOfSale;encrypt=true;trustServerCertificate=true";
    private static final String POS_DB_USER = "sa";
    private static final String POS_DB_PASS = "803671";
 
    // Connection for POS_KPHB DB
    private static final String ONLINE_DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=EcommerceDB;encrypt=true;trustServerCertificate=true";
    private static final String ONLINE_DB_USER = "sa";
    private static final String ONLINE_DB_PASS = "803671";

    public static Connection getOnlineDBConnection() throws SQLException {
        return DriverManager.getConnection(ONLINE_DB_URL, ONLINE_DB_USER, ONLINE_DB_PASS);
    }

    public static Connection getPOSDBConnection() throws SQLException {
        return DriverManager.getConnection(POS_DB_URL, POS_DB_USER, POS_DB_PASS);
    }
}
