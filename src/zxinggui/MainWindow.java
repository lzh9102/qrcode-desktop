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

package zxinggui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.OptionPaneUI;

import zxinggui.generator.*;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

@SuppressWarnings("serial")
public class MainWindow extends JFrame
	implements ActionListener, MouseListener, ScreenCaptureListener
	, ChangeListener {
	
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
	private JMenu menuBar_Image = new JMenu("Image");
	private JMenu menuBar_About = new JMenu("About");
	
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
	private JMenuItem menuImage_ViewPlainText = new JMenuItem("View Plain Text");
	private JMenuItem menuImage_SaveImage = new JMenuItem("Save Image");
	private JButton btnCapture = new JButton();
	private JMenuItem menuAbout_About = new JMenuItem("About This Software");
	
	private GeneratorManager generators = new GeneratorManager();
	
	private QRCodeWriter writer = new QRCodeWriter();
	private QRCodeReader reader = new QRCodeReader();
	
	private String prevText = new String();
	
	private String prevPath = new String(".");
	
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
		
		// Menu Items
		menuImage_ViewPlainText.setMnemonic(KeyEvent.VK_V);
		menuImage_ViewPlainText.addActionListener(this);
		menuImage_SaveImage.setMnemonic(KeyEvent.VK_S);
		menuImage_SaveImage.addActionListener(this);
		
		lblOutputImage.addMouseListener(this);
		//menuImage.add(menuImage_ViewPlainText);
		//menuImage.add(menuImage_SaveImage);
		
		// Menu Bar
		menuBar_Image.setMnemonic(KeyEvent.VK_I);
		menuBar_Image.add(menuImage_ViewPlainText);
		menuBar_Image.add(menuImage_SaveImage);
		menuBar_Image.addChangeListener(this);
		menuBar.add(menuBar_Image);
		menuBar_About.setMnemonic(KeyEvent.VK_A);
		menuAbout_About.setMnemonic(KeyEvent.VK_A);
		menuAbout_About.addActionListener(this);
		menuBar_About.add(menuAbout_About);
		menuBar.add(menuBar_About);
		setJMenuBar(menuBar);
		
		// Text
		btnEncode.setText("Encode");
		btnEncode.setToolTipText("Encode qr-code from the given information");
		btnCapture.setText("Capture Screen");
		btnCapture.setToolTipText("Decode qr-code from screenshot.");

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
		assert(encodings.length == encodingNames.length);
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
		
		fc.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				return f.getName().toLowerCase().endsWith(".png") || f.isDirectory();
			}
			public String getDescription() {
				return "PNG Image (*.png)";
			}
		});
		
		fc.setCurrentDirectory(new File(prevPath));
		fc.setDialogType(JFileChooser.SAVE_DIALOG);
		
		if (generated_image != null) {
			if (fc.showDialog(this, "Save Image") == JFileChooser.APPROVE_OPTION) {
				String filename = fc.getSelectedFile().getPath();
				if (!filename.endsWith(".png"))
					filename += ".png";
				
				File file = new File(filename);
				
				try {
					ImageIO.write(generated_image, "png", file);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this, "Failed to save image");
				}
				
				prevPath = fc.getCurrentDirectory().getPath();
			}
		} else {
			JOptionPane.showMessageDialog(this, "No image to save");
		}
	}
	
	private void showAboutDialog() {
		String msg = 
				  "QRCode Desktop, Copyright 2012 Timothy Lin <lzh9102@gmail.com>\n"
				+ "\n"
				+ "This program is free software licensed under the \n"
				+ "Apache Lincense Version 2.\n\n"
				+ "For more information, please see the project page: \n"
				+ "http://code.google.com/p/qrcode-desktop/" + "\n";
		JOptionPane.showMessageDialog(this, msg, "QRCode Desktop", JOptionPane.INFORMATION_MESSAGE);
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
	
	private void captureScreen() {
		// hide the window
		setVisible(false);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) { }
		
		ScreenCaptureWindow scw = new ScreenCaptureWindow(this);
		scw.captureScreen();
		/* The window will be re-enabled
		   in screenshotCanceled() or screenshotCaptured(). */
	}
	
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
		} else if (obj == menuImage_ViewPlainText) {
			viewPlainText();
		} else if (obj == btnCapture) { // capture screen and decode
			captureScreen();
		} else if (obj == menuImage_SaveImage) {
			saveImage();
		} else if (obj == menuAbout_About) {
			showAboutDialog();
		}
	}
	
	public void maybeShowPopup(MouseEvent e) {
		Object src = e.getSource();
		if (e.isPopupTrigger() && src == lblOutputImage) {
			menuImage_ViewPlainText.setEnabled(!prevText.isEmpty());
			menuImage_SaveImage.setEnabled(generated_image != null);
			menuImage.show(lblOutputImage, e.getX(), e.getY());
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) { }

	@Override
	public void mouseEntered(MouseEvent arg0) { }

	@Override
	public void mouseExited(MouseEvent arg0) { }

	@Override
	public void mousePressed(MouseEvent e) {
		Object obj = e.getSource();
		if (obj == lblOutputImage) {
			maybeShowPopup(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Object obj = e.getSource();
		if (obj == lblOutputImage) {
			maybeShowPopup(e);
		}
	}

	@Override
	public void screenshotCanceled() {
		setVisible(true);
	}

	@Override
	public void screenshotCaptured(BufferedImage image) {
		setVisible(true);
		decodeImage(image);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Object obj = e.getSource();
		if (obj == menuBar_Image) {
			menuImage_ViewPlainText.setEnabled(!prevText.isEmpty());
			menuImage_SaveImage.setEnabled(generated_image != null);
		}
	}
	
}
