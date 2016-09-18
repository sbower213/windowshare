package InputReader;

public class WindowAckEvent extends MouseEvent {
	String filepath;
	
	public WindowAckEvent() {
		type = "windowAck";
		filepath = null;
	}
	
	public WindowAckEvent(String path) {
		filepath = path;
	}
}
