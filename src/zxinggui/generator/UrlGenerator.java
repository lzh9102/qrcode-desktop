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

public class UrlGenerator implements GeneratorInterface {
	private JPanel panel = new JPanel();
	private JTextField txtUrl = new JTextField();
	
	public UrlGenerator() {
		SpringLayout layout = new SpringLayout();
		JLabel label = new JLabel("URL: ");
		
		txtUrl.setText("http://");
		
		panel.setLayout(layout);
		panel.add(label);
		panel.add(txtUrl);
		
		layout.putConstraint(SpringLayout.WEST, label, 5, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, label, 5, SpringLayout.NORTH, panel);
		
		layout.putConstraint(SpringLayout.WEST, txtUrl, 5, SpringLayout.EAST, label);
		layout.putConstraint(SpringLayout.NORTH, txtUrl, 5, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.EAST, txtUrl, -5, SpringLayout.EAST, panel);
	}

	@Override
	public String getName() {
		return "URL";
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}

	@Override
	public String getText() throws GeneratorException {
		String uri = txtUrl.getText();
		if (uri.isEmpty())
			throw new GeneratorException("URL cannot be empty.", txtUrl);
		if (!Validator.isValidURI(uri))
			throw new GeneratorException("Incorrect URL", txtUrl);
		return uri;
	}
	
	@Override
	public void setFocus() {
		txtUrl.requestFocusInWindow();
	}

	@Override
	public int getParsingPriority() {
		return 1;
	}

	@Override
	public boolean parseText(String text, boolean write) {
		String url = text;
		if (!Validator.isValidURI(url)) {
			url = "http://" + url;
			if (!Validator.isValidURI(url))
				return false;				
		}
			
		if (write)
			txtUrl.setText(url);
		
		return true;
	}

}
