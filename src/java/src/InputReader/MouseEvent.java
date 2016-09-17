package InputReader;

public class MouseEvent {
	String type;
	
	void send() {
		System.out.println(this.toString());
	}
	
	public String toString() {
		return "mouseEvent: " + type;
	}
}
