package UDPClientServer;

import java.io.*;
import java.net.*;
import java.util.concurrent.locks.ReentrantLock;

class UDPServer {
	private static final ReentrantLock lock1 = new ReentrantLock();
	private static final ReentrantLock lock2 = new ReentrantLock();
	public static final int SLOT_TIME_NANOS = 800000;
	private static Boolean collision = false;
	private static DatagramSocket serverSocket;
	static {
		try {
			serverSocket = new DatagramSocket(9876);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws Exception {
		byte[] receiveData;
		while (true) {
			receiveData = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			serverSocket.receive(receivePacket);
			final InetAddress IPAddress = receivePacket.getAddress();
			final int port = receivePacket.getPort();
			final String sentence = new String(receivePacket.getData());

			System.out.println("(" + IPAddress.getHostAddress() + ")RECEIVED: "
					+ sentence.substring(0, sentence.indexOf('\0')));

			new Thread(new Runnable() {
				public void run() {
					try {
						if (lock1.tryLock()) {
							Thread.sleep(0, SLOT_TIME_NANOS);
							if (lock2.isLocked()) {
								while (lock2.isLocked()) {}
								lock1.unlock();
								sendCollision(IPAddress, port, sentence);
								return;
							}
							lock1.unlock();
							sendSuccess(IPAddress, port, sentence);
						} else {
							lock2.lock();
							Thread.sleep(0, SLOT_TIME_NANOS);
							lock2.unlock();
							sendCollision(IPAddress, port, sentence);
						}
					}
					catch (Exception e) {
						e.printStackTrace();
						if (lock1.isHeldByCurrentThread()) {
							lock1.unlock();
						}
						if (lock2.isHeldByCurrentThread()) {
							lock2.unlock();
						}
					}
				}
			}).start();
		}
	}

	public static void sendCollision(InetAddress IPAddress, int port,
			String message) {
		String sentence = "(" + IPAddress.getHostAddress() + ")FALSE";
		System.out.println(sentence + ": " + message.substring(0, message.indexOf('\0')));
		DatagramPacket sendPacket = new DatagramPacket(sentence.getBytes(),
				sentence.length(), IPAddress, port);
		try {
			serverSocket.send(sendPacket);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void sendSuccess(InetAddress IPAddress, int port,
			String message) throws IOException {
		String sentence = "(" + IPAddress.getHostAddress() + ")TRUE";
		System.out.println(sentence + ": " + message.substring(0, message.indexOf('\0')));
		DatagramPacket sendPacket = new DatagramPacket(sentence.getBytes(),
				sentence.length(), IPAddress, port);
		try {
			serverSocket.send(sendPacket);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}