package UDPClientServer;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

import chapter2.problem52.Process;

class UDPClient extends Process {
	public static final long MAX_TIME = 4000; // 5000 * 0.8msec timeslots
	public static Integer[] LAMBDA = { 20, 18, 16, 14, 12, 10, 8, 6, 4 };
	private int numCollisions = 0;

	public int getNumCollisions() {
		return numCollisions;
	}

	public void resetNumCols() {
		numCollisions = 0;
	}

	public void incNumCols() {
		numCollisions++;
	}

	public UDPClient(int lambda) {
		super(lambda);
	}

	public int calculateBackOffTime() {
		int k = (int) (Math.pow(2, numCollisions));
		return r.nextInt(k);
	}

	public double calculateTimeToRun() {
		super.calculateTimeToRun();
		return -(lambda * (Math.log(r.nextDouble())));
	}

	public static void main(String args[]) throws Exception {

		JFrame jf = new JFrame();
		JPanel jp = new JPanel(new GridLayout(3,1));
		JPanel lambdaPanel = new JPanel(new BorderLayout());
		JPanel ipPanel = new JPanel(new BorderLayout());
		JButton sendButton = new JButton("SEND!");

		final JLabel ipLabel = new JLabel("IP Address");
		final JTextField ipField = new JTextField("192.168.43.160");
		ipPanel.add(ipLabel, BorderLayout.WEST);
		ipPanel.add(ipField, BorderLayout.EAST);
		
		final JComboBox<Integer> lambdaBox = new JComboBox<Integer>(LAMBDA);
		final JLabel lambdaLabel = new JLabel("Lambda");
		lambdaPanel.add(lambdaLabel, BorderLayout.WEST);
		lambdaPanel.add(lambdaBox, BorderLayout.EAST);

		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DatagramSocket clientSocket = null;
				try { 
					clientSocket = new DatagramSocket();
					InetAddress IPAddress = InetAddress
							.getByName(ipField.getText());
					byte[] sendData = new byte[1024];
					byte[] receiveData = new byte[1024];
					int success = 0;

					UDPClient p = new UDPClient((Integer) lambdaBox
							.getSelectedItem());
					long startTime = System.currentTimeMillis();
					while ((System.currentTimeMillis() - startTime) <= MAX_TIME) {

						try {
							sendData = ("" + success).getBytes();
							DatagramPacket sendPacket = new DatagramPacket(
									sendData, sendData.length, IPAddress, 9876);
							clientSocket.send(sendPacket);
							clientSocket.setSoTimeout(1000);
							DatagramPacket receivePacket = new DatagramPacket(
									receiveData, receiveData.length);
							clientSocket.receive(receivePacket);
							String modifiedSentence = new String(receivePacket
									.getData());

							if (modifiedSentence.toLowerCase().contains("true")) {
								System.out.println("YOU SENT ME TRUE "
										+ success);
								p.resetNumCols();
								long time = (long) (p.calculateTimeToRun() * UDPServer.SLOT_TIME_NANOS);
								success++;
								Thread.sleep(time / 1000000,
										(int) (time % 1000000));
							} else {
								System.out
										.println("YOU SENT ME FALSE. SADFACE D= !!");
								p.incNumCols();
								long BOTime = (long) (p.calculateBackOffTime() * UDPServer.SLOT_TIME_NANOS);
								Thread.sleep(BOTime / 1000000,
										(int) (BOTime % 1000000));
							}
						} catch (SocketTimeoutException e) {
							System.out.println("Timeout reached!!! " + e);
						}
					}
				} catch (Exception e) {
					
				} finally {
					if (clientSocket != null)
						clientSocket.close();
				}
			}
		});
		
		jp.add(ipPanel);
		jp.add(lambdaPanel);
		jp.add(sendButton);
		
		jf.add(jp);

		jf.setResizable(true);
		jf.setSize(300, 150);
		jf.setLocationRelativeTo(null);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}
}