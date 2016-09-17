package InputReader;

public class MouseMoveEvent extends MouseEvent {
	public int dx, dy;
	
	public MouseMoveEvent(int _dx, int _dy) {
		type = "move";
		dx = _dx;
		dy = _dy;
	}
	
	public String toString() {
		return super.toString() + " (" + dx + "," + dy + ")";
	}
}
