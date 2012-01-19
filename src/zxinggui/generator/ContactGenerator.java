package zxinggui.generator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class ContactGenerator implements GeneratorInterface {

	// the main panel of the generator
	private FormPanel panel = new FormPanel();
	
	// fields of the generator
	private JTextField txtName = new JTextField();
	private JTextField txtReading = new JTextField(); // prononciation of the name
	private JTextField txtOrg = new JTextField();
	private JTextField txtPhoneNumber = new JTextField();
	private JTextField txtEmail = new JTextField();
	private JTextField txtAddress = new JTextField();
	private JTextField txtWebsite = new JTextField();
	private JTextField txtMemo = new JTextField();
	private JComboBox cbFormat = new JComboBox();
	
	// constant definitions
	static final String NORTH = SpringLayout.NORTH;
	static final String SOUTH = SpringLayout.SOUTH;
	static final String EAST = SpringLayout.EAST;
	static final String WEST = SpringLayout.WEST;
	static final int ALIGH_RIGHT = JLabel.RIGHT;
	
	static final String FORMAT_VCARD = "vcard";
	static final String FORMAT_MECARD = "mecard";
	static final String[] contactFormats = {FORMAT_MECARD, FORMAT_VCARD};
	
	/* Listening for user selecting contact format. */
	private ActionListener cbFormatListener = new ActionListener() {
		public void actionPerformed(ActionEvent event) {
			String format = cbFormat.getSelectedItem().toString();
			/* When a format is selected, hide unsupported fields and show
			 * supported hidden fields.
			 */
			if (format == FORMAT_VCARD) {
				//txtOrg.setVisible(true);
				txtReading.setVisible(false);
			} else if (format == FORMAT_MECARD) {
				//txtOrg.setVisible(false);
				txtReading.setVisible(true);
			}
			panel.revalidate();
		}
	};
	
	public ContactGenerator() {
		JLabel lblName = new JLabel("Name: ", ALIGH_RIGHT);
		JLabel lblReading = new JLabel("Reading: ", ALIGH_RIGHT);
		JLabel lblOrg = new JLabel("Company/Organization: ", ALIGH_RIGHT);
		JLabel lblPhoneNumber = new JLabel("Phone Number: ", ALIGH_RIGHT);
		JLabel lblEmail = new JLabel("E-Mail: ", ALIGH_RIGHT);
		JLabel lblAddress = new JLabel("Address: ", ALIGH_RIGHT);
		JLabel lblWebsite = new JLabel("Website: ", ALIGH_RIGHT);
		JLabel lblMemo = new JLabel("Memo: ", ALIGH_RIGHT);
		JLabel lblFormat = new JLabel("Format: ", ALIGH_RIGHT);
		
		panel.addField(lblName, txtName);
		panel.addField(lblReading, txtReading);
		panel.addField(lblOrg, txtOrg);
		panel.addField(lblPhoneNumber, txtPhoneNumber);
		panel.addField(lblEmail, txtEmail);
		panel.addField(lblAddress, txtAddress);
		panel.addField(lblWebsite, txtWebsite);
		panel.addField(lblMemo, txtMemo);
		panel.addField(lblFormat, cbFormat);
		
		cbFormat.addActionListener(cbFormatListener);
		
		for (final String format: contactFormats) {
			cbFormat.addItem(new Object() {
				public String toString() { return format;	}
				});
		}
	}

	public String getName() {
		return "Contact Information";
	}

	public JPanel getPanel() {
		return panel;
	}

	public String getText() throws GeneratorException {
		String format = cbFormat.getSelectedItem().toString(); 
		if (format == FORMAT_VCARD) {
			return encodeVCard();
		} else if (format == FORMAT_MECARD) {
			return encodeMeCard();
		} else {
			throw new GeneratorException("Unknown contact format.", cbFormat);
		}
	}
	
	private String encodeMeCard() throws GeneratorException {
		StringBuilder content = new StringBuilder();
		
		// Field Definitions
		String name = getNameField();
		String reading = getReadingField();
		String company = getOrgField(); // not part of the standard MECARD format.
		String phone = getPhoneNumberField();
		String email = getEmailField();
		String addr = getAddressField();
		String url = getWebsiteField();
		String memo = getMemoField();

		content.append("MECARD:"); // mecard identifier
		
		content.append("N:" + name + ";");
		if (!reading.isEmpty())
			content.append("SOUND:" + reading + ";");
		if (!company.isEmpty())  // NOTE: ORG is not a standard MECARD field
			content.append("ORG:" + company + ";");
		if (!phone.isEmpty())
			content.append("TEL:" + phone + ";");
		if (!email.isEmpty())
			content.append("EMAIL:" + email + ";");
		if (!addr.isEmpty())
			content.append("ADR:" + addr + ";");
		if (!url.isEmpty())
			content.append("URL:" + url + ";");
		if (!memo.isEmpty())
			content.append("NOTE:" + memo + ";");
		
		content.append(";"); // MECARD ends with two ';'s
		return content.toString();
	}
	
	private String encodeVCard() throws GeneratorException {
		StringBuilder content = new StringBuilder();
		
		// Field Definitions
		String name = getNameField();
		/* VCard has no "reading" field */
		String company = getOrgField();
		String phone = getPhoneNumberField();
		String email = getEmailField();
		String addr = getAddressField();
		String url = getWebsiteField();
		String memo = getMemoField();

		content.append("BEGIN:VCARD\n"); // begin of vcard
		
		content.append("N:" + name + "\n");
		if (!company.isEmpty())  // NOTE: ORG is not a standard MECARD field
			content.append("ORG:" + company + "\n");
		if (!phone.isEmpty())
			content.append("TEL:" + phone + "\n");
		if (!email.isEmpty())
			content.append("EMAIL:" + email + "\n");
		if (!addr.isEmpty())
			content.append("ADR:" + addr + "\n");
		if (!url.isEmpty())
			content.append("URL:" + url + "\n");
		if (!memo.isEmpty())
			content.append("NOTE:" + memo + "\n");
		
		content.append("END:VCARD"); // end of VCARD
		return content.toString();
	}
	
	private String getNameField() throws GeneratorException {
		String name = txtName.getText();
		if (name.isEmpty())
			throw new GeneratorException("Name cannot be empty.", txtName);
		return name;
	}
	
	private void setNameField(String text) {
		txtName.setText(text);
	}
	
	private String getReadingField() throws GeneratorException {
		String reading = txtReading.getText();
		return reading;
	}
	
	private void setReadingField(String text) {
		txtReading.setText(text);
	}
	
	private String getOrgField() throws GeneratorException {
		String org = txtOrg.getText();
		return org;
	}
	
	private void setOrgField(String text) {
		txtOrg.setText(text);
	}
	
	private String getPhoneNumberField() throws GeneratorException {
		String number = txtPhoneNumber.getText();
		if (!number.isEmpty() && !Validator.isValidPhoneNumber(number))
			throw new GeneratorException("Incorrect phone number.", txtPhoneNumber);
		return number;
	}
	
	private void setPhoneNumberField(String text) {
		txtPhoneNumber.setText(text);
	}
	
	private String getEmailField() throws GeneratorException {
		String email = txtEmail.getText();
		if (!email.isEmpty() && !Validator.isValidEmail(email))
			throw new GeneratorException("Incorrect Email address.", txtEmail);
		return email;
	}
	
	private void setEmailField(String text) {
		txtEmail.setText(text);
	}
	
	private String getAddressField() throws GeneratorException {
		String addr = txtAddress.getText();
		return addr;
	}
	
	private void setAddressField(String text) {
		txtAddress.setText(text);
	}
	
	private String getWebsiteField() throws GeneratorException {
		String url = txtWebsite.getText();
		if (!url.isEmpty() && !Validator.isValidURI(url))
			throw new GeneratorException("Incorrect URL.", txtWebsite);
		return url;
	}
	
	private void setWebsiteField(String text) {
		txtWebsite.setText(text);
	}
	
	private String getMemoField() throws GeneratorException {
		String memo = txtMemo.getText();
		return memo;
	}
	
	private void setMemoField(String text) {
		txtMemo.setText(text);
	}

	public void setFocus() {
		txtPhoneNumber.requestFocusInWindow();
	}

	@Override
	public int getParsingPriority() {
		return 10;
	}

	@Override
	public boolean parseText(String text, boolean write) {
		return parseMeCard(text, write) || parseVCard(text, write);
	}
	
	private boolean parseMeCard(String text, boolean write) {
		if (!text.startsWith("MECARD:"))
			return false;
		
		// Field Definitions
		String name = null;
		String reading = null;
		String company = null; // not part of the standard MECARD format.
		String phone = null;
		String email = null;
		String addr = null;
		String url = null;
		String memo = null;
		
		String[] fields = text.substring("MECARD:".length()).split(";");
		
		for (String field: fields) {
			String[] field_split = field.split(":");
			if (field_split.length < 2)
				return false;
			
			String field_id = field_split[0];
			String field_val = field_split[1];
			
			if (field_id.equals("N"))
				name = field_val;
			else if (field_id.equals("SOUND"))
				reading = field_val;
			else if (field_id.equals("ORG"))
				company = field_val;
			else if (field_id.equals("TEL"))
				phone = field_val;
			else if (field_id.equals("EMAIL"))
				email = field_val;
			else if (field_id.equals("ADR"))
				addr = field_val;
			else if (field_id.equals("URL"))
				url = field_val;
			else if (field_id.equals("NOTE"))
				memo = field_val;
			else if (field_id.trim().isEmpty()) // end of mecard
				break;
			else
				return false; /* TODO: add more fields */
		}
		
		// validate fields
		if (name.isEmpty())
			return false;
		if (phone != null && !Validator.isValidPhoneNumber(phone))
			return false;
		if (email != null && !Validator.isValidEmail(email))
			return false;
		/* don't validate url because there are too much possibilities */
		
		// fill in fields
		if (write) {
			if (name != null)
				setNameField(name);
			if (reading != null)
				setReadingField(reading);
			if (company != null)
				setOrgField(company);
			if (phone != null)
				setPhoneNumberField(phone);
			if (email != null)
				setEmailField(email);
			if (addr != null)
				setAddressField(addr);
			if (url != null)
				setWebsiteField(url);
			if (memo != null)
				setMemoField(memo);
		}
		
		return true;
	}
	
	private boolean parseVCard(String text, boolean write) {
		if (!text.startsWith("BEGIN:VCARD\n"))
			return false;
		
		return false;
	}

}

