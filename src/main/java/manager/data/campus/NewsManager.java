package manager.data.campus;

import java.util.Collection;
import java.util.Map;

import lang.campus.Responses;
import security.campus.api.HtmlContentSanitizer;
import utils.campus.Logger;
import utils.campus.ReputationUtils;
import data.campus.Member;
import data.campus.News;
import data.campus.SimpleResponse;
import data.campus.Tag;
import data.campus.VotedContent;
import dbm.data.campus.MemberPersistor;
import dbm.data.campus.NewsPersistor;
import exceptions.campus.NoSuchNewsException;
import exceptions.campus.NoSuchTagException;
import exceptions.campus.NoTagsException;
import exceptions.campus.NotAuthorizedException;
import exceptions.campus.TransactionProcessingException;

public class NewsManager {

	/**
	 * Creates a new news content, given the authors id.
	 * 
	 * @param memberId
	 * @param news
	 * @return
	 * @throws TransactionProcessingException
	 * @throws NoSuchNewsException 
	 * @throws NotAuthorizedException 
	 * @throws NoTagsException 
	 * @throws NoSuchTagException 
	 */
	public static VotedContent createNews(int memberId, News news)
			throws TransactionProcessingException, NoSuchNewsException, NotAuthorizedException, NoTagsException, NoSuchTagException {
		Member member = MemberManager.getMember(memberId);
		
		for(Tag tag: news.getTags()) {
			if(TagManager.getTag(tag.getId()) == null && TagManager.getTag(TagManager.getTagId(tag.getName())) == null)
				throw new NoSuchTagException();
			if(TagManager.getTag(tag.getId()) == null) {
				tag.setId(TagManager.getTagId(tag.getName()));
			}
		}
		
		for(Tag tag: news.getTags()) {
			if(!TagManager.hasPermissions(memberId, tag.getId())) {
				throw new NotAuthorizedException();
			}
		}
		
		setupNews(news);
		
		int newsId = NewsPersistor.createNews(member, news);
		return getVotedContent(memberId, newsId);
	}

	private static void setupNews(News news) throws NoTagsException {
		news.setTitle(HtmlContentSanitizer.sanitizeHtml(news.getTitle()));
		news.setBrief(HtmlContentSanitizer.sanitizeHtml(news.getBrief()));
		news.setContent(HtmlContentSanitizer.sanitizeHtml(news.getContent()));
		
		if(news.getTags().size() == 0) throw new NoTagsException();
		
		String imgUrl = news.getImgPath();
		if(imgUrl == null) {
			for(Tag t: news.getTags()) {
				imgUrl = t.getImgPath();
				break;
			}
		}
		news.setImgPath(imgUrl);
	}

	/**
	 * Edits news, given the news id, and the memberId.
	 * 
	 * @param memberId
	 * @param newsId
	 * @param news
	 * @return
	 * @throws NotAuthorizedException
	 * @throws NoSuchNewsException 
	 * @throws NoTagsException 
	 * @throws TransactionProcessingException 
	 */
	public static VotedContent editNews(int memberId, int newsId, News news)
			throws NotAuthorizedException, NoSuchNewsException, NoTagsException, TransactionProcessingException {
		Logger.verboseLog("NewsManager editNews called");
		Logger.debug("NewsManager editNews with params memberId: " + memberId
				+ " newsId: " + newsId + " news: " + news.toString());

		int authorId = NewsPersistor.getAuthor(newsId);
		Logger.debug("NewsPresistor getAuthor with params memberId: "
				+ memberId + " newsId: " + newsId);

		Member member = MemberPersistor.getMember(memberId);
		Logger.verboseLog("NewsPresistor getMember with params memberId: "
				+ memberId);

		VotedContent v = NewsPersistor.getVotedContent(memberId, newsId);
		News n = v.getNews();
		
		Map<Integer,Boolean> newsTags = NewsPersistor.setupNewsTags(n.getTags(), false);
		
		for (Integer tag : newsTags.keySet()) {
			if (!TagManager.hasPermissions(memberId, tag)) {
				Logger.verboseLog("NewsManager editNews failed because the member is not authenticated by " + tag);
				NotAuthorizedException up = new NotAuthorizedException();
				throw up;
			}
		}
		
		newsTags = NewsPersistor.setupNewsTags(news.getTags(), false);
		for(Integer tag: newsTags.keySet()) {
			if (!TagManager.hasPermissions(memberId, tag)) {
				Logger.verboseLog("NewsManager editNews failed because the member is not authenticated by " + tag);
				NotAuthorizedException up = new NotAuthorizedException();
				throw up;
			}
		}

		if (authorId == memberId
				|| member.getReputation() > ReputationUtils.EDITIONREP) {
			setupNews(news);
			
			newsId = NewsPersistor.editNews(memberId, newsId, news);
			Logger.debug("NewsPersistor editNews called with params: "
					+ " memberId: " + memberId + " newsId: " + newsId
					+ " news: " + news.toString());
		} else {
			Logger.verboseLog("NewsManager editNews failed because the member is neigther the author or have the reputation to do it. The news author is " + authorId);
			NotAuthorizedException up = new NotAuthorizedException();
			throw up;
		}

		Logger.debug("NewsPersistor getVotedContent called with params: "
				+ " memberId: " + memberId + " newsId: " + newsId);
		return NewsPersistor.getVotedContent(memberId, newsId);
	}

	/**
	 * Deletes the news, given the news id, and the member id.
	 * 
	 * @param memberId
	 * @param newsId
	 * @return
	 * @throws NotAuthorizedException
	 * @throws TransactionProcessingException 
	 */
	public static SimpleResponse deleteNews(int memberId, int newsId)
			throws NotAuthorizedException, TransactionProcessingException {
		Logger.verboseLog("NewsManager deleteNews called");
		int authorId = NewsPersistor.getAuthor(newsId);
		Logger.debug("NewsPersistor getAuthor called with params: newsId: "
				+ newsId + " memberId: " + memberId);

		Member member = MemberPersistor.getMember(memberId);

		SimpleResponse response = new SimpleResponse(Responses.NOTALLOWED);
		Logger.verboseLog("SimpleResponse called");
		if (authorId == memberId) {
			Logger.verboseLog("NewsManager de " + newsId
					+ " deleteNews the person deleting is the author");
			NewsPersistor.deleteNews(newsId);
			Logger.debug("NewsPersistor deleteNews called with params: memberId: "
					+ memberId + " newsId: " + newsId);
			response.setResponse(Responses.DELETEDNEWS);
		} else if (member.getReputation() > ReputationUtils.DELETIONREP) {
			Logger.verboseLog("NewsManager deleteNews  the person deleting is not the author, but has the reputation to do it");
			NewsPersistor.deleteNews(newsId);
			Logger.debug("NewsPersistor deleteNews called with params: memberId: "
					+ memberId + " newsId " + newsId);
			response.setResponse(Responses.DELETEDNEWS);
		} else {
			Logger.verboseLog("Member " + memberId
					+ " not authorized to delete " + newsId);
			NotAuthorizedException up = new NotAuthorizedException();
			throw up;
		}
		return response;
	}

	/**
	 * Gets the voted content, given the member Id.
	 * 
	 * @param memberId
	 * @param newsId
	 * @return
	 * @throws NoSuchNewsException 
	 * @throws TransactionProcessingException 
	 */
	public static VotedContent getVotedContent(int memberId, int newsId) throws NoSuchNewsException, TransactionProcessingException {
		return NewsPersistor.getVotedContent(memberId, newsId);
	}

	/**
	 * Votes on some news content, given the newsId, the memberId, and the vote.
	 * 
	 * @param memberId
	 * @param newsId
	 * @param vote
	 * @return
	 * @throws NoSuchNewsException 
	 * @throws TransactionProcessingException 
	 */
	public static VotedContent vote(int memberId, int newsId, VotedContent vote) throws NoSuchNewsException, TransactionProcessingException {
		return NewsPersistor.vote(memberId, newsId, vote);
	}

	/**
	 * Searches a news throughout the news repository.
	 * 
	 * @param keywords
	 * @param tags
	 * @param offset
	 * @param begin
	 * @return
	 * @throws TransactionProcessingException 
	 */
	public static Collection<VotedContent> search(int memberId, String query, int begin, int offset) throws TransactionProcessingException {
		return NewsPersistor.search(memberId, query, begin, offset);
	}

	public static Collection<VotedContent> searchTagNews(int memberId, int id, int begin, int offset) throws TransactionProcessingException {
		return NewsPersistor.getTagNews(memberId, id, begin, offset);
	}

	public static Collection<VotedContent> getContentsBy(int authorId,
			int memberId, int begin, int offset) throws TransactionProcessingException {
		return NewsPersistor.getContentsBy(authorId, memberId, begin, offset);
	}

}
