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

	public String getName() {
		return "URL";
	}

	public JPanel getPanel() {
		return panel;
	}

	public String getText() throws GeneratorException {
		String uri = txtUrl.getText();
		if (uri.isEmpty())
			throw new GeneratorException("URL cannot be empty.", txtUrl);
		if (!Validator.isValidURI(uri))
			throw new GeneratorException("Incorrect URL", txtUrl);
		return uri;
	}

	public void setFocus() {
		txtUrl.requestFocusInWindow();
	}

}
