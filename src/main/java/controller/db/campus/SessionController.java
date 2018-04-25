package controller.db.campus;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import utils.campus.Logger;
import dbc.data.campus.DBCUtils;
import exceptions.campus.NoSuchMemberException;
import exceptions.campus.TransactionProcessingException;

public class SessionController {

	public static void saveSessionId(String sessionId, int memberId) throws TransactionProcessingException {
		String query = "insert into Sessions (member_id, session_id) values (?,?);";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setInt(1, memberId);
			ps.setString(2, sessionId);
			ps.execute();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static int getMemberId(String sessionId) throws NoSuchMemberException {
		String query = "select member_id from Sessions where session_id = ?;";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setString(1, sessionId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) return rs.getInt(1);
			throw new NoSuchMemberException();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new NoSuchMemberException();
		}
	}

	public static void deleteSession(String sessionId) throws TransactionProcessingException {
		String query = "delete from Sessions where session_id = ?;";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setString(1, sessionId);
			ps.execute();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static void createUser(int memberId, String password) throws TransactionProcessingException {
		String query = "insert into passwords (member_id, password) values (?,?);";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setInt(1, memberId);
			ps.setString(2, password);
			ps.execute();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
		
	}

	public static String getStoredPass(int memberId) throws NoSuchMemberException {
		String query = "select password from passwords where member_id = ?;";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setInt(1, memberId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) return rs.getString(1);
			throw new NoSuchMemberException();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new NoSuchMemberException();
		}
	}

}
