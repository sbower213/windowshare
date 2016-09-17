package InputListener;

import InputReader.MouseEvent;
import InputReader.MouseMoveEvent;
import Networking.NetworkListener;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.Gson;

public class InputListener implements NetworkListener {
	Robot robot;
	Gson gson;
	
	Queue<MouseEvent> eventQueue;
	
	public InputListener() throws AWTException {
		robot = new Robot();
		gson = new Gson();
		eventQueue = new ConcurrentLinkedQueue<MouseEvent>();
		new Thread(new MouseEventHandler()).start();
	}

	public void process(String message) {
		MouseEvent e = gson.fromJson(message, MouseEvent.class);
		//System.out.println(e);
		if (e.type.equals("move")) {
			MouseMoveEvent mme = gson.fromJson(message, MouseMoveEvent.class);
			eventQueue.add(mme);
			synchronized(eventQueue) {
				eventQueue.notify();
			}
			//System.out.println("" + (mouseLoc.x + mme.dx) + "," + (mouseLoc.y + mme.dy));
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
						robot.mouseMove(newX, newY);
						curTime = System.currentTimeMillis();
					} while (curTime - startTime < MouseEventHandler.DELTA);
				} else {
					// do other things with other events
				}
			}
		}
		
	}
}
