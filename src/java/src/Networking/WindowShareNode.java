package Networking;

import InputListener.InputListener;

public interface WindowShareNode {
	void send(String message);
	void addListener(NetworkListener l);
}
