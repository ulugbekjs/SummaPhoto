package ActivationManager;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.Seconds;

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


			// calculate time until next waking of thread

			DateTime now = DateTime.now();
			Calendar calendar = Calendar.getInstance();
			calendar.set(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH),
					hour, min);

			DateTime scheduledTime = new DateTime(calendar.getTime());

			DateTimeComparator comparator = DateTimeComparator.getTimeOnlyInstance();
			int timeToWake = 0;
			if (comparator.compare(now, scheduledTime) < 0) { // scheduledTime already passed for today
				timeToWake = Seconds.secondsBetween(DateTime.now(),scheduledTime.plusDays(1)).getSeconds();
			}
			else {
				timeToWake = Seconds.secondsBetween(DateTime.now(),scheduledTime).getSeconds();
			}

			// waits INTERVAL_IN_SECONDS seconds after end of last execution
			scheduler.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					manager.processPhotoBuffer();
				}
			},
			timeToWake,
			86400, // DAY
			TimeUnit.SECONDS);	
		}
	}

	public static void stopService() {
		scheduler.shutdown();
		scheduler = null;
	}

	public static boolean isServiceRunning() {
		return (scheduler != null);
	}

}