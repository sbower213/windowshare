package ScreenDrawer;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import Native.DraggedWindowDetector;
import Networking.BufferedImageTransferThread;
import Networking.WindowShareClient;
import Networking.WindowShareServer;

public class ScreenshotSender {
	
	private static final int T = 0;

	public static void main(String[] args) throws InterruptedException, AWTException {
		Thread.sleep(3000);
		Robot r = new Robot();
		BufferedImage i  = r.createScreenCapture(DraggedWindowDetector.activeWindowBounds());
		
		WindowShareClient<BufferedImage> c = new WindowShareClient<BufferedImage>(WindowShareClient.SERVER_IP,
				WindowShareServer.FILE_PORT, BufferedImageTransferThread.class);
		
		Thread t = new Thread(c);
		t.start();
		
		c.send(i);
		
		t.join();
	}

}
