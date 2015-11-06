package com.example.jsimmons;

import java.io.IOException;

import com.example.functions.Constants;
import com.example.jsimmons.R;
import com.example.utils.PrefStore;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class SplashScreen extends Activity{
	
	String PROJECT_NUMBER = "20231206239";

	GoogleCloudMessaging gcm;

	String regid;

	private PrefStore store;
	
	SharedPreferences sp;
	boolean homeStatus = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		homeStatus = sp.getBoolean("inHome", false);
		
		if(getIntent().getExtras()!=null){
		
			
			String msg = getIntent().getExtras().getString("msg");
			String id = getIntent().getExtras().getString("id");
			String type = getIntent().getExtras().getString("type");
			
			Constants.ID_NOTIFICATION = id;
			Constants.MESSAGE_NOTIFICATION = msg;
			Constants.TYPE_NOTIFICATION = type;
			//Toast.makeText(getApplicationContext(), msg+"=="+id+"=="+type, Toast.LENGTH_SHORT).show();
		}
		
		Thread t = new Thread(){
			public void run(){
				try {
					//sleep(1000*3);
					getRegID();
					
				} catch (Exception e) {					
					e.printStackTrace();
				}
			}
		};t.start();
	}
	
	private String getRegID() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
					}
					regid = gcm.register(PROJECT_NUMBER);
					msg = regid;

					Log.e("GCM", msg);

				}
				catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					Intent i = new Intent(SplashScreen.this, Login.class);
					startActivity(i);
					finish();

				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Constants.REGISTRATIO_ID = msg;

				if(homeStatus){
					Intent i = new Intent(SplashScreen.this , Home.class);					
					startActivity(i);
					finish();
				} else {
				Intent i = new Intent(SplashScreen.this , Login.class);					
				startActivity(i);
				finish();
				}

			}
		}.execute(null, null, null);
		return regid;

	}

}
