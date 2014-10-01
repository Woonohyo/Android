package net.woonohyo.nextagram.gcm;

import net.woonohyo.nextagram.controller.NewsFeedController;
import net.woonohyo.nextagram.view.NewsFeedViewer;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.R;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class GcmIntentService extends IntentService {
	private static final String TAG = GcmIntentService.class.getSimpleName();
	private static final int NOTIFICATION_ID = 1;
	private NotificationManager notificationManager;
	NotificationCompat.Builder ncBuilder;

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {

			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {

			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				new NewsFeedController(getApplicationContext()).refreshData();
				sendNotification("Received: " + extras.toString());
				Log.i(TAG, "Received: " + extras.toString());

			}
		}

	}

	private void sendNotification(String msg) {
		notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, NewsFeedViewer.class), 0);

		ncBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_dialog_alert).setContentTitle("GCM Notification")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg)).setContentText(msg);
		
		ncBuilder.setContentIntent(contentIntent);
		notificationManager.notify(NOTIFICATION_ID, ncBuilder.build());
	}

}
