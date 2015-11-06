package com.example.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;

import com.example.fragments.AnnouncementFragment.MyAdapter;
import com.example.fragments.AnnouncementFragment.getAnnouncemntList;
import com.example.fragments.SentFragment.ShareAPI;
import com.example.functions.Constants;
import com.example.functions.Functions;
import com.example.jsimmons.R;
import com.example.utils.NetConnection;
import com.example.utils.TransparentProgressDialog;
import com.macrew.imageloader.ImageLoaderPic;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AnnoncemnetDetail extends Fragment {

	private View rootView;
	String announcement_id;
	String title;
	private boolean isConnected;
	boolean noResultFound = false;
	TransparentProgressDialog db;
	LinearLayout view_map;
	Double lat, lng;
	Button attend, join;
	TextView titleTV, description, price, address, date, website;
	ImageButton back, next;
	ImageView image;

	ImageLoaderPic imageLoaderPic;

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
					if (noResultFound) {
						// goBack();
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
		Fragment fragment = new AnnouncementFragment();

		if (fragment != null) {
			ft.replace(R.id.frame_layout, fragment);
		} else {
			ft.add(R.id.frame_layout, fragment);
		}

		ft.commit();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.announcement_detail, container,
				false);
		init();

		return rootView;
	}

	private void init() {

		imageLoaderPic = new ImageLoaderPic(getActivity());
		titleTV = (TextView) rootView.findViewById(R.id.title);
		description = (TextView) rootView.findViewById(R.id.description);
		price = (TextView) rootView.findViewById(R.id.price);
		address = (TextView) rootView.findViewById(R.id.address);
		date = (TextView) rootView.findViewById(R.id.date);
		view_map = (LinearLayout) rootView.findViewById(R.id.view_map);
		attend = (Button) rootView.findViewById(R.id.attend);
		join = (Button) rootView.findViewById(R.id.join);
		back = (ImageButton) rootView.findViewById(R.id.back);
		next = (ImageButton) rootView.findViewById(R.id.next);
		image = (ImageView) rootView.findViewById(R.id.image);
		website = (TextView) rootView.findViewById(R.id.website);

		announcement_id = Constants.ANNOUNCEMENT_ID;
		title = Constants.ANNOUNCEMENT_TITLE;

		titleTV.setText(title);

		isConnected = NetConnection.checkInternetConnectionn(getActivity());
		if (isConnected) {
			new getAnnouncemntDetail(announcement_id).execute(new Void[0]);
		} else {
			showDialog(Constants.No_INTERNET);
		}

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goBack();

			}
		});

		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goFORWARD();
			}
		});

		attend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				Fragment fragment = new AttendFragment();

				Constants.LATITIUDE = lat;
				Constants.LONGITUDE = lng;

				if (fragment != null) {
					ft.replace(R.id.frame_layout, fragment);
				} else {
					ft.add(R.id.frame_layout, fragment);
				}
				ft.addToBackStack(null);
				// ft.commit();
				ft.commitAllowingStateLoss();
			}
		});

		join.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				Fragment fragment = new JoinAnn();

				if (fragment != null) {
					ft.replace(R.id.frame_layout, fragment);
				} else {
					ft.add(R.id.frame_layout, fragment);
				}
				ft.addToBackStack(null);
				// ft.commit();
				ft.commitAllowingStateLoss();
			}
		});

		view_map.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				Fragment fragment = new MapFragment();
				Constants.LATITIUDE = lat;
				Constants.LONGITUDE = lng;

				if (fragment != null) {
					ft.replace(R.id.frame_layout, fragment);
				} else {
					ft.add(R.id.frame_layout, fragment);
				}
				ft.addToBackStack(null);
				// ft.commit();
				ft.commitAllowingStateLoss();

				/*
				 * Intent intent = new
				 * Intent(android.content.Intent.ACTION_VIEW, Uri.parse(
				 * "http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"
				 * )); startActivity(intent);
				 */

			
			}
		});
	}

	protected void goFORWARD() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment fragment = new MapFragment();
		Constants.LATITIUDE = lat;
		Constants.LONGITUDE = lng;

		if (fragment != null) {
			ft.replace(R.id.frame_layout, fragment);
		} else {
			ft.add(R.id.frame_layout, fragment);
		}
		// ft.addToBackStack(null);
		// ft.commit();
		ft.commitAllowingStateLoss();

	}

	public class getAnnouncemntDetail extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		HashMap<String, String> result = new HashMap<String, String>();
		String AnnID;

		ArrayList localArrayList = new ArrayList();

		public getAnnouncemntDetail(String ann_id) {
			AnnID = ann_id;

		}

		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList.add(new BasicNameValuePair("announcement_id",
						this.AnnID));
				localArrayList.add(new BasicNameValuePair("user_id",
						Constants.USER_ID));
				localArrayList.add(new BasicNameValuePair("authKey",
						Constants.AUTH_KEY));

				result = function.getAnnouncemntDetail(localArrayList);

				Log.e("result item lit==", "" + result);

			} catch (Exception localException) {

			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.get("Status").equalsIgnoreCase("True")) {

					try {

						lat = Double.parseDouble((String) result
								.get("latitude"));
						lng = Double.parseDouble((String) result
								.get("longitute"));
					} catch (Exception ae) {
						ae.printStackTrace();
					}

					String url = "http://" + (String) result.get("image");

					imageLoaderPic.DisplayImage(url,
							R.drawable.sample_post_image, image);

					description.setText((String) result.get("description"));
					price.setText((String) result.get("price"));
					website.setText((String) result.get("website"));
					String address_text = (String) result.get("address");
					String city = (String) result.get("city");
					String state = (String) result.get("state");
					String country = (String) result.get("country");
					address.setText(address_text + ", " + city + ", " + state
							+ " ," + country + ".");

					Constants.ORG_ID = (String) result.get("org_id");

					String date_text = (String) result.get("start_date");
					Log.e("date_text==", "" + date_text);
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd-MM-yyyy");
					Date myDate = new Date();
					myDate = dateFormat.parse(date_text);

					SimpleDateFormat formatter = new SimpleDateFormat(
							"d-MMM-yyyy");

					// (3) create a new String using the date format we want
					String DateConverted = formatter.format(myDate);
					date.setText(DateConverted);
					Log.e("myDate==", "" + DateConverted);

					CallViewAPi();

				} else {
					noResultFound = true;
					showDialog("No detail found!");
				}
			}

			catch (Exception ae) {
				noResultFound = true;
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

	public void CallViewAPi() {
		isConnected = NetConnection.checkInternetConnectionn(getActivity());
		if (isConnected) {
			new ViewAPI(Constants.USER_ID, Constants.AUTH_KEY,
					Constants.ORG_ID, Constants.ANNOUNCEMENT_ID)
					.execute(new Void[0]);
		}
	}

	public class ViewAPI extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		HashMap result = new HashMap();

		ArrayList localArrayList = new ArrayList();
		String userID, authKEY, OrgID, AnnID;

		public ViewAPI(String uSER_ID, String aUTH_KEY, String oRG_ID,
				String aNNOUNCEMENT_ID) {
			this.userID = uSER_ID;
			this.authKEY = aUTH_KEY;
			this.OrgID = oRG_ID;
			this.AnnID = aNNOUNCEMENT_ID;
		}

		protected Void doInBackground(Void... paramVarArgs) {

			/*
			 * http://phphosting.osvin.net/divineDistrict/api/orgViews.php?
			 * user_id=31&authKey=divineDistrict@31&announcement_id=30&org_id=6
			 */

			try {
				localArrayList.add(new BasicNameValuePair("user_id",
						this.userID));
				localArrayList.add(new BasicNameValuePair("authKey",
						this.authKEY));
				localArrayList.add(new BasicNameValuePair("announcement_id",
						this.AnnID));
				localArrayList
						.add(new BasicNameValuePair("org_id", this.OrgID));

				result = function.ViewAPI(localArrayList);

			} catch (Exception localException) {

			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.get("Status").equals("true")) {

				} else if (result.get("Status").equals("false")) {

				}
			}

			catch (Exception ae) {
				Toast.makeText(getActivity(), "Something went wrong",
						Toast.LENGTH_SHORT);
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
