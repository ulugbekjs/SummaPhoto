package Common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import com.summaphoto.R;
import com.summaphoto.SettingsActivity;

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

public class Utils {

	/** Checks if external storage is available to at least read 
	 * */
	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) ||
				Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	/**
	 *  checks if external storage is available to read 
	 *  */
	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		return (Environment.MEDIA_MOUNTED.equals(state));
	}

	/**
	 * Creates notification for user about a newly created collage
	 * @param photo
	 * @throws FileNotFoundException 
	 */
	public static void notifyUserCollageCreated(Photo photo) throws FileNotFoundException {

		File ROOT = new File(Environment.getExternalStorageDirectory(), "Pictures");


		String  PHOTO_DIR = ROOT + File.separator + "SummaPhoto" + File.separator + photo.getFileName();
		File photoFile = new File(PHOTO_DIR);

		if (!photoFile.exists()) {
			throw new FileNotFoundException();
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

	public static void notifyUserWithError(String title, String text) {
		Context context = SettingsActivity.CONTEXT;

		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.icon)
		.setContentTitle(title)
		.setContentText(text)
		.setAutoCancel(true)
		.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(), 0)) // creates empty notfication
		.setOnlyAlertOnce(true)
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

	// TODO: remove, this is for Omri
	public static Photo createPhotoFromFile(String file) throws ImageProcessingException {

		Photo photo = null;
		File path = new File(file);

		// extract photo metadata
		Metadata metadata = null;
		try {
			metadata = ImageMetadataReader.readMetadata(path);
		} catch (IOException e) {
			throw new ImageProcessingException(e);
		}

		//get location
		GeoLocation location = null;
		try {
			GpsDirectory directory1 = metadata.getDirectory(GpsDirectory.class);
			 location = directory1.getGeoLocation();
			if (location == null) { // photo has no location, dont create photo
				throw new NullPointerException();
			}
		}
		catch (NullPointerException exception) {
			Log.e("UTILS", "photo has no loction and EXIF metada");
			return null;
		}

		//get time
		ExifSubIFDDirectory directory2 = metadata.getDirectory(ExifSubIFDDirectory.class);
		Date date = directory2.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);

		//get dimensions
		JpegDirectory jpgDirectory = metadata.getDirectory(JpegDirectory.class);
		try {
			int width = jpgDirectory.getImageWidth();
			int	height = jpgDirectory.getImageHeight();

			photo = new Photo(
					date,
					width,
					height,
					new GPSPoint(location.getLatitude(),location.getLongitude()),
					path.getPath());
		} catch (MetadataException e) {
			return null;
		}

		return photo;
	}
	
	

}
