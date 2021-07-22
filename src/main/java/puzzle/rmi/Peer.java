package puzzle.rmi;

import javax.swing.*;
import java.net.Inet4Address;
import java.net.UnknownHostException;

public class Peer {

	public static void main(final String[] args) {
		JFrame startFrame = new StartGame();
		try {
			System.setProperty("java.rmi.server.hostname", Inet4Address.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		startFrame.setVisible(true);
	}
}
