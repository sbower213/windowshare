
public class WindowShareServerDriver {
	public static void main(String[] args) {
		WindowShareServer server = new WindowShareServer();
		Thread t = new Thread(server);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
