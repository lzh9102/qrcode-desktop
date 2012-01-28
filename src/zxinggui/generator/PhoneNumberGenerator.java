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
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class PhoneNumberGenerator implements GeneratorInterface {
	private JPanel panel = new JPanel();
	private JTextField txtPhoneNumber = new JTextField();
	
	public PhoneNumberGenerator() {
		
		SpringLayout layout = new SpringLayout();
		JLabel label = new JLabel("Phone Number: ");
		
		panel.setLayout(layout);
		panel.add(label);
		panel.add(txtPhoneNumber);
		
		layout.putConstraint(SpringLayout.WEST, label, 5, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, label, 5, SpringLayout.NORTH, panel);
		
		layout.putConstraint(SpringLayout.WEST, txtPhoneNumber, 5, SpringLayout.EAST, label);
		layout.putConstraint(SpringLayout.NORTH, txtPhoneNumber, 5, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.EAST, panel, 5, SpringLayout.EAST, txtPhoneNumber);
	}

	@Override
	public String getName() {
		return "Phone Number";
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}

	@Override
	public String getText() throws GeneratorException {
		String number = txtPhoneNumber.getText();
		if (number.isEmpty())
			throw new GeneratorException("Phone number cannot be empty.", txtPhoneNumber);
		if (!Validator.isValidPhoneNumber(number))
			throw new GeneratorException("Incorrect phone number.", txtPhoneNumber);
		
		return "tel:" + number;
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
		if (!captext.matches("^TEL:.*$"))
			return false;
		
		String phone = text.split(":")[1].trim();
		if (!Validator.isValidPhoneNumber(phone))
			return false;
		
		if (write)
			txtPhoneNumber.setText(phone);
		
		return true;
	}

}
