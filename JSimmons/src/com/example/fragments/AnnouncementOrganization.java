package com.example.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;

import com.example.functions.Constants;
import com.example.functions.Functions;
import com.example.jsimmons.R;
import com.example.utils.NetConnection;
import com.example.utils.TransparentProgressDialog;
import com.macrew.imageloader.ImageLoader;
import com.macrew.imageloader.ImageLoaderPic;

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

import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import android.widget.TextView;

public class AnnouncementOrganization extends Fragment {
	
	TextView announcemnt_text;
	ListView listview;
	
	TransparentProgressDialog db;
	boolean isConnected;
	MyAdapter mAdapter;
	
	ImageLoaderPic imageLoader;
	
	boolean isfailure = false;
	private View rootView;
	
	ArrayList<HashMap<String, String>> MyOrgList = new ArrayList<HashMap<String,String>>();
	
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
					if(isfailure){
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
		Fragment fragment = new MyOrganization();

		
		if (fragment != null) {
			ft.replace(R.id.frame_layout, fragment);
		} else {
			ft.add(R.id.frame_layout, fragment);
		}
		ft.commitAllowingStateLoss();
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.announcement_organization, container, false);
		init();

		return rootView;
	}

	private void init() {
		
		imageLoader = new ImageLoaderPic(getActivity());
		announcemnt_text = (TextView) rootView.findViewById(R.id.announcemnt_text);
		listview = (ListView) rootView.findViewById(R.id.listview);
		
		announcemnt_text.setText(Constants.ORGANIZATION_NAME);
		
		CallAPI();
	}

	private void CallAPI() {
		isConnected = NetConnection.checkInternetConnectionn(getActivity());
		if (isConnected) {

			new getAnnList(Constants.USER_ID, Constants.AUTH_KEY , Constants.ORGANIZATION_ID)
					.execute(new Void[0]);

		} else {
			showDialog(Constants.No_INTERNET);
		}
	}
	
	
	public class getAnnList extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		ArrayList result;

		String userID;
		String OrgID;
		String authKEY;
		ArrayList localArrayList = new ArrayList();

		public getAnnList(String user_id, String auth_key , String orgID) {
			this.userID = user_id;
			this.authKEY = auth_key;
			this.OrgID = orgID;
		}

		protected Void doInBackground(Void... paramVarArgs) {
			try {

				localArrayList.add(new BasicNameValuePair("user_id",
						this.userID));
				localArrayList.add(new BasicNameValuePair("authKey",
						this.authKEY));
				localArrayList.add(new BasicNameValuePair("org_id",
						this.OrgID));
				result = function.getAnnouncmentOrganization(localArrayList);

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
					
					Log.i("listview===",""+listview);
					Log.i("mAdapter===",""+mAdapter);
					Log.i("MyOrgList===",""+MyOrgList);

					mAdapter = new MyAdapter(MyOrgList, getActivity());
					listview.setAdapter(mAdapter);

					
				} else {
					
					isfailure = true;
					showDialog("No announcement found under this organization!!");
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
				convertView = mInflater.inflate(R.layout.ann_org_listitem,
						null);
				holder.title = (TextView) convertView
						.findViewById(R.id.title);
				holder.description = (TextView) convertView.findViewById(R.id.description);
				holder.news_image = (ImageView) convertView.findViewById(R.id.news_image);
				
			

				convertView.setTag(holder);

			}

			else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.title.setText(MyOrgList.get(position).get("title"));
			holder.description.setText(MyOrgList.get(position).get("description"));
			
			String url = MyOrgList.get(position).get("image");
			
			imageLoader.DisplayImage("http://"+url, R.drawable.sample_post_image, holder.news_image);


			return convertView;
		}


		class ViewHolder {
			TextView title , description;
			ImageView news_image;
		}
	}

}
