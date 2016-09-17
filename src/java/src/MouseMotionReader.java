import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;

public class MouseMotionReader implements NativeMouseInputListener {
	int width, height;
	Robot robot;
	
	public MouseMotionReader() throws AWTException {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int)screenSize.getWidth();
		height = (int)screenSize.getHeight();
		robot = new Robot();
	}
	
	
	@Override
	public void nativeMouseClicked(NativeMouseEvent e) {
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent e) {
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent e) {
	}

	@Override
	public void nativeMouseDragged(NativeMouseEvent e) {
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent e) {
		int xDiff = e.getX() - width/2;
		int yDiff = e.getY() - height/2;
		System.out.println("mouse moved by (" + xDiff + ", " + yDiff + ")");
		
		if (xDiff * xDiff + yDiff * yDiff > 100) {
			robot.mouseMove(width/2, height/2);
		}
	}
}
