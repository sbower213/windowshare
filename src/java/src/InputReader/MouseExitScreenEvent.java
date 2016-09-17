package InputReader;

public class MouseExitScreenEvent extends MouseEvent {
	public double height;
	
	public MouseExitScreenEvent() {
		type = "leftOriginalScreen";
		height = 0;
	}
	
	public MouseExitScreenEvent(double _h, boolean original) {
		type = original ? "leftOriginalScreen" : "leftHostScreen";
		height = _h;
	}
	
	public String toString() {
		return super.toString() + " (" + height + ")";
	}
}
