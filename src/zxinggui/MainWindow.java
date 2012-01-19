package zxinggui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import zxinggui.generator.*;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

@SuppressWarnings("serial")
public class MainWindow extends JFrame
	implements ActionListener, MouseListener, ScreenCaptureListener {
	
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
	
	private JMenuBar menuBar = new JMenuBar();
	private JMenu menuFile;
	private JMenuItem menuFile_SaveImage;
	
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
	private JPopupMenu menuImage = new JPopupMenu();
	private JMenuItem menuItem_ViewPlainText = new JMenuItem("View Plain Text");
	private JButton btnCapture = new JButton();
	
	private GeneratorManager generators = new GeneratorManager();
	
	private QRCodeWriter writer = new QRCodeWriter();
	private QRCodeReader reader = new QRCodeReader();
	
	private String prevText = new String();
	
	private BufferedImage generated_image = null;
	
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
		
		// Menu Bar
		menuFile = new JMenu("File");
		menuFile.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menuFile);
		menuFile_SaveImage = new JMenuItem("Save Image");
		menuFile_SaveImage.setMnemonic(KeyEvent.VK_S);
		menuFile_SaveImage.addActionListener(this);
		menuFile.add(menuFile_SaveImage);
		setJMenuBar(menuBar);
		
		// Text
		btnEncode.setText("Encode");
		btnCapture.setText("Capture Screen");

		// Layout
		panelGenerator.setLayout(new BorderLayout());
		cbSelectGenerator.addActionListener(this);
		lblOutputImage.setHorizontalAlignment(JLabel.CENTER);
		lblOutputImage.setVerticalAlignment(JLabel.CENTER);
		
		// Events
		btnEncode.addActionListener(this);
		btnCapture.addActionListener(this);
		
		// Main Panel
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
		panelRight.add(btnCapture, BorderLayout.SOUTH);
		
		// context menu of the output image
		menuItem_ViewPlainText.addActionListener(this);
		menuImage.add(menuItem_ViewPlainText);
		lblOutputImage.addMouseListener(this);
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
		generated_image = null;
		
		// Set character encoding of the message.
		final int encoding_index = cbSelectEncoding.getSelectedIndex();
		Hashtable<EncodeHintType, String> options = new Hashtable<EncodeHintType, String>();
		options.put(EncodeHintType.CHARACTER_SET, encodings[encoding_index]);
		
		String text = new String();
		
		try {
			text = currentGenerator().getText();
			BitMatrix matrix = writer.encode(text
					, BarcodeFormat.QR_CODE, size, size, options);
			BufferedImage image = ImageHelper.toBufferedImage(matrix);
			lblOutputImage.setIcon(new ImageIcon(image));
			generated_image = image;
		} catch (WriterException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		} catch (GeneratorException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
		
		prevText = text;
	}
	
	private void viewPlainText() {
		if (!prevText.isEmpty())
			new ViewPlainTextWindow(this, prevText).setVisible(true);
	}
	
	private void saveImage() {
		JFileChooser fc = new JFileChooser();
		
		fc.addChoosableFileFilter(new FileFilter() {
			public boolean accept(File f) {
				return f.getName().toLowerCase().endsWith(".png") || f.isDirectory();
			}
			public String getDescription() {
				return "PNG Image (*.png)";
			}
		});
		
		fc.setCurrentDirectory(new File("."));
		fc.setDialogType(JFileChooser.SAVE_DIALOG);
		
		if (generated_image != null) {
			if (fc.showDialog(this, "Save Image") == JFileChooser.APPROVE_OPTION) {
				File file = new File(fc.getSelectedFile().getPath() + ".png");
				try {
					ImageIO.write(generated_image, "png", file);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this, "Failed to save image");
				}
			}
		} else {
			JOptionPane.showMessageDialog(this, "No image to save");
		}
	}
	
	/**
	 * @return the current selected generator
	 */
	private GeneratorInterface currentGenerator() {
		int index = cbSelectGenerator.getSelectedIndex();
		return generators.getGenerator(index);
	}
	
	private void decodeImage(BufferedImage image) {
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		try {
			Result result = reader.decode(bitmap);
			lblOutputImage.setIcon(new ImageIcon(image));
			generated_image = null;
			prevText = result.getText();
			
			/* find the most appropriate handler for the text,
			   switch to it, and fill in the fields. */
			int generator_index = generators.findTextHandlerIndex(prevText);
			cbSelectGenerator.setSelectedIndex(generator_index);
			generators.getGenerator(generator_index).parseText(prevText, true);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Failed to decode the image");
		}
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
		} else if (obj == menuItem_ViewPlainText) {
			viewPlainText();
		} else if (obj == btnCapture) { // capture screen and decode
			ScreenCaptureWindow scw = new ScreenCaptureWindow(this);
			scw.captureScreen();
		} else if (obj == menuFile_SaveImage) {
			saveImage();
		}
	}
	
	public void maybeShowPopup(MouseEvent e) {
		Object src = e.getSource();
		if (e.isPopupTrigger() && src == lblOutputImage) {
			menuImage.show(lblOutputImage, e.getX(), e.getY());
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) { }

	@Override
	public void mouseEntered(MouseEvent arg0) { }

	@Override
	public void mouseExited(MouseEvent arg0) { }

	@Override
	public void mousePressed(MouseEvent e) {
		maybeShowPopup(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);		
	}

	@Override
	public void screenshotCanceled() {
		
	}

	@Override
	public void screenshotCaptured(BufferedImage image) {
		decodeImage(image);
	}
	
}
