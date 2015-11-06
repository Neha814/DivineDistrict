package com.example.fragments;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.CompressFormat;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.fragments.PostFragment.updateProfileTask;
import com.example.functions.Constants;
import com.example.functions.Functions;
import com.example.jsimmons.Home;
import com.example.jsimmons.R;
import com.example.utils.HttpClientUpload;
import com.example.utils.NetConnection;
import com.example.utils.TransparentProgressDialog;

public class SubmitAnnouncement4 extends Fragment {

	private View rootView;
	ProgressBar progressBar;
	LinearLayout submit_layout;
	Button submit_button;
	
	boolean isSuccess = false;
	String type = "";
	Boolean isConnected;

	CheckBox minister_talent_checkbox, volunteer_checkbox, visitors_checkbox,
			org_event_checkbox;
	EditText price ,note;

	TransparentProgressDialog db;
	public  File imgFileGalleryFinal ;
	
	submitAnnouncementTask submitAnnouncementObj;
	
	EditText website;
	
	

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
					if(isSuccess){
						goToHomeScreen();
					}

				}
			});
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void goToHomeScreen() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment fragment = new SentFragment();

		if (fragment != null) {
			ft.replace(R.id.frame_layout, fragment);
		} else {
			ft.add(R.id.frame_layout, fragment);
		}

		ft.commit();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.submit_announcement4, container,
				false);
		init();

		return rootView;
	}

	private void init() {
		
		imgFileGalleryFinal = new File("");
		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		submit_button = (Button) rootView.findViewById(R.id.submit_button);
		submit_layout = (LinearLayout) rootView
				.findViewById(R.id.submit_layout);

		minister_talent_checkbox = (CheckBox) rootView
				.findViewById(R.id.minister_talent_checkbox);
		volunteer_checkbox = (CheckBox) rootView
				.findViewById(R.id.volunteer_checkbox);
		visitors_checkbox = (CheckBox) rootView
				.findViewById(R.id.visitors_checkbox);
		org_event_checkbox = (CheckBox) rootView
				.findViewById(R.id.org_event_checkbox);
		
		note  = (EditText) rootView.findViewById(R.id.note);

		price = (EditText) rootView.findViewById(R.id.price);
		
		website = (EditText) rootView.findViewById(R.id.website);

		progressBar.setProgress(100);
		
		minister_talent_checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					//  visitors, ministers, volunteers, organisation
					type = type+","+"minister";
					
					minister_talent_checkbox.setButtonDrawable(R.drawable.checkbox_checked);
				}else {
					minister_talent_checkbox.setButtonDrawable(R.drawable.checkbox_unchecked);
				}
				
			}
		});
		
		visitors_checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
				type = type +","+ "visitor";
				visitors_checkbox.setButtonDrawable(R.drawable.checkbox_checked);
				}else {
					visitors_checkbox.setButtonDrawable(R.drawable.checkbox_unchecked);
				}
			}
		});
		
		volunteer_checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
				type = type +","+ "volunteer";
				volunteer_checkbox.setButtonDrawable(R.drawable.checkbox_checked);
				}
				else {
					volunteer_checkbox.setButtonDrawable(R.drawable.checkbox_unchecked);
				}
			}
		});
	
	
		org_event_checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(isChecked){
			type = type +","+ "organisation";
			org_event_checkbox.setButtonDrawable(R.drawable.checkbox_checked);
			} else {
				org_event_checkbox.setButtonDrawable(R.drawable.checkbox_unchecked);
			}
		}
	});

		submit_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SubmitAPI();

			}
		});

		submit_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				
				SubmitAPI();
			}
		});

	}

	protected void SubmitAPI() {

		isConnected = NetConnection.checkInternetConnectionn(getActivity());
		if (isConnected) {

			String price_amount = price.getText().toString();
			String note_text = note.getText().toString();
			Constants.PRICE_TO_SUBMIT = price_amount;
			
			String WEBSITE = website.getText().toString();
			
			Constants.NOTE = note_text;
			
			submitAnnouncementObj = new submitAnnouncementTask(Constants.USER_ID, Constants.AUTH_KEY,
					Constants.ORG_ID_TO_SUBMIT, Constants.CAT_ID_TO_SUBMIT,
					Constants.SUB_ID_TO_SUBMIT, Constants.TITLE_TO_SUBMIT,
					Constants.DESCRIPTION_TO_SUBMIT,
					Constants.ADDRESS_TO_SUBMIT, Constants.CITY_TO_SUBMIT,
					Constants.STATE_TO_SUBMIT, Constants.COUNTRY_TO_SUBMIT,
					Constants.START_DATE_TO_SUBMIT,
					Constants.END_DATE_TO_SUBMIT, Constants.PRICE_TO_SUBMIT,type,Constants.NOTE ,WEBSITE);
			
				Log.e("start date===",""+Constants.START_DATE_TO_SUBMIT);
				Log.e("end date===",""+Constants.END_DATE_TO_SUBMIT);
				Log.e("address===",""+Constants.ADDRESS_TO_SUBMIT);
				Log.e("city===",""+Constants.CITY_TO_SUBMIT);
				Log.e("state===",""+Constants.STATE_TO_SUBMIT);
				Log.e("country===",""+Constants.COUNTRY_TO_SUBMIT);
			submitAnnouncementObj.execute();
			
			
		} else {
			showDialog(Constants.No_INTERNET);
		}
	}

	public class submitAnnouncementTask extends AsyncTask<String, Void, String> {
		
		ByteArrayOutputStream baos;

		HashMap result = new HashMap();

		ArrayList localArrayList = new ArrayList();
		String userID, authKEY, orgID, catID, subID, title, desc, address,
				city, state, country, startDate, endDate, price , Type , Note , Website;

		public submitAnnouncementTask(String uSER_ID, String aUTH_KEY,
				String oRG_ID_TO_SUBMIT, String cAT_ID_TO_SUBMIT,
				String sUB_ID_TO_SUBMIT, String tITLE_TO_SUBMIT,
				String dESCRIPTION_TO_SUBMIT, String aDDRESS_TO_SUBMIT,
				String cITY_TO_SUBMIT, String sTATE_TO_SUBMIT,
				String cOUNTRY_TO_SUBMIT, String sTART_DATE_TO_SUBMIT,
				String eND_DATE_TO_SUBMIT, String pRICE_TO_SUBMIT, String type,String note , String website) {
			
			this.userID = uSER_ID;
			this.authKEY = aUTH_KEY;
			this.orgID = oRG_ID_TO_SUBMIT;
			this.catID = cAT_ID_TO_SUBMIT;
			this.subID = sUB_ID_TO_SUBMIT;
			this.title = tITLE_TO_SUBMIT;
			this.desc = dESCRIPTION_TO_SUBMIT;
			this.address = aDDRESS_TO_SUBMIT;
			this.city = cITY_TO_SUBMIT;
			this.state = sTATE_TO_SUBMIT;
			this.country = cOUNTRY_TO_SUBMIT;
			this.startDate = sTART_DATE_TO_SUBMIT;
			this.endDate = eND_DATE_TO_SUBMIT;
			this.price = pRICE_TO_SUBMIT;
			this.Type = type;
			this.Note = note;
			this.Website = website;
		}

		protected String doInBackground(String... params) {
			try {
				baos = new ByteArrayOutputStream();
				
			}

			catch (Exception e) {
				Log.e("excptn==", "" + e);
			}
			
		

			try {
				HttpClient httpclient = new DefaultHttpClient();

				HttpClientUpload client = new HttpClientUpload(
						"http://phphosting.osvin.net/divineDistrict/api/addAnnouncement.php?");
				client.connectForMultipart();
				
				client.addFormPart("user_id",this.userID);
				client.addFormPart("authKey",this.authKEY);
				client.addFormPart("org_id",this.orgID);
				client.addFormPart("cat_id", this.catID);
				client.addFormPart("subcat", this.subID);
				client.addFormPart("title", this.title);
				client.addFormPart("description", this.desc);
				client.addFormPart("address", this.address);
				client.addFormPart("city", this.city);
				client.addFormPart("state", this.state);
				client.addFormPart("country", this.country);
				client.addFormPart("startdate", this.startDate);
				client.addFormPart("enddate", this.endDate);
				client.addFormPart("price", this.price);
				client.addFormPart("type", this.Type);
				client.addFormPart("note", this.Note);
				client.addFormPart("website", this.Website);
				client.addFormPart("starttime", Constants.START_TIME_TO_SUBMIT);
				client.addFormPart("endtime", Constants.END_TIME_TO_SUBMIT);
				
				Log.e("**** start date ****",""+this.startDate);
				Log.e("**** end date ****",""+this.endDate);
				
				try {
				imgFileGalleryFinal = new File(Constants.picturePath);
				Log.e("imgFileGalleryFinal====",""+imgFileGalleryFinal);
				if(!(imgFileGalleryFinal.getName().equals("") || imgFileGalleryFinal.getName()==null)){
				
					Log.e("img file===",""+ imgFileGalleryFinal.getName());
					client.addFilePart("image", imgFileGalleryFinal.getName(),
							baos.toByteArray());
				}
				} catch(Exception ae){
					ae.printStackTrace();
				}
				
				client.finishMultipart();
			

				String data = client.getResponse();

				Log.e("data==", "" + data);
				

				return data;
			} catch (Throwable t) {
				t.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String result) {
			db.dismiss();

			try {
				JSONObject data = new JSONObject(result);
				
			String status =	data.getString("Status");
			String Message =	data.getString("Message");
			
				if(status.equalsIgnoreCase("true")){
					isSuccess = true;
					Constants.ANNOUNCEMENT_TITLE= this.title;
					showDialog(Message);
				} else if(status.equalsIgnoreCase("false")){
					showDialog(Message);
				}
			}

			catch (Exception ae) {
				showDialog(Constants.ERROR_MSG);
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
