package InputReader;

public class MouseExitScreenEvent extends MouseEvent {
	public double height;
	public double offset;
	public boolean fromRight;
	public double startOffset;
	
	public MouseExitScreenEvent() {
		type = "leftOriginalScreen";
		height = 0;
		fromRight = false;
	}
	
	public MouseExitScreenEvent(double _h, double _o, boolean original, boolean _fromRight) {
		type = original ? "leftOriginalScreen" : "leftHostScreen";
		height = _h;
		offset = _o;
		startOffset = 0;
		fromRight = _fromRight;
	}
	
	public MouseExitScreenEvent(double _so, double _h,  double _o, boolean original, boolean _fromRight) {
		type = original ? "leftOriginalScreen" : "leftHostScreen";
		height = _h;
		offset = _o;
		startOffset = _so;
		fromRight = _fromRight;
	}
	
	public String toString() {
		return super.toString() + " (" + height + ")";
	}
}
