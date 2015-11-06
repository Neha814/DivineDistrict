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

import com.example.fragments.FindAnnouncementCategoryFragment.getCategoryList;
import com.example.functions.Constants;
import com.example.functions.Functions;
import com.example.jsimmons.R;
import com.example.utils.NetConnection;
import com.example.utils.TransparentProgressDialog;

public class JoinAnnouncement extends Fragment {

	private View rootView;
	EditText message;
	LinearLayout submit_layout;
	Button submit;
	TextView title;
	Boolean isConnected;
	String messageText="";
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

		rootView = inflater.inflate(R.layout.join_fragment, container, false);
		init();

		return rootView;
	}

	private void init() {
		title = (TextView) rootView.findViewById(R.id.title);
		submit_layout = (LinearLayout) rootView.findViewById(R.id.submit_layout);
		submit = (Button) rootView.findViewById(R.id.submit);
		message = (EditText) rootView.findViewById(R.id.message);
		back = (ImageButton) rootView.findViewById(R.id.back);
		title.setText(Constants.ANNOUNCEMENT_TITLE);
		
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goBACK();
			}
		});
		
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 messageText = message.getText().toString();
				CallAPI();
			}
		});
		
		submit_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 messageText = message.getText().toString();
				CallAPI();
			}
		});
	}

	protected void goBACK() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment fragment = new AttendFragment();
		
		
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
		isConnected = NetConnection.checkInternetConnectionn(getActivity());
		if (isConnected) {
			new SubmitAnnouncement(messageText,Constants.ANNOUNCEMENT_ID,Constants.USER_ID,Constants.AUTH_KEY).execute(new Void[0]);
		} else {
			showDialog(Constants.No_INTERNET);
		}
	}
	
	public class SubmitAnnouncement extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		HashMap result = new HashMap();

		ArrayList localArrayList = new ArrayList();
		String userID , authKEY, mesg , AnnID ;

		public SubmitAnnouncement(String messageText, String aNNOUNCEMENT_ID,
				String uSER_ID, String aUTH_KEY) {
			userID = uSER_ID;
			authKEY = aUTH_KEY;
			AnnID = aNNOUNCEMENT_ID;
			mesg = messageText;
		}
		
		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList.add(new BasicNameValuePair("user_id", this.userID));
				localArrayList.add(new BasicNameValuePair("authKey", this.authKEY));
				localArrayList.add(new BasicNameValuePair("announcement_id",this.AnnID ));
				localArrayList.add(new BasicNameValuePair("message", this.mesg));
				
				result = function.SubmitAnnouncement(localArrayList);

			}
			catch (Exception localException) {

			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.get("Status").equals("true")) {
					
					//showDialog("Announcement submitted successfully!");
					GoToNextPage();
				}
				else if (result.get("Status").equals("false")) {
					
						showDialog("You have already joined this announcement.");
				
				}
				//GoToNextPage();
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

	public void GoToNextPage() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment fragment = new SentFragment();
		
		
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
}
