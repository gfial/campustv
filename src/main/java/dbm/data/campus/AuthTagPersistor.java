package dbm.data.campus;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import controller.db.campus.AuthTagController;
import data.campus.Member;
import data.campus.Tag;
import exceptions.campus.TransactionProcessingException;

public class AuthTagPersistor {

	public static void addManager(int memberId, int tagId) throws TransactionProcessingException {
		if (isMember(memberId, tagId)) {
			AuthTagController.promote(memberId, tagId);
		} else
			AuthTagController.addMember(memberId, tagId, true);
	}

	public static void revokeManager(int memberId, int tagId) throws TransactionProcessingException {
		AuthTagController.revokeManager(memberId, tagId);
	}

	public static void addMember(int memberId, int tagId) throws TransactionProcessingException {
		AuthTagController.addMember(memberId, tagId, false);
	}

	public static void revokeMember(int memberId, int tagId) throws TransactionProcessingException {
		remRecAuthChilds(tagId, memberId);
		AuthTagController.revokeMember(memberId, tagId);
	}

	/**
	 * Removes a member from the childs of a tag, in which he is only
	 * authenticated by the tag.
	 * 
	 * @param tagId
	 * @param memberId
	 * @throws TransactionProcessingException 
	 */
	private static void remRecAuthChilds(int tagId, int memberId) throws TransactionProcessingException {
		// TODO check this method
		for (Integer child : TagPersistor.getChilds(tagId)) {
			int numAuths = 0;
			for (Integer parent : TagPersistor.getParents(child)) {
				if (isMember(memberId, parent)) {
					numAuths++;
					if (numAuths == 2)
						break;
				}
			}
			if (numAuths < 2) {
				remRecAuthChilds(child, memberId);
				AuthTagController.revokeMember(memberId, child);
			}
		}
	}

	public static boolean isManager(int memberId, int tagId) throws TransactionProcessingException {
		return AuthTagController.isManager(memberId, tagId);
	}

	public static boolean isMember(int memberId, int tagId) throws TransactionProcessingException {
		if (exists(tagId)) {
			return AuthTagController.isMember(memberId, tagId);
		}
		Collection<Integer> parents = TagPersistor
				.getAuthenticated(TagPersistor.getParents(tagId));
		for (Integer parent : parents) {
			if (AuthTagController.isMember(memberId, parent))
				return true;
		}
		return false;
	}

	public static boolean exists(int tagId) throws TransactionProcessingException {
		return AuthTagController.exists(tagId);
	}

	public static void createAuthTag(int memberId, int tagId) throws TransactionProcessingException {
		AuthTagController.createAuthTag(tagId);
		AuthTagController.addMember(memberId, tagId, true);
	}

	public static Collection<Member> getMembers(int tagId) throws TransactionProcessingException {
		return MemberPersistor.getMembers(AuthTagController.getMembers(tagId));
	}

	public static Collection<Member> getManagers(int tagId) throws TransactionProcessingException {
		return MemberPersistor.getMembers(AuthTagController.getManagers(tagId));
	}

	public static Collection<Member> autocompleteManagers(int tagId,
			String query) throws TransactionProcessingException {
		Collection<Integer> parentMembers = AuthTagController.autocompleteManagers(tagId, query);
		return MemberPersistor.getMinimalMembers(parentMembers);
	}

	public static Collection<Member> autocompleteMembers(int tagId, String query) throws TransactionProcessingException {
		Collection<Integer> parentMembers = AuthTagController.autocompleteMembers(tagId, query);
		return MemberPersistor.getMinimalMembers(parentMembers);
	}

	public static Collection<Member> autocompleteParentMembers(int tagId,
			String query) throws TransactionProcessingException {
		Collection<Integer> parents = TagPersistor
				.getAuthenticated(TagPersistor.getParents(tagId));
		if(parents.isEmpty()) return MemberPersistor.autocomplete(query);
		Map<Integer, Integer> members = new HashMap<Integer, Integer>();
		for(Integer parent: parents) {
			Collection<Integer> pmembers = AuthTagController.autocompleteMembers(parent, query);
			for(Integer member: pmembers) {
				members.put(member, member);
			}
		}
		
		Collection<Integer> parentMembers = members.keySet();
		return MemberPersistor.getMinimalMembers(parentMembers);
	}

	public static Collection<Tag> getManagedTags(int memberId) throws TransactionProcessingException {
		return AuthTagController.getManagedTags(memberId);
	}

	public static Collection<Tag> getMembershipedTags(int memberId) throws TransactionProcessingException {
		return AuthTagController.getMembershipedTags(memberId);
	}
}
