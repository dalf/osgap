package net.alf.osgap.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.concurrent.Semaphore;

import net.alf.osgap.Configuration;
import net.alf.osgap.auth.Authorization;
import net.alf.osgap.server.NanoHTTPD.Response.Status;
import net.alf.osgap.tools.Json;
import net.alf.osgap.tools.Os;

public class Server extends NanoHTTPD implements Configuration {

	private static final String MIME_JAVASCRIPT = "text/javascript";
	private static final String MIME_JSON = "application/json";
	
	private static final String URI_FILE_OPEN = "/file/open/";
	private static final String URI_FILE_EDIT = "/file/edit/";
	private static final String URI_IE = "/ie/";
	private static final String URI_CLOSE = "/close";
	private static final Object URI_JS = "/osgap.js";
	private static final Object URI_JS_MIN = "/osgap-min.js";
	private static final Object URI_JS_LOADER = "/osgap-loader.js";
	private static final Object URI_JS_LOADER_MIN = "/osgap-loader-min.js";
	
	private static Semaphore running = new Semaphore(1);
	private static Authorization AUTHORIZATION = Authorization.getInstance(); 
	
	private static int port;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		port = Integer.parseInt(System.getProperty("osgap.port", "" + PORT));
		
		running.acquire();
		Server server = new Server("127.0.0.1", port);
		server.start();
		running.acquire();
		// let the server sends the responses
		Thread.sleep(1000);
		server.stop();
	}

	public Server(String hostname, int port) {
		super(hostname, port);
	}
	
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
		return newResponse(Status.OK, MIME_JSON, "{ status : \"ok\"}");
	}
	
	public static Response newResponseException(Exception exception) {
		return newResponse(Status.BAD_REQUEST, MIME_JSON, "{ status : \"error\",\n  error: \"" +Json.escape(Os.exceptionToString(exception)) + "\"}");
	}

	@Override
	public Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> parms, Map<String, String> files) {
		Response response = null;
		
		// check authorization
		if (! AUTHORIZATION.check(uri, method, headers, parms, files)) {
			return newResponse(Status.FORBIDDEN, MIME_JSON, "{ status : \"forbidden\"}");
		}
		
		
		if (uri.equals(URI_JS)) {
			return newResponse(Status.OK, MIME_JAVASCRIPT, this.getClass().getResourceAsStream("/osgap.js"));
			
		} else if (uri.equals(URI_JS_MIN)) {
			return newResponse(Status.OK, MIME_JAVASCRIPT, this.getClass().getResourceAsStream("/osgap-min.js"));

		} else if (uri.equals(URI_JS_LOADER)) {
			return newResponse(Status.OK, MIME_JAVASCRIPT, this.getClass().getResourceAsStream("/osgap-loader.js"));
			
		} else if (uri.equals(URI_JS_LOADER_MIN)) {
			return newResponse(Status.OK, MIME_JAVASCRIPT, this.getClass().getResourceAsStream("/osgap-loader-min.js"));
			
		} else if (uri.startsWith(URI_FILE_OPEN)) {
			// ask to open a file or a directory 
			try {
				Os.openFile(getOneParameter(URI_FILE_OPEN, uri, parms));
				response = newResponseOk();
			} catch (Exception e) {
				response = newResponseException(e);
			}
			
		} else if (uri.startsWith(URI_FILE_EDIT)) {
			// ask to edit a file
			try {
				Os.editFile(getOneParameter(URI_FILE_EDIT, uri, parms));
				response = newResponseOk();
			} catch (Exception e) {
				response = newResponseException(e);
			}
			
		} else if (uri.startsWith(URI_IE)) {
			// ask to open with IE
			try {
				Os.openIE(getOneParameter(URI_IE, uri, parms));
				response = newResponseOk();
			} catch (Exception e) {
				response = newResponseException(e);
			}
			
		} else if (uri.startsWith(URI_CLOSE)) {
			// ask to end the service
			running.release();
			response = newResponse(Status.ACCEPTED, MIME_JSON, "{ status : \"ok\"}");
			
		} else {
			// not recognize : debug information
			StringBuilder sb = new StringBuilder();
			sb.append("<html>");
			sb.append("<head><title>Debug Server</title></head>");
			sb.append("<body>");
			sb.append("<h1>Response</h1>");
			sb.append("<p><blockquote><b>URI -</b> ").append(String.valueOf(uri)).append("<br />");
			sb.append("<b>Method -</b> ").append(String.valueOf(method)).append("</blockquote></p>");
			sb.append("<h3>Headers</h3><p><blockquote>").append(String.valueOf(headers)).append("</blockquote></p>");
			sb.append("<h3>Parms</h3><p><blockquote>").append(String.valueOf(parms)).append("</blockquote></p>");
			sb.append("<h3>Files</h3><p><blockquote>").append(String.valueOf(files)).append("</blockquote></p>");
			sb.append("</body>");
			sb.append("</html>");
			response = newResponse(Status.BAD_REQUEST, MIME_HTML, sb.toString());
			
		}
		
		// Add header
		AUTHORIZATION.postProcess(uri, method, headers, parms, files, response);

		System.gc();

		return response;
	}
	
	protected String getOneParameter(String serviceUri, String uri, Map<String, String> parms) {
		String result = uri;
		try {
			result = URLDecoder.decode(uri.substring(serviceUri.length()), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// nothing
		}
		String query = parms.get("NanoHttpd.QUERY_STRING");
		if (query != null) {
			result = result + "?" + query;
		}
		return result;
	}

}
