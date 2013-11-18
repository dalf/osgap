package net.alf.osgap.server;

import net.alf.osgap.server.NanoHTTPD.IHTTPSession;
import net.alf.osgap.server.NanoHTTPD.Response;

public abstract class Service extends ServerIO {

	public Service() {
	}
	
	abstract boolean mustBeAuthorized(IHTTPSession session);
	
	abstract boolean canServe(IHTTPSession session);
	
	abstract Response serve(IHTTPSession session) throws Exception;

}
