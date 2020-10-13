package com.sampurna.JDBCDemo;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.mysql.cj.jdbc.MysqlDataSource;

public class MyDataSourceFactory {

	public static MysqlDataSource getMysqlDataSource() {

		MysqlDataSource mysqlDataSource = new MysqlDataSource();

		try {
			Properties properties = new Properties();
			properties.load(MysqlDataSource.class.getResourceAsStream("/db.properties"));
			mysqlDataSource.setUrl(properties.getProperty("MYSQL_DB_URL"));
			mysqlDataSource.setUser(properties.getProperty("MYSQL_DB_USERNAME"));
			mysqlDataSource.setPassword(properties.getProperty("MYSQL_DB_PASSWORD"));

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return mysqlDataSource;
	}

}
