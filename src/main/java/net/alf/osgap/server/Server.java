package net.alf.osgap.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;

import net.alf.osgap.Configuration;
import net.alf.osgap.auth.Authorization;
import net.alf.osgap.server.NanoHTTPD.Response.Status;

public class Server extends NanoHTTPD implements Configuration {

	private static Semaphore running = new Semaphore(1);
	private static Authorization AUTHORIZATION = Authorization.getInstance();

	private static int port;

	public static void main(String[] args) throws IOException, InterruptedException {
		port = Integer.parseInt(System.getProperty("osgap.port", "" + PORT));

		running.acquire();
		Server server = new Server("127.0.0.1", port);
		server.start();
		running.acquire();
		// let the server sends the responses
		Thread.sleep(1000);
		server.stop();
	}
	
	public static void close() {
		running.release();
	}
	
	private List<Service> services = new ArrayList<Service>();

	public Server(String hostname, int port) {
		super(hostname, port);
		this.services.add(new ServiceStaticFile("/", false, ServerIO.MIME_HTML, "/index.html"));
		this.services.add(new ServiceStaticFile("/osgap.js", false, ServerIO.MIME_JAVASCRIPT, "/osgap.js"));
		this.services.add(new ServiceStaticFile("/osgap-min.js", false, ServerIO.MIME_JAVASCRIPT, "/osgap-min.js"));
		this.services.add(new ServiceStaticFile("/osgap-loader.js", false, ServerIO.MIME_JAVASCRIPT, "/osgap-loader.js"));
		this.services.add(new ServiceStaticFile("/osgap-loader-min.js", false, ServerIO.MIME_JAVASCRIPT, "/osgap-loader-min.js"));
		this.services.add(new ServiceFileOpen("/file/open", true));
		this.services.add(new ServiceFileEdit("/file/edit", true));
		this.services.add(new ServiceUriIE("/url/ie", true));
		this.services.add(new ServiceClipboardGet("/clipboard/get", true));
		this.services.add(new ServiceClipboardSet("/clipboard/set", true));
		this.services.add(new ServiceClose("/close", true));
		this.services.add(new ServiceDefault());
	}



	@Override
	public Response serve(IHTTPSession session) {
		Response response = null;
		
		Iterator<Service> serviceIterator = this.services.iterator();
		while ((serviceIterator.hasNext()) && (response == null)) {
			Service service = serviceIterator.next();
			if (service.canServe(session)) {
				if (service.mustBeAuthorized(session) && !AUTHORIZATION.check(session)) {
					response = ServerIO.newResponse(Status.FORBIDDEN, ServerIO.MIME_JSON, "{ status : \"forbidden\"}");
				} else {
					try {
						response = service.serve(session);
					} catch (Exception exception) {
						response = ServerIO.newResponseException(exception);
					}
				}
			}
		}
		
		if (response == null) {
			response = ServerIO.newResponseError("Unknow service");
		}

		// Add header
		AUTHORIZATION.postProcess(session, response);

		System.gc();

		return response;
	}

}
