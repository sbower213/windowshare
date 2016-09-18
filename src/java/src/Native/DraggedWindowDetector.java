package Native;

import java.awt.Rectangle;

import Native.OsCheck.OSType;
import Native.Windows.WinDraggedWindowDetector;

public class DraggedWindowDetector {

	public static boolean activeWindowIsDragged() {
		if (OsCheck.getOperatingSystemType() == OSType.Windows) {
			return WinDraggedWindowDetector.activeWindowIsDragged();
		} else {
			throw new UnsupportedOperationException();
		}
	}
	
	public static Rectangle activeWindowBounds() {
		if (OsCheck.getOperatingSystemType() == OSType.Windows) {
			return WinDraggedWindowDetector.activeWindowBounds();
		} else {
			throw new UnsupportedOperationException();
		}
	}
}
