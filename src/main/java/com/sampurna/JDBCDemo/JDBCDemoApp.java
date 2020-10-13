package com.sampurna.JDBCDemo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.CallableStatement;

/**
 * JDBC Demo
 *
 */
public class JDBCDemoApp {

	private static String sqlStatementQuery = "select *  from CUSTOMERS where city = \"NANTES\" and country = \"FRANCE\"";
	private static String sqlPreparedStatementQuery = "select * from CUSTOMERS where city = ? and country = ?";
	private static String callProcedure = "{call GetShippedOrders(?, ?)}";
	private static String sqlBatchQuery = "insert into EMPLOYEE(EMP_ID, NAME, ROLE) values(?, ?, ?)";

	private static PreparedStatement preparedStatement = null;
	private static ResultSet resultSetPreparedStmt = null;
	private static PreparedStatement preparedStatement2 = null;
	private static CallableStatement callableStatement = null;

	public static void main(String[] args) {

		Savepoint savepoint = null;

		try (Connection connection = (Connection) MyDataSourceFactory.getMysqlDataSource().getConnection(); // Statement
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sqlStatementQuery)) {

			connection.setAutoCommit(false);

			System.out.println("\n" + "Statement");
			while (resultSet.next()) {
				System.out.println("CustomerNumber " + resultSet.getInt("customerNumber"));
				System.out.println("CustomerName " + resultSet.getString("customerName"));
			}

			// PreparedStatement
			PreparedStatement preparedStatement = connection.prepareStatement(sqlPreparedStatementQuery);
			preparedStatement.setString(1, "NANTES");
			preparedStatement.setString(2, "FRANCE");
			ResultSet resultSetPreparedStmt = preparedStatement.executeQuery();

			System.out.println("\n" + "Prepared Statement");
			while (resultSetPreparedStmt.next()) {
				System.out.println("CustomerNumber " + resultSetPreparedStmt.getInt("CustomerNumber"));
				System.out.println("CustomerName" + resultSetPreparedStmt.getString("CustomerName"));
			}

			savepoint = connection.setSavepoint("BEFORE CALLABLE STATEMENT");

			// CallableStatement
			System.out.println("\n" + "Callable " + "Statement");
			callableStatement = connection.prepareCall(callProcedure);
			callableStatement.setString(1, "Shipped"); // in and out params are all question marks
			callableStatement.registerOutParameter(2, java.sql.Types.INTEGER);
			callableStatement.execute();
			System.out.println("Total orders that have been shipped: " + callableStatement.getInt(2));
			;

			// BatchProcessing..using prepared statement
			/*
			 * try { Connection connection =
			 * MyDataSourceFactory.getMysqlDataSource().getConnection(); preparedStatement2
			 * = connection.prepareStatement(sqlBatchQuery); for (int i = 100; i < 106; i++)
			 * { preparedStatement2.setInt(1, i); preparedStatement2.setString(2, "EMP" +
			 * i); preparedStatement2.setString(3, "ENG"); preparedStatement2.addBatch(); }
			 * int[] updateCounts = preparedStatement2.executeBatch();
			 * System.out.println("\n" + "Batch update counts " +
			 * Arrays.toString(updateCounts));
			 */
			connection.commit();

		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSetPreparedStmt != null) {
					resultSetPreparedStmt.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (preparedStatement2 != null) {
					preparedStatement2.close();
				}
				if (callableStatement != null) {
					callableStatement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
