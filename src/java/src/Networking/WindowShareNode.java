package Networking;

public interface WindowShareNode<T> {
	void send(T message);
	void addListener(NetworkListener<T> l);
}
