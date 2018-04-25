package com.campus.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import lang.campus.Responses;
import manager.data.campus.SessionManager;
import utils.campus.HttpUtils;
import utils.campus.Logger;
import utils.campus.ResponseUtils;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import exceptions.campus.InvalidSessionException;

@Path("/multimedia")
public class MultimediaApi {

	public static final String CDNPATH = "./src/main/webapp/amazon_s3/";

	@POST
	@Path("/get")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getImage(@Context HttpHeaders headers,
			@DefaultValue("") @QueryParam("query") String imageUrl) {

		if (!imageUrl.endsWith(".gif") && !imageUrl.endsWith(".png")
				&& !imageUrl.endsWith(".jpg")) {
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
					Responses.NOTAVALIDIMAGE);
		}

		try {
			SessionManager.getId(headers);
		} catch (InvalidSessionException e) {
			return ResponseUtils.noValidSession();
		}

		String filename = getFilename(imageUrl);

		String uploadedFileLocation = CDNPATH + filename;
		
		URL url;
		try {
			url = new URL(imageUrl);
			// save it
			writeToFile(url.openStream(), uploadedFileLocation);
		} catch (MalformedURLException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.NOTFOUND, Responses.INVALIDIMGURL);
		} catch (IOException e) {
			Logger.verboseLog(e.getLocalizedMessage());
			return ResponseUtils.getResponse(HttpUtils.NOTFOUND, Responses.TRANSACTIONFAILED);
		}
		
		

		String output = uploadedFileLocation;

		return ResponseUtils.getResponse(HttpUtils.HTTPOK, output);

	}

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadImage(@Context HttpHeaders headers,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {

		if (!fileDetail.getFileName().endsWith(".gif")
				&& !fileDetail.getFileName().endsWith(".png")
				&& !fileDetail.getFileName().endsWith(".jpg")) {
			return ResponseUtils.getResponse(HttpUtils.FORBIDDEN,
					Responses.NOTAVALIDIMAGE);
		}

		try {
			SessionManager.getId(headers);
		} catch (InvalidSessionException e) {
			return ResponseUtils.noValidSession();
		}

		String filename = getFilename(fileDetail.getFileName());

		String uploadedFileLocation = CDNPATH + filename;

		// save it
		writeToFile(uploadedInputStream, uploadedFileLocation);

		String output = uploadedFileLocation;

		return ResponseUtils.getResponse(HttpUtils.HTTPOK, output);

	}

	private String getFilename(String filename) {
		String suffix = filename.substring(filename.length() - 4);
		return UUID.randomUUID().toString() + suffix;
	}

	// save uploaded file to new location
	private void writeToFile(InputStream uploadedInputStream,
			String uploadedFileLocation) {

		try {
			OutputStream out = new FileOutputStream(new File(
					uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}
}
