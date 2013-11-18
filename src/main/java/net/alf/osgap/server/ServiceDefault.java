package net.alf.osgap.server;

import net.alf.osgap.server.NanoHTTPD.IHTTPSession;
import net.alf.osgap.server.NanoHTTPD.Response;
import net.alf.osgap.server.NanoHTTPD.Response.Status;

public class ServiceDefault extends Service {
	
	public ServiceDefault() {
		super();
	}
	
	@Override
	boolean canServe(IHTTPSession session) {
		return true;
	}
	
	@Override
	boolean mustBeAuthorized(IHTTPSession session) {
		return false;
	}

	@Override
	Response serve(IHTTPSession session) throws Exception {
		// not recognize : debug information
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<head><title>Debug Server</title></head>");
		sb.append("<body>");
		sb.append("<h1>Response</h1>");
		sb.append("<p><blockquote><b>URI -</b> ").append(String.valueOf(session.getUri())).append("<br />");
		sb.append("<b>Method -</b> ").append(String.valueOf(session.getMethod())).append("</blockquote></p>");
		sb.append("<h3>Headers</h3><p><blockquote>").append(String.valueOf(session.getHeaders())).append("</blockquote></p>");
		sb.append("<h3>Parms</h3><p><blockquote>").append(String.valueOf(session.getParms())).append("</blockquote></p>");
		sb.append("</body>");
		sb.append("</html>");
		return ServerIO.newResponse(Status.BAD_REQUEST, MIME_HTML, sb.toString());
	}

}
