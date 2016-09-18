package Networking;

public class WindowShareClientDriver {

	public static void main(String[] args) {
		WindowShareClient<String> client = new WindowShareClient<String>();
		client.send("Hello!");
	}
}
