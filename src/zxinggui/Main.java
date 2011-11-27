package zxinggui;

import javax.swing.SwingUtilities;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainWindow window = new MainWindow();
				window.setVisible(true);
			}
		});
	}

}
