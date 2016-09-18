package Networking;

public interface WindowShareNode<T> extends Runnable {
	void send(T message);
	void addListener(NetworkListener<T> l);
}
