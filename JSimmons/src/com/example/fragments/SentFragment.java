package com.example.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import com.example.functions.Constants;
import com.example.functions.Functions;
import com.example.jsimmons.R;
import com.example.utils.NetConnection;
import com.example.utils.TransparentProgressDialog;

import android.app.Dialog;

import android.content.ComponentName;

import android.content.Intent;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class SentFragment extends Fragment {

	private View rootView;
	TextView title;
	ImageButton back;
	ImageButton fb, twitter, post, google;
	Boolean isConnected;

	TransparentProgressDialog db;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.sent_fragment, container, false);
		back = (ImageButton) rootView.findViewById(R.id.back);
		title = (TextView) rootView.findViewById(R.id.title);
		fb = (ImageButton) rootView.findViewById(R.id.fb);
		twitter = (ImageButton) rootView.findViewById(R.id.twitter);
		post = (ImageButton) rootView.findViewById(R.id.post);
		google = (ImageButton) rootView.findViewById(R.id.google);

		title.setText(Constants.ANNOUNCEMENT_TITLE);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goBACK();
			}
		});

		post.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ShareAPI();
				MessageSharing();
			}
		});

		fb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ShareAPI();
				fbSharing();

			}
		});

		twitter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ShareAPI();
				twitterSharing();

			}
		});

		google.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ShareAPI();
				googleSharing();
			}
		});

		return rootView;
	}

	protected void ShareAPI() {
		isConnected = NetConnection.checkInternetConnectionn(getActivity());
		if (isConnected) {
			new ShareAPI(Constants.USER_ID, Constants.AUTH_KEY,
					Constants.ORG_ID, Constants.ANNOUNCEMENT_ID)
					.execute(new Void[0]);
		} 
	}

	protected void MessageSharing() {
		try {
			Intent sendIntent = new Intent(Intent.ACTION_VIEW);
			sendIntent.putExtra("sms_body",
					"#DivineDistrict - Check out his amazing app!!!");
			sendIntent.setType("vnd.android-dir/mms-sms");
			startActivity(sendIntent);

		} catch (Exception ex) {
			Toast.makeText(getActivity(), ex.getMessage().toString(),
					Toast.LENGTH_LONG).show();
			ex.printStackTrace();
		}
	}

	protected void googleSharing() {
		try {
			List<Intent> targetedShareIntents = new ArrayList<Intent>();
			Intent share = new Intent(android.content.Intent.ACTION_SEND);
			share.setType("text/plain");
			List<ResolveInfo> resInfo = getActivity().getPackageManager()
					.queryIntentActivities(share, 0);
			if (!resInfo.isEmpty()) {
				for (ResolveInfo info : resInfo) {
					Intent targetedShare = new Intent(
							android.content.Intent.ACTION_SEND);
					targetedShare.setType("text/plain"); // put here
															// your mime
															// type
					if (info.activityInfo.packageName.toLowerCase().contains(
							"plus")
							|| info.activityInfo.name.toLowerCase().contains(
									"plus")) {
						System.out.println("inside the if conditionsssss");
						targetedShare.putExtra(Intent.EXTRA_SUBJECT,
								"#DivineDistrict");
						targetedShare.putExtra(Intent.EXTRA_TEXT,
								"Check Out this amazing app...");
						targetedShare.setPackage(info.activityInfo.packageName);
						targetedShareIntents.add(targetedShare);
					}
				}
				Intent chooserIntent = Intent.createChooser(
						targetedShareIntents.remove(0), "Select app to share");
				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
						targetedShareIntents.toArray(new Parcelable[] {}));
				startActivity(chooserIntent);
			}
		} catch (Exception e) {
			Log.v("VM",
					"Exception while sending image on" + "plus" + " "
							+ e.getMessage());
		}
	}

	protected void twitterSharing() {
		try {

			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_TEXT, "It's a Tweet!"
					+ "#DivineDistrict");
			intent.setType("text/plain");
			final PackageManager pm = getActivity().getPackageManager();
			final List<?> activityList = pm.queryIntentActivities(intent, 0);
			int len = activityList.size();
			for (int i = 0; i < len; i++) {

				final ResolveInfo app = (ResolveInfo) activityList.get(i);

				if ((app.activityInfo.name.contains("twitter"))) {
					Log.i("twitter==<>", "" + app.activityInfo.name);

					final ActivityInfo activity = app.activityInfo;
					final ComponentName x = new ComponentName(
							activity.applicationInfo.packageName, activity.name);

					intent = new Intent(Intent.ACTION_SEND);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					intent.setComponent(x);

					intent.putExtra(Intent.EXTRA_TEXT,
							"It's a tweet #DivineDistrict");
					intent.setType("application/twitter");
					startActivity(intent);

					break;

				} else {
					if (i + 1 == len) {
						String link = "https://play.google.com/store/apps/details?id=com.twitter.android&hl=en";
						Log.e("else else", "else else");
						showDailog(link);
						break;
					}

				}
			}
		} catch (Exception ae) {
			ae.printStackTrace();
		}
	}

	protected void fbSharing() {
		try {

			String urlToShare = "http://www.google.com";
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");

			intent.putExtra(Intent.EXTRA_TEXT, urlToShare);
			intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
			final PackageManager pm = getActivity().getPackageManager();
			final List<?> activityList = pm.queryIntentActivities(intent, 0);
			int len = activityList.size();
			for (int i = 0; i < len; i++) {

				final ResolveInfo app = (ResolveInfo) activityList.get(i);
				Log.i("<>==<>", "" + app.activityInfo.packageName);
				if (app.activityInfo.packageName.toLowerCase().startsWith(
						"com.facebook.katana")) {
					String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u="
							+ urlToShare;
					intent = new Intent(Intent.ACTION_VIEW,
							Uri.parse(sharerUrl));
					startActivity(intent);

					break;

				} else {
					if (i + 1 == len) {
						String link = "https://play.google.com/store/apps/details?id=com.twitter.android&hl=en";
						Log.e("else else", "else else");
						showDailog(link);
						break;
					}

				}
			}
		} catch (Exception ae) {
			ae.printStackTrace();
		}

	}

	protected void showDailog(final String link) {
		try {
			final Dialog dialog;
			dialog = new Dialog(getActivity());
			dialog.setCancelable(false);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.getWindow().setFormat(PixelFormat.TRANSLUCENT);

			Drawable d = new ColorDrawable(Color.BLACK);
			d.setAlpha(0);
			dialog.getWindow().setBackgroundDrawable(d);

			Button yes, no;
			TextView msg;

			dialog.setContentView(R.layout.logout);
			yes = (Button) dialog.findViewById(R.id.yes);
			no = (Button) dialog.findViewById(R.id.no);
			msg = (TextView) dialog.findViewById(R.id.msg);

			msg.setText("Oops... it seems like your selection is not installed on your device. Would you like to install the app on your device?");

			yes.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					Intent i = new Intent(android.content.Intent.ACTION_VIEW);
					i.setData(Uri.parse(link));
					startActivity(i);
				}
			});

			no.setOnClickListener(new View.OnClickListener() {

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

	protected void goBACK() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment fragment = new JoinAnnouncement();

		if (fragment != null) {
			ft.replace(R.id.frame_layout, fragment);
		} else {
			ft.add(R.id.frame_layout, fragment);
		}
		// ft.addToBackStack(null);
		// ft.commit();
		ft.commitAllowingStateLoss();
	}

	// **************** share API **********************//

	public class ShareAPI extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		HashMap result = new HashMap();

		ArrayList localArrayList = new ArrayList();
		String userID, authKEY, OrgID, AnnID;

		public ShareAPI(String uSER_ID, String aUTH_KEY, String oRG_ID,
				String aNNOUNCEMENT_ID) {
			this.userID = uSER_ID;
			this.authKEY = aUTH_KEY;
			this.OrgID = oRG_ID;
			this.AnnID = aNNOUNCEMENT_ID;
		}

		protected Void doInBackground(Void... paramVarArgs) {
			
			/*http://phphosting.osvin.net/divineDistrict/api/orgShares.php?
				user_id=31&authKey=divineDistrict@31&announcement_id=30&org_id=6*/
				
			try {
				localArrayList.add(new BasicNameValuePair("user_id", this.userID));
				localArrayList.add(new BasicNameValuePair("authKey", this.authKEY));
				localArrayList.add(new BasicNameValuePair("announcement_id",this.AnnID ));
				localArrayList.add(new BasicNameValuePair("org_id", this.OrgID));
				
				result = function.Share(localArrayList);

			}
			catch (Exception localException) {

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
