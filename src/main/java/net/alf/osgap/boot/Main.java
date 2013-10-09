package net.alf.osgap.boot;

import java.io.IOException;
import java.net.ServerSocket;

import net.alf.osgap.Configuration;
import net.alf.osgap.auth.Authorization;
import net.alf.osgap.server.Server;
import net.alf.osgap.tools.Os;

public class Main implements Configuration {
	
	public static final boolean IS_SERVER_RUNNING = isServerRunning();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {
		if (args.length >= 1) {
			String url = args[0];
			// add domain to the trust list
			Authorization.getInstance().addDomain(url);
			// start the browser
			Os.openUrl(url);
		}
		
		if (! IS_SERVER_RUNNING) {
			// Start the localserver
			startLocalServer();
		}
	}

	private static boolean isServerRunning() {
		try {
		    ServerSocket serverSocket = new ServerSocket(PORT);
		    serverSocket.close();
		    return false;
		} catch (IOException e) {
		    return true;
		}
	}

	public static void startLocalServer() throws Exception {
		final String jarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		final String javaPath = Os.getJavaPath();
		String[] cmdLine = new String[] { javaPath, "-Dosgap.port=" + PORT, "-client", "-Xmx50m", "-XX:MinHeapFreeRatio=10", "-XX:MaxHeapFreeRatio=10", "-cp", jarPath, Server.class.getName() };
		Runtime.getRuntime().exec(cmdLine);
	}
	
}
