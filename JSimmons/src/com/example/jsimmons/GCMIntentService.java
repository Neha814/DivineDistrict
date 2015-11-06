package com.example.jsimmons;

import com.google.android.gcm.GCMBaseIntentService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.util.Log;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";

	static String PROJECT_NUMBER = "20231206239";

	SharedPreferences sp;

	public GCMIntentService() {
		super(PROJECT_NUMBER);
	}

	/**
	 * Method called on device registered
	 **/
	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.e(TAG, "Device registered: regId = " + registrationId);

	}

	/**
	 * Method called on device un registred
	 * */
	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.e(TAG, "Device unregistered");

	}

	/**
	 * Method called on Receiving a new message
	 * */
	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.e(TAG, "Received message");
		String msg = intent.getExtras().getString("message");
		String id = intent.getExtras().getString("id");
		String type = intent.getExtras().getString("type");

		Log.e("msg==", "" + msg + "  id :=" + id + "  type := " + type);

		generateNotification(context, msg, id, type);

	}

	/**
	 * Method called on receiving a deleted message
	 * */
	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");

		// notifies user

	}

	/**
	 * Method called on Error
	 * */
	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);

	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);

		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private static void generateNotification(Context context, String msg,
			String item_name, String amount) {

		Intent notificationIntent = new Intent(context, SplashScreen.class);

		notificationIntent.putExtra("msg", msg);
		notificationIntent.putExtra("id", item_name);
		notificationIntent.putExtra("type", amount);

		context.sendBroadcast(notificationIntent);

		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		int icon = R.drawable.app_logo;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		String title = context.getString(R.string.app_name);

		Notification.Builder builder = new Notification.Builder(context);

		builder.setContentIntent(intent)
				.setContentTitle("Divine District")
				.setContentText(msg)
				.setSmallIcon(icon);

		/*
		 * builder.setContentIntent(intent) .setContentTitle("Divine District")
		 * .setContentTitle(msg) .setSmallIcon(icon);
		 */

		Notification notification = builder.build();
		notificationManager.notify(0, notification);

		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Play default notification sound
		notification.defaults |= Notification.DEFAULT_SOUND;

		// Vibrate if vibrate is enabled
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);

	}

}
