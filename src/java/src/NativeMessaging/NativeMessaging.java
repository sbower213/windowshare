package NativeMessaging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Queue;

import com.google.gson.Gson;

public class NativeMessaging {

	public NativeMessaging(){
		new Thread(() -> {try {
			this.receiveMessage();
		} catch (IOException e) {
			e.printStackTrace();
		}});
	}
	
	public void sendMessage(String message) throws FileNotFoundException{
		
		Gson g = new Gson();
		String json = g.toJson(message);
		byte[] msg_stream = json.getBytes();

		byte[] lenBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(msg_stream.length).array();
		
		File f = new File("C:\\Users\\Steven\\Documents\\workspace\\temp.txt");
		PrintWriter p = new PrintWriter(f);
		p.write(json.toCharArray());
		p.flush();
		p.close();
		
		try {
			System.out.write(lenBytes);
			System.out.write(msg_stream);
			System.out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void receiveMessage() throws IOException{

		while(true){
			byte[] lengthByte = new byte[4];
			int bytes_read = System.in.read(lengthByte,0,4);

			//for little endian, replace with java.nio.ByteBuffer.wrap(lengthByte).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
			int text_length = java.nio.ByteBuffer.wrap(lengthByte).getInt();

			byte[] messageByte = new byte[text_length];
			int lengthAppMessage = System.in.read(messageByte,0,text_length);
			
			String message = messageByte.toString();
			
			processMessage(message);
		}

	}
	
	public void processMessage(String msg) throws FileNotFoundException{
		sendMessage(msg);
	}

	public static void main(String[] args) throws IOException{
		String message = "{\"msg\":\"hello\"}";
		NativeMessaging nm = new NativeMessaging();
		
		int returnedMessageLength = 10;
		
		while(true){

		System.out.write((byte) (returnedMessageLength));
		System.out.write((byte)0);
		System.out.write((byte)0);
		System.out.write((byte)0);
		
		System.out.append('{');
		System.out.append('"');
		System.out.append('m');
		System.out.append('"');
		System.out.append(':');
		System.out.append('"');
		System.out.append('h');
		System.out.append('i');
		System.out.append('"');
		System.out.append('}');  
		
		}
		
		//nm.sendMessage(message);
		//while(true){
			//hi!
		//}
	}
}
