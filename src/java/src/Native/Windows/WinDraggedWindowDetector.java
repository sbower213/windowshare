package Native.Windows;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser.GUITHREADINFO;

public class WinDraggedWindowDetector {

	public static boolean activeWindowIsDragged() {
		GUITHREADINFO info=new GUITHREADINFO();
		info.cbSize=info.size();
		User32.INSTANCE.GetGUIThreadInfo(0, info);
		int flags = info.flags;
		
		// GUI_INMOVESIZE 0x00000002    https://msdn.microsoft.com/en-us/library/ms632604(v=vs.85).aspx
		return ((flags & 0x00000002) == 0) ? false : true;
	}
}
