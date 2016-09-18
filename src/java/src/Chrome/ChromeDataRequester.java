package Chrome;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class ChromeDataRequester {
	static final String fileLocation = "C:\\Users\\jnnnnnnnnna\\WindowShare\\";
	static final String flagFilename = "flag.txt";
	static final String resultFilename = "result.txt";
	static final String instructionsFilename = "instructions.txt";
	
	public static String[] getUrls() throws InterruptedException, FileNotFoundException {
		File flag = new File(ChromeDataRequester.fileLocation + ChromeDataRequester.flagFilename);
		flag.setLastModified(System.currentTimeMillis());
		
		File results = new File(ChromeDataRequester.fileLocation + ChromeDataRequester.flagFilename);
		while (!results.exists()) {
			Thread.sleep(250);
		}
		
		String st = "";
		Scanner s = new Scanner(results);
		while(s.hasNextLine()) {
			st += s.nextLine() + "\n";
		}
		s.close();
		
		results.delete();
		
		return st.split("\n");
	}
	
	public static void openUrls(String[] urls) throws IOException {
		File myFoo = new File("foo.log");
		FileOutputStream fooStream = new FileOutputStream(myFoo, false); // true to append
		                                                                 // false to overwrite.
		byte[] myBytes = String.join("\n", urls).getBytes(); 
		fooStream.write(myBytes);
		fooStream.close();
	}
}
