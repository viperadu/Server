package com.example.bcast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseConnection {
	private static final String TAG = "DatabaseConnection: ";
	private static final boolean DEBUGGING = true;
	private static final boolean LOGGING = true;
	
	private String url;
	private String dbName;
	private String driver;
	private String userName;
	private String password;
	
	private Connection connection;
	
	public DatabaseConnection() {
		url = "jdbc:mysql://194.102.231.189:3306/";
		dbName = "bcast";
		driver = "com.mysql.jdbc.Driver";
		userName = "root";
		password = "password";
		connection = null;
	}
	
	public Connection init() {
		try {
			Class.forName(driver).newInstance();
			connection = DriverManager.getConnection(url + dbName, userName, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ResultSet executeQuery(String query) {
		connection = init();
		Statement statement = null;
		ResultSet result = null;
		synchronized(connection) {
			try {
				statement = connection.createStatement();
				result = statement.executeQuery(query);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public boolean executeInsert(String insert) {
		connection = init();
		Statement statement = null;
		int val = 0;
		synchronized(connection) {
			try {
				statement = connection.createStatement();
				val = statement.executeUpdate(insert);
			} catch (SQLException e) {
				e.printStackTrace();
				close();
				return false;
			}
		}
		close();
		return val == 1 ? true : false;
	}
	
	public int executeUpdate(String update) {
		connection = init();
		PreparedStatement preparedStatement = null;
		int result = 0;
		synchronized(connection) {
			try {
				preparedStatement = connection.prepareStatement(update);
//				preparedStatement.setInt(1, 0);
				if(!preparedStatement.execute()) {
					result = preparedStatement.getUpdateCount();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				close();
				return 0;
			}
		}
		close();
		return result <= 0 ? 0 : result;
	}
	
	public int executeDelete(String delete) {
		connection = init();
		PreparedStatement preparedStatement = null;
		int result = 0;
		synchronized (connection) {
			try {
				preparedStatement = connection.prepareStatement(delete);
//				preparedStatement.setInt(1, 0);
				if(!preparedStatement.execute()) {
					result = preparedStatement.getUpdateCount();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				close();
				return 0;
			}
		}
		close();
		return result <= 0 ? 0 : result;
	}

	public int getId(String query) {
		connection = init();
		Statement statement = null;
		ResultSet result = null;
		int id = 0;
		synchronized(connection) {
			try {
				statement = connection.createStatement();
				result = statement.executeQuery(query);
System.out.println(query);
				if(result.next()) {	
					id = result.getInt("sessionId");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				close();
				return 0;
			}
		}
		close();
		return id;
	}
}
