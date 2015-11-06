package com.example.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.apache.http.message.BasicNameValuePair;

import com.example.fragments.MyCalendar.CalendarApi;
import com.example.fragments.MyCalendar.DatePickerFragment;
import com.example.fragments.MyCalendar.DatePickerFragment1;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MySpiritualGrowth extends Fragment {
	
	private View rootView;
	EditText date_from, date_to;
	ImageButton search_image;
	LinearLayout search_layout;
	EditText search;
	ListView spiritual_listview;
	ListView announcement_listview;
	Boolean isConnected;
	TransparentProgressDialog db;
	MyAdapter mAdapter;
	MyAdapter1 mAdapter1;
	
	Boolean DateFROM = false, DateTO = false;
	
	public  ArrayList<HashMap<String, String>> mDisplayedValues;
	
	ArrayList<HashMap<String, String>> spiritual_list = new ArrayList<HashMap<String,String>>();
	ArrayList<HashMap<String, String>> announcement_list = new ArrayList<HashMap<String,String>>();
	
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

		rootView = inflater.inflate(R.layout.spiritualgrowth_fragment, container, false);
		init();

		return rootView;
	}

	private void init() {
		date_from = (EditText) rootView.findViewById(R.id.date_from);
		date_to = (EditText) rootView.findViewById(R.id.date_to);
		search_image = (ImageButton) rootView.findViewById(R.id.search_image);
		search_layout = (LinearLayout) rootView.findViewById(R.id.search_layout);
		search = (EditText) rootView.findViewById(R.id.search);
		spiritual_listview = (ListView) rootView.findViewById(R.id.spiritual_listview);
		announcement_listview = (ListView) rootView.findViewById(R.id.announcement_listview);
		
		SpiritualAPi();
		
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

					mAdapter1.filter2();
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

					mAdapter1.filter2();

					date_from.setText("");
					date_to.setText("");
				}

			}
		});
	}

	private void SpiritualAPi() {
		isConnected = NetConnection.checkInternetConnectionn(getActivity());
		if (isConnected) {

			/*http://phphosting.osvin.net/divineDistrict/api/addSpiritualGrowth.php?
				user_id=31&authKey=divineDistrict@31*/

			new SpiritualGrowth(Constants.USER_ID, Constants.AUTH_KEY).execute(new Void[0]);
		} else {
			showDialog(Constants.No_INTERNET);
		}
	}
	
	public class SpiritualGrowth extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();

		ArrayList localArrayList = new ArrayList();
		String userID, authKEY;

		public SpiritualGrowth(String uSER_ID, String aUTH_KEY) {
			userID = uSER_ID;
			authKEY = aUTH_KEY;
			
		}

		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList.add(new BasicNameValuePair("user_id",
						this.userID));
				localArrayList.add(new BasicNameValuePair("authKey",
						this.authKEY));
			

				result = function.SpiritualGrowth(localArrayList);

			} catch (Exception localException) {

			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {

				if (result.size() > 0) {
					spiritual_list.clear();
					spiritual_list.addAll(result);
					new AnnouncementList(Constants.USER_ID, Constants.AUTH_KEY).execute(new Void[0]);
					
//					mAdapter = new MyAdapter(spiritual_list, getActivity());
//					spiritual_listview.setAdapter(mAdapter);
				} else {
					showDialog("No Category to show in spirtual growth.");

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
	
	
	public class AnnouncementList extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();

		ArrayList localArrayList = new ArrayList();
		String userID, authKEY;

		public AnnouncementList(String uSER_ID, String aUTH_KEY) {
			userID = uSER_ID;
			authKEY = aUTH_KEY;
			
		}

		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList.add(new BasicNameValuePair("user_id",
						this.userID));
				localArrayList.add(new BasicNameValuePair("authKey",
						this.authKEY));
			

//				/result = function.announcmenetList(localArrayList);
				
				result = function.calendarapi(localArrayList);

			} catch (Exception localException) {

			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				
				mAdapter = new MyAdapter(spiritual_list, getActivity());
				spiritual_listview.setAdapter(mAdapter);

				if (result.size() > 0) {
					announcement_list.clear();
					announcement_list.addAll(result);
				
			
					
					mAdapter1 = new MyAdapter1(announcement_list, getActivity());
					announcement_listview.setAdapter(mAdapter1);
				} else {
					showDialog("No announcement list.");

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
	
	class MyAdapter extends BaseAdapter {

		LayoutInflater mInflater = null;
		
		
		public MyAdapter(ArrayList<HashMap<String, String>> list,
				Activity activity) {
			mInflater = LayoutInflater.from(getActivity());
			
		}

		

		@Override
		public int getCount() {

			return spiritual_list.size();
		}

		@Override
		public Object getItem(int position) {

			return spiritual_list.get(position);
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
				convertView = mInflater
						.inflate(R.layout.spiritual_listitem, null);
				holder.spiritual_name = (TextView) convertView
						.findViewById(R.id.spiritual_name);
				holder.progressBar = (ProgressBar) convertView
						.findViewById(R.id.progressBar);
				

				convertView.setTag(holder);

			}

			else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.spiritual_name.setText(spiritual_list.get(position).get("name"));
			
			int progress = Integer.parseInt(spiritual_list.get(position).get("count"));
			holder.progressBar.setProgress(progress);

			return convertView;
		}

		class ViewHolder {
			TextView spiritual_name;
			ProgressBar progressBar;
		}

			
	}
	
	
	class MyAdapter1 extends BaseAdapter {

		LayoutInflater mInflater = null;

		public MyAdapter1(ArrayList<HashMap<String, String>> list,
				Activity activity) {
			mInflater = LayoutInflater.from(getActivity());
			
			mDisplayedValues = new ArrayList<HashMap<String, String>>();
			mDisplayedValues.addAll(announcement_list);
		}

		@Override
		public int getCount() {

			return announcement_list.size();
		}

		@Override
		public Object getItem(int position) {

			return announcement_list.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}
		
		public void filter2() {
			announcement_list.clear();
			announcement_list.addAll(mDisplayedValues);
			notifyDataSetChanged();
		}
		
		public void filter1(String dateFROM, String dateTO) {

			try {
				String check = "";
				announcement_list.clear();

				if (dateFROM.length() == 0 && dateTO.length() == 0) {

					announcement_list.addAll(mDisplayedValues);
				} else {
					for (int i = 0; i < mDisplayedValues.size(); i++) {
						String dateConvert = mDisplayedValues.get(i).get(
								"start_date");

						dateConvert = dateConvert.replace("-", "/");

						try {

							boolean DateCheck = dateCheck(dateFROM, dateTO,
									dateConvert, i);
							Log.e("datecheck===", "" + DateCheck);

						} catch (Exception e) {
							e.printStackTrace();

						}
					}

				}
				notifyDataSetChanged();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {

				holder = new ViewHolder();
				convertView = mInflater
						.inflate(R.layout.category_listitem, null);
				holder.category = (TextView) convertView.findViewById(R.id.category);
				holder.ll1 = (LinearLayout) convertView.findViewById(R.id.ll1);
				

				convertView.setTag(holder);

			}

			else {
				holder = (ViewHolder) convertView.getTag();
			}

			
			
			holder.category.setText(announcement_list.get(position).get(
					"title"));
			
			if(announcement_list.get(position).get("attendance_status").equals("Attending")){
				holder.ll1.setVisibility(View.VISIBLE);
			} else {
				holder.ll1.setVisibility(View.INVISIBLE);
			}

			return convertView;
		}

		class ViewHolder {
			TextView category;
			LinearLayout ll1;
		}
	}

	
	// ******************* date picker ***************************//

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
				mAdapter1.filter1(dateFrom, dateTo);
			}
		}
		
		Boolean dateCheck(String from, String to, String check, int i) {

			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

			try {
				Date FROM = sdf.parse(from);
				Date TO = sdf.parse(to);
				Date CHECK = sdf.parse(check);

				Log.e("from==", "" + from);
				Log.e("to==", "" + to);
				Log.e("check==", "" + check);

				Log.e("FROM==", "" + FROM);
				Log.e("TO==", "" + TO);
				Log.e("CHECK==", "" + CHECK);

				if (CHECK.compareTo(FROM) > 0 && CHECK.compareTo(TO) < 0) {

					Toast.makeText(getActivity(), "true", Toast.LENGTH_SHORT)
							.show();

					announcement_list.add(mDisplayedValues.get(i));
					return true;
				}

			} catch (ParseException e) {
				e.printStackTrace();
			}

			return false;
		}
}
