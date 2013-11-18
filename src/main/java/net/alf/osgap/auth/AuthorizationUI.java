package net.alf.osgap.auth;

import java.net.URI;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class AuthorizationUI {
	
	public static boolean askUser(URI uri) {
		// hack to me sure dialog is on front
		JFrame frmOpt;  
		frmOpt = new JFrame();
		frmOpt.setVisible(true);
	    frmOpt.setAlwaysOnTop(true);
	    frmOpt.setLocation(320, 200);
	    frmOpt.setSize(0, 0);
	    
		int reply = JOptionPane.showConfirmDialog(frmOpt, "Do you want to add " + uri + " as a trusted source?", "Add domain?",  JOptionPane.YES_NO_OPTION);
		
		frmOpt.dispose();
		
		return (reply == JOptionPane.YES_OPTION);
	}

}
