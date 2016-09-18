package InputListener;

import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.io.File;

import InputReader.MouseEvent;
import Networking.BufferedImageTransferThread;
import Networking.FileTransferThread;
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
		WindowShareServer<File> fileServer = new WindowShareServer<File>(WindowShareServer.FILE_PORT,
				FileTransferThread.class);
		WindowShareServer<BufferedImage> imageServer = new WindowShareServer<BufferedImage>(
				WindowShareServer.IMAGE_PORT, BufferedImageTransferThread.class);
		
		listener.fileTransfer = fileServer;
		listener.imageTransfer = imageServer;
				
		MouseEvent.network = server;
		server.addListener(listener);
		Thread t = new Thread(server);
		t.start();
		Thread t2 = new Thread(fileServer);
		t2.start();
		Thread t3 = new Thread(imageServer);
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
