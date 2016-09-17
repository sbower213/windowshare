package Networking;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import InputListener.InputListener;

public class WindowShareClient implements WindowShareNode {
	public static final String SERVER_IP = "127.0.0.1";
	
	private Socket sock;
	private BufferedReader in;
	private PrintWriter out;
	private Set<InputListener> listeners;
	
	public WindowShareClient(String serverip) {
		try {
			sock = new Socket(serverip, WindowShareServer.PORT);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream(), true);
			listeners = new HashSet<InputListener>();
			new Thread(new ServerThread()).start();
		} catch (UnknownHostException e) {
			System.out.println("Bad hostname");
		} catch (IOException e) {
			System.out.println("Could not connect to server.");
		}
	}
	
	public WindowShareClient() {
		this(WindowShareClient.SERVER_IP);
	}
	
	public void send(String data) {
		out.write(data + "\n");
		out.flush();
	}
	
	public void addListener(InputListener l) {
		listeners.add(l);
	}
	
	private class ServerThread implements Runnable {
		static final int PAUSE_AMOUNT = 200; // ms
		
		@Override
		public void run() {
			try {
				while (true) {
					/* Read message if available */
						String message = "";
						if ((message = in.readLine()) != null) {
							handleMessage(message);
							System.out.println("[Client] " + message);
						}
					/* pause a little to avoid over processing */
					Thread.sleep(ServerThread.PAUSE_AMOUNT);
				}
			} catch (IOException e) {
				System.out.println("Can't read from server. Disconnecting");
			} catch (InterruptedException e) {
				System.out.println("Could not sleep");
			}
		}
		
		public void handleMessage(String message) {
			for (InputListener l : listeners) {
				l.process(message);
			}
		}
	}
}
