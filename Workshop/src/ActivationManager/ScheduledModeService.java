package ActivationManager;

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

	public static void startService(DateTime scheduledTime) {
		if (scheduler != null) { // already runs
			stopService();
		}
		
		if (scheduler == null) {
			scheduler =  Executors.newScheduledThreadPool(1);
			
			// calculate time until next 
			DateTime now = DateTime.now();
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
	
}
