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

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

@SuppressWarnings("serial")
public class FormPanel extends JPanel {
	
	static final String NORTH = SpringLayout.NORTH;
	static final String SOUTH = SpringLayout.SOUTH;
	static final String EAST = SpringLayout.EAST;
	static final String WEST = SpringLayout.WEST;
	
	private JPanel internalPanel = new JPanel(new GridLayout(0,2));
	private JScrollPane scrollPane;
		
	public FormPanel() {
		scrollPane = new JScrollPane(internalPanel);
		scrollPane.setBorder(BorderFactory.createEmptyBorder()); // disable border
		add(scrollPane);
	}
	
	/**
	 * Add a label and a field to the panel.
	 * @param label
	 * @param field
	 */
	public void addField(JLabel label, Component field) {
		if (label == null || field == null) return;
		internalPanel.add(label);
		internalPanel.add(field);
	}
	
}
