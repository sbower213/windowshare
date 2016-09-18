package InputReader;

public class WindowDraggedEvent extends MouseEvent {
	public String executableName;
	public String filepath;
	
	public WindowDraggedEvent() {
		type = "windowDragged";
		executableName = null;
		filepath = null;
	}
	
	public WindowDraggedEvent(String exeName, String fp) {
		executableName = exeName;
		filepath = fp;
	}
	
	public String toString() {
		return super.toString() + " (" + executableName + ": " + filepath + ")";
	}
}
