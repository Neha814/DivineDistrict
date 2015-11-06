package com.example.jsimmons;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.example.fragments.NewsFeedFragment;
import com.example.fragments.AddOrganization.AddOrgAPI;
import com.example.fragments.PostFragment.updateProfileTask;

import com.example.functions.Constants;
import com.example.functions.Functions;
import com.example.utils.HttpClientUpload;
import com.example.utils.NetConnection;
import com.example.utils.TransparentProgressDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.preference.PreferenceManager;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PicAddORgRadius extends Activity {

	ImageView upload_img;
	EditText radius;
	ListView listivew;
	LinearLayout next;
	Button next_btn;
	MyAdapter mAdapter;
	boolean isConnected;
	Bitmap takenImage;
	File imgFileGallery;

	public static ContentResolver appContext;

	updateProfileTask updateProfileObj;

	SharedPreferences sp;

	String orgIDS = "";
	TransparentProgressDialog db;

	ArrayList<HashMap<String, String>> organizationList = new ArrayList<HashMap<String, String>>();

	ArrayList<Integer> AddedList = new ArrayList<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.one_time_screen);

		imgFileGallery = new File("");

		appContext = getContentResolver();

		sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		Constants.AUTH_KEY = sp.getString("auth_key", "");
		Constants.USER_ID = sp.getString("user_id", "");
		Constants.USERNAME = sp.getString("username", "");

		upload_img = (ImageView) findViewById(R.id.upload_img);
		radius = (EditText) findViewById(R.id.radius);
		listivew = (ListView) findViewById(R.id.listivew);
		next = (LinearLayout) findViewById(R.id.next);
		next_btn = (Button) findViewById(R.id.next_btn);

		isConnected = NetConnection
				.checkInternetConnectionn(PicAddORgRadius.this);

		if (isConnected) {

			new OrganizationListAPI(Constants.USER_ID, Constants.AUTH_KEY)
					.execute(new Void[0]);
		} else {
			showDialog(Constants.No_INTERNET);
		}

		next_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				update();

			}
		});

		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				update();
			}
		});

		upload_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent GaleryIntent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(GaleryIntent, 1);
			}
		});
	}

	protected void update() {
		if (isConnected) {

			String radius_text = radius.getText().toString();

			// Log.e("picsssssss============",""+imgFileGallery);
			updateProfileObj = new updateProfileTask(Constants.USER_ID,
					Constants.AUTH_KEY, orgIDS, radius_text);
			updateProfileObj.execute();

		} else {
			showDialog(Constants.No_INTERNET);
		}
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

					mAdapter = new MyAdapter(organizationList,
							PicAddORgRadius.this);
					listivew.setAdapter(mAdapter);

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
			db = new TransparentProgressDialog(PicAddORgRadius.this,
					R.drawable.loadingicon);
			db.show();
		}

	}

	class MyAdapter extends BaseAdapter {

		LayoutInflater mInflater = null;

		public MyAdapter(ArrayList<HashMap<String, String>> list,
				Activity activity) {
			mInflater = LayoutInflater.from(PicAddORgRadius.this);

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

			holder.add.setTag(position);

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

					AddedList.add(pos);
					orgIDS = orgIDS + "," + organizationList.get(pos).get("id");

					mAdapter.notifyDataSetChanged();

				}
			});

			return convertView;
		}

		class ViewHolder {
			TextView org_name;
			Button add;
		}
	}

	protected void showDialog(String msg) {
		try {
			final Dialog dialog;
			dialog = new Dialog(PicAddORgRadius.this);
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		try {

			if (requestCode == 1) {
				Uri selectedImage = data.getData();
				InputStream imageStream = null;
				try {
					imageStream = appContext.openInputStream(selectedImage);
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

				Cursor SelectedCursor = appContext.query(SelectedImage,
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

		String userID, authKEY, orgID, RADIUS;

		public updateProfileTask(String uSER_ID, String aUTH_KEY,
				String orgIDS, String radius_text) {
			this.userID = uSER_ID;
			this.authKEY = aUTH_KEY;
			this.orgID = orgIDS;
			this.RADIUS = radius_text;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			db = new TransparentProgressDialog(PicAddORgRadius.this,
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
						"http://phphosting.osvin.net/divineDistrict/api/userOptions.php?");
				client.connectForMultipart();

				client.addFormPart("user_id", this.userID);
				client.addFormPart("authKey", this.authKEY);
				client.addFormPart("radius", this.RADIUS);
				client.addFormPart("organisations", this.orgID);
				client.addFormPart("login_type", Constants.LOGIN_TYPE);

				if (!(imgFileGallery.getName().equals("") || imgFileGallery
						.getName() == null)) {

					Log.e("if ifi ifi fifif", "if if ifi ifi if");
					Log.e("imgFileGallery", "===" + imgFileGallery);

					client.addFilePart("image", imgFileGallery.getName(),
							baos.toByteArray());
				}

				else {
					Log.e("else else else", "else else else");
					Log.e("client img", "===" + Constants.CLIENT_IMAGE);

					Log.e("img=====", "" + Constants.CLIENT_IMAGE);
					client.addFormPart("image", Constants.CLIENT_IMAGE);
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

					String image = localJSONObject.getString("image");

					Constants.PROFILE_PIC_URL = image;

					Editor e = sp.edit();

					e.putString("image", Constants.PROFILE_PIC_URL);
					e.commit();

					Intent i = new Intent(PicAddORgRadius.this, Home.class);
					startActivity(i);

				} else {
					/*
					 * Intent i = new Intent(PicAddORgRadius.this , Home.class);
					 * startActivity(i);
					 */

					Toast.makeText(PicAddORgRadius.this, "Error occured...",
							Toast.LENGTH_SHORT).show();

				}
			} catch (Exception e) {
				Log.e("Exception===", "" + e);
				showDialog("Something went wrong while processing your request.Please try again.");
			}
		}

	}
}
