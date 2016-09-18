package InputReader;

public class MouseUpDownEvent extends MouseEvent {
	public int buttons;
	
	public MouseUpDownEvent() {
		type = "click";
		buttons = 0;
	}
	
	public MouseUpDownEvent(boolean down, int _buttons) {
		type = down ? "click" : "release";
		buttons = _buttons;
	}
	
	public String toString() {
		return super.toString() + " (" + buttons + ")";
	}
}
