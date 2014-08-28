package com.example.printer;

import android.graphics.Bitmap;

public class PicFileInfo {
	private String name;
	private String size;
	private String path;
	private Bitmap thumbnail; 
	
	public PicFileInfo(String fileName, String fileSize, String filePath, Bitmap thumbnail_image) {
		name=fileName;
		size=fileSize;
		path=filePath; 
		thumbnail=thumbnail_image;
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
	
	public Bitmap getThumbnail() {
		return thumbnail;
	}
}
