package controller.db.campus;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import utils.campus.Logger;
import data.campus.Tag;
import dbc.data.campus.DBCUtils;
import dbm.data.campus.TagPersistor;
import exceptions.campus.TransactionProcessingException;

public class TagController {

	public static Tag getTag(ResultSet rs) throws TransactionProcessingException {
		try {
			int id = rs.getInt("id");
			String name = rs.getString("name");
			String imgPath = rs.getString("img_path");
			String brief = rs.getString("brief");
			boolean authenticated = rs.getBoolean("authenticated");
			Collection<Integer> parents = TagPersistor.getParents(id);
			Tag tag = new Tag(id, name, brief, imgPath, authenticated, parents);
			return tag;
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static Collection<Integer> getParents(int id) throws TransactionProcessingException {
		String query = "select parent from tag_children where child = ?";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			Collection<Integer> parents = new LinkedList<Integer>();
			while (rs.next()) {
				parents.add(rs.getInt("parent"));
			}
			return parents;
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static Tag getTag(int tagId) throws TransactionProcessingException {
		String query = "Select * from tags where id = ?";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setInt(1, tagId);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				return getTag(rs);
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
		return null;
	}

	public static int createTag(Tag newTag, int memberId, boolean authenticated) throws TransactionProcessingException {
		String query = "insert into tags (name, img_path, brief, last_editor, last_edition, authenticated) values (?,?,?,?,CURRENT_TIMESTAMP,?);";
		PreparedStatement ps = DBCUtils.getStatement(query, true);
		try {
			ps.setString(1, newTag.getName());
			ps.setString(2, newTag.getImgPath());
			ps.setString(3, newTag.getBrief());
			ps.setInt(4, memberId);
			ps.setBoolean(5, authenticated || newTag.getAuthenticated());
			ps.execute();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next())
				return rs.getInt(1);
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
		return -1;
	}

	public static void setParents(int tagId, Collection<Integer> parents) throws TransactionProcessingException {
		String query = "insert into tag_children (parent,child) values (?, ?);";
		PreparedStatement ps = DBCUtils.getStatement(query, true);
		for (Integer parent : parents) {
			try {
				ps.setInt(1, parent);
				ps.setInt(2, tagId);
				ps.execute();
			} catch (SQLException e) {
				Logger.verboseLog(e.getLocalizedMessage());
				Logger.debug(e.getSQLState());
				throw new TransactionProcessingException();
			}
		}
	}

	public static void editTag(int memberId, int tagId, Tag tag) throws TransactionProcessingException {
		String query = "update tags set name = ?, brief = ?, img_path = ?, last_editor = ?, last_edition = CURRENT_TIMESTAMP where id = ?";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setString(1, tag.getName());
			ps.setString(2, tag.getImgPath());
			ps.setString(3, tag.getBrief());
			ps.setInt(4, memberId);
			ps.setInt(5, tagId);
			ps.execute();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static Collection<Integer> getChilds(int id) throws TransactionProcessingException {
		String query = "select child from tag_children where parent=?";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			Collection<Integer> parents = new LinkedList<Integer>();
			while (rs.next()) {
				parents.add(rs.getInt(1));
			}
			return parents;
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static int getTagId(String name) throws TransactionProcessingException {
		String query = "Select id from tags where UPPER(UNACCENT(name)) = UPPER(UNACCENT(?))";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
			throw new TransactionProcessingException();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static Collection<Tag> search(String keyword, int begin, int offset) throws TransactionProcessingException {
		String query = "SELECT * "
				+ "FROM tags "
				+ "WHERE  to_tsvector(UNACCENT(UPPER(name)) || ' ' || UNACCENT(UPPER(brief))) @@ "
				+ "plainto_tsquery(UNACCENT(UPPER(?))) LIMIT ? OFFSET ?";
		PreparedStatement ps = DBCUtils.getStatement(query);
		Collection<Tag> tags = new LinkedList<Tag>();
		ResultSet rs;
		try {
			ps.setString(1, keyword);
			ps.setInt(2, offset);
			ps.setInt(3, begin);
			rs = ps.executeQuery();
			while(rs.next()) {
				tags.add(getTag(rs));
			}
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
		return tags;
	}

	public static Collection<Tag> autoComplete(String keyword) throws TransactionProcessingException {
		String query = "SELECT * "
				+ "FROM tags "
				+ "WHERE UNACCENT(UPPER(name)) like UNACCENT(UPPER(? || '%'))";
		PreparedStatement ps = DBCUtils.getStatement(query);
		Collection<Tag> tags = new LinkedList<Tag>();
		ResultSet rs;
		try {
			ps.setString(1, keyword);
			rs = ps.executeQuery();
			while(rs.next()) {
				tags.add(getTag(rs));
			}
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
		return tags;
	}
}
