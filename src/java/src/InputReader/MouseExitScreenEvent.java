package InputReader;

public class MouseExitScreenEvent extends MouseEvent {
	public double height;
	public double offset;
	public boolean fromRight;
	
	public MouseExitScreenEvent() {
		type = "leftOriginalScreen";
		height = 0;
		fromRight = false;
	}
	
	public MouseExitScreenEvent(double _h, boolean original, boolean _fromRight) {
		type = original ? "leftOriginalScreen" : "leftHostScreen";
		height = _h;
		offset = 0;
		fromRight = _fromRight;
	}
	
	public MouseExitScreenEvent(double _o, double _h, boolean original, boolean _fromRight) {
		type = original ? "leftOriginalScreen" : "leftHostScreen";
		height = _h;
		offset = _o;
		fromRight = _fromRight;
	}
	
	public String toString() {
		return super.toString() + " (" + height + ")";
	}
}
