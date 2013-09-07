package com.summaphoto;

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
import com.summaphoto.R;

import Common.ActualEvent;
import Common.ActualEventsBundle;
import Common.Constants;
import Common.Photo;
import Common.Tester;
//import Common.PhotoFilter;
import Common.TestsClass;
import Generator.AbstractTemplate;
import Generator.Line;
import Generator.PixelPoint;
import Generator.LocatePicturesWithMap.SlotPushPinTuple;
import Generator.MapCollageBuilder;
import Partitioning.Cluster;
import Partitioning.DBScan;
import Partitioning.PhotoObjectForClustering;
import Partitioning.TestDBScan;
import PhotoListener.CameraObserver;
import android.R.drawable;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

public class SettingsActivity extends Activity {

	private static final String TAG = SettingsActivity.class.getName();

	// static final fields
	public static final File ROOT = new File(Environment.getExternalStorageDirectory(), "DCIM");
	private static final String  PHOTO_DIR = ROOT + File.separator + "Tals2" + File.separator;
	//	private static final String  PHOTO_DIR = ROOT + File.separator + "Tals" + File.separator;
	//		private static final String  PHOTO_DIR = ROOT + File.separator + "Watched" + File.separator;
	//		private static final String  PHOTO_DIR = ROOT + File.separator + "Tests" + File.separator;
	//	private static final String  PHOTO_DIR = ROOT + File.separator + "copy" + File.separator;
	//	public static final String APP_PHOTO_DIR =  new File(Environment.getExternalStorageDirectory(), "Pictures") + File.separator + "SummaPhoto" + File.separator;
	//	public static final String APP_TEMP_DIR = new File(Environment.getExternalStorageDirectory(), "Summaphoto") + File.separator + "Temp" + File.separator;



	// public static fields
	public static Context CONTEXT = null;
	public static int MODE = 0;
	public static int COLLAGE_TYPE = 1;

	// private fields
	private RadioGroup modeGroup;
	private RadioButton offRadioButton;
	private RadioButton dailyRadioBtn;
	private RadioButton smartRadioButton;
	private RadioButton mapRadioButton;
	private RadioButton blocksRadioButton;

	private RadioButton lastCheckedButton;

	private int pickerHour = -1;
	private int pickerMin = -1;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		CONTEXT = this;

		createAppFolders();
		saveLogcatToFile();

		// getting radio buttons
		modeGroup = (RadioGroup) findViewById(R.id.radioMode);
		offRadioButton = (RadioButton) findViewById(R.id.radioOff);
		smartRadioButton = (RadioButton) findViewById(R.id.radioSmart);
		dailyRadioBtn = (RadioButton) findViewById(R.id.radioDaily);
		mapRadioButton = (RadioButton) findViewById(R.id.radioMapType);
		blocksRadioButton = (RadioButton) findViewById(R.id.radioBlocksType);

		lastCheckedButton = offRadioButton;

		if (savedInstanceState != null) {
			onRestoreInstanceState(savedInstanceState);
		}
		//	Yonatan's code


		OnClickListener listener = new ScheduledModeListener(); // use same listener every time
		dailyRadioBtn.setOnClickListener(listener);

		//		Button button = (Button) findViewById(R.id.button1);
		//
		//		button.setOnClickListener(new OnClickListener() {
		//
		//			@Override
		//			public void onClick(View arg0) {
		//				Tester.SmartWithMapTest();
		//			}
		//
		//		});
		//
		//		button = (Button) findViewById(R.id.button2);
		//
		//		button.setOnClickListener(new OnClickListener() {
		//
		//			@Override
		//			public void onClick(View arg0) {
		//				Tester.SmartWithBlocksTest();
		//			}
		//
		//		});
		//
		//
		//		button = (Button) findViewById(R.id.button3);
		//
		//		button.setOnClickListener(new OnClickListener() {
		//
		//			@Override
		//			public void onClick(View arg0) {
		//				Tester.ScheduledWithMapTest(SettingsActivity.this, 11, 49);
		//			}
		//
		//		});
		//
		//
		//		button = (Button) findViewById(R.id.button4);
		//
		//		button.setOnClickListener(new OnClickListener() {
		//
		//			@Override
		//			public void onClick(View arg0) {
		//				Tester.ScheduledWithBlocksTest(22,00);
		//			}
		//
		//		});
		//

		//		Omri's code

//				Tester.omriInsertFilesToObservedDir();
//				try {
//					Thread.sleep(60000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				Log.d(TAG, "starting second test!!!");
//				Tester.omriInsertFilesToObservedDir();
//				try {
//					Thread.sleep(60000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				Log.d(TAG, "starting thirs test!!!");
//				Tester.omriInsertFilesToObservedDir();
		//						File directory = new File(PHOTO_DIR);
		//						if (!directory.exists())
		//							return;
		//						File[] arrayOfPic =  directory.listFiles();
		//						Photo tempPhoho = null;
		//						List<Photo> photosToCluster = new LinkedList<Photo>(); 
		//						for (File file : arrayOfPic)
		//						{
		//							try
		//							{
		//								tempPhoho = Common.Utils.createPhotoFromFile(file.getAbsolutePath());
		//							}
		//							catch (Exception ex)
		//							{
		//							}
		//							if (tempPhoho != null)
		//								photosToCluster.add(tempPhoho);
		//						}
		//						DBScan algo = new DBScan(photosToCluster);
		//						ActualEventsBundle bundle = algo.ComputeCluster();		
		//						List<ActualEvent> events = new LinkedList<ActualEvent>();
		//						Cluster tempCluster;
		//						for (Photo p :photosToCluster)
		//						{
		//							tempCluster = new Cluster();
		//							tempCluster.photosInCluster.add( new PhotoObjectForClustering(p));
		//							events.add(new ActualEvent(tempCluster));
		//						}
		//						MapCollageBuilder builder = new MapCollageBuilder(bundle);
		//						builder.setTemplate();
		//						if (builder.populateTemplate())
		//						{
		//							builder.buildCollage();
		//						}
		//				
		//						return;
		//

	}



	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putBoolean("off", offRadioButton.isChecked());
		outState.putBoolean("smart", smartRadioButton.isChecked());
		outState.putBoolean("daily", dailyRadioBtn.isChecked());
		outState.putBoolean("map", mapRadioButton.isChecked());
		outState.putBoolean("blocks", blocksRadioButton.isChecked());

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		offRadioButton.setChecked(savedInstanceState.getBoolean("off"));
		smartRadioButton.setChecked(savedInstanceState.getBoolean("smart"));
		dailyRadioBtn.setChecked(savedInstanceState.getBoolean("daily"));
		mapRadioButton.setChecked(savedInstanceState.getBoolean("map"));
		blocksRadioButton.setChecked(savedInstanceState.getBoolean("blocks"));
	}

	/**
	 * create necessary folders for app
	 * @return
	 */
	private boolean createAppFolders() {

		if (!Common.Utils.isExternalStorageWritable())
			return false;

		//create folders for app
		File tmpFile = new File(Constants.APP_PHOTO_DIR);
		if (!tmpFile.exists()) {
			tmpFile.mkdirs();
		}

		tmpFile = new File(Constants.APP_TEMP_DIR);
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

	/**
	 * sends an intent to start running the PhotoListenerService
	 */
	private void startObserverService() {
		// start camera folder observer 
		Intent i= new Intent(this, PhotoListenerService.class);
		i.putExtra("path", Constants.PHOTO_DIR);
		//		i.putExtra("path", Constants.ROOT + File.separator + "Watched" + File.separator);
		startService(i);
	}

	/**
	 * creates and directs log to file
	 */
	public static void saveLogcatToFile() {    
		String fileName =  "log.txt";
		File outputFile = new File(Constants.APP_TEMP_DIR,fileName);
		if (!outputFile.exists()) {
			try {
				outputFile.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		try {
			@SuppressWarnings("unused")
			Process process = Runtime.getRuntime().exec("logcat -f "+outputFile.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		if (PhotoListenerService.isObserving()) {
			stopService(new Intent(this, PhotoListenerService.class));
		}
		if (SmartModeFlow.isFlowRunning()) {
			turnOffSmartMode();
		}
		if (ScheduledModeService.isServiceRunning()) {
			turnOffDailyMode();
		}
	}

	private void dailyButtonClicked() {

		if (!PhotoListenerService.isObserving()) {
			startObserverService();
		}

		if (SmartModeFlow.isFlowRunning())
			turnOffSmartMode();

		MODE = 2;

		Thread thread = new Thread() {

			@Override
			public void run() {
				ScheduledModeService.startScheduledMode(SettingsActivity.this,
						SettingsActivity.this.pickerHour, 
						SettingsActivity.this.pickerMin);
			}
		};

		thread.run();
	}

	/**
	 * when smart button clicked, need to start observing and turn off daily mode
	 */
	private void smartButtonClicked() {

		if (!PhotoListenerService.isObserving()) {
			startObserverService();
		}

		turnOffDailyMode();

		MODE = 1;

		Thread thread = new Thread() {

			@Override
			public void run() {
				if (!SmartModeFlow.isFlowRunning()) {
					SmartModeFlow.startFlow(); 
				}
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
		if (SmartModeFlow.isFlowRunning())
			SmartModeFlow.stopService();
	}

	private void turnOffDailyMode() {
		if (ScheduledModeService.isServiceRunning())
			ScheduledModeService.stopService();
	}

	/**
	 * listener for the daily mode button, opens up the time picker dialog
	 * @author yonatan
	 *
	 */
	private class ScheduledModeListener implements View.OnClickListener { 


		@Override
		public void onClick(View v) {

			final TimePicker timePickerDialog = new TimePicker(v.getContext());
			timePickerDialog.setIs24HourView(true);
			if (pickerHour == -1 && pickerMin == -1) { // first time
				timePickerDialog.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY ) + 1);
				timePickerDialog.setCurrentMinute(0);
			}
			else {
				timePickerDialog.setCurrentHour(pickerHour);
				timePickerDialog.setCurrentMinute(pickerMin);
			}

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
}
