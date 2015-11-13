package com.khizhny.spamsender;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//import com.khizhny.spamsender.Mail;

public class MainActivity extends Activity {
	
	private String csvPath;
	private String userName;
	private String password;
	private String smtpServer;
	private String smtpPort;
	private String sender;
	private String attachmentPath;
	private int processed;
	private int blockSize;
	private int blockSendDelay;
	private boolean working;
	Thread thread = new Thread(new Runnable(){
	    @Override
	    public void run() {
	    	working=true;
	    	InputStream in = null;
			try {
				in = new FileInputStream(csvPath);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				Log.e("MailApp", "File emails not found", e1); 
				working=false;
			}
			String line;
			String[] arr=null;
			if (in!=null) {
				BufferedReader reader= new BufferedReader(new InputStreamReader(in));
				try {
					line = reader.readLine();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for (int i=1;i<10000;i++){
					line="";
					Boolean timeToExit = false;
					try {
						line = reader.readLine();
						arr = line.split("\t");
					} catch (Exception e) {
						timeToExit=true;
					}
					if (!timeToExit){
						processed=processed+1;				    			    		
				    		Mail m = new Mail(userName, password,smtpServer,smtpPort); 
						      try { 
						    	  m.addAttachment(attachmentPath); 
					    		  } 
						      catch(Exception e) {} 						      
						      try {						      
							     String[] toArr = {arr[0]}; 
							      m.setTo(toArr); 
							      m.setFrom(sender); 
							      m.setSubject(arr[1]); 
							      m.setBody(arr[2]); 				   	          
					   	          m.send();
					   	          showToast("Sending message #"+processed+ " to " + arr[0]);
						      } catch(Exception e) { 
						    	  showToast("Could not send email #"+processed+" to "+ arr[0]);
						      } 
				    		
						}else{
						i=10000;
						try {
							reader.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (i%blockSize==0) {
						try {
							
							Thread.sleep(blockSendDelay);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}			
			working=false;
	    }
	});

	
	@Override 
	public void onCreate(Bundle icicle) { 
	  super.onCreate(icicle); 
	  setContentView(R.layout.activity_main); 
	// Restore preferences
	  working=false;
	  SharedPreferences settings =PreferenceManager.getDefaultSharedPreferences(this);
	  csvPath=settings.getString("csv_file_path", "/storage/sdcard1/emails.txt");
	  userName=settings.getString("userName", "khizhny.tester@gmail.com");
	  password=settings.getString("password", "khizhny.tester1");
	  smtpServer=settings.getString("smtpServer", "smtp.gmail.com");
	  smtpPort=settings.getString("smtpPort", "465");
	  sender=settings.getString("sender", "khizhny.tester@gmail.com");
	  attachmentPath=settings.getString("attachmentPath", "/storage/sdcard1/info.txt");
	  blockSize=Integer.parseInt(settings.getString("blockSize","2"));
	  blockSendDelay=Integer.parseInt(settings.getString("blockSendDelay", "5"));
	  final Button addImage = (Button) findViewById(R.id.send_email); 
	  addImage.setOnClickListener(new View.OnClickListener() { 
	    public void onClick(View view) { 
	    	processed=0;
	    	if (!working){
	    		addImage.setText("Working...");
	    		thread.start(); 
	  		}
	    } 
	  }); 
	} 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, PrefActivity.class);
		    startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void showToast(final String toast)
	{
	    runOnUiThread(new Runnable() {
	        public void run()
	        {
	            Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
	        }
	    });
	}
}
