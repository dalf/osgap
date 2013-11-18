package net.alf.osgap.server;

import net.alf.osgap.server.NanoHTTPD.IHTTPSession;

public abstract class ServiceSupport extends Service {
	
	private final String uri;
	private final boolean mustBeAuthorized;

	public ServiceSupport(String uri, boolean mustBeAuthorized) {
		this.uri = uri;
		this.mustBeAuthorized = mustBeAuthorized;
	}
	
	boolean mustBeAuthorized(IHTTPSession session) {
		return this.mustBeAuthorized;
	}
	
	boolean canServe(IHTTPSession session) {
		return session.getUri().equals(uri);
	}
	

}
