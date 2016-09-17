package InputReader;

import com.google.gson.Gson;

import Networking.WindowShareNode;

public class MouseEvent {
	static WindowShareNode network;
	
	String type;
	transient Gson gson;
	
	public MouseEvent() {
	}
	
	public MouseEvent(String t) {
		type = t;
	}
	
	void send() {
		if (gson == null) {
			gson = new Gson();
		}
		System.out.println(this.toString());
		System.out.println(gson.toJson(this));
		network.send(gson.toJson(this));
	}
	
	public String toString() {
		return "mouseEvent: " + type;
	}
}
