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
import manager.data.campus.NewsManager;
import manager.data.campus.SessionManager;
import manager.data.campus.TagManager;
import utils.campus.HttpUtils;
import utils.campus.Logger;
import utils.campus.ResponseUtils;
import data.campus.Tag;
import data.campus.VotedContent;
import exceptions.campus.InvalidSessionException;
import exceptions.campus.NoSuchTagException;
import exceptions.campus.NotAuthorizedException;
import exceptions.campus.TransactionProcessingException;

@Path("/tag")
public class TagApi {

	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createTag(@Context HttpHeaders headers, Tag newTag) throws TransactionProcessingException {
		System.out.println(newTag.toString());
		try {
			int memberId = SessionManager.getId(headers);
			Tag tag = TagManager.createTag(memberId, newTag);
			if (tag == null) {
				return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
						Responses.FAILEDCREATETAG);
			}
			
			return Response.status(HttpUtils.HTTPOK).entity(tag).build();
		} catch (InvalidSessionException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
					Responses.NOSESSION);
		} catch (NotAuthorizedException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
					Responses.NOTALLOWED);
		} catch (NoSuchTagException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.NOTFOUND,
					Responses.NOSUCHTAG);
		}

	}

	@POST
	@Path("/{id}/edit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response editTag(@Context HttpHeaders headers,
			@PathParam("id") int id, Tag tag) throws TransactionProcessingException {
		
		try {
			int memberId = SessionManager.getId(headers);
			Tag editedTag = TagManager.editTag(memberId, id, tag);

			return Response.status(HttpUtils.HTTPOK).entity(editedTag).build();
		} catch (InvalidSessionException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
					Responses.NOSESSION);
		} catch (NotAuthorizedException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
					Responses.NOTALLOWED);
		}

	}

	@GET
	@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTagByName(@QueryParam("query") String name) {
		int tagId;
		try {
			tagId = TagManager.getTagId(name);
			Logger.verboseLog(name);
			Tag tag = TagManager.getTag(tagId);
			Logger.verboseLog(tag.toString());
			return Response.status(HttpUtils.HTTPOK).entity(tag).build();
		} catch (TransactionProcessingException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.NOTFOUND,
					Responses.NOSUCHTAG);
		}
	}

	@GET
	@Path("/{id}/get")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTag(@PathParam("id") int id) throws TransactionProcessingException {
		Tag tag = TagManager.getTag(id);

		return Response.status(HttpUtils.HTTPOK).entity(tag).build();
	}
	
	@GET
	@Path("/{id}/search")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchTagNews(@Context HttpHeaders headers, @DefaultValue("0") @QueryParam("beg") int begin, @DefaultValue("15") @QueryParam("offset") int offset, @PathParam("id") int id) throws TransactionProcessingException {
		
		int memberId = -1;
		try {
			memberId = SessionManager.getId(headers);
		} catch (InvalidSessionException e) {
		}
		
		Collection<VotedContent> news = NewsManager.searchTagNews(memberId,id, begin, offset);

		return Response.status(HttpUtils.HTTPOK).entity(news).build();
	}

	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchTag(@DefaultValue("0") @QueryParam("beg") int begin, @DefaultValue("15") @QueryParam("offset") int offset, @QueryParam("query") String keyword) throws TransactionProcessingException {
		Collection<Tag> searchResults = TagManager.search(keyword, begin, offset);
		return Response.status(HttpUtils.HTTPOK).entity(searchResults).build();
	}
	
	@GET
	@Path("/autocomplete")
	@Produces(MediaType.APPLICATION_JSON)
	public Response autoComplete(@QueryParam("term") String keyword) throws TransactionProcessingException {
		String [] keywords = keyword.split(",");
		keyword = keywords[keywords.length-1].trim();
		Collection<Tag> searchResults = TagManager.autoComplete(keyword);
		return Response.status(HttpUtils.HTTPOK).entity(searchResults).build();
	}

}
