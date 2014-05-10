package UDPClientServer;

import java.io.*;
import java.net.*;
import java.util.concurrent.locks.ReentrantLock;

class UDPServer
{
	private static final ReentrantLock lock = new ReentrantLock();
	private static Boolean collision = false;
	public static final long SLOT_TIME_NANOS = 800000L;
	private static DatagramSocket serverSocket;
	static {
		try {
			serverSocket = new DatagramSocket(9876);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
   public static void main(String args[]) throws Exception
      {
         byte[] receiveData;
         while(true)
           {
        	  receiveData = new byte[1024];
              DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
              serverSocket.receive(receivePacket);
              final InetAddress IPAddress = receivePacket.getAddress();
              final int port = receivePacket.getPort();
              final String sentence = new String( receivePacket.getData());
              System.out.println("RECEIVED: " + sentence.substring(0, sentence.indexOf('\0')));
              
              if (!lock.isLocked()) {
            	  new Thread(new Runnable() {
					public void run() {
						try {
							if (lock.tryLock()) {
								long startTime = System.nanoTime();
								while (System.nanoTime() - startTime < SLOT_TIME_NANOS) 
								{
									if (collision) {
										sendCollision(IPAddress, port, sentence);
										collision = false;
										lock.unlock();
										return;
									}
								}
								lock.unlock();
								sendSuccess(IPAddress, port, sentence);
							} else {
								System.out.println("SHIT GOT MESSED UP");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} 
            	  }).start();
              } else {
            	  sendCollision(IPAddress, port, sentence);
            	  collision = true;
              }
           }
      }
   
   public static void sendCollision(InetAddress IPAddress, int port, String message) throws IOException {
	   System.out.println("SENT: FALSE");
	   String sentence = "false " + message;
       DatagramPacket sendPacket = new DatagramPacket(sentence.getBytes(), sentence.length(), IPAddress, port);
       serverSocket.send(sendPacket);
   }
   
   public static void sendSuccess(InetAddress IPAddress, int port, String message) throws IOException {
	   System.out.println("SENT: TRUE");
	   String sentence = "true " + message;
       DatagramPacket sendPacket = new DatagramPacket(sentence.getBytes(), sentence.length(), IPAddress, port);
       serverSocket.send(sendPacket);
   }
}