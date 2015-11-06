package com.example.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;

import android.app.Dialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.fragments.JoinAnnouncement.SubmitAnnouncement;
import com.example.functions.Constants;
import com.example.functions.Functions;
import com.example.jsimmons.R;
import com.example.utils.NetConnection;
import com.example.utils.TransparentProgressDialog;

public class MyReminders extends Fragment {

	private View rootView;

	Button alerts;
	Spinner next_spinner, first_spinner;
	LinearLayout submit_layout;
	Boolean isConnected;
	Button submit;
	TransparentProgressDialog db;

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
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.my_reminders, container, false);

		init();

		return rootView;
	}

	private void init() {
		alerts = (Button) rootView.findViewById(R.id.alerts);
		next_spinner = (Spinner) rootView.findViewById(R.id.next_spinner);
		first_spinner = (Spinner) rootView.findViewById(R.id.first_spinner);
		submit_layout = (LinearLayout) rootView
				.findViewById(R.id.submit_layout);
		submit = (Button) rootView.findViewById(R.id.submit);

		String[] list = new String[] { "Option", "1 week", "1 day", "6 hours",
				"2 hours", "1 hour", "30 min", "10 min" };
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
				getActivity(), R.layout.simple_spinner_item, R.id.text, list);

		first_spinner.setAdapter(dataAdapter);
		next_spinner.setAdapter(dataAdapter);

		submit_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ReminderAPI();
			}
		});

		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ReminderAPI();
			}
		});

		alerts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goToAlertScreen();
			}
		});
	}

	protected void ReminderAPI() {
		isConnected = NetConnection.checkInternetConnectionn(getActivity());
		if (isConnected) {
			String first_text;
			String next_text;
			int first = first_spinner.getSelectedItemPosition();
			int next = next_spinner.getSelectedItemPosition();

			if (first > 0) {
				first_text = first_spinner.getSelectedItem().toString();
			} else {
				first_text = "";
			}

			if (next > 0) {
				next_text = next_spinner.getSelectedItem().toString();
			} else {
				next_text = "";
			}
			 new SetReminder(Constants.USER_ID,Constants.AUTH_KEY,first_text,next_text).execute(new Void[0]);
		} else {
			showDialog(Constants.No_INTERNET);
		}
	}

	protected void goToAlertScreen() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment fragment = new MyAlerts();

		if (fragment != null) {
			ft.replace(R.id.frame_layout, fragment);
		} else {
			ft.add(R.id.frame_layout, fragment);
		}
		// ft.addToBackStack(null);
		 ft.commit();
		//ft.commitAllowingStateLoss();
	}
	
	
	public class SetReminder extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		HashMap result = new HashMap();

		ArrayList localArrayList = new ArrayList();
		String userID , authKEY, first , next ;

		public SetReminder(String uSER_ID, String aUTH_KEY , String first , String next) {
			this.userID = uSER_ID;
			this.authKEY = aUTH_KEY;
			this.first = first;
			this.next = next;
		}
		/*http://phphosting.osvin.net/divineDistrict/api/reminderOptions.php?
			user_id=31&authKey=divineDistrict@31&reminder1=1%20week&reminder2=1%20hour*/
		
		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList.add(new BasicNameValuePair("user_id", this.userID));
				localArrayList.add(new BasicNameValuePair("authKey", this.authKEY));
				localArrayList.add(new BasicNameValuePair("reminder1",this.first ));
				localArrayList.add(new BasicNameValuePair("reminder2", this.next));
				
				result = function.SetReminder(localArrayList);

			}
			catch (Exception localException) {

			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.get("Status").equals("true")) {
					showDialog((String) result.get("Message"));
				}
				else if (result.get("Status").equals("false")) {
					showDialog((String) result.get("Message"));
				
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
