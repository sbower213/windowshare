package InputReader;

public class MouseMoveEvent extends MouseEvent {
	public double dx, dy;
	
	public MouseMoveEvent() {
		type = "move";
		dx = 0;
		dy = 0;
	}
	
	public MouseMoveEvent(double _dx, double _dy) {
		type = "move";
		dx = _dx;
		dy = _dy;
		System.out.println("dx: " + _dx + ", dy: " + _dy);
	}
	
	public String toString() {
		return super.toString() + " (" + dx + "," + dy + ")";
	}
}
