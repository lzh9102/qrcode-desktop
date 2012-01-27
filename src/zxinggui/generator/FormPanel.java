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
