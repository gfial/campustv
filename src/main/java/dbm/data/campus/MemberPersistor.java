package dbm.data.campus;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import manager.data.campus.ChannelManager;
import utils.campus.Logger;
import controller.db.campus.ChannelController;
import controller.db.campus.MemberController;
import data.campus.Channel;
import data.campus.Filter;
import data.campus.Member;
import data.campus.News;
import exceptions.campus.MemberAlreadyExistsException;
import exceptions.campus.NoSuchMemberException;
import exceptions.campus.NoSuchNewsException;
import exceptions.campus.TransactionProcessingException;

public class MemberPersistor {

	public static Member getMember(int memberId) throws TransactionProcessingException {
		return MemberController.getMember(memberId);
	}

	public static int getId(String email) throws NoSuchMemberException, TransactionProcessingException {
		if (!MemberController.existsEmail(email))
			throw new NoSuchMemberException();
		return MemberController.getId(email);
	}

	public static int createMember(Member newMember) throws MemberAlreadyExistsException, TransactionProcessingException {
		Logger.verboseLog("MemberPersistor createMember called");
		int id = MemberController.getId(newMember.getEmail());
		if(id > 0) throw new MemberAlreadyExistsException();
		int memberId = MemberController.createMember(newMember);
		Logger.debug("Created member with id:" + memberId);
		Channel smartTv = new Channel(-1,ChannelManager.SMARTTV,memberId, new HashSet<Filter>(), ChannelManager.SMARTFILTER, true);
		int smarTvId = ChannelController.createChannel(smartTv);
		MemberController.setSmartTv(memberId,smarTvId);
		return memberId;
	}

	public static Member editMember(int id, Member member) throws TransactionProcessingException {
		Logger.verboseLog("MemberPersistor editMember called");
		MemberController.editMember(id, member);
		return member;
	}

	public static Collection<Member> searchMembers(String query, int begin, int offset) throws TransactionProcessingException {
		return MemberController.searchMembers(query, begin, offset);
	}

	public static void voteDown(int authorId) throws TransactionProcessingException {
		MemberController.updatePoints(authorId, -1);
	}

	public static void voteUp(int authorId) throws TransactionProcessingException {
		MemberController.updatePoints(authorId, 1);
	}

	public static void voteDown(int authorId, int newsId) throws TransactionProcessingException {
		try {
			News n = NewsPersistor.getVotedContent(authorId, newsId).getNews();
			int repWeight = n.getReportWeight();
			int vote = repWeight / (int) Math.sqrt(n.getLikeWeight() + 1);
			MemberController.updatePoints(authorId, -vote);
		} catch (NoSuchNewsException e) {
			Logger.verboseLog(e.getLocalizedMessage());
		}
	}

	public static Collection<Member> autocomplete(String query) throws TransactionProcessingException {
		return MemberController.autocomplete(query);
	}

	public static Collection<Member> getMembers(Collection<Integer> memberIds) throws TransactionProcessingException {
		Collection<Member> members = new LinkedList<Member>();
		for(Integer id: memberIds) {
			members.add(getMember(id));
		}
		return members;
	}

	public static Collection<Member> getMinimalMembers(
			Collection<Integer> parentMembers) throws TransactionProcessingException {
		Collection<Member> members = new LinkedList<Member>();
		for(Integer id: parentMembers) {
			members.add(MemberController.getMinimalMember(id));
		}
		return members;
	}
}
