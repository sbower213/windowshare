package FileTransferer;

import java.io.File;

import Networking.FileTransferThread;
import Networking.WindowShareClient;
import Networking.WindowShareServer;

public class Transfer {
	public static void main(String[] args) throws InterruptedException {
		WindowShareClient<File> c = new WindowShareClient<File>("127.0.0.1",
				WindowShareServer.FILE_PORT, FileTransferThread.class);
		
		Thread t = new Thread(c);
		t.start();
		File toTransfer = new File("/Users/robbieadkins/Documents/Projects/windowshare/src/java/src/FileTransferer/Transfer.java");
        System.out.println("Exists? "  + toTransfer.exists());
		c.send(toTransfer);
		t.join();
	}
}
