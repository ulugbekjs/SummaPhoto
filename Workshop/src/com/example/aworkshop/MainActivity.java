package com.example.aworkshop;

import java.io.File;

import ActivationManager.ActivationManager;
import ActivationManager.ActivationManagerThread;
import ActivationManager.SmartModeService;
import Common.Photo;
import PhotoListener.PhotoListenerThread;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.view.Menu;


public class MainActivity extends Activity {

	// static final fields
	public static final File ROOT = new File(Environment.getExternalStorageDirectory(), "DCIM");
	//		File dataDirectory = new File(root + "/DCIM/Camera/");
	private static final String  PHOTO_DIR = ROOT + File.separator + "Camera" + File.separator;

	// global fields
	PhotoListenerThread observer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		observer = new PhotoListenerThread(PHOTO_DIR); // observer over the gallery directory

		observer.startWatching();
		SmartModeService.startService();
	}

	//listen();

	//	private void listen() {
	//
	//		File root = android.os.Environment.getExternalStorageDirectory(); 
	//
	//		File dataDirectory = new File(root + "/DCIM/Tests/");
	//
	//		PhotoListenerThread t = new PhotoListenerThread();
	//
	//		File directory =dataDirectory;
	//		for (File file : directory.listFiles()) {
	//			Photo p = null;
	//
	//			p = t.createPhotoFromFile(file);
	//
	//			if (p != null) {
	//				ActivationManagerThread.getInstance().addToBuffer(p);
	//			}
	//		}
	//
	//		ActivationManagerThread.getInstance().processPhotoBuffer();
	//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
