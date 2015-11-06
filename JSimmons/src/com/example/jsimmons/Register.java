package com.example.jsimmons;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;

import com.example.functions.Constants;
import com.example.functions.Functions;
import com.example.jsimmons.R;
import com.example.utils.NetConnection;
import com.example.utils.StringUtils;
import com.example.utils.TransparentProgressDialog;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Register extends Activity {
	
	EditText username ,email ,password, confirm_password;
	 CheckBox agree_checkbox;
	LinearLayout register;
	TextView terms;
	Button registerBbT;
	
	SharedPreferences sp;
	
	String username_text;
	String password_text ;
	String confirm_password_text ;
	String email_text;
	Boolean isConnected;
	
	TransparentProgressDialog db;
	
	protected void showDialog(String msg) {
		final Dialog dialog;
		dialog = new Dialog(Register.this);
		dialog.setCancelable(false);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFormat(PixelFormat.TRANSLUCENT);

		Drawable d = new ColorDrawable(Color.BLACK);
		d.setAlpha(0);
		dialog.getWindow().setBackgroundDrawable(d);

		Button ok;
		TextView message;

		dialog.setContentView(R.layout.dialog);
		ok = (Button) dialog.findViewById(R.id.ok);
		message = (TextView) dialog.findViewById(R.id.message);

		message.setText(msg);

		ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();

			}
		});
		dialog.show();

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		initUI();
	}

	private void initUI() {
		
		sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		username = (EditText) findViewById(R.id.username);
		email = (EditText) findViewById(R.id.email);
		password = (EditText) findViewById(R.id.password);
		confirm_password = (EditText) findViewById(R.id.confirm_password);
		register = (LinearLayout) findViewById(R.id.register);
		terms = (TextView) findViewById(R.id.terms);
		registerBbT = (Button) findViewById(R.id.registerBbT);
		agree_checkbox = (CheckBox) findViewById(R.id.agree_checkbox);
		
		isConnected = NetConnection.checkInternetConnectionn(getApplicationContext());
		
		SpannableString content1 = new SpannableString("Terms & Condition.");
		content1.setSpan(new UnderlineSpan(), 0, content1.length(), 0);
		terms.setText(content1);
		
		registerBbT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				validate();
			}
		});
		
		register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				validate();
				
			}
		});
	}
	
	
	protected void validate() {
		 username_text = username.getText().toString();
		 password_text = password.getText().toString();
		 confirm_password_text = confirm_password.getText().toString();
		 email_text = email.getText().toString();
		 

		if (username_text.trim().length() < 1) {
			username.setError("Please enter username.");
		}else if(email_text.trim().length()<1){
			email.setError("Please enter email.");
		} 	else if (!(StringUtils.verify(email_text))) {
			email.setError("Please enter valid email address.");
		}
		else if (password_text.trim().length() < 1) {
			password.setError("Please enter password.");
		} 
		else if (confirm_password_text.trim().length() < 1) {
			confirm_password.setError("Please enter password.");
		} else if(!confirm_password_text.equals(password_text)){
			confirm_password.setError("Confirm password did not match.");
		} else {
			 if (agree_checkbox.isChecked()) {
				 if (isConnected) {
						new RegistrationTask(username_text, password_text, email_text, "Mannual")
						.execute(new Void[0]);
						} else {
							 showDialog(Constants.No_INTERNET);
						} 
			 }
			 else {
			 showDialog("Please accept terms and conditions.");
			 }
			
		}
		
	}
	
	public class RegistrationTask extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		HashMap result = new HashMap();

		ArrayList localArrayList = new ArrayList();

		String username, pass, email, type;

		public RegistrationTask(String username_text, String password_text,
				String email_text, String string) {
			this.username = username_text;
			this.pass = password_text;
			this.email = email_text;
			this.type = string;
		}

		

		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList.add(new BasicNameValuePair("username", this.username));
				localArrayList.add(new BasicNameValuePair("password", this.pass));
				localArrayList.add(new BasicNameValuePair("email", this.email));
				localArrayList.add(new BasicNameValuePair("login_type", this.type));
				localArrayList.add(new BasicNameValuePair("facebook_id",""));
				localArrayList.add(new BasicNameValuePair("token_id",Constants.REGISTRATIO_ID));
				
				result = function.register(localArrayList);

			}
			catch (Exception localException) {

			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.get("Status").equals("true")) {
				
					Constants.USER_ID = (String) result.get("user_id");
					Constants.AUTH_KEY = (String) result.get("auth_key");
					Constants.USERNAME = (String) result.get("username");
					
					Constants.PROFILE_PIC_URL = (String) result.get("image");
					
					Editor e = sp.edit();
					e.putString("auth_key",Constants.AUTH_KEY );
					e.putString("user_id",Constants.USER_ID );
					e.putString("username",Constants.USERNAME );
					
					e.putString("image", Constants.PROFILE_PIC_URL);
					e.commit();
					
					
					Constants.LOGIN_TYPE = "Mannual";
					Intent i = new Intent(Register.this , PicAddORgRadius.class);
					startActivity(i);
					finish();
					
					/*Intent i = new Intent(Register.this , Home.class);
					startActivity(i);
					finish();*/
					
				}
				else if (result.get("Status").equals("false")) {
					showDialog((String)result.get("Message"));
				}
			}

			catch (Exception ae) {
				showDialog(Constants.ERROR_MSG);
				ae.printStackTrace();
			}

		}

		protected void onPreExecute() {
			super.onPreExecute();
			db = new TransparentProgressDialog(Register.this, R.drawable.loadingicon);
			db.show();
		}

	}
}
