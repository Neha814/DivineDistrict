package com.example.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.message.BasicNameValuePair;

import com.example.fragments.AnnouncementFragment.DatePickerFragment;
import com.example.fragments.AnnouncementFragment.DatePickerFragment1;
import com.example.fragments.AnnouncementFragment.MyAdapter;
import com.example.fragments.AnnouncementFragment.MyAdapter.ViewHolder;
import com.example.fragments.MyReminders.SetReminder;
import com.example.functions.Constants;
import com.example.functions.Functions;
import com.example.jsimmons.R;
import com.example.utils.NetConnection;
import com.example.utils.TransparentProgressDialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
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
import android.text.format.DateFormat;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyCalendar extends Fragment {

	private View rootView;

	TextView currentMonth;
	// Button selectedDayMonthYearButton;
	ImageView prevMonth;
	ImageView nextMonth;
	GridView calendarView;
	GridCellAdapter adapter;
	Calendar _calendar;
	Boolean isConnected;

	TransparentProgressDialog db;
	int month, year;
	@SuppressWarnings("unused")
	final DateFormat dateFormatter = new DateFormat();
	final String dateTemplate = "MMM yyyy";

	 EditText date_from, date_to;

	ListView listview;
	MyAdapter mAdapter;
	ArrayList<HashMap<String, String>> CalendarList = new ArrayList<HashMap<String, String>>();

	ArrayList<String> DateList = new ArrayList<String>();

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

		rootView = inflater.inflate(R.layout.mycalendar, container, false);
		date_to = (EditText) rootView.findViewById(R.id.date_to);
		date_from = (EditText) rootView.findViewById(R.id.date_from);
		init();

		return rootView;
	}

	private void init() {
		listview = (ListView) rootView.findViewById(R.id.listview);
		isConnected = NetConnection.checkInternetConnectionn(getActivity());
		if (isConnected) {

			new CalendarApi(Constants.USER_ID, Constants.AUTH_KEY)
					.execute(new Void[0]);
		} else {
			showDialog(Constants.No_INTERNET);
		}
		showCalendar();

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
	}

	private void showCalendar() {
		_calendar = Calendar.getInstance(Locale.getDefault());
		month = _calendar.get(Calendar.MONTH) + 1;
		year = _calendar.get(Calendar.YEAR);
		Log.d("", "Calendar Instance:= " + "Month: " + month + " " + "Year: "
				+ year);

		prevMonth = (ImageView) rootView.findViewById(R.id.prevMonth);

		prevMonth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (month <= 1) {
					month = 12;
					year--;
				} else {
					month--;
				}
				Log.d("", "Setting Prev Month in GridCellAdapter: " + "Month: "
						+ month + " Year: " + year);
				setGridCellAdapterToDate(month, year);

			}
		});

		currentMonth = (TextView) rootView.findViewById(R.id.currentMonth);
		currentMonth.setText(DateFormat.format(dateTemplate,
				_calendar.getTime()));

		nextMonth = (ImageView) rootView.findViewById(R.id.nextMonth);

		nextMonth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (month > 11) {
					month = 1;
					year++;
				} else {
					month++;
				}
				Log.d("", "Setting Next Month in GridCellAdapter: " + "Month: "
						+ month + " Year: " + year);
				setGridCellAdapterToDate(month, year);

			}
		});

		calendarView = (GridView) rootView.findViewById(R.id.calendar);

		// Initialised
		adapter = new GridCellAdapter(getActivity(),
				R.id.calendar_day_gridcell, month, year);
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
	}

	private void setGridCellAdapterToDate(int month, int year) {
		adapter = new GridCellAdapter(getActivity(),
				R.id.calendar_day_gridcell, month, year);
		_calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
		currentMonth.setText(DateFormat.format(dateTemplate,
				_calendar.getTime()));
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
	}

	/************** calendar gridview ************************/
	// Inner Class
	public class GridCellAdapter extends BaseAdapter implements OnClickListener {
		private static final String tag = "GridCellAdapter";
		private final Context _context;

		private final List<String> list;
		private static final int DAY_OFFSET = 1;
		private final String[] weekdays = new String[] { "Sun", "Mon", "Tue",
				"Wed", "Thu", "Fri", "Sat" };
		private final String[] months = { "Jan", "Feb", "Mar", "Apr", "May",
				"Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
		private final int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30,
				31, 30, 31 };
		private int daysInMonth;
		private int currentDayOfMonth;
		private int currentWeekDay;
		private Button gridcell;
		private TextView num_events_per_day;
		private final HashMap<String, Integer> eventsPerMonthMap;
		private final SimpleDateFormat dateFormatter = new SimpleDateFormat(
				"MMM-dd-yyyy");

		// Days in Current Month
		public GridCellAdapter(Context context, int textViewResourceId,
				int month, int year) {
			super();
			this._context = context;
			this.list = new ArrayList<String>();
			Log.d(tag, "==> Passed in Date FOR Month: " + month + " "
					+ "Year: " + year);
			Calendar calendar = Calendar.getInstance();
			setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
			setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
			Log.d(tag, "New Calendar:= " + calendar.getTime().toString());
			Log.d(tag, "CurrentDayOfWeek :" + getCurrentWeekDay());
			Log.d(tag, "CurrentDayOfMonth :" + getCurrentDayOfMonth());

			// Print Month
			printMonth(month, year);

			// Find Number of Events
			eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
		}

		private String getMonthAsString(int i) {
			return months[i];
		}

		private String getWeekDayAsString(int i) {
			return weekdays[i];
		}

		private int getNumberOfDaysOfMonth(int i) {
			return daysOfMonth[i];
		}

		public String getItem(int position) {
			return list.get(position);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		/**
		 * Prints Month
		 * 
		 * @param mm
		 * @param yy
		 */
		private void printMonth(int mm, int yy) {
			Log.d(tag, "==> printMonth: mm: " + mm + " " + "yy: " + yy);
			int trailingSpaces = 0;
			int daysInPrevMonth = 0;
			int prevMonth = 0;
			int prevYear = 0;
			int nextMonth = 0;
			int nextYear = 0;

			int currentMonth = mm - 1;
			String currentMonthName = getMonthAsString(currentMonth);
			daysInMonth = getNumberOfDaysOfMonth(currentMonth);

			Log.d(tag, "Current Month: " + " " + currentMonthName + " having "
					+ daysInMonth + " days.");

			GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);
			Log.d(tag, "Gregorian Calendar:= " + cal.getTime().toString());

			if (currentMonth == 11) {
				prevMonth = currentMonth - 1;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 0;
				prevYear = yy;
				nextYear = yy + 1;
				Log.d(tag, "*->PrevYear: " + prevYear + " PrevMonth:"
						+ prevMonth + " NextMonth: " + nextMonth
						+ " NextYear: " + nextYear);
			} else if (currentMonth == 0) {
				prevMonth = 11;
				prevYear = yy - 1;
				nextYear = yy;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 1;
				Log.d(tag, "**--> PrevYear: " + prevYear + " PrevMonth:"
						+ prevMonth + " NextMonth: " + nextMonth
						+ " NextYear: " + nextYear);
			} else {
				prevMonth = currentMonth - 1;
				nextMonth = currentMonth + 1;
				nextYear = yy;
				prevYear = yy;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				Log.d(tag, "***---> PrevYear: " + prevYear + " PrevMonth:"
						+ prevMonth + " NextMonth: " + nextMonth
						+ " NextYear: " + nextYear);
			}

			int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
			trailingSpaces = currentWeekDay;

			Log.d(tag, "Week Day:" + currentWeekDay + " is "
					+ getWeekDayAsString(currentWeekDay));
			Log.d(tag, "No. Trailing space to Add: " + trailingSpaces);
			Log.d(tag, "No. of Days in Previous Month: " + daysInPrevMonth);

			if (cal.isLeapYear(cal.get(Calendar.YEAR)))
				if (mm == 2)
					++daysInMonth;
				else if (mm == 3)
					++daysInPrevMonth;

			// Trailing Month days
			for (int i = 0; i < trailingSpaces; i++) {
				Log.d(tag,
						"PREV MONTH:= "
								+ prevMonth
								+ " => "
								+ getMonthAsString(prevMonth)
								+ " "
								+ String.valueOf((daysInPrevMonth
										- trailingSpaces + DAY_OFFSET)
										+ i));
				list.add(String
						.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
								+ i)
						+ "-GREY"
						+ "-"
						+ getMonthAsString(prevMonth)
						+ "-"
						+ prevYear);
			}

			// Current Month Days
			for (int i = 1; i <= daysInMonth; i++) {
				Log.d(currentMonthName, String.valueOf(i) + " "
						+ getMonthAsString(currentMonth) + " " + yy);
				if (i == getCurrentDayOfMonth()) {
					list.add(String.valueOf(i) + "-BLUE" + "-"
							+ getMonthAsString(currentMonth) + "-" + yy);
				} else {
					list.add(String.valueOf(i) + "-WHITE" + "-"
							+ getMonthAsString(currentMonth) + "-" + yy);
				}
			}

			// Leading Month days
			for (int i = 0; i < list.size() % 7; i++) {
				Log.d(tag, "NEXT MONTH:= " + getMonthAsString(nextMonth));
				list.add(String.valueOf(i + 1) + "-GREY" + "-"
						+ getMonthAsString(nextMonth) + "-" + nextYear);
			}
		}

		/**
		 * NOTE: YOU NEED TO IMPLEMENT THIS PART Given the YEAR, MONTH, retrieve
		 * ALL entries from a SQLite database for that month. Iterate over the
		 * List of All entries, and get the dateCreated, which is converted into
		 * day.
		 * 
		 * @param year
		 * @param month
		 * @return
		 */
		private HashMap<String, Integer> findNumberOfEventsPerMonth(int year,
				int month) {
			HashMap<String, Integer> map = new HashMap<String, Integer>();

			return map;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) _context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.calendar_cell, parent, false);
			}

			// Get a reference to the Day gridcell
			gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
			gridcell.setOnClickListener(this);

			// ACCOUNT FOR SPACING

			Log.d(tag, "Current Day: " + getCurrentDayOfMonth());
			String[] day_color = list.get(position).split("-");
			String theday = day_color[0];
			String themonth = day_color[2];
			String theyear = day_color[3];
			if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null)) {
				if (eventsPerMonthMap.containsKey(theday)) {
					num_events_per_day = (TextView) row
							.findViewById(R.id.num_events_per_day);
					Integer numEvents = (Integer) eventsPerMonthMap.get(theday);

					num_events_per_day.setText(numEvents.toString());
				}
			}

			// Set the Day GridCell
			gridcell.setText(theday);
			gridcell.setTag(theday + "-" + themonth + "-" + theyear);
			Log.d(tag, "Setting GridCell " + theday + "-" + themonth + "-"
					+ theyear);

			if (day_color[1].equals("GREY")) {
				gridcell.setTextColor(getResources()
						.getColor(R.color.lightgray));
			}
			if (day_color[1].equals("WHITE")) {
				gridcell.setTextColor(getResources().getColor(
						R.color.lightgray02));

				for (int i = 0; i < DateList.size(); i++) {
					String dateCheck = theday + "-" + themonth + "-" + theyear;
					if (DateList.contains(dateCheck)) {
						gridcell.setTextColor(getResources().getColor(
								R.color.green));
					} else {
						gridcell.setTextColor(getResources().getColor(
								R.color.lightgray02));
					}
				}

			}
			if (day_color[1].equals("BLUE")) {
				Log.e("day_color[0]", "" + day_color[0]);
				Log.e("day_color[1]", "" + day_color[1]);
				Log.e("day_color[2]", "" + day_color[2]);
				Log.e("day_color[3]", "" + day_color[3]);

				gridcell.setTextColor(getResources().getColor(R.color.orrange));
			}
			return row;
		}

		@Override
		public void onClick(View view) {
			String date_month_year = (String) view.getTag();

			Log.e("Selected date", date_month_year);

			try {
				Date parsedDate = dateFormatter.parse(date_month_year);
				Log.d(tag, "Parsed Date: " + parsedDate.toString());

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		public int getCurrentDayOfMonth() {
			return currentDayOfMonth;
		}

		private void setCurrentDayOfMonth(int currentDayOfMonth) {
			this.currentDayOfMonth = currentDayOfMonth;
		}

		public void setCurrentWeekDay(int currentWeekDay) {
			this.currentWeekDay = currentWeekDay;
		}

		public int getCurrentWeekDay() {
			return currentWeekDay;
		}
	}

	public class CalendarApi extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		ArrayList result;

		String userID;
		String authKEY;
		ArrayList localArrayList = new ArrayList();

		public CalendarApi(String uSER_ID, String aUTH_KEY) {

			this.userID = uSER_ID;
			this.authKEY = aUTH_KEY;
		}

		protected Void doInBackground(Void... paramVarArgs) {
			try {

				localArrayList.add(new BasicNameValuePair("user_id",
						this.userID));
				localArrayList.add(new BasicNameValuePair("authKey",
						this.authKEY));
				result = function.calendarapi(localArrayList);

				Log.e("result item lit==", "" + result);

			} catch (Exception localException) {

			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.size() > 0) {
					CalendarList.clear();
					CalendarList.addAll(result);
					for (int i = 0; i < CalendarList.size(); i++) {
						String dateConvert = CalendarList.get(i).get(
								"start_date");
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"MM-dd-yyyy");
						Date myDate = new Date();
						try {
							myDate = dateFormat.parse(dateConvert);

							SimpleDateFormat formatter = new SimpleDateFormat(
									"MMM-d-yyyy");
							String DateConverted = formatter.format(myDate);
							DateList.add(DateConverted);

						} catch (ParseException e) {
							e.printStackTrace();
						}

					}

					Log.e("date list===", "" + DateList);
					mAdapter = new MyAdapter(CalendarList, getActivity());
					listview.setAdapter(mAdapter);
				} else {
					// showDialog("No subcategories related to this category!");
					Toast.makeText(getActivity(),
							"No announcement list found.", Toast.LENGTH_SHORT)
							.show();
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
		private ArrayList<HashMap<String, String>> mDisplayedValues;

		public MyAdapter(ArrayList<HashMap<String, String>> list,
				Activity activity) {
			mInflater = LayoutInflater.from(getActivity());
			mDisplayedValues = new ArrayList<HashMap<String, String>>();
			mDisplayedValues.addAll(CalendarList);
		}

		public void filter2() {
			CalendarList.clear();
			CalendarList.addAll(mDisplayedValues);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {

			return CalendarList.size();
		}

		@Override
		public Object getItem(int position) {

			return CalendarList.get(position);
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
				convertView = mInflater.inflate(R.layout.category_listitem,
						null);
				holder.announcemnt_name = (TextView) convertView
						.findViewById(R.id.category);
				holder.ll1 = (LinearLayout) convertView.findViewById(R.id.ll1);

				convertView.setTag(holder);

			}

			else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.announcemnt_name.setText(CalendarList.get(position).get(
					"title"));
			
			Log.i("attendacne status=====",""+CalendarList.get(position).get("attendance_status"));
			/*if(CalendarList.get(position).get("attendance_status").equals("Attending")){
				holder.ll1.setVisibility(View.VISIBLE);
			} else {
				holder.ll1.setVisibility(View.INVISIBLE);
			}*/
			

			return convertView;
		}

		class ViewHolder {
			TextView announcemnt_name;
			LinearLayout ll1;
		}

		public void filter1(String dateFROM, String dateTO) {

			String check = "";
			CalendarList.clear();

			if (dateFROM.length() == 0 && dateTO.length() == 0) {

				CalendarList.addAll(mDisplayedValues);
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

					CalendarList.add(mDisplayedValues.get(i));
					return true;
				}

			} catch (ParseException e) {
				e.printStackTrace();
			}

			return false;
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
			mAdapter.filter1(dateFrom, dateTo);
		}
	}
}
