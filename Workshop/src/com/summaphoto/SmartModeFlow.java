package com.summaphoto;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.R.id;
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
	private static final int MIN_ACTUAL_EVENTS = 3;
	private static final int MIN_TIME_BETWEEN_COLLAGES = 1;

	private static ExecutorService scheduler = null;
	private static boolean busy = false;
	private static ActivationManager manager = ActivationManager.getInstance();  
	public static long lastCollageTime = -1;

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
						Log.e(TAG, "sleep was interrupted");
					}

					boolean changed = manager.consumeDedictedRequests();
					if (changed) {
						Log.d(TAG, "Activation manager changed to dedicated mode");
						Log.d(TAG, "Activation manager: " + manager.toString());
					}

					boolean clusteringNeeded = manager.processPhotoBuffer();
					Log.d(TAG, "Collage needed: " + clusteringNeeded);

					long diffHours;
					diffHours = (new Date().getTime() - lastCollageTime) / (60 * 60 * 1000) % 24; // hours passed since last collage

					if (clusteringNeeded) {	 // ActivationManager decided clustering should be made
						if (diffHours >= MIN_TIME_BETWEEN_COLLAGES) {	// only create collage if MIN_TIME_BETWEEN_COLLAGES passed
							
							ActualEventsBundle events = cluster();
							Log.d(TAG, "ActualEvents calculated: " + events.getActualEvents().size());

							ResultPair result = null;
							if 	(events.getActualEvents().size() >= MIN_ACTUAL_EVENTS) { // only create collage if exceeds MIN_EVENTS value of ActualEvents
 
								if (SettingsActivity.COLLAGE_TYPE == AbstractTemplate.BLOCK_TYPE) {
									Log.d(TAG, "attempting to build Block collage");
									result =  generate(new BlockCollageBuilder(events));
								}
								if (SettingsActivity.COLLAGE_TYPE == AbstractTemplate.MAP_TYPE) {
									result = generate(new MapCollageBuilder(events));
									Log.d(TAG, "attempting to build Map Collage");
								}
								if (result.validCollage) {
									Log.d(TAG, "Collage is valid!");
									lastCollageTime = new Date().getTime();
									try {
										Utils.notifyUserCollageCreated(result.collage);
									} catch (FileNotFoundException e) {
										Log.e(TAG, "Could not open the created collage file, collage notification aborted.");
									}
								}
							}
							else {
								Log.d(TAG, "Not building collage because number of Actual Events calculated < MIN_ACTUAL_EVENTS (" + MIN_ACTUAL_EVENTS + ")");
							}
						}
						else {
							Log.d(TAG, "Not building because the time diff from last collage < MIN_TIME_BETWEEN_COLLAGES (" + MIN_TIME_BETWEEN_COLLAGES + ")");
						}

					}

					setBusy(false);

					Log.d(TAG, "Flow ended: " + new Date());
					Log.d(TAG, "Activatin manager at end of flow:\n" + manager.toString());
				}

				/**
				 * run the DBScan algorithm to cluster photos to ActualEvents
				 * @return
				 */
				private ActualEventsBundle cluster() {
					List<Photo> photos = new ArrayList<Photo>(PhotoContainer.getInstance().getProcessedPhotos());
					DBScan eventsClusterer = new DBScan(photos);
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

	private static ResultPair generate(AbstractBuilder builder) {
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
