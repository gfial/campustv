package manager.data.campus;

import java.util.Collection;

import utils.campus.Logger;
import utils.exceptions.campus.Errors;
import data.campus.Member;
import dbm.data.campus.AuthTagPersistor;
import dbm.data.campus.TagPersistor;
import exceptions.campus.NoSuchAuthenticatedTagException;
import exceptions.campus.NotAuthorizedException;
import exceptions.campus.TransactionProcessingException;

public class AuthTagManager {

	/**
	 * TODO
	 * Ver como se vai adicionar e remover membros.
	 * 
	 * Para se poder por alguem como manager:
	 * 	-> quem esta a por e manager.
	 *  -> quem esta a ser posto e membro.
	 * 
	 * Para se poder por alguem como membro:
	 *  -> quem esta a por e manager.
	 *  -> quem esta a ser posto tem de ser membro de 
	 *  	um dos tags do qual este e filho,
	 *  	e se houver algum parent que seja tag autenticado. 
	 *   Se nenhum dos parents for autenticado, simplesmente deixa-se.
	 *   
	 * Para remover um membro, este nao pode ser manager.
	 * Para remover um manager, este tem de ser membro.
	 * 
	 * 
	 * 
	 * TODO
	 * a hierarquia qd um tag nao autenticado herda
	 * 	de um tag autenticado. Como ver se se pode publicar
	 * 	nesses casos, e como ver os grupos que se criam nesses casos.
	 * 	
	 * um manager que tenha pouca reputacao nao pode apagar outros managers.
	 * 
	 * 
	 * 
	 * 
	 */
	
	/**
	 * Adds a manager to a certain authenticated tag.
	 * @param managerId
	 * @param memberId
	 * @param tagId
	 * @return
	 * @throws NoSuchAuthenticatedTagException 
	 * @throws NotAuthorizedException 
	 * @throws TransactionProcessingException 
	 */
	public static void addManager(int managerId, int memberId, int tagId) throws NoSuchAuthenticatedTagException, NotAuthorizedException, TransactionProcessingException {
		Logger.verboseLog("add manager called");
		Logger.debug("AuthTagPersistor addManager with params: managerId=" + managerId + ", memberId=" + memberId + "tagId =" + tagId);
		if(!isAuthenticated(tagId)) throw new NoSuchAuthenticatedTagException();
		if(!isManager(managerId,tagId)) throw new NotAuthorizedException();
		//TODO o mesmo que o manager
		//Se tiver pelo menos um pai autenticado, entao tem de ser membro desse pai.
		if(!authByParents(memberId,tagId)) {
			throw new NotAuthorizedException();
		}
		AuthTagPersistor.addManager(memberId, tagId);
	}
	
	/**
	 * Revokes a manager to a certain authenticated tag.
	 * @param managerId
	 * @param memberId
	 * @param tagId
	 * @return
	 * @throws NoSuchAuthenticatedTagException 
	 * @throws NotAuthorizedException 
	 * @throws TransactionProcessingException 
	 */
	public static void revokeManager(int managerId, int memberId, int tagId) throws NoSuchAuthenticatedTagException, NotAuthorizedException, TransactionProcessingException {
		Logger.verboseLog("revoke manager called");
		Logger.debug("AuthTagPersistor revokeManager with params: managerId=" + managerId + ", memberId=" + memberId + "tagId =" + tagId);
		if(!isAuthenticated(tagId)) throw new NoSuchAuthenticatedTagException();
		if(!isManager(managerId,tagId)) throw new NotAuthorizedException();
		if(managerId == memberId) throw new NotAuthorizedException(Errors.MANAGERCANTREVOKEHIMSELF);
		AuthTagPersistor.revokeManager(memberId, tagId);
	}

	/**
	 * Adds a member to a certain authenticated tag.
	 * @param managerId
	 * @param memberId
	 * @param tagId
	 * @return
	 * @throws NoSuchAuthenticatedTagException 
	 * @throws NotAuthorizedException 
	 * @throws TransactionProcessingException 
	 */
	public static void addMember(int managerId, int memberId, int tagId) throws NoSuchAuthenticatedTagException, NotAuthorizedException, TransactionProcessingException {
		Logger.verboseLog("add member called");
		Logger.debug("AuthTagPersistor addMember with params: managerId=" + managerId + ", memberId=" + memberId + "tagId =" + tagId);
		if(!isAuthenticated(tagId)) throw new NoSuchAuthenticatedTagException();
		if(!isManager(managerId,tagId)) throw new NotAuthorizedException();
		if(isManager(memberId,tagId)) throw new NotAuthorizedException();
		if(!authByParents(memberId,tagId)) {
			throw new NotAuthorizedException();
		}
		AuthTagPersistor.addMember(memberId, tagId);
	}

	/**
	 * Revokes a member to a certain authenticated tag.
	 * @param managerId
	 * @param memberId
	 * @param tagId
	 * @return
	 * @throws NoSuchAuthenticatedTagException 
	 * @throws NotAuthorizedException 
	 * @throws TransactionProcessingException 
	 */
	public static void revokeMember(int managerId, int memberId, int tagId) throws NoSuchAuthenticatedTagException, NotAuthorizedException, TransactionProcessingException {
		Logger.verboseLog("revoke member called");
		Logger.debug("AuthTagPersistor revokeMember with params: managerId=" + managerId + ", memberId=" + memberId + "tagId =" + tagId);
		if(!isAuthenticated(tagId)) throw new NoSuchAuthenticatedTagException();
		if(!isManager(managerId,tagId)) throw new NotAuthorizedException();
		if(isManager(memberId,tagId)) throw new NotAuthorizedException();
		AuthTagPersistor.revokeMember(memberId, tagId);
	}

	/**
	 * Checks if a member is a manager of the tag.
	 * @param memberId
	 * @param tagId
	 * @return
	 * @throws TransactionProcessingException 
	 * @throws NoSuchAuthenticatedTagException 
	 */
	public static boolean isManager(int memberId, int tagId) throws TransactionProcessingException {
		Logger.verboseLog("add manager called");
		Logger.debug("AuthTagPersistor addManager with params: memberId=" + memberId + "tagId =" + tagId);
		if(!isAuthenticated(tagId)) return false;
		return AuthTagPersistor.isManager(memberId, tagId);
	}
	
	/**
	 * Checks if a member is a manager of the tag.
	 * @param memberId
	 * @param tagId
	 * @return
	 * @throws TransactionProcessingException 
	 */
	public static boolean isMember(int memberId, int tagId) throws TransactionProcessingException {
		return AuthTagPersistor.isMember(memberId, tagId);
	}

	/**
	 * Checks if a member has permissions to publish by that tag.
	 * @param memberId
	 * @param tagId
	 * @return
	 * @throws TransactionProcessingException 
	 */
	public static boolean hasPermissions(int memberId, int tagId) throws TransactionProcessingException {
		return AuthTagPersistor.isMember(memberId, tagId);
	}

	public static void createAuthTag(int memberId, int tagId) throws TransactionProcessingException {
		AuthTagPersistor.createAuthTag(memberId, tagId);
	}
	
	public static boolean isAuthenticated(int tagId) throws TransactionProcessingException {
		return AuthTagPersistor.exists(tagId);
	}
	
	private static boolean authByParents(int memberId, int tagId) throws TransactionProcessingException {
		Collection<Integer> authParents = TagPersistor.getAuthenticated(TagPersistor.getParents(tagId));
		boolean needsAuth = !authParents.isEmpty();
		if(needsAuth) {
			for(Integer parent: authParents) {
				if(AuthTagPersistor.isMember(memberId, parent)) return true;
			}
		}
		if(needsAuth) return false;
		return true;
	}

	public static Collection<Member> getManagers(int memberId, int tagId) throws NoSuchAuthenticatedTagException, NotAuthorizedException, TransactionProcessingException {
		Logger.verboseLog("get managers called");
		Logger.debug("AuthTagPersistor getManagers with params: memberId=" + memberId + "tagId =" + tagId);
		if(!isAuthenticated(tagId)) throw new NoSuchAuthenticatedTagException();
		if(!isMember(memberId,tagId)) throw new NotAuthorizedException();
		return AuthTagPersistor.getManagers(tagId);
	}

	public static Collection<Member> getMembers(int memberId, int tagId) throws NoSuchAuthenticatedTagException, NotAuthorizedException, TransactionProcessingException {
		Logger.verboseLog("get managers called");
		Logger.debug("AuthTagPersistor getManagers with params: memberId=" + memberId + "tagId =" + tagId);
		if(!isAuthenticated(tagId)) throw new NoSuchAuthenticatedTagException();
		if(!isMember(memberId,tagId)) throw new NotAuthorizedException();
		return AuthTagPersistor.getMembers(tagId);
	}

	public static Collection<Member> autocompleteManagers(int tagId,
			int memberId, String query) throws NoSuchAuthenticatedTagException, NotAuthorizedException, TransactionProcessingException {
		Logger.verboseLog("get managers called");
		Logger.debug("AuthTagPersistor getManagers with params: memberId=" + memberId + "tagId =" + tagId);
		if(!isAuthenticated(tagId)) throw new NoSuchAuthenticatedTagException();
		if(!isMember(memberId,tagId)) throw new NotAuthorizedException();
		return AuthTagPersistor.autocompleteManagers(tagId, query);
	}

	public static Collection<Member> autocompleteMembers(int tagId,
			int memberId, String query) throws NoSuchAuthenticatedTagException, NotAuthorizedException, TransactionProcessingException {
		Logger.verboseLog("get managers called");
		Logger.debug("AuthTagPersistor getManagers with params: memberId=" + memberId + "tagId =" + tagId);
		if(!isAuthenticated(tagId)) throw new NoSuchAuthenticatedTagException();
		if(!isMember(memberId,tagId)) throw new NotAuthorizedException();
		return AuthTagPersistor.autocompleteMembers(tagId, query);
	}

	public static Collection<Member> autocompleteParentMembers(int tagId,
			int memberId, String query) throws NoSuchAuthenticatedTagException, NotAuthorizedException, TransactionProcessingException {
		Logger.verboseLog("get managers called");
		Logger.debug("AuthTagPersistor getManagers with params: memberId=" + memberId + "tagId =" + tagId);
		if(!isAuthenticated(tagId)) throw new NoSuchAuthenticatedTagException();
		if(!isMember(memberId,tagId)) throw new NotAuthorizedException();
		return AuthTagPersistor.autocompleteParentMembers(tagId, query);
	}
}
