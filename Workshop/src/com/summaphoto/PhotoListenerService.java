package com.summaphoto;


import PhotoListener.CameraObserver;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.FileObserver;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class PhotoListenerService extends Service {

	private static final String TAG = PhotoListenerService.class.getName();
	private static FileObserver observer = null;


	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		final Intent i = intent;
		if (i != null) {
			if (observer == null) { // first time or observer was garbage-collected
				new Thread(new Runnable() {

					@Override
					public void run() {
						String path = i.getStringExtra("path");
						observer = new CameraObserver(path);
						observer.startWatching();
						startObservingInForeground();
						Log.d(TAG, "CameraObserver started watching");						
					}
				}).run();

			}
		}
		else {
			Log.e(TAG, "recieved null intent");
		}

		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_REDELIVER_INTENT;
	}
	
	public static boolean isObserving() {
		return (observer != null);
	}
	
	
	@Override
	public void onDestroy() {
		observer = null;
		super.onDestroy();
	}

	@SuppressLint("NewApi")
	private void startObservingInForeground() {

		Intent notifyIntent = new Intent(Intent.ACTION_MAIN);
		notifyIntent.setClass(getApplicationContext(), SettingsActivity.class);
		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
				Intent.FLAG_ACTIVITY_SINGLE_TOP);


		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.icon)
		.setContentTitle("SummaPhoto")
		.setContentText("Return to app")
		.setContentIntent(PendingIntent.getActivity(this, 0, notifyIntent, 0));

		Notification notification = mBuilder.build();
		if (Build.VERSION.SDK_INT >= 16 ) {
			notification.priority = Notification.PRIORITY_MIN;
		}

		startForeground(1337, notification);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
