package com.example.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;

import com.example.fragments.FindAnnouncementCategoryFragment.MyAdapter;
import com.example.fragments.FindAnnouncementCategoryFragment.MyAdapter.ViewHolder;
import com.example.functions.Constants;
import com.example.functions.Functions;
import com.example.jsimmons.R;
import com.example.utils.NetConnection;
import com.example.utils.TransparentProgressDialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CommentFragment extends Fragment {

	View rootView;
	TextView no_of_likes;
	Boolean isConnected;
	ListView comments_listview;
	TransparentProgressDialog db;
	MyAdapter mAdapter;
	EditText message;
	Button send;

	ArrayList<HashMap<String, String>> commentList = new ArrayList<HashMap<String, String>>();

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

		rootView = inflater
				.inflate(R.layout.comment_fragment, container, false);
		no_of_likes = (TextView) rootView.findViewById(R.id.no_of_likes);
		comments_listview = (ListView) rootView
				.findViewById(R.id.comments_listview);
		message = (EditText) rootView.findViewById(R.id.message);
		// message.getText().append("\ud83d\ude01");
		send = (Button) rootView.findViewById(R.id.send);

		no_of_likes.setText(Constants.LIKES + " people");

		isConnected = NetConnection.checkInternetConnectionn(getActivity());
		if (isConnected) {

			/*
			 * http://phphosting.osvin.net/divineDistrict/api/newsfeedDetail.php?
			 * user_id=31&authKey=divineDistrict@31&news_id=1
			 */

			new GetComments(Constants.USER_ID, Constants.AUTH_KEY,
					Constants.NEWS_ID).execute(new Void[0]);
		} else {
			showDialog(Constants.No_INTERNET);
		}

		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String messageToSend = message.getText().toString();
				if (messageToSend.trim().length() > 0) {
					SendMessage(messageToSend);
				} else {
					message.setError("Enter message to send.");
				}
			}
		});

		return rootView;

	}

	protected void SendMessage(String msg) {

		/**
		 * update Listview with your comment
		 */

		message.setText("");

		HashMap<String, String> newComment = new HashMap<String, String>();
		newComment.put("comment", msg);
		newComment.put("user_id", Constants.USER_ID);
		newComment.put("username", Constants.USERNAME);
		newComment.put("user_image", "");

		if (commentList.size() > 0) {
			commentList.add(newComment);
			mAdapter.notifyDataSetChanged();
		} else {
			commentList.add(newComment);
			mAdapter = new MyAdapter(commentList, getActivity());
			comments_listview.setAdapter(mAdapter);
		}

		scrollCommentListToBottom();

		if (isConnected) {

			String enventTYPE = "News";
			String messg = msg;
			new SendComment(Constants.USER_ID, Constants.AUTH_KEY,
					Constants.NEWS_ID, enventTYPE, messg).execute(new Void[0]);
		} else {
			showDialog(Constants.No_INTERNET);
		}
	}

	private void scrollCommentListToBottom() {
		comments_listview.setSelection(mAdapter.getCount() - 1);

	}

	public class GetComments extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();

		ArrayList localArrayList = new ArrayList();
		String userID, authKEY, newsID, AnnID;

		public GetComments(String uSER_ID, String aUTH_KEY, String nEWS_ID) {
			userID = uSER_ID;
			authKEY = aUTH_KEY;
			newsID = nEWS_ID;
		}

		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList.add(new BasicNameValuePair("user_id",
						this.userID));
				localArrayList.add(new BasicNameValuePair("authKey",
						this.authKEY));
				localArrayList.add(new BasicNameValuePair("news_id",
						this.newsID));

				result = function.GetComments(localArrayList);

			} catch (Exception localException) {

			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			db.dismiss();

			try {

				if (result.size() > 0) {
					commentList.clear();
					commentList.addAll(result);
					mAdapter = new MyAdapter(commentList, getActivity());
					comments_listview.setAdapter(mAdapter);
				} else {

					// showDialog("No Comment yet!");

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

	public class SendComment extends AsyncTask<Void, Void, Void> {
		Functions function = new Functions();

		HashMap<String, String> result = new HashMap<String, String>();

		ArrayList localArrayList = new ArrayList();
		String userID, authKEY, newsID, eventTYPE, mEssage;

		public SendComment(String uSER_ID, String aUTH_KEY, String nEWS_ID,
				String enventTYPE, String messg) {
			this.userID = uSER_ID;
			this.authKEY = aUTH_KEY;
			this.newsID = nEWS_ID;
			this.eventTYPE = enventTYPE;
			this.mEssage = messg;
		}

		protected Void doInBackground(Void... paramVarArgs) {
			try {
				localArrayList.add(new BasicNameValuePair("user_id",
						this.userID));
				localArrayList.add(new BasicNameValuePair("authKey",
						this.authKEY));
				localArrayList.add(new BasicNameValuePair("event_id",
						this.newsID));
				localArrayList.add(new BasicNameValuePair("event_type",
						this.eventTYPE));
				localArrayList.add(new BasicNameValuePair("comment",
						this.mEssage));

				result = function.SendComment(localArrayList);

			} catch (Exception localException) {

			}

			return null;
		}

		protected void onPostExecute(Void paramVoid) {
			// db.dismiss();

			try {
				try {
					Log.e("comment result==", "" + result);
					if (result.get("Status").equals("true")) {

					} else if (result.get("Status").equals("false")) {

					}

				}

				catch (Exception ae) {
					ae.printStackTrace();
					// showDialog(Constants.ERROR_MSG);
				}
			}

			catch (Exception ae) {
				showDialog(Constants.ERROR_MSG);
			}

		}

		protected void onPreExecute() {
			super.onPreExecute();
			/*
			 * db = new TransparentProgressDialog(getActivity(),
			 * R.drawable.loadingicon); db.show();
			 */
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

			return commentList.size();
		}

		@Override
		public Object getItem(int position) {

			return commentList.get(position);
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
						.inflate(R.layout.comment_listitem, null);
				holder.username = (TextView) convertView
						.findViewById(R.id.username);
				holder.comment = (TextView) convertView
						.findViewById(R.id.comment);
				holder.user_image = (ImageView) convertView
						.findViewById(R.id.user_image);

				convertView.setTag(holder);

			}

			else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.comment.setText(commentList.get(position).get("comment"));
			holder.username.setText(commentList.get(position).get("username"));

			return convertView;
		}

		class ViewHolder {
			TextView username, comment;
			ImageView user_image;
		}
	}
}
