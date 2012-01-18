package zxinggui.generator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class EmailGenerator implements GeneratorInterface {
	private JPanel panel = new JPanel();
	private JTextField txtEmail = new JTextField();
	
	public EmailGenerator() {
		SpringLayout layout = new SpringLayout();
		JLabel label = new JLabel("E-Mail: ");
		
		panel.setLayout(layout);
		panel.add(label);
		panel.add(txtEmail);
		
		layout.putConstraint(SpringLayout.WEST, label, 5, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, label, 5, SpringLayout.NORTH, panel);
		
		layout.putConstraint(SpringLayout.WEST, txtEmail, 5, SpringLayout.EAST, label);
		layout.putConstraint(SpringLayout.NORTH, txtEmail, 5, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.EAST, txtEmail, -5, SpringLayout.EAST, panel);
	}

	@Override
	public String getName() {
		return "E-Mail";
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}

	@Override
	public String getText() throws GeneratorException {
		String uri = txtEmail.getText();
		if (uri.isEmpty())
			throw new GeneratorException("E-Mail cannot be empty.", txtEmail);
		if (!Validator.isValidEmail(uri))
			throw new GeneratorException("Incorrect E-Mail", txtEmail);
		return "mailto:" + uri;
	}

	@Override
	public void setFocus() {
		txtEmail.requestFocusInWindow();
	}

	@Override
	public int getParsingPriority() {
		return 1;
	}

	@Override
	public boolean parseText(String text, boolean write) {
		String captext = text.toUpperCase();
		if (!captext.matches("^MAILTO:.*$"))
			return false;
		
		String email = text.split(":")[1].trim();
		if (!Validator.isValidEmail(email))
			return false;
		
		if (write)
			txtEmail.setText(email);
		
		return true;
	}

}
