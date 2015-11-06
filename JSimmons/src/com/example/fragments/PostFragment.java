package com.example.fragments;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputFilter.LengthFilter;
import android.util.EventLogTags.Description;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fragments.NewsFeedFragment.getNewsFeedListTask;
import com.example.functions.Constants;
import com.example.functions.Functions;
import com.example.jsimmons.Home;
import com.example.jsimmons.R;

import com.example.utils.HttpClientUpload;
import com.example.utils.NetConnection;
import com.example.utils.TransparentProgressDialog;

public class PostFragment extends Fragment {

	private View rootView;

	ImageView upload_img;
	ImageView camera_img;
	ImageView user_image;
	EditText write_post;
	Button post;
	LinearLayout post_layout;
	Bitmap takenImage;
	File imgFileGallery;
	Boolean isConnected;
	public String photoFileName;
	TransparentProgressDialog db;
	updateProfileTask updateProfileObj;

	boolean isCamera = false, isGallery = false;

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

		rootView = inflater.inflate(R.layout.post_fragment, container, false);
		init();
		return rootView;
	}

	private void init() {
		upload_img = (ImageView) rootView.findViewById(R.id.upload_img);
		camera_img = (ImageView) rootView.findViewById(R.id.camera_img);
		user_image = (ImageView) rootView.findViewById(R.id.user_image);
		write_post = (EditText) rootView.findViewById(R.id.write_post);

		post = (Button) rootView.findViewById(R.id.post);
		post_layout = (LinearLayout) rootView.findViewById(R.id.post_layout);

		imgFileGallery = new File("");

		camera_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!isGallery) {
					photoFileName = System.currentTimeMillis() + ".png";
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT,
							getPhotoFileUri(photoFileName)); // set the image
																// file name
					isCamera = true;

					startActivityForResult(intent, 0);
				} else {
					Toast.makeText(getActivity(), "Photo is already uploaded.",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		upload_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isCamera) {
					Intent GaleryIntent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					isGallery = true;
					startActivityForResult(GaleryIntent, 1);
				} else {
					Toast.makeText(getActivity(), "Photo is already captured.",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		post.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PostAPI();
			}
		});

		post_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PostAPI();
			}
		});
	}

	public Uri getPhotoFileUri(String fileName) {
		// Get safe storage directory for photos
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"");

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
			Log.d("", "failed to create directory");
		}

		// Return the file target for the photo based on filename
		return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator
				+ fileName));
	}

	protected void PostAPI() {
		String description = write_post.getText().toString();

		if (description.trim().length() < 1) {
			write_post.setError("Please write description");
		} else {
			isConnected = NetConnection.checkInternetConnectionn(getActivity());
			if (isConnected) {
				updateProfileObj = new updateProfileTask(Constants.USER_ID,
						Constants.AUTH_KEY, description);
				updateProfileObj.execute();

			} else {
				showDialog(Constants.No_INTERNET);
			}
		}
	}

	// public class PostNews extends AsyncTask<Void, Void, Void> {
	// Functions function = new Functions();
	//
	// HashMap result = new HashMap();
	//
	// ArrayList localArrayList = new ArrayList();
	// String userID, tITILE, authKEY, dESCRIPTION;
	//
	// public PostNews(String uSER_ID, String aUTH_KEY, String title,
	// String description2) {
	// this.userID = uSER_ID;
	// this.authKEY = aUTH_KEY;
	// this.dESCRIPTION = description2;
	// this.tITILE = title;
	// }
	//
	// protected Void doInBackground(Void... paramVarArgs) {
	// try {
	//
	// /*
	// * http://phphosting.osvin.net/divineDistrict/api/addNews.php?
	// * user_id
	// * =31&authKey=divineDistrict@31&title=News%20test&description
	// * =newsDescription
	// */
	//
	// localArrayList.add(new BasicNameValuePair("user_id",
	// this.userID));
	// localArrayList.add(new BasicNameValuePair("authKey",
	// this.authKEY));
	// localArrayList
	// .add(new BasicNameValuePair("title", this.tITILE));
	// localArrayList.add(new BasicNameValuePair("description",
	// this.dESCRIPTION));
	//
	// result = function.postNews(localArrayList);
	//
	// } catch (Exception localException) {
	// localException.printStackTrace();
	// }
	//
	// return null;
	// }
	//
	// protected void onPostExecute(Void paramVoid) {
	// try {
	// Log.e("LIKE result==", "" + result);
	// if (result.get("Status").equals("true")) {
	// Toast.makeText(getActivity(), "News posted successfully..",
	// Toast.LENGTH_SHORT).show();
	// FragmentManager fm = getActivity()
	// .getSupportFragmentManager();
	// FragmentTransaction ft = fm.beginTransaction();
	// Fragment fragment = new NewsFeedFragment();
	//
	// if (fragment != null) {
	// ft.replace(R.id.frame_layout, fragment);
	// } else {
	// ft.add(R.id.frame_layout, fragment);
	// }
	// // ft.addToBackStack(null);
	// // ft.commit();
	// ft.commitAllowingStateLoss();
	// } else if (result.get("Status").equals("false")) {
	// Toast.makeText(
	// getActivity(),
	// "News not posted. Something went wrong.Please try again...",
	// Toast.LENGTH_SHORT).show();
	// }
	//
	// }
	//
	// catch (Exception ae) {
	// ae.printStackTrace();
	// // showDialog(Constants.ERROR_MSG);
	// }
	//
	// }
	//
	// protected void onPreExecute() {
	// super.onPreExecute();
	// }
	//
	// }

	public class updateProfileTask extends AsyncTask<String, Void, String> {
		ByteArrayOutputStream baos;

		String userID, authKEY, dESCRIPTION;

		public updateProfileTask(String uSER_ID, String aUTH_KEY,
				String description) {
			this.userID = uSER_ID;
			this.authKEY = aUTH_KEY;
			this.dESCRIPTION = description;
			//this.dESCRIPTION = URLEncoder.encode(description);

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

			try {
				HttpClient httpclient = new DefaultHttpClient();

				HttpClientUpload client = new HttpClientUpload(
						"http://phphosting.osvin.net/divineDistrict/api/addNews.php?");
				client.connectForMultipart();

				client.addFormPart("user_id", this.userID);
				client.addFormPart("authKey", this.authKEY);
				client.addFormPart("description", this.dESCRIPTION);

				if (!(imgFileGallery.getName().equals("") || imgFileGallery
						.getName() == null)) {
					
					Log.e("img file===",""+ imgFileGallery.getName());
					
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
					localHashMap.put("Status", "true");
					localHashMap.put("Message",
							localJSONObject.getString("Message"));

					Toast.makeText(getActivity(), "News posted successfully..",
							Toast.LENGTH_SHORT).show();
					FragmentManager fm = getActivity()
							.getSupportFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					Fragment fragment = new NewsFeedFragment();

					if (fragment != null) {
						ft.replace(R.id.frame_layout, fragment);
					} else {
						ft.add(R.id.frame_layout, fragment);
					}
					// ft.addToBackStack(null);
					// ft.commit();
					ft.commitAllowingStateLoss();

				} else {
					localHashMap.put("Status", "false");
					localHashMap.put("Message",
							localJSONObject.getString("Message"));

					Toast.makeText(
							getActivity(),
							"News not posted. Something went wrong.Please try again...",
							Toast.LENGTH_SHORT).show();

				}
			} catch (Exception e) {
				Log.e("Exception===", "" + e);
				showDialog("Something went wrong while processing your request.Please try again.");
			}
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		try {
		if (requestCode == 0) {

			Uri takenPhotoUri = getPhotoFileUri(photoFileName);
			// by this point we have the camera photo on disk
			takenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
			// Load the taken image into a preview
			// takenImage = Bitmap.createScaledBitmap(takenImage, 120, 120,
			// false);
			camera_img.setImageBitmap(takenImage);

			Log.e("takenPhotoUri.getPath()==", "" + takenPhotoUri.getPath());

			imgFileGallery = new File(takenPhotoUri.getPath());
			Log.e("imgFileGallery==", "" + imgFileGallery);
		}

		else if (requestCode == 1) {
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

			int columnIndex = SelectedCursor.getColumnIndex(FilePathColumn[0]);
			String picturePath = SelectedCursor.getString(columnIndex);
			SelectedCursor.close();

			Log.e("picturePath==", "" + picturePath);

			imgFileGallery = new File(picturePath);

		}
	} catch(Exception e){
		e.printStackTrace();
	}
	}
}
