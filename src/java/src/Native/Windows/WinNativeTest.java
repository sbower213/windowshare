package Native.Windows;

public class WinNativeTest {

	public static void main(String[] args) throws InterruptedException {
		while(true)
			System.out.println(WinDraggedWindowDetector.activeWindowProcessName());
	}

}
