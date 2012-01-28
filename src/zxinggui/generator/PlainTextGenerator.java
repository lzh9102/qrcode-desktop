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

package zxinggui.generator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import java.lang.String;

public class PlainTextGenerator implements GeneratorInterface {
	private JPanel panel = new JPanel();
	private JTextArea textarea = new JTextArea();
	static final String NORTH = SpringLayout.NORTH;
	static final String SOUTH = SpringLayout.SOUTH;
	static final String EAST = SpringLayout.EAST;
	static final String WEST = SpringLayout.WEST;

	public PlainTextGenerator() {
		SpringLayout layout = new SpringLayout();
		JLabel label = new JLabel("Text: ");
		JScrollPane scrollPane = new JScrollPane(textarea);
		
		textarea.setLineWrap(true);
		
		panel.setLayout(layout);
		panel.add(label);
		panel.add(scrollPane);
		
		layout.putConstraint(NORTH, label, 5, NORTH, panel);
		layout.putConstraint(WEST, label, 5, WEST, panel);
		
		layout.putConstraint(NORTH, scrollPane, 5, SOUTH, label);
		layout.putConstraint(SOUTH, scrollPane, -5, SOUTH, panel);
		layout.putConstraint(EAST, scrollPane, -5, EAST, panel);
		layout.putConstraint(WEST, scrollPane, 5, WEST, panel);
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}

	@Override
	public String getName() {
		return "Plain Text";
	}

	@Override
	public String getText() throws GeneratorException {
		String text = textarea.getText();
		if (text.isEmpty())
			throw new GeneratorException("Text cannot be empty.", textarea);

		return text;
	}

	@Override
	public void setFocus() {
		textarea.requestFocusInWindow();
	}

	@Override
	public int getParsingPriority() {
		return 0; // always fallback to plain text
	}

	@Override
	public boolean parseText(String text, boolean write) {
		if (text.isEmpty())
			return false;
		if (write)
			textarea.setText(text);
		return true;
	}
}
