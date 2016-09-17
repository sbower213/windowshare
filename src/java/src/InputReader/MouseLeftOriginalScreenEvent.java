package InputReader;

public class MouseLeftOriginalScreenEvent extends MouseEvent {
	public int height;
	
	public MouseLeftOriginalScreenEvent() {
		type = "leftOriginalScreen";
		height = 0;
	}
	
	public MouseLeftOriginalScreenEvent(int _h) {
		type = "leftOriginalScreen";
		height = _h;
	}
	
	public String toString() {
		return super.toString() + " (" + height + ")";
	}
}
