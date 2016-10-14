package InputReader;
import org.jnativehook.NativeInputEvent;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import com.google.gson.Gson;

import Chrome.ChromeDataRequester;
import Native.DraggedWindowDetector;
import Native.OSX.OSXDraggedWindowDetector;
import Networking.NetworkListener;
import Networking.WindowShareNode;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;

public class MouseMotionReader implements NativeMouseInputListener, NetworkListener<String> {
	static final boolean hasChromeExtension = false;
	
	int width, height;
	int transferX, transferY;
	Robot robot;
	boolean waiting;
	boolean mouseOffscreen;
	boolean justJumped;
	public WindowShareNode<File> fileTransfer;
	public WindowShareNode<BufferedImage> imageTransfer;
	String lastFilepath;
	boolean applicationSent;
	
	public MouseMotionReader() throws AWTException {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int)screenSize.getWidth();
		height = (int)screenSize.getHeight();
		waiting = false;
		robot = new Robot();
		mouseOffscreen = false;
		applicationSent = false;
	}
	
	@Override
	public void nativeMouseClicked(NativeMouseEvent e) {
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent e) {
		if (mouseOffscreen) {
			int b = e.getButton();
			int buttons = b == NativeMouseEvent.BUTTON1 ? InputEvent.BUTTON1_DOWN_MASK :
				(b == NativeMouseEvent.BUTTON2 ? InputEvent.BUTTON2_DOWN_MASK : InputEvent.BUTTON3_DOWN_MASK);
			(new MouseUpDownEvent(true, buttons)).send();
			
            Field f;
			try {
				f = NativeInputEvent.class.getDeclaredField("reserved");
	            f.setAccessible(true);
	            f.setShort(e, (short) 0x01);
			} catch (Exception e1) {
			}
		}
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent e) {
		if (mouseOffscreen) {
			int b = e.getButton();
			int buttons = b == NativeMouseEvent.BUTTON1 ? InputEvent.BUTTON1_DOWN_MASK :
				(b == NativeMouseEvent.BUTTON2 ? InputEvent.BUTTON2_DOWN_MASK : InputEvent.BUTTON3_DOWN_MASK);
			(new MouseUpDownEvent(false, buttons)).send();
			
			Field f;
			try {
				f = NativeInputEvent.class.getDeclaredField("reserved");
	            f.setAccessible(true);
	            f.setShort(e, (short) 0x01);
			} catch (Exception e1) {
			}
		} else {
			System.out.println("cancelling due to release");
			applicationSent = false;
		}
	}

	@Override
	public void nativeMouseDragged(NativeMouseEvent e) {
		//System.out.println(DraggedWindowDetector.activeWindowIsDragged());
		if (mouseOffscreen) {
			int dx = e.getX() - width/2;
			int dy = e.getY() - height/2;
			
			if (!waiting) {
				waitAndSend(dx, dy);
			}
		} else if (e.getX() <= 0) {
			try {
				leaveScreen(e.getY(), false);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.getX() >= width) {
			try {
				leaveScreen(e.getY(), true);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			if (DraggedWindowDetector.activeWindowIsDragged()) {
				Rectangle windowBounds = DraggedWindowDetector.activeWindowBounds();
				
				if (!applicationSent) {
					if (windowBounds.getX() <= 0) {
						try {
							sendApplication(-e.getX(), e.getY(), false);
							applicationSent = true;
							transferX = e.getX();
							transferY = e.getY();
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else if (windowBounds.getMaxX() >= width) {
						try {
							sendApplication(-(width - e.getX()), e.getY(), true);
							applicationSent = true;
							transferX = e.getX();
							transferY = e.getY();
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				} else if (windowBounds.getX() > 0 && windowBounds.getMaxX() < width) {
					System.out.println("cancelling");
					applicationSent = false;
				}
			}
		}
		
		if (!mouseOffscreen && applicationSent) {
			int dx = e.getX() - transferX;
			int dy = e.getY() - transferY;
			
			if (!waiting) {
				waitAndSend(dx, dy);
			}
		}
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent e) {
		////System.out.println(OSXDraggedWindowDetector.activeWindowBounds());
		if (mouseOffscreen) {
			int dx = e.getX() - width/2;
			int dy = e.getY() - height/2;
			
			if (!waiting) {
				waitAndSend(dx, dy);
			}
		} else if (e.getX() <= 0) {
			try {
				leaveScreen(e.getY(), false);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.getX() >= width) {
			try {
				leaveScreen(e.getY(), true);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public void captureMouse() {
		if (!justJumped) {
			mouseOffscreen = true;
			robot.mouseMove(width/2, height/2);
		}
	}
	
	public void leaveScreen(int h, boolean fromRight) throws FileNotFoundException, InterruptedException {
		if (!applicationSent) {
			sendApplication(0, h, fromRight);
		}
		captureMouse();
	}
	
	public void sendApplication(int startOffset, int h, boolean fromRight) throws FileNotFoundException, InterruptedException {
		if (!justJumped) {
			System.out.println("Send application");
			(new MouseExitScreenEvent(startOffset, (1.0 * h) / height, 0, true, fromRight)).send();
			
			if (DraggedWindowDetector.activeWindowIsDragged()) {
				//System.out.println("ACTIVE WINDOW IS DRAGGED");
				String executableName = DraggedWindowDetector.executableNameForActiveWindow();
				//System.out.println("Executable: " + executableName);
				String filepath = null;
				try {
					filepath = DraggedWindowDetector.filepathForActiveWindow();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//System.out.println("Filepath: " + filepath);
				WindowDraggedEvent e = new WindowDraggedEvent(executableName, filepath);
				e.send();
				
				if (hasChromeExtension && executableName.equalsIgnoreCase("chrome")) {
					String[] chromeUrls = ChromeDataRequester.getUrls();
					ChromeDataEvent cde = new ChromeDataEvent(chromeUrls);
					cde.send();
				}
				
				BufferedImage i = robot.createScreenCapture(DraggedWindowDetector.activeWindowBounds());
				imageTransfer.send(i);
				
				lastFilepath = filepath;
			} else {
				//System.out.println("ACTIVE WINDOW IS NOT DRAGGED");
			}
		}
	}
	
	public void waitAndSend(int dx, int dy) {
		new Thread(() -> {
			waiting = true;
			try {
				Thread.sleep(33);
				MouseMoveEvent mme = new MouseMoveEvent((dx * 1.0f) / width, (dy * 1.0f) / height);
				if (mme.dx != 0 || mme.dy != 0) {
					mme.send();
				} else {
					System.out.println("movement 0");
				}
				if (mouseOffscreen) {
					robot.mouseMove(width/2, height/2);
				} else {
					transferX += dx;
					transferY += dy;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}		// Roughly 1/30 seconds
			waiting = false;
		}).start();
	}


	@Override
	public void process(String message) {
		Gson gson = new Gson();
		MouseEvent e = gson.fromJson(message, MouseEvent.class);

		//System.out.println("processing an event: " + e);
		if (e.type.equals("leftHostScreen")) {
			//System.out.println("mouse control is back");
			MouseExitScreenEvent mlse = gson.fromJson(message, MouseExitScreenEvent.class);
			mouseOffscreen = false;
			applicationSent = false;
			robot.mouseMove(!mlse.fromRight ? 10 : width - 10, (int) (mlse.height * height));
			
			justJumped = true;
			Thread t = new Thread(() -> {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				justJumped = false;
			});
			t.start();
		} else if (e.type.equals("windowAck")) {
			WindowAckEvent wae = gson.fromJson(message, WindowAckEvent.class);
			System.out.println("EVENT OCCURED: " + wae);
			if (lastFilepath != null) {
				File f = new File(lastFilepath);
				fileTransfer.send(f);
				System.out.println("sent file " + lastFilepath);
			}
		}
	}
}
