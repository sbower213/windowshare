package FileTransferer;

import java.io.File;

import Networking.FileTransferThread;
import Networking.WindowShareServer;

public class Receive {
	public static void main(String[] args) throws InterruptedException {
		WindowShareServer<File> server = new WindowShareServer<File>
		(WindowShareServer.FILE_PORT, FileTransferThread.class);

		Thread t = new Thread(server);
		t.start();
		t.join();
	}
}
