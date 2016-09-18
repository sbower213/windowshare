package InputReader;
import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.io.File;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import Networking.BufferedImageTransferThread;
import Networking.FileTransferThread;
import Networking.WindowShareClient;
import Networking.WindowShareServer;

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
		
		WindowShareClient<String> wsc = new WindowShareClient<String>();
		WindowShareClient<File> fileClient = new WindowShareClient<File>(WindowShareClient.SERVER_IP, WindowShareServer.FILE_PORT,
				FileTransferThread.class);
		WindowShareClient<BufferedImage> imageClient = new WindowShareClient<BufferedImage>(
				WindowShareClient.SERVER_IP, WindowShareServer.IMAGE_PORT, BufferedImageTransferThread.class);
		
		example.fileTransfer = fileClient;
		example.imageTransfer = imageClient;
		
		MouseEvent.network = wsc;
		wsc.addListener(example);
		Thread t = new Thread(wsc);
		t.start();
		Thread t2 = new Thread(fileClient);
		t2.start();
		Thread t3 = new Thread(imageClient);
		t3.start();
        // Add the appropriate listeners.
        GlobalScreen.addNativeMouseListener(example);
        GlobalScreen.addNativeMouseMotionListener(example);
        GlobalScreen.addNativeKeyListener(new KeyboardReader());
	}

}
