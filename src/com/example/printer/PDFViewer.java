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


public class PDFViewer extends Activity{

	private WebView webview;
	private int viewSize = 0;
	private float scale; 
	private String name; 
	private String path; 
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pdf_viewer_layout);
		Log.d("Alex", "in onCreate"); 
		Intent receivedIntent = getIntent(); 
		name=receivedIntent.getStringExtra("file_name"); 
		path=receivedIntent.getStringExtra("file_path");
		openPdfIntent(path); 
	}
	
	private void openPdfIntent(String path) {
        try {
            final Intent intent = new Intent(PDFViewer.this, Second.class);
            Log.d("Alex", "openPdfIntent"); 
            intent.putExtra(PdfViewerActivity.EXTRA_PDFFILENAME, path);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
		
}
