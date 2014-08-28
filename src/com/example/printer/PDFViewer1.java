package com.example.printer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.RandomAccessFile;
//import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import net.sf.andpdf.pdfviewer.PdfViewerActivity;
import net.sf.andpdf.refs.HardReference;
import net.sf.andpdf.nio.ByteBuffer;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFImage;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFPaint;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.ImageView;


public class PDFViewer1 extends PdfViewerActivity{

	private WebView webview;
	private int viewSize = 0;
	private float scale; 
	private String name; 
	private String path; 
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pdf_viewer_layout);
		Log.d("Alex", "in onCreate"); 
		PDFImage.sShowImages = true; // show images
	    PDFPaint.s_doAntiAlias = true; // make text smooth
	    HardReference.sKeepCaches = true; // save images in cache
		webview = (WebView)findViewById(R.id.webview);
		webview.getSettings().setBuiltInZoomControls(true);//show zoom buttons
		webview.getSettings().setSupportZoom(true);//allow zoom
	    //get the width of the webview
		webview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
		{
			@Override
			public void onGlobalLayout()
			{
				Log.d("Alex", "in onGlobalLayout"); 
				viewSize = webview.getWidth();
				webview.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}
		});
	}

	protected void onResume() {
		super.onResume(); 
		Intent receivedIntent = getIntent(); 
		name=receivedIntent.getStringExtra("file_name"); 
		path=receivedIntent.getStringExtra("file_path");
		Log.d("Alex", "about to display PDF"); 
	    displayPDF(path);//load images
	}
	
	protected void onPause() {
		super.onPause(); 
	}

	@Override
	public int getNextPageImageResource() {
		// TODO Auto-generated method stub
		return 0;
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

	@Override
	public int getPreviousPageImageResource() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getZoomInImageResource() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getZoomOutImageResource() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void displayPDF(final String filePath) {
//		try {
			// run async
		AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
			//create and show a progress dialog
//	        ProgressDialog progressDialog = ProgressDialog.show(PDFViewer.this, "Opening...", "Please wait while opening the file..."); 
	        @Override
	        protected void onPostExecute(Void result) {
	        	//after async close progress dialog
				Log.d("Alex", "does it ever end??"); 
//	        	progressDialog.dismiss();
	        }
	        @Override
	        protected Void doInBackground(Void... params) {
	        	try{
	    			Log.d("Alex", "doInBackground"); 
	        	// select a document and get bytes
//	        	File file = new File(Environment.getExternalStorageDirectory().getPath()+"/randompdf.pdf");
	        	File file = new File(filePath);
	            RandomAccessFile raf = new RandomAccessFile(file, "r");
	            FileChannel channel = raf.getChannel();
	            ByteBuffer byteBuffer = ByteBuffer.NEW(channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()));
	            raf.close();
	            // create a pdf doc
	            PDFFile pdfFile = new PDFFile(byteBuffer); 
	            //Get the first page from the pdf doc
	            PDFPage PDFpage = pdfFile.getPage(1, true);
	            //create a scaling value according to the WebView Width
	            scale = viewSize / PDFpage.getWidth() * 0.95f;
	            //convert the page into a bitmap with a scaling value
	            Bitmap page = PDFpage.getImage((int)(PDFpage.getWidth() * scale), (int)(PDFpage.getHeight() * scale), null, true, true);
	            //save the bitmap to a byte array
	            ByteArrayOutputStream stream = new ByteArrayOutputStream();
	            page.compress(Bitmap.CompressFormat.PNG, 100, stream);
	            stream.close();
	            byte[] byteArray = stream.toByteArray();
	            //convert the byte array to a base64 string
	            String base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
	            //create the html + add the first image to the html
	            String html = "<!DOCTYPE html><html><body bgcolor=\"#7f7f7f\"><img src=\"data:image/png;base64,"+base64+"\" hspace=10 vspace=10><br>";
	            //loop through the rest of the pages and repeat the above
	            for(int i = 2; i <= pdfFile.getNumPages(); i++)
	            {
	            	Log.d("Alex", "i is? "+i); 
	            	PDFpage = pdfFile.getPage(i, true);
	            	page = PDFpage.getImage((int)(PDFpage.getWidth() * scale), (int)(PDFpage.getHeight() * scale), null, true, true);
	                stream = new ByteArrayOutputStream();
	                page.compress(Bitmap.CompressFormat.PNG, 100, stream);
	                stream.close();
	                byteArray = stream.toByteArray();
	                base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
	                html += "<img src=\"data:image/png;base64,"+base64+"\" hspace=10 vspace=10><br>";
	             }
				Log.d("Alex", "are we here??"); 
	            html += "</body></html>";
	            //load the html in the webview
	            webview.loadDataWithBaseURL("", html, "text/html","UTF-8", "");
	                    }
	                    catch (Exception e)
	                    {
	                        Log.d("CounterA", e.toString());
	                    }
	            return null;
	        }
	    }.execute();
		Log.d("Alex", "after executing"); 
	    System.gc();// run GC
//	    catch (Exception e)
//	    {
//	        Log.d("error", e.toString());
//	    }
	}
		
}
