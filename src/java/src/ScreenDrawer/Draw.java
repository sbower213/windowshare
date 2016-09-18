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

public class Draw {
	
	private static BufferedImage image;
	
	public static void main(String[] args) throws InterruptedException, AWTException {
		Thread.sleep(3000);
		Robot r = new Robot();
		BufferedImage i  = r.createScreenCapture(DraggedWindowDetector.activeWindowBounds());
		
		Window w = defineWindow(i);
		Thread.sleep(15000);
		clearWindow(w);
	}
	static Window defineWindow(BufferedImage image){
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
		w.setVisible(true);
		return w;
	}
	
	static void drawImage(String filePath, Graphics g) {
		try {                
	          image = ImageIO.read(new File("C:/Users/Siddy/Pictures/siddy.jpg"));
	          g.drawImage(image, 0, 0, null);
	       } catch (IOException ex) {
	            // handle exception...
	       }
	}
	
	static void clearWindow(Window w) {
		w.setVisible(false);
		w.dispose();
	}
	
}
