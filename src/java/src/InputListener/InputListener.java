package InputListener;

import InputReader.ChromeDataEvent;
import InputReader.MouseEvent;
import InputReader.MouseMoveEvent;
import InputReader.MouseUpDownEvent;
import InputReader.MouseExitScreenEvent;
import InputReader.WindowDraggedEvent;
import InputReader.WindowAckEvent;
import Native.DraggedWindowDetector;
import Networking.NetworkListener;
import Networking.WindowShareNode;
import ScreenDrawer.Draw;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.imageio.ImageIO;

import com.google.gson.Gson;

import Chrome.ChromeDataRequester;

public class InputListener {
	Robot robot;
	Gson gson;
	boolean remoteControl;
	int mouseX, mouseY;
	int origMouseX, origMouseY;
	Window cursorWindow;
	Draw draw;
	boolean mouseDown;
	WindowShareNode<String> stringTransfer;
	WindowShareNode<File> fileTransfer;
	WindowShareNode<BufferedImage> imageTransfer;
	BufferedImage cursor;
	BufferedImage spinner;
	File dataFile;
	boolean justJumped;
	String[] chromeUrls;
	
	Queue<MouseEvent> eventQueue;
	
	public InputListener() throws AWTException {
		robot = new Robot();
		gson = new Gson();
		remoteControl = false;
		eventQueue = new ConcurrentLinkedQueue<MouseEvent>();
		mouseDown = false;
		try {
			cursor = ImageIO.read(new File(getClass().getResource("cursor_win_hand.png").getPath()));
			spinner = ImageIO.read(new File(getClass().getResource("spinner.gif").getPath()));
			draw = new Draw();
			cursorWindow = draw.defineWindow(cursor, null);
			//cursorWindow.setSize(cursor.getWidth(), cursor.getHeight());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new Thread(new MouseEventHandler()).start();
	}
	
	public void setStringTransfer(WindowShareNode<String> n) {
		stringTransfer = n;
		stringTransfer.addListener(new StringListener());
	}
	
	public void setFileTransfer(WindowShareNode<File> n) {
		fileTransfer = n;
		fileTransfer.addListener(new FileListener());
	}
	
	public void setImageTransfer(WindowShareNode<BufferedImage> n) {
		imageTransfer = n;
		imageTransfer.addListener(new BufferedImageListener());
	}

	public class StringListener implements NetworkListener<String> {

		@Override
		public void process(String message) {
			MouseEvent e = gson.fromJson(message, MouseEvent.class);
			//System.out.println(e);
			if (e.type.equals("move")) {
				e = gson.fromJson(message, MouseMoveEvent.class);
			} else if (e.type.equals("click") || e.type.equals("release")) {
				e = gson.fromJson(message, MouseUpDownEvent.class);
			} else if (e.type.equals("leftOriginalScreen")) {
				e = gson.fromJson(message, MouseExitScreenEvent.class);
			} else if (e.type.equals("windowDragged")) {
				e = gson.fromJson(message, WindowDraggedEvent.class);
			}
			eventQueue.add(e);
			synchronized(eventQueue) {
				eventQueue.notify();
			}
		}

	}
	
	public class FileListener implements NetworkListener<File> {
		@Override
		public void process(File message) {
			dataFile = message;
		}
	}
	
	public class BufferedImageListener implements NetworkListener<BufferedImage> {
		@Override
		public void process(BufferedImage message) {
			draw.defineWindow(cursor, message);
		}
	}
	
	private class MouseEventHandler implements Runnable {
		static final int DELTA = 33; //ms
		
		String lastExecName = null;
		String lastPathName = null;
		
		@Override
		public void run() {
			while (true) {
				// wait for more input
				if (eventQueue.isEmpty()) {
					try {
						synchronized(eventQueue) {
							eventQueue.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				// read event and process it.
				MouseEvent event = eventQueue.remove();
				//System.out.println(event.type);
				if (event instanceof MouseMoveEvent) {
					if (!remoteControl) {
						/* only control mouse if it was transferred from original computer */
						continue;
					}
					// interpolate the current position with the new position.
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					int width = (int)screenSize.getWidth();
					int height = (int)screenSize.getHeight();
					
					MouseMoveEvent mme = (MouseMoveEvent)event;
					long startTime = System.currentTimeMillis();
					long curTime = startTime;
					int newX, newY;
					do {
						long deltaTime = curTime - startTime;
						float frac = (deltaTime * 1.0f) / MouseEventHandler.DELTA;
						int dx = (int) (mme.dx * frac * width);
						int dy = (int) (mme.dy * frac * height);
						newX = Math.min(Math.max(0, mouseX + dx), width);
						newY = Math.min(Math.max(0, mouseY + dy), height);
						//System.out.println(newX + ", " + newY);
						if (!justJumped && newX >= width || newX <= 0) {
							MouseExitScreenEvent e = new MouseExitScreenEvent((1.0 * newY) / height, false, newX <= 0);
							e.send();
							remoteControl = false;
							cursorWindow.setVisible(false);
							mouseX = width - 10;
							mouseY =  newY;
							break;
						}
						cursorWindow.setLocation(newX, newY);
						if (mouseDown) {
							robot.mouseMove(newX, newY);
						}
						curTime = System.currentTimeMillis();
					} while (curTime - startTime < MouseEventHandler.DELTA);
					mouseX = newX;
					mouseY = newY;
				} else if (event instanceof MouseUpDownEvent) {
					if (event.type.equals("click")) {
						Point start = MouseInfo.getPointerInfo().getLocation();
						origMouseX = start.x;
						origMouseY = start.y;
						robot.mouseMove(mouseX, mouseY);
						cursorWindow.setVisible(false);
						try {
							synchronized(robot) {
								robot.wait(100);
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						robot.mousePress(((MouseUpDownEvent) event).buttons);
						mouseDown = true;
					} else if (event.type.equals("release")) {
						robot.mouseRelease(((MouseUpDownEvent) event).buttons);
						cursorWindow.setVisible(true);
						try {
							synchronized(robot) {
								robot.wait(100);
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						robot.mouseMove(origMouseX, origMouseY);
						robot.waitForIdle();
						mouseDown = false;
						if (lastExecName != null) {
							if (lastPathName != null) {
								new Thread(new Runnable() {

									@Override
									public void run() {
										System.out.println("Thread waiting for file");
										draw.defineWindow(spinner, null);
										while (dataFile == null) {
											try {
												Thread.sleep(200);
											} catch (InterruptedException e) {
												e.printStackTrace();
											}
										}
										draw.defineWindow(cursor, null);
										System.out.println("File is here.");
										try {
											DraggedWindowDetector.openFile(lastExecName, dataFile.getAbsolutePath());
											dataFile = null;
											lastPathName = null;
											lastExecName = null;
										} catch (IOException e) {
											e.printStackTrace();
										}
									}

								}).start();
							} else {
								System.out.println("File path is null");
								draw.defineWindow(cursor, null);
								try {
									DraggedWindowDetector.openFile(lastExecName, null);
									
									if (lastExecName.equalsIgnoreCase("chrome")) {
										while(chromeUrls == null) {
											Thread.sleep(250);
										}
										ChromeDataRequester.openUrls(chromeUrls);
										chromeUrls = null;
									}
									
									lastPathName = null;
									lastExecName = null;
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							
						}
					}
				} else if (event instanceof MouseExitScreenEvent) {
					MouseExitScreenEvent mlose = (MouseExitScreenEvent)event;
					remoteControl = true;
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					int width = (int)screenSize.getWidth();
					int height = (int)screenSize.getHeight();
					if (mlose.fromRight)
						mouseX = 10;
					else
						mouseX = width - 10;
					mouseY = (int) (height * mlose.height); 
					System.out.println("init: " + mouseX + ", " + mouseY);
					cursorWindow.setVisible(true);
					cursorWindow.setLocation(mouseX, mouseY);
					
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
				} else if (event instanceof WindowDraggedEvent) {
					WindowDraggedEvent wde = (WindowDraggedEvent)event;
					lastExecName = wde.executableName;
					lastPathName = wde.filepath;
					WindowAckEvent e = new WindowAckEvent(lastPathName);
					e.send();
					System.out.println("Sent ack event");
				} else if (event instanceof ChromeDataEvent) {
					ChromeDataEvent cde = (ChromeDataEvent)event;
					chromeUrls = cde.urls;
				} else {
					// do other things with other events
				}
			}
		}
		
	}
}
