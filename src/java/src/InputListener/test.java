package InputListener;

import com.google.gson.Gson;

import InputReader.MouseEvent;

public class test {

	public static void main(String[] args) {
		Gson g = new Gson();
		MouseEvent a = new MouseEvent();
		String j = g.toJson(a);
		MouseEvent b = g.fromJson(j, MouseEvent.class);
		System.out.println(b);
	}
}
