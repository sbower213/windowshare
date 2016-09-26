package Native.OSX;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class OSXDraggedWindowDetector {
	public static boolean activeWindowIsDragged() {
		System.out.println("Calling active window drag");
		MouseInfo.getPointerInfo().getLocation();
		Point mouse = MouseInfo.getPointerInfo().getLocation();
		Rectangle bounds = activeWindowBounds();
		boolean res =  bounds.contains(mouse);
		System.out.println("dragged: " + res);
		return res;
	}
	
	public static Rectangle activeWindowBounds() {
		System.out.println("Calling active window boudn");
		String script = "";
		script += "tell application \"System Events\"\n";
		script += "  set frontApp to name of first application process whose frontmost is true\n";
		script += "end tell\n";
		script += "tell application frontApp\n";
		script += "  set b to bounds of window 1\n";
		script += "end tell\n";
		script += "do shell script \"echo \" & quoted form of b";
		Runtime runtime = Runtime.getRuntime();
		String[] args = { "osascript", "-e", script};
		try {
			Process process = runtime.exec(args);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String raw = reader.readLine();
			System.out.println("raw: " + raw);
			String sub = raw.substring(1, raw.length() - 1);
			String[] nums = sub.split(",");
			Rectangle r = new Rectangle(Integer.parseInt(nums[0]), Integer.parseInt(nums[1]), Integer.parseInt(nums[2]), Integer.parseInt(nums[3]));
			System.out.println(r);
			return r;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static String activeWindowFilePath() {
		System.out.println("Calling active window path");
		String script = "";
		script += "try\n";
		script += "  tell application (path to frontmost application as text)\n";
		script += "    (path of document 1) as text\n";
		script += "  end tell\n";
		script += "on error\n";
		script += "  try\n";
		script += "    tell application \"System Events\" to tell (process 1 where frontmost is true)\n";
		script += "      value of attribute \"AXDocument\" of window 1\n";
		script += "    end tell\n";
		script += "    do shell script \"x=\" & quoted form of result & \"\n";
        script += "  x=${x/#file:\\\\/\\\\/}\n";
        script += "  x=${x/#localhost} # 10.8 and earlier\n";
        script += "  printf ${x//%/\\\\\\\\x}\"\n";
        script += "  end try\n";
        script += "end try";
        Runtime runtime = Runtime.getRuntime();
		String[] args = { "osascript", "-e", script};
		try {
			Process process = runtime.exec(args);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String res = reader.readLine();
			System.out.println("filepath: " + res);
			return res;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	
	public static String activeWindowProcessName() {
		System.out.println("Calling active window proc");
		String script = "tell application \"System Events\"\n set frontApp to name of first application process whose frontmost is true\n end tell\n frontApp";
		Runtime runtime = Runtime.getRuntime();
		String[] args = { "osascript", "-e", script};
		try {
			Process process = runtime.exec(args);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String res = reader.readLine();
			System.out.println("proc: " + res);
			return res;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
}