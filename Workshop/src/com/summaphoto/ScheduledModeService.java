package com.summaphoto;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ActivationManager.DedicatedRequest;
import Common.ActualEventsBundle;
import Common.Photo;
import Common.PhotoContainer;
import Common.Utils;
import Generator.AbstractBuilder;
import Generator.AbstractTemplate;
import Generator.BlockCollageBuilder;
import Generator.MapCollageBuilder;
import Partitioning.DBScan;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class ScheduledModeService extends Service{

	private final static String TAG = ScheduledModeService.class.getName();

	//	private static ScheduledExecutorService scheduler = null;
	private static AlarmManager alarmManager = null;
	private static PendingIntent intent = null;

	public ScheduledModeService() {
		super();
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		new Thread( new Runnable() {

			@Override
			public void run() {

				Log.d(TAG, "Scheduled Mode: Starting flow");

				// in this flow there are no dedicated requests
				ActualEventsBundle events = partitionToEvents();

				// build the collage from Bundle of photos
				ResultPair result = null;

				if (SettingsActivity.COLLAGE_TYPE == AbstractTemplate.BLOCK_TYPE) {
					result =  buildCollage(new BlockCollageBuilder(events));
				}
				if (SettingsActivity.COLLAGE_TYPE == AbstractTemplate.MAP_TYPE) {
					result = buildCollage(new MapCollageBuilder(events));
				}
				if (result.validCollage) {
					try {
						Utils.notifyUserCollageCreated(result.collage);
					} catch (FileNotFoundException e) {
						Log.e(TAG, "Could not open the created collage file, collage notification aborted.");
					}
				}		

				Log.d(TAG, "Scheduled Mode: flow ended");

			}

			private ActualEventsBundle partitionToEvents() {
				List<Photo> photos = new ArrayList<Photo>();
				while (!PhotoContainer.getInstance().isEmpty()) {
					photos.add(PhotoContainer.getInstance().getNextPhotoFromBuffer());
				}
				DBScan eventsClusterer = new DBScan(photos);
				ActualEventsBundle events = eventsClusterer.ComputeCluster();
				return events;
			}
		}).run();

		return START_REDELIVER_INTENT;
	}

	public static void startScheduledMode(Context context, int hour, int min) {
		if (alarmManager != null && intent != null) {
			alarmManager.cancel(intent);
		}

		if (alarmManager == null) {

			alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//			PendingIntent pIntent = PendingIntent.getService(context, 0, new Intent(context, ScheduledModeService.class), 0);
			Intent in = new Intent(context, ScheduledModeService.class);
			PendingIntent pIntent = PendingIntent.getService(context, 0, in, 0);
		

			intent = pIntent;

			Calendar calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			
			if (hasPassedToday(hour, min)) {
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, plusDay(year, month, day, hour, min).getTime(), AlarmManager.INTERVAL_DAY, pIntent);
			}
			else {
				calendar.set(Calendar.HOUR_OF_DAY, hour);
				calendar.set(Calendar.MINUTE, min);
				calendar.set(Calendar.SECOND, 0);
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pIntent);
			}
	        
		}
	}

	//
	//	public static void startService(int hour, int min) {
	//		if (scheduler != null) { // already runs
	//			stopService();
	//		}
	//		
	//		if (scheduler == null) {
	//			scheduler =  Executors.newScheduledThreadPool(1);
	//
	//			int timeToWakeInSeconds = calcTimeToWakeInSeconds(hour, min);
	//
	//			// waits INTERVAL_IN_SECONDS seconds after end of last execution
	//			scheduler.scheduleWithFixedDelay(new Runnable() {
	//				@Override
	//				public void run() {
	//					
	//					Log.d(TAG, "Scheduled Mode: Starting flow");
	//					
	//					// in this flow there are no dedicated requests
	//					ActualEventsBundle events = partitionToEvents();
	//
	//					// build the collage from Bundle of photos
	//					ResultPair result = null;
	//					
	//					if (SettingsActivity.COLLAGE_TYPE == AbstractTemplate.BLOCK_TYPE) {
	//						result =  buildCollage(new BlockCollageBuilder(events));
	//					}
	//					if (SettingsActivity.COLLAGE_TYPE == AbstractTemplate.MAP_TYPE) {
	//						result = buildCollage(new MapCollageBuilder(events));
	//					}
	//					if (result.validCollage) {
	//						try {
	//							Utils.notifyUserCollageCreated(result.collage);
	//						} catch (FileNotFoundException e) {
	//							Log.e(TAG, "Could not open the created collage file, collage notification aborted.");
	//						}
	//					}		
	//					
	//					Log.d(TAG, "Scheduled Mode: flow ended");
	//					
	//				}
	//				
	//				private ActualEventsBundle partitionToEvents() {
	//					List<Photo> photos = new ArrayList<Photo>();
	//					while (!PhotoContainer.getInstance().isEmpty()) {
	//						photos.add(PhotoContainer.getInstance().getNextPhotoFromBuffer());
	//					}
	//					DBScan eventsClusterer = new DBScan(photos);
	//					ActualEventsBundle events = eventsClusterer.ComputeCluster();
	//					return events;
	//				}
	//			},
	//			timeToWakeInSeconds,
	//			86400, // DAY
	//			TimeUnit.SECONDS);	
	//		}
	//	}

	private static int calcTimeToWakeInSeconds(int hour, int min) {
		// calculate time until next waking of thread

		Calendar calendar = Calendar.getInstance();
		Date currentDateTime =  calendar.getTime();

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.set(year,
				month,
				day,
				hour, min, 0);

		Date scheduledTime = calendar.getTime();

		int timeToWakeInSeconds = 0;
		if (compareTimes(scheduledTime, currentDateTime) < 0) { // scheduledTime passed today
			scheduledTime = plusDay(year, month, day, hour, min);
			timeToWakeInSeconds = (int) ((scheduledTime.getTime() - currentDateTime.getTime()) / 1000);
		}
		else {
			timeToWakeInSeconds = (int) ((scheduledTime.getTime() - currentDateTime.getTime()) / 1000);
		}

		return timeToWakeInSeconds;
	}
	
	private static boolean hasPassedToday(int hour, int min) {
		// calculate time until next waking of thread

		Calendar calendar = Calendar.getInstance();
		Date currentDateTime =  calendar.getTime();

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.set(year,
				month,
				day,
				hour, min, 0);

		Date scheduledTime = calendar.getTime();

		if (compareTimes(scheduledTime, currentDateTime) < 0) { // scheduledTime passed today
			scheduledTime = plusDay(year, month, day, hour, min);
			return true;
		}
		else {
			return false;
		}

	}

	private static int compareTimes(Date d1, Date d2)
	{
		int     t1;
		int     t2;

		t1 = (int) (d1.getTime() % (24*60*60*1000L));
		t2 = (int) (d2.getTime() % (24*60*60*1000L));

		return (t1 - t2);
	}

	private static Date plusDay(int year, int month, int day, int hour, int min) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year,
				month,
				day + 1,
				hour, min, 0);
		return calendar.getTime();

	}

	public static void stopService() {
		alarmManager.cancel(intent);
		alarmManager = null;
	}

	public static boolean isServiceRunning() {
		return (alarmManager != null);
	}

	private static ResultPair buildCollage(AbstractBuilder builder) {
		boolean successful;
		Photo collage = null;
		DedicatedRequest request = builder.setTemplate();
		if (request != null) { // not enough photos for collage
			successful = false;
		}
		else { 
			builder.populateTemplate();
			collage =  builder.buildCollage();
			successful = true;
		}
		return new ResultPair(successful, collage);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
