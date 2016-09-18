package Native.Windows;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class HandleFileReader {

	public static void main(String[] args) throws IOException, InterruptedException {
		//String command =  System.getProperty("user.dir")+"\\handle.exe |findstr /i";
		String command =  System.getProperty("user.dir")+"\\handle.exe";
		System.out.println(java.io.File.listRoots());
		String root = "";
		for(File thing : java.io.File.listRoots()) {
			System.out.println(thing.toString());
			if(command.contains(thing.toString())) {
				root = thing.toString();
				break;
			}
		}
		System.out.println(root);
		//command += " " + root;
		System.out.println(command);
		Process p = Runtime.getRuntime().exec(command);
		BufferedReader bf = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = null;
        while ((line = bf.readLine()) != null) {
            System.out.println(line);
        }
		p.waitFor();
		
		
	       System.out.println(
	              System.getProperty("user.dir")+"\\handle.exe");
	       
	  }

}
