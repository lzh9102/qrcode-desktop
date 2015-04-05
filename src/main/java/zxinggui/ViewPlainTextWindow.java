/*
 * Copyright 2012 Timothy Lin <lzh9102@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
