package com.campus.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import manager.data.campus.NewsManager;
import manager.data.campus.SessionManager;
import utils.campus.HttpUtils;
import utils.campus.Logger;
import utils.campus.ResponseUtils;
import utils.exceptions.campus.Errors;
import data.campus.News;
import data.campus.SimpleResponse;
import data.campus.VotedContent;
import exceptions.campus.InvalidSessionException;
import exceptions.campus.NoSuchNewsException;
import exceptions.campus.NoSuchTagException;
import exceptions.campus.NoTagsException;
import exceptions.campus.NotAuthorizedException;
import exceptions.campus.TransactionProcessingException;

@Path("/news")
public class NewsApi {

	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createNews(@Context HttpHeaders headers, News n) {
		try {
			int memberId = SessionManager.getId(headers);
			VotedContent content = NewsManager.createNews(memberId, n);
			return Response.status(HttpUtils.HTTPOK).entity(content).build();
		} catch (InvalidSessionException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
					Responses.NOSESSION);
		} catch (TransactionProcessingException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
					Responses.TRANSACTIONFAILED);
		} catch (NoSuchNewsException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
					Responses.TRANSACTIONFAILED);
		} catch (NotAuthorizedException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
					Responses.NOTALLOWED);
		} catch (NoTagsException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
					Responses.NOTAGS);
		} catch (NoSuchTagException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.NOTFOUND, Responses.NOSUCHTAG);
		}
	}

	@POST
	@Path("/{id}/edit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response editNews(@Context HttpHeaders headers,
			@PathParam("id") int newsId, News n) throws TransactionProcessingException {
		Logger.verboseLog("edit news getting member id");
		try {
			int memberId = SessionManager.getId(headers);
			VotedContent content = NewsManager.editNews(memberId, newsId, n);
			Logger.debug("edit news " + content.toString());
			return Response.status(HttpUtils.HTTPOK).entity(content).build();
		} catch (InvalidSessionException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
					Responses.NOSESSION);
		} catch (NotAuthorizedException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
					Responses.NOTALLOWED);
		} catch (NoSuchNewsException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
					Responses.NOSUCHNEWS);
		} catch (NoTagsException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
					Responses.NOTAGS);
		}

	}

	@DELETE
	@Path("/{id}/delete")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteNews(@Context HttpHeaders headers,
			@PathParam("id") int newsId) throws TransactionProcessingException {
		try {
			int memberId = SessionManager.getId(headers);
			SimpleResponse response = NewsManager.deleteNews(memberId, newsId);
			return Response.status(HttpUtils.HTTPOK).entity(response).build();
		} catch (InvalidSessionException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
					Responses.NOSESSION);
		} catch (NotAuthorizedException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
					Errors.NOTALLOWED);
		}
	}

	@GET
	@Path("/{id}/get")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNews(@Context HttpHeaders headers,
			@PathParam("id") int newsId) throws TransactionProcessingException {
		VotedContent content = null;
		try {
			int memberId = SessionManager.getId(headers);
			content = NewsManager.getVotedContent(memberId, newsId);
		} catch (InvalidSessionException e) {
			Logger.verboseLog("get news without session");
			try {
				content = NewsManager.getVotedContent(-1, newsId);
			} catch (NoSuchNewsException e1) {
				Logger.verboseLog(e.getLocalizedMessage());
				return ResponseUtils.getResponse(HttpUtils.NOTFOUND,
						Responses.NOSUCHNEWS);
			}
		} catch (NoSuchNewsException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.NOTFOUND,
					Responses.NOSUCHNEWS);
		}
		if (content == null) {
			Logger.verboseLog("get news not found");
			return Response.status(HttpUtils.NOTFOUND).build();
		}
		return Response.status(HttpUtils.HTTPOK).entity(content).build();
	}

	@POST
	@Path("/{id}/vote")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response vote(@Context HttpHeaders headers,
			@PathParam("id") int newsId, VotedContent vote) throws TransactionProcessingException {
		try {
			int memberId = SessionManager.getId(headers);
			VotedContent content = NewsManager.vote(memberId, newsId, vote);
			return Response.status(HttpUtils.HTTPOK).entity(content).build();
		} catch (InvalidSessionException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
					Responses.NOSESSION);
		} catch (NoSuchNewsException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
					Responses.NOSUCHNEWS);
		}
	}

	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public Response search(@Context HttpHeaders headers,
			@QueryParam("query") String query,
			@DefaultValue("0") @QueryParam("beg") int begin, @DefaultValue("15") @QueryParam("offset") int offset) throws TransactionProcessingException {
		try {
			int memberId = SessionManager.getId(headers);
			// Searches for contents given the member id.
			return Response.status(HttpUtils.HTTPOK).entity(NewsManager.search(memberId,
					query, begin, offset)).build();
		} catch (InvalidSessionException e) {
			// Searches for contents without the member id.
			return Response.status(HttpUtils.HTTPOK).entity(NewsManager.search(-1,
					query, begin, offset)).build();
		}

	}

}
