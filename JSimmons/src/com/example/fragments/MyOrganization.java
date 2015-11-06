package com.example.fragments;

import java.util.ArrayList;

import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;

import com.example.fragments.AddOrganization.AddOrgAPI;
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
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MyOrganization extends Fragment {

	private View rootView;

	Button add_org;
	Spinner personal_org_spinner;
	ListView my_org_listview;

	TransparentProgressDialog db;

	boolean isConnected;
	MyAdapter mAdapter;

	ArrayList<HashMap<String, String>> MyOrgList = new ArrayList<HashMap<String, String>>();

	ArrayList<Integer> DeletedList = new ArrayList<Integer>();

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

		rootView = inflater.inflate(R.layout.my_organization, container, false);
		init();

		return rootView;
	}

	private void init() {
		add_org = (Button) rootView.findViewById(R.id.add_org);
		personal_org_spinner = (Spinner) rootView
				.findViewById(R.id.personal_org_spinner);
		my_org_listview = (ListView) rootView
				.findViewById(R.id.my_org_listview);

		CallMyOrgAPi();

		add_org.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goToAddORgScreen();
			}
		});
		
		personal_org_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
				if(position>0) {
				
				try {
					
			Constants.ORGANIZATION_ID  = MyOrgList.get(position-1).get("org_id");
			Constants.ORGANIZATION_NAME = MyOrgList.get(position-1).get("Organisation");
			
			FragmentManager fm = getActivity().getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			Fragment fragment = new AnnouncementOrganization();

			
			if (fragment != null) {
				ft.replace(R.id.frame_layout, fragment);
			} else {
				ft.add(R.id.frame_layout, fragment);
			}
			ft.addToBackStack(null);
			// ft.commit();
			ft.commitAllowingStateLoss();
			
			} 
				catch(Exception e){
				e.printStackTrace();
			}
			}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	private void CallMyOrgAPi() {
		isConnected = NetConnection.checkInternetConnectionn(getActivity());
		if (isConnected) {

			new getOrgList(Constants.USER_ID, Constants.AUTH_KEY)
					.execute(new Void[0]);

		} else {
			showDialog(Constants.No_INTERNET);
		}
	}

	protected void goToAddORgScreen() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment fragment = new AddOrganization();

		if (fragment != null) {
			ft.replace(R.id.frame_layout, fragment);
		} else {
			ft.add(R.id.frame_layout, fragment);
		}
		// ft.addToBackStack(null);
		ft.commit();
		// ft.commitAllowingStateLoss();

	}

	public class getOrgList extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		ArrayList result;

		String userID;
		String authKEY;
		ArrayList localArrayList = new ArrayList();

		public getOrgList(String user_id, String auth_key) {
			this.userID = user_id;
			this.authKEY = auth_key;
		}

		protected Void doInBackground(Void... paramVarArgs) {
			try {

				localArrayList.add(new BasicNameValuePair("user_id",
						this.userID));
				localArrayList.add(new BasicNameValuePair("authKey",
						this.authKEY));
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
					MyOrgList.clear();
					MyOrgList.addAll(result);

					mAdapter = new MyAdapter(MyOrgList, getActivity());
					my_org_listview.setAdapter(mAdapter);

					
				} else {

					showDialog("No oraganization found under my organization!!");
				}
				
				String[] list = new String[MyOrgList.size()+1];

				list[0] = "Select organization";
				for (int i = 0; i < MyOrgList.size(); i++) {
					list[i + 1] = MyOrgList.get(i).get("Organisation");
				}

				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
						getActivity(), R.layout.simple_spinner_item,
						R.id.text, list);
				personal_org_spinner.setAdapter(dataAdapter);
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

			return MyOrgList.size();
		}

		@Override
		public Object getItem(int position) {

			return MyOrgList.get(position);
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
				convertView = mInflater.inflate(R.layout.delete_oranization,
						null);
				holder.org_name = (TextView) convertView
						.findViewById(R.id.category);
				holder.delete = (Button) convertView.findViewById(R.id.delete);

				holder.rr1 = (RelativeLayout) convertView
						.findViewById(R.id.rr1);

				convertView.setTag(holder);

			}

			else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.delete.setTag(position);

			holder.org_name
					.setText(MyOrgList.get(position).get("Organisation"));

			if (DeletedList.size() > 0) {
				for (int i = 0; i < MyOrgList.size(); i++) {
					if (DeletedList.contains(position)) {
						holder.rr1.setVisibility(View.INVISIBLE);
					} else {
						holder.rr1.setVisibility(View.VISIBLE);
					}
				}
			} else {
				holder.rr1.setVisibility(View.VISIBLE);
			}

			holder.delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					int pos = (Integer) v.getTag();

					delOrg(pos);
				}
			});

			return convertView;
		}

		protected void delOrg(int pos) {
			String id = MyOrgList.get(pos).get("org_id");

			Log.e("id================", "" + id);
			isConnected = NetConnection.checkInternetConnectionn(getActivity());
			if (isConnected) {

				new DeleteOrgAPI(Constants.USER_ID, Constants.AUTH_KEY, id, pos)
						.execute(new Void[0]);
			} else {
				showDialog(Constants.No_INTERNET);
			}
		}

		class ViewHolder {
			TextView org_name;
			Button delete;
			RelativeLayout rr1;
		}
	}

	public class DeleteOrgAPI extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		HashMap result = new HashMap();

		ArrayList localArrayList = new ArrayList();
		String userID, authKEY, id;
		int POS;

		public DeleteOrgAPI(String uSER_ID, String aUTH_KEY, String iD, int pos) {
			this.userID = uSER_ID;
			this.authKEY = aUTH_KEY;
			this.id = iD;
			this.POS = pos;
		}

		/*
		 * http://phphosting.osvin.net/divineDistrict/api/deleteOrg.php?
		 * user_id=31&authKey=divineDistrict@31&org_id=5
		 */

		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList.add(new BasicNameValuePair("user_id",
						this.userID));
				localArrayList.add(new BasicNameValuePair("authKey",
						this.authKEY));
				localArrayList.add(new BasicNameValuePair("org_id", this.id));

				result = function.deleteOrg(localArrayList);

			} catch (Exception localException) {
				localException.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.get("Status").equals("true")) {

					showDialog("Organization deleted successfully.");

					DeletedList.add(POS);

					mAdapter.notifyDataSetChanged();
				} else if (result.get("Status").equals("false")) {
					// showDialog("Organization already adde.");
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
