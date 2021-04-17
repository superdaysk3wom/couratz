package lib.PatPeter.SQLibrary;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Child class for the Ingres database.<br>
 * Date Created: 2012-12-18 06:20.
 * 
 * @author Nicholas Solin, a.k.a. PatPeter
 */
public class Ingres extends HostnameDatabase {
	public enum Statements implements StatementEnum {}
	
	public Ingres(Logger log,
				  String prefix,
				  String hostname,
				  int port,
				  String database,
				  String username,
				  String password) {
		super(log, prefix, DBMS.Ingres, hostname, port, database, username, password);
	}
	
	public Ingres(Logger log,
				  String prefix,
				  String database,
				  String username,
				  String password) {
		super(log, prefix, DBMS.Ingres, "localhost", 21017, database, username, password);
	}
	
	public Ingres(Logger log,
				  String prefix,
				  String database,
				  String username) {
		super(log, prefix, DBMS.Ingres, "localhost", 21017, database, username, "");
	}
	
	public Ingres(Logger log,
				  String prefix,
				  String database) {
		super(log, prefix, DBMS.Ingres, "localhost", 21017, database, "", "");
	}
	
	@Override
	public boolean initialize() {
		try {
			Class.forName("com.ingres.jdbc.IngresDriver");
			return true;
	    } catch (ClassNotFoundException e) {
	    	error("Ingres driver class missing: " + e.getMessage() + ".");
	    	return false;
	    }
	}
	
	@Override
	public boolean open() {
		if (initialize()) {
			String url = "";
			url = "jdbc:ingres://" + getHostname() + ":" + getPort() + "/" + getDatabase();
			try {
				connection = DriverManager.getConnection(url, getUsername(), getPassword());
				return true;
			} catch (SQLException e) {
				error("Could not establish a Ingres connection, SQLException: " + e.getMessage());
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Override
	protected void queryValidation(StatementEnum statement) throws SQLException {}

	@Override
	public Statements getStatement(String query) throws SQLException {
		String[] statement = query.trim().split(" ", 2);
		try {
			Statements converted = Statements.valueOf(statement[0].toUpperCase());
			return converted;
		} catch (IllegalArgumentException e) {
			throw new SQLException("Unknown statement: \"" + statement[0] + "\".");
		}
	}
	
	@Override
	public boolean isTable(String table) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean truncate(String table) {
		throw new UnsupportedOperationException();
	}
}
