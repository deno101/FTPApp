package main_application;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.swing.plaf.*;

import udpconnection.*;
import tcpconnection.*;

public class Window implements ActionListener {

	public static JFrame frmApplication;
	public JTextField filename;
	private JTextField ip;
	protected static JTextArea console;
	private static JTextArea userLog;
	private JButton sendButton;
	private JButton selectButton;
	private JFileChooser filechooser = new JFileChooser();
	private static TCPConnection tcp = new TCPConnection();
	private File file;
	private String absolutePath, path;
	private static JProgressBar progressBar;

	public Window() throws UnknownHostException {
		initialize();
	}

	private void initialize() throws UnknownHostException {
		frmApplication = new JFrame();
		frmApplication.setResizable(false);
		frmApplication.setIconImage(new ImageIcon(getClass().getResource("/images/ico.png")).getImage());
		frmApplication.setForeground(SystemColor.activeCaptionBorder);
		frmApplication.setBackground(SystemColor.activeCaptionBorder);
		InetAddress ips = InetAddress.getLocalHost();
		frmApplication.setTitle("TRANS");
		frmApplication.setBounds(100, 100, 564, 533);
		frmApplication.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmApplication.getContentPane().setLayout(null);
		frmApplication.setLocationRelativeTo(null);
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 312, 499);
		frmApplication.getContentPane().add(panel);
		filechooser.updateUI();

		console = new JTextArea();
		console.setToolTipText("Console");
		console.setEditable(false);
		console.setFont(new Font("heveltica", Font.PLAIN, 14));
		console.append("THIS PC ADDRESS ==> " + ips.getHostName().toUpperCase() + "/"
				+ ips.getHostAddress().toUpperCase() + "\n");
		((DefaultCaret) console.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		panel.setLayout(null);
		JScrollPane scrollPane_1 = new JScrollPane(console);
		scrollPane_1.setBounds(0, 84, 312, 280);
		scrollPane_1.setBorder(BorderFactory.createTitledBorder("CONSOLE"));
		panel.add(scrollPane_1);

		selectButton = new JButton("SELECT");
		selectButton.setBounds(223, 387, 89, 39);
		selectButton.setToolTipText("Select file to send");
		selectButton.setBackground(SystemColor.scrollbar);
		selectButton.addActionListener(this);
		selectButton.setFont(new Font("Tahoma", Font.PLAIN, 13));
		panel.add(selectButton);

		sendButton = new JButton("SEND");
		sendButton.setBounds(223, 441, 89, 33);
		sendButton.setToolTipText("Send File");
		sendButton.setBackground(SystemColor.scrollbar);
		sendButton.addActionListener(this);
		panel.add(sendButton);

		filename = new JTextField();
		filename.setBounds(0, 388, 213, 39);
		filename.setFont(new Font("heveltica", Font.PLAIN, 14));
		panel.add(filename);
		filename.setColumns(10);

		ip = new JTextField();
		ip.setBounds(94, 40, 208, 33);
		ip.setText(null);
		ip.setFont(new Font("heveltica", Font.PLAIN, 15));
		panel.add(ip);
		ip.setColumns(10);

		JLabel lblEnter = new JLabel("Destination: ");
		lblEnter.setBounds(10, 34, 89, 39);
		lblEnter.setFont(new Font("Heveltica", Font.PLAIN, 14));
		panel.add(lblEnter);

		progressBar = new JProgressBar();
		progressBar.setBounds(0, 441, 213, 33);
		progressBar.setVisible(false);

		progressBar.setStringPainted(true);
		progressBar.setForeground(new Color(50, 205, 50));
		panel.add(progressBar);

		JLabel connection = new JLabel("");
		connection.setBounds(185, 11, 117, 24);
		panel.add(connection);

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(312, 0, 236, 499);
		frmApplication.getContentPane().add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));

		userLog = new JTextArea();
		userLog.setEditable(false);
		userLog.setFont(new Font("heveltica", Font.PLAIN, 14));
		JScrollPane scrollPane = new JScrollPane(userLog);
		scrollPane.setBorder(BorderFactory.createTitledBorder("CONNECTED USERS"));
		panel_1.add(scrollPane, BorderLayout.CENTER);

		frmApplication.setVisible(true);

	}

	public static void userlog(String massage) {
		userLog.append(massage);
	}

	public static void clear() {
		userLog.setText(null);
	}

	public static void consoleLog(String string) {
		console.append(string + "\n");
	}

	public static void progress(int num) {
		progressBar.setValue(num);
	}

	public static void visible(boolean bool) {
		if (bool) {
			progressBar.setVisible(true);
		} else if (!bool) {
			progressBar.setVisible(false);
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == sendButton) {
			String conn = ip.getText();
			filename.setText(null);
			if (conn != null && absolutePath != null && file != null) {
				try {
					tcp.client(conn, file);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			} else {
				consoleLog("[ERR]: INVALID ENTRY");
			}
			file = null;
			conn = null;
		} else if (e.getSource() == selectButton) {

			int i = filechooser.showOpenDialog(frmApplication);
			if (i == filechooser.APPROVE_OPTION) {
				file = filechooser.getSelectedFile();
				absolutePath = file.getAbsolutePath();
				path = file.getName();
				filename.setText(path);

			}
		}
	}
}
