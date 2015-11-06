package com.example.jsimmons;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.functions.Constants;
import com.example.functions.Functions;
import com.example.jsimmons.R;
import com.example.jsimmons.Register.RegistrationTask;
import com.example.utils.NetConnection;
import com.example.utils.TransparentProgressDialog;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class Login extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener {
	Button loginWithFacebook;
	Button loginWithGoogle;
	LinearLayout sign_in;
	TextView forgot_password;
	TextView register_here;
	EditText username;
	Button signINBbT;
	EditText password;
	TransparentProgressDialog db;
	SharedPreferences sp;
	Boolean isConnected;

	boolean isGoogleLogin = false;

	String APP_ID = "1443617225937981";

	// String APP_ID = "1610219072600732";
	private GoogleApiClient mGoogleApiClient;
	private static final int RC_SIGN_IN = 0;
	private boolean mSignInClicked;
	private boolean mIntentInProgress;
	private ConnectionResult mConnectionResult;

	// Instance of Facebook Class
	private Facebook facebook = new Facebook(APP_ID);
	private AsyncFacebookRunner mAsyncRunner;
	String FILENAME = "AndroidSSO_data";
	private SharedPreferences mPrefs;

	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	protected void showDialog(String msg) {
		final Dialog dialog;
		dialog = new Dialog(Login.this);
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

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		isConnected = NetConnection
				.checkInternetConnectionn(getApplicationContext());

		initUI();
		mAsyncRunner = new AsyncFacebookRunner(facebook);
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();

	}

	private void initUI() {
		loginWithFacebook = (Button) findViewById(R.id.loginWithFacebook);
		loginWithGoogle = (Button) findViewById(R.id.loginWithGoogle);
		forgot_password = (TextView) findViewById(R.id.forgot_password);
		register_here = (TextView) findViewById(R.id.register_here);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		sign_in = (LinearLayout) findViewById(R.id.sign_in);
		signINBbT = (Button) findViewById(R.id.signINBbT);

		sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		SpannableString content1 = new SpannableString("Register Here");
		content1.setSpan(new UnderlineSpan(), 0, content1.length(), 0);
		register_here.setText(content1);

		register_here.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// register_here.setTextColor(Color.parseColor("#213e9a"));
				Intent i = new Intent(Login.this, Register.class);
				startActivity(i);

			}
		});

		loginWithFacebook.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				loginToFacebook();
			}
		});

		loginWithGoogle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				loginToGogglePlus();
			}
		});

		forgot_password.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// forgot_password.setTextColor(Color.parseColor("#213e9a"));
			}
		});

		sign_in.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				validate();

			}
		});

		signINBbT.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				validate();

			}
		});

	}

	protected void validate() {
		String username_text = username.getText().toString();
		String password_text = password.getText().toString();

		if (username_text.trim().length() < 1) {
			username.setError("Please enter username.");
		} else if (password_text.trim().length() < 1) {
			password.setError("Please enter password.");
		} else {
			if (isConnected) {
				new LoginTask(username_text, password_text)
						.execute(new Void[0]);
			} else {
				showDialog(Constants.No_INTERNET);
			}
		}
	}

	public class LoginTask extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		HashMap result = new HashMap();

		ArrayList localArrayList = new ArrayList();

		String username, pass, email, type;

		public LoginTask(String username_text, String password_text) {
			this.username = username_text;
			this.pass = password_text;
		}

		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList.add(new BasicNameValuePair("username",
						this.username));
				localArrayList
						.add(new BasicNameValuePair("password", this.pass));
				localArrayList.add(new BasicNameValuePair("login_type",
						"Mannual"));
				localArrayList.add(new BasicNameValuePair("facebook_id", ""));
				localArrayList.add(new BasicNameValuePair("token_id",
						Constants.REGISTRATIO_ID));

				result = function.login(localArrayList);

			} catch (Exception localException) {

			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.get("Status").equals("true")) {
					Constants.USER_ID = (String) result.get("user_id");
					Constants.AUTH_KEY = (String) result.get("auth_key");
					Constants.USERNAME = (String) result.get("username");
					Constants.PROFILE_PIC_URL = (String) result.get("image");

					Editor e = sp.edit();
					e.putString("auth_key", Constants.AUTH_KEY);
					e.putString("user_id", Constants.USER_ID);
					e.putString("username", Constants.USERNAME);
					e.putString("image", Constants.PROFILE_PIC_URL);
					e.commit();

					Intent i = new Intent(Login.this, Home.class);
					startActivity(i);
					finish();

				} else if (result.get("Status").equals("false")) {

					showDialog((String) result.get("Message"));

				}
			}

			catch (Exception ae) {
				showDialog(Constants.ERROR_MSG);
			}

		}

		protected void onPreExecute() {
			super.onPreExecute();
			db = new TransparentProgressDialog(Login.this,
					R.drawable.loadingicon);
			db.show();
		}

	}

	public class LoginTaskFBGoogle extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		HashMap result = new HashMap();

		ArrayList localArrayList = new ArrayList();

		String username, type, email, id;

		public LoginTaskFBGoogle(String name, String email2, String type,
				String id) {
			this.username = name;
			this.email = email2;
			this.type = type;
			this.id = id;
		}

		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList.add(new BasicNameValuePair("username",
						this.username));
				localArrayList.add(new BasicNameValuePair("password", ""));
				localArrayList.add(new BasicNameValuePair("login_type",
						this.type));
				localArrayList.add(new BasicNameValuePair("facebook_id",
						this.id));
				localArrayList.add(new BasicNameValuePair("token_id",
						Constants.REGISTRATIO_ID));

				result = function.login(localArrayList);

			} catch (Exception localException) {

			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.get("Status").equals("true")) {

					Constants.USER_ID = (String) result.get("user_id");
					Constants.AUTH_KEY = (String) result.get("auth_key");
					Constants.USERNAME = (String) result.get("username");

					Constants.PROFILE_PIC_URL = (String) result.get("image");

					Editor e = sp.edit();
					e.putString("auth_key", Constants.AUTH_KEY);
					e.putString("user_id", Constants.USER_ID);
					e.putString("username", Constants.USERNAME);
					e.putString("image", Constants.PROFILE_PIC_URL);
					e.commit();

					isGoogleLogin = false;

					Intent i = new Intent(Login.this, Home.class);
					startActivity(i);
					finish();

				} else if (result.get("Status").equals("false")) {

					new RegisterTaskFBGoogle(this.username, this.email,
							this.type, this.id).execute(new Void[0]);
				}
			}

			catch (Exception ae) {
				showDialog(Constants.ERROR_MSG);
			}

		}

		protected void onPreExecute() {
			super.onPreExecute();
			db = new TransparentProgressDialog(Login.this,
					R.drawable.loadingicon);
			db.show();
		}

	}

	public class RegisterTaskFBGoogle extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		HashMap result = new HashMap();

		ArrayList localArrayList = new ArrayList();

		String username, email, type, id;

		public RegisterTaskFBGoogle(String username_text, String email_text,
				String string, String id) {
			this.username = username_text;
			this.email = email_text;
			this.type = string;
			this.id = id;
		}

		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList.add(new BasicNameValuePair("username",
						this.username));
				localArrayList.add(new BasicNameValuePair("email", this.email));
				localArrayList.add(new BasicNameValuePair("password", ""));
				localArrayList.add(new BasicNameValuePair("facebook_id",
						this.id));
				localArrayList.add(new BasicNameValuePair("login_type",
						this.type));
				localArrayList.add(new BasicNameValuePair("token_id",
						Constants.REGISTRATIO_ID));

				result = function.register(localArrayList);

			} catch (Exception localException) {

			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.get("Status").equals("true")) {

					Constants.USER_ID = (String) result.get("user_id");
					Constants.AUTH_KEY = (String) result.get("auth_key");
					Constants.USERNAME = (String) result.get("username");
					Constants.PROFILE_PIC_URL = (String) result.get("image");

					Editor e = sp.edit();
					e.putString("auth_key", Constants.AUTH_KEY);
					e.putString("user_id", Constants.USER_ID);
					e.putString("username", Constants.USERNAME);
					e.putString("image", Constants.PROFILE_PIC_URL);
					e.commit();

					Constants.LOGIN_TYPE = this.type;

					Intent i = new Intent(Login.this, PicAddORgRadius.class);
					i.putExtra("fromFBorGoogle", this.type);
					startActivity(i);
					finish();

					/*
					 * Intent i = new Intent(Login.this , Home.class);
					 * startActivity(i); finish();
					 */

				} else if (result.get("Status").equals("false")) {
					showDialog((String) result.get("Message"));
				}
			}

			catch (Exception ae) {
				showDialog(Constants.ERROR_MSG);
			}

		}

		protected void onPreExecute() {
			super.onPreExecute();
			db = new TransparentProgressDialog(Login.this,
					R.drawable.loadingicon);
			db.show();
		}

	}

	// ============================= login to facebook
	// =======================================//
	protected void loginToFacebook() {
		mPrefs = getPreferences(MODE_PRIVATE);
		String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);

		// String access_token =
		// "CAAW4fFfVOpwBAIEXo7ozNQgrAhOjaZBpBbwNseTPHdMhdwDRpkxx1vZAiyskv8LbZBZCKXPWWarZAXIawAndAuZA1qZBptDZCK6a35ylKCkYn0GmgFcx2KkRSQl1ap0bb3WTwzmkXjo4GsazFnRhijcZC30cLoGIJeQ1dFXOpjkWvkHpKZAODCeQzmJDwqe64wl2okqAc7NV9nDrTCO4NRon5zPvbrkTJWMZAsZD";
		// long expires = 1438930800;

		if (access_token != null) {
			facebook.setAccessToken(access_token);

			Log.d("FB Sessions", "" + facebook.isSessionValid());
			getProfileInformation();
		}

		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}

		if (!facebook.isSessionValid()) {
			facebook.authorize(this, new String[] { "read_stream",
					"user_photos", "email", "user_location", "public_profile",
					"basic_info", "publish_actions" }, new DialogListener() {

				@Override
				public void onCancel() {
					// Function to handle cancel event
				}

				@Override
				public void onComplete(Bundle values) {
					// Function to handle complete event
					// Edit Preferences and update facebook acess_token
					SharedPreferences.Editor editor = mPrefs.edit();
					editor.putString("access_token", facebook.getAccessToken());
					editor.putLong("access_expires",
							facebook.getAccessExpires());
					editor.commit();
					getProfileInformation();

				}

				@Override
				public void onError(DialogError error) {
					// Function to handle error

				}

				@Override
				public void onFacebookError(FacebookError fberror) {
					// Function to handle Facebook errors

				}

			});
		}
	}

	/**
	 * to get facebook profile info
	 */

	public void getProfileInformation() {
		mAsyncRunner.request("me", new RequestListener() {
			@Override
			public void onComplete(String response, Object state) {
				Log.d("Profile", response);
				String json = response;
				try {
					// Facebook Profile JSON data
					JSONObject profile = new JSONObject(json);

					Log.d("profile response", profile + "");
					// getting name of the user
					final String name = profile.getString("name");
					final String id = profile.getString("id");

					// getting email of the user
					// final String email = profile.getString("email");

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Log.e("name==", "" + name);
							Editor e = sp.edit();
							e.putString("name", name);
							e.putString("email", name + ".facebook.com");

							String trimNAME = name.replace(" ", "");
							String email = trimNAME + ".facebook.com";

							e.commit();
							Toast.makeText(getApplicationContext(),
									"Login successfull.", Toast.LENGTH_LONG)
									.show();

							String profile_pic = "https://graph.facebook.com/"
									+ id + "/picture";

							Constants.CLIENT_IMAGE = profile_pic;

							// e.putString("image", profile_pic);

							new LoginTaskFBGoogle(name, email, "Facebook", id)
									.execute(new Void[0]);
						}

					});

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
			}

			@Override
			public void onIOException(IOException e, Object state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
				// TODO Auto-generated method stub

			}
		});
	}

	protected void loginToGogglePlus() {
		isGoogleLogin = true;
		if (!mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			resolveSignInError();
		}
	}

	/**
	 * Method to resolve any signin errors
	 * */
	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {

				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (SendIntentException e) {

				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RC_SIGN_IN) {
			if (resultCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		} else {
			facebook.authorizeCallback(requestCode, resultCode, data);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {

			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}

		if (!mIntentInProgress) {
			// Store the ConnectionResult for later usage
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}

	}

	@Override
	public void onConnected(Bundle arg0) {
		mSignInClicked = false;
		Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

		// Get user's information

		if (isGoogleLogin) {
			getGoogleProfileInformation();
		}

	}

	private void getGoogleProfileInformation() {
		try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi
						.getCurrentPerson(mGoogleApiClient);
				String personName = currentPerson.getDisplayName();
				String personPhotoUrl = currentPerson.getImage().getUrl();
				String personGooglePlusProfile = currentPerson.getUrl();
				String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
				String id = currentPerson.getId();

				Log.e("g+ info====", "Name: " + personName + ", plusProfile: "
						+ personGooglePlusProfile + ", email: " + email
						+ ", Image: " + personPhotoUrl);

				Constants.CLIENT_IMAGE = personPhotoUrl;

				Editor e = sp.edit();
				e.putString("name", personName);
				e.putString("email", email);
				e.commit();

				Toast.makeText(getApplicationContext(), "Login successfull.",
						Toast.LENGTH_LONG).show();

				new LoginTaskFBGoogle(personName, email, "Google", id)
						.execute(new Void[0]);

			} else {
				Toast.makeText(getApplicationContext(),
						"Person information is null", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();

	}
}
