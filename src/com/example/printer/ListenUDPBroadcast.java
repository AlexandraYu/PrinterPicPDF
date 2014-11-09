package com.example.printer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.Charset;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ListenUDPBroadcast implements Runnable{
//	private Context context;
	private String ip= null; 
	private final int PORT = 7411; 
	private final String BOARD_ATTRIBUTE = "ANNOU"; 
	private int counter=0;
	private Handler handler;// anotherHandler; 
	private final int RECEIVED_IP = 2000; 
	private final int IP_DISAPPEARED = 6000;
//	private final int COUNTDOWN_RESET = 3000; 
	
	public ListenUDPBroadcast(Handler h) {
		Log.d("Alex", "ListenUDPBroadcast is called!"); 
		handler = h; 
	}
	
	public ListenUDPBroadcast(){}
	
//	ResponseCountdown responseCountdown = new ResponseCountdown();
	@Override
	public void run() {
		Log.d("Alex", "in ListenUDPBroadcast run()"); 
		// TODO Auto-generated method stub
		DatagramSocket socket = null; 
//		Log.d("Alex", "ListenUDPBroadcast run!, anotherHandler is: "+anotherHandler); 
		try {
			socket= new DatagramSocket(null);
			socket.setReuseAddress(true); 
			socket.bind(new InetSocketAddress(PORT)); //printer plugin board sends out broadcast that will be received through port 7411 
		} catch (SocketException e) {
	// TODO Auto-generated catch block
			Log.d("Alex", "socket is? "+socket); 
			e.printStackTrace();
		}
		if(socket!=null) 
		{
			byte[] buf = new byte[1024];
//			while(counter<10) { //set to do this 10 times for now... 
			while(true){
				Log.d("Alex", "counter is: "+counter); 
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				try {
					socket.receive(packet); //if the printer isn't on, the process will basically get stuck here... 
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					byte[] data = packet.getData(); 

					InputStreamReader input = new InputStreamReader(new ByteArrayInputStream(data), Charset.forName("UTF-8"));
					StringBuilder str = new StringBuilder();
					for (int value; (value = input.read()) != -1; )
						str.append((char) value);
					Log.d("Alex", "str is: "+str); 
					boolean check = str.toString().contains(BOARD_ATTRIBUTE); 
					Log.d("Alex", "check is: "+check);
					if(check) { //found suitable printer/AP within this network
						ip = packet.getAddress().getHostAddress();
						Log.d("Alex", "ip : "+ ip);	
						Message message = handler.obtainMessage(RECEIVED_IP, ip); //int what, Object obj
						handler.sendMessage(message);
						ResponseCountdown.setFlag(true); 
						
//						Log.d("Alex", "anotherHandler is: "+anotherHandler);
//						Message message2 = anotherHandler.obtainMessage(COUNTDOWN_RESET);
//						anotherHandler.sendMessage(message2); 
//						break; 
					}
					else { //found UDP packet but not from the suitable printer
						//got wrong packet, do nothing. 
					}
//					Message message = handler.obtainMessage(RECEIVED_IP, ip);
//					handler.sendMessage(message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.d("Alex", "socket didn't receive packet? "); 
					e.printStackTrace();
				}
				counter++; 
			}
		}
	}
/*
	public void receiveHandler(Handler handler) { 
		anotherHandler = handler; 
		Log.d("Alex", "in receiveHandler, anotherHandler is: "+anotherHandler);
	}
	*/
}
