package UDPClientServer;

import java.io.*;
import java.net.*;
import java.util.concurrent.locks.ReentrantLock;

class UDPServer {
	private static final ReentrantLock lock = new ReentrantLock();
	public static final int SLOT_TIME_NANOS = 800000;
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

			System.out.println("RECEIVED: "
					+ sentence.substring(0, sentence.indexOf('\0')));

			new Thread(new Runnable() {
				public void run() {
					try {
						if (lock.isLocked()) {
							lock.lockInterruptibly();
							Thread.sleep(0, SLOT_TIME_NANOS);
							lock.unlock();
							sendCollision(IPAddress, port, sentence);
						} else {
							lock.lockInterruptibly();
							Thread.sleep(0, SLOT_TIME_NANOS);
							lock.unlock();
							sendSuccess(IPAddress, port, sentence);
						}
					}
					catch (InterruptedException ie) {
						while (lock.isLocked()) { }
						sendCollision(IPAddress, port, sentence);
					}
					catch (Exception e) {
						e.printStackTrace();
						if (lock.isHeldByCurrentThread())
							lock.unlock();
					}
				}
			}).start();
		}
	}

	public static void sendCollision(InetAddress IPAddress, int port,
			String message) {
		String sentence = "FALSE";
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
		String sentence = "TRUE";
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