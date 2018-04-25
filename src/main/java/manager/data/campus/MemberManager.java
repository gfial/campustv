package manager.data.campus;

import java.util.Collection;
import java.util.LinkedList;

import security.campus.api.HtmlContentSanitizer;
import utils.campus.Logger;
import data.campus.Member;
import data.campus.Tag;
import dbm.data.campus.AuthTagPersistor;
import dbm.data.campus.MemberPersistor;
import exceptions.campus.MemberAlreadyExistsException;
import exceptions.campus.NoSuchMemberException;
import exceptions.campus.TransactionProcessingException;

public class MemberManager {

	/**
	 * Creates a member.
	 * 
	 * @param newMember
	 * @return
	 * @throws TransactionProcessingException 
	 * @throws MemberAlreadyExistsException 
	 */
	public static Member createMember(String email, String username,
			String password, String imgPath, String gender) throws TransactionProcessingException, MemberAlreadyExistsException {
		Logger.verboseLog("MemberManager createMember called");
		username = HtmlContentSanitizer.strictSanitizeHtml(username);
		
		Member member = new Member(1,username,email,100,imgPath,gender,null,null);
		Logger.debug(member.toString());
		int memberId = MemberPersistor.createMember(member);
		Logger.debug("" + memberId);
		member.setId(memberId);
		SessionManager.createMember(memberId, password);
		Logger.debug(password);
		return MemberPersistor.getMember(memberId);
	}

	/**
	 * Edits a member, given its id.
	 * 
	 * @param id
	 * @param member
	 * @return
	 * @throws TransactionProcessingException 
	 */
	public static Member editMember(int id, Member member) throws TransactionProcessingException {
		Logger.verboseLog("MemberManager editMember called");
		if (member != null)
			Logger.debug("MemberManager editMember called MemberPersistor editMember with params: id: "
					+ id + " member: " + member.toString());
		return MemberPersistor.editMember(id, member);
	}

	/**
	 * Gets the id of a member, given its email address.
	 * 
	 * @param email
	 * @return
	 * @throws NoSuchMemberException 
	 * @throws TransactionProcessingException 
	 */
	public static int getId(String email) throws NoSuchMemberException, TransactionProcessingException {
		return MemberPersistor.getId(email);
	}

	/**
	 * Gets the member, given its id.
	 * 
	 * @param id
	 * @return
	 * @throws TransactionProcessingException 
	 */
	public static Member getMember(int id) throws TransactionProcessingException {
		Logger.verboseLog("MemberManager getMember called");
		Logger.debug("MemberManager getMember called MemberPersistor getMember with params: id: "
				+ id);
		return MemberPersistor.getMember(id);
	}

	public static Collection<Member> searchMembers(String query, int begin, int offset) throws TransactionProcessingException {
		return MemberPersistor.searchMembers(query, begin, offset);
	}

	public static Collection<Member> autocomplete(String query) throws TransactionProcessingException {
		return MemberPersistor.autocomplete(query);
	}

	public static Collection<Tag> memberGroups(int memberId) throws TransactionProcessingException {
		return AuthTagPersistor.getMembershipedTags(memberId);
	}

	public static Collection<Tag> managedGroups(int memberId) throws TransactionProcessingException {
		return AuthTagPersistor.getManagedTags(memberId);
	}

	public static Collection<Tag> autocompleteManagedGroups(int memberId, String term) throws TransactionProcessingException {
		Collection<Tag> tags = new LinkedList<Tag>();
		Collection<Tag> managedGroups = managedGroups(memberId);
		for(Tag tag: managedGroups) {
			if(tag.getName().startsWith(term))
				tags.add(tag);
		}
		return tags;
	}
}
