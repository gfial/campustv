package manager.data.campus;

import java.util.Collection;
import java.util.LinkedList;

import security.campus.api.HtmlContentSanitizer;
import utils.campus.Logger;
import utils.campus.ReputationUtils;
import utils.exceptions.campus.Errors;
import data.campus.Tag;
import dbm.data.campus.MemberPersistor;
import dbm.data.campus.TagPersistor;
import exceptions.campus.NoSuchTagException;
import exceptions.campus.NotAuthorizedException;
import exceptions.campus.TransactionProcessingException;

public class TagManager {
	
	/**
	 * Checks if a member has permissions to publish on a certain tag.
	 * 
	 * @param memberId
	 * @param tagId
	 * @return
	 * @throws TransactionProcessingException 
	 */
	public static boolean hasPermissions(int memberId, int tagId) throws TransactionProcessingException {
		Logger.verboseLog("Checking permissions of " + memberId
				+ " to post by " + tagId);
		Tag tag = getTag(tagId);
		if (tag == null)
			return false;
		Logger.verboseLog("The tag is authenticated ? "
				+ tag.getAuthenticated());
		if (tag.getAuthenticated())
			return AuthTagManager.hasPermissions(memberId, tagId);
		return true;
	}

	/**
	 * Checks the permissions of a member to each of the given tags.
	 * 
	 * @param memberId
	 * @param tags
	 * @return
	 * @throws TransactionProcessingException 
	 */
	public static Collection<Integer> checkPermissions(int memberId,
			Collection<Integer> tags) throws TransactionProcessingException {
		Collection<Integer> authorizedTags = new LinkedList<Integer>();
		for (int tag : tags) {
			if (TagManager.hasPermissions(memberId, tag)) {
				authorizedTags.add(tag);
			}
		}
		return authorizedTags;
	}

	/**
	 * Creates a new tag, given the memberId, and the new tag.
	 * 
	 * @param memberId
	 * @param newTag
	 * @return
	 * @throws NotAuthorizedException
	 * @throws TransactionProcessingException 
	 * @throws NoSuchTagException 
	 */
	public static Tag createTag(int memberId, Tag newTag)
			throws NotAuthorizedException, TransactionProcessingException, NoSuchTagException {
		if (MemberPersistor.getMember(memberId).getReputation() < ReputationUtils.TAGCREATIONREP) {
			Logger.verboseLog("Not enough reputation");
			throw new NotAuthorizedException();
		}
		
		newTag.setParents(TagPersistor.getTagIds(newTag.getParents()));
		
		boolean hasPermissions = true;

		Collection<Integer> authParents = TagPersistor.getAuthenticated(newTag
				.getParents());
		hasPermissions = authParents.isEmpty();
		boolean authenticatedParents = !authParents.isEmpty();
		if (!hasPermissions) {
			for (Integer tag : authParents) {
				if (AuthTagManager.hasPermissions(memberId, tag)) {
					hasPermissions = true;
				}
			}
		}

		if (!hasPermissions) {
			Logger.verboseLog("Member is trying to create a tag inside an authenticated "
					+ "tag and he is not a member of the authenticated tag.");
			throw new NotAuthorizedException();
		}
		
		newTag.setBrief(HtmlContentSanitizer.strictSanitizeHtml(newTag.getBrief()));
		newTag.setName(HtmlContentSanitizer.strictSanitizeHtml(newTag.getName()));

		int tagId = TagPersistor.createTag(memberId, newTag,
				authenticatedParents);

		Logger.verboseLog("Creating authenticated tag");

		if (authenticatedParents) {
			Logger.verboseLog("Tag is authenticated");
			if (newTag.getAuthenticated()) {
				Logger.debug("The tag will be a new authenticated tag.");
				AuthTagManager.createAuthTag(memberId, tagId);
			}
		}
		return TagPersistor.getTag(tagId);
	}

	/**
	 * Edits a tag, given its id, the editor's id, and the tag.
	 * 
	 * @param memberId
	 * @param tagId
	 * @param tag
	 * @return
	 * @throws NotAuthorizedException
	 * @throws TransactionProcessingException 
	 */
	public static Tag editTag(int memberId, int tagId, Tag nTag)
			throws NotAuthorizedException, TransactionProcessingException {
		
		if (TagManager.isAuthenticatedTag(tagId)) {
			if(!TagManager.hasPermissions(memberId, tagId)) {
				throw new NotAuthorizedException(Errors.NOPERMISSIONS);
			}
			if(AuthTagManager.isAuthenticated(tagId)) {
				if(!AuthTagManager.isManager(memberId, tagId)) {
					throw new NotAuthorizedException(Errors.NOTAMANAGER);
				}
			}
		} else if (MemberPersistor.getMember(memberId).getReputation() < ReputationUtils.TAGEDITIONREP) {
			Logger.verboseLog(memberId + " hasnt got enough reputation");
			throw new NotAuthorizedException();
		}

		boolean hasPermissions = true;

		Collection<Integer> authParents = TagPersistor
				.getAuthenticated(TagPersistor.getParents(tagId));
		hasPermissions = authParents.isEmpty();
		boolean authenticatedParents = !authParents.isEmpty();
		if (!hasPermissions) {
			for (Integer tag : authParents) {
				if (AuthTagManager.hasPermissions(memberId, tag)) {
					hasPermissions = true;
				}
			}
		}

		if (!hasPermissions) {
			Logger.verboseLog("Member is trying to create a tag inside an authenticated "
					+ "tag and he is not a member of the authenticated tag.");
			throw new NotAuthorizedException();
		}
		
		if (authenticatedParents) {
			if (!AuthTagManager.isManager(memberId, tagId)) {
				throw new NotAuthorizedException();
			}
		}
		nTag.setBrief(HtmlContentSanitizer.strictSanitizeHtml(nTag.getBrief()));
		nTag.setName(HtmlContentSanitizer.strictSanitizeHtml(nTag.getName()));
		return TagPersistor.editTag(memberId, tagId, nTag);
	}

	/**
	 * Gets a tag, given its id.
	 * 
	 * @param tagId
	 * @return
	 * @throws TransactionProcessingException 
	 */
	public static Tag getTag(int tagId) throws TransactionProcessingException {
		return TagPersistor.getTag(tagId);
	}

	/**
	 * Gets a tag, given its name.
	 * 
	 * @param name
	 * @return
	 * @throws TransactionProcessingException 
	 */
	public static int getTagId(String name) throws TransactionProcessingException {
		return TagPersistor.getTagId(name);
	}

	/**
	 * Searches for tags.
	 * @param offset 
	 * @param begin 
	 * 
	 * @param keywords
	 * @param offset
	 * @param begin
	 * @return
	 * @throws TransactionProcessingException 
	 */
	public static Collection<Tag> search(String keyword, int begin, int offset) throws TransactionProcessingException {
		return TagPersistor.search(keyword, begin, offset);
	}

	/**
	 * Checks if a given tag is authenticated or not.
	 * 
	 * @param id
	 * @return
	 * @throws TransactionProcessingException 
	 */
	public static boolean isAuthenticatedTag(int id) throws TransactionProcessingException {
		Tag tag = getTag(id);
		if (tag == null)
			return false;
		return tag.getAuthenticated();
	}

	public static Collection<Tag> autoComplete(String keyword) throws TransactionProcessingException {
		return TagPersistor.autoComplete(keyword);
	}
}
