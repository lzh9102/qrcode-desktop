package zxinggui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.swing.*;
import zxinggui.generator.*;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

@SuppressWarnings("serial")
public class MainWindow extends JFrame implements ActionListener {
	
	private static final int STARTUP_WIDTH = 800;
	private static final int STARTUP_HEIGHT = 400;
	
	/**
	 * qrcode encoding strings passed to the zxing library.
	 * **DO NOT** translate these strings.
	 */
	private static final String[] encodings = {
		"UTF-8", "ISO-8859-1", "Shift_JIS"
	};
	
	/**
	 * encoding names to be displayed to user.
	 */
	private static final String[] encodingNames = {
		"UTF-8", "ISO-8859-1", "Shift_JIS"
	};
	
	private static final int[] outputSizes = {
		350, 230, 120
	};
	
	private JPanel panelMain = new JPanel();
	
	private JPanel panelLeft = new JPanel();
	private JComboBox cbSelectGenerator = new JComboBox();
	private JPanel panelGenerator = new JPanel();
	private JPanel panelButtons = new JPanel();
	private JComboBox cbSelectSize = new JComboBox();
	private JComboBox cbSelectEncoding = new JComboBox();
	private JButton btnEncode = new JButton();
	
	private JPanel panelRight = new JPanel();
	private JLabel lblOutputImage = new JLabel();
	
	private GeneratorManager generators = new GeneratorManager();
	
	private QRCodeWriter writer = new QRCodeWriter();
	private QRCodeReader reader = new QRCodeReader();
	
	public MainWindow() {
		
		setupUI();
		setupGenerators();
		setupEncodings();
		setupOutputSizeSelection();
		
		setTitle("Main Window");
		setSize(STARTUP_WIDTH, STARTUP_HEIGHT);
		setMinimumSize(new Dimension(getWidth(), getHeight()));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	/**
	 * Initialize GUI components.
	 */
	private void setupUI() {
		panelMain.setLayout(new GridLayout());
		panelLeft.setLayout(new BorderLayout());
		panelRight.setLayout(new BorderLayout());
		panelButtons.setLayout(new GridLayout());
		
		panelGenerator.setLayout(new BorderLayout());
		cbSelectGenerator.addActionListener(this);
		btnEncode.setText("Encode");
		btnEncode.addActionListener(this);
		lblOutputImage.setHorizontalAlignment(JLabel.CENTER);
		lblOutputImage.setVerticalAlignment(JLabel.CENTER);
		
		add(panelMain);
		panelMain.add(panelLeft);
		panelMain.add(panelRight);
		
		// Left Panel
		panelLeft.add(cbSelectGenerator, BorderLayout.PAGE_START);
		panelLeft.add(panelGenerator, BorderLayout.CENTER);
		panelLeft.add(panelButtons, BorderLayout.PAGE_END);
		
		panelButtons.add(cbSelectSize);
		panelButtons.add(new JLabel("Encoding: ", JLabel.RIGHT));
		panelButtons.add(cbSelectEncoding);
		panelButtons.add(btnEncode);
		
		// Right Panel
		panelRight.add(lblOutputImage, BorderLayout.CENTER);
	}
	
	/**
	 * Initialize the generator selection combobox.
	 */
	private void setupGenerators() {
		for (final GeneratorInterface gen: generators.getGenerators()) {
			Object item = new Object() {
				public String toString() { return gen.getName(); }
			};
			cbSelectGenerator.addItem(item);
		}
	}
	
	/**
	 * Initialize the encoding selection combobox.
	 */
	private void setupEncodings() {
		assert(encodings.length != encodingNames.length);
		for (int i=0; i<encodings.length; i++) {
			final int index = i;
			Object item = new Object() {
				public String toString() { return encodingNames[index]; }
			};
			cbSelectEncoding.addItem(item);
		}
	}
	
	/**
	 * Initialize the output size selection combobox.
	 */
	private void setupOutputSizeSelection() {
		for (final int size: outputSizes) {
			Object item = new Object() {
				public String toString() { return "" + size + "x" + size;}	
			};
			cbSelectSize.addItem(item);
		}
	}
	
	/**
	 * This function is called when a generator is selected.
	 * Remove the current generator panel and add the new generator panel
	 * to panelGenerator.
	 * @param index the index of the generator in the combobox
	 */
	private void generatorSelected(int index) {
		GeneratorInterface gen = generators.getGenerator(index);
		panelGenerator.removeAll();
		if (gen != null)
			panelGenerator.add(gen.getPanel());
		panelMain.validate(); // recalculate the layout
		panelMain.repaint();
	}
	
	/**
	 * Called when the encode button is pressed.
	 * This function encodes the information from the current generator
	 * and displays the resulting qrcode on the right panel.
	 */
	private void encodeButtonPressed() {
		
		// Set output image size
		int size = outputSizes[cbSelectSize.getSelectedIndex()];
		lblOutputImage.setIcon(null); // Remove existing image.
		
		// Set character encoding of the message.
		final int encoding_index = cbSelectEncoding.getSelectedIndex();
		Hashtable<EncodeHintType, String> options = new Hashtable<EncodeHintType, String>();
		options.put(EncodeHintType.CHARACTER_SET, encodings[encoding_index]);
		
		try {
			BitMatrix matrix = writer.encode(currentGenerator().getText()
					, BarcodeFormat.QR_CODE, size, size, options);
			BufferedImage image = ImageHelper.toBufferedImage(matrix);
			lblOutputImage.setIcon(new ImageIcon(image));
		} catch (WriterException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		} catch (GeneratorException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}
	
	/**
	 * @return the current selected generator
	 */
	private GeneratorInterface currentGenerator() {
		int index = cbSelectGenerator.getSelectedIndex();
		return generators.getGenerator(index);
	}
	
	/* Decoding QR Code:
	BufferedImage image = ImageHelper.captureScreen();
	LuminanceSource source = new BufferedImageLuminanceSource(image);
	BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
	Result result = reader.decode(bitmap);
	*/

	// GUI Events

	@Override
	public void actionPerformed(ActionEvent event) {
		Object obj = event.getSource();
		if (obj == cbSelectGenerator) { // generator selected
			int index = cbSelectGenerator.getSelectedIndex();
			if (index >= 0)
				generatorSelected(index);
		} else if (obj == btnEncode) { // "Encode" button pressed
			encodeButtonPressed();
		}
	}
	
}
