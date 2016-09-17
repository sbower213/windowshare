package InputReader;

import com.google.gson.Gson;

public class MouseEvent {
	static WindowShareNode network;
	
	String type;
	Gson gson = new Gson();
	
	void send() {
		System.out.println(this.toString());
		System.out.println(gson.toJson(this));
		network.send(gson.toJson(this) + "\n");
	}
	
	public String toString() {
		return "mouseEvent: " + type;
	}
}
