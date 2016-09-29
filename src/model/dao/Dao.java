package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public final class Dao {
	
	private DataSource ds;
	private Connection conn = null;
	
	public Dao(DataSource ds) {
		this.ds = ds;
	}
	
	/**
	 * Creates a new Dao with the following options:
	 * <br>
	 * <br>
	 * <b>Driver:</b> com.mysql.jdbc.Driver
	 * <br>
	 * <b>Database:</b> jdbc:mysql://localhost/genericsqlresultset
	 * <br>
	 * <b>User:</b> root
	 * <br>
	 * <b>Password:</b> root
	 * 
	 * @return A new instance of this class.
	 */
	public static Dao createStandardDao() {
		return new Dao(new DataSource(
			"com.mysql.jdbc.Driver",
			"jdbc:mysql://localhost/genericsqlresultset?useSSL=false",
			"root",
			"root"
		));
	}
	
	/**
	 * Creates a new connection to the database. This method must be called before
	 * any operation is done on the database with this object.
	 * 
	 * @throws RuntimeException If an error occurs while getting the connection.
	 */
	public void connect() {
		if (conn == null) {
			try {
				Class.forName(ds.driver);
				conn = DriverManager.getConnection(ds.database, ds.user, ds.password);
				System.out.println("Connected to database: " + ds.database);
			} catch (SQLException | ClassNotFoundException e) {
				conn = null;
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * Closes the current connection with the database. This method must be called
	 * when no more operations are to be done on the database with this object.
	 * 
	 * @throws RuntimeException If an error occurs while getting the connection.
	 */
	public void disconnect() {
		if (conn != null) {
			try {
				conn.close();
				System.out.println("Disconnected from database: " + ds.database);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				conn = null;
			}
		}
	}
	
	/**
	 * Begins a new transaction. This object's connection's auto commit mode
	 * is disabled.
	 * 
	 * @throws RuntimeException If an error occurs while beginning the transaction.
	 */
	public void beginTransaction() {
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Commits all the changes made since the transaction was initialized.
	 * This object's connection's auto commit mode is enabled.
	 * 
	 * @throws RuntimeException If an error occurs while commiting the changes.
	 */
	public void commit() {
		try {
			conn.commit();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * Reverts all the changes made since the transaction was initialized.
	 * This object's connection's auto commit mode is enabled.
	 * 
	 * @throws RuntimeException If an error occurs while reverting the changes.
	 */
	public void rollback() {
		try {
			conn.rollback();
		} catch (SQLException e){
			throw new RuntimeException(e);
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * Runs the given query with the specified parameters and returns the result set.
	 * This method should only be used with SELECT queries. The number of parameters
	 * must math the number of '?' tokens in the query.
	 * 
	 * @param sql The SQL query to execute.
	 * @param params The parameters to be passed to the SQL query.
	 * @return A list of hashtables, each one representing one row of the result set.
	 * If the result set is empty, then the returned map will be empty as well.
	 * @throws RuntimeException If an error occurs while fetching the result set or
	 * if the number of '?' tokens does not match the number of parameters.
	 */
	public List<Map<String, Object>> query(String sql, Object... params) {
		long numberOfTokens = sql.chars().filter(e -> e == '?').count();
		
		if (numberOfTokens != params.length) {
			throw new RuntimeException("The number of tokens does not match the number of parameters.");
		}
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			for (int i = 1; i <= numberOfTokens; i++) {
				stmt.setObject(i, params[i - 1]);
			}
			
			try (ResultSet rs = stmt.executeQuery()) {
				List<Map<String, Object>> table = new ArrayList<>();
				ResultSetMetaData rsmd = rs.getMetaData();
				int numberOfColumns = rsmd.getColumnCount();
				
				while (rs.next()) {
					Map<String, Object> column = new HashMap<>();
					for (int i = 1; i <= numberOfColumns; i++) {
						column.put(rsmd.getColumnLabel(i), rs.getObject(i));
					}
					
					table.add(column);
				}
				
				return table;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Runs the given statement with the specified parameters. This method
	 * should only be used with INSERT, UPDATE or DELETE statements. The number
	 * of parameters must math the number of '?' tokens in the query.
	 * 
	 * @param sql The SQL statement to execute.
	 * @param params The parameters to be passed to the SQL statement.
	 * @throws RuntimeException If an error occurs while executing the statement
	 * or if the number of '?' tokens does not match the number of parameters.
	 */
	public void execute(String sql, Object... params) {
		long numberOfTokens = sql.chars().filter(e -> e == '?').count();
		
		if (numberOfTokens != params.length) {
			throw new RuntimeException("The number of tokens does not match the number of parameters.");
		}
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			for (int i = 1; i <= numberOfTokens; i++) {
				stmt.setObject(i, params[i - 1]);
			}
			
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}