package net.alf.osgap.server;

import net.alf.osgap.server.NanoHTTPD.IHTTPSession;
import net.alf.osgap.server.NanoHTTPD.Response;
import net.alf.osgap.tools.Os;

public class ServiceUriIE extends ServiceSupport {

	public ServiceUriIE(String uri, boolean mustBeAuthorized) {
		super(uri, mustBeAuthorized);
	}
	
	@Override
	Response serve(IHTTPSession session) throws Exception {
		Os.openIE(session.getParms().get(PARAM_URL));
		return newResponseOk();
	}

}
