package net.alf.osgap.tools;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

public final class Os {
	
	public static Runtime RUNTIME = Runtime.getRuntime();
	
	private static final String JAVA_HOME = "java.home";
	private static final String FILE_SEPARATOR = "file.separator";

	private Os() {
		// tools
	}
	
	public static String exceptionToString(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

	public static void openUrl(String url) throws IOException, URISyntaxException, InterruptedException, CmdLineReturnException {
		if (Desktop.isDesktopSupported()) {
			Desktop.getDesktop().browse(new URI(url));
		} else {
			xdgOpen(url);
		}
	}
	
	public static void editFile(String path) throws IOException, URISyntaxException, InterruptedException, CmdLineReturnException {
		File file = new File(path);
		if (file.canExecute()) {
			throw new IllegalArgumentException(file + " is executable");
		}
		
		if (Desktop.isDesktopSupported()) {
			Desktop.getDesktop().edit(file);
		} else {
			xdgOpen(path);
		}
	}

	public static void openFile(String path) throws IOException, InterruptedException, CmdLineReturnException {
		File file = new File(path);
		if (file.canExecute()) {
			throw new IllegalArgumentException(file + " is executable");
		}
		
		if (Desktop.isDesktopSupported()) {
			Desktop.getDesktop().open(file);
		} else {
			xdgOpen(path);
		}
	}

	public static void xdgOpen(String arg) throws IOException, InterruptedException, CmdLineReturnException {
		runtimeExec("xdg-open " + arg, true);
	}

	public static void openIE(String url) throws IOException, URISyntaxException, InterruptedException, CmdLineReturnException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (System.getProperty("os.name").startsWith("Windows")) {
			String iePath = System.getenv("ProgramFiles") + File.separator + "Internet Explorer" + File.separator + "iexplore.exe";
			if (! new File(iePath).exists()) {
				iePath = WinRegistryLight.readString(WinRegistryLight.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\App Paths\\IEXPLORE.EXE", "");
			}
			runtimeExec(iePath + " " + url, false);
		} else {
			// A défaut
			openUrl(url);
		}
	}
	
	public static void runtimeExec(String cmdLine, boolean waitFor) throws CmdLineReturnException, InterruptedException, IOException {
		Process process = RUNTIME.exec(cmdLine);
		if (waitFor) {
			int exitCode = process.waitFor();
			if (exitCode != 0) { 
				throw new CmdLineReturnException("Command:" + cmdLine + "\nExit code: "+ exitCode);
			}
		}
	}
	
	public static String getJavaPath() {
		final String separator = System.getProperty(FILE_SEPARATOR);
		final String javaHome = System.getProperty(JAVA_HOME);
		final String javaPath =  javaHome + separator + "bin" + separator + "java";
		final String javawPath = javaHome + separator + "bin" + separator + "javaw";
		if (new File(javawPath).exists()) {
			return javawPath;
		} else {
			return javaPath;
		}
	}
 	
	@SuppressWarnings("serial")
	static public final class CmdLineReturnException extends Exception {
		
		public CmdLineReturnException(String message) {
			super(message);
		}
		
	}

}
