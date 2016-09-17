package InputReader;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;

public class MouseMotionReader implements NativeMouseInputListener {
	int width, height;
	Robot robot;
	boolean waiting;
	
	public MouseMotionReader() throws AWTException {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int)screenSize.getWidth();
		height = (int)screenSize.getHeight();
		waiting = false;
		robot = new Robot();
	}
	
	
	@Override
	public void nativeMouseClicked(NativeMouseEvent e) {
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent e) {
		int b = e.getButton();
		int buttons = b == NativeMouseEvent.BUTTON1 ? InputEvent.BUTTON1_DOWN_MASK :
			(b == NativeMouseEvent.BUTTON2 ? InputEvent.BUTTON2_DOWN_MASK : InputEvent.BUTTON3_DOWN_MASK);
		(new MouseUpDownEvent(true, buttons)).send();
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent e) {
		int b = e.getButton();
		int buttons = b == NativeMouseEvent.BUTTON1 ? InputEvent.BUTTON1_DOWN_MASK :
			(b == NativeMouseEvent.BUTTON2 ? InputEvent.BUTTON2_DOWN_MASK : InputEvent.BUTTON3_DOWN_MASK);
		(new MouseUpDownEvent(false, buttons)).send();
	}

	@Override
	public void nativeMouseDragged(NativeMouseEvent e) {
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent e) {
		int dx = e.getX() - width/2;
		int dy = e.getY() - height/2;
		
		if (!waiting) {
			waitAndSend(dx, dy);
		}
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
}
