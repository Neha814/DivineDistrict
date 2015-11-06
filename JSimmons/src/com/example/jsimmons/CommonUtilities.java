package com.example.jsimmons;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public final class CommonUtilities {
	
	// give your server registration url here
  //  static final String SERVER_URL = "http://10.0.2.2/gcm_server_php/register.php"; 
    static final String SERVER_URL =  "http://mydrchat.com/api/updateDeviceId.php";

    // Google project id
    static final String SENDER_ID = "828841915730"; 

    /**
     * Tag used on log messages.
     */
    static final String TAG = "MyDrChatApp";

   /* static final String DISPLAY_MESSAGE_ACTION =
            "com.androidhive.pushnotifications.DISPLAY_MESSAGE";*/
    
    static final String DISPLAY_MESSAGE_ACTION =
            "com.example.mydrchatapp.CommonUtilities";

    static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        Log.i("unread msg==111","=="+message);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
