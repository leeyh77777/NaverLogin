package com.core;

import java.sql.*;
import javax.servlet.FilterConfig;

/**
 * DB 연결 클래스 
 *
 */
public class DB {
	
	private static String driver;
	private static String url;
	private static String user;
	private static String password;
	
	public static void init(FilterConfig config) {
		init(
				config.getInitParameter("DBDriver"),
				config.getInitParameter("DBUrl"),
				config.getInitParameter("DBUser"),
				config.getInitParameter("DBPass")
		);
		
	}
	
	public static void init(String driver, String url, String user, String password) {
		DB.driver = driver;
		DB.url = url;
		DB.user = user;
		DB.password = password;
	}
		
	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, user, password);
		
		return conn;
	}
}
