package net.alf.osgap.server;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import net.alf.osgap.server.NanoHTTPD.IHTTPSession;
import net.alf.osgap.server.NanoHTTPD.Response;
import net.alf.osgap.server.NanoHTTPD.Response.Status;
import net.alf.osgap.tools.Json;

public class ServiceClipboardGet extends ServiceSupport {
	
	private static Clipboard CLIPBOARD = Toolkit.getDefaultToolkit().getSystemClipboard();
		
	public ServiceClipboardGet(String uri, boolean mustBeAuthorized) {
		super(uri, mustBeAuthorized);
	}

	@Override
	Response serve(IHTTPSession session) throws UnsupportedFlavorException, IOException {
		Transferable contents = CLIPBOARD.getContents(null);
		
		if (contents != null) {
			if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String text = (String) contents.getTransferData(DataFlavor.stringFlavor);
				StringBuilder sb = new StringBuilder();
				sb.append("{ \"status\" : \"ok\",\n \"text\" : \"");
				sb.append(Json.escape(text));
				sb.append("\"}\n");
				return newResponse(Status.OK, MIME_JSON, sb.toString());
			}
			if (contents.isDataFlavorSupported(DataFlavor.imageFlavor)) {
				ByteArrayOutputStream output = new ByteArrayOutputStream(2048);
				Object image = contents.getTransferData(DataFlavor.imageFlavor);
				if (image instanceof RenderedImage) {
					ImageIO.write((RenderedImage) image, "PNG", output);
					return newResponse(Status.OK, MIME_PNG, new ByteArrayInputStream(output.toByteArray()));
				}
			}
			if (contents.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				Object fileListRaw = contents.getTransferData(DataFlavor.javaFileListFlavor);
				StringBuilder sb = new StringBuilder();
				sb.append("{ \"status\" : \"ok\",\n \"fileList\" : \n");
				sb.append("[\n");
				if (fileListRaw instanceof List<?>) {
					@SuppressWarnings("unchecked")
					List<File> fileList = (List<File>) fileListRaw;
					for (File f : fileList) {
						sb.append("\"" + f.getAbsolutePath() + "\",\n");
					}
				}
				sb.append("]}\n");
				return newResponse(Status.OK, MIME_JSON, sb.toString());
			}
		}

		return newResponseError("Unknow clipboard format");
	}

}
