package controller.db.campus;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import utils.campus.Logger;
import data.campus.Member;
import data.campus.News;
import data.campus.Tag;
import data.campus.VotedContent;
import dbc.data.campus.DBCUtils;
import dbm.data.campus.TagPersistor;
import exceptions.campus.NoSuchNewsException;
import exceptions.campus.TransactionProcessingException;

public class NewsController {

	private static VotedContent getVotedContent(int memberId, ResultSet rs)
			throws SQLException, TransactionProcessingException {
		VotedContent v;
		int id = rs.getInt("id");
		String imgPath = rs.getString("img_path");
		String title = rs.getString("title");
		String brief = rs.getString("brief");
		String content = rs.getString("content");
		boolean show = rs.getBoolean("show");
		int authorId = rs.getInt("author");
		int nlikes = rs.getInt("likes");
		int nreports = rs.getInt("reports");
		int likeWeight = rs.getInt("like_weight");
		int reportWeight = rs.getInt("report_weight");
		java.util.Date creationDate = rs.getTimestamp("creation_date");
		java.util.Date eventDate = rs.getTimestamp("event_date");

		Collection<Tag> tags = getTags(id);

		News news = new News(id, title, brief, content, imgPath, tags, show,
				authorId, nlikes, nreports, likeWeight, reportWeight,
				creationDate, eventDate);
		v = new VotedContent(news, like(memberId, id), report(memberId, id));
		return v;
	}

	public static int insertNews(News news, Member member)
			throws TransactionProcessingException {
		String query = "insert into news (title, brief, content, img_path, show, author, creation_date, likes, reports, like_weight, report_weight, event_date) values (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, 0, 0, ?, 0, ?);";
		Logger.verboseLog("NewsPersistor insertNews called");
		int id = -1;
		
		try {
			PreparedStatement ps = DBCUtils.getStatement(query, true);

			ps.setString(1, news.getTitle());
			ps.setString(2, news.getBrief());
			ps.setString(3, news.getContent());
			ps.setString(4, news.getImgPath());
			ps.setBoolean(5, news.getShow());
			ps.setInt(6, member.getId());
			ps.setInt(7, member.getReputation());

			Timestamp date = news.getEventDate() != null ? new Timestamp(news
					.getEventDate().getTime()) : null;
			ps.setTimestamp(8, date);
			ps.execute();

			ResultSet rs = ps.getGeneratedKeys();
			rs.next();
			id = rs.getInt(1);

		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}

		return id;
	}

	public static int editNews(int memberId, int newsId, News news) throws TransactionProcessingException {
		Logger.verboseLog("NewsPersistor editNews called");
		editNews(newsId, news);
		setLastEditor(memberId, newsId);
		return newsId;
	}

	private static void setLastEditor(int memberId, int newsId) throws TransactionProcessingException {
		String query = "INSERT INTO Last_Edition (news_id, member_id, edition_date ) VALUES (?,?,CURRENT_TIMESTAMP);";
		try {
			PreparedStatement ps = DBCUtils.getStatement(query);
			ps.setInt(1, newsId);
			ps.setInt(2, memberId);
			ps.execute();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static void editNews(int newsId, News news) throws TransactionProcessingException {
		Logger.verboseLog("NewsPersistor editNews called");
		String query = "UPDATE News SET img_path=?, title=?, brief=?, content=?, show=? WHERE id =?;";
		try {
			PreparedStatement ps = DBCUtils.getStatement(query);
			ps.setInt(6, newsId);
			ps.setString(1, news.getImgPath());
			ps.setString(2, news.getTitle());
			ps.setString(3, news.getBrief());
			ps.setString(4, news.getContent());
			ps.setBoolean(5, news.getShow());
			ps.execute();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static VotedContent getVotedContent(int memberId, int newsId)
			throws NoSuchNewsException, TransactionProcessingException {
		Logger.verboseLog("NewsPersistor getVotedContent called");

		String query = "SELECT * FROM news where id = ?;";

		VotedContent v = null;
		PreparedStatement preparedStatement;
		preparedStatement = DBCUtils.getStatement(query);
		try {
			preparedStatement.setInt(1, newsId);

			ResultSet rs = preparedStatement.executeQuery();

			if (!rs.next())
				throw new NoSuchNewsException();
			v = getVotedContent(memberId, rs);

		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
		}
		return v;
	}

	private static Collection<Tag> getTags(int id) throws TransactionProcessingException {
		String query = "select tag_id from news_category where news_id=? and chosen";
		PreparedStatement ps = DBCUtils.getStatement(query);
		Collection<Tag> tags = new LinkedList<Tag>();
		try {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int tagId = rs.getInt(1);
				Tag tag = TagController.getTag(tagId);
				Logger.debug("Got tag " + tag);
				if (tagId != TagPersistor.BASE_TAG)
					tags.add(tag);
			}
			return tags;
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	private static boolean like(int memberId, int newsId) throws TransactionProcessingException {
		String query = "SELECT count (*) FROM likes where news_id = ? and member_id = ?;";
		try {
			PreparedStatement preparedStatement = DBCUtils.getStatement(query);
			preparedStatement.setInt(1, newsId);
			preparedStatement.setInt(2, memberId);

			ResultSet rs = preparedStatement.executeQuery();
			rs.next();
			return rs.getInt("count") > 0;

		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	private static boolean report(int memberId, int newsId) throws TransactionProcessingException {
		String query = "SELECT count (*) FROM report where news_id = ? and member_id = ?;";
		try {
			PreparedStatement preparedStatement = DBCUtils.getStatement(query);
			preparedStatement.setInt(1, newsId);
			preparedStatement.setInt(2, memberId);

			ResultSet rs = preparedStatement.executeQuery();
			rs.next();
			return rs.getInt("count") > 0;

		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static Collection<VotedContent> search(int memberId,
			String searchQuery, int begin, int offset) throws TransactionProcessingException {
		Logger.verboseLog("NewsPersistor search called");
		Collection<VotedContent> news = new LinkedList<VotedContent>();
		Logger.verboseLog("NewsPersistor getVotedContent called");
		
		String query = "(SELECT id "
				+ "FROM news "
				+ "WHERE to_tsvector("
				+ "UPPER(UNACCENT(news.title)) || ' ' || "
				+ "UPPER(UNACCENT(news.content)) || ' ' || "
				+ "UPPER(UNACCENT(news.brief))) @@ plainto_tsquery(UPPER(UNACCENT(?))) "
				+ "ORDER BY creation_date DESC LIMIT ? OFFSET ?) "
				+ "UNION (SELECT news_id "
				+ "FROM news_category inner join tags on (tag_id = tags.id) "
				+ "WHERE  to_tsvector(UPPER(UNACCENT(tags.name))) @@ plainto_tsquery(UPPER(UNACCENT(?))) "
				+ "ORDER BY news_id DESC LIMIT ? OFFSET ?)"
				+ "ORDER BY id DESC LIMIT ? OFFSET ?";

		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setString(1, searchQuery);
			ps.setInt(2, offset);
			ps.setInt(3, begin);
			ps.setString(4, searchQuery);
			ps.setInt(5, offset);
			ps.setInt(6, begin);
			ps.setInt(7, offset);
			ps.setInt(8, begin);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				VotedContent v = getVotedContent(memberId, rs.getInt(1));

				news.add(v);

				Logger.debug(v.toString());
			}

		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		} catch (NoSuchNewsException e) {
			Logger.verboseLog(e.getLocalizedMessage());
		}
		return news;
	}

	public static void setTags(int newsId, Map<Integer, Boolean> tags) throws TransactionProcessingException {
		removeNewsTags(newsId);
		setNewsTags(newsId, tags);
	}

	private static void removeNewsTags(int newsId) throws TransactionProcessingException {
		String query = "delete from news_category where news_id = ?";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setInt(1, newsId);
			ps.execute();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	private static void setNewsTags(int newsId, Map<Integer, Boolean> tags) throws TransactionProcessingException {
		String query = "Insert into news_category (news_id,tag_id,chosen) values(?,?,?);";
		for (Integer tag : tags.keySet()) {
			PreparedStatement ps = DBCUtils.getStatement(query);
			try {
				ps.setInt(1, newsId);
				ps.setInt(2, tag);
				ps.setBoolean(3, tags.get(tag));
				ps.execute();
			} catch (SQLException e) {
				Logger.verboseLog(e.getLocalizedMessage());
				Logger.debug(e.getSQLState());
				throw new TransactionProcessingException();
			}
		}
	}

	public static int getAuthor(int newsId) throws TransactionProcessingException {
		String query = "SELECT author FROM news where id = ?;";
		try {
			PreparedStatement preparedStatement = DBCUtils.getStatement(query);
			preparedStatement.setInt(1, newsId);
			ResultSet rs = preparedStatement.executeQuery();
			rs.next();
			return rs.getInt("author");
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static void deleteNews(int memberId) throws TransactionProcessingException {
		String query = "delete from news where id = ? cascade";
		try {
			PreparedStatement preparedStatement = DBCUtils.getStatement(query);
			preparedStatement.setInt(1, memberId);
			preparedStatement.execute();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static int getLikeWeight(int newsId, int memberId) throws TransactionProcessingException {
		String query = "select weight from likes where member_id = ? and news_id = ?";
		int weight = 0;
		try {
			PreparedStatement ps = DBCUtils.getStatement(query);
			ps.setInt(1, memberId);
			ps.setInt(2, newsId);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				weight = rs.getInt(1);
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
		return weight;
	}

	public static int getReportWeight(int newsId, int memberId) throws TransactionProcessingException {
		String query = "select weight from report where member_id = ? and news_id = ?";
		int weight = 0;
		try {
			PreparedStatement ps = DBCUtils.getStatement(query);
			ps.setInt(1, memberId);
			ps.setInt(2, newsId);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				weight = rs.getInt(1);
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
		return weight;
	}

	public static void deleteLike(int newsId, int memberId) throws TransactionProcessingException {
		String query;
		query = "delete from likes where news_id = ? and member_id = ?";
		try {
			PreparedStatement ps = DBCUtils.getStatement(query);
			ps.setInt(1, newsId);
			ps.setInt(2, memberId);
			ps.execute();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static void deleteReport(int newsId, int memberId) throws TransactionProcessingException {
		String query;
		query = "delete from report where news_id = ? and member_id = ?";
		try {
			PreparedStatement ps = DBCUtils.getStatement(query);
			ps.setInt(1, newsId);
			ps.setInt(2, memberId);
			ps.execute();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static void insertLike(int memberId, int newsId, int weight) throws TransactionProcessingException {
		String query = "insert into likes (news_id,member_id,weight) values (?,?,?)";
		try {
			PreparedStatement preparedStatement = DBCUtils.getStatement(query);
			preparedStatement.setInt(1, newsId);
			preparedStatement.setInt(2, memberId);
			preparedStatement.setInt(3, weight);
			preparedStatement.execute();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static void insertReport(int memberId, int newsId, int weight) throws TransactionProcessingException {
		String query = "insert into report (news_id,member_id,weight) values (?,?,?)";
		try {
			PreparedStatement preparedStatement = DBCUtils.getStatement(query);
			preparedStatement.setInt(1, newsId);
			preparedStatement.setInt(2, memberId);
			preparedStatement.setInt(3, weight);
			preparedStatement.execute();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static void decreaseLikeWeight(int newsId, int weight) throws TransactionProcessingException {
		String query;
		query = "update news set like_weight = like_weight - ?, likes = likes - 1 where id = ?";
		try {
			PreparedStatement preparedStatement = DBCUtils.getStatement(query);
			preparedStatement.setInt(1, weight);
			preparedStatement.setInt(2, newsId);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static void addLikeWeight(int newsId, int weight) throws TransactionProcessingException {
		String query;
		query = "update news set like_weight = like_weight + ?, likes = likes + 1 where id = ?";
		try {
			PreparedStatement preparedStatement = DBCUtils.getStatement(query);
			preparedStatement.setInt(1, weight);
			preparedStatement.setInt(2, newsId);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static void addReportWeight(int newsId, int weight) throws TransactionProcessingException {
		String query;
		query = "update news set report_weight = report_weight + ?, reports = reports + 1 where id = ?;";
		try {
			PreparedStatement preparedStatement = DBCUtils.getStatement(query);
			preparedStatement.setInt(1, weight);
			preparedStatement.setInt(2, newsId);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static Collection<VotedContent> getTagNews(int memberId, int tagId, int begin, int offset) throws TransactionProcessingException {
		String query = "select * from news_category INNER JOIN news ON (news_id = id) where tag_id = ? ORDER BY like_weight DESC LIMIT ? OFFSET ?";
		Collection<VotedContent> news = new LinkedList<VotedContent>();
		try {
			PreparedStatement ps = DBCUtils.getStatement(query);
			ps.setInt(1, tagId);
			ps.setInt(2, offset);
			ps.setInt(3, begin);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				news.add(getVotedContent(memberId, rs));
			}
			return news;
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static Collection<VotedContent> getNews(int memberId,
			Collection<Integer> newsIds) throws TransactionProcessingException {
		Collection<VotedContent> news = new LinkedList<VotedContent>();
		for (Integer newsId : newsIds) {
			try {
				news.add(getVotedContent(memberId, newsId));
			} catch (NoSuchNewsException e) {
			}
		}
		return news;
	}

	public static Collection<Integer> getContentsBy(int authorId, int begin, int offset) throws TransactionProcessingException {
		String query = "select id from news where author = ? ORDER BY creation_date DESC LIMIT ? OFFSET ?";
		Collection<Integer> news = new LinkedList<Integer>();
		try {
			PreparedStatement ps = DBCUtils.getStatement(query);
			ps.setInt(1, authorId);
			ps.setInt(2, offset);
			ps.setInt(3, begin);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				news.add(rs.getInt(1));
			}
			return news;
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

}
