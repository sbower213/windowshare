package InputReader;
import java.awt.AWTException;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import Networking.WindowShareClient;

public class MouseMotionReaderTester {

	public static void main(String[] args) {
        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        // Construct the example object.
        MouseMotionReader example = null;
		try {
			example = new MouseMotionReader();
		} catch (AWTException ex) {
            System.err.println("There was a problem setting up the robot.");
            System.err.println(ex.getMessage());
            
            System.exit(1);
		}
		
		WindowShareClient wsc = new WindowShareClient();
		MouseEvent.network = wsc;
		wsc.addListener(example);

        // Add the appropriate listeners.
        GlobalScreen.addNativeMouseListener(example);
        GlobalScreen.addNativeMouseMotionListener(example);
        GlobalScreen.addNativeKeyListener(new KeyboardReader());
	}

}
