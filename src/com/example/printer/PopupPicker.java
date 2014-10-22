package com.example.printer;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class PopupPicker {
	private Context context; 
	private String filePath;
	private MenuItem menuItem;
	private FrameLayout frameLayout; 
	private NumberPicker numPicker;
    private TextView textView;
    private int DEFAULT_COPY = 1; 
    private int pickedValue; 
    
	public PopupPicker(Context ctx, MenuItem menu, String file, FrameLayout frame) {
		context = ctx; 
		filePath = file; 
		menuItem = menu; 
		frameLayout = frame; 
	}
	
	public void popup() {
		Log.d("Alex", "popup is called"); 
		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);  
	    View popupView = layoutInflater.inflate(R.layout.popup, null); 
	    final PopupWindow popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);  
//	    textView=(TextView) popupView.findViewById(R.id.textView);
	       
        numPicker=(NumberPicker) popupView.findViewById(R.id.numberPicker);
        numPicker.setMaxValue(9);  
        numPicker.setMinValue(1);    
        numPicker.setValue(1); 
        String pickerTitle = context.getResources().getString(R.string.picker_title);
   	 	String strPickerTitle = String.format(pickerTitle, String.valueOf(DEFAULT_COPY));
   	 	textView.setText(strPickerTitle);
        //get picked value
        numPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener (){
             public void onValueChange(NumberPicker view, int oldValue, int newValue) {
            	 Log.d("Alex", "old value is: "+oldValue); 
            	 Log.d("Alex", "new value is: "+newValue);
            	 String pickerTitle = context.getResources().getString(R.string.picker_title);
            	 String strPickerTitle = String.format(pickerTitle, String.valueOf(newValue));
            	 pickedValue = newValue; 
            	 textView.setText(strPickerTitle);
               
             }
        });
/*	    Button btnDismiss = (Button)popupView.findViewById(R.id.dismiss);
	    btnDismiss.setOnClickListener(new Button.OnClickListener(){
	    	@Override
	    	public void onClick(View v) {
	    		// TODO Auto-generated method stub
	    		Log.d("Alex", "dismiss onClicked!"); 
	    		if (frameLayout!=null)
	    			frameLayout.getForeground().setAlpha(0); //restore
	    		menuItem.setEnabled(true); 
	    		popupWindow.dismiss();
	    	}
	    });
	    Button btnOK = (Button)popupView.findViewById(R.id.ok);
	    btnOK.setOnClickListener(new Button.OnClickListener(){
	    	@Override
	    	public void onClick(View v) {
	    		// TODO Auto-generated method stub
	    		Log.d("Alex", "OK onClicked!"); 
	    		Toast.makeText(context.getApplicationContext(), "Will print out "+pickedValue+ " copies! The filePath is: "+filePath, Toast.LENGTH_LONG).show(); 
	    		if (frameLayout!=null)
	    			frameLayout.getForeground().setAlpha(0); //restore
	    		menuItem.setEnabled(true); 
	    		popupWindow.dismiss();
	    	}
	    });  */         
	    popupWindow.showAtLocation(popupView, DEFAULT_COPY, 50, -30);
	}

}
