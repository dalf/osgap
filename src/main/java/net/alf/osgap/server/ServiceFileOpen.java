package net.alf.osgap.server;

import net.alf.osgap.server.NanoHTTPD.IHTTPSession;
import net.alf.osgap.server.NanoHTTPD.Response;
import net.alf.osgap.tools.Os;

public class ServiceFileOpen extends ServiceSupport {

	public ServiceFileOpen(String uri, boolean mustBeAuthorized) {
		super(uri, mustBeAuthorized);
	}
	
	@Override
	Response serve(IHTTPSession session) throws Exception {
		Os.openFile(session.getParms().get(PARAM_PATH));
		return newResponseOk();
	}

}
