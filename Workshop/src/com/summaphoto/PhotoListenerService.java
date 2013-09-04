package com.summaphoto;


import PhotoListener.CameraObserver;
import android.app.Service;
import android.content.Intent;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;

public class PhotoListenerService extends Service {

	private static final String TAG = PhotoListenerService.class.getName();
	private FileObserver observer = null;


	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (observer == null) { // first time or observer was garbage-collected
			observer = new CameraObserver(intent.getStringExtra("path"));
			observer.startWatching();
			Log.d(TAG, "CameraObserver started watching");
		}

		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
