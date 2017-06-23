package de.unidisk.common.mysql;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import de.unidisk.common.SystemProperties;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

/**
 * 
 * @author Julian Dehne
 */
public abstract class MysqlConnect {

	public enum LoadedDatabase{
		notLoaded, noDatabase, loaded
	}


	LoadedDatabase state;
	static final Logger logger = LogManager.getLogger(MysqlConnect.class.getName());
	private static Properties systemProperties = SystemProperties.getInstance();
	private Connection conn;

	protected static MysqlConnect mySqlConnect;

	public MysqlConnect() {
		state = LoadedDatabase.notLoaded;
	}

	public static String getLocalhostConnection(LoadedDatabase database) {
		//todo jb einen Strang für die TestDb
		switch (database) {
			case noDatabase:
				return String.format("jdbc:mysql://%s?user=%s&password=%s&useSSL=%s&requireSSL=%s&verifyServerCertificate=%s&serverTimezone=%s",
						systemProperties.getProperty("database.localhost"),
						systemProperties.getProperty("database.root.name"),
						systemProperties.getProperty("database.root.password"),
						systemProperties.getProperty("database.sec.useSSL"),
						systemProperties.getProperty("database.sec.requireSSL"),
						systemProperties.getProperty("database.sec.verifyServerCertificate"),
						systemProperties.getProperty("database.server.timezone"));

			case loaded:
				return String.format("jdbc:mysql://%s/%s?user=%s&password=%s&useSSL=%s&requireSSL=%s&verifyServerCertificate=%s&serverTimezone=%s",
						systemProperties.getProperty("database.localhost"),
						systemProperties.getProperty("uni.db.name"),
						systemProperties.getProperty("database.root.name"),
						systemProperties.getProperty("database.root.password"),
						systemProperties.getProperty("database.sec.useSSL"),
						systemProperties.getProperty("database.sec.requireSSL"),
						systemProperties.getProperty("database.sec.verifyServerCertificate"),
						systemProperties.getProperty("database.server.timezone"));

			default:
				return null;
		}

	}

	/**
	 * Mit dieser Methode stellt man die Verbindung zu der Datenbank her.
	 */
	public void connect(String connectionString) throws CommunicationsException {
		try {
			conn = DriverManager.getConnection(connectionString);
			state = LoadedDatabase.loaded;
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
			String newConnectionString = getLocalhostConnection(LoadedDatabase.noDatabase);
			if (!newConnectionString.equals(connectionString)){
				connect(newConnectionString);
				state = LoadedDatabase.noDatabase;
			}
			createSchema("unidisk");
			try {
				conn.setCatalog("unidisk");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			List<String> files = Arrays.asList("db_overview.sql","Hochschulen.sql");
			for (String file : files) {
				importSQL(conn, file);
			}



		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}

	}

	protected abstract void createSchema(String name);



	protected abstract void readDump(String dbName, String file);

	public static void importSQL(Connection conn, String fileName)
	{
		InputStream is = null;

		//Pfad für Datei bauen
		String[] path = {".", "src", "main", "resources", "sql_dumps", fileName};
		String filePath = String.join(File.separator, path);
		Statement st = null;

		try
		{
			is = new FileInputStream(filePath);
			Scanner s = new Scanner(is);
			s.useDelimiter("(;(\r)?\n)|((\r)?\n)?(--)?.*(--(\r)?\n)");
			st = conn.createStatement();

			while (s.hasNext())
			{
				String line = s.next();
				if (line.startsWith("/*!") && line.endsWith("*/"))
				{
					int i = line.indexOf(' ');
					line = line.substring(i + 1, line.length() - " */".length());
				}

				if (line.trim().length() > 0)
				{
					st.execute(line);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally
		{
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (st != null) try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void ensureConnection() {

	}

	/**
	 * Mit dieser Methode stellt man die Verbindung zu der Datenbank her.
	 */
	public void connectToLocalhost() {
		//TODO yw Loaded Database und connect to Database sind nicht das gleiche!!!!!1111elf
		String connection = getLocalhostConnection(LoadedDatabase.loaded);
		try {
			connect(connection);
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
			final PreparedStatement ps = getConnection().prepareStatement(statement);
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

	public Connection getConnection() throws CommunicationsException {
		if (conn == null) {
			connect(getLocalhostConnection(LoadedDatabase.noDatabase));
		}
		return conn;
	}

	public void setConnection(Connection conn) {
		this.conn = conn;
	}
}
