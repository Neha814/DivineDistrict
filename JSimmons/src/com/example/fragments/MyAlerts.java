package com.example.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fragments.AddOrganization.AddOrgAPI;
import com.example.functions.Constants;
import com.example.functions.Functions;
import com.example.jsimmons.R;
import com.example.utils.NetConnection;
import com.example.utils.TransparentProgressDialog;

public class MyAlerts extends Fragment {

	private View rootView;

	ImageButton search_image;
	LinearLayout search_layout;
	EditText search;
	Button remiders, alerts;
	LinearLayout update_layout;
	Button update;
	boolean isConnected;
	TransparentProgressDialog db;
	
	SharedPreferences sp;
	
	EditText alert_1, alert_2,alert_3,alert_4,alert_5;
	
	ArrayList<String> alertList = new ArrayList<String>();
	
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

		rootView = inflater.inflate(R.layout.my_alerts, container, false);
		init();

		return rootView;
	}

	private void init() {
		search_image = (ImageButton) rootView.findViewById(R.id.search_image);
		search_layout = (LinearLayout) rootView
				.findViewById(R.id.search_layout);
		search = (EditText) rootView.findViewById(R.id.search);
		remiders = (Button) rootView.findViewById(R.id.remiders);
		alerts = (Button) rootView.findViewById(R.id.alerts);

		update_layout = (LinearLayout) rootView
				.findViewById(R.id.update_layout);
		update = (Button) rootView.findViewById(R.id.update);
		
		alert_1 = (EditText) rootView.findViewById(R.id.alert_1);
		alert_2 = (EditText) rootView.findViewById(R.id.alert_2);
		alert_3 = (EditText) rootView.findViewById(R.id.alert_3);
		alert_4 = (EditText) rootView.findViewById(R.id.alert_4);
		alert_5 = (EditText) rootView.findViewById(R.id.alert_5);
		
		isConnected = NetConnection.checkInternetConnectionn(getActivity());
		
		sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		
		if (isConnected) {
			
			new getAlerts(Constants.USER_ID, Constants.AUTH_KEY)
					.execute(new Void[0]);
		} else {
			showDialog(Constants.No_INTERNET);
		}

		remiders.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goToReminderScreen();
			}
		});
		
		update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UpdateAlert();
			}
		});
		
		update_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UpdateAlert();
			}
		});
	}

	protected void UpdateAlert() {
		String alert1_text = alert_1.getText().toString();
		String alert2_text = alert_2.getText().toString();
		String alert3_text = alert_3.getText().toString();
		String alert4_text = alert_4.getText().toString();
		String alert5_text = alert_5.getText().toString();
		
		
		
		String commaSepValues = alert1_text+","+alert2_text+","+alert3_text+","+alert4_text+","+alert5_text;
		commaSepValues = commaSepValues.replaceAll(",$", "");
		Log.e("commaSepValues===",""+commaSepValues);
		
		
		if (isConnected) {
			
			new AddAlert(Constants.USER_ID, Constants.AUTH_KEY, commaSepValues)
					.execute(new Void[0]);
		} else {
			showDialog(Constants.No_INTERNET);
		}
		
	}

	protected void goToReminderScreen() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment fragment = new MyReminders();

		if (fragment != null) {
			ft.replace(R.id.frame_layout, fragment);
		} else {
			ft.add(R.id.frame_layout, fragment);
		}
		// ft.addToBackStack(null);
		ft.commit();
		// ft.commitAllowingStateLoss();
	}
	
	
	public class AddAlert extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		HashMap result = new HashMap();

		ArrayList localArrayList = new ArrayList();
		String userID, authKEY, KEYWORD;
		int POS ;

		public AddAlert(String uSER_ID, String aUTH_KEY, String keyword) {
			this.userID = uSER_ID;
			this.authKEY = aUTH_KEY;
			this.KEYWORD = keyword;
		}
		/*
		 * http://phphosting.osvin.net/divineDistrict/api/userAlert.php?
		 * user_id=31&authKey=divineDistrict@31&keywords=a,b,c
		 */
		
		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList.add(new BasicNameValuePair("user_id",
						this.userID));
				localArrayList.add(new BasicNameValuePair("authKey",
						this.authKEY));
				localArrayList.add(new BasicNameValuePair("keywords",
						this.KEYWORD));

				result = function.addAlert(localArrayList);

			} catch (Exception localException) {

			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.get("Status").equals("true")) {

					showDialog("Alerts added successfully.");
					
					Editor e = sp.edit();
					e.putString("alert1", alert_1.getText().toString());
					e.putString("alert2", alert_2.getText().toString());
					e.putString("alert3", alert_3.getText().toString());
					e.putString("alert4", alert_4.getText().toString());
					e.putString("alert5", alert_5.getText().toString());
					e.commit();
					
				} else if (result.get("Status").equals("false")) {
				Toast.makeText(getActivity(), "alerts not addedd", Toast.LENGTH_SHORT).show();
				}
			}

			catch (Exception ae) {
				//showDialog(Constants.ERROR_MSG);
			}

		}

		protected void onPreExecute() {
			super.onPreExecute();
			db = new TransparentProgressDialog(getActivity(),
					R.drawable.loadingicon);
			db.show();
		}

	}
	
	
	
	public class getAlerts extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		HashMap result = new HashMap();

		ArrayList localArrayList = new ArrayList();
		String userID, authKEY;
		int POS ;

		public getAlerts(String uSER_ID, String aUTH_KEY) {
			this.userID = uSER_ID;
			this.authKEY = aUTH_KEY;
		}

		/*
		 * http://phphosting.osvin.net/divineDistrict/api/getUserAlerts.php?
		 * user_id=24&authKey=divineDistrict@24
		 */
		
		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList.add(new BasicNameValuePair("user_id",
						this.userID));
				localArrayList.add(new BasicNameValuePair("authKey",
						this.authKEY));

				result = function.getAlert(localArrayList);

			} catch (Exception localException) {

			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.get("Status").equals("true")) {

					String keyword = (String)result.get("keywords");
					
					Log.e("keywordss====",""+keyword);
					
					Collections.addAll(alertList, keyword.split(","));
					
					/*alertList.removeAll(Arrays.asList(null,""));
					
					Log.e("item size==",""+alertList.size()+"   items=="+alertList);*/
					
					/*String alert1 = sp.getString("alert1", "");
					String alert2 = sp.getString("alert2", "");
					String alert3 = sp.getString("alert3", "");
					String alert4 = sp.getString("alert4", "");
					String alert5 = sp.getString("alert5", "");*/
					
					String alert1 = alertList.get(0);
					String alert2 = alertList.get(1);
					String alert3 = alertList.get(2);
					String alert4 = alertList.get(3);
					String alert5 = alertList.get(4);
					
					if(!alert1.equals("")){
						alert_1.setText(alert1);
					}
					else {
						alert_1.setHint("Alert 1...");
					}
					
					
					if(!alert2.equals("")){
						alert_2.setText(alert2);
					}
					else {
						alert_2.setHint("Alert 2...");
					}
					
					
					
					if(!alert3.equals("")){
						alert_3.setText(alert3);
					}
					else {
						alert_3.setHint("Alert 3...");
					}
					
					
					
					
					if(!alert4.equals("")){
						alert_4.setText(alert4);
					}
					else {
						alert_4.setHint("Alert 4...");
					}
					
					if(!alert5.equals("")){
						alert_5.setText(alert5);
					}
					else {
						alert_5.setHint("Alert 5...");
					}
					
				} else if (result.get("Status").equals("false")) {
				Toast.makeText(getActivity(), "no alerts", Toast.LENGTH_SHORT).show();
				}
			}

			catch (Exception ae) {
				//showDialog(Constants.ERROR_MSG);
				ae.printStackTrace();
			}

		}

		protected void onPreExecute() {
			super.onPreExecute();
			db = new TransparentProgressDialog(getActivity(),
					R.drawable.loadingicon);
			db.show();
		}

	}

}
