package FileTransferer;

import java.io.File;

import Networking.FileTransferThread;
import Networking.WindowShareClient;
import Networking.WindowShareServer;

public class Transfer {
	public static void main(String[] args) throws InterruptedException {
		WindowShareClient<File> c = new WindowShareClient<File>(WindowShareClient.SERVER_IP,
				WindowShareServer.FILE_PORT, FileTransferThread.class);
		
		Thread t = new Thread(c);
		t.start();
		File toTransfer = new File("C:\\Users\\jnnnnnnnnnna\\Dropbox\\thumb_IMG_4198_1024.jpg");
        System.out.println("Exists? "  + toTransfer.exists());
		c.send(toTransfer);
		t.join();
	}
}
