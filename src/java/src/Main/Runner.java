package Main;

import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.io.File;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import InputListener.InputListener;
import InputReader.KeyboardReader;
import InputReader.MouseEvent;
import InputReader.MouseMotionReader;
import Networking.BufferedImageTransferThread;
import Networking.FileTransferThread;
import Networking.WindowShareClient;
import Networking.WindowShareNode;
import Networking.WindowShareServer;

public class Runner {
	static final boolean isServer = true;

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
        MouseMotionReader motionReader = null;
		try {
			motionReader = new MouseMotionReader();
		} catch (AWTException ex) {
            System.err.println("There was a problem setting up the robot.");
            System.err.println(ex.getMessage());
            
            System.exit(1);
		}
		
		InputListener listener = null;
		try {
			listener = new InputListener();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		WindowShareNode<String> stringTransfer;
		WindowShareNode<File> fileTransfer;
		WindowShareNode<BufferedImage> imageTransfer;
		
		if (!isServer) {
			stringTransfer = new WindowShareClient<String>();
			fileTransfer = new WindowShareClient<File>(WindowShareClient.SERVER_IP, WindowShareServer.FILE_PORT,
					FileTransferThread.class);
			imageTransfer = new WindowShareClient<BufferedImage>(
					WindowShareClient.SERVER_IP, WindowShareServer.IMAGE_PORT, BufferedImageTransferThread.class);
		} else {
			stringTransfer = new WindowShareServer<String>();
			fileTransfer = new WindowShareServer<File>(WindowShareServer.FILE_PORT,
					FileTransferThread.class);
			imageTransfer = new WindowShareServer<BufferedImage>(
					WindowShareServer.IMAGE_PORT, BufferedImageTransferThread.class);
		}
		
		motionReader.fileTransfer = fileTransfer;
		motionReader.imageTransfer = imageTransfer;

		listener.setStringTransfer(stringTransfer);
		listener.setFileTransfer(fileTransfer);
		listener.setImageTransfer(imageTransfer);
		
        // Add the appropriate listeners.
        GlobalScreen.addNativeMouseListener(motionReader);
        GlobalScreen.addNativeMouseMotionListener(motionReader);
        GlobalScreen.addNativeKeyListener(new KeyboardReader());
		
		MouseEvent.network = stringTransfer;
		stringTransfer.addListener(motionReader);
		Thread t = new Thread(stringTransfer);
		t.start();
		Thread t2 = new Thread(fileTransfer);
		t2.start();
		Thread t3 = new Thread(imageTransfer);
		t3.start();

		try {
			t.join();
			t2.join();
			t3.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
