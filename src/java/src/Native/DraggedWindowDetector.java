package Native;

import java.awt.Desktop;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import Native.OsCheck.OSType;
import Native.OSX.OSXDraggedWindowDetector;
import Native.Windows.SpecialFolderRecent;
import Native.Windows.WinDraggedWindowDetector;
import Native.Windows.WindowsShortcut;

public class DraggedWindowDetector {

	public static boolean activeWindowIsDragged() {
		if (OsCheck.getOperatingSystemType() == OSType.Windows) {
			return WinDraggedWindowDetector.activeWindowIsDragged();
		} else {
			return OSXDraggedWindowDetector.activeWindowIsDragged();
		}
	}
	
	public static Rectangle activeWindowBounds() {
		if (OsCheck.getOperatingSystemType() == OSType.Windows) {
			return WinDraggedWindowDetector.activeWindowBounds();
		} else {
			return OSXDraggedWindowDetector.activeWindowBounds();
		}
	}
	
	public static String executableNameForActiveWindow() {
		if (OsCheck.getOperatingSystemType() == OSType.Windows) {
			String path = WinDraggedWindowDetector.activeWindowProcessName();

			int end = path.lastIndexOf(".");
			if (end < 0) {
				end = path.length();
			}
			return path.substring(path.lastIndexOf("\\") + 1, end);
		} else {
			return OSXDraggedWindowDetector.activeWindowProcessName();
		}
	}
	
	public static String filepathForActiveWindow() throws IOException, ParseException {
		if (OsCheck.getOperatingSystemType() == OSType.Windows) {
			String[] filepaths = SpecialFolderRecent.recentFilenames();
			String title = WinDraggedWindowDetector.activeWindowTitle();
			String best = null;
			int match = 0;
			for (String f : filepaths) {
				System.out.println(f);
				int end = f.lastIndexOf(".");
				if (end < 0) {
					end = f.length();
				}
				System.out.println(f.substring(f.lastIndexOf("\\") + 1, end));
				if (title.contains(f.substring(f.lastIndexOf("\\") + 1, end))) {
					if (end - f.lastIndexOf("\\") + 1 > match) {
						best = f;
						match = end - f.lastIndexOf("\\") + 1;
					}
				}
			}

			if (best != null) {
				System.out.println("found " + best);
				return (new WindowsShortcut(new File(best))).getRealFilename();
			}
			return null;
		} else {
			return OSXDraggedWindowDetector.activeWindowFilePath();
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
		System.out.println("opening " + filePath + ", exe: " + exeName);
		if (filePath == null) {
			String closestExe = closestExecutableTo(exeName);
			if (closestExe == null) {
				return;
			} else {
				if (OsCheck.getOperatingSystemType() == OSType.Windows) {
					Runtime.getRuntime().exec(closestExe);
					System.out.println("windows");
				} else if (OsCheck.getOperatingSystemType() == OSType.MacOS) {
					System.out.println("mac");
					Desktop.getDesktop().open(new File(closestExe));
				}
			}
		} else {
			String closestExe = closestExecutableTo(exeName);
			if (closestExe == null) {
				System.out.println("no exe");
				Desktop.getDesktop().open(new File(filePath));
			} else {
				if (OsCheck.getOperatingSystemType() == OSType.Windows) {
					System.out.println("windows");
					Runtime.getRuntime().exec(closestExe + " \"" + filePath + "\"");
				} else {
					System.out.println("other os");
					Runtime.getRuntime().exec("open -a " + closestExe + " \"" + filePath + "\"");
				}
			}
		}
	}
}
