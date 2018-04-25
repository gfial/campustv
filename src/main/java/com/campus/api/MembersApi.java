package com.campus.api;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import lang.campus.Responses;
import manager.data.campus.MemberManager;
import manager.data.campus.NewsManager;
import manager.data.campus.SessionManager;
import utils.campus.HttpUtils;
import utils.campus.Logger;
import utils.campus.ResponseUtils;
import utils.exceptions.campus.Errors;
import data.campus.Credentials;
import data.campus.Member;
import data.campus.Tag;
import data.campus.VotedContent;
import exceptions.campus.InvalidSessionException;
import exceptions.campus.MemberAlreadyExistsException;
import exceptions.campus.NoSuchMemberException;
import exceptions.campus.TransactionProcessingException;

@Path("/member")
public class MembersApi {

	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createMember(Credentials creds) {
		Logger.verboseLog("create member");

		String email = creds.getEmail();
		String username = creds.getUsername();
		String password = creds.getPassword();
		String gender = "M";
		String imgPath = creds.getImgPath() == null || creds.getImgPath().isEmpty() ? "http://www.picturesnew.com/media/images/genius-photo.png" : creds.getImgPath();

		Logger.debug(email);
		Logger.debug(username);
		Logger.debug(password);
		Logger.debug(imgPath);
		Logger.debug(gender);
		Member member;

		if (!email.contains("@")) {
			Logger.debug(Errors.INVALIDQUERYPARAMS);
			return Response.status(HttpUtils.FORBIDDEN)
					.entity(Responses.INVALIDEMAIL).build();
		}

		try {
			try {
				MemberManager.getId(email);
				Logger.debug(Errors.MEMBERALREADYEXISTS);
				return Response.status(HttpUtils.FORBIDDEN)
						.entity(Responses.MEMBEREMAILALREADYEXISTS).build();
			} catch (NoSuchMemberException e) {
				// Ok there is no member with the same email.
			}

			member = MemberManager.createMember(email, username, password,
					imgPath, gender);
			Logger.debug(member.toString());
			return Response.status(HttpUtils.HTTPOK).entity(member).build();
		} catch (TransactionProcessingException e) {
			Logger.log(e.getLocalizedMessage());
			return Response.status(HttpUtils.FORBIDDEN)
					.entity(Responses.TRANSACTIONFAILED).build();
		} catch (MemberAlreadyExistsException e) {
			Logger.log(e.getLocalizedMessage());
			return Response.status(HttpUtils.FORBIDDEN)
					.entity(Responses.MEMBERALREADYEXISTS).build();
		}
	}

	@POST
	@Path("/edit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response editMember(@Context HttpHeaders headers, Member member) throws TransactionProcessingException {
		Logger.verboseLog("edit member called");
		try {
			int id = SessionManager.getId(headers);
			Logger.debug("edit member id is " + id);
			if (member != null)
				Logger.debug(member.toString());
			Member editedMember = MemberManager.editMember(id, member);
			Logger.debug("editMember called MemberManager editMember with params: id: "
					+ id + " member: " + member.toString());

			return Response.status(HttpUtils.HTTPOK).entity(editedMember)
					.build();
		} catch (InvalidSessionException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.noValidSession();
		}
	}

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(Credentials creds) throws TransactionProcessingException {
		String email = creds.getEmail();
		String password = creds.getPassword();

		int id;
		try {
			id = MemberManager.getId(email);
		} catch (NoSuchMemberException e) {
			Logger.verboseLog(Errors.NOSUCHMEMBER + " " + email);
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
					Errors.WRONGCREDENTIALS);
		}
		Logger.verboseLog("login getting the id");
		Logger.debug("MemberManager getId called with params: email: " + email);

		if (!SessionManager.checkUser(id, password)) {
			Logger.verboseLog(Responses.WRONGCREDENTIALS);
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
					Responses.WRONGCREDENTIALS);
		}

		Member member = MemberManager.getMember(id);
		Logger.verboseLog("login gettng member");
		Logger.verboseLog("MemberManager getMember called with params: id: "
				+ id);

		return Response.status(HttpUtils.HTTPOK).entity(member)
				.cookie(SessionManager.createCookie(member)).build();
	}

	@POST
	@Path("/logout")
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout(@Context HttpHeaders headers) throws TransactionProcessingException {
		Logger.verboseLog("logout called");
		try {
			SessionManager.endSession(headers);
		} catch (InvalidSessionException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.noValidSession();
		}
		Logger.verboseLog("SesionManager endSession  called");

		return ResponseUtils.getResponse(HttpUtils.HTTPOK, Responses.LOGOUT);
	}

	@GET
	@Path("/{id}/get")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMember(@PathParam("id") int memberId) throws TransactionProcessingException {
		Logger.verboseLog("getMember called");
		Member member = MemberManager.getMember(memberId);
		Logger.verboseLog("MemberManager getMember getting member");
		Logger.debug("MemberManager getMember called with params: memberId: "
				+ memberId);
		return Response.status(HttpUtils.HTTPOK).entity(member).build();
	}

	@GET
	@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMember(@Context HttpHeaders headers) throws TransactionProcessingException {
		Logger.verboseLog("getMember called");

		Logger.verboseLog("editMember getting SessionManager id");
		int memberId;
		try {
			memberId = SessionManager.getId(headers);
			Member member = MemberManager.getMember(memberId);
			Logger.verboseLog("MemberManager getMember called");
			Logger.debug("MemberManager getMember called with params: memberId: "
					+ memberId);

			return Response.status(HttpUtils.HTTPOK).entity(member).build();
		} catch (InvalidSessionException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.noValidSession();
		}
	}

	@GET
	@Path("/{id}/memberGroups")
	@Produces(MediaType.APPLICATION_JSON)
	public Response memberGroups(@PathParam("id") int memberId) throws TransactionProcessingException {
		Collection<Tag> memberGroups = MemberManager.memberGroups(memberId);
		return Response.status(HttpUtils.HTTPOK).entity(memberGroups).build();
	}

	@GET
	@Path("/{id}/managedGroups")
	@Produces(MediaType.APPLICATION_JSON)
	public Response managedGroups(@PathParam("id") int memberId) throws TransactionProcessingException {
		Collection<Tag> managedGroups = MemberManager.managedGroups(memberId);
		return Response.status(HttpUtils.HTTPOK).entity(managedGroups).build();
	}
	
	@GET
	@Path("/{id}/groups/autocomplete")
	@Produces(MediaType.APPLICATION_JSON)
	public Response autocompleteManagedGroups(@Context HttpHeaders headers, @QueryParam("term") String term) throws TransactionProcessingException {
		int memberId;
		try {
			memberId = SessionManager.getId(headers);
			Collection<Tag> managedGroups = MemberManager.autocompleteManagedGroups(memberId, term);
			return Response.status(HttpUtils.HTTPOK).entity(managedGroups).build();
		} catch (InvalidSessionException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.noValidSession();
		}	
	}
		

	@GET
	@Path("/{id}/posts")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPosts(@Context HttpHeaders headers,
			@PathParam("id") int authorId,
			@DefaultValue("0") @QueryParam("beg") int begin,
			@DefaultValue("15") @QueryParam("offset") int offset) throws TransactionProcessingException {
		try {
			int memberId = SessionManager.getId(headers);
			Collection<VotedContent> contents = NewsManager.getContentsBy(
					authorId, memberId, begin, offset);
			for (VotedContent c : contents) {
				Logger.debug(c.toString());
			}
			return Response.status(HttpUtils.HTTPOK).entity(contents).build();
		} catch (InvalidSessionException e) {
			Collection<VotedContent> contents = NewsManager.getContentsBy(
					authorId, -1, begin, offset);
			for (VotedContent c : contents) {
				Logger.debug(c.toString());
			}
			return Response.status(HttpUtils.HTTPOK).entity(contents).build();
		}
	}

	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchMember(@QueryParam("query") String query,
			@DefaultValue("0") @QueryParam("beg") int begin,
			@DefaultValue("15") @QueryParam("offset") int offset) throws TransactionProcessingException {
		Collection<Member> members = MemberManager.searchMembers(query, begin,
				offset);
		return Response.status(HttpUtils.HTTPOK).entity(members).build();
	}

	@GET
	@Path("/autocomplete")
	@Produces(MediaType.APPLICATION_JSON)
	public Response autocomplete(@QueryParam("query") String query) throws TransactionProcessingException {
		Collection<Member> members = MemberManager.autocomplete(query);
		return Response.status(HttpUtils.HTTPOK).entity(members).build();
	}
}
