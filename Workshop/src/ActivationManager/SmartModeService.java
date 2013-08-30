package ActivationManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes.Name;

import com.example.aworkshop.R;
import com.example.aworkshop.SettingsActivity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import Common.ActualEventsBundle;
import Common.Photo;
import Common.PhotoContainer;
import Generator.BlockCollageBuilder;
import Generator.BlockTemplate;
import Partitioning.DBScan;

public class SmartModeService {

	private static final String TAG = "ActivationManager.SmartModeService";
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

					if (collageNeeded) { // ActivationManager decided clustering should be made
						ActualEventsBundle events = partiotionToEvents();

						// build the collage from Bundle of photos
						Photo collage = buildBlockCollage(events);
						if (collage != null) {
							notifyUser(collage);
						}
						//	MapTemplate template = MapTemplate.getTemplate(1);
						//	StaticMap map = BingServices.getStaticMap(BingServices.getImagesPointsList(),890,523);
						//File collageFile = MapCollageBuilder.BuildCollage(template);
						//BlockTemplate template = BlockTemplate.getTemplate(1); 
						//	BlockCollageBuilder builder = new BlockCollageBuilder(template, CandidatePhotoContainer.getInstance().getAllEventsInContainer());
						//						builder.populateTemplate();
						//						builder.BuildCollage();

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
					ActualEventsBundle events = eventsClusterer.runAlgorithmClusters();
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

	private static Photo buildBlockCollage(ActualEventsBundle bundle) {
		BlockCollageBuilder builder = new BlockCollageBuilder(bundle);
		DedicatedRequest request = builder.setTemplate();
		if (request != null) {
			manager.addRequestToBuffer(request);
			return null;
		}
		else { 
			builder.populateTemplate();
			return builder.buildCollage();
		}

	}
	public static void notifyUser(Photo photo) {

		File ROOT = new File(Environment.getExternalStorageDirectory(), "Pictures");


		String  PHOTO_DIR = ROOT + File.separator + "Output" + File.separator + photo.getFileName();
		File photoFile = new File(PHOTO_DIR);

		if (!photoFile.exists()) {
			Log.e(TAG, "Could not open collage file");
			return;
		}
		// scan into gallery
		Uri uri =  addImageToGallery(photo);

		Context context = SettingsActivity.CONTEXT;
		Intent it = new Intent();
		it.setDataAndType(uri, "image/jpeg");
		it.setAction(Intent.ACTION_VIEW);

		Intent[] arrIntents = {it};
		PendingIntent pendingIntent = PendingIntent.getActivities(context, 1, arrIntents, PendingIntent.FLAG_ONE_SHOT);
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.icon)
		.setContentTitle("New collage created!")
		.setContentText("Click to see the newly created collage in the gallery")
		.setAutoCancel(true)
		.setContentIntent(pendingIntent)
		.setSound(alarmSound);


		NotificationManager mNotifyMgr = 
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// Builds the notification and issues it.
		mNotifyMgr.notify(1, mBuilder.build());
	}

	private static Uri addImageToGallery(Photo photo) {
		ContentValues image = new ContentValues();

		image.put(Images.Media.TITLE, photo.getFileName());
		image.put(Images.Media.DISPLAY_NAME, photo.getFileName());
		image.put(Images.Media.DESCRIPTION, "Summaphoto smart mode auto generated");
		image.put(Images.Media.DATE_ADDED, photo.getTakenDate().toString());
		image.put(Images.Media.DATE_TAKEN, photo.getTakenDate().toString());
		image.put(Images.Media.DATE_MODIFIED, photo.getTakenDate().toString());
		image.put(Images.Media.MIME_TYPE, "image/jpeg");
		image.put(Images.Media.ORIENTATION, 0);

		File photoFile = new File(photo.getFilePath());
		File parent = photoFile.getParentFile();
		String path = parent.toString().toLowerCase();
		String name = parent.getName().toLowerCase();
		image.put(Images.ImageColumns.BUCKET_ID, path.hashCode());
		image.put(Images.ImageColumns.BUCKET_DISPLAY_NAME, name);
		image.put(Images.Media.SIZE, photo.getFilePath().length());

		image.put(Images.Media.DATA, photoFile.getAbsolutePath());

		return SettingsActivity.CONTEXT.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);
	}


}
