package net.alf.osgap.server;

import net.alf.osgap.server.NanoHTTPD.IHTTPSession;
import net.alf.osgap.server.NanoHTTPD.Response;

public class ServiceClose extends ServiceSupport {

	public ServiceClose(String uri, boolean mustBeAuthorized) {
		super(uri, mustBeAuthorized);
	}
	
	@Override
	Response serve(IHTTPSession session) throws Exception {
		Server.close();
		return newResponseOk();
	}

}
