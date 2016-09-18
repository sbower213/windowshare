package Native.Windows;

public class WinNativeTest {

	public static void main(String[] args) throws InterruptedException {
		while(true) {
			System.out.println("window dragged: " + WinDraggedWindowDetector.activeWindowIsDragged());
			Thread.sleep(200);
		}
	}

}
