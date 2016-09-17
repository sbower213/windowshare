package InputListener;

import InputReader.MouseEvent;
import InputReader.MouseMoveEvent;
import Networking.NetworkListener;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;

import com.google.gson.Gson;

public class InputListener implements NetworkListener {
	Robot robot;
	Gson gson;
	
	public InputListener() throws AWTException {
		robot = new Robot();
		gson = new Gson();
	}

	public void process(String message) {
		MouseEvent e = gson.fromJson(message, MouseEvent.class);
		if (e instanceof MouseMoveEvent) {
			MouseMoveEvent mme = (MouseMoveEvent)e;
			
			Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
			robot.mouseMove(mouseLoc.x + mme.dx, mouseLoc.y + mme.dy);
		}
	}
	
}
