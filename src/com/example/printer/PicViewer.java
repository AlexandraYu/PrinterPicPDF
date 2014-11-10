package com.example.printer;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.print.PrintHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

public class PicViewer extends Activity {
	private ImageView imageView;
	private String filePath;
	private String fileName; 
//	private MenuItem printKey; 
	private Context context;
	private Bitmap bMap;
	private FrameLayout frameLayout;
	private View dialogLayout; 
	private int copy = -1; 
	private NumberPicker numPicker; 
	private static final int MAX_CHOICE=9; 
	private static final int MIN_CHOICE=1;
	private static String ip; 
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pic_viewer_layout);
		frameLayout = (FrameLayout) findViewById(R.id.pic_viewer);
		frameLayout.getForeground().setAlpha(0);
		context=this; 		
		imageView = (ImageView) findViewById(R.id.image);
//		button = (Button) findViewById(R.id.bt_print);
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		dialogLayout = inflater.inflate(R.layout.popup,null);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("Alex", "in onCreateOptionsMenu, ip is: "+ip); 
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
//        printKey = menu.getItem(0);
        return super.onCreateOptionsMenu(menu);
    }
	
	public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
        case R.id.action_print:
            //print action
        	Log.d("Alex", "what is ip? "+ip); 
        	if(ip!=null) {
        		printPicture(ip);
        	}
        	else Toast.makeText(context, R.string.printer_disconnect_warning, Toast.LENGTH_SHORT).show(); 
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	
	protected void onPause() {
		super.onPause();
	}
	
	protected void onResume() {
		super.onResume(); 
		Intent receivedIntent = getIntent(); 
		filePath=receivedIntent.getStringExtra("file_path"); 
		fileName=receivedIntent.getStringExtra("file_name"); 
		bMap = BitmapFactory.decodeFile(filePath);
		if(bMap!=null) { 
			imageView.setImageBitmap(bMap);
		}
		else Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
/*		
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				printPicture(); 
				}
			});
			*/
	}
	
	public static void setPrinterIP(String ipAddress) {
		ip = ipAddress; 
	}
	public void printPicture(final String ipAddress) {
		/*
		PrintHelper photoPrinter = new PrintHelper(context);
		photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
	    Log.d("Alex", "color? "+photoPrinter.getColorMode()); 
	    Log.d("Alex", "orientation? "+photoPrinter.getOrientation());
	    photoPrinter.printBitmap(fileName, bMap); 
	    */
		/*
		frameLayout.getForeground().setAlpha(200); //dim
		PopupPicker popupPicker = new PopupPicker(context, printKey, filePath, frameLayout);
		popupPicker.popup();
		*/
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View npView = inflater.inflate(R.layout.popup, null);
		numPicker=(NumberPicker) npView.findViewById(R.id.numberPicker);
        numPicker.setMaxValue(MAX_CHOICE);  
        numPicker.setMinValue(MIN_CHOICE);    
        numPicker.setValue(MIN_CHOICE); 
        numPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener () {
        	public void onValueChange(NumberPicker view, int oldValue, int newValue) {
        		Log.d("Alex", "newValue is: "+newValue); 
        		
        		if (newValue<1)
        			newValue = 1; 
        		copy = newValue; 
        	}
        });
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle(R.string.print_copy_title); 
		alertDialogBuilder
			.setMessage(R.string.print_copy_instruction)
			.setCancelable(false)
			.setView(npView)
			.setPositiveButton(R.string.dialog_ok,new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					if (copy<0)
						copy=1; 
					Toast.makeText(context, "Print out "+String.valueOf(copy)+" copies", Toast.LENGTH_SHORT).show(); 
					//send copy to print......
					Print print = new Print(context, copy, filePath, ipAddress); 
					print.runGetPrinterAttributeProcess();
					print.runValidateJobProcess();
					print.runPrintJobProcess();
					copy=-1; 
					dialog.cancel();
				}
			})
			.setNegativeButton(R.string.dialog_cancel,new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					copy=-1; 
					dialog.cancel();
				}
			});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
//		printKey.setEnabled(false); 
	}
}
