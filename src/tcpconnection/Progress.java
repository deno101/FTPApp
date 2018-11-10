package tcpconnection;

import javax.swing.*;
import java.awt.*;

public class Progress {

	public static JFrame frame;
	public static JProgressBar progressBar;
	private static JLabel fileLable;

	public Progress() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 383, 155);
		frame.setIconImage(new ImageIcon(getClass().getResource("/images/ico.png")).getImage());
		frame.setAlwaysOnTop(true);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(null);

		progressBar = new JProgressBar();
		progressBar.setBounds(10, 67, 349, 45);
		progressBar.setForeground(new Color(50, 205, 50));
		progressBar.setStringPainted(true);
		frame.getContentPane().add(progressBar);

		fileLable = new JLabel("");
		fileLable.setBounds(10, 11, 349, 45);
		fileLable.setFont(new Font("helveltica", Font.PLAIN, 16));
		frame.getContentPane().add(fileLable);
		frame.setVisible(true);
	}

	public static void file(String filename) {
		fileLable.setText("Recieving " + filename);
	}

	public static void progress(int num) {
		progressBar.setValue(num);
	}
}
