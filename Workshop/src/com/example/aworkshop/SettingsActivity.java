package com.example.aworkshop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.adobe.xmp.impl.Utils;

import ActivationManager.ScheduledModeService;
import ActivationManager.SmartModeService;
import Common.ActualEvent;
import Common.ActualEventsBundle;
import Common.Line;
import Common.Photo;
import Common.Tester;
//import Common.PhotoFilter;
import Common.TestsClass;
import Generator.AbstractTemplate;
import Generator.PixelPoint;
import Generator.LocatePicturesWithMap.SlotPushPinTuple;
import Generator.MapCollageBuilder;
import Partitioning.Cluster;
import Partitioning.DBScan;
import Partitioning.PhotoObjectForClustering;
import Partitioning.TestDBScan;
import PhotoListener.PhotoListenerThread;
import android.R.drawable;
import android.R.integer;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

public class SettingsActivity extends FragmentActivity { // Extends FragmentActivity to support < Android 3.0
	
	private static final String TAG = SettingsActivity.class.getName();
	
	// static final fields
	public static final File ROOT = new File(Environment.getExternalStorageDirectory(), "DCIM");
//		private static final String  PHOTO_DIR = ROOT + File.separator + "Camera" + File.separator;
	private static final String  PHOTO_DIR = ROOT + File.separator + "test4" + File.separator;
//	private static final String  PHOTO_DIR = ROOT + File.separator + "Watched" + File.separator;

	//	private static final String  PHOTO_DIR = ROOT + File.separator + "copy" + File.separator;
	public static final String APP_PHOTO_DIR =  new File(Environment.getExternalStorageDirectory(), "Pictures") + File.separator + "SummaPhoto" + File.separator;
	public static final String APP_TEMP_DIR = new File(Environment.getExternalStorageDirectory(), "Summaphoto") + File.separator + "Temp" + File.separator;



	// public static fields
	public static Context CONTEXT = null;
	public static int MODE = 0;
	public static int COLLAGE_TYPE = 2;

	// global fields
	PhotoListenerThread observer;

	// private fields
	private RadioButton dailyRadioBtn;
	private RadioGroup modeGroup;
	private RadioButton lastCheckedButton;

	private int pickerHour = 20;
	private int pickerMin = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		CONTEXT = this;

		createAppFolders();
		
	
//		Tester.insertFilesToObservedDir();
//		
//		Canvas canvas = null;
//		Bitmap bmpBase = null;
//
//		bmpBase = Bitmap.createBitmap(1469, 1102, Bitmap.Config.ARGB_8888);
//		bmpBase.setHasAlpha(true);
//		canvas = new Canvas(bmpBase);
//		
//		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		//paint.setColor(android.graphics.Color.MAGENTA);
//		paint.setShader(new LinearGradient(0, 0, 200, 200, Color.YELLOW, Color.WHITE, android.graphics.Shader.TileMode.MIRROR));
//		paint.setStrokeWidth(5f);
//	    paint.setStrokeJoin(Paint.Join.ROUND);
//	    paint.setStyle(Style.STROKE);
//	    paint.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
//	    paint.setAlpha(120);
//		canvas.drawLine(200, 200, 400, 400, paint);
////		Path path = getArrowHead(400, 400);
////		canvas.drawPath(path, paint);
//		Photo photo = null;
//		try {
//			photo = saveCollage(bmpBase);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
////		
//		
//		try {
//			Common.Utils.notifyUserCollageCreated(photo);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		//		// 		Yonatan's code
		//		//
		//
		//

//		observer = new PhotoListenerThread(PHOTO_DIR); // observer over the gallery directory
//		observer.startWatching();
//		dailyRadioBtn = (RadioButton) findViewById(R.id.radioDaily);
//		modeGroup = (RadioGroup) findViewById(R.id.radioMode);
//		lastCheckedButton = (RadioButton) findViewById(R.id.radioOff);
//
//		OnClickListener listener = new ScheduledModeListener(); // use same listener every time
//		dailyRadioBtn.setOnClickListener(listener);
		 

////		//TODO: remove, this is because of netwrok on main thread error
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); 
			StrictMode.setThreadPolicy(policy);
		}

		//		//		//		Omri's code
		File directory = new File(PHOTO_DIR);
		if (!directory.exists())
			return;
		File[] arrayOfPic =  directory.listFiles();
		Photo tempPhoho = null;
		List<Photo> photosToCluster = new LinkedList<Photo>(); 
		for (File file : arrayOfPic)
		{
			try
			{
				tempPhoho = Common.Utils.createPhotoFromFile(file.getAbsolutePath());
			}
			catch (Exception ex)
			{
			}
			if (tempPhoho != null)
				photosToCluster.add(tempPhoho);
		}
		DBScan algo = new DBScan(photosToCluster);
		ActualEventsBundle bundle = algo.runDBScanAlgorithm();
//		
//		List<ActualEvent> events = new LinkedList<ActualEvent>();
//		Cluster tempCluster;
//		for (Photo p :photosToCluster)
//		{
//			tempCluster = new Cluster();
//			tempCluster.photosInCluster.add( new PhotoObjectForClustering(p));
//			events.add(new ActualEvent(tempCluster));
//		}
//		 = new ActualEventsBundle(events);
		MapCollageBuilder builder = new MapCollageBuilder(bundle);
		builder.buildCollage();

		return;
				

	}
	
	private Path getArrowHead(int tipX, int tipY) {
		Path path = new Path();
		path.moveTo(tipX, tipY);
        path.lineTo(tipX - 20, tipY +  60);
        path.lineTo(tipX, tipY + 50);
        path.lineTo(tipX + 20, tipY + 60);
        path.close();
        
        Matrix mMatrix = new Matrix();
        RectF bounds = new RectF();
        path.computeBounds(bounds, true);
        mMatrix.postRotate(90, 
                           (bounds.right + bounds.left)/2, 
                           (bounds.bottom + bounds.top)/2);
        path.transform(mMatrix);
		return path;
	}

	// TODO: remove
	protected Photo saveCollage(Bitmap bmpBase) throws IOException {
		Calendar calendar = Calendar.getInstance();
		File externalStorageDir = new File(Environment.getExternalStorageDirectory(), "Pictures");
		File testsDir = new File(externalStorageDir.getAbsolutePath() + File.separator + "Output");
		File file = null;
		FileOutputStream fos = null;

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
		file = new File
				(testsDir, "summaphoto_" + formatter.format(calendar.getTime()) + ".jpg");

		// Save Bitmap to File
		fos = new FileOutputStream(file);
		bmpBase.compress(Bitmap.CompressFormat.JPEG, 70, fos);

		fos.flush();
		fos.close();
		fos = null;

		bmpBase.recycle();
		bmpBase = null;

		return new Photo(calendar.getTime(), 3264, 2488, null, file.getAbsolutePath());
	}

	private boolean createAppFolders() {
		
		if (!Common.Utils.isExternalStorageWritable())
				return false;
		
		//create folders for app
		File tmpFile = new File(APP_PHOTO_DIR);
		if (!tmpFile.exists()) {
			tmpFile.mkdirs();
		}
		
		tmpFile = new File(APP_TEMP_DIR);
		if (!tmpFile.exists()) {
			tmpFile.mkdirs();
		}
		
		File nomediaFile = new File(tmpFile, ".nomedia");
		if (!nomediaFile.exists()) {
			try {
				nomediaFile.createNewFile();
			} catch (IOException e) {
				Log.e(TAG, "Could not create .nomedia file");
			}
		}
		
		tmpFile = null;
		
		
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	public void onRadioButtonClicked(View view) {

		lastCheckedButton = (RadioButton) view;
		boolean checked = lastCheckedButton.isChecked();

		// Check which radio button was clicked
		switch(view.getId()) {
		case R.id.radioSmart:
			if (checked) {
				smartButtonClicked();
			}
			break;
		case R.id.radioDaily:
			if (checked) {
				dailyButtonClicked();
			}
			break;
		case R.id.radioOff: 
			if (checked) {
				offButtonClicked();
			}
		default: {

		}
		}
	}

	public void onTypeRadioButtonClicked(View view) {
		lastCheckedButton = (RadioButton) view;
		boolean checked = lastCheckedButton.isChecked();

		// Check which radio button was clicked
		switch(view.getId()) {
		case R.id.radioMapType:
			if (checked) {
				mapTypeButtonClicked();
			}
			break;
		case R.id.radioBlocksType:
			if (checked) {
				blocksTypeButtonClicked();
			}
			break;
		default: {

		}
		}
	}

	/**
	 * when off button is pressed, services need to be turned off
	 */
	private void offButtonClicked() {
		
		MODE = 0;

		// turn off active modes
		if (SmartModeService.isServiceRunning()) {
			turnOffSmartMode();
		}
		if (ScheduledModeService.isServiceRunning()) {
			turnOffDailyMode();
		}
		

	}

	private void dailyButtonClicked() {

		if (SmartModeService.isServiceRunning())
			turnOffSmartMode();
		
		MODE = 2;

		Thread thread = new Thread() {

			@Override
			public void run() {
				ScheduledModeService.startService(SettingsActivity.this.pickerHour, SettingsActivity.this.pickerMin);
			}
		};

		thread.run();
	}

	private void smartButtonClicked() {

		turnOffDailyMode();
		
		MODE = 1;

		Thread thread = new Thread() {

			@Override
			public void run() {
				SmartModeService.startService(); 
			}
		};

		thread.run();

	}

	private void mapTypeButtonClicked() {
		COLLAGE_TYPE = AbstractTemplate.MAP_TYPE;
	}

	private void blocksTypeButtonClicked() {
		COLLAGE_TYPE = AbstractTemplate.BLOCK_TYPE;
	}

	private void turnOffSmartMode() {
		if (SmartModeService.isServiceRunning())
			SmartModeService.stopService();
	}

	private void turnOffDailyMode() {
		if (ScheduledModeService.isServiceRunning())
			ScheduledModeService.stopService();
	}

	private class ScheduledModeListener implements View.OnClickListener { 


		@Override
		public void onClick(View v) {

			final TimePicker timePickerDialog = new TimePicker(v.getContext());
			timePickerDialog.setIs24HourView(true);
			timePickerDialog.setCurrentHour(pickerHour);
			timePickerDialog.setCurrentMinute(pickerMin);

			// creating AlertDialog because of no cancel button in TimePickerDialog
			new AlertDialog.Builder(v.getContext())
			.setTitle("Choose Time...")
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					SettingsActivity.this.pickerHour = timePickerDialog.getCurrentHour();
					SettingsActivity.this.pickerMin = timePickerDialog.getCurrentMinute();
					dailyButtonClicked();
				}
			})
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog,	int which) {
					modeGroup.check(lastCheckedButton.getId());
				}
			}).setView(timePickerDialog).show();
		}
	}

	@Override
	protected void onDestroy() {
		offButtonClicked(); // shutdown all services
		super.onDestroy();
	}

}
