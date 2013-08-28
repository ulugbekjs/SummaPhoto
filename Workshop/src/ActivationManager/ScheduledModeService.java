package ActivationManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.Seconds;

import android.R.integer;

public class ScheduledModeService{

	private static ScheduledExecutorService scheduler = null;
	private static ActivationManager manager = ActivationManager.getInstance();  

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
					manager.processPhotoBuffer();
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

}
