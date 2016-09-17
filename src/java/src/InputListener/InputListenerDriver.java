package InputListener;

import java.awt.AWTException;

public class InputListenerDriver {

	public static void main(String[] args) {
		try {
			InputListener listener = new InputListener();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		WindowShareServer server = new WindowShareServer();
		Thread t = new Thread(server);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
