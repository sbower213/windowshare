package Networking;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

public class WindowShareClient<T> implements WindowShareNode<T>, Runnable {
	public static final String SERVER_IP = "18.22.8.46";
	
	private Socket sock;
	private Set<NetworkListener<T>> listeners;
	private TransferThread<T> transferThread;
	
	public WindowShareClient(String serverip, int port, Class<?> threadClass) {
		try {
			sock = new Socket(serverip, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		listeners = new HashSet<NetworkListener<T>>();
		try {
			this.transferThread = (TransferThread<T>) threadClass.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		transferThread.addSocket(sock);
		transferThread.addListeners(listeners);
	}
	
	public WindowShareClient() {
		this(WindowShareClient.SERVER_IP, WindowShareServer.PORT, StringTransferThread.class);
	}
	
	public void send(T message) {
		transferThread.addToQueue(message);
	}
	
	public void addListener(NetworkListener<T> l) {
		listeners.add(l);
	}

	@Override
	public void run() {
		transferThread.run();
	}
}
