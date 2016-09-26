package Native.Windows;

// Based on http://www.rgagnon.com/javadetails/java-0653.html

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class SpecialFolderRecent {

  public static String SF_RECENT = "Recent";
  
  public static String[] recentFilenames() {
	  String path = getSpecialFolder(SF_RECENT);
	  
	  File folder = new File(path);
	  File[] listOfFiles = folder.listFiles();
	  String[] files = new String[listOfFiles.length];
	  
      for (int i = 0; i < listOfFiles.length; i++) {
    	  files[i] = listOfFiles[i].getPath();
        /*if (listOfFiles[i].isFile()) {]
          //System.out.println("File " + listOfFiles[i].getName());
        } else if (listOfFiles[i].isDirectory()) {
          //System.out.println("Directory " + listOfFiles[i].getName());
          // Do nothing, we don't need to look in folders
        }*/
      }
      
      return files;
  }

  public static String getSpecialFolder(String folder) {
    String result = "";
    try {
        File file = File.createTempFile("realhowto",".vbs");
        file.deleteOnExit();
        FileWriter fw = new java.io.FileWriter(file);

        String vbs = "Set WshShell = WScript.CreateObject(\"WScript.Shell\")\n"
                     + "wscript.echo WshShell.SpecialFolders(\"" + folder + "\")\n"
                     + "Set WSHShell = Nothing\n";

        fw.write(vbs);
        fw.close();
        Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
        BufferedReader input =
            new BufferedReader
              (new InputStreamReader(p.getInputStream()));
        result = input.readLine();
        input.close();
    }
    catch(Exception e){
        e.printStackTrace();
    }
    return result;
  }
  
  public static void main(String[] args) {
	  recentFilenames();
  }
}