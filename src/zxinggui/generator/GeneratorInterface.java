package zxinggui.generator;
import javax.swing.JPanel;
import java.lang.String;

public interface GeneratorInterface {
	/**
	 * @return a JPanel object containing the GUI
	 */
	JPanel getPanel();
	
	/**
	 * @return the name of the generator
	 */
	String getName();

	/**
	 * @return the generated text to be encoded
	 */
	String getText() throws GeneratorException;

	/**
	 * Called when the generator interface is activated.
	 * The generator should set the focus to the first widget it defines.
	 */
	void setFocus();
	
}
