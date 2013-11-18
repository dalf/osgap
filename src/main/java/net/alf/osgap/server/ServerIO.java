package net.alf.osgap.server;

import java.io.InputStream;

import net.alf.osgap.server.NanoHTTPD.Response;
import net.alf.osgap.server.NanoHTTPD.Response.Status;
import net.alf.osgap.tools.Json;
import net.alf.osgap.tools.Os;

public class ServerIO {
	
    public static final String MIME_PLAINTEXT = "text/plain";
    public static final String MIME_HTML = "text/html";
	public static final String MIME_JAVASCRIPT = "text/javascript";
	public static final String MIME_JSON = "application/json";
	public static final String MIME_PNG = "image/png";
	
	public static final String PARAM_TEXT = "text";
	public static final String PARAM_URL = "url";
	public static final String PARAM_PATH = "path";
	
	public static Response initResponse(Response response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		return response;
	}

	public static Response newResponse(Status status, String mimeType, InputStream data) {
		Response response = new Response(status, mimeType, data);
		return initResponse(response);
	}

	public static Response newResponse(Status status, String mimeType, String txt) {
		Response response = new Response(status, mimeType, txt);
		return initResponse(response);
	}

	public static Response newResponseOk() {
		return newResponse(Status.OK, MIME_JSON, "{ \"status\" : \"ok\"}");
	}
	
	public static Response newResponseError(String error) {
		return newResponse(Status.BAD_REQUEST, MIME_JSON, "{ \"status\" : \"error\",\n  \"error\" : \"" + Json.escape(error) + "\"}");
	}
	
	public static Response newResponseException(Exception exception) {
		return newResponseError(Os.exceptionToString(exception));
	}

}
