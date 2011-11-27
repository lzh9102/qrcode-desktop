package zxinggui.generator;

import javax.swing.JComponent;

/**
 * This exception is raised by a generator when the input data contains errors.
 * Use the getMessage() method to get the error message to display to the user.
 */
@SuppressWarnings("serial")
public class GeneratorException extends Exception {
	private JComponent widget;
	public GeneratorException(String msg, JComponent w) {
		super(msg);
		widget = w;
	}
	public JComponent getWidget() {
		return widget;
	}
}
