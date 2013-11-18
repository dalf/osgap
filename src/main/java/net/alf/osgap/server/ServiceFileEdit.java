package net.alf.osgap.server;

import net.alf.osgap.server.NanoHTTPD.IHTTPSession;
import net.alf.osgap.server.NanoHTTPD.Response;
import net.alf.osgap.tools.Os;

public class ServiceFileEdit extends ServiceSupport {

	public ServiceFileEdit(String uri, boolean mustBeAuthorized) {
		super(uri, mustBeAuthorized);
	}
	
	@Override
	Response serve(IHTTPSession session) throws Exception {
		Os.editFile(session.getParms().get(PARAM_PATH));
		return newResponseOk();
	}

}
