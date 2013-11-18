package net.alf.osgap.auth;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import net.alf.osgap.server.NanoHTTPD.IHTTPSession;
import net.alf.osgap.server.NanoHTTPD.Response;

public class Authorization {

	private static final String HTTP_ORIGIN = "origin";
	private static final String HTTP_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	
	private static final Authorization INSTANCE = new Authorization();
	private static  Preferences PREFERENCES = Preferences.userNodeForPackage(net.alf.osgap.server.Server.class);

	public static Authorization getInstance() {
		return INSTANCE;
	}

	private Set<String> domains = new HashSet<String>();

	private Authorization() {
		this.load();
	}
	
	private void load() {
		try {
			PREFERENCES = Preferences.userNodeForPackage(net.alf.osgap.server.Server.class);
			String[] keys = PREFERENCES.keys();
			for (int i=0; i<keys.length; i++) {
				this.domains.add(keys[i]);
			}
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	private void save() {
		try {
			PREFERENCES.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public void addDomain(String domain) throws URISyntaxException {
		this.addDomain(new URI(domain));
	}
	
	private String getKey(URI uri) {
		String scheme = uri.getScheme();
		String host = uri.getHost();
		return scheme + "://" +host;
	}
 
	public void addDomain(URI uri) {
		String key = this.getKey(uri);
		this.domains.add(key);
		PREFERENCES.putInt(key, 1);
		this.save();
	}
	
	private boolean checkDomain(URI uri) {
		String key = this.getKey(uri);
		return this.domains.contains(key);		
	}

	public boolean check(IHTTPSession session) {
		try {
			String origin = session.getHeaders().get(HTTP_ORIGIN);
			if (origin == null) {
				return true;
			}
			URI originUri = new URI(origin);
			boolean result = this.checkDomain(originUri);
			if (! result) {
				result = AuthorizationUI.askUser(originUri);
				if (result) {
					this.addDomain(originUri);
				}
			}
			return result;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void postProcess(IHTTPSession session, Response response) {
		Map<String, String> headers = session.getHeaders();
		String origin = headers.get(HTTP_ORIGIN);
		if (origin != null) {
			response.addHeader(HTTP_ACCESS_CONTROL_ALLOW_ORIGIN, origin);
		}
	}

}
