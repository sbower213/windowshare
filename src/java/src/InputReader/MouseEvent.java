package InputReader;

import com.google.gson.Gson;

public class MouseEvent {
	String type;
	Gson gson = new Gson();
	
	void send() {
		System.out.println(this.toString());
		System.out.println(gson.toJson(this));
	}
	
	public String toString() {
		return "mouseEvent: " + type;
	}
}
