package com.example.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.HashMap;


import org.apache.http.message.BasicNameValuePair;

import com.example.functions.Constants;
import com.example.functions.Functions;
import com.example.jsimmons.R;
import com.example.utils.NetConnection;
import com.example.utils.TransparentProgressDialog;

import android.app.DatePickerDialog;
import android.app.Dialog;

import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;

import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


public class SubmitAnnouncement1 extends Fragment {

	private View rootView;
	ProgressBar progressBar;
	Boolean isConnected;
	Spinner organisation_spinner, category_spinner, subcategory_spinner;
	TransparentProgressDialog db;

	//Button date_from;
	//Button date_to ;
	//TimePicker time , endtime;
	//Boolean DateFROM = false, DateTO = false;
	//DatePickerDialog.OnDateSetListener dateListener;

	EditText announcement_title;
	
	/*String formattedDateIndian="";
	String formattedDateIndianTo = "";*/

	ArrayList<HashMap<String, String>> organizationList = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> categoryList = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> globalSubCategoryList = new ArrayList<HashMap<String, String>>();

	Button next_button;
	LinearLayout next_layout;

	//static final int DATE_DIALOG_ID = 1;

	protected void showDialog1(String msg) {
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

		rootView = inflater.inflate(R.layout.submit_announcement1, container,
				false);

		init();

		return rootView;
	}

	private void init() {
/*
		date_from = (Button) rootView.findViewById(R.id.date_from);
		date_to = (Button) rootView.findViewById(R.id.date_to);*/
		next_button = (Button) rootView.findViewById(R.id.next_button);
		next_layout = (LinearLayout) rootView.findViewById(R.id.next_layout);
		announcement_title = (EditText) rootView
				.findViewById(R.id.announcement_title);
	/*	time = (TimePicker) rootView.findViewById(R.id.time);
		endtime = (TimePicker) rootView.findViewById(R.id.endtime);
		
		time.setIs24HourView(true);
		endtime.setIs24HourView(true);*/

		next_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goToNextScreen();

			}
		});

		next_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goToNextScreen();
			}
		});

	/*	date_from.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment picker = new DatePickerFragment();
				picker.show(getFragmentManager(), "datePicker");
			}
		});

		date_to.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment picker = new DatePickerFragment1();
				picker.show(getFragmentManager(), "datePicker");
			}
		});*/
		
		/*time.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogFragment picker = new TimePickerFragment1();
				picker.show(getFragmentManager(), "datePicker");
				
			}
		});*/

		/*
		 * date_to.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { String date =
		 * date_from.getText().toString(); if(date.length()>0){
		 * Log.e("hasFocus==", "true"); DateFROM = false; DateTO = true;
		 * showCalendarDialog(); }else {
		 * showDialog("Please select From Date first!"); } } });
		 */

		isConnected = NetConnection.checkInternetConnectionn(getActivity());

		if (isConnected) {

			new OrganizationListAPI(Constants.USER_ID, Constants.AUTH_KEY)
					.execute(new Void[0]);
		} else {
			showDialog1(Constants.No_INTERNET);
		}
		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		organisation_spinner = (Spinner) rootView
				.findViewById(R.id.organisation_spinner);
		category_spinner = (Spinner) rootView
				.findViewById(R.id.category_spinner);
		subcategory_spinner = (Spinner) rootView
				.findViewById(R.id.subcategory_spinner);

		progressBar.setProgress(20);

		category_spinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						
						String cat_id = "";
						try{
						 cat_id = categoryList.get(position-1).get("id");
						} catch(Exception e){
							e.printStackTrace();
						}

//						String[] list = new String[globalSubCategoryList.size() + 1];
						
						ArrayList<String> list = new ArrayList<String>();

						if (globalSubCategoryList.size() > 0) {
							for (int i = 0; i < globalSubCategoryList.size(); i++) {
								if (globalSubCategoryList.get(i).get("cat_id")
										.equals(cat_id)) {
									list.add(globalSubCategoryList.get(i)
											.get("name"));
								}
							}
							
						} else {
							list.add("No sub-category found");
						}
						
						if(list.size()==0){
							list.add("No sub-category found");
						}
						
					
						ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(
								getActivity(), R.layout.simple_spinner_item,
								R.id.text, list);
						subcategory_spinner.setAdapter(dataAdapter2);

					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub

					}
				});
		
	
	}

	protected void goToNextScreen() {

		/**
		 * Validation
		 */

		String title_name = announcement_title.getText().toString();
		int org_pos = organisation_spinner.getSelectedItemPosition();
		int cat_pos = category_spinner.getSelectedItemPosition();
		int sub_pos = subcategory_spinner.getSelectedItemPosition();
		String sub_name = subcategory_spinner.getSelectedItem().toString();

		/*String To_Date = date_to.getText().toString();
		String From_Date = date_from.getText().toString();*/

		if (title_name.trim().length() < 1) {
			announcement_title.setError("Please enter announcement title.");
		} else if (org_pos == 0) {
			Toast.makeText(getActivity(), "Please select organization.",
					Toast.LENGTH_LONG).show();
		} else if (cat_pos == 0) {
			Toast.makeText(getActivity(), "Please select category.",
					Toast.LENGTH_LONG).show();
		} 
		/*else if(From_Date.equals("")){
			date_from.setError("Please select date");
		}
		else if(To_Date.equals("")){
			date_to.setError("Please select date");
		}*/

		/*
		 * else if(sub_pos==0 &&
		 * sub_name.equalsIgnoreCase("Select Sub-category")){
		 * Toast.makeText(getActivity(), "Please select Sub-category.",
		 * Toast.LENGTH_LONG).show(); }
		 */
		else {
			Constants.TITLE_TO_SUBMIT = title_name;
			Constants.ORG_ID_TO_SUBMIT = organizationList.get(org_pos-1).get("org_id");
			Constants.CAT_ID_TO_SUBMIT = categoryList.get(cat_pos-1).get("id");
			
			/*int hours = time.getCurrentHour();
			int min = time.getCurrentMinute();
			
			int Endhours = endtime.getCurrentHour();
			int Endmin = endtime.getCurrentMinute();*/
		
			/*Constants.START_TIME_TO_SUBMIT = ""+hours+":"+min;
			
			Constants.END_TIME_TO_SUBMIT = ""+Endhours+":"+Endmin;*/
			
			if(sub_name.equals("No sub-category found") || sub_name.equals("Select sub-category")){
				Constants.SUB_ID_TO_SUBMIT = "";
			} else {
				for(int i =0 ;i<globalSubCategoryList.size();i++){
					if(globalSubCategoryList.get(i).get("name").equalsIgnoreCase(sub_name)){
						Constants.SUB_ID_TO_SUBMIT = globalSubCategoryList.get(i).get("id");
					}
				}
			}
			
			/*Constants.START_DATE_TO_SUBMIT = date_from.getText().toString();
			Constants.END_DATE_TO_SUBMIT = date_to.getText().toString();*/
			
			/*Constants.START_DATE_TO_SUBMIT = formattedDateIndian;
			
			Constants.END_DATE_TO_SUBMIT = formattedDateIndianTo;*/
			
			/*Log.e("start time=====",""+Constants.START_TIME_TO_SUBMIT);
			Log.e("end time=====",""+Constants.END_TIME_TO_SUBMIT);*/
			
			announcement_title.setText("");
			FragmentManager fm = getActivity().getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			Fragment fragment = new SubmitAnnouncement5();

			if (fragment != null) {
				ft.replace(R.id.frame_layout, fragment);
			} else {
				ft.add(R.id.frame_layout, fragment);
			}
			ft.addToBackStack(null);
			ft.commit();
			// ft.commitAllowingStateLoss();
		}
	}

	public class OrganizationListAPI extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		ArrayList result;

		String userID;
		String authKEY;
		ArrayList localArrayList = new ArrayList();

		public OrganizationListAPI(String uSER_ID, String aUTH_KEY) {

			this.userID = uSER_ID;
			this.authKEY = aUTH_KEY;
		}

		protected Void doInBackground(Void... paramVarArgs) {
			try {

				localArrayList.add(new BasicNameValuePair("user_id",
						this.userID));
				localArrayList.add(new BasicNameValuePair("authKey",
						this.authKEY));
				//result = function.OrganizationListAPI(localArrayList);
				
				result = function.getOrgList(localArrayList);

				Log.e("result item lit==", "" + result);

			} catch (Exception localException) {

			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.size() > 0) {
					organizationList.clear();
					organizationList.addAll(result);
					String[] list = new String[organizationList.size() + 1];
					for (int i = 0; i < organizationList.size(); i++) {
						list[i + 1] = organizationList.get(i).get("Organisation");
					}

					list[0] = "Select organization";

					ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(
							getActivity(), R.layout.simple_spinner_item,
							R.id.text, list);
					organisation_spinner.setAdapter(dataAdapter1);

					//new CategoryListAPI().execute(new Void[0]);
				} else {
					Toast.makeText(getActivity(), "No organization found.",
							Toast.LENGTH_SHORT).show();
					String[] list = new String[] { "No organisation found." };
					ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(
							getActivity(), R.layout.simple_spinner_item,
							R.id.text, list);
					organisation_spinner.setAdapter(dataAdapter1);
				}
				
				new CategoryListAPI().execute(new Void[0]);
			}

			catch (Exception ae) {
				showDialog1(Constants.ERROR_MSG);
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

	public class CategoryListAPI extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		ArrayList result;

		ArrayList localArrayList = new ArrayList();

		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList.add(new BasicNameValuePair("user_id",
						Constants.USER_ID));
				localArrayList.add(new BasicNameValuePair("authKey",
						Constants.AUTH_KEY));
				result = function.Categorylist(localArrayList);

				Log.e("result item lit==", "" + result);

			} catch (Exception localException) {

			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.size() > 0) {
					categoryList.clear();
					categoryList.addAll(result);
					String[] list = new String[categoryList.size() + 1];
					for (int i = 0; i < categoryList.size(); i++) {
						list[i + 1] = categoryList.get(i).get("name");
					}

					list[0] = "Select category";
					ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(
							getActivity(), R.layout.simple_spinner_item,
							R.id.text, list);
					category_spinner.setAdapter(dataAdapter2);

					//new SubCategoryListAPI().execute(new Void[0]);
				} else {
					showDialog1("No category List found.");
					String[] list = new String[] { "No cateogry found." };
					ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(
							getActivity(), R.layout.simple_spinner_item,
							R.id.text, list);
					category_spinner.setAdapter(dataAdapter2);
				}
				
				new SubCategoryListAPI().execute(new Void[0]);
			}

			catch (Exception ae) {
				showDialog1(Constants.ERROR_MSG);
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

	public class SubCategoryListAPI extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		ArrayList result;

		ArrayList localArrayList = new ArrayList();

		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList.add(new BasicNameValuePair("user_id",
						Constants.USER_ID));
				localArrayList.add(new BasicNameValuePair("authKey",
						Constants.AUTH_KEY));
				result = function.SubCategory(localArrayList);

				Log.e("result item lit==", "" + result);

			} catch (Exception localException) {

			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.size() > 0) {
					globalSubCategoryList.clear();
					globalSubCategoryList.addAll(result);

					String[] list = new String[globalSubCategoryList.size() + 1];
					for (int i = 0; i < globalSubCategoryList.size(); i++) {
						list[i + 1] = globalSubCategoryList.get(i).get("name");
					}

					list[0] = "Select sub-category";
					Log.i("sub cat list====",""+list);
					ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(
							getActivity(), R.layout.simple_spinner_item,
							R.id.text, list);
					subcategory_spinner.setAdapter(dataAdapter2);

				} else {
					showDialog1("No subcategory List found.");
					String[] list = new String[] { "No subcategory found" };
					ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(
							getActivity(), R.layout.simple_spinner_item,
							R.id.text, list);
					subcategory_spinner.setAdapter(dataAdapter2);
				}
			}

			catch (Exception ae) {
				showDialog1(Constants.ERROR_MSG);
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

	/*public class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {
			Calendar c = Calendar.getInstance();
			c.set(year, month, day);

			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
			String formattedDate = sdf.format(c.getTime());

			Log.e("date====", "" + formattedDate);
			date_from.setText(formattedDate);
			
			SimpleDateFormat sdfIndian = new SimpleDateFormat("dd-MM-yyyy");
			 formattedDateIndian = sdfIndian.format(c.getTime());
		}
	}*/

	/*public class DatePickerFragment1 extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {
			Calendar c = Calendar.getInstance();
			c.set(year, month, day);

			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
			String formattedDate = sdf.format(c.getTime());
			
			
			SimpleDateFormat sdfIndian = new SimpleDateFormat("dd-MM-yyyy");
			 formattedDateIndianTo = sdfIndian.format(c.getTime());

			Log.e("date====", "" + formattedDate);
			date_to.setText(formattedDate);
		}
	}*/
}
