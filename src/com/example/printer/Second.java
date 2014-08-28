package com.example.printer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import net.sf.andpdf.pdfviewer.PdfViewerActivity;

public class Second extends PdfViewerActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.pic_viewer_layout);
        Log.d("Alex", "in Second class"); 
        Context context = this; 
        ImageView imageView = (ImageView) findViewById(R.id.image); 
        Intent intent=getIntent(); 
        String path=intent.getStringExtra(PdfViewerActivity.EXTRA_PDFFILENAME); 
        Bitmap bMap = BitmapFactory.decodeFile(path);
        Log.d("Alex", "What's bMap? "+bMap); 
		if(bMap!=null) 
		{ 
			imageView.setImageBitmap(bMap);
		}
		else Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();*/
        Log.d("Alex", "in Second class");
        Intent intent=getIntent(); 
        String path=intent.getStringExtra(PdfViewerActivity.EXTRA_PDFFILENAME);
        Log.d("Alex", path);
    }

    public int getPreviousPageImageResource() {
    	Log.d("Alex", "1"); 
        return R.drawable.left_arrow;
    }

    public int getNextPageImageResource() {
    	Log.d("Alex", "2");
        return R.drawable.right_arrow;
    }

    public int getZoomInImageResource() {
    	Log.d("Alex", "3");
        return R.drawable.zoom_in;
    }

    public int getZoomOutImageResource() {
    	Log.d("Alex", "4");
        return R.drawable.zoom_out;
    }

	@Override
	public int getPdfPageNumberEditField() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPdfPageNumberResource() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPdfPasswordEditField() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPdfPasswordExitButton() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPdfPasswordLayoutResource() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPdfPasswordOkButton() {
		// TODO Auto-generated method stub
		return 0;
	}
}
