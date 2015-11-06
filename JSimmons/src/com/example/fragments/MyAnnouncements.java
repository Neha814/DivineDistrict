package com.example.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;

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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import android.widget.TextView;

public class MyAnnouncements extends Fragment {

	private View rootView;

	ListView my_ann_listview;
	TransparentProgressDialog db;
	MyAdapter mAdapter;
	boolean isConnected;

	ArrayList<HashMap<String, String>> myAnnouncementList = new ArrayList<HashMap<String, String>>();

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

		rootView = inflater.inflate(R.layout.my_announcemnts, container, false);
		init();

		return rootView;
	}

	private void init() {

		my_ann_listview = (ListView) rootView
				.findViewById(R.id.my_ann_listview);
		
		my_ann_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				goToNextPAge(position);
			}
		});

		CallMyAnnAPI();
	}

	protected void goToNextPAge(int position) {

		Constants.MY_ANN_TITLE = myAnnouncementList.get(position).get("title");
		Constants.MY_ANN_DES = myAnnouncementList.get(position).get("description");
		Constants.MY_ANN_ADDRESS = myAnnouncementList.get(position).get("address")+" ,"+
		myAnnouncementList.get(position).get("city")+" ,"+ myAnnouncementList.get(position).get("state")+
		" ,"+ myAnnouncementList.get(position).get("country");
		Constants.MY_ANN_DATE = myAnnouncementList.get(position).get("start_date");
		Constants.MY_ANN_PRICE = myAnnouncementList.get(position).get("price");
		try {
		Constants.MY_ANN_LAT = Double.parseDouble(myAnnouncementList.get(position).get("latitude"));
		Constants.MY_ANN_LONG = Double.parseDouble(myAnnouncementList.get(position).get("longitute"));
		} catch(Exception e){
			Constants.MY_ANN_LAT = 0;
			Constants.MY_ANN_LONG = 0;
		}
		Constants.MY_ANN_IMAGE = myAnnouncementList.get(position).get("image");
		
		goToNExtPage();
		
	}

	private void goToNExtPage() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment fragment = new MyAnnoncemnetDetail();

		if (fragment != null) {
			ft.replace(R.id.frame_layout, fragment);
		} else {
			ft.add(R.id.frame_layout, fragment);
		}
		ft.addToBackStack(null);
		// / ft.commit();
		ft.commitAllowingStateLoss();
		
	}

	private void CallMyAnnAPI() {
		isConnected = NetConnection.checkInternetConnectionn(getActivity());
		if (isConnected) {

			new MyAnnList(Constants.USER_ID, Constants.AUTH_KEY)
					.execute(new Void[0]);

		} else {
			showDialog(Constants.No_INTERNET);
		}
	}

	public class MyAnnList extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		ArrayList result;

		String userID;
		String authKEY;
		ArrayList localArrayList = new ArrayList();

		public MyAnnList(String user_id, String auth_key) {
			this.userID = user_id;
			this.authKEY = auth_key;
		}

		protected Void doInBackground(Void... paramVarArgs) {
			try {

				localArrayList.add(new BasicNameValuePair("user_id",
						this.userID));
				localArrayList.add(new BasicNameValuePair("authKey",
						this.authKEY));
				result = function.getAnnList(localArrayList);

				Log.e("result item lit==", "" + result);

			} catch (Exception localException) {
				localException.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.size() > 0) {
					myAnnouncementList.clear();
					myAnnouncementList.addAll(result);
					
					Log.e("myAnnouncementList",""+myAnnouncementList);

					mAdapter = new MyAdapter(myAnnouncementList, getActivity());
					my_ann_listview.setAdapter(mAdapter);
				} else {

					showDialog("No announcement found under my Announcements!!");
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

			return myAnnouncementList.size();
		}

		@Override
		public Object getItem(int position) {

			return myAnnouncementList.get(position);
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
				convertView = mInflater.inflate(R.layout.subcategory_listitem,
						null);
				holder.subcategory = (TextView) convertView
						.findViewById(R.id.subcategory);

				convertView.setTag(holder);

			}

			else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.subcategory.setText(myAnnouncementList.get(position).get(
					"title"));

			return convertView;
		}

		class ViewHolder {
			TextView subcategory;
		}
	}
}
