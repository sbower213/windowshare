package InputListener;

import InputReader.MouseEvent;
import InputReader.MouseMoveEvent;
import InputReader.MouseUpDownEvent;
import InputReader.MouseExitScreenEvent;
import Networking.NetworkListener;
import ScreenDrawer.Draw;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.imageio.ImageIO;

import com.google.gson.Gson;

public class InputListener implements NetworkListener<String> {
	Robot robot;
	Gson gson;
	boolean remoteControl;
	int mouseX, mouseY;
	int origMouseX, origMouseY;
	Window cursorWindow;
	
	Queue<MouseEvent> eventQueue;
	
	public InputListener() throws AWTException {
		robot = new Robot();
		gson = new Gson();
		remoteControl = false;
		eventQueue = new ConcurrentLinkedQueue<MouseEvent>();
		BufferedImage cursor;
		try {
			cursor = ImageIO.read(new File(getClass().getResource("cursor_win_hand.png").getPath()));
			cursorWindow = Draw.defineWindow(cursor);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new Thread(new MouseEventHandler()).start();
	}

	public void process(String message) {
		MouseEvent e = gson.fromJson(message, MouseEvent.class);
		System.out.println(e);
		if (e.type.equals("move")) {
			e = gson.fromJson(message, MouseMoveEvent.class);
		} else if (e.type.equals("click") || e.type.equals("release")) {
			e = gson.fromJson(message, MouseUpDownEvent.class);
		} else if (e.type.equals("leftOriginalScreen")) {
			e = gson.fromJson(message, MouseExitScreenEvent.class);
		}
		eventQueue.add(e);
		synchronized(eventQueue) {
			eventQueue.notify();
		}
	}
	
	private class MouseEventHandler implements Runnable {
		static final int DELTA = 33; //ms
		
		@Override
		public void run() {
			while (true) {
				// wait for more input
				if (eventQueue.isEmpty()) {
					try {
						synchronized(eventQueue) {
							eventQueue.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				// read event and process it.
				MouseEvent event = eventQueue.remove();
				System.out.println(event.type);
				if (event instanceof MouseMoveEvent) {
					if (!remoteControl) {
						/* only control mouse if it was transferred from original computer */
						continue;
					}
					// interpolate the current position with the new position.
					MouseMoveEvent mme = (MouseMoveEvent)event;
					long startTime = System.currentTimeMillis();
					long curTime = startTime;
					int newX, newY;
					do {
						long deltaTime = curTime - startTime;
						float frac = (deltaTime * 1.0f) / MouseEventHandler.DELTA;
						int dx = (int) (mme.dx * frac);
						int dy = (int) (mme.dy * frac);
						Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
						int width = (int)screenSize.getWidth();
						int height = (int)screenSize.getHeight();
						newX = Math.min(Math.max(0, mouseX + dx), width);
						newY = Math.min(Math.max(0, mouseY + dy), height);
						if (newX >= width || newX <= 0) {
							MouseExitScreenEvent e = new MouseExitScreenEvent((1.0 * newY) / height, true, newX <= 0);
							e.send();
							remoteControl = false;
							cursorWindow.setVisible(false);
							mouseX = width - 10;
							mouseY =  newY;
							break;
						}
						cursorWindow.setLocation(newX, newY);
						curTime = System.currentTimeMillis();
					} while (curTime - startTime < MouseEventHandler.DELTA);
					mouseX = newX;
					mouseY = newY;
				} else if (event instanceof MouseUpDownEvent) {
					if (event.type.equals("click")) {
						Point start = MouseInfo.getPointerInfo().getLocation();
						origMouseX = start.x;
						origMouseY = start.y;
						robot.mouseMove(mouseX, mouseY);
						robot.mousePress(((MouseUpDownEvent) event).buttons);
					} else if (event.type.equals("release")) {
						robot.mouseRelease(((MouseUpDownEvent) event).buttons);
						robot.mouseMove(origMouseX, origMouseY);
					}
				} else if (event instanceof MouseExitScreenEvent) {
					MouseExitScreenEvent mlose = (MouseExitScreenEvent)event;
					remoteControl = true;
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					int width = (int)screenSize.getWidth();
					int height = (int)screenSize.getHeight();
					if (mlose.fromRight)
						mouseX = 10;
					else
						mouseX = width - 10;
					mouseY = (int) (height * mlose.height); 
					System.out.println("init: " + mouseX + ", " + mouseY);
					cursorWindow.setVisible(true);
					cursorWindow.setLocation(mouseX, mouseY);
				}
				else {
					// do other things with other events
				}
			}
		}
		
	}
}
