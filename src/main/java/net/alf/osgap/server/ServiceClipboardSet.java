package net.alf.osgap.server;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import net.alf.osgap.server.NanoHTTPD.IHTTPSession;
import net.alf.osgap.server.NanoHTTPD.Response;

public class ServiceClipboardSet extends ServiceSupport {
	
	private static Clipboard CLIPBOARD = Toolkit.getDefaultToolkit().getSystemClipboard();

	public ServiceClipboardSet(String uri, boolean mustBeAuthorized) {
		super(uri, mustBeAuthorized);
	}
	
	@Override
	Response serve(IHTTPSession session) throws Exception {
		String content = session.getParms().get(PARAM_TEXT);
		StringSelection selection = new StringSelection(content);
		CLIPBOARD.setContents(selection, selection);
		return newResponseOk();
	}

}
