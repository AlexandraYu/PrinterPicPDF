package com.example.printer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class Print {
	private Context context; 
	private int quantity;
	private String filePath; 
	private String ip; 
	private byte[] totalPrintJobByte; 
	private String urlStr;//="http://192.168.0.101:631"; 
	private String urlLocStr;//="http://192.168.0.101:631/USB1_LQ";
	private byte[] respCodePrinterAttriBuf = new byte []{0x0a, 0x0a, 0x0a, 0x0a};
	private byte[] respCodeValidateJobBuf = new byte []{0x0a, 0x0a, 0x0a, 0x0a};
	private byte[] respCodePrintJobBuf = new byte []{0x0a, 0x0a, 0x0a, 0x0a};
	private byte[] respCodeJobAttriBuf = new byte []{0x0a, 0x0a, 0x0a, 0x0a};
			
	public Print(Context c, int copy, String path, String ipAddress) {
		context = c; 
		quantity = copy; 
		filePath = path; 
		ip = ipAddress; 
		urlStr = "http://"+ip+":631";
		urlLocStr = urlStr+"/USB1_LQ";
	}
	
	public void excutePrint() {
		Thread getPrinterAttriProc = new Thread(getPrinterAttributes); 
		getPrinterAttriProc.start(); 
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("Alex", "respCodePrinterAttriBuf[2]= "+respCodePrinterAttriBuf[2]); 
		Log.d("Alex", "respCodePrinterAttriBuf[3]= "+respCodePrinterAttriBuf[3]); 
		if (respCodePrinterAttriBuf[2]==(byte)0x00 && respCodePrinterAttriBuf[3]==(byte)0x00) 
		{	
			Thread validateJobProc = new Thread(validatePrintJob); 
			validateJobProc.start();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			Toast.makeText(context, "Printer attribute error.", Toast.LENGTH_SHORT).show(); 
		}
		if (respCodeValidateJobBuf[2]==(byte)0x00 && respCodeValidateJobBuf[3]==(byte)0x00)
		{
			Thread printJobProc = new Thread(sendPrintJob);
			printJobProc.start(); 
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Thread getJobAttriProc = new Thread(getJobAttributes); 
			getJobAttriProc.start(); 
		}
		else {
			Toast.makeText(context, "Validate job error.", Toast.LENGTH_SHORT).show(); 
		}
	}
/*	
	public void runPrintJobProcess() {
		Log.d("Alex", "quantity is: "+quantity); 
		byte[] quantityArray = intToByteArray(quantity);
		
		int j=0;
		for(int i=0; i<4; i++) { 
			printJobByte[221+j] = quantityArray[i]; 
			j++; 
		} 
		Log.d("Alex", "quantityArray is: "+quantityArray[0]);
		Log.d("Alex", "quantityArray is: "+quantityArray[1]);
		Log.d("Alex", "quantityArray is: "+quantityArray[2]);
		Log.d("Alex", "quantityArray is: "+quantityArray[3]); 

		Log.d("Alex", "printJobByte[218] is: "+printJobByte[218]);
		Log.d("Alex", "printJobByte[219] is: "+printJobByte[219]);
		Log.d("Alex", "printJobByte[220] is: "+printJobByte[220]);
		Log.d("Alex", "printJobByte[221] is: "+printJobByte[221]);
		Log.d("Alex", "printJobByte[222] is: "+printJobByte[222]); 
		Log.d("Alex", "printJobByte[223] is: "+printJobByte[223]); 
		Log.d("Alex", "printJobByte[224] is: "+printJobByte[224]); 
		Log.d("Alex", "printJobByte[225] is: "+printJobByte[225]);
		Log.d("Alex", "printJobByte[226] is: "+printJobByte[226]);
		Log.d("Alex", "printJobByte[227] is: "+printJobByte[227]);
		Log.d("Alex", "printJobByte[228] is: "+printJobByte[228]);

		File file = new File(filePath);
		Log.d("Alex", "filePath is: "+filePath);
		Log.d("Alex", "file is: "+file); 
		byte[] fileByte = convertFileToByte(file); 
		totalPrintJobByte = new byte[printJobByte.length+fileByte.length];
		System.arraycopy(printJobByte, 0, totalPrintJobByte, 0, printJobByte.length);
		System.arraycopy(fileByte, 0, totalPrintJobByte, printJobByte.length, fileByte.length);
		Thread threadSendPrintJob =new Thread(sendPrintJob); 
		threadSendPrintJob.start();
	}
	
	public void runValidateJobProcess() {
		Thread threadValidateJobAttributes = new Thread(getJobAttributes);  
		threadValidateJobAttributes.start(); 
	}
	
	public void runGetPrinterAttributeProcess() {
		Thread threadGetPrinterAttributes = new Thread(getPrinterAttributes); 
		threadGetPrinterAttributes.start();
	}
		
*/	
	public byte[] intToByteArray(int value) {
	    return new byte[] {
	    		(byte)((value >>> 24) & 0xff),
	            (byte)((value >>> 16) & 0xff),
	            (byte)((value >>> 8) & 0xff),
	            (byte)(value & 0xff)};
	}
	
	public byte[] convertFileToByte(File file) {
		FileInputStream fileInputStream =null; 
		byte[] fileByte = null; 
		try {
			fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("Alex", "fileInputStream is: "+fileInputStream); 
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    byte[] buf = new byte[1024];
	    try {
	    	for (int readNum; (readNum = fileInputStream.read(buf)) != -1;) {
	            bos.write(buf, 0, readNum); //no doubt here is 0
	            //Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
	            Log.d("Alex ","read: " + readNum + " bytes.");
	        }
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
		return fileByte = bos.toByteArray();
	}
		
	public byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
		
	public byte[] convertPicToByte(File file) throws IOException {
		RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r"); 
		try{
		long longLength = file.length(); 
		int length = (int) longLength; 
		if(length != longLength)
			throw new IOException("File size >= 2GB"); 
		byte[] byteData = new byte[length]; 
		randomAccessFile.readFully(byteData); 
		return byteData; 
		} finally {
			randomAccessFile.close(); 
		}
	}
		
	private String getStringFromInputStream(InputStream is) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
		}
		is.close();
		String state = os.toString();
		os.close();
		return state;
	}
	
	Runnable getPrinterAttributes = new Runnable () {
		OutputStream outputStream=null;
		InputStream inputStream=null; 
		public void run() {
			Log.d("Alex", "run getPrinterAttributes");
			try {
				URL myUrl = new URL(urlStr);
				HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/ipp");
				outputStream = connection.getOutputStream();
				outputStream.write(getPrinterAttri);
				outputStream.flush();
				outputStream.close(); 
				int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                	Log.d("Alex", "OK!");
                	 //see what's in response from the printer...
                    inputStream = connection.getInputStream();
                    StringBuffer stringBufPrinterAttri = new StringBuffer();
                    int readNum; 
                    while ((readNum = inputStream.read()) != -1) {
                    	stringBufPrinterAttri.append((char) readNum);
                    }
                    Log.d("Alex", "getPrinterAttributes, stringBuf is: "+stringBufPrinterAttri);
                    byte[] byteArrayPrinterAttri = stringBufPrinterAttri.toString().getBytes(); 
                    System.arraycopy(byteArrayPrinterAttri, 0, respCodePrinterAttriBuf, 0, 4);
                    //3rd and 4th bytes are status-codes
                    Log.d("Alex", "respCodePrinterAttriBuf[2] is: "+respCodePrinterAttriBuf[2]); 
                    Log.d("Alex", "respCodePrinterAttriBuf[3] is: "+respCodePrinterAttriBuf[3]);
                    inputStream.close(); 
                } 
                else
                    Log.d("Alex", "Failed: " + responseCode);
			} catch (IOException e) {
					Log.d("Alex", "IOException--getPrinterAttributes");
					e.printStackTrace();
			}
		}
	};
	
	
	Runnable validatePrintJob = new Runnable () {
		OutputStream outputStream=null;
		InputStream inputStream=null; 
		public void run() {
			Log.d("Alex", "run validatePrintJob");
			try {
				URL myUrl = new URL(urlStr);
				HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/ipp");
				outputStream = connection.getOutputStream();
				outputStream.write(validateJob);
				outputStream.flush();
				outputStream.close(); 
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                	Log.d("Alex", "OK!");
                	 //see what's in response from the printer...
                    inputStream = connection.getInputStream();
                    StringBuffer stringBufValidateJob = new StringBuffer();
                    int readNum; 
                    while ((readNum = inputStream.read()) != -1) {
                    	stringBufValidateJob.append((char) readNum);
                    }
                    Log.d("Alex", "validatePrintJob, stringBufValidateJob is: "+stringBufValidateJob);
                    byte[] byteArrayPrinterAttri = stringBufValidateJob.toString().getBytes(); 
                    System.arraycopy(byteArrayPrinterAttri, 0, respCodeValidateJobBuf, 0, 4);
                    //3rd and 4th bytes are status-codes
                    Log.d("Alex", "respCodeValidateJobBuf[2] is: "+respCodeValidateJobBuf[2]); 
                    Log.d("Alex", "respCodeValidateJobBuf[3] is: "+respCodeValidateJobBuf[3]);
                    inputStream.close(); 
                } 
                else
                    Log.d("Alex", "Failed: " + responseCode);
			} catch (IOException e) {
					Log.d("Alex", "IOException--validatePrintJob");
					e.printStackTrace();
			}
		}
	};
	
	Runnable sendPrintJob = new Runnable () {
		OutputStream outputStream=null;
		InputStream inputStream=null; 
		public void run() {
			Log.d("Alex", "run sendPrintJob!");
			int delimiter_length = 4 ;
			byte[] chunkLenByteArray = Integer.toHexString(printJob.length).getBytes(); //length in hex for each chunk's size
			int length_byte_length = chunkLenByteArray.length; 
			Log.d("Alex", "length = "+(delimiter_length+length_byte_length)); 
			Log.d("Alex", "chunk size: "+(printJob.length+length_byte_length+delimiter_length)); 
			Log.d("Alex", "in sendPrintJob, filePath is: "+filePath);
			File file = new File(filePath);
			byte[] fileByte = convertFileToByte(file); 
			totalPrintJobByte = new byte[printJob.length+fileByte.length];
			System.arraycopy(printJob, 0, totalPrintJobByte, 0, printJob.length);
			Log.d("Alex", "quantity is: "+quantity); 
			//convert quantity gotten from user to byte array
			byte[] quantityArray = intToByteArray(quantity);
			//copy the quantity byte array to totalPrintJobByte, replacing from 221st byte. 
			//the quantity is decided by 4 bytes, starting from 221st byte. 
			int j=0;
			for(int i=0; i<4; i++) { 
				totalPrintJobByte[221+j] = quantityArray[i]; 
				j++; 
			} 
			System.arraycopy(fileByte, 0, totalPrintJobByte, printJob.length, fileByte.length);
			try {
				URL myUrl = new URL(urlStr);
				HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/ipp");
				connection.setChunkedStreamingMode(printJob.length+length_byte_length+delimiter_length);
				outputStream = connection.getOutputStream();
				outputStream.write(totalPrintJobByte);
				Log.d("Alex", "totalPrintJobByte.length is: "+totalPrintJobByte.length); 
				outputStream.flush();
				outputStream.close();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                	Log.d("Alex", "OK: ");
                	//see what's in response from the printer...
                	inputStream = connection.getInputStream();
                    StringBuffer stringBufSendJob = new StringBuffer();
                    int readNum; 
                    while ((readNum = inputStream.read()) != -1) {
                    	stringBufSendJob.append((char) readNum);
                    }
                    Log.d("Alex", "sendPrintJob, stringBufSendJob is: "+stringBufSendJob);
                    byte[] byteArraySendJob = stringBufSendJob.toString().getBytes(); 
                    System.arraycopy(byteArraySendJob, 0, respCodePrintJobBuf, 0, 4);
                    Log.d("Alex", "respCodePrintJobBuf[2] is: "+respCodePrintJobBuf[2]); 
                    Log.d("Alex", "respCodePrintJobBuf[3] is: "+respCodePrintJobBuf[3]);
                    inputStream.close(); 
                } 
                else {
                    Log.d("Alex", "failed" + responseCode);
                    Toast.makeText(context, "Send print job error!", Toast.LENGTH_SHORT).show(); 
                } 
			} catch (IOException e) {
				Log.d("Alex", "IOException--sendPrintJob");
				e.printStackTrace();
			}
		}
	};
	
	Runnable getJobAttributes = new Runnable () {
		OutputStream outputStream=null;
		InputStream inputStream=null; 
		int i=0; 
		public void run() {
			Log.d("Alex", "run getJobAttributes");
			while(i<20){
			try {
				URL myUrl = new URL(urlStr);
				HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/ipp");
				outputStream = connection.getOutputStream();
				outputStream.write(getJobAttri);
				outputStream.flush();
				outputStream.close(); 
				int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                	Log.d("Alex", "OK!");
                	 //see what's in response from the printer...
                    inputStream = connection.getInputStream();
                    StringBuffer stringBufJobAttri = new StringBuffer();
                    int readNum; 
                    while ((readNum = inputStream.read()) != -1) {
                    	stringBufJobAttri.append((char) readNum);
                    }
                    Log.d("Alex", "getJobAttributes, stringBufJobAttri is: "+stringBufJobAttri);
                    byte[] byteArrayJobAttri = stringBufJobAttri.toString().getBytes(); 
                    System.arraycopy(byteArrayJobAttri, 0, respCodeJobAttriBuf, 0, 4);
                    Log.d("Alex", "respCodeJobAttriBuf[2] is: "+respCodeJobAttriBuf[2]); 
                    Log.d("Alex", "respCodeJobAttriBuf[3] is: "+respCodeJobAttriBuf[3]);
                    inputStream.close(); 
                } 
                else{
                	Log.d("Alex", "Failed: " + responseCode);
                	Toast.makeText(context, "Job attribute error.", Toast.LENGTH_SHORT).show(); 
                	break; 
                }              
			} catch (IOException e) {
					Log.d("Alex", "IOException--getJobAttributes");
					e.printStackTrace();
			}
			try {
				Thread.sleep(2000);
				i++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		}
	};	

	
	private byte[] getPrinterAttri= new byte[] { //will repeat...
			0x02, 0x00, //Version: 2.0
			0x00, 0x0b, //Operation-id: Get-Printer-Attributes
			0x00, 0x00, 0x00, 0x10, //Request ID: 16
			0x01, //operation-attributes-tag
			//attributes-charset: utf-8
			0x47, //Tag: Character set
			0x00, 0x12, //Name length: 18
			0x61, 0x74, 0x74, 0x72, 0x69, 0x62, 0x75, 0x74, 0x65, 0x73, 0x2d, 0x63, 0x68, 0x61, 0x72, 0x73, 0x65, 0x74, //Name: attributes-charset
			0x00, 0x05, //Value length: 5
			0x75, 0x74, 0x66, 0x2d, 0x38, //Value: utf-8 
			//attributes-natural-language: zh-tw
			0x48, //Tag: Natural language
			0x00, 0x1b, //Name length: 27
			0x61, 0x74, 0x74, 0x72, 0x69, 0x62, 0x75, 0x74, 0x65, 0x73, 0x2d, 0x6e, 0x61, 0x74, 0x75, 0x72, 0x61, 0x6c, 0x2d, 0x6c, 0x61, 0x6e, 0x67, 0x75, 0x61, 0x67, 0x65, //Name: attributes-natural-language
			0x00, 0x05, //Value length: 5
			0x7a, 0x68, 0x2d, 0x74, 0x77, //Value: zh-tw
			0x45, //Tag: URI
			0x00, 0x0b, //Name length: 11
			0x70, 0x72, 0x69, 0x6e, 0x74, 0x65, 0x72, 0x2d, 0x75, 0x72, 0x69, //Name: printer-uri
			0x00, 0x22, //Value length: 34
			0x69, 0x70, 0x70, 0x3a, 0x2f, 0x2f, 0x6d, 0x79, 0x50, 0x72, 0x69, 0x6e, 0x74, 0x33, 0x42, 0x2e, 0x6c, 0x6f, 0x63, 0x61, 0x6c, 0x2e, 0x3a, 0x36, 0x33, 0x31, 0x2f, 0x55, 0x53, 0x42, 0x31, 0x5f, 0x4c, 0x51, //Value: ipp://myPrint3B. local.:631/USB1_LQ
			0x44, //Tag: Keyword
			0x00, 0x14, //Name length: 20
			0x72, 0x65, 0x71, 0x75, 0x65, 0x73, 0x74, 0x65, 0x64, 0x2d, 0x61, 0x74, 0x74, 0x72, 0x69, 0x62, 0x75, 0x74, 0x65, 0x73, //Name: requested-attributes
			0x00, 0x10, //Value length: 16
			0x63, 0x6f, 0x70, 0x69, 0x65, 0x73, 0x2d, 0x73, 0x75, 0x70, 0x70, 0x6f, 0x72, 0x74, 0x65, 0x64, //Value: copies-supported
			0x44, //Tag: Keyword
			0x00, 0x00, //Name length: 0
			0x00, 0x17, //Value length: 23
			0x70, 0x72, 0x69, 0x6e, 0x74, 0x2d, 0x71, 0x75, 0x61, 0x6c, 0x69, 0x74, 0x79, 0x2d, 0x73, 0x75, 0x70, 0x70, 0x6f, 0x72, 0x74, 0x65, 0x64, //Value: print-quality-supported
			0x44, //Tag: Keyword
			0x00, 0x00, //Name length: 0
			0x00, 0x0f, //Value length: 15
			0x73, 0x69, 0x64, 0x65, 0x73, 0x2d, 0x73, 0x75, 0x70, 0x70, 0x6f, 0x72, 0x74, 0x65, 0x64, //Value: sides-supported
			0x03 //End of attributes
			}; 
	
	private byte[] validateJob = new byte[] { //occur before sending print job? might do this right after user chooses to print and it gets input from user like quality, sides, quantity....
			//but now we don't let user choose quality, sides...only quantity is an option. 	
			0x02, 0x00, //Version: 2.0
			0x00, 0x04, //Operation-id: Validate-Job
			0x00, 0x00, 0x00, 0x01, //Request ID: 1
			0x01, //operation-attributes-tag
			//attributes-charset: utf-8
			0x47, //Tag: Character set
			0x00, 0x12, //Name length: 18
			0x61, 0x74, 0x74, 0x72, 0x69, 0x62, 0x75, 0x74, 0x65, 0x73, 0x2d, 0x63, 0x68, 0x61, 0x72, 0x73, 0x65, 0x74, //Name: attributes-charset
			0x00, 0x05, //Value length: 5
			0x75, 0x74, 0x66, 0x2d, 0x38, //Value: utf-8 
			//attributes-natural-language: zh-tw
			0x48, //Tag: Natural language
			0x00, 0x1b, //Name length: 27
			0x61, 0x74, 0x74, 0x72, 0x69, 0x62, 0x75, 0x74, 0x65, 0x73, 0x2d, 0x6e, 0x61, 0x74, 0x75, 0x72, 0x61, 0x6c, 0x2d, 0x6c, 0x61, 0x6e, 0x67, 0x75, 0x61, 0x67, 0x65, //Name: attributes-natural-language
			0x00, 0x05, //Value length: 5
			0x7a, 0x68, 0x2d, 0x74, 0x77, //Value: zh-tw
			//printer-uri: ipp://myPrint3B. local.:631/USB1_LQ
			0x45, //Tag: URI
			0x00, 0x0b, //Name length: 11
			0x70, 0x72, 0x69, 0x6e, 0x74, 0x65, 0x72, 0x2d, 0x75, 0x72, 0x69, //Name: printer-uri
			0x00, 0x22, //Value length: 34
			0x69, 0x70, 0x70, 0x3a, 0x2f, 0x2f, 0x6d, 0x79, 0x50, 0x72, 0x69, 0x6e, 0x74, 0x33, 0x42, 0x2e, 0x6c, 0x6f, 0x63, 0x61, 0x6c, 0x2e, 0x3a, 0x36, 0x33, 0x31, 0x2f, 0x55, 0x53, 0x42, 0x31, 0x5f, 0x4c, 0x51, //Value: ipp://myPrint3B. local.:631/USB1_LQ
			//requesting-user-name: mobile
			0x42, //Tag: Name without language
			0x00, 0x14, //Name length: 20
			0x72, 0x65, 0x71, 0x75, 0x65, 0x73, 0x74, 0x69, 0x6e, 0x67, 0x2d, 0x75, 0x73, 0x65, 0x72, 0x2d, 0x6e, 0x61, 0x6d, 0x65, //Name: requesting-user-name
			0x00, 0x06, //Value length: 6
			0x6d, 0x6f, 0x62, 0x69, 0x6c, 0x65, //Value: mobile
			//document-format: image/jpeg  ********
			0x49, //Tag: MIME media type
			0x00, 0x0f, //Name length: 15
			0x64, 0x6f, 0x63, 0x75, 0x6d, 0x65, 0x6e, 0x74, 0x2d, 0x66, 0x6f, 0x72, 0x6d, 0x61, 0x74, //Name: document-format
//			0x00, 0x0a, //Value length: 10  **********
			0x00, 0x0f, //Value length: 15
//			0x69, 0x6d, 0x61, 0x67, 0x65, 0x2f, 0x6a, 0x70, 0x65, 0x67, //Value: image/jpeg  *******
			0x61, 0x70, 0x70, 0x6c, 0x69, 0x63, 0x61, 0x74, 0x69, 0x6f, 0x6e, 0x2f, 0x70, 0x64, 0x66, //Value: application/pdf
			0x02, //Job attributes
			//copies: 1
			0x21, //Tag: Integer
			0x00, 0x06, //Name length: 6
			0x63, 0x6f, 0x70, 0x69, 0x65, 0x73, //Name: copies
			0x00, 0x04, //Value length: 4
			0x00, 0x00, 0x00, 0x01, //Value: 1
			//print-quality: 4 
			0x23, //Tag: Enum
			0x00, 0x0d, //Name length: 13
			0x70, 0x72, 0x69, 0x6e, 0x74, 0x2d, 0x71, 0x75, 0x61, 0x6c, 0x69, 0x74, 0x79, //Name: print-quality
			0x00, 0x04,//Value length: 4
			0x00, 0x00, 0x00, 0x04, //Value: 4
			//sides: one-sided
			0x44, //Tag: Keyword
			0x00, 0x05, //Name length: 5
			0x73, 0x69, 0x64, 0x65, 0x73, //Name: sides
			0x00, 0x09, //Value length: 9
			0x6f, 0x6e, 0x65, 0x2d, 0x73, 0x69, 0x64, 0x65, 0x64, //Value: one-sided
			0x03 //End of attributes			
		};
	
	private byte[] printJob = new byte[] { //print job request
			0x02, 0x00, //Version: 2.0
			0x00, 0x02, //Operation-id: Print-Job
			0x00, 0x00, 0x00, 0x03, //Request ID: 3
			0x01, //operation-attributes-tag
			//attributes-charset: utf-8
			0x47, //Tag: Character set
			0x00, 0x12, //Name length: 18
			0x61, 0x74, 0x74, 0x72, 0x69, 0x62, 0x75, 0x74, 0x65, 0x73, 0x2d, 0x63, 0x68, 0x61, 0x72, 0x73, 0x65, 0x74, //Name: attributes-charset
			0x00, 0x05, //Value length: 5
			0x75, 0x74, 0x66, 0x2d, 0x38, //Value: utf-8 
			//attributes-natural-language: zh-tw
			0x48, //Tag: Natural language
			0x00, 0x1b, //Name length: 27
			0x61, 0x74, 0x74, 0x72, 0x69, 0x62, 0x75, 0x74, 0x65, 0x73, 0x2d, 0x6e, 0x61, 0x74, 0x75, 0x72, 0x61, 0x6c, 0x2d, 0x6c, 0x61, 0x6e, 0x67, 0x75, 0x61, 0x67, 0x65, //Name: attributes-natural-language
			0x00, 0x05, //Value length: 5
			0x7a, 0x68, 0x2d, 0x74, 0x77, //Value: zh-tw
			//printer-uri: ipp://myPrint3B. local.:631/USB1_LQ
			0x45, //Tag: URI
			0x00, 0x0b, //Name length: 11
			0x70, 0x72, 0x69, 0x6e, 0x74, 0x65, 0x72, 0x2d, 0x75, 0x72, 0x69, //Name: printer-uri
			0x00, 0x22, //Value length: 34
			0x69, 0x70, 0x70, 0x3a, 0x2f, 0x2f, 0x6d, 0x79, 0x50, 0x72, 0x69, 0x6e, 0x74, 0x33, 0x42, 0x2e, 0x6c, 0x6f, 0x63, 0x61, 0x6c, 0x2e, 0x3a, 0x36, 0x33, 0x31, 0x2f, 0x55, 0x53, 0x42, 0x31, 0x5f, 0x4c, 0x51, //Value: ipp://myPrint3B. local.:631/USB1_LQ
			//requesting-user-name: mobile
			0x42, //Tag: Name without language
			0x00, 0x14, //Name length: 20
			0x72, 0x65, 0x71, 0x75, 0x65, 0x73, 0x74, 0x69, 0x6e, 0x67, 0x2d, 0x75, 0x73, 0x65, 0x72, 0x2d, 0x6e, 0x61, 0x6d, 0x65, //Name: requesting-user-name
			0x00, 0x06, //Value length: 6
			0x6d, 0x6f, 0x62, 0x69, 0x6c, 0x65, //Value: mobile
			//document-format: application/pdf  ********
			0x49, //Tag: MIME media type
			0x00, 0x0f, //Name length: 15
			0x64, 0x6f, 0x63, 0x75, 0x6d, 0x65, 0x6e, 0x74, 0x2d, 0x66, 0x6f, 0x72, 0x6d, 0x61, 0x74, //Name: document-format
			0x00, 0x0f, //Value length: 15  **********
			0x61, 0x70, 0x70, 0x6c, 0x69, 0x63, 0x61, 0x74, 0x69, 0x6f, 0x6e, 0x2f, 0x70, 0x64, 0x66,//Value: application/pdf  *******
			//job-name: Google
			0x42, //Tag: Name without language
			0x00, 0x08, //Name length: 8
			0x6a, 0x6f, 0x62, 0x2d, 0x6e, 0x61, 0x6d, 0x65, //Name: job-name
			0x00, 0x06, //Value length: 6
			0x47, 0x6f, 0x6f, 0x67, 0x6c, 0x65, //Value: Google
			0x02, //job-attributes-tag
			//copies: 1
			0x21, //Tag: Integer
			0x00, 0x06, //Name length: 6
			0x63, 0x6f, 0x70, 0x69, 0x65, 0x73, //Name: copies //214~218
			0x00, 0x04, //Value length: 4 //219~220
			0x00, 0x00, 0x00, 0x01, //Value: 1 ******************* 221~224
			//Value: 
			//print-quality: 4 
			0x23, //Tag: Enum
			0x00, 0x0d, //Name length: 13
			0x70, 0x72, 0x69, 0x6e, 0x74, 0x2d, 0x71, 0x75, 0x61, 0x6c, 0x69, 0x74, 0x79, //Name: print-quality
			0x00, 0x04,//Value length: 4
			0x00, 0x00, 0x00, 0x04, //Value: 4
			//sides: one-sided
			0x44, //Tag: Keyword
			0x00, 0x05, //Name length: 5
			0x73, 0x69, 0x64, 0x65, 0x73, //Name: sides
			0x00, 0x09, //Value length: 9
			0x6f, 0x6e, 0x65, 0x2d, 0x73, 0x69, 0x64, 0x65, 0x64, //Value: one-sided
			0x03 //End of attributes	
		}; 
	
	private byte[] getJobAttri = new byte[] { //will repeat
			0x02, 0x00, //Version: 2.0
			0x00, 0x09, //Operation-id: Get-Job-Attributes
			0x00, 0x00, 0x00, 0x04, //Request ID: 4
			0x01, //Operation attributes
			//attributes-charset: utf-8
			0x47, //Tag: Character set
			0x00, 0x12, //Name length: 18
			0x61, 0x74, 0x74, 0x72, 0x69, 0x62, 0x75, 0x74, 0x65, 0x73, 0x2d, 0x63, 0x68, 0x61, 0x72, 0x73, 0x65, 0x74, //Name: attributes-charset
			0x00, 0x05, //Value length: 5
			0x75, 0x74, 0x66, 0x2d, 0x38, //Value: utf-8 
			//attributes-natural-language: zh-tw
			0x48, //Tag: Natural language
			0x00, 0x1b, //Name length: 27
			0x61, 0x74, 0x74, 0x72, 0x69, 0x62, 0x75, 0x74, 0x65, 0x73, 0x2d, 0x6e, 0x61, 0x74, 0x75, 0x72, 0x61, 0x6c, 0x2d, 0x6c, 0x61, 0x6e, 0x67, 0x75, 0x61, 0x67, 0x65, //Name: attributes-natural-language
			0x00, 0x05, //Value length: 5
			0x7a, 0x68, 0x2d, 0x74, 0x77, //Value: zh-tw
			//printer-uri: ipp://myPrint3B. local.:631/USB1_LQ
			0x45, //Tag: URI
			0x00, 0x0b, //Name length: 11
			0x70, 0x72, 0x69, 0x6e, 0x74, 0x65, 0x72, 0x2d, 0x75, 0x72, 0x69, //Name: printer-uri
			0x00, 0x22, //Value length: 34
			0x69, 0x70, 0x70, 0x3a, 0x2f, 0x2f, 0x6d, 0x79, 0x50, 0x72, 0x69, 0x6e, 0x74, 0x33, 0x42, 0x2e, 0x6c, 0x6f, 0x63, 0x61, 0x6c, 0x2e, 0x3a, 0x36, 0x33, 0x31, 0x2f, 0x55, 0x53, 0x42, 0x31, 0x5f, 0x4c, 0x51, //Value: ipp://myPrint3B. local.:631/USB1_LQ
			//job-id: 3
			0x21, //Tag: Integer
			0x00, 0x06, //Name length: 6
			0x6a, 0x6f, 0x62, 0x2d, 0x69, 0x64, //Name: job-id
			0x00, 0x04, //Value length: 4
			0x00, 0x00, 0x00, 0x03, //Value: 3
			//requesting-user-name: mobile
			0x42, //Tag: Name without language
			0x00, 0x14, //Name length: 20
			0x72, 0x65, 0x71, 0x75, 0x65, 0x73, 0x74, 0x69, 0x6e, 0x67, 0x2d, 0x75, 0x73, 0x65, 0x72, 0x2d, 0x6e, 0x61, 0x6d, 0x65, //Name: requesting-user-name
			0x00, 0x06, //Value length: 6
			0x6d, 0x6f, 0x62, 0x69, 0x6c, 0x65, //Value: mobile
			//requesting-user-name: mobile
			0x42, //Tag: Name without language
			0x00, 0x14, //Name length: 20
			0x72, 0x65, 0x71, 0x75, 0x65, 0x73, 0x74, 0x69, 0x6e, 0x67, 0x2d, 0x75, 0x73, 0x65, 0x72, 0x2d, 0x6e, 0x61, 0x6d, 0x65, //Name: requesting-user-name
			0x00, 0x06, //Value length: 6
			0x6d, 0x6f, 0x62, 0x69, 0x6c, 0x65, //Value: mobile
			//requested-attributes: job-media-sheets-completed
			0x44, //Tag: keyword
			0x00, 0x14, 
			0x72, 0x65, 0x71, 0x75, 0x65, 0x73, 0x74, 0x65, 0x64, 0x2d, 0x61, 0x74, 0x74, 0x72, 0x69, 0x62, 0x75, 0x74, 0x65, 0x73, //Name: requested-attributes
			0x00, 0x1a, //Value length: 26
			0x6a, 0x6f, 0x62, 0x2d, 0x6d, 0x65, 0x64, 0x69, 0x61, 0x2d, 0x73, 0x68, 0x65, 0x65, 0x74, 0x73, 0x2d, 0x63, 0x6f, 0x6d, 0x70, 0x6c, 0x65, 0x74, 0x65, 0x64, //Value: job-media-sheets-completed
			0x44, //Tag: Keyword
			0x00, 0x00, //Name length: 0
			0x00, 0x19, //Value length: 25
			0x6a, 0x6f, 0x62, 0x2d, 0x69, 0x6d, 0x70, 0x72, 0x65, 0x73, 0x73, 0x69, 0x6f, 0x6e, 0x73, 0x2d, 0x63, 0x6f, 0x6d, 0x70, 0x6c, 0x65, 0x74, 0x65, 0x64, //Value: job-impressions-completed
			0x44, //Tag: Keyword
			0x00, 0x00, //Name length: 0
			0x00, 0x09, //Value length: 9
			0x6a, 0x6f, 0x62, 0x2d, 0x73, 0x74, 0x61, 0x74, 0x65, //Value: job-state
			0x44, //Tag: Keyword
			0x00, 0x00, //Name length: 0
			0x00, 0x11, //Value length: 17
			0x6a, 0x6f, 0x62, 0x2d, 0x73, 0x74, 0x61, 0x74, 0x65, 0x2d, 0x72, 0x65, 0x61, 0x73, 0x6f, 0x6e, 0x73, //Value: job-state-reasons
			0x03 //End of attributes					
	};

}
