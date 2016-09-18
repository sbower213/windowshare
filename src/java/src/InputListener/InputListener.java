package InputListener;

import InputReader.MouseEvent;
import InputReader.MouseMoveEvent;
import InputReader.MouseUpDownEvent;
import InputReader.MouseExitScreenEvent;
import Networking.NetworkListener;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.Gson;

public class InputListener implements NetworkListener<String> {
	Robot robot;
	Gson gson;
	boolean remoteControl;
	
	Queue<MouseEvent> eventQueue;
	
	public InputListener() throws AWTException {
		robot = new Robot();
		gson = new Gson();
		remoteControl = false;
		eventQueue = new ConcurrentLinkedQueue<MouseEvent>();
		new Thread(new MouseEventHandler()).start();
	}

	public void process(String message) {
		MouseEvent e = gson.fromJson(message, MouseEvent.class);
		//System.out.println(e);
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
				if (event instanceof MouseMoveEvent) {
					if (!remoteControl) {
						/* only control mouse if it was transferred from original computer */
						continue;
					}
					// interpolate the current position with the new position.
					MouseMoveEvent mme = (MouseMoveEvent)event;
					Point start = MouseInfo.getPointerInfo().getLocation();
					long startTime = System.currentTimeMillis();
					long curTime = startTime;
					do {
						long deltaTime = curTime - startTime;
						float frac = (deltaTime * 1.0f) / MouseEventHandler.DELTA;
						int dx = (int) (mme.dx * frac);
						int dy = (int) (mme.dy * frac);
						int newX = start.x + dx;
						int newY = start.y + dy;
						Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
						int width = (int)screenSize.getWidth();
						int height = (int)screenSize.getHeight();
						if (newX >= width) {
							MouseExitScreenEvent e = new MouseExitScreenEvent((1.0 * newY) / height, false);
							e.send();
							remoteControl = false;
							robot.mouseMove(width - 10, newY);
							break;
						}
						robot.mouseMove(newX, newY);
						curTime = System.currentTimeMillis();
					} while (curTime - startTime < MouseEventHandler.DELTA);
				} else if (event instanceof MouseUpDownEvent) {
					if (event.type.equals("click")) {
						robot.mousePress(((MouseUpDownEvent) event).buttons);
					} else if (event.type.equals("release")) {
						robot.mouseRelease(((MouseUpDownEvent) event).buttons);
					}
				} else if (event instanceof MouseExitScreenEvent) {
					MouseExitScreenEvent mlose = (MouseExitScreenEvent)event;
					remoteControl = true;
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					int width = (int)screenSize.getWidth();
					int height = (int)screenSize.getHeight();
					robot.mouseMove(width - 10, (int) (height * mlose.height));
				}
				else {
					// do other things with other events
				}
			}
		}
		
	}
}
