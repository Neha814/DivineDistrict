package com.example.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.apache.http.message.BasicNameValuePair;

import com.example.fragments.JoinAnnouncement.SubmitAnnouncement;
import com.example.fragments.MyOrganization.MyAdapter;
import com.example.fragments.MyOrganization.MyAdapter.ViewHolder;
import com.example.fragments.SubmitAnnouncement1.CategoryListAPI;
import com.example.fragments.SubmitAnnouncement1.OrganizationListAPI;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AddOrganization extends Fragment {

	private View rootView;

	Button my_org;
	EditText search;
	ListView addorg_listview;
	boolean isConnected;

	TransparentProgressDialog db;
	MyAdapter mAdapter;

	ArrayList<HashMap<String, String>> organizationList = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> mDisplayedValues = new ArrayList<HashMap<String, String>>();

	ArrayList<Integer> AddedList = new ArrayList<Integer>();

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

		rootView = inflater
				.inflate(R.layout.add_organization, container, false);
		init();

		return rootView;
	}

	private void init() {
		my_org = (Button) rootView.findViewById(R.id.my_org);
		search = (EditText) rootView.findViewById(R.id.search);
		addorg_listview = (ListView) rootView
				.findViewById(R.id.addorg_listview);

		isConnected = NetConnection.checkInternetConnectionn(getActivity());
		if (isConnected) {

			new OrganizationListAPI(Constants.USER_ID, Constants.AUTH_KEY)
					.execute(new Void[0]);
		} else {
			showDialog(Constants.No_INTERNET);
		}

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
				try {
				mAdapter.filter(text);
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		});

		my_org.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goToMyOrgScreen();
			}
		});
	}

	protected void goToMyOrgScreen() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment fragment = new MyOrganization();

		if (fragment != null) {
			ft.replace(R.id.frame_layout, fragment);
		} else {
			ft.add(R.id.frame_layout, fragment);
		}
		// ft.addToBackStack(null);
		ft.commit();
		// ft.commitAllowingStateLoss();
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
				result = function.OrganizationListAPI(localArrayList);

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

					mAdapter = new MyAdapter(organizationList, getActivity());
					addorg_listview.setAdapter(mAdapter);

				} else {
					showDialog("No organization list found.");
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
			mDisplayedValues = new ArrayList<HashMap<String, String>>();
			mDisplayedValues.addAll(organizationList);

		}

		public void filter(String charText) {
			charText = charText.toLowerCase(Locale.getDefault());
			
			organizationList.clear();
			if (charText.length() == 0) {

				organizationList.addAll(mDisplayedValues);
			} else {
				for(int i =0;i<mDisplayedValues.size();i++){
					if(mDisplayedValues.get(i).get("username").toLowerCase(Locale.getDefault())
							.startsWith(charText)){
						organizationList.add(mDisplayedValues.get(i));
					}
				}
			}
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {

			return organizationList.size();
		}

		@Override
		public Object getItem(int position) {

			return organizationList.get(position);
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
						.inflate(R.layout.add_org_listitem, null);
				holder.org_name = (TextView) convertView
						.findViewById(R.id.category);
				holder.add = (Button) convertView.findViewById(R.id.add);

				convertView.setTag(holder);

			}

			else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (AddedList.size() > 0) {
				for (int i = 0; i < organizationList.size(); i++) {
					if (AddedList.contains(position)) {
						holder.add.setVisibility(View.INVISIBLE);
					} else {
						if (organizationList.get(position).get("isLike")
								.equals("0")) {
							holder.add.setVisibility(View.VISIBLE);
						} else {
							holder.add.setVisibility(View.INVISIBLE);
						}
					}
				}
			} else {
				if (organizationList.get(position).get("isLike").equals("0")) {
					holder.add.setVisibility(View.VISIBLE);
				} else {
					holder.add.setVisibility(View.INVISIBLE);
				}
			}

			holder.add.setTag(position);

			holder.org_name.setText(organizationList.get(position).get(
					"username"));

			holder.add.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int pos = (Integer) v.getTag();

					addOrgAPI(pos);
				}
			});

			return convertView;
		}

		protected void addOrgAPI(int pos) {

			String id = organizationList.get(pos).get("id");
			isConnected = NetConnection.checkInternetConnectionn(getActivity());
			if (isConnected) {

				new AddOrgAPI(Constants.USER_ID, Constants.AUTH_KEY, id, pos)
						.execute(new Void[0]);
			} else {
				showDialog(Constants.No_INTERNET);
			}
		}

		class ViewHolder {
			TextView org_name;
			Button add;
		}
	}

	public class AddOrgAPI extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		HashMap result = new HashMap();

		ArrayList localArrayList = new ArrayList();
		String userID, authKEY, id;
		int POS;

		public AddOrgAPI(String uSER_ID, String aUTH_KEY, String iD, int pos) {
			this.userID = uSER_ID;
			this.authKEY = aUTH_KEY;
			this.id = iD;
			this.POS = pos;
		}

		/*
		 * http://phphosting.osvin.net/divineDistrict/api/addOrganisation .php?
		 * user_id=31&authKey=divineDistrict@31&org_id=5
		 */

		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList.add(new BasicNameValuePair("user_id",
						this.userID));
				localArrayList.add(new BasicNameValuePair("authKey",
						this.authKEY));
				localArrayList.add(new BasicNameValuePair("org_id", this.id));

				result = function.addOrg(localArrayList);

			} catch (Exception localException) {

			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.get("Status").equals("true")) {

					showDialog("Organization added successfully.");

					AddedList.add(POS);

					mAdapter.notifyDataSetChanged();
				} else if (result.get("Status").equals("false")) {
					showDialog("Organization already addedd.");
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

}
