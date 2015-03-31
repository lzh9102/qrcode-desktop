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
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class SmsGenerator implements GeneratorInterface {

	private JPanel panel = new JPanel();
	private JTextField txtPhoneNumber = new JTextField();
	private JTextArea txtMessage = new JTextArea();
	
	static final String NORTH = SpringLayout.NORTH;
	static final String SOUTH = SpringLayout.SOUTH;
	static final String EAST = SpringLayout.EAST;
	static final String WEST = SpringLayout.WEST;
	
	public SmsGenerator() {
		SpringLayout layout = new SpringLayout();
		JLabel lblNumber = new JLabel("Phone Number: ");
		JLabel lblMessage = new JLabel("Message: ");
		JScrollPane scrollPane = new JScrollPane(txtMessage);
		txtMessage.setLineWrap(true);
		
		panel.setLayout(layout);
		panel.add(lblNumber);
		panel.add(txtPhoneNumber);
		panel.add(lblMessage);
		panel.add(scrollPane);
		
		// Phone Number Label
		layout.putConstraint(WEST, lblNumber, 5, WEST, panel);
		layout.putConstraint(NORTH, lblNumber, 5, NORTH, panel);
		
		// Phone Number Field
		layout.putConstraint(WEST, txtPhoneNumber, 5, EAST, lblNumber);
		layout.putConstraint(NORTH, txtPhoneNumber, 5, NORTH, panel);
		layout.putConstraint(EAST, txtPhoneNumber, -5, EAST, panel);
		
		// Message Label
		layout.putConstraint(WEST, lblMessage, 5, WEST, panel);
		layout.putConstraint(NORTH, lblMessage, 5, SOUTH, lblNumber);
		layout.putConstraint(EAST, lblMessage, -5, EAST, panel);
		
		// Message Field
		layout.putConstraint(WEST, scrollPane, 5, WEST, panel);
		layout.putConstraint(NORTH, scrollPane, 5, SOUTH, lblMessage);
		layout.putConstraint(EAST, scrollPane, -5, EAST, panel);
		layout.putConstraint(SOUTH, scrollPane, -5, SOUTH, panel);
	}

	@Override
	public String getName() {
		return "SMS";
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}

	@Override
	public String getText() throws GeneratorException {
		String number = getNumberField();
		String message = getMessageField();
		String output = number;
		
		if (!message.isEmpty())
			output += ':' + message; // Append the message if it is not empty.
		
		return "smsto:" + output;
	}
	
	private String getNumberField() throws GeneratorException {
		String number = txtPhoneNumber.getText();
		if (number.isEmpty())
			throw new GeneratorException("Phone number cannot be empty.", txtPhoneNumber);
		if (!Validator.isValidPhoneNumber(number))
			throw new GeneratorException("Incorrect phone number.", txtPhoneNumber);
		return number;
	}
	
	private String getMessageField() throws GeneratorException {
		return txtMessage.getText();
	}

	@Override
	public void setFocus() {
		txtPhoneNumber.requestFocusInWindow();
	}

	@Override
	public int getParsingPriority() {
		return 1;
	}

	@Override
	public boolean parseText(String text, boolean write) {
		String captext = text.toUpperCase();
		
		// matches "SMSTO:[space]<phone number>[space]", space is optional
		if (!captext.matches("^SMSTO:.+$"))
			return false;
		
		String phone = captext.split(":")[1].trim(); // text after the colon
		
		if (!Validator.isValidPhoneNumber(phone))
			return false;
		
		if (write) {
			txtPhoneNumber.setText(phone);
		}
		
		return true;
	}

}
