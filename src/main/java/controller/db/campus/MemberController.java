package controller.db.campus;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import utils.campus.Logger;
import data.campus.Channel;
import data.campus.Member;
import dbc.data.campus.DBCUtils;
import exceptions.campus.NoSuchChannelException;
import exceptions.campus.TransactionProcessingException;

public class MemberController {

	public static Member getMinimalMember(ResultSet rs) throws TransactionProcessingException {
		try {
			if (!rs.next())
				return null;
			int id = rs.getInt("id");
			String username = rs.getString("username");
			String imgPath = rs.getString("img_path");
			int reputation = rs.getInt("reputation");
			String gender = rs.getString("gender");
			
			Member m = new Member(id, username, null, reputation, imgPath,
					gender, null, null);
			return m;
		} catch (SQLException e) {
			Logger.debug("Failed to parse query result set: "
					+ e.getLocalizedMessage());
			throw new TransactionProcessingException();
		}
	}
	
	public static Member getMember(ResultSet rs) throws TransactionProcessingException {
		try {
			if (!rs.next())
				return null;
			int id = rs.getInt("id");
			String username = rs.getString("username");
			String imgPath = rs.getString("img_path");
			int reputation = rs.getInt("reputation");
			String gender = rs.getString("gender");
			int smartTvId = rs.getInt("smart_tv");
			Channel smartTv = null;
			try {
				smartTv = ChannelController.getChannel(smartTvId);
			} catch (NoSuchChannelException e) {
			}
			
			Member m = new Member(id, username, null, reputation, imgPath,
					gender, smartTv, null);
			return m;
		} catch (SQLException e) {
			Logger.debug("Failed to parse query result set: "
					+ e.getLocalizedMessage());
			throw new TransactionProcessingException();
		}
	}

	public static Member getMember(ResultSet rs, ResultSet rsChannels) throws TransactionProcessingException {
		Member member = getMember(rs);
		member.setChannels(ChannelController.getChannels(member.getId()));
		return member;
	}

	public static boolean existsId(int memberId) throws TransactionProcessingException {
		String query = "select id from members where id = ?;";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setInt(1, memberId);
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			throw new TransactionProcessingException();
		}
	}

	public static boolean existsEmail(String email) throws TransactionProcessingException {
		String query = "SELECT id from members where email=?";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			throw new TransactionProcessingException();
		}
	}

	public static int getId(String email) throws TransactionProcessingException {
		String query = "SELECT id from members where email=?";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("id");
			}
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			throw new TransactionProcessingException();
		}
		return -1;
	}

	public static Member getMember(int memberId) throws TransactionProcessingException {
		String query = "select * from members where id = ?;";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setInt(1, memberId);
			ResultSet rs = ps.executeQuery();
			Member m = getMember(rs);
			m.setChannels(ChannelController.getChannels(memberId));
			return m;
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			throw new TransactionProcessingException();
		}
	}

	public static Member getMinimalMember(int memberId) throws TransactionProcessingException {
		String query = "select * from members where id = ?;";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setInt(1, memberId);
			ResultSet rs = ps.executeQuery();
			Member m = getMinimalMember(rs);
			return m;
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			throw new TransactionProcessingException();
		}

	}
	
	public static int createMember(Member newMember) throws TransactionProcessingException {
		String query = "insert into members (email,username,reputation,gender,img_path) values (?,?,?,?,?)";
		PreparedStatement ps = DBCUtils.getStatement(query, true);
		try {
			Logger.debug("creating new member: " + newMember.toString());
			ps.setString(1, newMember.getEmail());
			ps.setString(2, newMember.getName());
			ps.setInt(3, 0);
			ps.setString(4, "" + newMember.getGender());
			if (newMember.getImgPath() != null) {
				ps.setString(5, newMember.getImgPath());
			} else {
				ps.setString(5,
						"http://www.picturesnew.com/media/images/genius-photo.png");
			}
			ps.execute();
			ResultSet rs = ps.getGeneratedKeys();
			if (!rs.next()) {
				Logger.debug("Failed to create new member");
				return -1;
			}
			int id = rs.getInt("id");
			
			Logger.debug("Created new member with id: ");
			
			return id;
		} catch (SQLException e) {
			Logger.debug(e.getLocalizedMessage());
			throw new TransactionProcessingException();
		}
	}

	public static Collection<Member> searchMembers(String searchQuery, int begin, int offset) throws TransactionProcessingException {
		String query = "Select * from members where  to_tsvector(UPPER(UNACCENT(email)) || ' ' || UPPER(UNACCENT(username))) @@ plainto_tsquery(UPPER(UNACCENT(?))) LIMIT ? OFFSET ?;";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			Collection<Member> members = new LinkedList<Member>();
			ps.setString(1, searchQuery);
			ps.setInt(2, offset);
			ps.setInt(3, begin);
			ResultSet rs = ps.executeQuery();
			Member m = getMember(rs);
			while(m != null) {
				members.add(m);
				m = getMinimalMember(rs);
			}
			return members;
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			throw new TransactionProcessingException();
		}
	}
	
	

	public static void setSmartTv(int memberId, int channelId) throws TransactionProcessingException {
		String query = "update members set smart_tv = ? where id = ?";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setInt(1, channelId);
			ps.setInt(2, memberId);
			ps.executeUpdate();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			throw new TransactionProcessingException();
		}
	}

	public static void updatePoints(int memberId, int numPoints) throws TransactionProcessingException {
		String query = "update members set reputation = GREATEST(reputation + ?, 1) where id = ?";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setInt(1, numPoints);
			ps.setInt(2, memberId);
			ps.executeUpdate();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			throw new TransactionProcessingException();
		}	
	}

	public static Collection<Member> autocomplete(String keyword) throws TransactionProcessingException {
		String query = "select * from members where UPPER(UNACCENT(username)) like UNACCENT(UPPER(? || '%')) OR UPPER(UNACCENT(email)) like UNACCENT(UPPER(? || '%'));";
		PreparedStatement ps = DBCUtils.getStatement(query);
		Logger.debug(keyword);
		Collection<Member> members = new LinkedList<Member>();
		ResultSet rs;
		try {
			ps.setString(1, keyword);
			ps.setString(2, keyword);
			rs = ps.executeQuery();
			Member m = getMinimalMember(rs);
			while(m != null) {
				members.add(m);
				m = getMinimalMember(rs);
			}
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
		return members;
	}

	public static void editMember(int memberId, Member member) throws TransactionProcessingException {
		String query = "update members set username = ?, img_path = ?, gender = ? where id = ?";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setString(1, member.getName());
			ps.setString(2, member.getImgPath());
			ps.setString(3, member.getGender());
			ps.setInt(4, memberId);
			ps.executeUpdate();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			throw new TransactionProcessingException();
		}
		
	}
}
