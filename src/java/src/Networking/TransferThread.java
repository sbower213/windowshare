package Networking;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class TransferThread<T> implements Runnable {
	private boolean shouldDie = false;
	static final int PAUSE_AMOUNT = 10; // ms
	Socket sock;
	
	Reader in;
	Writer out;
	
	Thread readThread;
	Thread writeThread;
	
	Queue<T> sendQueue;
	Set<NetworkListener<T>> listeners;
	
	public TransferThread() {
		sendQueue = new ConcurrentLinkedQueue<T>();
		readThread = new Thread(new ReadThread());
		writeThread = new Thread(new WriteThread());
	}
	
	/* Call addSocket and addListeners before run */
	
	public void addSocket(Socket sock) {
		this.sock = sock;
	}
	
	public void addListeners(Set<NetworkListener<T>> listeners) {
		this.listeners = listeners;
	}
	
	abstract public T read() throws IOException;
	abstract public void write(T message) throws IOException;
	
	@Override
	public void run() {
		readThread.start();
		writeThread.start();
		try {
			readThread.join();
			writeThread.join();
		} catch (InterruptedException e) {
			System.out.println("Could not wait for threads to finish.");
		}
	}
	
	public void addToQueue(T message) {
		sendQueue.add(message);
	}
	
	public void handleMessage(T message) {
		for (NetworkListener<T> l : listeners) {
			l.process(message);
		}
	}
	
	private class ReadThread implements Runnable {
		@Override
		public void run() {
			try {
				while (!shouldDie) {
					T message = read();
					if (message == null) {
						System.out.println("Message is null");
						throw new IOException();
					}
					handleMessage(message);
				}
			} catch (IOException e) {
				System.out.println("Connection Broken. Disconnecting.");
			}
			try {
				sock.close();
			} catch(IOException e) {
				System.out.println("You are hopeless");
			}
			shouldDie = true;
		}
	}
	
	private class WriteThread implements Runnable {
		@Override
		public void run() {
			try {
				while (!shouldDie) {
					if (!sendQueue.isEmpty()) {
						write(sendQueue.remove());
					} else {
						Thread.sleep(PAUSE_AMOUNT);
					}
				}
			} catch (IOException e) {
				System.out.println("Connection Broken. Disconnecting.");
			} catch (InterruptedException e) {
				System.out.println("Could not sleep.");
			}
			try {
				sock.close();
			} catch(IOException e) {
				System.out.println("You are hopeless");
			}
			shouldDie = true;
		}
	}
}
