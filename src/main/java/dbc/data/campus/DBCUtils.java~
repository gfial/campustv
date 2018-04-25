package dbc.data.campus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import utils.campus.Logger;

public class DBCUtils {

	private static final String URL = "jdbc:postgresql://ec2-54-247-78-153.eu-west-1.compute.amazonaws.com:5432/d8rg30ms0vnd66";
	private static final String USER = "enblsslgfymaud";
	private static final String PASSWD = "bNpZf5f_re5eJHd1VuPrSLJjhX";

//	private static final String URL = "jdbc:postgresql://localhost:5432/campusdb";
//	private static final String USER = "postgres";
//	private static final String PASSWD = "ggsenag09";
	
	public static Connection con = null;

	/**
	 * Initializes the database connection.
	 */
	static {
		Logger.log("Setting up database connection");
		try {
			con = DriverManager.getConnection(URL, USER, PASSWD);
			if (con == null) {
				Logger.log("Could not setup database connection");
			} else {
				Logger.log("Set up the database connection");
				con.setAutoCommit(true);
				Logger.log("Set auto commit to true");
			}
		} catch (SQLException e) {
			Logger.log(e.getLocalizedMessage());
		}
	}

	public static PreparedStatement getStatement(String query) {
		if (con != null)
			try {
				return con.prepareStatement(query);
			} catch (SQLException e) {
				// Basically, without saying too much, you're screwed. Royally
				// and totally.
				return null;
			} catch (Exception e) {
				// If you thought you were screwed before, boy have I news for
				// you!!!
				return null;
			}
		else
			return null;
	}

	public static PreparedStatement getStatement(String query, boolean returning) {
		if (con != null)
			try {
				if (returning) {
					return con.prepareStatement(query,
							Statement.RETURN_GENERATED_KEYS);
				}
				return con.prepareStatement(query);
			} catch (SQLException e) {
				// Basically, without saying too much, you're screwed. Royally
				// and totally.
				return null;
			} catch (Exception e) {
				// If you thought you were screwed before, boy have I news for
				// you!!!
				return null;
			}
		else
			return null;
	}

	public static PreparedStatement getFileStatement(String filename) {
		String query = FileOps.getFile(filename);
		if (query == null)
			return null;
		return getStatement(query);
	}

	public static ResultSet execute(Statement st, String query) {
		if (st == null || query == null)
			return null;
		try {
			return st.executeQuery(query);
		} catch (SQLException e) {
			// Basically, without saying too much, you're screwed. Royally
			// and totally.
		}
		return null;
	}

}
