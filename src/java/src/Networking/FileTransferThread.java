package Networking;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FileTransferThread extends TransferThread<File>{
	
	@Override
	public File read() throws IOException {
		InputStream in = sock.getInputStream();

		/* read filename */
        byte[] nameLenBytes = readExactly(in, 4);
        int nameLen = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).put(nameLenBytes).getInt(0);
        byte[] nameBytes = readExactly(in, nameLen);
        String name = new String(nameBytes);
        System.out.println("Name: " + name);
		
		byte[] sizeBytes = readExactly(in, 8);
		long size = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).put(sizeBytes).getLong(0);
		if (size == 0) {
			return null;
		}
		System.out.println("Size: " + size);
		byte[] imageBytes = readExactly(in, (int)size);
		File dir = new File(System.getProperty("java.io.tmpdir"));
		File file = new File(dir, name);
		System.out.println("Saving file to " + file.getAbsolutePath());
		FileOutputStream out = new FileOutputStream(file);
		out.write(imageBytes);
		out.close();
	    return file;
	}

	@Override
	public void write(File message) throws IOException {
		byte[] bytes = new byte[1024];
        InputStream in = new FileInputStream(message);
        String filename = message.getName();
        long length = message.length();
        byte[] lenBytes = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(length).array();

        OutputStream out = sock.getOutputStream();
        /* give filename */
        byte[] nameBytes = filename.getBytes();
        byte[] nameLenBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(nameBytes.length).array();
        out.write(nameLenBytes);
        out.write(nameBytes);
        out.flush();
        /* give file */
        System.out.println("Size: " + length);
        out.write(lenBytes);
        int count;
        while ((count = in.read(bytes)) > 0) {
            out.write(bytes, 0, count);
        }
        in.close();
	}

	private static byte[] readExactly(InputStream input, int size) throws IOException
	{
	    byte[] data = new byte[size];
	    byte[] chunk = new byte[Math.min(1024, size)];
	    int bytesRead = 0;
	    int off = 0;
	    while (off < size && (bytesRead = input.read(chunk)) >= 0)
	    {
	    	System.out.println(bytesRead);
	        System.arraycopy(chunk, 0, data, off, bytesRead);
	        off += bytesRead;
	    }
	    return data;
	}
}
