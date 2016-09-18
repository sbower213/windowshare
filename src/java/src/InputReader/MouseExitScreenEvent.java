package InputReader;

public class MouseExitScreenEvent extends MouseEvent {
	public double height;
	public boolean fromRight;
	
	public MouseExitScreenEvent() {
		type = "leftOriginalScreen";
		height = 0;
		fromRight = false;
	}
	
	public MouseExitScreenEvent(double _h, boolean original, boolean _fromRight) {
		type = original ? "leftOriginalScreen" : "leftHostScreen";
		height = _h;
		fromRight = _fromRight;
	}
	
	public String toString() {
		return super.toString() + " (" + height + ")";
	}
}
