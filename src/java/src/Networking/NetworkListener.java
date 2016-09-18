package Networking;

public interface NetworkListener<T> {
	public void process(T message);
}
