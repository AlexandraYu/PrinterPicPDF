package com.example.printer;

public class PDFFile {
	private String name;
	private String size;
	private String path;
	public PDFFile(String fileName, String fileSize, String thisPath) {
		name=fileName;
		size=fileSize;
		path=thisPath; 
	}
	public String getName() {
		return name;
	}
	
	public String getSize() {
		return size;
	}
	
	public String getPath() {
		return path;
	}
}
