package manager.data.campus;

import java.util.Collection;

import security.campus.api.HtmlContentSanitizer;
import utils.campus.Logger;
import data.campus.Channel;
import data.campus.Member;
import data.campus.VotedContent;
import dbm.data.campus.ChannelPersistor;
import dbm.data.campus.MemberPersistor;
import exceptions.campus.CantEditSmartTvException;
import exceptions.campus.NoSuchChannelException;
import exceptions.campus.NoSuchTagException;
import exceptions.campus.NotAuthorizedException;
import exceptions.campus.TransactionProcessingException;

public class ChannelManager {

	//Filter Types
	public static final String SMARTFILTER = "smart";
	public static final String REGULARFILTER = "regular";
	public static final String LIKEFILTER = "like";

	
	//Time constraint types
	public static final String LASTHOURFILTER = "trending";
	public static final String REGULARHOURFILTER = "regular";
	
	
	public static final String SMARTTV = "Smart News";
	
	
	/**
	 * Gets a channel, given its id.
	 * @param id
	 * @return
	 * @throws TransactionProcessingException 
	 */
	public static Channel getChannel(int channelId) throws NoSuchChannelException, TransactionProcessingException {
		Logger.verboseLog("ChannelManager getChannel called");
		Logger.debug("ChannelPersistor get called with params: channelId: " + channelId);
		return ChannelPersistor.get(channelId);
	}

	/**
	 * Creates a channel, given the owner, and the channel object.
	 * @param memberId
	 * @param newChannel
	 * @return
	 * @throws TransactionProcessingException 
	 * @throws NoSuchTagException 
	 */
	public static Channel createChannel(int memberId, Channel newChannel) throws NoSuchChannelException, TransactionProcessingException, NoSuchTagException {
		newChannel.setOwnerId(memberId);
		newChannel.setName(HtmlContentSanitizer.strictSanitizeHtml(newChannel.getName()));
		int channelId = ChannelPersistor.createChannel(memberId, newChannel);
		return ChannelPersistor.get(channelId);
	}

	/**
	 * Edits a channel, given its id, the owners id, and the channel.
	 * @param memberId
	 * @param id
	 * @param channel
	 * @return
	 * @throws NoSuchChannelException 
	 * @throws NotAuthorizedException 
	 * @throws CantEditSmartTvException 
	 * @throws TransactionProcessingException 
	 * @throws NoSuchTagException 
	 */
	public static Channel editChannel(int memberId, int channelId, Channel channel) throws NoSuchChannelException, NotAuthorizedException, CantEditSmartTvException, TransactionProcessingException, NoSuchTagException {
		Logger.verboseLog("ChannelManager editChannel called");
		Logger.debug("ChannelPersistor editChannel called with params: memberId: " + memberId + " channelId: " + channelId +
				" channel: " + channel.toString());
		Channel c = ChannelPersistor.get(channelId);
		if(c.getOwnerId() != memberId || c.getName().equals(SMARTTV)) throw new NotAuthorizedException();
		Member m = MemberPersistor.getMember(memberId);
		if(m.getSmartTv().getId() == channelId) throw new CantEditSmartTvException();
		channel.setName(HtmlContentSanitizer.strictSanitizeHtml(channel.getName()));
		ChannelPersistor.editChannel(memberId, channelId, channel);
		return ChannelPersistor.get(channelId);
	}

	/**
	 * Gets some news from the channel.
	 * @param channel
	 * @param begin
	 * @param offset
	 * @return
	 * @throws TransactionProcessingException 
	 */
	public static Collection<VotedContent> getNews(int memberId, Channel channel, int begin, int offset) throws TransactionProcessingException {
		Logger.verboseLog("ChannelManager getNews called");
		Logger.debug("ChannelPersistor getNews called with params: channel: " + channel.toString() + " begin: " + begin +
				" offset: " + offset);
		return ChannelPersistor.getNews(memberId, channel, begin, offset);
	}

	/**
	 * Searches for channels, given the search query.
	 * @param filterTypes
	 * @param name
	 * @param tags
	 * @param begin
	 * @param offset
	 * @return
	 * @throws TransactionProcessingException 
	 */
	public static Collection<Channel> search(
			String name, int begin, int offset) throws TransactionProcessingException {
		Logger.verboseLog("ChannelManager search called");
		Logger.debug("ChannelPersistor search called with params: name: " + name +  " begin: " + begin + " offset: " + offset);
		return ChannelPersistor.search(name, begin, offset);
	}

	public static void deleteChannel(int memberId, int channelId) throws NotAuthorizedException, NoSuchChannelException, TransactionProcessingException, CantEditSmartTvException {
		if(ChannelPersistor.isOwner(memberId, channelId)) {
			if(MemberManager.getMember(memberId).getSmartTv().getId() == channelId) throw new CantEditSmartTvException();
			ChannelPersistor.deleteChannel(channelId);
		} else throw new NotAuthorizedException();
		
	}

}
