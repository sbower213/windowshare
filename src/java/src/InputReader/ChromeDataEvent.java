package InputReader;

public class ChromeDataEvent extends MouseEvent {
	public String[] urls;
	
	public ChromeDataEvent() {
		this.type = "chromeData";
		urls = new String[0];
	}
	
	public ChromeDataEvent(String[] u) {
		type = "chromeData";
		urls = u;
	}
}
