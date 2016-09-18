package InputListener;

import java.awt.AWTException;

import InputReader.MouseEvent;
import Networking.WindowShareServer;

public class InputListenerDriver {

	public static void main(String[] args) {
		InputListener listener = null;
		try {
			listener = new InputListener();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		WindowShareServer<String> server = new WindowShareServer<String>();
		MouseEvent.network = server;
		server.addListener(listener);
		Thread t = new Thread(server);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
