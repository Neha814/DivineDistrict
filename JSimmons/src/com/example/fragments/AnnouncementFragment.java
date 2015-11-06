package com.example.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.util.HashMap;

import java.util.Locale;

import org.apache.http.message.BasicNameValuePair;

import com.example.functions.Constants;
import com.example.functions.Functions;
import com.example.jsimmons.R;
import com.example.utils.NetConnection;
import com.example.utils.TransparentProgressDialog;

import android.app.Activity;

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
import android.text.Editable;

import android.text.TextWatcher;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;

import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.ImageButton;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utils.*;

public class AnnouncementFragment extends Fragment {

	private View rootView;
	ListView listview;
	private String cat_id;
	private String subcat_id;
	Boolean isConnected;
	MyAdapter mAdapter;
	TransparentProgressDialog db;
	boolean noListFound = false;
	LinearLayout search_layout;
	ImageButton search_image;
	EditText search;
	TextView announcemnt_text;
	GPSTracker gps;
	EditText date_from, date_to;
	ArrayList<HashMap<String, String>> AnnouncementList = new ArrayList<HashMap<String, String>>();
	
	private double latitude;
	private double longitude;

	Boolean DateFROM = false, DateTO = false;

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
					if (noListFound) {
						goBack();
					}
				}
			});
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void goBack() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment fragment = new FindAnnouncementSubCategoryFragment();

		if (fragment != null) {
			ft.replace(R.id.frame_layout, fragment);
		} else {
			ft.add(R.id.frame_layout, fragment);
		}
		// ft.addToBackStack(null);
		ft.commit();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.announcement_list, container,
				false);
		init();

		return rootView;
	}

	private void init() {
		listview = (ListView) rootView.findViewById(R.id.listview);
		search_layout = (LinearLayout) rootView
				.findViewById(R.id.search_layout);
		search_image = (ImageButton) rootView.findViewById(R.id.search_image);
		search = (EditText) rootView.findViewById(R.id.search);
		announcemnt_text = (TextView) rootView
				.findViewById(R.id.announcemnt_text);
		date_from = (EditText) rootView.findViewById(R.id.date_from);
		date_to = (EditText) rootView.findViewById(R.id.date_to);

		// Boolean dateTrue =
		// dateCheck("02-Jul-2014","02-Sep-2014","02-Oct-2014");

		gps = new GPSTracker(getActivity());
		if (this.gps.canGetLocation()) {
			latitude = gps.getLatitude();
			longitude = gps.getLongitude();
			
			Constants.MY_LATITIUDE = latitude;
			Constants.MY_LONGITUDE = longitude;
		}

		date_from.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				DateFROM = true;
				DateTO = false;
				DialogFragment picker = new DatePickerFragment();
				picker.show(getFragmentManager(), "datePicker");
			}
		});

		date_to.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String date = date_from.getText().toString();
				if (date.length() > 0) {
				
					DateFROM = false;
					DateTO = true;
					DialogFragment picker = new DatePickerFragment1();
					picker.show(getFragmentManager(), "datePicker");
				} else {
					showDialog("Please select From Date first!");
				}
			}
		});

		search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String text = search.getText().toString()
						.toLowerCase(Locale.getDefault());
				mAdapter.filter(text);
			}
		});

		date_from.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String dateFROM = date_from.getText().toString();
				
				DateTO = false;
				DateFROM = true;
				if (dateFROM.trim().length() == 1) {
					Constants.DATE_FROM = "";
					Constants.DATE_TO = "";
						
						mAdapter.filter2();
						date_from.setText("");
						date_to.setText("");
					
				}

			}
		});

		date_to.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				DateTO = true;
				DateFROM = false;
				String dateFROM = date_from.getText().toString();
				String dateTO = date_to.getText().toString()
						.toLowerCase(Locale.getDefault());

				if (dateTO.trim().length() == 1) {
					Constants.DATE_FROM = "";
					Constants.DATE_TO = "";
					
					mAdapter.filter2();
					
					date_from.setText("");
					date_to.setText("");
				}

			}
		});

		cat_id = Constants.CAT_ID;
		subcat_id = Constants.SUBCAT_ID;

		isConnected = NetConnection.checkInternetConnectionn(getActivity());
		if (isConnected) {

			new getAnnouncemntList(cat_id, subcat_id, Constants.USER_ID,
					latitude, longitude).execute(new Void[0]);

		} else {
			showDialog(Constants.No_INTERNET);
		}

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String announcement_id = AnnouncementList.get(position).get(
						"id");
				String title = AnnouncementList.get(position).get("title");
				
				
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				Fragment fragment = new AnnoncemnetDetail();

				Constants.ANNOUNCEMENT_ID = announcement_id;
				
				Constants.ANNOUNCEMENT_TITLE = title;

				if (fragment != null) {
					ft.replace(R.id.frame_layout, fragment);
				} else {
					ft.add(R.id.frame_layout, fragment);
				}
				ft.addToBackStack(null);
				// / ft.commit();
				ft.commitAllowingStateLoss();

			}
		});

		search_image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int text_visi = announcemnt_text.getVisibility();
				int search_visi = search_layout.getVisibility();

				if (text_visi == 0) {
					announcemnt_text.setVisibility(View.INVISIBLE);
					search_layout.setVisibility(View.VISIBLE);
				} else if (search_visi == 0) {
					announcemnt_text.setVisibility(View.VISIBLE);
					search_layout.setVisibility(View.INVISIBLE);
				}
			}
		});
	}

	public class getAnnouncemntList extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		ArrayList result;
		String catID;
		String subcatID;
		String userID;
		String lat;
		String lng;
		ArrayList localArrayList = new ArrayList();

		public getAnnouncemntList(String cat_id, String subcat_id,
				String uSER_ID, double latitude, double longitude) {
			catID = cat_id;
			subcatID = subcat_id;
			userID = uSER_ID;
			lat = String.valueOf(latitude);
			lng = String.valueOf(longitude);
		}

		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList
						.add(new BasicNameValuePair("cat_id", this.catID));
				localArrayList.add(new BasicNameValuePair("subcat_id",
						this.subcatID));
				localArrayList.add(new BasicNameValuePair("user_id",
						this.userID));
				localArrayList.add(new BasicNameValuePair("lat", this.lat));
				localArrayList.add(new BasicNameValuePair("long", this.lng));

				localArrayList.add(new BasicNameValuePair("authKey",
						Constants.AUTH_KEY));
				result = function.getAnnouncemntList(localArrayList);

				Log.e("result item lit==", "" + result);

			} catch (Exception localException) {

			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.size() > 0) {
					AnnouncementList.clear();
					AnnouncementList.addAll(result);
				
					mAdapter = new MyAdapter(AnnouncementList, getActivity());
					listview.setAdapter(mAdapter);
				} else {
					noListFound = true;
					showDialog("No announcement related to this sub-category!");
				}
			}

			catch (Exception ae) {
				noListFound = true;
				showDialog(Constants.ERROR_MSG);
			}

		}

		protected void onPreExecute() {
			super.onPreExecute();
			db = new TransparentProgressDialog(getActivity(),
					R.drawable.loadingicon);
			db.show();
		}

	}

	class MyAdapter extends BaseAdapter {

		LayoutInflater mInflater = null;
		private ArrayList<HashMap<String, String>> mDisplayedValues;

		public MyAdapter(ArrayList<HashMap<String, String>> list,
				Activity activity) {
			mInflater = LayoutInflater.from(getActivity());
			mDisplayedValues = new ArrayList<HashMap<String, String>>();
			mDisplayedValues.addAll(AnnouncementList);
		}

		public void filter1(String dateFROM, String dateTO) {

			String check = "";
			AnnouncementList.clear();

			if (dateFROM.length() == 0 && dateTO.length() == 0) {

				AnnouncementList.addAll(mDisplayedValues);
			} else {
				for (int i = 0; i < mDisplayedValues.size(); i++) {
					String dateConvert = mDisplayedValues.get(i).get(
							"start_date");
					
					dateConvert = dateConvert.replace("-", "/");

					try {
					
						boolean DateCheck = dateCheck(dateFROM, dateTO, dateConvert, i);
						Log.e("datecheck===",""+DateCheck);
					
					} catch (Exception e) {
						e.printStackTrace();
					
					}
				}

			}
			notifyDataSetChanged();
		}
		
		public void filter2(){
			AnnouncementList.clear();
			AnnouncementList.addAll(mDisplayedValues);
			notifyDataSetChanged();
		}

		Boolean dateCheck(String from, String to, String check, int i) {
			
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
          
            try {
            	  Date FROM = sdf.parse(from);
				Date TO = sdf.parse(to);
				Date CHECK = sdf.parse(check);
				
				Log.e("from==",""+from);
				Log.e("to==",""+to);
				Log.e("check==",""+check);
				
				Log.e("FROM==",""+FROM);
				Log.e("TO==",""+TO);
				Log.e("CHECK==",""+CHECK);
				
				  if(CHECK.compareTo(FROM)>0 && CHECK.compareTo(TO)<0){

					  Toast.makeText(getActivity(), "true", Toast.LENGTH_SHORT)
						.show();
				
					  AnnouncementList.add(mDisplayedValues.get(i));
					  return true;
				  }
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
            
            return false;
		}

		public void filter(String charText) {
			charText = charText.toLowerCase(Locale.getDefault());

			AnnouncementList.clear();
			if (charText.length() == 0) {

				AnnouncementList.addAll(mDisplayedValues);
			} else {

				for (int i = 0; i < mDisplayedValues.size(); i++) {

					if (mDisplayedValues.get(i).get("title")
							.toLowerCase(Locale.getDefault())
							.startsWith(charText)) {

						AnnouncementList.add(mDisplayedValues.get(i));

					}
				}
			}
			notifyDataSetChanged();

		}

		@Override
		public int getCount() {

			return AnnouncementList.size();
		}

		@Override
		public Object getItem(int position) {

			return AnnouncementList.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {

				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.announcement_listitem,
						null);
				holder.listitem_name = (TextView) convertView
						.findViewById(R.id.listitem_name);
				holder.distance = (TextView) convertView
						.findViewById(R.id.distance);
				convertView.setTag(holder);

			}

			else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.listitem_name.setText((position + 1) + ". "
					+ AnnouncementList.get(position).get("title"));

			double dist = Double.parseDouble(AnnouncementList.get(position)
					.get("distance"));
			dist = (double) Math.round(dist * 100) / 100;
			holder.distance.setText(dist + " miles");

			return convertView;
		}

		class ViewHolder {
			TextView listitem_name, distance;
		}
	}
	
	//***************** date picker **************************//

	public class DatePickerFragment extends DialogFragment implements
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

			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			String formattedDate = sdf.format(c.getTime());

			
			date_from.setText(formattedDate);
		}
	}

	public class DatePickerFragment1 extends DialogFragment implements
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

			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			String formattedDate = sdf.format(c.getTime());

			Log.e("date====", "" + formattedDate);
			date_to.setText(formattedDate);
			
			String dateFrom = date_from.getText().toString();
			String dateTo = date_to.getText().toString();
			mAdapter.filter1(dateFrom,dateTo);
		}
	}
}
