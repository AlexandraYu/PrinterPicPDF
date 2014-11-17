package com.example.printer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import net.sf.andpdf.pdfviewer.PdfViewerActivity;

public class SignalReceiver extends BroadcastReceiver{
	private final String RECEIVED_IP="printer.com.example.received_ip"; 

	@Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
		Log.d("Alex", "SignalReceiver onReceive!"); 
        if(intent.getAction().equals(RECEIVED_IP)) {
        	Log.d("Alex", "got IP!");
        	String ip=intent.getStringExtra("IP"); 
        	PicViewer.setPrinterIP(ip);
        	PdfViewerActivity.setPrinterIP(ip);
        	long time=System.currentTimeMillis();
        	Log.d("Alex", "broadcast time! "+time);
        	PicViewer.setGotPacketTime(time); 
        	PdfViewerActivity.setGotPacketTime(time); ; 
        }
        else Log.d("Alex", "receiver error!"); 
    }
}

