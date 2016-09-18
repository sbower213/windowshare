package ScreenDrawer;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Robot;
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
	
	private static BufferedImage image;
	
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
		Window w = defineWindow(message);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		clearWindow(w);
	}
	
	public static Window defineWindow(BufferedImage image){
		Window w=new Window(null)
		{
		  @Override
		  public void paint(Graphics g)
		  {
		    g.drawImage(image, 0, 0, null);
		  }
		  @Override
		  public void update(Graphics g)
		  {
			  //paint(g);
		  }
		};
		w.setAlwaysOnTop(true);
		w.setBounds(w.getGraphicsConfiguration().getBounds());
		w.setBackground(new Color(0, true));
		//w.setVisible(true);
		return w;
	}
	
	public static void clearWindow(Window w) {
		w.setVisible(false);
		w.dispose();
	}
	
}
