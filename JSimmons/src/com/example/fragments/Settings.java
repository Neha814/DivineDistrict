package com.example.fragments;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.example.fragments.LocationSettingFragment.updateProfileTask;
import com.example.functions.Constants;
import com.example.jsimmons.Home;
import com.example.jsimmons.R;
import com.example.utils.GPSTracker;
import com.example.utils.HttpClientUpload;
import com.example.utils.NetConnection;
import com.example.utils.TransparentProgressDialog;
import com.google.android.gms.maps.model.LatLng;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends Fragment {
	
private View rootView;
	
	LinearLayout update_layout;
	Button update;
	ImageView upload_img;

	Boolean isConnected;

	String radiusValue;
	
	TransparentProgressDialog db;
	
	
	boolean isSucess = false;
	
	Bitmap takenImage;
	File imgFileGallery;
	updateProfileTask updateProfileObj;


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

		rootView = inflater.inflate(R.layout.setting_fragment, container,
				false);

		update_layout = (LinearLayout) rootView
				.findViewById(R.id.update_layout);
		update = (Button) rootView.findViewById(R.id.update);
		upload_img = (ImageView) rootView.findViewById(R.id.upload_img);

		
		imgFileGallery = new File("");

		

		upload_img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent GaleryIntent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(GaleryIntent, 1);
			}
		});

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
		return rootView;
	}

	protected void updateRadius() {
		radiusValue = "";
		if (!(imgFileGallery.getName().equals("") || imgFileGallery
				.getName() == null)) {
		callAPI();
		} else {
			showDialog("Please select image to upload");
		}

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
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		try {

			if (requestCode == 1) {
				Uri selectedImage = data.getData();
				InputStream imageStream = null;
				try {
					imageStream = Home.appContext.openInputStream(selectedImage);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("Exception==", "" + e);
				}
				takenImage = BitmapFactory.decodeStream(imageStream);

				upload_img.setImageBitmap(takenImage);

				/**
				 * saving to file
				 */

				Uri SelectedImage = data.getData();
				String[] FilePathColumn = { MediaStore.Images.Media.DATA };

				Cursor SelectedCursor = Home.appContext.query(SelectedImage,
						FilePathColumn, null, null, null);
				SelectedCursor.moveToFirst();

				int columnIndex = SelectedCursor
						.getColumnIndex(FilePathColumn[0]);
				String picturePath = SelectedCursor.getString(columnIndex);
				SelectedCursor.close();

				Log.e("picturePath==", "" + picturePath);

				imgFileGallery = new File(picturePath);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
				takenImage.compress(CompressFormat.PNG, 100, baos);
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
			

				if (!(imgFileGallery.getName().equals("") || imgFileGallery
						.getName() == null)) {

					Log.e("if ifi ifi fifif", "if if ifi ifi if");
					Log.e("imgFileGallery", "===" + imgFileGallery);

					client.addFilePart("image", imgFileGallery.getName(),
							baos.toByteArray());
				}

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
					showDialog("Profile updated successfully.");
				} else {
					Toast.makeText(getActivity(), "Something went wrong while updating your profile.Please try again.", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				Log.e("Exception===", "" + e);
				showDialog("Something went wrong while processing your request.Please try again.");
			}
		}

	}

}
