package utils.campus;

import javax.ws.rs.core.Response;

import lang.campus.Responses;
import data.campus.SimpleResponse;

public class ResponseUtils {
	
	public static SimpleResponse getMessage(String message) {
		return new SimpleResponse(message);
	}
	
	public static Response getResponse(int httpStatus, String message) {
		return Response.status(httpStatus).entity(getMessage(message)).build();
	}
	
	public static Response invalidQueryParams() {
		return Response
				.status(HttpUtils.FORBIDDEN)
				.entity(ResponseUtils
						.getMessage(Responses.INVALIDQUERYPARAMS)).build();
	}

	public static Response noValidSession() {
		return Response.status(HttpUtils.FORBIDDEN)
				.entity(ResponseUtils.getMessage(Responses.NOSESSION))
				.build();
	}
	
}
