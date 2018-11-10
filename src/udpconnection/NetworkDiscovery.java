package udpconnection;

import java.io.IOException;
import java.net.*;
import java.util.*;
import main_application.*;

public class NetworkDiscovery {
	private DatagramSocket socket;
	private static String massage = "Alive";
	private List<InetAddress> broadcastAddress;
	private volatile ArrayList<InetAddress> connectedUsers = new ArrayList<InetAddress>();
	private Thread t1;
	private Thread t2;
	private Thread t3;
	private Window window;
	private volatile boolean truth2;
	private volatile boolean truth1;

	public void main() throws InterruptedException {
		try {
			socket = new DatagramSocket(4444);
		} catch (SocketException e1) {
			Window.userlog("[ERR]: PORT 4444 IS ALREDY IN USE " + "\n" + "APPLICATION WILL EXIT IN 10SEC");
			Thread.sleep(5000);
			System.exit(1);
		}
		massageSender(socket);
		massageReciever(socket);
		t3 = new Thread(new Runnable() {
			public void run() {
				try {
					while (true) {
						Thread.sleep(13000);
						refresh();
						Thread.sleep(13000);
						refresh();
						Thread.sleep(13000);
						refresh();
						Thread.sleep(13000);
						t1.interrupt();
						t2.interrupt();
						massageSender(socket);
						massageReciever(socket);
					}
				} catch (InterruptedException e) {
					return;
				}
			}
		});
		t3.start();
	}

	public void refresh() {
		Window.clear();
		connectedUsers.clear();
	}

	public List<InetAddress> listAllBroadcastAddresses() throws SocketException {
		List<InetAddress> broadcastList = new ArrayList<>();
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			NetworkInterface networkInterface = interfaces.nextElement();

			if (networkInterface.isLoopback() || !networkInterface.isUp()) {
				continue;
			}

			networkInterface.getInterfaceAddresses().stream().map(a -> a.getBroadcast()).filter(Objects::nonNull)
					.forEach(broadcastList::add);
		}
		return broadcastList;
	}

	private void massageSender(DatagramSocket sock) {
		t1 = new Thread(new Runnable() {
			public void run() {
				try {
					broadcastAddress = listAllBroadcastAddresses();
					byte[] sendData = massage.getBytes();
					int size = broadcastAddress.size();
					int i = 0;
					while (true) {
						Thread.sleep(400);
						if (i >= size) {
							i = 0;
							continue;
						}
						// System.out.println("sending to "+broadcastAddress.get(i));
						DatagramPacket packet = new DatagramPacket(sendData, sendData.length, broadcastAddress.get(i),
								4444);
						sock.send(packet);
						i++;
					}
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					return;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		t1.start();
	}

	private void massageReciever(DatagramSocket sock) {
		t2 = new Thread(new Runnable() {
			public void run() {
				try {
					byte[] recieveData = new byte[massage.length() * 8];
					DatagramPacket packet = new DatagramPacket(recieveData, recieveData.length);
					String localName = (InetAddress.getLocalHost()).getHostName();
					InetAddress[] localIp = InetAddress.getAllByName(localName);
					while (true) {
						truth1 = true;
						truth2 = true;
						sock.receive(packet);
						InetAddress recieveip = packet.getAddress();
						for (InetAddress ip : connectedUsers) {
							truth1 = truth1 && !ip.equals(recieveip);
						}
						for (InetAddress localhost : localIp) {
							truth2 = truth2 && !localhost.equals(recieveip);
						}

						Thread.sleep(400);
						if (truth1 && truth2) {
							connectedUsers.add(recieveip);
							String hostName = recieveip.getHostName().toUpperCase();
							String hostIp = recieveip.getHostAddress().toUpperCase();
							Window.userlog(hostName + "\n");
							Window.userlog("    -->" + hostIp + "\n");

						}
					}
				} catch (InterruptedException e) {
					return;
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});
		t2.start();
	}

}
