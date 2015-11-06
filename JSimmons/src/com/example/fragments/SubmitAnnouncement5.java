package com.example.fragments;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.example.functions.Constants;
import com.example.jsimmons.R;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TimePicker;

public class SubmitAnnouncement5 extends Fragment {

	Button date_from;
	Button date_to;
	TimePicker time, endtime;
	Boolean DateFROM = false, DateTO = false;
	DatePickerDialog.OnDateSetListener dateListener;

	String formattedDateIndian = "";
	String formattedDateIndianTo = "";

	static final int DATE_DIALOG_ID = 1;
	
	ProgressBar progressBar;

	private View rootView;

	LinearLayout next_layout;
	Button next_button;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.submit_announcement_5, container,
				false);

		init();

		return rootView;
	}

	private void init() {

		date_from = (Button) rootView.findViewById(R.id.date_from);
		date_to = (Button) rootView.findViewById(R.id.date_to);
		time = (TimePicker) rootView.findViewById(R.id.time);
		endtime = (TimePicker) rootView.findViewById(R.id.endtime);
		next_button = (Button) rootView.findViewById(R.id.next_button);
		next_layout = (LinearLayout) rootView.findViewById(R.id.next_layout);
		
		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		
		progressBar.setProgress(40);

		time.setIs24HourView(true);
		endtime.setIs24HourView(true);

		date_from.setOnClickListener(new OnClickListener() {

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
		});

		next_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ValidateAndGoToNExtPage();
			}
		});

		next_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ValidateAndGoToNExtPage();
			}
		});
	}

	protected void ValidateAndGoToNExtPage() {
		String To_Date = date_to.getText().toString();
		String From_Date = date_from.getText().toString();

		if (From_Date.equals("")) {
			date_from.setError("Please select date");
		} else if (To_Date.equals("")) {
			date_to.setError("Please select date");
		} else {
			int hours = time.getCurrentHour();
			int min = time.getCurrentMinute();

			int Endhours = endtime.getCurrentHour();
			int Endmin = endtime.getCurrentMinute();

			Constants.START_TIME_TO_SUBMIT = "" + hours + ":" + min;
			Constants.END_TIME_TO_SUBMIT = "" + Endhours + ":" + Endmin;
			
			Constants.START_DATE_TO_SUBMIT = formattedDateIndian;
			Constants.END_DATE_TO_SUBMIT = formattedDateIndianTo;
			
			Log.e("start time=====",""+Constants.START_TIME_TO_SUBMIT);
			Log.e("end time=====",""+Constants.END_TIME_TO_SUBMIT);
			
			FragmentManager fm = getActivity().getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			Fragment fragment = new SubmitAnnouncement2();

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

			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
			String formattedDate = sdf.format(c.getTime());

			Log.e("date====", "" + formattedDate);
			date_from.setText(formattedDate);

			SimpleDateFormat sdfIndian = new SimpleDateFormat("dd-MM-yyyy");
			formattedDateIndian = sdfIndian.format(c.getTime());
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

			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
			String formattedDate = sdf.format(c.getTime());

			SimpleDateFormat sdfIndian = new SimpleDateFormat("dd-MM-yyyy");
			formattedDateIndianTo = sdfIndian.format(c.getTime());

			Log.e("date====", "" + formattedDate);
			date_to.setText(formattedDate);
		}
	}
}
