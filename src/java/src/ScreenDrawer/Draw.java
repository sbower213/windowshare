package ScreenDrawer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Draw {
	
	private static BufferedImage image;
	
	public static void main(String[] args) {
		Window w = defineWindow();
		clearWindow(w);
	}
	static Window defineWindow(){
		Window w=new Window(null)
		{
		  @Override
		  public void paint(Graphics g)
		  {
		    final Font font = getFont().deriveFont(48f);
		    g.setFont(font);
		    g.setColor(Color.RED);
		    final String message = "Hello";
		    FontMetrics metrics = g.getFontMetrics();
		    g.drawString(message,
		      (getWidth()-metrics.stringWidth(message))/2,
		      (getHeight()-metrics.getHeight())/2);
		    //drawImage("hello",g);
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
