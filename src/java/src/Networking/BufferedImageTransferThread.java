package Networking;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class BufferedImageTransferThread extends TransferThread<BufferedImage> {

	@Override
	public BufferedImage read() throws IOException {
		return ImageIO.read(sock.getInputStream());
	}

	@Override
	public void write(BufferedImage message) throws IOException {
		ImageIO.write(message, "PNG", sock.getOutputStream());
		
	}

}
