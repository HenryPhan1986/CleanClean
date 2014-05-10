//package UDPClientServer;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

class UDPClient
{
   public static void main(String args[]) throws Exception
   {
/*
        JFrame jf = new JFrame();
        JPanel jp = new JPanel();
        JButton sendButton = new JButton("SEND!");
        jp.add(sendButton);
        jf.add(jp);

        jf.setSize(new Dimension(680, 680));
        jf.setResizable(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);*/

      BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
      DatagramSocket clientSocket = new DatagramSocket();
      InetAddress IPAddress = InetAddress.getByName("192.168.43.160");
      byte[] sendData = new byte[1024];
      byte[] receiveData = new byte[1024];

   while(true){
      try{
      String sentence = inFromUser.readLine();
      sendData = sentence.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
      clientSocket.send(sendPacket);
      clientSocket.setSoTimeout(1000);
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      clientSocket.receive(receivePacket);
      String modifiedSentence = new String(receivePacket.getData());
      System.out.println("FROM SERVER:" + modifiedSentence);

    }catch (SocketTimeoutException e) {
                // timeout exception.
                System.out.println("Timeout reached!!! " + e);
            }

    /*  if (modifiedSentence.contains("true")){
         System.out.println("YOU SENT ME TRUE");
      }else{
         System.out.println("YOU SENT ME FALSE. SADFACE!!");
      }*/
   
     // clientSocket.close();
      }

   }
}