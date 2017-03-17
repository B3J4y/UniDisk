package de.unidisk.common.mysql;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import de.unidisk.common.SystemProperties;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Date;
import java.util.Properties;

/**
 * 
 * @author Julian Dehne
 */
public abstract class MysqlConnect {

	public enum LoadedDatabase{
		notLoaded, noDatabase, unidisk
	}

	LoadedDatabase state;
	static final Logger logger = LogManager.getLogger(MysqlConnect.class.getName());
	private static Properties systemProperties = SystemProperties.getInstance();
	private Connection conn;

	public MysqlConnect() {
		state = LoadedDatabase.notLoaded;
	}

	public static String getLocalhostConnection(LoadedDatabase database) {
		switch (database) {
			case noDatabase:
				return "jdbc:mysql://" + systemProperties.getProperty("database.localhost") +
								"?user=" + systemProperties.getProperty("database.root.name") +
								"&password=" + systemProperties.getProperty("database.root.password");
			case unidisk:
				return "jdbc:mysql://" + systemProperties.getProperty("database.localhost") + "/" +
								systemProperties.getProperty("uni.db.name") +
								"?user=" + systemProperties.getProperty("database.root.name") +
								"&password=" + systemProperties.getProperty("database.root.password");
			default:
				return null;
		}

	}

	/**
	 * Mit dieser Methode stellt man die Verbindung zu der Datenbank her.
	 */
	public void connect(String connectionString, LoadedDatabase database) throws CommunicationsException {
		try {
			conn = DriverManager.getConnection(connectionString);
		} catch (CommunicationsException ex) {
			//Server not reachable
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			throw ex;
		} catch (SQLSyntaxErrorException ex){
			//Schema not available
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			String unnamed = getLocalhostConnection(LoadedDatabase.noDatabase);
			if (!unnamed.equals(connectionString)){
				connect(unnamed,LoadedDatabase.noDatabase);
				state = LoadedDatabase.noDatabase;
			}
			createSchemaWithTables("unidisk");
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}

	}

	protected abstract void createSchemaWithTables(String name);

	protected abstract void readDump(String dbName, String file);

	private void ensureConnection() {

	}

	/**
	 * Mit dieser Methode stellt man die Verbindung zu der Datenbank her.
	 */
	public void connectToLocalhost() throws CommunicationsException {
		String connection = getLocalhostConnection(LoadedDatabase.unidisk);
		try {
			connect(connection, LoadedDatabase.unidisk);
		} catch (CommunicationsException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Mit dieser Methode wird die Verbindung zu der Datenbank geschlossen
	 */
	public void close() {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (final SQLException e) {
			throw new Error("could not close mysql");
		}
	}

	/**
	 * Hilfsmethode 2 - fügt der einem PreparedStatement die entsprechenden
	 * Parameter hinzu
	 * 
	 * @param statement
	 * @param args
	 * @return
	 * @throws SQLException
	 */
	private PreparedStatement addParameters(final String statement, final Object[] args) {
		try {
			final PreparedStatement ps = conn.prepareStatement(statement);
			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					final Object arg = args[i];
					setParam(ps, arg, i + 1);
				}
			}
			return ps;
		} catch (SQLException ex) {
			logger.error(ex);
		}
		return null;
	}

	/**
	 * Mit dieser Methode können select-Statements abgesetzt werden.
	 * 
	 * @param statement
	 * @param args
	 * @return
	 */
	public VereinfachtesResultSet issueSelectStatement(final String statement, final Object... args) {
		try {
			PreparedStatement ps = addParameters(statement, args);
			ResultSet queryResult = ps.executeQuery();
			return new VereinfachtesResultSet(queryResult);
		} catch (SQLException ex) {
			logger.error(ex);
		}
		return null;
	}

	/**
	 * Mit dieser Methode können Statements ohne Variablen und ohne Rückgaben
	 * ausgeführt werden.
	 * 
	 * @param statement
	 */
	public void otherStatements(final String statement) {
		try {
			conn.createStatement().execute(statement);
		} catch (SQLException ex) {
			logger.error(ex);
		}
	}

	/**
	 * Mit dieser Methode können updateStatements abgesetzt werden.
	 * 
	 * @param statement
	 * @param args
	 * @return
	 */
	public Integer issueUpdateStatement(final String statement, final Object... args) {
		PreparedStatement ps = addParameters(statement, args);
		try {
			return ps.executeUpdate();
		} catch (SQLException ex) {
			logger.error(ex);
		}
		return null;
	}

	/**
	 * Mit dieser Methode können insert- oder delete-Statements abgesetzt
	 * werden.
	 * 
	 * @param statement
	 * @param args
	 */
	public void issueInsertOrDeleteStatement(final String statement, final Object... args) {
		PreparedStatement ps = addParameters(statement, args);
		try {
			ps.execute();
		} catch (SQLException ex) {
			logger.error(ex);
		}
	}

	private void setParam(final PreparedStatement ps, final Object arg, final int i) throws SQLException {
		if (arg instanceof String) {
			ps.setString(i, (String) arg);
		} else if (arg instanceof Integer) {
			ps.setInt(i, (Integer) arg);
		} else if (arg instanceof Double) {
			ps.setDouble(i, (Double) arg);
		} else if (arg instanceof Boolean) {
			ps.setBoolean(i, (Boolean) arg);
		} else if (arg instanceof Float) {
			ps.setFloat(i, (Float) arg);
		} else if (arg instanceof Short) {
			ps.setShort(i, (Short) arg);
		} else if (arg instanceof Long) {
			ps.setLong(i, (Long) arg);
		} else if (arg instanceof Byte) {
			ps.setByte(i, (Byte) arg);
		} else if (arg instanceof Character) {
			ps.setString(i, ((Character) arg).toString());
		} else if (arg instanceof Date) {
			final java.sql.Date d = new java.sql.Date(((Date) arg).getTime());
			ps.setDate(i, d);
		} else if (arg == null) {
			ps.setNull(i, java.sql.Types.NULL);
		} else {
			ps.setString(i, arg.toString());
		}
	}

	public Connection getConnection() {
		return conn;
	}

	public void setConnection(Connection conn) {
		this.conn = conn;
	}
}
