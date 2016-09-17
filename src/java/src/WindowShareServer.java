import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WindowShareServer implements Runnable {
	public static final int PORT = 5000;
	
	private ServerSocket sock;
	private HashMap<Socket, ClientThread> clientMap;
	private Vector<Socket> clients;
	
	public WindowShareServer() {
		// Create ServerSocket and start listening for client connections.
		try {
			sock = new ServerSocket(WindowShareServer.PORT);
		} catch (IOException e) {
			System.out.println("Unable to create server on port " + WindowShareServer.PORT);
			return;
		}
		System.out.println("Server running at " + sock.getInetAddress() + ":" + sock.getLocalPort());
		clients = new Vector<Socket>();
		clientMap = new HashMap<Socket, ClientThread>();
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				Socket client = sock.accept();
				clients.add(client);
				ClientThread thread = new ClientThread(client);
				new Thread(thread).start();
				clientMap.put(client, thread);
				System.out.println("Client connected! " + client.getInetAddress());
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
	
	public void send(String message, Socket client) {
		ClientThread thread = clientMap.get(client);
		if (thread == null) {
			throw new IllegalArgumentException("Client doesn't exist.");
		}
		thread.addToQueue(message);
	}
	
	private class ClientThread implements Runnable {
		static final int PAUSE_AMOUNT = 200; // ms
		BufferedReader in;
		PrintWriter out;
		
		Queue<String> sendQueue;
		
		public ClientThread(Socket sock) {
			try {
				this.in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				this.out = new PrintWriter(sock.getOutputStream(), true);
			} catch (IOException e) {
				System.out.println("Trouble establishing streams to client.");
			}
			sendQueue = new ConcurrentLinkedQueue<String>();
		}
		
		@Override
		public void run() {
			while (true) {
				/* Read message if available */
				try {
					String message = "";
					if ((message = in.readLine()) != null) {
						handleMessage(message);
						System.out.println("[SERVER] " + message);
					}
				} catch (IOException e) {
					System.out.println("Can't read from client");
				}
				/* Writer message if available */
				if (!sendQueue.isEmpty()) {
					out.write(sendQueue.remove());
					out.flush();
				}
				
				/* pause a little to avoid over processing */
				try {
					Thread.sleep(ClientThread.PAUSE_AMOUNT);
				} catch (InterruptedException e) {
					System.out.println("Could not sleep");
				}
			}
		}
		
		public void addToQueue(String message) {
			sendQueue.add(message);
		}
		
		public void handleMessage(String message) {
			// TODO: handle message sent from client
		}
	}
}
