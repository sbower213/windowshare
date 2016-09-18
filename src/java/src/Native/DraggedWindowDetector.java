package Native;

import java.awt.Desktop;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import Native.OsCheck.OSType;
import Native.Windows.SpecialFolderRecent;
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
	
	public static String executableNameForActiveWindow() {
		String path = WinDraggedWindowDetector.activeWindowProcessName();
		
		return path.substring(path.lastIndexOf("\\" + 1), path.lastIndexOf("."));
	}
	
	public static String filepathForActiveWindow() {
		if (OsCheck.getOperatingSystemType() == OSType.Windows) {
			String[] filepaths = SpecialFolderRecent.recentFilenames();
			String title = WinDraggedWindowDetector.activeWindowTitle();
			for (String f : filepaths) {
				System.out.println(f);
				int end = f.lastIndexOf(".");
				if (end < 0) {
					end = f.length();
				}
				System.out.println(f.substring(f.lastIndexOf("\\" + 1), end));
				if (title.contains(f.substring(f.lastIndexOf("\\" + 1), end))) {
					System.out.println("found " + f);
					return f;
				}
			}
			return null;
		} else {
			throw new UnsupportedOperationException();
		}
	}
	
	public static String closestExecutableTo(String exeName) {
		String[] paths = new String[0];
		if (OsCheck.getOperatingSystemType() == OSType.Windows) {
			File folder1 = new File("C:\\Program Files");
			File[] files1 = folder1.listFiles();
			File folder2  = new File("C:\\Program Files (x86)");
			File[] files2 = folder2.listFiles();
			
			paths = new String[files1.length + files2.length];
			
			for (int i = 0; i < files1.length; i++) {
				paths[i] = files1[i].getPath();
			}
			for (int i = 0; i < files2.length; i++) {
				paths[i + files1.length] = files2[i].getPath();
			}
		} else if (OsCheck.getOperatingSystemType() == OSType.MacOS) {
			File folder = new File("/Applications");
			File[] files = folder.listFiles();
			
			paths = new String[files.length];
			
			for (int i = 0;  i < paths.length; i++) {
				paths[i] = files[i].getPath();
			}
		}
		
		for (int i = 0; i < paths.length; i++) {
			System.out.println(paths[i]);
			int end = paths[i].lastIndexOf(".");
			if (end < 0) {
				end = paths[i].length();
			}
			if (paths[i].substring(Math.max(paths[i].lastIndexOf("/"), paths[i].lastIndexOf("\\")) + 1,
					end).equalsIgnoreCase(exeName)) {
				System.out.println("Choosing path: " + paths[i]);
				return paths[i];
			}
		}
		
		return null;
	}
	
	public static void openFile(String exeName, String filePath) throws IOException {
		if (filePath == null) {
			String closestExe = closestExecutableTo(exeName);
			if (closestExe == null) {
				return;
			} else {
				if (OsCheck.getOperatingSystemType() == OSType.Windows) {
					Runtime.getRuntime().exec(closestExe);
				} else if (OsCheck.getOperatingSystemType() == OSType.MacOS) {
					Desktop.getDesktop().open(new File(closestExe));
				}
			}
		} else {
			String closestExe = closestExecutableTo(exeName);
			if (closestExe == null) {
				Desktop.getDesktop().open(new File(filePath));
			} else {
				if (OsCheck.getOperatingSystemType() == OSType.Windows) {
					Runtime.getRuntime().exec(closestExe + " \"" + filePath + "\"");
				} else {
					Runtime.getRuntime().exec("open -a " + closestExe + " \"" + filePath + "\"");
				}
			}
		}
	}
}
