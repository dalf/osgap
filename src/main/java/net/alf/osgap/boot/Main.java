package net.alf.osgap.boot;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.UUID;

import net.alf.osgap.Configuration;
import net.alf.osgap.server.Server;
import net.alf.osgap.tools.Os;

public class Main implements Configuration {
	
	private static final String KEY = UUID.randomUUID().toString();
	public static final boolean IS_SERVER_RUNNING = isServerRunning();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {
		if (args.length >= 1) {
			String url = args[0];
			if (IS_SERVER_RUNNING) {
				// start the browser
				Os.openUrl(url);
			} else {
				// start the browser
				Os.openUrl(url + "#osgap.key=" + KEY);
				// Start the localserver
				startLocalServer();
			}
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
		System.out.println(KEY);
		String[] cmdLine = new String[] { javaPath, "-Dosgap.port=" + PORT, "-Dosgap.key=" + KEY, "-client", "-Xmx50m", "-XX:MinHeapFreeRatio=10", "-XX:MaxHeapFreeRatio=10", "-cp", jarPath, Server.class.getName() };
		Runtime.getRuntime().exec(cmdLine);
	}
	
}
