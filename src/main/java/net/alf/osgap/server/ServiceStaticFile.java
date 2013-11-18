package net.alf.osgap.server;

import net.alf.osgap.server.NanoHTTPD.IHTTPSession;
import net.alf.osgap.server.NanoHTTPD.Response;
import net.alf.osgap.server.NanoHTTPD.Response.Status;

public class ServiceStaticFile extends ServiceSupport {

	private String path;
	private String mimeType;

	public ServiceStaticFile(String uri, boolean mustBeAuthorized, String mimeType, String path) {
		super(uri, mustBeAuthorized);
		this.mimeType = mimeType;
		this.path = path;
	}

	@Override
	Response serve(IHTTPSession session) throws Exception {;
		return newResponse(Status.OK, this.mimeType, this.getClass().getResourceAsStream(this.path));
	}

}
