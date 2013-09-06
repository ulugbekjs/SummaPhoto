package com.summaphoto;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import android.os.FileObserver;
import android.util.Log;
import ActivationManager.ActivationManager;
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

public class SmartModeFlow {

	private static final String TAG = SmartModeFlow.class.getName();
	private static final int MIN_EVENTS = 3;
	private static ExecutorService scheduler = null;
	private static boolean busy = false;
	private static ActivationManager manager = ActivationManager.getInstance();  

	private SmartModeFlow() {
	}

	public static void startFlow() {
		if (!isFlowRunning()) {
			scheduler = Executors.newSingleThreadExecutor();
			scheduler.execute(new Runnable() {

				@Override
				public void run() {

					Log.d(TAG, "Flow started: " + new Date());

					setBusy(true);					
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					boolean changed = manager.consumeDedictedRequests();
					if (changed) {
						Log.d(TAG, "Activation manager changed to dedicated mode");
						Log.d(TAG, "Activation manager: " + manager.toString());
					}

					boolean collageNeeded = manager.processPhotoBuffer();
					Log.d(TAG, "Collage needed: " + collageNeeded);

					if (collageNeeded) { // ActivationManager decided clustering should be made
						ActualEventsBundle events = partitionToEvents();
						Log.d(TAG, "ActualEvents calculated: " + events.getActualEvents().size());
						// build the collage from Bundle of photos
						ResultPair result = null;
						if (events.getActualEvents().size() >= MIN_EVENTS) {
							if (SettingsActivity.COLLAGE_TYPE == AbstractTemplate.BLOCK_TYPE) {
								Log.d(TAG, "attempting to build Block collage");
								result =  buildCollage(new BlockCollageBuilder(events));
							}
							if (SettingsActivity.COLLAGE_TYPE == AbstractTemplate.MAP_TYPE) {
								result = buildCollage(new MapCollageBuilder(events));
								Log.d(TAG, "attempting to build Map Collage");
							}
							if (result.validCollage) {
								Log.e(TAG, "Collage is valid!");
								try {
									Utils.notifyUserCollageCreated(result.collage);
								} catch (FileNotFoundException e) {
									Log.e(TAG, "Could not open the created collage file, collage notification aborted.");
								}
							}
						}

					}

					setBusy(false);

					Log.d(TAG, "Flow ended: " + new Date());
					Log.d(TAG, "Activatin mangager at end of flow:\n" + manager.toString());
				}

				/**
				 * run the DBScan algorithm to cluster photos to ActualEvents
				 * @return
				 */
				private ActualEventsBundle partitionToEvents() {
					DBScan eventsClusterer = new DBScan(PhotoContainer.getInstance().getProcessedPhotos());
					ActualEventsBundle events = eventsClusterer.ComputeCluster();
					return events;
				}
			});
		}
	}

	public synchronized static void stopService() {
		//		scheduler.shutdown();
		scheduler = null;
		busy = false;
	}

	private synchronized static void setBusy(boolean busy) {
		SmartModeFlow.busy = busy;
	}

	public static synchronized boolean isFlowRunning() {
		//		return (scheduler != null);
		return busy;
	}

	private static ResultPair buildCollage(AbstractBuilder builder) {
		boolean successful;
		Photo collage = null;
		DedicatedRequest request = builder.setTemplate();
		if (request != null) { // not enough photos for collage
			manager.addRequestToBuffer(request);
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
