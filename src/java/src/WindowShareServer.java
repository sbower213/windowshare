import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class WindowShareServer implements Runnable {
	public static int PORT = 5000;
	
	private ServerSocket sock;
	private Vector<Socket> clients;
	
	public WindowShareServer() {
		// Create ServerSocket and start listening for client connections.
		try {
			sock = new ServerSocket(WindowShareServer.PORT);
		} catch (IOException e) {
			System.out.println("Unable to create server on port " + WindowShareServer.PORT);
		}
		System.out.println("Server running at " + sock.getInetAddress() + ":" + sock.getLocalPort());
		clients = new Vector<Socket>();
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				Socket client = sock.accept();
				clients.add(client);
			} catch (IOException e) {
				System.out.println("Trouble connecting to client.");
			}
		}
	}
	
	public Socket[] getClients() {
		Socket[] clients = new Socket[this.clients.size()];
		this.clients.copyInto(clients);
		return clients;
	}
}
