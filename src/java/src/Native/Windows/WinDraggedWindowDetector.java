package Native.Windows;

import java.awt.Rectangle;

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
	
	public static Rectangle activeWindowBounds() {
		GUITHREADINFO info=new GUITHREADINFO();
		info.cbSize=info.size();
		User32.INSTANCE.GetGUIThreadInfo(0, info);
		HWND wnd = info.hwndActive;
		RECT rect = new RECT();
		User32.INSTANCE.GetWindowRect(wnd, rect);
		
		Rectangle r = new Rectangle(rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top);
		
		return r;
	}
	
	public static String activeWindowTitle() {
		GUITHREADINFO info=new GUITHREADINFO();
		info.cbSize=info.size();
		User32.INSTANCE.GetGUIThreadInfo(0, info);
		HWND wnd = info.hwndActive;
		
		int titleLength = User32.INSTANCE.GetWindowTextLength(wnd);
		char[] titleArr = new char[titleLength];
		User32.INSTANCE.GetWindowText(wnd, titleArr, titleLength);
		
		return new String(titleArr);
	}
}
