package com.example.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.functions.Constants;
import com.example.functions.Functions;
import com.example.jsimmons.Home;
import com.example.jsimmons.R;
import com.example.utils.NetConnection;
import com.example.utils.TransparentProgressDialog;
import com.macrew.imageloader.ImageLoader;

public class FindAnnouncementCategoryFragment extends Fragment {
	
	private View rootView;
	ListView listview;
	Boolean isConnected;
	MyAdapter mAdapter;
	TransparentProgressDialog db;
	ArrayList<HashMap<String, String>> categoryList = new ArrayList<HashMap<String, String>>();
	TextView name;
	LinearLayout news_layout;
	
	ImageView user_image;
	ImageLoader imageLoader;
	ImageButton chat, notification , setting;
	
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

		rootView = inflater.inflate(R.layout.find_announcement_category, container, false);
		init();
		
		

		return rootView;
	}

	private void init() {
		
		imageLoader = new ImageLoader(getActivity());
		
		listview  = (ListView) rootView.findViewById(R.id.listview);
		user_image = (ImageView) rootView.findViewById(R.id.user_image);
		name = (TextView) rootView.findViewById(R.id.name);
		news_layout = (LinearLayout) rootView.findViewById(R.id.news_layout);
		
		chat = (ImageButton) rootView.findViewById(R.id.chat);
		notification = (ImageButton) rootView.findViewById(R.id.notification);
		setting = (ImageButton) rootView.findViewById(R.id.setting);
		
		imageLoader.DisplayImage("http://"+Constants.PROFILE_PIC_URL, R.drawable.profile_pic, user_image );
		
		
		chat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				Fragment fragment = new PostFragment();

				if (fragment != null) {
					ft.replace(R.id.frame_layout, fragment);
				} else {
					ft.add(R.id.frame_layout, fragment);
				}
				// /ft.addToBackStack(null);
				// ft.commit();
				ft.commitAllowingStateLoss();
			}
		});

		setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				Fragment fragment = new LocationSettingFragment();

				if (fragment != null) {
					ft.replace(R.id.frame_layout, fragment);
				} else {
					ft.add(R.id.frame_layout, fragment);
				}
				// /ft.addToBackStack(null);
				// ft.commit();
				ft.commitAllowingStateLoss();
			}
		});
		
	

		notification.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				Fragment fragment = new MyReminders();

				if (fragment != null) {
					ft.replace(R.id.frame_layout, fragment);
				} else {
					ft.add(R.id.frame_layout, fragment);
				}
				// /ft.addToBackStack(null);
				// ft.commit();
				ft.commitAllowingStateLoss();
			}
		});
		
		name.setText(Constants.USERNAME);
		
		isConnected = NetConnection.checkInternetConnectionn(getActivity());
		if (isConnected) {
			new getCategoryList().execute(new Void[0]);
		} else {
			showDialog(Constants.No_INTERNET);
		}
		
		
		news_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				Fragment fragment = null;

				fragment = new NewsFeedFragment();

				if (fragment != null) {

					ft.replace(R.id.frame_layout, fragment);
				} else {
					ft.add(R.id.frame_layout, fragment);
				}
				 ft.addToBackStack(null);
				ft.commit();
			}
		});
		
		
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String cat_id = categoryList.get(position).get("id");
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				Fragment fragment = new FindAnnouncementSubCategoryFragment();
				
			    
			    Constants.CAT_ID = cat_id;
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
	}

	//***************** category list API ****************//
	
	public class getCategoryList extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		ArrayList result;

		ArrayList localArrayList = new ArrayList();

		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList
				.add(new BasicNameValuePair("user_id", Constants.USER_ID));
				localArrayList
				.add(new BasicNameValuePair("authKey", Constants.AUTH_KEY));
				result = function.Categorylist(localArrayList);

				Log.e("result item lit==", "" + result);

			} catch (Exception localException) {

			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.size() > 0) {
					categoryList.clear();
					categoryList.addAll(result);
					mAdapter = new MyAdapter(categoryList, getActivity());
					listview.setAdapter(mAdapter);
				} else {
					showDialog("No category List found.");
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

			return categoryList.size();
		}

		@Override
		public Object getItem(int position) {

			return categoryList.get(position);
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
				convertView = mInflater.inflate(R.layout.category_listitem, null);
				holder.category = (TextView) convertView.findViewById(R.id.category);
				convertView.setTag(holder);

			}

			else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.category.setText(categoryList.get(position).get("name"));
		

			return convertView;
		}

		class ViewHolder {
			TextView category;
		}
	}
}
