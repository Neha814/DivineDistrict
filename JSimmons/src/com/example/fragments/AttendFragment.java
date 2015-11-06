package com.example.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.functions.Constants;
import com.example.functions.Functions;
import com.example.jsimmons.Home;
import com.example.jsimmons.Login;
import com.example.jsimmons.R;
import com.example.jsimmons.Login.LoginTask;
import com.example.utils.TransparentProgressDialog;

public class AttendFragment extends Fragment {

	private View rootView;
	Spinner spinner;
	CheckBox checkBox1;
	LinearLayout submit_layout;
	Button submit;
	TextView title;
	String reminder;
	String option;
	TransparentProgressDialog db;
	ImageButton back;
	
	protected void showDialog(String msg) {
		try {
		final Dialog dialog;
		dialog = new Dialog(getActivity());
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
		} catch(Exception e){
			e.printStackTrace();
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.attend_fragment, container, false);
		init();

		return rootView;
	}

	private void init() {
		spinner = (Spinner) rootView.findViewById(R.id.spinner);
		checkBox1 = (CheckBox) rootView.findViewById(R.id.checkBox1);
		submit_layout = (LinearLayout) rootView
				.findViewById(R.id.submit_layout);
		submit = (Button) rootView.findViewById(R.id.submit);
		title = (TextView) rootView.findViewById(R.id.title);
		back = (ImageButton) rootView.findViewById(R.id.back);
		
		title.setText(Constants.ANNOUNCEMENT_TITLE);
		
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goBACk();
			}
		});

		String[] list = new String[] { "Attending","Request to Minister", "Request to Volunteer", "Visit" };
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
				getActivity(), R.layout.simple_spinner_item, R.id.text, list);
		spinner.setAdapter(dataAdapter);
		
//		spinner.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				String spinner_text = spinner.getSelectedItem().toString();
//				if(!spinner_text.equals("I would like to")){
//					reminder = "1";
//					option = spinner_text;
//				CallAPI();
//				}
//			}
//		});
		
		checkBox1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				String spinner_text = spinner.getSelectedItem().toString();
				option = spinner_text;
				if(isChecked){
					
						reminder = "1";
					CallAPI();
					
				} else if(!isChecked){
					
						reminder = "0";
						CallAPI();
						
				}
			}
		});
		
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
					goTONExt();
				
			}
		});
		
		submit_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goTONExt();
			}
		});
	}

	protected void goBACk() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment fragment = new AnnoncemnetDetail();
		
		
	    if(fragment != null) {
        	ft.replace(R.id.frame_layout, fragment);
       }
         else{
             ft.add(R.id.frame_layout, fragment);
            }
        //ft.addToBackStack(null);		 
       // ft.commit();
        ft.commitAllowingStateLoss();
		
	}

	protected void CallAPI() {
		new SetReminder(Constants.USER_ID,Constants.AUTH_KEY,reminder,Constants.ANNOUNCEMENT_ID,option).execute(new Void[0]);
	}

	protected void goTONExt() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment fragment = new JoinAnnouncement();
		
		
	    if(fragment != null) {
        	ft.replace(R.id.frame_layout, fragment);
       }
         else{
             ft.add(R.id.frame_layout, fragment);
            }
        ft.addToBackStack(null);		 
       // ft.commit();
        ft.commitAllowingStateLoss();
	}
	
	public class SetReminder extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		HashMap result = new HashMap();

		ArrayList localArrayList = new ArrayList();
		String userID , authKEY, reminder_text , AnnID , OPTION;

		public SetReminder(String uSER_ID, String aUTH_KEY, String reminder,
				String aNNOUNCEMENT_ID, String option) {
			userID = uSER_ID;
			authKEY = aUTH_KEY;
			reminder_text = reminder;
			AnnID = aNNOUNCEMENT_ID;
			OPTION = option;
		}
		/*phphosting.osvin.net/divineDistrict/api/attendance.php?
				user_id=31&authKey=divineDistrict@31&announcement_id=11&option=attending&reminder=1*/
		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList.add(new BasicNameValuePair("user_id", this.userID));
				localArrayList.add(new BasicNameValuePair("authKey", this.authKEY));
				localArrayList.add(new BasicNameValuePair("announcement_id",this.AnnID ));
				localArrayList.add(new BasicNameValuePair("option", this.OPTION));
				localArrayList.add(new BasicNameValuePair("reminder", this.reminder_text));
				
				result = function.setReminder(localArrayList);

			}
			catch (Exception localException) {
				localException.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.get("Status").equals("true")) {
					
					showDialog((String)result.get("Message"));
				}
				else if (result.get("Status").equals("false")) {
					
						showDialog((String)result.get("Message"));
				
				}
			}

			catch (Exception ae) {
				showDialog(Constants.ERROR_MSG);
			}

		}

		protected void onPreExecute() {
			super.onPreExecute();
			db = new TransparentProgressDialog(getActivity(), R.drawable.loadingicon);
			db.show();
		}

	}

}
