package Networking;

import Networking.TransferThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class WindowShareServer<T> implements Runnable, WindowShareNode<T> {
	public static final int PORT = 5000;
	public static final int FILE_PORT = PORT + 1;
	
	private ServerSocket sock;
	private HashMap<Socket, TransferThread<T>> clientMap;
	private Vector<Socket> clients;
	private Set<NetworkListener<T>> listeners;
	private Class<?> clientThreadClass;
	
	public WindowShareServer(int port, Class<?> clientThreadClass) {
		// Create ServerSocket and start listening for client connections.
		try {
			sock = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("Unable to create server on port " + port);
			return;
		}
		System.out.println("Server running at " + sock.getInetAddress() + ":" + sock.getLocalPort());
		clients = new Vector<Socket>();
		clientMap = new HashMap<Socket, TransferThread<T>>();
		listeners = new HashSet<NetworkListener<T>>();
		this.clientThreadClass = clientThreadClass;
	}
	
	public WindowShareServer() {
		this(PORT, StringTransferThread.class);
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				Socket client = sock.accept();
				clients.add(client);
				TransferThread<T> thread = (TransferThread<T>) clientThreadClass.newInstance();
				thread.addSocket(client);
				thread.addListeners(listeners);
				new Thread(thread).start();
				clientMap.put(client, thread);
				System.out.println("Client connected! " + client.getInetAddress());
			} catch (IOException e) {
				System.out.println("Trouble connecting to client.");
			} catch (Exception e) {
				System.out.println("Could not instantiate thread class");
			}
		}
	}
	
	public Socket[] getClients() {
		Socket[] clients = new Socket[this.clients.size()];
		this.clients.copyInto(clients);
		return clients;
	}
	
	public void send(T message, Socket client) {
		TransferThread<T> thread = clientMap.get(client);
		if (thread == null) {
			throw new IllegalArgumentException("Client doesn't exist.");
		}
		thread.addToQueue(message);
	}
	
	public void send(T message) {
		send(message, this.clients.get(0));
	}
	
	public void addListener(NetworkListener<T> l) {
		listeners.add(l);
	}	
}
