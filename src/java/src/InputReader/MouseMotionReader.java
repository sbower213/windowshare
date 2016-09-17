package InputReader;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import com.google.gson.Gson;

import Networking.NetworkListener;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;

public class MouseMotionReader implements NativeMouseInputListener, NetworkListener {
	int width, height;
	Robot robot;
	boolean waiting;
	boolean mouseOffscreen;
	
	public MouseMotionReader() throws AWTException {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int)screenSize.getWidth();
		height = (int)screenSize.getHeight();
		waiting = false;
		robot = new Robot();
		mouseOffscreen = false;
	}
	
	
	@Override
	public void nativeMouseClicked(NativeMouseEvent e) {
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent e) {
		if (mouseOffscreen) {
			int b = e.getButton();
			int buttons = b == NativeMouseEvent.BUTTON1 ? InputEvent.BUTTON1_DOWN_MASK :
				(b == NativeMouseEvent.BUTTON2 ? InputEvent.BUTTON2_DOWN_MASK : InputEvent.BUTTON3_DOWN_MASK);
			(new MouseUpDownEvent(true, buttons)).send();
		}
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent e) {
		if (mouseOffscreen) {
			int b = e.getButton();
			int buttons = b == NativeMouseEvent.BUTTON1 ? InputEvent.BUTTON1_DOWN_MASK :
				(b == NativeMouseEvent.BUTTON2 ? InputEvent.BUTTON2_DOWN_MASK : InputEvent.BUTTON3_DOWN_MASK);
			(new MouseUpDownEvent(false, buttons)).send();
		}
	}

	@Override
	public void nativeMouseDragged(NativeMouseEvent e) {
		if (mouseOffscreen) {
			int dx = e.getX() - width/2;
			int dy = e.getY() - height/2;
			
			if (!waiting) {
				waitAndSend(dx, dy);
			}
		} else if (e.getX() <= 0) {
			leaveScreen(e.getY());
		}
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent e) {
		if (mouseOffscreen) {
			int dx = e.getX() - width/2;
			int dy = e.getY() - height/2;
			
			if (!waiting) {
				waitAndSend(dx, dy);
			}
		} else if (e.getX() <= 0) {
			leaveScreen(e.getY());
		}
	}
	
	public void leaveScreen(int h) {
		mouseOffscreen = true;
		
		(new MouseLeftScreenEvent((1.0 * h) / height, true)).send();
	}
	
	public void waitAndSend(int dx, int dy) {
		new Thread(() -> {
			waiting = true;
			try {
				Thread.sleep(33);
				(new MouseMoveEvent(dx, dy)).send();
				robot.mouseMove(width/2, height/2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}		// Roughly 1/30 seconds
			waiting = false;
		}).start();
	}


	@Override
	public void process(String message) {
		Gson gson = new Gson();
		MouseEvent e = gson.fromJson(message, MouseEvent.class);
		
		if (e.type.equals("leftHostScreen")) {
			MouseLeftScreenEvent mlse = gson.fromJson(message, MouseLeftScreenEvent.class);
			mouseOffscreen = false;
			robot.mouseMove(0, (int) (mlse.height * height));
		}
	}
}
