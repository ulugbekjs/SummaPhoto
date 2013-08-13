package com.example.aworkshop;

import java.io.File;
import java.security.PublicKey;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;

import ActivationManager.ScheduledModeService;
import ActivationManager.SmartModeService;
import Common.Photo;
import PhotoListener.PhotoListenerThread;
import android.os.Bundle;
import android.os.Environment;
import android.R.integer;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TimePicker;

public class SettingsActivity extends FragmentActivity { // Extends FragmentActivity to support < Android 3.0

	// static final fields
	public static final File ROOT = new File(Environment.getExternalStorageDirectory(), "DCIM");
	//		File dataDirectory = new File(root + "/DCIM/Camera/");
	private static final String  PHOTO_DIR = ROOT + File.separator + "Camera" + File.separator;

	// global fields
	PhotoListenerThread observer;

	// private fields
	private TimePicker timePicker;
	private int pickerHour = -1;
	private int pickerMin = -1;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
//
//		observer = new PhotoListenerThread(PHOTO_DIR); // observer over the gallery directory
//		observer.startWatching();
//		SmartModeService.startService();
//

//		// disable time picker
//		timePicker = (TimePicker) findViewById(R.id.timePicker);
//		timePicker.setOnClickListener(new TimePickerFragment());
//		timePicker.setEnabled(false);


		//		Omri's code

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
						tempPhoho = PhotoListenerThread.createPhotoFromFile(file.getAbsolutePath());
					}
					catch (Exception ex)
					{
					}
					if (tempPhoho != null)
						photosToCluster.add(tempPhoho);
				}
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	public void onRadioButtonClicked(View view) {

		boolean checked = ((RadioButton) view).isChecked();

		// Check which radio button was clicked
		switch(view.getId()) {
		case R.id.radioSmart:
			if (checked) {
				smartButtonClicked();
			}

			break;
		case R.id.radioDaily:
			if (checked) {
			}
			break;
		case R.id.radioOff: 
			if (checked) {

			}

		}
	}

	/**
	 * when off button is pressed, services need to be turned off
	 */
	private void offButtonClicked() {

		timePicker.setEnabled(false);

		// turn off active mode
		turnOffSmartMode();
		turnOffDailyMode();

	}

	private void dailyButtonClicked() {
		timePicker.setEnabled(true);
		turnOffSmartMode();

		if (pickerHour != -1 && pickerMin != -1)
			ScheduledModeService.startService(this.pickerHour, this.pickerMin);

	}

	private void smartButtonClicked() {

		timePicker.setEnabled(false);
		turnOffDailyMode();

		SmartModeService.startService();

	}

	private void turnOffSmartMode() {
		if (SmartModeService.isServiceRunning())
			SmartModeService.stopService();
	}

	private void turnOffDailyMode() {
		if (ScheduledModeService.isServiceRunning())
			ScheduledModeService.stopService();
	}

	public class TimePickerFragment extends DialogFragment implements OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			SettingsActivity.this.pickerHour = hourOfDay;
			SettingsActivity.this.pickerMin = minute; 
		}
	}

	public void showTimePickerDialog(View v) {
		TimePickerFragment newFragment = new TimePickerFragment();
		newFragment.show(getFragmentManager(), "timePicker"); 
	}

	public int getScheduledHour() {
		return this.pickerHour;
	}

	public int getScheduledMinute() {
		return this.pickerMin;
	}

}
