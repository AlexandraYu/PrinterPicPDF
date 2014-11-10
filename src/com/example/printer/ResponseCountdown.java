package com.example.printer;

import com.example.printer.ListenUDPBroadcast;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ResponseCountdown implements Runnable{
//	private final int COUNTDOWN_RESET = 3000; 
	private final int IP_DISAPPEARED = 6000; 
	private static int count = 0; 
	private Handler myHandler; 
	private static boolean flag=false; 
	
	public ResponseCountdown(Handler h) { //to receive handler from ListenUDPBroadcast to reset countdown, h is given by ListenUDPBroadcast
		myHandler = h; 
	}
	
	public ResponseCountdown() {}
	
	@Override
	public void run() {
		Log.d("Alex", "ResponseCountdown run!"); 
		// TODO Auto-generated method stub
//		ListenUDPBroadcast listenUDP = new ListenUDPBroadcast();
//		listenUDP.receiveHandler(handler); 
		while (true) {
			Log.d("Alex", "flag is: "+flag); 
//			if(flag) count=0;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			count++;
			Log.d("Alex", "Here! count is: "+count); 
			if (count<10) continue; 
			else {			
				//the printer/AP is dead, send out a signal to clear out the current printer IP
				Message msg = myHandler.obtainMessage(IP_DISAPPEARED, null); 
				myHandler.sendMessage(msg); 
//				setFlag(false); 
//				count = 0; 
			}
		Log.d("Alex", "ResponseCountdown, count is: "+count); 	
		}
	}
/*	
	private Handler handler = new Handler() { //this handler will be given to reset the counter
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case COUNTDOWN_RESET: //receive msg from ListenUDPBroadcast to reset counter, meaning UDP packet is found
					Log.d("Alex", "COUNTDOWN_RESET"); 
					count = 0; 
					break;
			}
		}
	};
*/
	public static void setFlag(boolean setFlag){
		flag = setFlag; 
		Log.d("Alex", "setFlag, flag is: "+flag); 
		if (flag) count=0; 
	}
	

}
