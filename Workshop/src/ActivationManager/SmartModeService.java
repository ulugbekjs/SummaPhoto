package ActivationManager;

import java.io.FileNotFoundException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.example.aworkshop.SettingsActivity;
import android.util.Log;
import Common.ActualEventsBundle;
import Common.Photo;
import Common.PhotoContainer;
import Common.Utils;
import Generator.AbstractBuilder;
import Generator.AbstractTemplate;
import Generator.BlockCollageBuilder;
import Generator.MapCollageBuilder;
import Partitioning.DBScan;

public class SmartModeService {

	private static final String TAG = SmartModeService.class.getName();
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
				public void run() { // this is the main flow of the app

					manager.consumeDedictedRequests(); 
					boolean collageNeeded = manager.processPhotoBuffer();

					boolean successful = true;
					if (collageNeeded) { // ActivationManager decided clustering should be made
						ActualEventsBundle events = partiotionToEvents();

						// build the collage from Bundle of photos
						Photo collage = null;
						if (SettingsActivity.COLLAGE_TYPE == AbstractTemplate.BLOCK_TYPE) {
							successful &= buildCollage(collage, new BlockCollageBuilder(events));
						}
						if (SettingsActivity.COLLAGE_TYPE == AbstractTemplate.MAP_TYPE) {
							successful &= buildCollage(collage, new MapCollageBuilder(events));
						}
						if (successful) {
							try {
								Utils.notifyUserCollageCreated(collage);
							} catch (FileNotFoundException e) {
								Log.e(TAG, "Could not open the created collage file, collage notification aborted.");
							}
						}
						else { // there was an error, notify user
							Utils.notifyUserWithError("Error when building collage",
									"Summaphoto has failed building your collage.");
						}

					}
					else {
						// do nothing, advance to next iteration
					}
				}

				/**
				 * run the DBScan algorithm to cluster photos to ActualEvents
				 * @return
				 */
				private ActualEventsBundle partiotionToEvents() {
					DBScan eventsClusterer = new DBScan(PhotoContainer.getInstance().getProcessedPhotos());
					ActualEventsBundle events = eventsClusterer.runDBScanAlgorithm();
					return events;
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

	private static boolean buildCollage(Photo collage, AbstractBuilder builder) {
		DedicatedRequest request = builder.setTemplate();
		if (request != null) { // not enough photos for collage
			manager.addRequestToBuffer(request);
			return false;
		}
		else { 
			builder.populateTemplate();
			collage =  builder.buildCollage();
			return true;
		}
	}
	
	
	


}
