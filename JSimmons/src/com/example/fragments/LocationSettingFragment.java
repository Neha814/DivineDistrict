package com.example.fragments;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.example.fragments.FindAnnouncementCategoryFragment.MyAdapter;
import com.example.functions.Constants;
import com.example.functions.Functions;
import com.example.jsimmons.Home;
import com.example.jsimmons.PicAddORgRadius;
import com.example.jsimmons.R;

import com.example.utils.GPSTracker;
import com.example.utils.HttpClientUpload;
import com.example.utils.NetConnection;
import com.example.utils.TransparentProgressDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LocationSettingFragment extends Fragment {

	private View rootView;
	
	EditText radius;
	LinearLayout update_layout;
	Button update;


	Boolean isConnected;
	
	GPSTracker gps;
	double latitude , longitude;
	
	TransparentProgressDialog db;
	
	String radiusValue;
	
	boolean isSucess = false;

	updateProfileTask updateProfileObj;
	
	/**
	 * Variables for google map
	 */
	
	GoogleMap mGoogleMap;
	SupportMapFragment fm;
	Location location;
	LatLng utilis;
	
	ArrayList<HashMap<String, String>> announcmentList = new ArrayList<HashMap<String,String>>();

	private void showGPSDisabledAlertToUser() {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(
				getActivity());
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
					
					if(isSucess){
						goBack();
					}

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

		rootView = inflater.inflate(R.layout.location_fragment, container,
				false);

		
		radius = (EditText) rootView.findViewById(R.id.radius);
		update_layout = (LinearLayout) rootView
				.findViewById(R.id.update_layout);
		update = (Button) rootView.findViewById(R.id.update);
	
		fm = ((SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map));
		
		mGoogleMap = fm.getMap();
		mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
		mGoogleMap.setMyLocationEnabled(true);
		
		mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(9.0F)); 

		if (!((LocationManager) getActivity().getSystemService("location"))
				.isProviderEnabled("gps")) {
			showGPSDisabledAlertToUser();

		}


		update_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateRadius();
			}
		});

		update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateRadius();
			}
		});
		
		new getAnnouncmentList().execute(new Void[0]);
		
		
		
		return rootView;
	}

	protected void updateRadius() {
		radiusValue = radius.getText().toString();
		gps = new GPSTracker(getActivity());
		if (this.gps.canGetLocation()) {
			latitude = gps.getLatitude();
			longitude = gps.getLongitude();
		}
		callAPI();

	}

	private void callAPI() {
		isConnected = NetConnection.checkInternetConnectionn(getActivity());
		if (isConnected) {
			
			updateProfileObj = new updateProfileTask(Constants.USER_ID,
					Constants.AUTH_KEY,radiusValue);
			updateProfileObj.execute();
			
		} else {
			showDialog(Constants.No_INTERNET);
		}
	}



	public void goBack() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment fragment = new NewsFeedFragment();
		
		
	    if(fragment != null) {
        	ft.replace(R.id.frame_layout, fragment);
       }
         else{
             ft.add(R.id.frame_layout, fragment);
            }
      		 
       ft.commit();
       // ft.commitAllowingStateLoss();
	}
	

	
	public class updateProfileTask extends AsyncTask<String, Void, String> {
		ByteArrayOutputStream baos;

		String userID, authKEY, RADIUS;

		public updateProfileTask(String uSER_ID, String aUTH_KEY,String radius_text) {
			this.userID = uSER_ID;
			this.authKEY = aUTH_KEY;
			this.RADIUS = radius_text;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			db = new TransparentProgressDialog(getActivity(),
					R.drawable.loadingicon);
			db.show();

		}

		@Override
		protected String doInBackground(String... Params) {
			try {
				baos = new ByteArrayOutputStream();
			//	takenImage.compress(CompressFormat.PNG, 100, baos);
			}

			catch (Exception e) {
				Log.e("excptn==", "" + e);
			}

			/*
			 * http://phphosting.osvin.net/divineDistrict/api/userOptions.php?
			 * user_id
			 * =31&authKey=divineDistrict@31&radius=30&organisations=7,8&image=
			 */

			try {
				HttpClient httpclient = new DefaultHttpClient();

				HttpClientUpload client = new HttpClientUpload(
						"http://phphosting.osvin.net/divineDistrict/api/settings.php?");
				client.connectForMultipart();

				client.addFormPart("user_id", this.userID);
				client.addFormPart("authKey", this.authKEY);
				client.addFormPart("radius", this.RADIUS);
			


				client.finishMultipart();

				String data = client.getResponse();

				Log.e("data==", "" + data);

				return data;
			} catch (Throwable t) {
				t.printStackTrace();
			}

			return null;

		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			db.dismiss();

			try {
				JSONObject localJSONObject = new JSONObject(result);
				HashMap<String, String> localHashMap = new HashMap<String, String>();
				String status = localJSONObject.getString("Status");
				if (status.equalsIgnoreCase("true")) {
					isSucess = true;
					showDialog("Location updated successfully.");
				} else {
					Toast.makeText(getActivity(), "Something went wrong while updating your location.Please try again.", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				Log.e("Exception===", "" + e);
				showDialog("Something went wrong while processing your request.Please try again.");
			}
		}

	}

	
	
	
	public class getAnnouncmentList extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		ArrayList result;

		ArrayList localArrayList = new ArrayList();

		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList
				.add(new BasicNameValuePair("user_id", Constants.USER_ID));
				localArrayList
				.add(new BasicNameValuePair("authKey", Constants.AUTH_KEY));
				result = function.announcmenetList(localArrayList);

				Log.e("result item lit==", "" + result);

			} catch (Exception localException) {

			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.size() > 0) {
					
					announcmentList.addAll(result);
					
					for(int i =0 ; i<result.size();i++){
						double lati = Double.parseDouble(announcmentList.get(i)
								.get("latitude"));
						double longLat = Double.parseDouble(announcmentList.get(i)
								.get("longitute"));
					}
					
				} else {
					Log.e("NO announcement found","No announcment found");
				}
			}

			catch (Exception ae) {
				Log.e("NO announcement found","No announcment found");
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
