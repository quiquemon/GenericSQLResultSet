package model.dao;

/**
 * Represents the source database options.
 * These options are the driver, the name of the database,
 * a user that has access the database and his password.
 */
public final class DataSource {
	
	public final String driver;
	public final String database;
	public final String user;
	public final String password;
	
	public DataSource(String driver, String database, String user, String password) {
		this.driver = driver;
		this.database = database;
		this.user = user;
		this.password = password;
	}
}