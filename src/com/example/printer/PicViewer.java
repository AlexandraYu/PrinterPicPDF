package com.example.printer;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.print.PrintHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class PicViewer extends Activity {
	private ImageView imageView;
	private String filePath;
	private String fileName; 
//	private Button button; 
	private Context context;
	private Bitmap bMap;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pic_viewer_layout);
		
		context=this; 		
		imageView = (ImageView) findViewById(R.id.image);
//		button = (Button) findViewById(R.id.bt_print);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
 
        return super.onCreateOptionsMenu(menu);
    }
	
	public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
        case R.id.action_print:
            //print action
        	printPicture();
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
		if(bMap!=null) 
		{ 
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
	
	public void printPicture() {
		PrintHelper photoPrinter = new PrintHelper(context);
		photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
	    Log.d("Alex", "color? "+photoPrinter.getColorMode()); 
	    Log.d("Alex", "orientation? "+photoPrinter.getOrientation());
	    photoPrinter.printBitmap(fileName, bMap);
	}
	
}
