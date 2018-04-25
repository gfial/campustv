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
import manager.data.campus.ChannelManager;
import manager.data.campus.SessionManager;
import utils.campus.HttpUtils;
import utils.campus.Logger;
import utils.campus.ResponseUtils;
import data.campus.Channel;
import data.campus.VotedContent;
import exceptions.campus.CantEditSmartTvException;
import exceptions.campus.InvalidSessionException;
import exceptions.campus.NoSuchChannelException;
import exceptions.campus.NoSuchTagException;
import exceptions.campus.NotAuthorizedException;
import exceptions.campus.TransactionProcessingException;

@Path("/channel")
public class ChannelApi {

	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createChannel(@Context HttpHeaders headers,
			Channel newChannel) throws TransactionProcessingException {

		try {
			int memberId = SessionManager.getId(headers);

			try {
				Logger.log(newChannel.toString());
				Channel channel = ChannelManager.createChannel(memberId,
						newChannel);
				Logger.debug("create channel: \n" + channel.toString());
				return Response.status(HttpUtils.HTTPOK).entity(channel)
						.build();
			} catch (NoSuchChannelException e) {
				Logger.verboseLog(e.getLocalizedMessage());
				return ResponseUtils.getResponse(HttpUtils.NOTFOUND,
						Responses.NOSUCHCHANNEL);
			} catch (NoSuchTagException e) {
				Logger.verboseLog(e.getLocalizedMessage());
				return ResponseUtils.getResponse(HttpUtils.NOTFOUND,
						Responses.NOSUCHTAG);
			}
		} catch (InvalidSessionException e1) {
			Logger.verboseLog(e1.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
					Responses.NOSESSION);
		}
	}

	@POST
	@Path("/{id}/edit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response editchannel(@Context HttpHeaders headers,
			@PathParam("id") int channelId, Channel channel)
			throws TransactionProcessingException {

		try {
			int memberId = SessionManager.getId(headers);

			try {
				Channel editedChannel = ChannelManager.editChannel(memberId,
						channelId, channel);
				return Response.status(HttpUtils.HTTPOK).entity(editedChannel)
						.build();
			} catch (NoSuchChannelException e) {
				Logger.verboseLog(e.getLocalizedMessage());
				return ResponseUtils.getResponse(HttpUtils.NOTFOUND,
						Responses.NOSUCHCHANNEL);
			} catch (NotAuthorizedException e) {
				Logger.verboseLog(e.getLocalizedMessage());
				return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
						Responses.NOTALLOWED);
			} catch (CantEditSmartTvException e) {
				Logger.verboseLog(e.getLocalizedMessage());
				return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
						Responses.CANTEDITSMARTTV);
			} catch (NoSuchTagException e) {
				Logger.verboseLog(e.getLocalizedMessage());
				return ResponseUtils.getResponse(HttpUtils.NOTFOUND,
						Responses.NOSUCHTAG);
			}
		} catch (InvalidSessionException e1) {
			Logger.verboseLog(e1.getLocalizedMessage());
			return ResponseUtils.noValidSession();
		}
	}

	@POST
	@Path("/{id}/delete")
	@Produces(MediaType.APPLICATION_JSON)
	public Response editchannel(@Context HttpHeaders headers,
			@PathParam("id") int channelId)
			throws TransactionProcessingException, NotAuthorizedException, NoSuchChannelException {

		try {
			int memberId = SessionManager.getId(headers);

			ChannelManager.deleteChannel(memberId, channelId);
			return Response.status(HttpUtils.HTTPOK)
					.entity(Responses.CHANNELDELETED).build();

		} catch (InvalidSessionException e1) {
			Logger.verboseLog(e1.getLocalizedMessage());
			return ResponseUtils.noValidSession();
		} catch (CantEditSmartTvException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
					Responses.CANTEDITSMARTTV);
		}
	}

	@GET
	@Path("/{id}/get")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getChannel(@PathParam("id") int channelId)
			throws TransactionProcessingException {
		try {
			Channel channel = ChannelManager.getChannel(channelId);
			return Response.status(HttpUtils.HTTPOK).entity(channel).build();
		} catch (NoSuchChannelException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
					Responses.NOSUCHCHANNEL);
		}
	}

	@GET
	@Path("/{id}/fill")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fillChannel(@Context HttpHeaders headers,
			@PathParam("id") int channelId,
			@DefaultValue("0") @QueryParam("begin") int begin,
			@DefaultValue("15") @QueryParam("offset") int offset)
			throws TransactionProcessingException {

		try {
			int memberId = SessionManager.getId(headers);
			try {
				Channel channel = ChannelManager.getChannel(channelId);
				Collection<VotedContent> news = ChannelManager.getNews(
						memberId, channel, begin, offset);
				return Response.status(HttpUtils.HTTPOK).entity(news).build();
			} catch (NoSuchChannelException e) {
				Logger.verboseLog(e.getLocalizedMessage());
				return ResponseUtils.getResponse(HttpUtils.NOTFOUND,
						Responses.NOSUCHCHANNEL);
			}
		} catch (InvalidSessionException e) {
			try {
				Channel channel = ChannelManager.getChannel(channelId);
				Collection<VotedContent> news = ChannelManager.getNews(-1,
						channel, begin, offset);
				return Response.status(HttpUtils.HTTPOK).entity(news).build();
			} catch (NoSuchChannelException e1) {
				Logger.verboseLog(e1.getLocalizedMessage());
				return ResponseUtils.getResponse(HttpUtils.NOTFOUND,
						Responses.NOSUCHCHANNEL);
			}
		}
	}

	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchChannel(@QueryParam("query") String name,
			@DefaultValue("0") @QueryParam("beg") int begin,
			@DefaultValue("15") @QueryParam("offset") int offset)
			throws TransactionProcessingException {
		Collection<Channel> channels = ChannelManager.search(name, begin,
				offset);

		return Response.status(HttpUtils.HTTPOK).entity(channels).build();
	}
}
