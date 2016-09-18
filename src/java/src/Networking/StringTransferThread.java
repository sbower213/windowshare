package Networking;

import java.io.IOException;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class StringTransferThread extends TransferThread<String> {

	@Override
	public void addSocket(Socket sock) {
		super.addSocket(sock);
		try {
			this.in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			this.out = new PrintWriter(sock.getOutputStream(), true);
		} catch (IOException e) {
			System.out.println("Trouble establishing streams to client.");
		}
	}
	
	@Override
	public void addToQueue(String message) {
		super.addToQueue(message + "\n");
	}
	
	@Override
	public String read() throws IOException {
		BufferedReader ins = (BufferedReader)in;		
		/* Read message if available */
		String message = ins.readLine();
		//System.out.println("message: " + message);
		return message;
	}
	
	@Override
	public void write(String message) throws IOException {
		PrintWriter outs = (PrintWriter)out;
		outs.write(message);
		outs.flush();
	}
}
