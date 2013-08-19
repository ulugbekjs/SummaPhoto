package ActivationManager;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import Bing.BingServices;
import Bing.StaticMap;
import Generator.MapCollageBuilder;
import Generator.Template;

public class SmartModeService {
	private static ScheduledExecutorService scheduler = null;
	private static ActivationManager manager = ActivationManager.getInstance();  
	private static final int INTERVAL_IN_SECONDS = 30;

	private SmartModeService() {
	}

	public static void startService() {
		if (scheduler == null) {
			scheduler =  Executors.newScheduledThreadPool(1);
			// waits INTERVAL_IN_SECONDS seconds after end of last execution
			scheduler.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					manager.consumeDedictedRequests(); 
					boolean collageNeeded = manager.processPhotoBuffer();

					if (collageNeeded) {
						Template template = Template.getTemplate(1);
						StaticMap map = BingServices.getStaticMap(BingServices.getImagesPointsList(),890,523);
						File collageFile = MapCollageBuilder.BuildCollage(template);
					}
				}
			},
			20,
			INTERVAL_IN_SECONDS,
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
