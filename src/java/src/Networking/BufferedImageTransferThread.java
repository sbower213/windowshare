package Networking;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;

public class BufferedImageTransferThread extends TransferThread<BufferedImage> {
	
	@Override
	public BufferedImage read() throws IOException {
		InputStream in = sock.getInputStream();
		byte[] sizeBytes = readExactly(in, 4);
		int size = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).put(sizeBytes).getInt();
		System.out.println("Size: " + size);
		byte[] imageBytes = readExactly(in, size);
		ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
	    try {
	        return ImageIO.read(bais);
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	}

	@Override
	public void write(BufferedImage message) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(message, "png", baos);
		byte[] bytes = baos.toByteArray();
		System.out.println("Size: " + bytes.length);
		byte[] lenBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(bytes.length).array();
		OutputStream out = sock.getOutputStream();
		out.write(lenBytes);
		out.write(bytes);
		out.flush();
	}
	
	private static byte[] readExactly(InputStream input, int size) throws IOException
	{
	    byte[] data = new byte[size];
	    int index = 0;
	    while (index < size)
	    {
	        int bytesRead = input.read(data, index, size - index);
	        if (bytesRead < 0)
	        {
	            throw new IOException("Insufficient data in stream");
	        }
	        index += size;
	    }
	    return data;
	}
}
