package ActivationManager;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.example.aworkshop.R;
import com.example.aworkshop.SettingsActivity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import Common.ActualEventsBundle;
import Generator.BlockCollageBuilder;
import Generator.BlockTemplate;
import Partitioning.DBScan;

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
				public void run() { // this is the main flow of the app

					manager.consumeDedictedRequests(); 
					boolean collageNeeded = manager.processPhotoBuffer();

					if (collageNeeded) { // ActivationManager decided clustering should be made
						DBScan eventsClusterer = new DBScan(manager.getProcessedPhotos());
						ActualEventsBundle events = eventsClusterer.runAlgorithmClusters();
						
						// build the collage from Bundle of photos
						File collageFile = buildBlockCollage(events);
						if (collageFile != null) {
							notifyUser(collageFile);
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

	private static File buildBlockCollage(ActualEventsBundle bundle) {
		DedicatedRequest request = new DedicatedRequest();
		BlockTemplate template = BlockCollageBuilder.chooseTemplate(bundle, request);
		if (!request.isEmptyRequest()) {
			manager.addRequestToBuffer(request);
			return null;
		}
		else { 
			BlockCollageBuilder.populateTemplate(bundle, template);
			return BlockCollageBuilder.BuildCollage(template);
		}

	}
	public static void notifyUser(File file) {

		final File ROOT = new File(Environment.getExternalStorageDirectory(), "DCIM");
		final String  PHOTO_DIR = ROOT + File.separator + "Tests" + File.separator + "IMG_20130804_130626.jpg";

		File photoFile = new File(PHOTO_DIR);
		if (!photoFile.exists())
			return;

		Context context = SettingsActivity.CONTEXT;
		Uri uri = Uri.withAppendedPath(Uri.fromFile(ROOT), "Tests" + File.separator + "IMG_20130804_130626.jpg");
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


}
