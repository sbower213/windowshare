package Native.Windows;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.IntByReference;

public class HandleFileReader {

	public static void main(String[] args) throws IOException, InterruptedException {
		HWND hwnd = User32.INSTANCE.GetForegroundWindow();  
		IntByReference pId=new IntByReference();  
		User32.INSTANCE.GetWindowThreadProcessId(hwnd, pId);  
		int processId=pId.getValue();  
		
		String command =  System.getProperty("user.dir")+"\\handle.exe -p " + processId;
		
		System.out.println(command);
		Process p = Runtime.getRuntime().exec(command);
		BufferedReader bf = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = null;
        while ((line = bf.readLine()) != null) {
            System.out.println(line);
        }
		p.waitFor();
	  }

}
