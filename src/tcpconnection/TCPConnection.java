package tcpconnection;

import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

import java.awt.*;

import main_application.Window;
import udpconnection.NetworkDiscovery;

public class TCPConnection {
	private Thread server;
	private ServerSocket serverSocket;
	private Socket[] socketS = new Socket[1000];
	int i = 0;
	private static Window window;
	private int z;

	public static void main(String[] args) throws Exception {
		window = new Window();
		NetworkDiscovery nd = new NetworkDiscovery();
		nd.main();
		TCPConnection tcp = new TCPConnection();
		tcp.server();
	}

	// to receive incoming files
	public void server() {
		server = new Thread(new Runnable() {
			public void run() {
				try {
					serverSocket = new ServerSocket(4445);
					while (true) {
						new Thread(new Runnable() {
							public void run() {
								try {
									final int z = i;
									socketS[z] = serverSocket.accept();
									int bytesRead;
									DataInputStream dis = new DataInputStream(socketS[z].getInputStream());
									InetAddress ipx = socketS[z].getInetAddress();
									String fileName = dis.readUTF();
									Window.consoleLog("RECIEVING " + fileName.toUpperCase());
									File file = FileSystemView.getFileSystemView().getHomeDirectory();
									String desktop = file.getAbsolutePath();
									File output = new File(desktop+"\\Recieved\\");
									output.mkdirs();
									String fileoutput = output.getAbsolutePath()+"\\"+fileName;
									OutputStream os = new FileOutputStream(fileoutput);
									long size = dis.readLong();
									byte[] buffer = new byte[1024 + 2048];
									long total = size;
									long initialize = 0;
									new Progress();
									Progress.file(fileName);
									while (size > 0 && (bytesRead = dis.read(buffer, 0,
											(int) Math.min(buffer.length, size))) != -1) {
										os.write(buffer, 0, bytesRead);
										size -= bytesRead;
										initialize += (long) bytesRead;
										float x = (float) initialize / total;
										float w = x * 100;
										int num = (int) w;
										// System.out.println(initialize);
										Progress.progress(num);
										if (num >= 100) {
											Thread.sleep(2000);
											Progress.frame.setVisible(false);
										}
									}
									os.close();
									dis.close();
								} catch (IOException e) {
									e.printStackTrace();
									return;
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).start();
						Thread.sleep(2000);
						i += 1;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				} catch (IOException e) {
					Window.consoleLog("[ERR]: ERROR SERVER COULD NOT BE STARTED");
					return;
				}
			}
		});
		server.start();
	}

	// To send files
	public void client(String ip, File file) throws InterruptedException {

		new Thread(new Runnable() {
			public void run() {
				try {
					int port = 4445;
					InetAddress recipient = InetAddress.getByName(ip);
					Socket sender = new Socket(recipient, port);
					byte[] buffer = new byte[2048 + 1024];
					long fileLength = file.length();
					FileInputStream fis = new FileInputStream(file);
					DataInputStream dis = new DataInputStream(new BufferedInputStream(fis));
					Window.consoleLog("SENDING " + file.getName().toUpperCase());
					int bytesRead;
					DataOutputStream dos = new DataOutputStream(sender.getOutputStream());
					dos.writeUTF(file.getName());
					dos.writeLong(file.length());
					long init = 0;
					long total = fileLength;
					long bytes = 0;
					Window.visible(true);
					while (fileLength > 0
							&& (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, fileLength))) != -1) {

						dos.write(buffer, 0, bytesRead);
						dos.flush();
						bytes = bytes + (long) bytesRead;
						init += bytesRead;
						float w = ((float) (init) / (total));
						float x = w * 100;
						z = (int) (x);
						Window.progress(z);
						fileLength -= bytesRead;
						if (z >= 100) {
							Thread.sleep(2000);
							Window.visible(false);
						}
					}
					sender.close();
					dis.close();
				} catch (UnknownHostException e) {
					Window.consoleLog("[ERR]: INVALID IP");
					return;
				} catch (FileNotFoundException e) {
					Window.consoleLog("[ERR]: FILE NOT FOUND");
					return;
				} catch (IOException e) {
					Window.consoleLog("[ERR]:ERROR IN SOCKET");
					return;
				} catch (InterruptedException e) {
					Window.consoleLog("[ERR]:INTURRUPTEDEXCEPTION");
					e.printStackTrace();
				}
			}

		}).start();
		

	}

}
