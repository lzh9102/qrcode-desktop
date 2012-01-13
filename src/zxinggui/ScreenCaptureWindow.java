package zxinggui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class ScreenCaptureWindow extends JFrame
	implements MouseListener, MouseMotionListener, KeyListener {
	
	ScreenCaptureListener screen_capture_listener = null;
	BufferedImage screencapture;
	RectArea rect = new RectArea();
	
	public ScreenCaptureWindow(ScreenCaptureListener listener) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dim = toolkit.getScreenSize();
		
		if (listener != null)
			screen_capture_listener = listener;
		
		// window properties
		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		setUndecorated(true);
		setResizable(false);
		setAlwaysOnTop(true);
		setSize(dim.width, dim.height);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		// event listeners
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	/**
	 * Show the screen capture window and let user select the region.
	 */
	public void captureScreen() {		
		try {
			 screencapture = ImageHelper.captureScreen();
		} catch (Exception e) {
			return; // failed to capture screen
		}

		setVisible(true);
		
		return;
	}
	
	public void paint(Graphics g) {
		g.drawImage(screencapture, 0, 0, null);
		if (rect.dimension() == 2) {
			g.setColor(Color.RED);
			drawThickRect(g, rect, 5);
		}
	}
	
	/** Draw a rectangle with thick border.
	 * @param g the Graphics object
	 * @param r rectangle area
	 * @param bw border width
	 */
	private void drawThickRect(Graphics g, RectArea r, int bw) {
		int x = r.getX1();
		int y = r.getY1();
		int w = r.getW();
		int h = r.getH();
		for (int i=0; i<bw; i++) {
			g.drawRect(x-i, y-i, w+2*i, h+2*i);
		}
	}
	
	private void finishCapture() {
		BufferedImage image = ImageHelper.cropImage(
				screencapture, rect.getX1(), rect.getY1(), rect.getW(), rect.getH());
		setVisible(false);
		
		if (screen_capture_listener != null) {
			screen_capture_listener.screenshotCaptured(image);
		}
	}
	
	private void cancelCapture() {
		setVisible(false);
		if (screen_capture_listener != null) {
			screen_capture_listener.screenshotCanceled();
		}
	}

	/*====== Mouse Events ======*/
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) { // right key
			rect.reset();
			this.repaint();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (rect.containsXY(e.getX(), e.getY())) {
				finishCapture();
			} else {
				rect.setXY1(e.getX(), e.getY());
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			rect.setXY2(e.getX(), e.getY());
			this.repaint();
		}
	}
	@Override
	public void mouseEntered(MouseEvent e) { }
	@Override
	public void mouseExited(MouseEvent e) { }
	
	/*====== Mouse Motion ======*/

	@Override
	public void mouseDragged(MouseEvent e) {
		int modifiers = e.getModifiers();
		if ((modifiers & MouseEvent.BUTTON1_MASK) == MouseEvent.BUTTON1_MASK) {
			rect.setXY2(e.getX(), e.getY());
			this.repaint();
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) { }
	
	/*====== Keyboard Events ======*/

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			this.setVisible(false); // cancel on ESC key
		}
	}

	@Override
	public void keyReleased(KeyEvent e) { }
	@Override
	public void keyTyped(KeyEvent e) { }


}

class RectArea {
	private int x1, y1, x2, y2;
	
	public RectArea() {
		reset();
	}
	
	public void setXY1(int x, int y) {
		this.x1 = x;
		this.y1 = y;
	}
	
	public void setXY2(int w, int h) {
		this.x2 = w;
		this.y2 = h;
	}
	
	public int getX1() {
		return x1<x2 ? x1 : x2;
	}
	
	public int getY1() {
		return y1<y2 ? y1 : y2;
	}
	
	public int getX2() {
		return x2>x1 ? x2 : x1;
	}
	
	public int getY2() {
		return y2>y1 ? y2 : y1;
	}
	
	public int getW() {
		return getX2() - getX1();
	}
	
	public int getH() {
		return getY2() - getY1();
	}
	
	public void reset() {
		x1 = y1 = x2 = y2 = 0;
	}
	
	/** Return the dimension of the shape formed by the four points.
	 * @return If the four points form a rectangle, the function returns 2.
	 *  Otherwise, it returns 1 if they form a line and 0 if they form a point.
	 */
	public int dimension() {
		if (x1 == x2 && y1 == y2) // degenerated into a point
			return 0;
		else if (x1 == x2 || y1 == y2) // degenerated into a line
			return 1;
		else // rectangle
			return 2;
	}
	
	/** Determine if the point is inside the rectangle area.
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean containsXY(int x, int y) {
		return (getX1() < x && x < getX2()) && (getY1() < y && y < getY2());
	}
}
