package com.summaphoto;

import java.util.ArrayList;
import java.util.List;

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
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class ImmediateModeTask extends AsyncTask<Void, Void, ResultPair> {
	private static final String TAG = ImmediateModeTask.class.getName();
	private Activity activity = null;
	ProgressDialog progDailog = null;
	int oldMode = 0;

	public ImmediateModeTask(Activity activity) {
		super();
		this.activity = activity;
	}

	@Override
	protected ResultPair doInBackground(Void... params) {

		Log.d(TAG, "Immediate Mode: Starting flow");

		// in this flow there are no dedicated requests
		ActualEventsBundle events = cluster();
		Log.d(TAG, "ActualEvents calculated: " + events.getActualEvents().size());
		Log.d(TAG, "Horizontal photos count in ActualEventsBundle: " + events.horizontalCount());
		Log.d(TAG, "vertical photos count in ActualEventsBundle: " + events.verticalCount());


		// build the collage from Bundle of photos
		ResultPair result = null;

		if (SettingsActivity.COLLAGE_TYPE == AbstractTemplate.BLOCK_TYPE) {
			result =  buildCollage(new BlockCollageBuilder(events));
		}
		if (SettingsActivity.COLLAGE_TYPE == AbstractTemplate.MAP_TYPE) {
			result = buildCollage(new MapCollageBuilder(events));
		}
		Log.d(TAG, "Immediate Mode: flow ended");
		return result;
	}

	private ActualEventsBundle cluster() {

		ActivationManager.getInstance().processPhotoBuffer(); // process photos artificially, ignore return value

		List<Photo> photos = new ArrayList<Photo>(PhotoContainer.getInstance().getProcessedPhotos());
		Log.d(TAG, "read " + photos.size() + " photos to cluster");

		DBScan eventsClusterer = new DBScan(photos, true); // disable noise functionality
		ActualEventsBundle events = eventsClusterer.ComputeCluster();
		return events;
	}

	private static ResultPair buildCollage(AbstractBuilder builder) {
		boolean successful;
		Photo collage = null;
		DedicatedRequest request = builder.setTemplate();
		if (request != null) { // not enough photos for collage
			Log.d(TAG, "needed horizontal photos for closest template: " + request.getHorizontalNeeded());
			Log.d(TAG, "needed vertical photos for closest template: " + request.getVerticalNeeded());
			Log.d(TAG, "There were not enough photos in bundle for template.\n");
			return new ResultPair(false, collage, request.getHorizontalNeeded(), request.getVerticalNeeded());
		}
		else { 
			successful = builder.populateTemplate();
			if (successful) {
				collage =  builder.buildCollage();
				successful = true;
			}
		}
		return new ResultPair(successful, collage);
	}

	@Override
	protected void onPostExecute(ResultPair result) {
		super.onPostExecute(result);

		handlePostImmediateClick(result);
		progDailog.dismiss();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		progDailog = new ProgressDialog(activity);
		progDailog.setTitle("Summaphoto is attempting to create a collage...");
		progDailog.setIndeterminate(false);
		progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDailog.setCancelable(false);
		progDailog.show();

		// stop any running services
		this.oldMode = SettingsActivity.MODE;
		SettingsActivity.MODE = 0;
//		
//		if (SmartModeFlow.isFlowRunning()) {
//			smart = true;
//			SmartModeFlow.stopService();
//		}
	}

	public void handlePostImmediateClick(ResultPair pair) {
		if (pair != null) {
			if (pair.validCollage) {

				Uri uri = Utils.addImageToGallery(pair.collage);	

				// create intent
				Intent it = new Intent();
				it.setDataAndType(uri, "image/jpeg");
				it.setAction(Intent.ACTION_VIEW);
				activity.startActivity(it);		  
			}
			else { // construct message with info

				StringBuilder builder = new StringBuilder("Sorry, Summaphoto could not create a collage now.");

				if (pair.getDiffHorizontal() != 0) {
					builder.append("\n" + pair.getDiffHorizontal() + " horizontal photos needed.");
				}
				if (pair.getDiffVertical() != 0) {
					builder.append("\n" + pair.getDiffVertical() + " vertical photos needed.");
				}

				new AlertDialog.Builder(activity)
				.setTitle("Collage not created.")
				.setCancelable(true)
				.setMessage(builder.toString())
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) { 
						dialog.dismiss();
					}
				})
				.show();
			}
		}
		
		// resume modes
		SettingsActivity.MODE = oldMode;
	} 
}
