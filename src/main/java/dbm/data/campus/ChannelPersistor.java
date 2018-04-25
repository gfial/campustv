package dbm.data.campus;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import utils.campus.Logger;
import controller.db.campus.ChannelController;
import data.campus.Channel;
import data.campus.Filter;
import data.campus.Member;
import data.campus.Tag;
import data.campus.VotedContent;
import dbc.data.campus.DBCUtils;
import exceptions.campus.NoSuchChannelException;
import exceptions.campus.NoSuchNewsException;
import exceptions.campus.NoSuchTagException;
import exceptions.campus.TransactionProcessingException;

public class ChannelPersistor {

	public static void validateFilters(Collection<Filter> filters) throws NoSuchTagException, TransactionProcessingException {
		if(filters == null) return;
		for(Filter f: filters) {
			int tagId = TagPersistor.getTag(f.getTag());
			f.setTag(TagPersistor.getTag(tagId));
		}
	}
	
	public static Channel get(int channelId) throws NoSuchChannelException, TransactionProcessingException {
		return ChannelController.getChannel(channelId);
	}

	public static int createChannel(int memberId, Channel newChannel) throws TransactionProcessingException, NoSuchTagException {
		Logger.verboseLog("ChannelPersistor createChannel called");
		newChannel.setOwnerId(memberId);
		int channelId = ChannelController.createChannel(newChannel);
		validateFilters(newChannel.getFilter());
		Logger.verboseLog(newChannel.getFilter().toString());
		ChannelController.setupChannelTags(newChannel, channelId);
		return channelId;
	}

	public static void editChannel(int memberId, int newsId, Channel channel) throws TransactionProcessingException, NoSuchTagException {
		Logger.verboseLog("ChannelPersistor editChannel called");
		ChannelController.deleteChannelTags(newsId);
		validateFilters(channel.getFilter());
		ChannelController.setupChannelTags(channel, newsId);
		ChannelController.updateChannel(newsId, channel);
	}

	public static Collection<VotedContent> getNews(int memberId,
			Channel channel, int begin, int offset) throws TransactionProcessingException {
		Logger.verboseLog("ChannelPersistor getNews called");
		return ChannelController.getNews(memberId, channel.getId(),
				begin, offset);
	}

	public static Collection<Channel> search(String searchQuery, int begin,
			int offset) throws TransactionProcessingException {
		Logger.verboseLog("ChannelPersistor search called");
		Collection<Channel> channels = new LinkedList<Channel>();
		String query = "select * from channels where  to_tsvector(name || ' ' || filter_type) @@ plainto_tsquery(?) LIMIT ? OFFSET ?";
		PreparedStatement ps = DBCUtils.getStatement(query);
		try {
			ps.setString(1, searchQuery);
			ps.setInt(2, offset);
			ps.setInt(3, begin);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Channel c = ChannelController.getChannel(rs);
				channels.add(c);
			}
		} catch (SQLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			Logger.debug(e.getSQLState());
		}
		return channels;
	}

	public static void updateSmartTvAddLikes(int newsId, int memberId) throws TransactionProcessingException {
		Member member = MemberPersistor.getMember(memberId);
		Logger.verboseLog(member.toString());
		Channel smartTv = MemberPersistor.getMember(memberId).getSmartTv();
		try {
			Collection<Tag> newsTags = NewsPersistor
					.getVotedContent(memberId, newsId).getNews().getTags();
			Logger.debug(newsTags.toString());
			for (Tag tag : newsTags) {
				addSmartTvVoteUp(smartTv.getId(), tag.getId());
			}
		} catch (NoSuchNewsException e) {
			Logger.verboseLog(e.getLocalizedMessage());
		}
	}

	private static void addSmartTvVoteUp(int smartTv, int tagId) throws TransactionProcessingException {
		Logger.verboseLog("Adding vote up to channel_tag to tv " + smartTv);
		if (!ChannelController.exists(smartTv, tagId)) {
			ChannelController.addCategory(smartTv, tagId);
		} else {
			ChannelController.incCategoryWeight(smartTv, tagId);
		}
	}

	public static void updateSmartTvRmLikes(int newsId, int memberId) throws TransactionProcessingException {
		Channel smartTv = MemberPersistor.getMember(memberId).getSmartTv();
		try {
			Collection<Tag> newsTags = NewsPersistor
					.getVotedContent(memberId, newsId).getNews().getTags();
			Logger.debug(newsTags.toString());
			for (Tag tag : newsTags) {
				rmSmartTvVoteUp(smartTv.getId(), tag.getId());
			}
		} catch (NoSuchNewsException e) {
			Logger.verboseLog(e.getLocalizedMessage());
		}
	}

	private static void rmSmartTvVoteUp(int smartTv, int tagId) throws TransactionProcessingException {
		if (ChannelController.exists(smartTv, tagId))
			ChannelController.decCategoryWeight(smartTv, tagId);

	}

	public static boolean isOwner(int memberId, int channelId) throws NoSuchChannelException, TransactionProcessingException {
		return get(channelId).getOwnerId() == memberId;
	}

	public static void deleteChannel(int channelId) throws TransactionProcessingException {
		ChannelController.deleteChannel(channelId);
	}

}
