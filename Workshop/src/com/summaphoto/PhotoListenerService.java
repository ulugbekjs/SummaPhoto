package com.summaphoto;


import PhotoListener.CameraObserver;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.FileObserver;
import android.os.IBinder;
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

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
