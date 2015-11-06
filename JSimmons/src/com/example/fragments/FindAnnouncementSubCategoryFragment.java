package com.example.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;

import com.example.fragments.AnnouncementFragment.getAnnouncemntList;
import com.example.functions.Constants;
import com.example.functions.Functions;
import com.example.jsimmons.R;
import com.example.utils.NetConnection;
import com.example.utils.TransparentProgressDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class FindAnnouncementSubCategoryFragment extends Fragment {
	
	private View rootView;
	ListView listview;
	Boolean isConnected;
	MyAdapter mAdapter;
	TransparentProgressDialog db;
	ArrayList<HashMap<String, String>> subcategoryList = new ArrayList<HashMap<String, String>>();
	Boolean noListFound = false;
	String cat_id;
	
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
				if(noListFound){
					goBack();
				}

			}
		});
		dialog.show();
		} catch(Exception e){
			e.printStackTrace();
		}

	}

	protected void goBack() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment fragment = new FindAnnouncementCategoryFragment();
		
	    if(fragment != null) {
        	ft.replace(R.id.frame_layout, fragment);
       }
         else{
             ft.add(R.id.frame_layout, fragment);
            }
        //ft.addToBackStack(null);		 
        ft.commit();
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.find_announcement_subcategory, container, false);
		init();

		return rootView;
	}

	private void init() {
		listview  = (ListView) rootView.findViewById(R.id.listview);
	
		
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String Subcat_id = subcategoryList.get(position).get("id");
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				Fragment fragment = new AnnouncementFragment();
				
				Constants.SUBCAT_ID = Subcat_id;
				
			    if(fragment != null) {
		        	ft.replace(R.id.frame_layout, fragment);
		       }
		         else{
		             ft.add(R.id.frame_layout, fragment);
		            }
	        ft.addToBackStack(null);		 
		        ft.commit();				
			}
		});
		
		try {
		
		
        cat_id = Constants.CAT_ID;
		} catch(Exception e){
			e.printStackTrace();
		}
		
		isConnected = NetConnection.checkInternetConnectionn(getActivity());
		
		
		if (!((LocationManager) getActivity().getSystemService("location"))
				.isProviderEnabled("gps")) {
			showGPSDisabledAlertToUser();
			//return;
			if (isConnected) {
				new getSubCategoryList(cat_id).execute(new Void[0]);
			} else {
				showDialog(Constants.No_INTERNET);
			}
		} else {
			if (isConnected) {
				new getSubCategoryList(cat_id).execute(new Void[0]);
			} else {
				showDialog(Constants.No_INTERNET);
			}
		}
		
	}

	//***************** category list API ****************//
	
	private void showGPSDisabledAlertToUser() {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(getActivity());
		localBuilder
				.setMessage(
						"GPS is disabled in your device. Would you like to enable it?")
				.setCancelable(false)
				.setPositiveButton("Goto Settings Page To Enable GPS",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface paramAnonymousDialogInterface,
									int paramAnonymousInt) {
								
								Intent localIntent2 = new Intent(
										"android.settings.LOCATION_SOURCE_SETTINGS");
								startActivity(localIntent2);
								
							}
						});
		localBuilder.create().show();
	}

	public class getSubCategoryList extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		ArrayList result;
		String catID;
		ArrayList localArrayList = new ArrayList();

		public getSubCategoryList(String cat_id) {
			catID = cat_id;
		}

		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList.add(new BasicNameValuePair("cat_id", this.catID));
				localArrayList
				.add(new BasicNameValuePair("user_id", Constants.USER_ID));
				localArrayList
				.add(new BasicNameValuePair("authKey", Constants.AUTH_KEY));
				result = function.SubCategorylist(localArrayList);

				Log.e("result item lit==", "" + result);

			} catch (Exception localException) {

			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.size() > 0) {
					subcategoryList.clear();
					subcategoryList.addAll(result);
					mAdapter = new MyAdapter(subcategoryList, getActivity());
					listview.setAdapter(mAdapter);
				} else {
					noListFound = true;
					showDialog("No subcategories related to this category!");
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

		public MyAdapter(ArrayList<HashMap<String, String>> list,
				Activity activity) {
			mInflater = LayoutInflater.from(getActivity());
		}

		@Override
		public int getCount() {

			return subcategoryList.size();
		}

		@Override
		public Object getItem(int position) {

			return subcategoryList.get(position);
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
				convertView = mInflater.inflate(R.layout.subcategory_listitem, null);
				holder.subcategory = (TextView) convertView.findViewById(R.id.subcategory);
				convertView.setTag(holder);

			}

			else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.subcategory.setText(subcategoryList.get(position).get("name"));
		

			return convertView;
		}

		class ViewHolder {
			TextView subcategory;
		}
	}
}


