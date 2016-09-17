package InputListener;

import InputReader.MouseEvent;
import InputReader.MouseMoveEvent;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;

public class InputListener {
	Robot robot;
	
	public InputListener() throws AWTException {
		robot = new Robot();
	}

	public void process(MouseEvent e) {
		if (e instanceof MouseMoveEvent) {
			MouseMoveEvent mme = (MouseMoveEvent)e;
			
			Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
			robot.mouseMove(mouseLoc.x + mme.dx, mouseLoc.y + mme.dy);
		}
	}
	
}
