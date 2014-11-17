package com.example.printer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.example.printer.DisplayPic.ShowPictureList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayPDF extends Activity {
	private static final String PATH_PRIFIX = "/Android/data";
	private Context context; 
	private ArrayList<PDFFileInfo> pdfFileList;	 
	private ListView listView;
	private final int MENU_LIST1 = Menu.FIRST; 	 /* menu parameters*/
	private final String START_PDF_VIEWER_INTENT="com.example.printer.VIEW_MY_PDF";
	private final int UPDATE_STATUS_COMPLETE=2000;
	private ProgressDialog progressDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		pdfFileList = new ArrayList<PDFFileInfo>();
		listView = (ListView) findViewById(R.id.listview);
		listView.setAdapter(new PDFFileListAdapter(context, pdfFileList));
		clickItem(listView); 
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		int idGroup1 = 0; 
		int orderMenuItem1 = Menu.NONE; 
		menu.add(idGroup1, MENU_LIST1, orderMenuItem1, R.string.show_pic); 
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case MENU_LIST1:
			Intent intent = new Intent(this, DisplayPic.class);
			startActivity(intent); 
			this.finish(); 
			break;
		default:
			Log.d("Error", "Menu error!"); 
		}
		return super.onOptionsItemSelected(item);
	}	

	protected void onPause() {
		super.onPause();
	}
	
	protected void onResume() {
		super.onResume();
		pdfFileList.clear();
		progressDialog = ProgressDialog.show(context, getString(R.string.progress_dialog_title), getString(R.string.progress_dialog_content), false); //since it might take a while for info to load
//		getPDFfiles();
		ShowPDFList showPDFList = new ShowPDFList(); 
		new Thread(showPDFList). start();
		/*Collections.sort(pdfFileList, new Comparator<PDFFileInfo>() {
			public int compare(PDFFileInfo a, PDFFileInfo b){
				return a.getName().compareTo(b.getName()); 
			}
		});*/
	}
	
	public class ShowPDFList implements Runnable {
		public void run() {
			Log.d("Alex", "will call getPDFFiles!"); 
			getPDFfiles(handler);
		}
	}
	
	public void clickItem(ListView currentView) {
		currentView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				String path=pdfFileList.get(position).getPath();
				String name=pdfFileList.get(position).getName(); 
//				Toast.makeText(context, path, Toast.LENGTH_SHORT).show();
				Intent viewPDFIntent = new Intent(START_PDF_VIEWER_INTENT);
				viewPDFIntent.putExtra("file_path", path);
				viewPDFIntent.putExtra("file_name", name); 
				startActivity(viewPDFIntent);
			}
		});
	}
	
	public boolean forbiddenPath(String path) {
		String pathFilter=Environment.getExternalStorageDirectory().getAbsolutePath() + PATH_PRIFIX;
		Log.d("Alex", "pathFilter is "+pathFilter); 
		if (path.startsWith(pathFilter)) return true;
		else 
			return false;
	}
	
	public void getPDFfiles(Handler h) {
		ContentResolver pdfFileResolver = context.getContentResolver();
		Uri uri = MediaStore.Files.getContentUri("external");

		String[] projection = null;
//		String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE; //exclude media files
//		String[] selectionArgs = null;
		String sortOrder = null;
//		Cursor allNonMediaFiles = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
		String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
		String[] selectionArgsPdf = new String[]{ mimeType };
		Cursor pdfFileCr = pdfFileResolver.query(uri, projection, selectionMimeType, selectionArgsPdf, sortOrder);
		Log.d("Alex", "pdfFileCr is: "+pdfFileCr);
		if(pdfFileCr==null) pdfFileList.add(new PDFFileInfo(Integer.toString(R.string.no_pdf_found), "", "")); 
		if(pdfFileCr!=null && pdfFileCr.moveToFirst()){
			//get columns
//			int nameColumn = pdfFileCr.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//			int sizeColumn = pdfFileCr.getColumnIndex(OpenableColumns.SIZE);
			int nameColumn = pdfFileCr.getColumnIndex(MediaStore.Files.FileColumns.TITLE);
			int sizeColumn = pdfFileCr.getColumnIndex(MediaStore.Files.FileColumns.SIZE);
			int pathColumn = pdfFileCr.getColumnIndex(MediaStore.Files.FileColumns.DATA);
			
			//add pdf file to list
			do {
				String thisName = pdfFileCr.getString(nameColumn);
				String thisSize = pdfFileCr.getString(sizeColumn);
				String thisPath = pdfFileCr.getString(pathColumn); 
				Log.d("Alex", "thisName is: "+thisName);
				Log.d("Alex", "thisSize is: "+thisSize);
				Log.d("Alex", "thisPath is: "+thisPath);
				if (!forbiddenPath(thisPath)) 
					pdfFileList.add(new PDFFileInfo(thisName, thisSize, thisPath));
			}
			while (pdfFileCr.moveToNext());
				Log.d("Alex", "pdfFileList size is: "+pdfFileList.size());
			}
		Message message = h.obtainMessage(UPDATE_STATUS_COMPLETE);
		h.sendMessage(message);
	}
	
	public class PDFFileListAdapter extends BaseAdapter {
		private ArrayList<PDFFileInfo> myPDFList = null;
//		private LayoutInflater inflater;
		private Context context;
		public PDFFileListAdapter(Context ctx, ArrayList<PDFFileInfo> inputList) {
			myPDFList=inputList;
			context=ctx;
//			inflater=LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return myPDFList.size();
		}
		
		@Override
		public PDFFileInfo getItem(int position) {
			// TODO Auto-generated method stub
			return myPDFList.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			//map to pdf file layout
			
			ViewHolderPDF viewHolderPDF;
			if(convertView==null){
				// inflate the layout
				LayoutInflater inflater = ((Activity) context).getLayoutInflater();
				convertView = inflater.inflate(R.layout.pdf_file, parent, false);
				
				//set up the ViewHolder
				viewHolderPDF = new ViewHolderPDF();
				viewHolderPDF.fileName = (TextView) convertView.findViewById(R.id.pdf_name);
				viewHolderPDF.fileSize = (TextView) convertView.findViewById(R.id.pdf_size);
				// store the holder with the view.
				convertView.setTag(viewHolderPDF);
			}
			else{
				// we've just avoided calling findViewById() on resource every time but just use the viewHolder
				viewHolderPDF = (ViewHolderPDF) convertView.getTag();
			}
			// PDF file name and size based on the position
			PDFFileInfo pdfFile = getItem(position);
			// assign values if the object is not null
			if(pdfFile != null) {
				// get the TextView from the ViewHolder and then set the text (item name) and tag (item ID) values
				viewHolderPDF.fileName.setText(pdfFile.getName());
				viewHolderPDF.fileSize.setText(pdfFile.getSize());
			}
			return convertView;
		}
	}
	
	static class ViewHolderPDF {
		TextView fileName;
		TextView fileSize; 
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case UPDATE_STATUS_COMPLETE:
					Log.d("Alex", "Am I HERE??"); 
					Collections.sort(pdfFileList, new Comparator<PDFFileInfo>() {
						public int compare(PDFFileInfo a, PDFFileInfo b){
							return a.getName().compareTo(b.getName()); 
						}
					});
					progressDialog.dismiss(); //info loaded ready, progress dialog can be dismissed.
					((BaseAdapter) listView.getAdapter()).notifyDataSetChanged(); //do this AFTER dismissing the progressDialog in order to avoid UI being too busy and getting stuck
					break;
			}
		}
	};
}

