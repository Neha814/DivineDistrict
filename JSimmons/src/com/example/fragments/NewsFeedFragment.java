package com.example.fragments;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.functions.Constants;
import com.example.functions.Functions;
import com.example.jsimmons.R;
import com.example.utils.NetConnection;
import com.example.utils.TransparentProgressDialog;
import com.macrew.imageloader.ImageLoader;
import com.macrew.imageloader.ImageLoaderPic;

public class NewsFeedFragment extends Fragment {

	private View rootView;
	ListView listview;
	Boolean isConnected;
	MyAdapter mAdapter;
	public ImageLoader imageLoader;
	public ImageLoaderPic imageLoaderpic;
	TransparentProgressDialog db;
	ArrayList<HashMap<String, String>> newsFeedList = new ArrayList<HashMap<String, String>>();
	ArrayList<Integer> likeslist = new ArrayList<Integer>();

	Uri uri2;
	ImageButton post_news, notification, setting ,location;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater
				.inflate(R.layout.newsfeedfragment, container, false);
		init();

		return rootView;
	}

	private void init() {
		listview = (ListView) rootView.findViewById(R.id.listview);
		post_news = (ImageButton) rootView.findViewById(R.id.post_news);
		notification = (ImageButton) rootView.findViewById(R.id.notification);
		setting = (ImageButton) rootView.findViewById(R.id.setting);
		location = (ImageButton) rootView.findViewById(R.id.location);

		isConnected = NetConnection.checkInternetConnectionn(getActivity());
		if (isConnected) {
			new getNewsFeedListTask().execute(new Void[0]);
		} else {
			showDialog(Constants.No_INTERNET);
		}

		post_news.setOnClickListener(new OnClickListener() {

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
				ft.addToBackStack(null);
				// ft.commit();
				ft.commitAllowingStateLoss();
			}
		});

		setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				Fragment fragment = new Settings();

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
		
		
		location.setOnClickListener(new OnClickListener() {
			
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
				ft.addToBackStack(null);
				// ft.commit();
				ft.commitAllowingStateLoss();
			}
		});
	}

	// **************** newsFeddList **************************//

	public class getNewsFeedListTask extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		ArrayList result;

		ArrayList localArrayList = new ArrayList();

		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList.add(new BasicNameValuePair("user_id",
						Constants.USER_ID));
				localArrayList.add(new BasicNameValuePair("authKey",
						Constants.AUTH_KEY));
				result = function.newsFeedlist(localArrayList);

				Log.e("result item lit==", "" + result);

			} catch (Exception localException) {
				localException.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {
				if (result.size() > 0) {
					newsFeedList.clear();
					likeslist.clear();
					newsFeedList.addAll(result);
					mAdapter = new MyAdapter(newsFeedList, getActivity());
					listview.setAdapter(mAdapter);
				} else {
					showDialog("No NewsFeed List found.");
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
			imageLoader = new ImageLoader(getActivity());
			imageLoaderpic = new ImageLoaderPic(getActivity());
		}

		@Override
		public int getCount() {

			return newsFeedList.size();
		}

		@Override
		public Object getItem(int position) {

			return newsFeedList.get(position);
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
				convertView = mInflater.inflate(R.layout.news_feed_listitem,
						null);
				holder.username = (TextView) convertView
						.findViewById(R.id.username);
				holder.description = (TextView) convertView
						.findViewById(R.id.description);
				holder.comments = (TextView) convertView
						.findViewById(R.id.comments);
				holder.like = (TextView) convertView.findViewById(R.id.like);
				holder.share = (ImageView) convertView.findViewById(R.id.share);
				holder.user_image = (ImageView) convertView
						.findViewById(R.id.user_image);
				holder.news_image = (ImageView) convertView
						.findViewById(R.id.news_image);
	
				holder.org_name = (TextView) convertView.findViewById(R.id.org_name);
				

				// holder.news_image.setVisibility(View.GONE);

				convertView.setTag(holder);

			}

			else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.share.setTag(position);
			holder.like.setTag(position);
			holder.comments.setTag(position);
			holder.news_image.setTag(position);

			holder.username
					.setText(newsFeedList.get(position).get("user_name"));
			holder.description.setText(newsFeedList.get(position).get(
					"description"));
			holder.comments.setText(newsFeedList.get(position).get(
					"comment_count")
					+ " comments");
			
			
			Log.e("org name=====>>>",""+newsFeedList.get(position).get("org_name"));
			
			try {
			holder.org_name.setText(newsFeedList.get(position).get("org_name"));
			} catch(Exception e){
				Log.e("org eception ===>","org exception====>");
				e.printStackTrace();
			}

			String userIMAGE = newsFeedList.get(position).get("user_image");
			// if(userIMAGE.contains("jpg") || userIMAGE.contains("png")){
			imageLoader.DisplayImage(" http://" + userIMAGE,
					R.drawable.profile_pic, holder.user_image);
			// } else {

			// imageLoader.DisplayImage("https://graph.facebook.com/1851007448456759/picture",R.drawable.profile_pic,
			// holder.user_image);
			// holder.user_image.setImageResource(R.drawable.profile_pic);
			// }

			String imageNAME = newsFeedList.get(position).get("image");
			if (imageNAME.contains(".jpg") || imageNAME.contains(".png")) {
				holder.news_image.setVisibility(View.VISIBLE);
			
				
				imageLoaderpic.DisplayImage(" http://"
						+ newsFeedList.get(position).get("image"),
						R.drawable.main_bg, holder.news_image);
			} else {
				holder.news_image.setVisibility(View.GONE);
			}

			if (likeslist.size() > 0) {

				for (int i = 0; i < newsFeedList.size(); i++) {
					if (likeslist.contains(position)) {
						holder.like.setCompoundDrawablesWithIntrinsicBounds(0,
								0, R.drawable.like_active, 0);

						int likes = Integer.parseInt((newsFeedList
								.get(position).get("favourite_count")));
						likes = likes + 1;
						holder.like.setText(likes + " likes");

					} else {
						if (newsFeedList.get(position).get("isLike")
								.equals("0")) {
							holder.like
									.setCompoundDrawablesWithIntrinsicBounds(0,
											0, R.drawable.like_inactive, 0);

							int likes = Integer.parseInt((newsFeedList
									.get(position).get("favourite_count")));

							holder.like.setText(likes + " likes");
						} else {
							holder.like
									.setCompoundDrawablesWithIntrinsicBounds(0,
											0, R.drawable.like_active, 0);

							int likes = Integer.parseInt((newsFeedList
									.get(position).get("favourite_count")));

							holder.like.setText(likes + " likes");
						}
					}
				}
			}

			else {
				holder.like.setText(newsFeedList.get(position).get(
						"favourite_count")
						+ " likes");
				if (newsFeedList.get(position).get("isLike").equals("1")) {
					holder.like.setCompoundDrawablesWithIntrinsicBounds(0, 0,
							R.drawable.like_active, 0);
				} else {
					holder.like.setCompoundDrawablesWithIntrinsicBounds(0, 0,
							R.drawable.like_inactive, 0);
				}
			}

			holder.news_image.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int pos = (Integer) v.getTag();
					Constants.IMAGE_LINK = " http://"
							+ newsFeedList.get(position).get("image");
					showImage(Constants.IMAGE_LINK);

				}
			});

			holder.comments.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int pos = (Integer) v.getTag();
					int likeCOUNT = Integer.parseInt(newsFeedList.get(position)
							.get("favourite_count"));
					String id = newsFeedList.get(position).get("id");

					Constants.LIKES = likeCOUNT;
					Constants.NEWS_ID = id;

					FragmentManager fm = getActivity()
							.getSupportFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					Fragment fragment = new CommentFragment();

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

			holder.like.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int pos = (Integer) v.getTag();
					/**
					 * LIKE
					 */
					if (newsFeedList.get(pos).get("isLike").equals("0")) {

						// HashMap<String, String> hashmap = new HashMap<String,
						// String>();
						/*
						 * int likeCOUNT = Integer.parseInt(newsFeedList.get(
						 * position).get("favourite_count")); likeCOUNT =
						 * likeCOUNT + 1; hashmap.put("pos",
						 * String.valueOf(pos)); hashmap.put("like",
						 * String.valueOf(likeCOUNT));
						 */

						// mAdapter.notifyDataSetChanged();

						String status = newsFeedList.get(pos).get("status");
						String user_id = Constants.USER_ID;
						String id = newsFeedList.get(pos).get("id");
						String eventType = "News";

						LikeAPI(user_id, id, Constants.AUTH_KEY, eventType,
								status, pos);
					}
					/**
					 * DISLIKE
					 */
					else {

					}

				}

			});
			holder.share.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int pos = (Integer) v.getTag();
					share(pos);
				}
			});

			return convertView;
		}

		protected void showImage(String msg) {
			try {
				final Dialog dialog;
				dialog = new Dialog(getActivity(), R.style.full_screen_dialog);
				dialog.setCancelable(true);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.getWindow().setFormat(PixelFormat.OPAQUE);

				Drawable d = new ColorDrawable(Color.BLACK);
				d.setAlpha(200);
				dialog.getWindow().setBackgroundDrawable(d);

				dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT);
				dialog.getWindow().setFormat(PixelFormat.TRANSLUCENT);
				dialog.getWindow().setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

				ImageLoader imageLoader1;
				ImageView image;

				dialog.setContentView(R.layout.newsfedd_detail);
				imageLoader1 = new ImageLoader(getActivity());
				image = (ImageView) dialog.findViewById(R.id.image);

				imageLoader.DisplayImage(Constants.IMAGE_LINK,
						Color.parseColor("#000000"), image);

				dialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		private void LikeAPI(String user_id, String id, String aUTH_KEY,
				String eventType, String status, int pos) {

			new Like(user_id, id, aUTH_KEY, eventType, status, pos)
					.execute(new Void[0]);
		}

		protected void share(final int pos) {
			
			Thread t =new Thread(){
				
				public void run() {

			try {
				
				//onStartprogress();

			String desc = newsFeedList.get(pos).get("description");
			String name = newsFeedList.get(pos).get("user_name");
			String image = "http://"+newsFeedList.get(pos).get("image");
			
			if(image.contains("png") || image.contains("jpg")) {
			URL url = new URL(image);
			URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
			url = uri.toURL();
			
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();

			connection.setDoInput(true);

			connection.connect();

			InputStream input = connection.getInputStream();

			Bitmap immutableBpm = BitmapFactory.decodeStream(input);

			Bitmap mutableBitmap = immutableBpm.copy(Bitmap.Config.ARGB_8888,
					true);

			View view = new View(getActivity());

			view.draw(new Canvas(mutableBitmap));

			String path = Images.Media.insertImage(getActivity().getContentResolver(),
					mutableBitmap, "Nur", null);

			 uri2 = Uri.parse(path);
			
			}

			Intent sharingIntent = new Intent(
					android.content.Intent.ACTION_SEND);
			sharingIntent.setType("text/plain");
			String shareBody = name + ": " + desc;
			sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"#DivineDistrict");
			sharingIntent
					.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
			
			if(image.contains("png") || image.contains("jpg")){
				Log.e("uri====>>",""+Uri.parse(image));
				
					sharingIntent.putExtra(Intent.EXTRA_STREAM, uri2);
					
					Log.e("image====>>",""+image);
					
					Log.e("**** if image *****","**** if image ****");
			sharingIntent.setType("image/png");
			}
			else {
				
				Log.e("image====>>",""+image);
				
				Log.e("**** if not image *****","**** if not image ****");
				sharingIntent.setType("text/plain");
			}
			startActivity(Intent.createChooser(sharingIntent, "Share via"));
			
			//onStopprogress();
			} catch(Exception ee){
				ee.printStackTrace();
			}
				}
			};t.start();
		
		}

		private void onStopprogress() {
			db.dismiss();
			
		}

		private void onStartprogress() {
			db = new TransparentProgressDialog(getActivity(),
					R.drawable.loadingicon);
			db.show();
		}

		class ViewHolder {
			TextView username, description, comments, like;
			ImageView share, user_image;
			ImageView news_image;
			TextView org_name;

		}
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

				}
			});
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public class Like extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		HashMap result = new HashMap();

		ArrayList localArrayList = new ArrayList();
		String userID, newsID, authKEY, eventTYPE, STATUS;
		int POS;

		public Like(String user_id, String id, String aUTH_KEY,
				String eventType, String status, int pos) {
			this.userID = user_id;
			this.newsID = id;
			this.authKEY = aUTH_KEY;
			this.eventTYPE = eventType;
			this.STATUS = status;
			this.POS = pos;
		}

		protected Void doInBackground(Void... paramVarArgs) {
			try {

				/*
				 * http://phphosting.osvin.net/divineDistrict/api/favourite.php?
				 * user_id
				 * =31&authKey=divineDistrict@31&event_id=1&event_type=News
				 * &status=1
				 */

				localArrayList.add(new BasicNameValuePair("user_id",
						this.userID));
				localArrayList.add(new BasicNameValuePair("authKey",
						this.authKEY));
				localArrayList.add(new BasicNameValuePair("event_id",
						this.newsID));
				localArrayList.add(new BasicNameValuePair("event_type",
						this.eventTYPE));
				localArrayList
						.add(new BasicNameValuePair("status", this.STATUS));

				result = function.Like(localArrayList);

			} catch (Exception localException) {
				localException.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			try {
				Log.e("LIKE result==", "" + result);
				if (result.get("Status").equals("true")) {

					/*
					 * Toast.makeText(getActivity(), "pos==" + POS,
					 * Toast.LENGTH_SHORT).show();
					 */
					likeslist.add(POS);
					mAdapter.notifyDataSetChanged();

				} else if (result.get("Status").equals("false")) {

				}

			}

			catch (Exception ae) {
				ae.printStackTrace();
				// showDialog(Constants.ERROR_MSG);
			}

		}

		protected void onPreExecute() {
			super.onPreExecute();
		}

	}
}
