package com.example.printer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import net.sf.andpdf.pdfviewer.PdfViewerActivity;

public class SignalReceiver extends BroadcastReceiver{
	private final String RECEIVED_IP="printer.com.example.received_ip"; 
	private final String IP_DISAPPEARED="printer.com.example.ip_disappeared"; 
	@Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
		Log.d("Alex", "SignalReceiver onReceive!"); 
        if(intent.getAction().equals(RECEIVED_IP)) {
        	Log.d("Alex", "got IP!");
        	String ip=intent.getStringExtra("IP"); 
        	PicViewer.setPrinterIP(ip);
        	PdfViewerActivity.setPrinterIP(ip);
			ResponseCountdown.setFlag(true); 
        }
        else if(intent.getAction().equals(IP_DISAPPEARED)) {
        	Log.d("Alex", "no IP!");
        	PicViewer.setPrinterIP(null); 
        	PdfViewerActivity.setPrinterIP(null);
        	ResponseCountdown.setFlag(false); 
        }
        else Log.d("Alex", "receiver error!"); 
    }
}

