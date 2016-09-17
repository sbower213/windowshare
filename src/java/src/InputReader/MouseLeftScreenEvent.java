package InputReader;

public class MouseLeftScreenEvent extends MouseEvent {
	public double height;
	
	public MouseLeftScreenEvent() {
		type = "leftOriginalScreen";
		height = 0;
	}
	
	public MouseLeftScreenEvent(double _h, boolean original) {
		type = original ? "leftOriginalScreen" : "leftHostScreen";
		height = _h;
	}
	
	public String toString() {
		return super.toString() + " (" + height + ")";
	}
}
