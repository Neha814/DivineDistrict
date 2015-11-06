package com.example.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;

import com.example.fragments.FindAnnouncementCategoryFragment.MyAdapter;
import com.example.fragments.FindAnnouncementCategoryFragment.getCategoryList;
import com.example.fragments.FindAnnouncementCategoryFragment.MyAdapter.ViewHolder;
import com.example.functions.Constants;
import com.example.functions.Functions;
import com.example.jsimmons.R;
import com.example.utils.NetConnection;
import com.example.utils.TransparentProgressDialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Notification extends Fragment {
	
	private View rootView;
	
	ListView listview;
	
	TextView no_result_found;
	
	boolean isConnected ;
	
	TransparentProgressDialog db ;
	
	MyAdapter mAdapter ;
	
	ArrayList<HashMap<String, String>> notificationList = new ArrayList<HashMap<String,String>>();
	
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

		rootView = inflater.inflate(R.layout.notification, container, false);
		
		isConnected = NetConnection.checkInternetConnectionn(getActivity());
		
		if (isConnected) {
			new getNotification().execute(new Void[0]);
		} else {
			showDialog(Constants.No_INTERNET);
		}
		
		
		init();

		return rootView;
	}

	private void init() {
		listview = (ListView) rootView.findViewById(R.id.listview);
		no_result_found = (TextView) rootView.findViewById(R.id.no_result_found);
	}
	
	//***************** category list API ****************//
	
	public class getNotification extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		ArrayList result;

		ArrayList localArrayList = new ArrayList();

		protected Void doInBackground(Void... paramVarArgs) {

			/* http://phphosting.osvin.net/divineDistrict/api/getNotification.php?
				 org_id=5&user_id=31&authKey=divineDistrict@31*/
				 
				 
			try {
				localArrayList
				.add(new BasicNameValuePair("user_id", Constants.USER_ID));
				localArrayList
				.add(new BasicNameValuePair("authKey", Constants.AUTH_KEY));
				result = function.getNotification(localArrayList);

				Log.e("result item lit==", "" + result);

			} catch (Exception localException) {

			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.size() > 0) {
					no_result_found.setVisibility(View.GONE);
					
					notificationList.clear();
					notificationList.addAll(result);
					mAdapter = new MyAdapter(notificationList, getActivity());
					listview.setAdapter(mAdapter);
				} else {
					no_result_found.setVisibility(View.VISIBLE);
				}
			}

			catch (Exception ae) {
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

		public MyAdapter(ArrayList<HashMap<String, String>> list,
				Activity activity) {
			mInflater = LayoutInflater.from(getActivity());
		}

		@Override
		public int getCount() {

			return notificationList.size();
		}

		@Override
		public Object getItem(int position) {

			return notificationList.get(position);
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
				convertView = mInflater.inflate(R.layout.notification_listitem, null);
				
				holder.subject = (TextView) convertView.findViewById(R.id.subject);
				holder.message = (TextView) convertView.findViewById(R.id.message);
				
				convertView.setTag(holder);

			}

			else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.subject.setText(notificationList.get(position).get("subject"));
			holder.message.setText(notificationList.get(position).get("message"));
			
			return convertView;
		}

		class ViewHolder {
			TextView subject , message;
		}
	}

}
