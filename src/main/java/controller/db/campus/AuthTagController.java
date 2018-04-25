package controller.db.campus;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import utils.campus.Logger;
import data.campus.Tag;
import dbc.data.campus.DBCUtils;
import exceptions.campus.TransactionProcessingException;

public class AuthTagController {

	public static boolean exists(int id) throws TransactionProcessingException {
		String query = "select * from authenticatedtags where id = ?";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static boolean isMember(int memberId, int tagId) throws TransactionProcessingException {
		String query = "select * from membersof where tag_id = ? and member_id = ?";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setInt(1, tagId);
			ps.setInt(2, memberId);
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static boolean isManager(int memberId, int tagId) throws TransactionProcessingException {
		String query = "select is_manager from membersof where tag_id = ? and member_id = ?";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setInt(1, tagId);
			ps.setInt(2, memberId);
			ResultSet rs = ps.executeQuery();
			if(!rs.next()) return false;
			return rs.getBoolean(1);
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static boolean isAuthenticated(int tagId) throws TransactionProcessingException {
		String query = "select id from authenticatedtags where id = ?";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setInt(1, tagId);
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static void createAuthTag(int tagId) throws TransactionProcessingException {
		String query = "insert into authenticatedtags (id) values (?);";
		PreparedStatement ps = DBCUtils.getStatement(query);
		Logger.debug("Creating authenticated id: " + tagId);
		try { 
			ps.setInt(1, tagId);
			ps.execute();
			Logger.debug("Added " + tagId + " to authenticated tags.");
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static void addMember(int memberId, int tagId, boolean isManager) throws TransactionProcessingException {
		String query = "insert into membersof (member_id, tag_id, is_manager) values (?,?,?)";
		PreparedStatement ps = DBCUtils.getStatement(query);
		Logger.debug("Inserting member " + memberId + " into " + tagId + " and he is manager? " + isManager);
		try {
			ps.setInt(1, memberId);
			ps.setInt(2, tagId);
			ps.setBoolean(3, isManager);
			ps.execute();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static void revokeMember(int memberId, int tagId) throws TransactionProcessingException {
		String query = "delete from membersof where member_id = ? and tag_id = ? and is_manager = false";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setInt(1, memberId);
			ps.setInt(2, tagId);
			ps.execute();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static void revokeManager(int memberId, int tagId) throws TransactionProcessingException {
		String query = "update membersof set is_manager = false where member_id = ? and tag_id = ?";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setInt(1, memberId);
			ps.setInt(2, tagId);
			ps.execute();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static void promote(int memberId, int tagId) throws TransactionProcessingException {
		String query = "update membersof set is_manager = true where member_id = ? and tag_id = ?";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setInt(1, memberId);
			ps.setInt(2, tagId);
			ps.execute();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static Collection<Integer> getMembers(int tagId) throws TransactionProcessingException {
		String query = "select member_id from membersof where tag_id = ?";
		PreparedStatement ps = DBCUtils.getStatement(query);
		Collection<Integer> members = new LinkedList<Integer>();
		try {
			ps.setInt(1, tagId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				members.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
		return members;
	}

	public static Collection<Integer> getManagers(int tagId) throws TransactionProcessingException {
		String query = "select member_id from membersof where tag_id = ? and is_manager = true";
		
		PreparedStatement ps = DBCUtils.getStatement(query);
		Collection<Integer> members = new LinkedList<Integer>();
		try {
			ps.setInt(1, tagId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				members.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
		return members;
	}

	public static Collection<Integer> autocompleteMembers(int tagId,
			String searchQuery) throws TransactionProcessingException {
		String query = "SELECT member_id "
				+ "FROM membersof inner join members on (member_id = id) "
				+ "WHERE tag_id = ? "
				+ "AND "
				+ "("
				+ "	UNACCENT(UPPER(username)) like UNACCENT(UPPER(? || '%')) "
				+ "	OR "
				+ "	UNACCENT(UPPER(email)) like UNACCENT(UPPER(? || '%'))"
				+ ");";
		PreparedStatement ps = DBCUtils.getStatement(query);
		Collection<Integer> members = new LinkedList<Integer>();
		try {
			ps.setInt(1, tagId);
			ps.setString(2, searchQuery);
			ps.setString(3, searchQuery);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				members.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
		return members;
	}

	public static Collection<Integer> autocompleteManagers(int tagId,
			String searchQuery) throws TransactionProcessingException {
		String query = "SELECT member_id "
				+ "FROM membersof inner join members on (member_id = id) "
				+ "WHERE tag_id = ? AND is_manager"
				+ "AND "
				+ "("
				+ "	UNACCENT(UPPER(username)) like UNACCENT(UPPER(? || '%')) "
				+ "	OR "
				+ "	UNACCENT(UPPER(email)) like UNACCENT(UPPER(? || '%'))"
				+ ");";
		PreparedStatement ps = DBCUtils.getStatement(query);
		Collection<Integer> members = new LinkedList<Integer>();
		try {
			ps.setInt(1, tagId);
			ps.setString(2, searchQuery);
			ps.setString(3, searchQuery);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				members.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
		return members;
	}

	public static Collection<Tag> getManagedTags(int memberId) throws TransactionProcessingException {
		String query = "select * from membersof inner join tags on (tag_id = id) where member_id = ? and is_manager;";
		PreparedStatement ps = DBCUtils.getStatement(query);
		Collection<Tag> tags = new LinkedList<Tag>();
		try {
			ps.setInt(1, memberId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				tags.add(TagController.getTag(rs));
			}
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
		return tags;
	}

	public static Collection<Tag> getMembershipedTags(int memberId) throws TransactionProcessingException {
		String query = "select * from membersof inner join tags on (tag_id = id) where member_id = ?;";
		PreparedStatement ps = DBCUtils.getStatement(query);
		Collection<Tag> tags = new LinkedList<Tag>();
		try {
			ps.setInt(1, memberId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				tags.add(TagController.getTag(rs));
			}
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
		return tags;
	}

}
