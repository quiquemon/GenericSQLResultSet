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
	
	public void connect() {
		if (conn != null) {
			try {
				Class.forName(ds.driver);
				conn = DriverManager.getConnection(ds.database, ds.user, ds.password);
			} catch (SQLException | ClassNotFoundException e) {
				conn = null;
				throw new RuntimeException(e);
			}
		}
	}
	
	public void disconnect() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				conn = null;
			}
		}
	}
	
	public void beginTransaction() {
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
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
	
	public List<Map<String, Object>> query(String sql) {
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			List<Map<String, Object>> table = new ArrayList<>();
			ResultSetMetaData rsmd = rs.getMetaData();
			int numberOfColumns = rsmd.getColumnCount();
			
			while (rs.next()) {
				Map<String, Object> column = new HashMap<>();
				for (int i = 1; i <= numberOfColumns; i++)
					column.put(rsmd.getColumnLabel(i), rs.getObject(i));
			}
			
			return table;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
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
					for (int i = 1; i <= numberOfColumns; i++)
						column.put(rsmd.getColumnLabel(i), rs.getObject(i));
				}
				
				return table;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void execute(String sql, Object[] params) {
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