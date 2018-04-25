package com.campus.api;

import java.util.Collection;

import javax.ws.rs.Consumes;
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
import manager.data.campus.AuthTagManager;
import manager.data.campus.SessionManager;
import utils.campus.HttpUtils;
import utils.campus.Logger;
import utils.campus.ResponseUtils;
import data.campus.Member;
import exceptions.campus.InvalidSessionException;
import exceptions.campus.NoSuchAuthenticatedTagException;
import exceptions.campus.NotAuthorizedException;
import exceptions.campus.TransactionProcessingException;

@Path("/tag/auth")
public class AuthTagApi {

	@POST
	@Path("/{id}/manager/add/{member_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addManager(@Context HttpHeaders headers,
			@PathParam("id") int tagId, @PathParam("member_id") int memberId) throws TransactionProcessingException {
		try {
			int managerId = SessionManager.getId(headers);
			Logger.verboseLog("add manager Adding new manager");
			AuthTagManager.addManager(managerId, memberId, tagId);
		} catch (InvalidSessionException e) {
			Logger.verboseLog(Responses.NOSESSION);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.noValidSession();
		} catch (NoSuchAuthenticatedTagException e) {
			Logger.verboseLog(Responses.NOSUCHAUTHTAG);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN, Responses.NOSUCHAUTHTAG);
		} catch (NotAuthorizedException e) {
			Logger.verboseLog(Responses.NOTALLOWED);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN, Responses.NOSUCHMANAGER);
		}

		return ResponseUtils.getResponse(HttpUtils.HTTPOK,
				Responses.MANAGERADDED);
	}

	@POST
	@Path("/{id}/manager/revoke/{member_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response revokeManager(@Context HttpHeaders headers,
			@PathParam("id") int tagId, @PathParam("member_id") int memberId) throws TransactionProcessingException {
		try {
			int managerId = SessionManager.getId(headers);
			Logger.verboseLog("revoke manager Adding new manager");
			AuthTagManager.revokeManager(managerId, memberId, tagId);
		} catch (InvalidSessionException e) {
			Logger.verboseLog(Responses.NOSESSION);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.noValidSession();
		} catch (NoSuchAuthenticatedTagException e) {
			Logger.verboseLog(Responses.NOSUCHAUTHTAG);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN, Responses.NOSUCHAUTHTAG);
		} catch (NotAuthorizedException e) {
			Logger.verboseLog(Responses.NOTALLOWED);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN, Responses.NOSUCHMANAGER);
		}

		return ResponseUtils.getResponse(HttpUtils.HTTPOK,
				Responses.MANAGERREVOKED);
	}

	@POST
	@Path("/{id}/member/add/{member_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addMember(@Context HttpHeaders headers,
			@PathParam("id") int tagId, @PathParam("member_id") int memberId) throws TransactionProcessingException {
		try {
			int managerId = SessionManager.getId(headers);
			Logger.verboseLog("add member Adding new manager");
			AuthTagManager.addMember(managerId, memberId, tagId);
		} catch (InvalidSessionException e) {
			Logger.verboseLog(Responses.NOSESSION);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.noValidSession();
		} catch (NoSuchAuthenticatedTagException e) {
			Logger.verboseLog(Responses.NOSUCHAUTHTAG);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN, Responses.NOSUCHAUTHTAG);
		} catch (NotAuthorizedException e) {
			Logger.verboseLog(Responses.NOTALLOWED);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN, Responses.NOSUCHMANAGER);
		}

		return ResponseUtils.getResponse(HttpUtils.HTTPOK,
				Responses.MEMBERADDED);
	}

	@POST
	@Path("/{id}/member/revoke/{member_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response revokeMember(@Context HttpHeaders headers,
			@PathParam("id") int tagId, @PathParam("member_id") int memberId) throws TransactionProcessingException {
		try {
			int managerId = SessionManager.getId(headers);
			Logger.verboseLog("revokeManager revoking the manager");
			AuthTagManager.revokeMember(managerId, memberId, tagId);
		} catch (InvalidSessionException e) {
			Logger.verboseLog(Responses.NOSESSION);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.noValidSession();
		} catch (NoSuchAuthenticatedTagException e) {
			Logger.verboseLog(Responses.NOSUCHAUTHTAG);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN, Responses.NOSUCHAUTHTAG);
		} catch (NotAuthorizedException e) {
			Logger.verboseLog(Responses.NOTALLOWED);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN, Responses.NOSUCHMANAGER);
		}

		return ResponseUtils.getResponse(HttpUtils.HTTPOK,
				Responses.MEMBERREVOKED);
	}
	
	@GET
	@Path("/{id}/member/list")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMembers(@Context HttpHeaders headers,
			@PathParam("id") int tagId) throws TransactionProcessingException {
		try {
			int memberId = SessionManager.getId(headers);
			Collection<Member> members = AuthTagManager.getMembers(memberId, tagId);
			return Response.status(HttpUtils.HTTPOK).entity(members).build();
		} catch (InvalidSessionException e) {
			Logger.verboseLog(Responses.NOSESSION);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.noValidSession();
		} catch (NoSuchAuthenticatedTagException e) {
			Logger.verboseLog(Responses.NOSUCHAUTHTAG);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN, Responses.NOSUCHAUTHTAG);
		} catch (NotAuthorizedException e) {
			Logger.verboseLog(Responses.NOTALLOWED);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN, Responses.NOSUCHMANAGER);
		}
	}
	
	@GET
	@Path("/{id}/manager/list")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getManagers(@Context HttpHeaders headers,
			@PathParam("id") int tagId) throws TransactionProcessingException {
		try {
			int memberId = SessionManager.getId(headers);
			Collection<Member> managers = AuthTagManager.getManagers(memberId, tagId);
			return Response.status(HttpUtils.HTTPOK).entity(managers).build();
		} catch (InvalidSessionException e) {
			Logger.verboseLog(Responses.NOSESSION);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.noValidSession();
		} catch (NoSuchAuthenticatedTagException e) {
			Logger.verboseLog(Responses.NOSUCHAUTHTAG);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN, Responses.NOSUCHAUTHTAG);
		} catch (NotAuthorizedException e) {
			Logger.verboseLog(Responses.NOTALLOWED);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN, Responses.NOSUCHMANAGER);
		}
	}
	
	@GET
	@Path("/{id}/manager/autocomplete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response autocompleteManagers(@Context HttpHeaders headers, @PathParam("id") int tagId,
			@QueryParam("term") String query) throws TransactionProcessingException {
		try {
			int memberId = SessionManager.getId(headers);
			Collection<Member> managers = AuthTagManager.autocompleteManagers(tagId, memberId, query);
			return Response.status(HttpUtils.HTTPOK).entity(managers).build();
		} catch (InvalidSessionException e) {
			Logger.verboseLog(Responses.NOSESSION);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.noValidSession();
		} catch (NoSuchAuthenticatedTagException e) {
			Logger.verboseLog(Responses.NOSUCHAUTHTAG);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN, Responses.NOSUCHAUTHTAG);
		} catch (NotAuthorizedException e) {
			Logger.verboseLog(Responses.NOTALLOWED);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN, Responses.NOSUCHMANAGER);
		}
	}
	
	@GET
	@Path("/{id}/member/autocomplete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response autocompleteMembers(@Context HttpHeaders headers, @PathParam("id") int tagId,
			@QueryParam("term") String query) throws TransactionProcessingException {
		try {
			int memberId = SessionManager.getId(headers);
			Collection<Member> managers = AuthTagManager.autocompleteMembers(tagId, memberId, query);
			return Response.status(HttpUtils.HTTPOK).entity(managers).build();
		} catch (InvalidSessionException e) {
			Logger.verboseLog(Responses.NOSESSION);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.noValidSession();
		} catch (NoSuchAuthenticatedTagException e) {
			Logger.verboseLog(Responses.NOSUCHAUTHTAG);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN, Responses.NOSUCHAUTHTAG);
		} catch (NotAuthorizedException e) {
			Logger.verboseLog(Responses.NOTALLOWED);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN, Responses.NOSUCHMANAGER);
		}
	}
	
	@GET
	@Path("/{id}/parentMember/autocomplete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response autocompleteParentMembers(@Context HttpHeaders headers, @PathParam("id") int tagId,
			@QueryParam("term") String query) throws TransactionProcessingException {
		try {
			int memberId = SessionManager.getId(headers);
			//Gets the members authenticated by at least one tag of the parent tags of the given tag.
			Collection<Member> managers = AuthTagManager.autocompleteParentMembers(tagId, memberId, query);
			return Response.status(HttpUtils.HTTPOK).entity(managers).build();
		} catch (InvalidSessionException e) {
			Logger.verboseLog(Responses.NOSESSION);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.noValidSession();
		} catch (NoSuchAuthenticatedTagException e) {
			Logger.verboseLog(Responses.NOSUCHAUTHTAG);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN, Responses.NOSUCHAUTHTAG);
		} catch (NotAuthorizedException e) {
			Logger.verboseLog(Responses.NOTALLOWED);
			Logger.debug(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN, Responses.NOSUCHMANAGER);
		}
	}
}
