package ActivationManager;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import Common.ActualEventsBundle;
import Common.Photo;
import Common.PhotoContainer;
import Common.Utils;
import Generator.AbstractBuilder;
import Generator.AbstractTemplate;
import Generator.BlockCollageBuilder;
import Generator.MapCollageBuilder;
import Partitioning.DBScan;
import android.util.Log;

import com.example.aworkshop.SettingsActivity;

public class ScheduledModeService{
	
	private final static String TAG = ScheduledModeService.class.getName();

	private static ScheduledExecutorService scheduler = null;

	private ScheduledModeService() {
	}

	public static void startService(int hour, int min) {
		if (scheduler != null) { // already runs
			stopService();
		}

		if (scheduler == null) {
			scheduler =  Executors.newScheduledThreadPool(1);

			int timeToWakeInSeconds = calcTimeToWakeInSeconds(hour, min);

			// waits INTERVAL_IN_SECONDS seconds after end of last execution
			scheduler.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					
					Log.d(TAG, "Starting flow");
					
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
					
					Log.d(TAG, "flow ended");
					
				}
				
				private ActualEventsBundle partitionToEvents() {
					List<Photo> photos = new ArrayList<Photo>();
					while (!PhotoContainer.getInstance().isEmpty()) {
						photos.add(PhotoContainer.getInstance().getNextPhotoFromBuffer());
					}
					DBScan eventsClusterer = new DBScan(photos);
					ActualEventsBundle events = eventsClusterer.runDBScanAlgorithm();
					return events;
				}
			},
			timeToWakeInSeconds,
			86400, // DAY
			TimeUnit.SECONDS);	
		}
	}

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
		scheduler.shutdown();
		scheduler = null;
	}

	public static boolean isServiceRunning() {
		return (scheduler != null);
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
}
