package controller.db.campus;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import utils.campus.Logger;
import data.campus.Channel;
import data.campus.Filter;
import data.campus.Tag;
import data.campus.VotedContent;
import dbc.data.campus.DBCUtils;
import dbm.data.campus.NewsPersistor;
import exceptions.campus.NoSuchChannelException;
import exceptions.campus.NoSuchNewsException;
import exceptions.campus.TransactionProcessingException;

public class ChannelController {

	public static Filter getFilter(ResultSet rs) throws TransactionProcessingException {
		try {
			int tagId = rs.getInt("tag_id");
			int weight = rs.getInt("weight");
			Tag tag = TagController.getTag(tagId);
			return new Filter(tag, weight);
		} catch (SQLException e) {
			Logger.debug(e.toString());
			throw new TransactionProcessingException();
		}
	}

	public static Collection<Filter> getFilters(ResultSet rs) throws TransactionProcessingException {
		Collection<Filter> filters = new LinkedList<Filter>();
		
		try {
			while(rs.next()) {
				filters.add(getFilter(rs));
			}
			return filters;
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			throw new TransactionProcessingException();
		}
	}

	public static Channel getChannel(ResultSet rs) throws TransactionProcessingException {
		try {
			if (!rs.next())
				return null;
			int id = rs.getInt("id");
			String name = rs.getString("name");
			String filterType = rs.getString("filter_type");
			int owner = rs.getInt("owner");
			boolean trending = rs.getBoolean("trending");
			return new Channel(id, name, owner, null, filterType, trending);
		} catch (SQLException e) {
			Logger.debug(e.getLocalizedMessage());
			throw new TransactionProcessingException();
		}
	}

	public static int createChannel(Channel newChannel) throws TransactionProcessingException {
		String query = "Insert into channels (name, owner, filter_type, trending) values (?, ?, ?, ?);";
		PreparedStatement ps = DBCUtils.getStatement(query, true);
		int channelId = -1;
		try {
			ps.setString(1, newChannel.getName());
			ps.setInt(2, newChannel.getOwnerId());
			ps.setString(3, newChannel.getFilterType());
			ps.setBoolean(4, newChannel.isTrending());
			ps.execute();
			ResultSet rs = ps.getGeneratedKeys();
			rs.next();
			channelId = rs.getInt(1);
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
		return channelId;
	}

	public static void setupChannelTags(Channel channel, int channelId) throws TransactionProcessingException {
		String query;
		PreparedStatement ps;
		query = "insert into channel_tag (channel_id , tag_id, weight) values(?,?,?)";
		for (Filter f : channel.getFilter()) {
			ps = DBCUtils.getStatement(query);
			try {
				ps.setInt(1, channelId);
				ps.setInt(2, f.getTag().getId());
				ps.setInt(3, f.getWeight());
				ps.execute();
			} catch (SQLException e) {
				Logger.verboseLog(e.getLocalizedMessage());
				Logger.debug(e.getSQLState());
				throw new TransactionProcessingException();
			}
		}
	}
	
	public static void updateChannel(int newsId, Channel channel) throws TransactionProcessingException {
		String query = "update channels set name = ?, filter_type = ?, trending = ? where id = ?";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setString(1, channel.getName());
			ps.setString(2, channel.getFilterType());
			ps.setBoolean(3, channel.isTrending());
			ps.setInt(4, newsId);
			ps.executeUpdate();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static Collection<Channel> getChannels(int memberId) throws TransactionProcessingException {
		String query = "Select * from channels where owner = ? and name != 'Smart News'";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setInt(1, memberId);
			ResultSet rs = ps.executeQuery();
			Collection<Channel> channels = new LinkedList<Channel>();
			while(rs.next()) {
				try {
					channels.add(getChannel(rs.getInt("id")));
				} catch (NoSuchChannelException e) {
				}
			}
			return channels;
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			throw new TransactionProcessingException();
		}
	}

	public static Collection<VotedContent> getNews(int memberId, int channelId,
			int begin, int offset) throws TransactionProcessingException {
		String query = "SELECT id, calc_rank(?,id) as rank FROM news WHERE like_weight > report_weight AND COALESCE(event_date, CURRENT_TIMESTAMP) < ( CURRENT_TIMESTAMP  + interval '1 hour') ORDER BY rank DESC LIMIT ? OFFSET ?;";
		Collection<VotedContent> news = new LinkedList<VotedContent>();
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			offset = Math.min(offset, 50);
			ps.setInt(1, channelId);
			ps.setInt(2, offset);
			ps.setInt(3, begin);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				try {
					if(rs.getDouble(2) > 0.0)
						news.add(NewsPersistor.getVotedContent(memberId, rs.getInt(1)));
					Logger.debug(rs.getInt(1) + " " + rs.getInt(2));
				} catch (NoSuchNewsException e) {
					Logger.verboseLog(e.getLocalizedMessage());
				}
			}
			return news;
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			throw new TransactionProcessingException();
		}
	}
	
	public static void deleteChannelTags(int id) throws TransactionProcessingException {
		String deleteTags = "delete from channel_tag where channel_id = ?";
		PreparedStatement ps = DBCUtils.getStatement(deleteTags);
		try {
			ps.setInt(1, id);
			ps.execute();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static Channel getChannel(int channelId) throws NoSuchChannelException, TransactionProcessingException {
		String getChannel = "select id, name, owner, filter_type, trending from channels where id = ?;";
		PreparedStatement ps = DBCUtils.getStatement(getChannel);
		try {
			ps.setInt(1, channelId);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				throw new NoSuchChannelException(channelId);
			}
			Channel channel = new Channel(rs.getInt(1), rs.getString(2),
					rs.getInt(3), null, rs.getString(4), rs.getBoolean(5));
			String getFilter = "select tag_id, weight from channel_tag where channel_id = ?;";
			ps = DBCUtils.getStatement(getFilter);
			ps.setInt(1, channelId);
			Collection<Filter> filters = new LinkedList<Filter>();

			rs = ps.executeQuery();

			while (rs.next()) {
				Tag tag = TagController.getTag(rs.getInt(1));
				Filter filter = new Filter(tag, rs.getInt(2));
				filters.add(filter);
			}

			channel.setFilter(filters);

			return channel;
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static void incCategoryWeight(int smartTv, int tagId) throws TransactionProcessingException {
		String query = "update channel_tag set weight = weight + 1 where tag_id = ? and channel_id = ?";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setInt(1, tagId);
			ps.setInt(2, smartTv);
			ps.executeUpdate();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static void decCategoryWeight(int smartTv, int tagId) throws TransactionProcessingException {
		String query = "update channel_tag set weight = weight - 1 where tag_id = ? and channel_id = ?";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setInt(1, tagId);
			ps.setInt(2, smartTv);
			ps.executeUpdate();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static boolean exists(int smartTv, int tagId) {
		String query = "select * from channel_tag where tag_id = ? and channel_id = ?";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setInt(1, tagId);
			ps.setInt(2, smartTv);
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
		}
		return false;
	}

	public static void addCategory(int smartTv, int tagId) throws TransactionProcessingException {
		String query = "insert into channel_tag (tag_id, channel_id, weight) values (?,?,1)";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setInt(1, tagId);
			ps.setInt(2, smartTv);
			ps.execute();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}

	public static void deleteChannel(int channelId) throws TransactionProcessingException {
		String deleteTags = "delete from channels where id = ?";
		PreparedStatement ps = DBCUtils.getStatement(deleteTags);
		try {
			ps.setInt(1, channelId);
			ps.execute();
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
			throw new TransactionProcessingException();
		}
	}
	
}
