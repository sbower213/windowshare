package ScreenDrawer;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import Native.DraggedWindowDetector;
import Networking.BufferedImageTransferThread;
import Networking.NetworkListener;
import Networking.WindowShareServer;

public class Draw implements NetworkListener<BufferedImage> {
	
	private DragWindow w;
	
	public Draw() {
		w = new DragWindow();
	}
	
	public static void main(String[] args) throws InterruptedException, AWTException {
		WindowShareServer<BufferedImage> server = new WindowShareServer<BufferedImage>
			(WindowShareServer.FILE_PORT, BufferedImageTransferThread.class);

		server.addListener(new Draw());
		Thread t = new Thread(server);
		t.start();
		t.join();
	}

	@Override
	public void process(BufferedImage message) {
		Window w = defineWindow(message, null);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		clearWindow(w);
	}
	
	public class DragWindow extends Window {
		private static final long serialVersionUID = 1L;
		
		BufferedImage cursor;
		BufferedImage bg;

		public DragWindow() {
			super(null);
			setAlwaysOnTop(true);
			setBounds(getGraphicsConfiguration().getBounds());
			setBackground(new Color(0, true));
		}
		
		public void paint(Graphics g) {
			 if (bg != null) {
				 Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				 int width = (int)screenSize.getWidth();
				 this.setSize(width / 4, (int) ((bg.getHeight() * 1.0) / bg.getWidth() * width / 4));
				 g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
			 }
			 if (cursor != null) {
				 g.drawImage(cursor, 0, 0, null);
			 }
		}
		@Override
		public void update(Graphics g) {
			super.update(g);
			//paint(g);
		}
	}
	
	public Window defineWindow(BufferedImage image, BufferedImage bg){
		w.cursor = image;
		w.bg = bg;
		w.repaint();
		return w;
	}
	
	public static void clearWindow(Window w) {
		w.setVisible(false);
		w.dispose();
	}
	
}
