package Native.Windows;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinUser.GUITHREADINFO;

public class WinDraggedWindowDetector {

	public static boolean activeWindowIsDragged() {
		GUITHREADINFO info=new GUITHREADINFO();
		info.cbSize=info.size();
		User32.INSTANCE.GetGUIThreadInfo(0, info);
		int flags = info.flags;
		HWND wnd = info.hwndMoveSize;
		RECT rect = new RECT();
		User32.INSTANCE.GetWindowRect(wnd, rect);
		System.out.println(rect);
		
		// GUI_INMOVESIZE 0x00000002    https://msdn.microsoft.com/en-us/library/ms632604(v=vs.85).aspx
		return ((flags & 0x00000002) == 0) ? false : true;
	}
	
	public static RECT activeWindowBounds() {
		GUITHREADINFO info=new GUITHREADINFO();
		info.cbSize=info.size();
		User32.INSTANCE.GetGUIThreadInfo(0, info);
		HWND wnd = info.hwndMoveSize;
		RECT rect = new RECT();
		User32.INSTANCE.GetWindowRect(wnd, rect);
		
		return rect;
	}
}
