package Networking;

public class WindowShareServerDriver {
	public static void main(String[] args) {
		WindowShareServer<String> server = new WindowShareServer<String>();
		Thread t = new Thread(server);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
