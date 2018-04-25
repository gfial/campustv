package dbm.data.campus;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import utils.campus.Logger;
import utils.exceptions.campus.Errors;
import controller.db.campus.NewsController;
import data.campus.Member;
import data.campus.News;
import data.campus.Tag;
import data.campus.VotedContent;
import exceptions.campus.NoSuchNewsException;
import exceptions.campus.TransactionProcessingException;

public class NewsPersistor {

	public static final int BASETAG = 1;

	public static int createNews(Member member, News news)
			throws TransactionProcessingException {
		if(news.getBrief() == null) {
			news.setBrief(news.getContent().substring(0, 80));
		}
		if(news.getBrief().length() > 140) {
			news.setBrief(news.getContent().substring(0, 80));
		}
		
		if(news.getContent().length() > 5000) {
			throw new TransactionProcessingException(Errors.NEWSTOOBIG);
		}
		
		
		
		int newsId = NewsController.insertNews(news, member);
		Map<Integer, Boolean> tags = setupNewsTags(news.getTags(), true);
		NewsController.setTags(newsId, tags);
		return newsId;
	}

	public static void deleteNews(int memberId) throws TransactionProcessingException {
		Logger.verboseLog("NewsPersistor deleteNews called");
		NewsController.deleteNews(memberId);
	}

	// Ver tudo do vote. o report nao ta muito bem. e a pontuacao da noticia tb
	// nao.
	public static VotedContent vote(int memberId, int newsId, VotedContent vote)
			throws NoSuchNewsException, TransactionProcessingException {
		Logger.debug(vote.toString());

		boolean likes = vote.getLike();
		boolean reports = vote.getReport();

		VotedContent v = getVotedContent(memberId, newsId);

		Logger.debug("Previous vote: \n" + v.toString());

		int weight = Math.max(0, MemberPersistor.getMember(memberId)
				.getReputation());

		// Verifies if the like has been changed
		if (v.getLike() != likes) {
			Logger.verboseLog("Like has changed");
			if (!likes)
				removeLike(memberId, newsId);
			else
				addLike(memberId, newsId, weight);
		}

		// Verified if the report has been changed
		if (v.getReport() != reports) {
			Logger.verboseLog("News has been reported.");
			if (reports)
				addReport(memberId, newsId, weight);
		}

		return getVotedContent(memberId, newsId);
	}

	private static void addReport(int memberId, int newsId, int weight) throws TransactionProcessingException {
		// Por transaction
		NewsController.insertReport(memberId, newsId, weight);
		NewsController.addReportWeight(newsId, weight);
		int authorId = NewsController.getAuthor(newsId);
		MemberPersistor.voteDown(authorId, newsId);
	}

	private static void addLike(int memberId, int newsId, int weight) throws TransactionProcessingException {
		// Por transaction
		NewsController.insertLike(memberId, newsId, weight);
		NewsController.addLikeWeight(newsId, weight);
		int authorId = NewsController.getAuthor(newsId);
		MemberPersistor.voteUp(authorId);

		ChannelPersistor.updateSmartTvAddLikes(newsId, memberId);
	}

	private static void removeLike(int memberId, int newsId) throws TransactionProcessingException {
		// Por transaction
		int weight = NewsController.getLikeWeight(newsId, memberId);
		Logger.verboseLog("weight of the like was " + weight);
		NewsController.deleteLike(newsId, memberId);
		NewsController.decreaseLikeWeight(newsId, weight);
		int authorId = NewsController.getAuthor(newsId);
		MemberPersistor.voteDown(authorId);

		ChannelPersistor.updateSmartTvRmLikes(newsId, memberId);
	}

	public static int getAuthor(int newsId) throws TransactionProcessingException {
		return NewsController.getAuthor(newsId);
	}

	/**
	 * Sets up the new tags for a news.
	 * 
	 * @param tags
	 * @return
	 * @throws TransactionProcessingException 
	 */
	public static Map<Integer, Boolean> setupNewsTags(
			Collection<Tag> newsTags, boolean recursive) throws TransactionProcessingException {
		Map<Integer, Boolean> tags = new HashMap<Integer, Boolean>();
		for (Tag tag : newsTags) {
			if (!tags.containsKey(tag)) {
				tags.put(tag.getId(), true);
				if (recursive) {
					setupNewsTags(tags, tag.getId());
				}
			}
		}
		return tags;
	}

	private static void setupNewsTags(Map<Integer, Boolean> tags, Integer tag) throws TransactionProcessingException {
		Collection<Integer> parents = TagPersistor.getAllParents(tag);
		for (Integer ctag : parents) {
			tags.put(ctag, false);
		}
	}

	public static VotedContent getVotedContent(int memberId, int newsId)
			throws NoSuchNewsException, TransactionProcessingException {
		return NewsController.getVotedContent(memberId, newsId);
	}

	public static int editNews(int memberId, int newsId, News news) throws TransactionProcessingException {
		Map<Integer, Boolean> tags = setupNewsTags(news.getTags(), true);
		NewsController.setTags(newsId, tags);
		return NewsController.editNews(memberId, newsId, news);
	}

	public static Collection<VotedContent> search(int memberId,
			String query, int begin, int offset) throws TransactionProcessingException {
		return NewsController.search(memberId, query, begin, offset);
	}

	public static Collection<VotedContent> getTagNews(int memberId, int tagId, int begin, int offset) throws TransactionProcessingException {
		return NewsController.getTagNews(memberId, tagId, begin, offset);
	}

	public static Collection<VotedContent> getContentsBy(int authorId,
			int memberId, int begin, int offset) throws TransactionProcessingException {
		Collection<Integer> news = NewsController.getContentsBy(authorId, begin, offset);
		return  NewsController.getNews(memberId, news);
	}
}
