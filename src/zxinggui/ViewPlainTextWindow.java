package zxinggui;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class ViewPlainTextWindow extends JFrame 
	implements KeyListener {
	JTextArea textarea;
	JFrame parent;
	
	public ViewPlainTextWindow(JFrame p, String s) {
		parent = p;
		textarea = new JTextArea(s);
		init_widgets();
	}
	
	private void init_widgets() {
		JScrollPane panel = new JScrollPane(textarea);
		BorderLayout layout = new BorderLayout();
		
		this.setLayout(layout);
		this.add(panel);
		
		textarea.setEditable(false); // read-only
		textarea.addKeyListener(this);
		
		this.setSize(400,300);
		if (parent != null)
			this.setLocation(parent.getX(), parent.getY());
	}
	
	void setText(String s) {
		textarea.setText(s);
	}

	/*====== Textbox Key Events ======*/

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			this.setVisible(false);
	}

	@Override
	public void keyTyped(KeyEvent e) { }
	@Override
	public void keyReleased(KeyEvent e) { }
}
