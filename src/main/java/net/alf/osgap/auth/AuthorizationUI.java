package net.alf.osgap.auth;

import java.net.URI;

import javax.swing.JOptionPane;

public class AuthorizationUI {
	
	public static boolean askUser(URI uri) {
		int reply = JOptionPane.showConfirmDialog(null, "Do you want to add " + uri + " as a trusted source?", "Add domain?",  JOptionPane.YES_NO_OPTION);
		return (reply == JOptionPane.YES_OPTION);
	}

}
